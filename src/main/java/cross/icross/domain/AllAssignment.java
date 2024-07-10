package cross.icross.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AllAssignment {

    @Id
    private Long webId;

    // 과목 이름
    private String subjectName;

    // 웹강 이름
    private String name;

    // 출석 인정 기간
    private LocalDateTime deadline;
}
