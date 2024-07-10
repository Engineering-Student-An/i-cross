package cross.icross.controller;

import cross.icross.domain.*;
import cross.icross.domain.dto.SubjectDTO;
import cross.icross.domain.dto.VideoLectureDTO;
import cross.icross.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final StudentService studentService;
    private final SubjectService subjectService;
    private final VideoLectureService videoLectureService;
    private final AllVideoLectureService allVideoLectureService;

    private final ScheduleService scheduleService;
    private final AssignmentService assignmentService;
    private final AllAssignmentService allAssignmentService;

    private final AuthService authService;
    private final CustomMessageService customMessageService;

    private final ScheduledTask scheduledTask;
    private final CoursemosService coursemosService;

//    private final AsyncService asyncService;

    @GetMapping("/")
    public String home(Model model, HttpServletRequest request){

        Student loginStudent = (Student) model.getAttribute("loginStudent");
        if (loginStudent != null) {

            // ========================== 모델에 스케줄 추가 ==========================
            model.addAttribute("scheduleList", scheduleService.findByStudentId(loginStudent.getId()));

            // 스케줄 -> 카톡 보내기
            String code = request.getParameter("code");
            if(code != null) {
                if(authService.getKakaoAuthToken(code)) {
                    customMessageService.sendMyMessage(loginStudent.getId());
                    System.out.println("code = " + code);
                    return "redirect:/";

                }
            }

        }
        return "home/index";
    }

    @GetMapping("/subject/today")
    public String todaySubject(Model model) {

        Student loginStudent = (Student) model.getAttribute("loginStudent");

        // ========================== 모델에 강의 추가 ==========================
        List<SubjectDTO> todaySubject = new ArrayList<>();  // 오늘 강의 이름 리스트
        String koreanDay = getKoreanDayOfWeek(LocalDate.now().getDayOfWeek());
        for (Long subjectId : loginStudent.getSubjectList()) {

            Subject subject = subjectService.findById(subjectId);
            for (String time : subject.getTime()) {
                if(time.contains(koreanDay)) {

                    // 요일을 제외하고 시간 부분만 추출하기 위해 정규 표현식 사용
                    String timePart = time.replaceAll("^[가-힣]+", "");

                    SubjectDTO subjectDTO = new SubjectDTO(subject.getName(), timePart);
                    todaySubject.add(subjectDTO);
                }
            }

        }

        // 오늘 강의 시간 순 오름차순 정렬
        todaySubject.sort(Comparator.comparing(SubjectDTO::getTime));
        model.addAttribute("todaySubject", todaySubject);

        // 전체 강의 추가
        List<Subject> mySubjectList = new ArrayList<>();
        for (Long subjectId : studentService.findStudentById(loginStudent.getId()).getSubjectList()) {
            mySubjectList.add(subjectService.findById(subjectId));
        }
        model.addAttribute("allSubject", mySubjectList);
        return "home/subject";
    }

    @GetMapping("/remain")
    public String assignment(Model model) {
        Student loginStudent = (Student) model.getAttribute("loginStudent");

        // ========================== 모델에 (남은) 과제 추가 ==========================
        List<AllAssignment> remainAssignments = new ArrayList<>();
        // 과제 - 학생 에서 완료되지 않은 리스트 찾음
        List<Assignment> find2 = assignmentService.findByCompletedAndStudentId(false, loginStudent.getId());
        for (Assignment assignment : find2) {
            remainAssignments.add(allAssignmentService.findByWebId(assignment.getWebId()));
        }
        model.addAttribute("remainAssignments", remainAssignments);

        // ========================== 모델에 (남은) 웹강 추가 ==========================
        List<AllVideoLecture> remainVideoLectures = new ArrayList<>();
        // 웹강 - 학생 에서 완료되지 않은 리스트 찾음
        List<VideoLecture> find1 = videoLectureService.findByCompletedAndStudentId(false, loginStudent.getId());
        for (VideoLecture videoLecture : find1) {
            remainVideoLectures.add(allVideoLectureService.findByWebId(videoLecture.getWebId()));
        }
        model.addAttribute("remainVideoLectures", remainVideoLectures);


        return "home/remain";
    }

    @GetMapping("/reload")
    @ResponseBody
    public ResponseEntity<String> reloadIclassInfo(Model model) {

        Student loginStudent = (Student) model.getAttribute("loginStudent");

        System.out.println("리로드");
        // wstoken 가져오기
        String wstoken = coursemosService.getWstoken();
        System.out.println("reloadwstoken = " + wstoken);

        // utoken 가져오기
        String utoken = coursemosService.login(loginStudent.getStuId(), loginStudent.getPassword(), wstoken);
        System.out.println("utoken = " + utoken);

        // 수강중인 강의의 id 가져오기
        List<Long> courseIds = coursemosService.getCourseIds(utoken);
        System.out.println("courseIds = " + courseIds);
        // 로그인 학생의 수강 목록에 추가
        for (Long courseId : courseIds) {
            if(!loginStudent.getSubjectList().contains(courseId)) {
                studentService.addSubject(loginStudent.getId(), courseId);
            }
        }


        for (Long courseId : courseIds) {
            System.out.println("courseId = " + courseId);


            ListPair listPair = coursemosService.getList(utoken, courseId);

            // 웹강 리스트
            List<VideoLectureDTO> videos = listPair.videoList;
            // 과제 리스트
            List<VideoLectureDTO> assigns = listPair.assignList;
            // 퀴즈 리스트
            List<VideoLectureDTO> quizs = listPair.quizList;

            Long studentId = ((Student) model.getAttribute("loginStudent")).getId();

            // 새로 저장한 웹강 리스트
            List<Long> videoIds = new ArrayList<>();
            // 새로 저장한 과제 리스트
            List<Long> assignIds = new ArrayList<>();
            // 새로 저장한 퀴즈 리스트
            List<Long> quizIds = new ArrayList<>();

            // 웹강 저장
            for (VideoLectureDTO video : videos) {
                Long videoId = coursemosService.saveVideo(utoken, video);
                if(videoId != null) {  videoIds.add(videoId);  }
            }

            // 웹강 - 학생 저장
            for (Long videoId : videoIds) {
                if (!videoLectureService.existsByWebIdAndStudentId(videoId, studentId)) {
                    if(!allVideoLectureService.findByWebId(videoId).getDeadline().toLocalDate().isBefore(LocalDate.now())) {
                        videoLectureService.save(videoId, studentId);
                    }
                }
            }

            // 과제 저장
            for (VideoLectureDTO assign : assigns) {
                Long assignId = coursemosService.saveAssign(utoken, assign);
                if(assignId != null) assignIds.add(assignId);
            }

            // 과제 - 학생 저장
            for (Long assignId : assignIds) {
                if(!assignmentService.existsByWebIdAndStudentId(assignId, studentId)) {
                    if(!allAssignmentService.findByWebId(assignId).getDeadline().toLocalDate().isBefore(LocalDate.now())) {
                        assignmentService.save(assignId, studentId);
                    }
                }
            }

            // 퀴즈 (과제) 저장
            for (VideoLectureDTO quiz : quizs) {
                Long quizId = coursemosService.saveQuiz(utoken, quiz);
                if(quizId != null) quizIds.add(quizId);
            }

            // 퀴즈 - 학생 저장
            for (Long quizId : quizIds) {
                if(!assignmentService.existsByWebIdAndStudentId(quizId, studentId)) {
                    if(!allAssignmentService.findByWebId(quizId).getDeadline().toLocalDate().isBefore(LocalDate.now())) {
                        assignmentService.save(quizId, studentId);
                    }
                }
            }
        }

        // 첫 로그인이나 정보 가져오면 스케줄 새로 생성해줌
        scheduledTask.firstSchedule(loginStudent.getId());

//        scheduledTask.announcement();
        return ResponseEntity.ok("Reload Successful");
    }


    @GetMapping("/login")
    public String login() {

        return "home/login";
    }

    @GetMapping("/loginSuccess")
    public String loginSuccess(Model model) {

        Student loginStudent = (Student) model.getAttribute("loginStudent");

        // I-Class 정보 초기 설정
        if (loginStudent.getStuId() == null || loginStudent.getPassword() == null) {
            return "redirect:/iclassInfo";
        }

        // 이메일 초기 설정
        if (loginStudent.getEmail() == null) {
            return "redirect:/email";
        }

        // 공지 스타일 초기 설정
        if (loginStudent.getAnnouncementStyle() == null) {
            return "redirect:/style/add";
        }

        // 공지 주기 초기 설정
        if (loginStudent.getAnnouncementPeriod().isEmpty()) {
            return "redirect:/period/add";
        }

        // 수강 목록이 비어있으면 아이클래스 정보 다시 가져옴
        if (loginStudent.getSubjectList().isEmpty()) {
            return "redirect:/reload";
        }

        return "home/loadingHome";
    }

    @GetMapping("/loginSuccessNext")
    public ResponseEntity<?> loginSuccessNext(Model model) {

        Student loginStudent = (Student) model.getAttribute("loginStudent");

        try {
            // wstoken 가져오기
            String wstoken = coursemosService.getWstoken();
            System.out.println("wstoken = " + wstoken);

            // utoken 가져오기
            String utoken = coursemosService.login(loginStudent.getStuId(), loginStudent.getPassword(), wstoken);
            System.out.println("utoken = " + utoken);

            // 수강중인 강의의 id 가져오기
            List<Long> courseIds = loginStudent.getSubjectList();
            System.out.println("courseIds = " + courseIds);

            // 비동기 작업 호출
//        asyncService.processStudentData(utoken, courseIds, loginStudent.getId());

            Long studentId = loginStudent.getId();

            for (Long courseId : courseIds) {
                HomeController.ListPair listPair = coursemosService.getList(utoken, courseId);

                // 웹강 리스트
                List<VideoLectureDTO> videos = listPair.getVideoList();
                // 과제 리스트
                List<VideoLectureDTO> assigns = listPair.getAssignList();
                // 퀴즈 리스트
                List<VideoLectureDTO> quizs = listPair.getQuizList();

                // 새로 저장한 웹강 리스트
                List<Long> videoIds = new ArrayList<>();
                // 새로 저장한 과제 리스트
                List<Long> assignIds = new ArrayList<>();
                // 새로 저장한 퀴즈 리스트
                List<Long> quizIds = new ArrayList<>();

                // 웹강 저장
                for (VideoLectureDTO video : videos) {

                    Long videoId = coursemosService.saveVideo(utoken, video);
                    if (videoId != null) {
                        videoIds.add(videoId);
                    }
                }

                // 웹강 - 학생 저장
                for (Long videoId : videoIds) {
                    if (!videoLectureService.existsByWebIdAndStudentId(videoId, studentId)) {
                        if (!allVideoLectureService.findByWebId(videoId).getDeadline().toLocalDate().isBefore(LocalDate.now())) {
                            videoLectureService.save(videoId, studentId);
                        }

                    }
                }

                // 과제 저장
                for (VideoLectureDTO assign : assigns) {
                    Long assignId = coursemosService.saveAssign(utoken, assign);
                    if (assignId != null) assignIds.add(assignId);
                }

                // 과제 - 학생 저장
                for (Long assignId : assignIds) {
                    if (!assignmentService.existsByWebIdAndStudentId(assignId, studentId)) {
                        if (!allAssignmentService.findByWebId(assignId).getDeadline().toLocalDate().isBefore(LocalDate.now())) {
                            assignmentService.save(assignId, studentId);
                        }
                    }
                }

                // 퀴즈 (과제) 저장
                for (VideoLectureDTO quiz : quizs) {
                    Long quizId = coursemosService.saveQuiz(utoken, quiz);
                    if (quizId != null) quizIds.add(quizId);
                }

                // 퀴즈 - 학생 저장
                for (Long quizId : quizIds) {
                    if (!assignmentService.existsByWebIdAndStudentId(quizId, studentId)) {
                        if (!allAssignmentService.findByWebId(quizId).getDeadline().toLocalDate().isBefore(LocalDate.now())) {
                            assignmentService.save(quizId, studentId);
                        }
                    }
                }
            }
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "처리가 성공적으로 완료되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "I-Class 계정 연동 중 오류가 발생했습니다.\n입력하신 정보를 확인 해 주세요!\n\n");
            System.out.println("오류");
            return ResponseEntity.status(500).body(response);
        }

    }

    @ModelAttribute("loginStudent")
    public Student loginStudent(HttpSession session) {

        if (session.getAttribute("loginId") != null) {
            return studentService.findLoginStudentByLoginId((String) session.getAttribute("loginId"));
        }
        return null;
    }

    private static String getKoreanDayOfWeek(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return "월";
            case TUESDAY:
                return "화";
            case WEDNESDAY:
                return "수";
            case THURSDAY:
                return "목";
            case FRIDAY:
                return "금";
            case SATURDAY:
                return "토";
            case SUNDAY:
                return "일";
            default:
                return "";
        }
    }

    @Data
    @AllArgsConstructor
    public static class ListPair {

        private List<VideoLectureDTO> videoList;
        private List<VideoLectureDTO> assignList;
        private List<VideoLectureDTO> quizList;
    }
}

