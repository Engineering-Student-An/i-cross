package cross.icross.service;

import cross.icross.domain.Schedule;
import cross.icross.domain.dto.DefaultMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomMessageService {

    private final MessageService messageService;
    private final ScheduleService scheduleService;

    public boolean sendMyMessage(Long studentId) {
        DefaultMessageDto myMsg = new DefaultMessageDto();
        myMsg.setBtnTitle("자세히보기");
        myMsg.setMobileUrl("");
        myMsg.setObjType("text");
        myMsg.setWebUrl("");

        List<Schedule> schedules = scheduleService.findByStudentId(studentId);
        StringBuilder sb = new StringBuilder();
        for (Schedule schedule : schedules) {
            sb.append(schedule.getTime()).append(" ").append(schedule.getName()).append("\n\n");
        }


        myMsg.setText(sb.toString());

        String accessToken = AuthService.authToken;

        return messageService.sendMessage(accessToken, myMsg);
    }

}