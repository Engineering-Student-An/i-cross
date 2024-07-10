package cross.icross.service;

import cross.icross.domain.VideoLecture;
import cross.icross.repository.VideoLectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoLectureService {

    private final VideoLectureRepository videoLectureRepository;


    @Transactional
    public void save(Long webId, Long studentId) {
        VideoLecture videoLecture = VideoLecture.builder().webId(webId)
                .studentId(studentId).completed(false).build();
        videoLectureRepository.save(videoLecture);
    }

    public boolean existsByWebIdAndStudentId(Long webId, Long studentId) {
        return videoLectureRepository.existsVideoLectureByWebIdAndStudentId(webId, studentId);
    }

    public List<VideoLecture> findByCompletedAndStudentId(boolean completed, Long studentId) {
        return videoLectureRepository.findVideoLecturesByCompletedAndStudentId(completed, studentId);
    }


    @Transactional
    public void setCompleted(Long videoId, Long studentId) {
        videoLectureRepository.findVideoLectureByWebIdAndStudentId(videoId, studentId).setCompleted();
    }

    public List<VideoLecture> findByCompleted(boolean completed) {
        return videoLectureRepository.findVideoLecturesByCompleted(completed);
    }
}
