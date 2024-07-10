package cross.icross.domain.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class ScheduleForm {

    private String scheduleName;
    private LocalTime startTime;
    private LocalTime endTime;
}
