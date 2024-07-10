package cross.icross.service;

import cross.icross.domain.Schedule;
import cross.icross.domain.dto.ScheduleForm;
import cross.icross.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    @Transactional
    public void save(Schedule schedule) {
        scheduleRepository.save(schedule);
    }

    @Transactional
    public void saveByForm(Long studentId, ScheduleForm scheduleForm) {

        String time = scheduleForm.getStartTime() + "~" + scheduleForm.getEndTime();
        Schedule schedule = Schedule.builder()
                        .studentId(studentId)
                        .name(scheduleForm.getScheduleName())
                        .time(time)
                        .completed(false).build();

        scheduleRepository.save(schedule);
    }

    @Transactional
    public void delete(Schedule schedule) {
        scheduleRepository.delete(schedule);
    }

    public List<Schedule> findByStudentId(Long studentId) {
        return scheduleRepository.findSchedulesByStudentIdOrderByTime(studentId);
    }

    public Schedule findById(Long scheduleId) {
        return scheduleRepository.findScheduleById(scheduleId);
    }

    @Transactional
    public void setCompleted(Long scheduleId) {
        scheduleRepository.findScheduleById(scheduleId).setCompleted();
    }


}
