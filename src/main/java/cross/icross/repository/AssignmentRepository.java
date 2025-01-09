package cross.icross.repository;

import cross.icross.domain.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    boolean existsAssignmentByWebIdAndStudentId(Long webId, Long studentId);

    List<Assignment> findAssignmentsByCompletedAndStudentId(boolean completed, Long studentId);

    Assignment findAssignmentByWebIdAndStudentId(Long webId, Long studentId);

    List<Assignment> findAssignmentsByCompleted(boolean completed);
}
