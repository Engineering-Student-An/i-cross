package cross.icross.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VideoLectureDTO {
    // 웹강 id
    private Long webId;

    // 과목 이름
    private String subjectName;

    // 웹강 이름
    private String name;

}
