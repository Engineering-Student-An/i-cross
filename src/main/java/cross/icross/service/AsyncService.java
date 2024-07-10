package cross.icross.service;

import cross.icross.controller.HomeController;
import cross.icross.domain.dto.VideoLectureDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AsyncService {

    private final CoursemosService coursemosService;
    private final VideoLectureService videoLectureService;
    private final AssignmentService assignmentService;
    private final AllVideoLectureService allVideoLectureService;
    private final AllAssignmentService allAssignmentService;

    @Async
    public void processStudentData(String utoken, List<Long> courseIds, Long studentId) {

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
    }
}
