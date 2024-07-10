package cross.icross.service;

import cross.icross.domain.AllVideoLecture;
import cross.icross.domain.dto.VideoLectureDTO;
import cross.icross.repository.AllVideoLectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AllVideoLectureService {

    private final AllVideoLectureRepository allVideoLectureRepository;

    public boolean existsByWebId(Long webId) {
        return allVideoLectureRepository.existsAllVideoLectureByWebId(webId);
    }

    public void save(VideoLectureDTO videoLectureDTO, LocalDateTime deadline) {
        AllVideoLecture all = AllVideoLecture.builder()
                .webId(videoLectureDTO.getWebId())
                .subjectName(videoLectureDTO.getSubjectName())
                .name(videoLectureDTO.getName())
                .deadline(deadline).build();
        allVideoLectureRepository.save(all);
    }

    public AllVideoLecture findByWebId(Long webId) {
        return allVideoLectureRepository.findAllVideoLectureByWebId(webId);
    }
}
