package cross.icross.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="schedule_id")
    private Long id;

    // 내용
    private String name;

    // 시작 시간~끝 시간 (ex. 10:30~12:00)
    private String time;

    // 학생 id
    private Long studentId;

    private boolean completed;

    public void setCompleted() {
        this.completed = !this.completed;
    }
}
