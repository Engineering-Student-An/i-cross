package cross.icross.service;

import cross.icross.domain.*;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Transactional
public class ScheduledTask {

    private final SubjectService subjectService;
    private final StudentService studentService;
    private final OpenAIService openAIService;
    private final EmailService emailService;
    private final ScheduleService scheduleService;
    private final AllAssignmentService allAssignmentService;
    private final AssignmentService assignmentService;
    private final AllVideoLectureService allVideoLectureService;
    private final VideoLectureService videoLectureService;


    @Scheduled(cron = "0 20 22 * * ?") // 매일 자정에 실행

    // 과제, 웹강 알림 메일 보내기
    public void announcement() {
        // 완료되지 않은 과제 찾음
        List<Assignment> all = assignmentService.findByCompleted(false);

        // 학생 - 과제 map
        Map<Long, List<AllAssignment>> assignMap = new HashMap<>();

        // 완료 안된 학생-과제 반복
        for (Assignment assignment : all) {
            // 과제 찾음
            AllAssignment allAssignment = allAssignmentService.findByWebId(assignment.getWebId());

            // 과제의 마감 기한 (몇월 며칠 까지만)
            LocalDate deadline = allAssignment.getDeadline().toLocalDate();

            // 오늘, 마감기한 차이
            long daysDifference = ChronoUnit.DAYS.between(LocalDate.now(), deadline);

            // 학생 정보 찾음
            Student student = studentService.findStudentById(assignment.getStudentId());
            if (student.getAnnouncementPeriod().contains((int) daysDifference)) {
                if(assignMap.get(assignment.getStudentId()) == null) {
                    List<AllAssignment> allAssignmentList = new ArrayList<>();
                    allAssignmentList.add(allAssignment);
                    assignMap.put(assignment.getStudentId(), allAssignmentList);
                }
                else {
                    List<AllAssignment> allAssignmentList = assignMap.get(assignment.getStudentId());
                    allAssignmentList.add(allAssignment);
                    assignMap.put(assignment.getStudentId(), allAssignmentList);
                }
            }
        }
        for (Long studentId : assignMap.keySet()) {
            Student student = studentService.findStudentById(studentId);
            String prompt = "내 이름은 " + student.getName() + "이야. 내가 남은 과제에 대한 정보들을 알려줄게.";
            int index = 1;
            for (Long l : assignMap.keySet()) {
                System.out.println("l = " + l);
            }
            for (AllAssignment allAssignment : assignMap.get(studentId)) {
                prompt += ( (index++) + "번째 남은 과제에 대한 정보는 과목명은 " + allAssignment.getSubjectName() + "이고, 과제 이름은 "
                        + allAssignment.getName() + "이고, 마감기한이 " + ChronoUnit.DAYS.between(LocalDate.now(), allAssignment.getDeadline().toLocalDate())
                        + "일 만큼 남았어.");
            }
            prompt += "위의 모든 남은 과제들에 대해서 아직 제출하지 않았음을 알려줘. <h2>이때 말투 스타일은 " + student.getAnnouncementStyle() + "으로 해줘" +
                    "그리고 한 과제를 설명하면 한 줄 띄고 보내서 글이 한눈에 들어오게 해줘</h2>" +
                    "알겠다는 대답은 하지말아줘";

            String result = openAIService.announcement(prompt);

            // jsonString을 JSONObject로 변환
            JSONObject obj = new JSONObject(result);

            // "choices" 배열에서 첫 번째 요소를 가져옴
            JSONObject firstChoice = obj.getJSONArray("choices").getJSONObject(0);

            // "message" 객체에서 "content" 값을 추출
            String content = firstChoice.getJSONObject("message").getString("content");

            // 메일 보내기
            System.out.println("content = " + content);
            emailService.sendEmail(student.getEmail(), content, "emailForm/assignmentAnnouncement");

        }


        // 완료되지 않은 웹강 찾음
        List<VideoLecture> all2 = videoLectureService.findByCompleted(false);

        // 학생 - 웹강 map
        Map<Long, List<AllVideoLecture>> videoMap = new HashMap<>();

        // 완료 안된 학생-웹강 반복
        for (VideoLecture videoLecture : all2) {
            // 과제 찾음
            AllVideoLecture allVideoLecture = allVideoLectureService.findByWebId(videoLecture.getWebId());

            // 과제의 마감 기한 (몇월 며칠 까지만)
            LocalDate deadline = allVideoLecture.getDeadline().toLocalDate();

            // 오늘, 마감기한 차이
            long daysDifference = ChronoUnit.DAYS.between(LocalDate.now(), deadline);

            // 학생 정보 찾음
            Student student = studentService.findStudentById(videoLecture.getStudentId());
            if (student.getAnnouncementPeriod().contains((int) daysDifference)) {
                if(videoMap.get(videoLecture.getStudentId()) == null) {
                    List<AllVideoLecture> allVideoLectureList = new ArrayList<>();
                    allVideoLectureList.add(allVideoLecture);
                    videoMap.put(videoLecture.getStudentId(), allVideoLectureList);
                }
                else {
                    List<AllVideoLecture> allVideoLectureList = videoMap.get(videoLecture.getStudentId());
                    allVideoLectureList.add(allVideoLecture);
                    videoMap.put(videoLecture.getStudentId(), allVideoLectureList);
                }
            }
        }
        for (Long l : videoMap.keySet()) {
            System.out.println("l = " + l);
        }
        for (Long studentId : videoMap.keySet()) {
            Student student = studentService.findStudentById(studentId);
            String prompt = "내 이름은 " + student.getName() + "이야. 내가 남은 웹강에 대한 정보들을 알려줄게.";
            int index = 1;
            for (AllVideoLecture allVideoLecture : videoMap.get(studentId)) {
                prompt += ( (index++) + "번째 남은 웹강에 대한 정보는 과목명은 " + allVideoLecture.getSubjectName() + "이고, 웹강 이름은 "
                        + allVideoLecture.getName() + "이고, 마감기한이 " + ChronoUnit.DAYS.between(LocalDate.now(), allVideoLecture.getDeadline().toLocalDate())
                        + "일 만큼 남았어.");
            }
            prompt += "위의 모든 남은 웹강들에 대해서 아직 시청하지 않았음을 알려줘. <h2>이때 말투 스타일은 " + student.getAnnouncementStyle() + "으로 해줘" +
                    "그리고 한 웹강을 설명하면 한 줄 띄고 보내서 글이 한눈에 들어오게 해줘</h2>"+
                    "알겠다는 대답은 하지말아줘";

            String result = openAIService.announcement(prompt);

            // jsonString을 JSONObject로 변환
            JSONObject obj = new JSONObject(result);

            // "choices" 배열에서 첫 번째 요소를 가져옴
            JSONObject firstChoice = obj.getJSONArray("choices").getJSONObject(0);

            // "message" 객체에서 "content" 값을 추출
            String content = firstChoice.getJSONObject("message").getString("content");

            // 메일 보내기
            System.out.println("content = " + content);
            emailService.sendEmail(student.getEmail(), content, "emailForm/videoAnnouncement");

        }
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    // 스케줄 초기화
    public void clearSchedule() {
        List<Schedule> all = scheduleService.findAll();
        for (Schedule schedule : all) {
            scheduleService.delete(schedule);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    // 스케줄 추천 작성
    public void schedule() {

        List<Student> all = studentService.findAll();
        for (Student student : all) {
            // 수강 과목명
            List<Long> subjectList = student.getSubjectList();
            List<String> subjectName = new ArrayList<>();
            for (Long subjectId : subjectList) {
                Subject subject = subjectService.findById(subjectId);
                subjectName.add("과목 이름 : " + subject.getName() + ", 과목 시간 : " + subject.getTime());
            }

            // 남은 과제
            List<Assignment> all2 = assignmentService.findByCompletedAndStudentId(false, student.getId());
            List<String> assignmentName = new ArrayList<>();
            for (Assignment assignment : all2) {
                AllAssignment allAssignment = allAssignmentService.findByWebId(assignment.getWebId());
                assignmentName.add("해당 과제의 과목 이름 : " + allAssignment.getSubjectName() + ", 과제 이름 : " + allAssignment.getName() + ", 마감일 : " + allAssignment.getDeadline());
            }

            // 남은 웹강
            List<VideoLecture> all3 = videoLectureService.findByCompletedAndStudentId(false, student.getId());
            List<String> videoLectureName = new ArrayList<>();
            for (VideoLecture videoLecture : all3) {
                AllVideoLecture allVideoLecture = allVideoLectureService.findByWebId(videoLecture.getWebId());
                videoLectureName.add("해당 웹강의 과목 이름 : " + allVideoLecture.getSubjectName() + ", 웹강 이름 : " + allVideoLecture.getName() + ", 마감일 : " + allVideoLecture.getDeadline());
            }

            System.out.println("subjectName = " + subjectName);
            System.out.println("assignmentName = " + assignmentName);
            System.out.println("videoLectureName = " + videoLectureName);

            String prompt = "나의 강의 리스트는 다음과 같아" + subjectName +
                    "그리고 나에게 남은 과제와 웹강은 각각 다음과 같아." + assignmentName + ", " + videoLectureName +
                    "오늘 요일은 " + LocalDate.now().getDayOfWeek() + "이야. 꼭 스케줄에 오늘 요일도 고려해서 넣어줘 " +
                    "나에게 오늘 하루 추천 스케줄을 알려줘, 이때 복습, 휴식, 점심시간, 저녁시간도 고려해서 넣어줘 반드시 오전 9시부터 24시까지의 스케줄을 알려줘야만 해" +
                    "그리고 오늘 진행하는 강의 이외의 강의는 절대 오늘 시간표에 넣지마" +
                    "강의 시간도 정확하게 고려해서 작성해줘. 10:30-12:00 수업인데 12:00-13:30 수업으로 잘못 쓰지 말라는 이야기야.";


            String result = openAIService.schedule(prompt);

            // jsonString을 JSONObject로 변환
            JSONObject obj = new JSONObject(result);

            // "choices" 배열에서 첫 번째 요소를 가져옴
            JSONObject firstChoice = obj.getJSONArray("choices").getJSONObject(0);

            // "message" 객체에서 "content" 값을 추출
            String content = firstChoice.getJSONObject("message").getString("content");

            // 리스트화 해서 스케줄에 저장
            System.out.println("content = " + content);
            List<String> times = new ArrayList<>();
            List<String> contents = new ArrayList<>();

            String[] scheduleParts = content.split(", ");

            for (String part : scheduleParts) {
                // 각 부분을 "/” 기준으로 시간과 내용으로 분리
                String[] splitPart = part.split("/", 2);
                times.add(splitPart[0]); // 시간을 리스트에 추가
                contents.add(splitPart[1]); // 내용을 리스트에 추가
            }

            for (int i = 0; i < times.size(); i++) {

                // 시간 앞에 0 추가 (ex. 9:00 -> 09:00)

                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("H:mm");
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm");
                String[] split = times.get(i).split("-");
                LocalTime startTime = LocalTime.parse(split[0], inputFormatter);
                LocalTime endTime = LocalTime.parse(split[1], inputFormatter);

                String formattedTime = outputFormatter.format(startTime) + "~" + outputFormatter.format(endTime);

                Schedule schedule = Schedule.builder()
                                .studentId(student.getId())
                                .name(contents.get(i))
                                .time(formattedTime)
                                .completed(false)
                                .build();

                scheduleService.save(schedule);
            }
        }
    }

    public void firstSchedule(Long studentId) {

        List<Schedule> scheduleList = scheduleService.findByStudentId(studentId);
        if(scheduleList!= null) {
            for (Schedule schedule : scheduleList) {
                scheduleService.delete(schedule);
            }
        }

        Student student = studentService.findStudentById(studentId);

        // 수강 과목명
        List<Long> subjectList = student.getSubjectList();

        List<String> subjectName = new ArrayList<>();
        for (Long subjectId : subjectList) {
            Subject subject = subjectService.findById(subjectId);
            subjectName.add("과목 이름 : " + subject.getName() + ", 과목 시간 : " + subject.getTime());
        }

        // 남은 과제
        List<Assignment> all2 = assignmentService.findByCompletedAndStudentId(false, studentId);
        List<String> assignmentName = new ArrayList<>();
        for (Assignment assignment : all2) {
            AllAssignment allAssignment = allAssignmentService.findByWebId(assignment.getWebId());
            assignmentName.add("해당 과제의 과목 이름 : " + allAssignment.getSubjectName() + ", 과제 이름 : " + allAssignment.getName() + ", 마감일 : " + allAssignment.getDeadline());
        }

        // 남은 웹강
        List<VideoLecture> all3 = videoLectureService.findByCompletedAndStudentId(false, studentId);
        List<String> videoLectureName = new ArrayList<>();
        for (VideoLecture videoLecture : all3) {
            AllVideoLecture allVideoLecture = allVideoLectureService.findByWebId(videoLecture.getWebId());
            videoLectureName.add("해당 웹강의 과목 이름 : " + allVideoLecture.getSubjectName() + ", 웹강 이름 : " + allVideoLecture.getName() + ", 마감일 : " + allVideoLecture.getDeadline());
        }

        System.out.println("subjectName = " + subjectName);
        System.out.println("assignmentName = " + assignmentName);
        System.out.println("videoLectureName = " + videoLectureName);

        String prompt = "나의 강의 리스트는 다음과 같아" + subjectName +
                "그리고 나에게 남은 과제와 웹강은 각각 다음과 같아." + assignmentName + ", " + videoLectureName +
                "오늘 요일은 " + LocalDate.now().getDayOfWeek() + "이야. 꼭 스케줄에 오늘 요일도 고려해서 넣어줘 " +
                "나에게 오늘 하루 추천 스케줄을 알려줘, 이때 복습, 휴식, 점심시간, 저녁시간도 고려해서 넣어줘 반드시 오전 9시부터 24시까지의 스케줄을 알려줘야만 해" +
                "그리고 오늘 진행하는 강의 이외의 강의는 절대 오늘 시간표에 넣지마" +
                "강의 시간도 정확하게 고려해서 작성해줘. 10:30-12:00 수업인데 12:00-13:30 수업으로 잘못 쓰지 말라는 이야기야.";


        String result = openAIService.schedule(prompt);

        // jsonString을 JSONObject로 변환
        JSONObject obj = new JSONObject(result);

        // "choices" 배열에서 첫 번째 요소를 가져옴
        JSONObject firstChoice = obj.getJSONArray("choices").getJSONObject(0);

        // "message" 객체에서 "content" 값을 추출
        String content = firstChoice.getJSONObject("message").getString("content");

        // 리스트화 해서 스케줄에 저장
        System.out.println("content = " + content);
        List<String> times = new ArrayList<>();
        List<String> contents = new ArrayList<>();

        String[] scheduleParts = content.split(", ");

        for (String part : scheduleParts) {
            // 각 부분을 "/” 기준으로 시간과 내용으로 분리
            String[] splitPart = part.split("/", 2);
            times.add(splitPart[0]); // 시간을 리스트에 추가
            contents.add(splitPart[1]); // 내용을 리스트에 추가
        }

        for (int i = 0; i < times.size(); i++) {

            // 시간 앞에 0 추가 (ex. 9:00 -> 09:00)

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("H:mm");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm");
            String[] split = times.get(i).split("-");
            LocalTime startTime = LocalTime.parse(split[0], inputFormatter);
            LocalTime endTime = LocalTime.parse(split[1], inputFormatter);

            String formattedTime = outputFormatter.format(startTime) + "~" + outputFormatter.format(endTime);

            Schedule schedule = Schedule.builder()
                    .studentId(student.getId())
                    .name(contents.get(i))
                    .time(formattedTime)
                    .completed(false)
                    .build();

            scheduleService.save(schedule);
        }
    }
}
