package cross.icross.repository;

import cross.icross.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findSchedulesByStudentIdOrderByTime(Long student_id);

    Schedule findScheduleById(Long id);

}
