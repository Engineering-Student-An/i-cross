package cross.icross.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubjectDTO {

    // 과목명
    private String name;

    // 오늘 강의 시간
    private String time;
}
