package cross.icross.repository;

import cross.icross.domain.AllVideoLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllVideoLectureRepository extends JpaRepository<AllVideoLecture, Long> {

    boolean existsAllVideoLectureByWebId(Long webId);

    AllVideoLecture findAllVideoLectureByWebId(Long webId);
}
