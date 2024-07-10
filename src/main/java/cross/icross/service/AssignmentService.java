package cross.icross.service;

import cross.icross.domain.Assignment;
import cross.icross.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;


    @Transactional
    public void save(Long webId, Long studentId) {
        Assignment assignment = Assignment.builder().webId(webId)
                .studentId(studentId).completed(false).build();
        assignmentRepository.save(assignment);
    }

    public boolean existsByWebIdAndStudentId(Long webId, Long studentId) {
        return assignmentRepository.existsAssignmentByWebIdAndStudentId(webId, studentId);
    }

    public List<Assignment> findByCompletedAndStudentId(boolean completed, Long studentId) {
        return assignmentRepository.findAssignmentsByCompletedAndStudentId(completed, studentId);
    }

    public List<Assignment> findByCompleted(boolean completed) {
        return assignmentRepository.findAssignmentsByCompleted(completed);
    }

    @Transactional
    public void setCompleted(Long webId, Long studentId) {
        assignmentRepository.findAssignmentByWebIdAndStudentId(webId, studentId).setCompleted();
    }
}
