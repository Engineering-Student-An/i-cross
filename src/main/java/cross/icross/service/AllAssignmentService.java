package cross.icross.service;

import cross.icross.domain.AllAssignment;
import cross.icross.domain.dto.VideoLectureDTO;
import cross.icross.repository.AllAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AllAssignmentService {


    private final AllAssignmentRepository allAssignmentRepository;
    public boolean existsByWebId(Long webId) {
        return allAssignmentRepository.existsAllAssignmentByWebId(webId);
    }

    public void save(VideoLectureDTO videoLectureDTO, LocalDateTime deadline) {
        AllAssignment all = AllAssignment.builder()
                .webId(videoLectureDTO.getWebId())
                .subjectName(videoLectureDTO.getSubjectName())
                .name(videoLectureDTO.getName())
                .deadline(deadline).build();
        allAssignmentRepository.save(all);
    }

    public AllAssignment findByWebId(Long webId) {
        return allAssignmentRepository.findAllAssignmentByWebId(webId);
    }
}
