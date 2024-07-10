package cross.icross.repository;

import cross.icross.domain.VideoLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoLectureRepository extends JpaRepository<VideoLecture, Long> {

    boolean existsVideoLectureByWebId(Long webId);

    boolean existsVideoLectureByWebIdAndStudentId(Long webId, Long studentId);

    List<VideoLecture> findVideoLecturesByCompletedAndStudentId(boolean completed, Long studentId);

    VideoLecture findVideoLectureByWebIdAndStudentId(Long webId, Long studentId);

    List<VideoLecture> findVideoLecturesByCompleted(boolean completed);
}
