package cross.icross.service;

import cross.icross.domain.Subject;
import cross.icross.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SubjectService {

    private final SubjectRepository subjectRepository;

    @Transactional
    public void save(Long id, String code, String name, String day, String hour) {

        System.out.println("code = " + code);
        System.out.println("day = " + day);
        System.out.println("hour = " + hour);
        String[] splitCode = code.split("_");
        String[] splitDay = day.split("<BR>");
        String[] splitHour = hour.split("<BR>");

        List<String> time = new ArrayList<>();
        for (int i = 0; i < splitDay.length; i++) {
            // 웹강 과목이 아닌 경우
            if(!splitDay[i].equals("WEB")) {
                String[] splitHour2 = splitHour[i].split("~");
                time.add(splitDay[i] + transformTime(Integer.parseInt(splitHour2[0])) + "~" + transformTime(Integer.parseInt(splitHour2[1])+1));
            }

            // 웹강 과목인 경우
            else {
                time.add("WEB");
                break;
            }
        }

        Subject subject = Subject.builder()
                .id(id)
                .name(name)
                .code(splitCode[2] + "-" + splitCode[3])    // 학수번호
                .time(time).build();

        subjectRepository.save(subject);
    }

    public boolean existsById(Long id) {
        return subjectRepository.existsById(id);
    }



    public LocalTime transformTime(int time) {

        // 9시 시작
        LocalTime baseTime = LocalTime.of(9, 0);

        return baseTime.plusMinutes((time - 1) * 30L);
    }

    public Subject findById(Long courseId) {
        return subjectRepository.findSubjectById(courseId);
    }
}
