package cross.icross.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
// 학생 개인의 과제
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="assignment_id")
    private Long id;

    // 과제 id
    private Long webId;

    // 학생 id
    private Long studentId;

    // 완료 여부
    private boolean completed;

    // 완료 설정
    public void setCompleted() {
        this.completed = true;
    }

}

