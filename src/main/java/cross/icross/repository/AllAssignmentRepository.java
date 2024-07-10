package cross.icross.repository;

import cross.icross.domain.AllAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllAssignmentRepository extends JpaRepository<AllAssignment, Long> {

    boolean existsAllAssignmentByWebId(Long webId);

    AllAssignment findAllAssignmentByWebId(Long webId);
}
