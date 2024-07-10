package cross.icross.service;

import cross.icross.domain.Student;
import cross.icross.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    // 공지 스타일 설정/변경
    public void setAnnouncementStyle(Long id, String announcementStyle) {
        studentRepository.findStudentById(id).setAnnouncementStyle(announcementStyle);
    }

    // 공지 시간(d-day) 설정/변경
    public void setAnnouncementPeriod(Student student, String string) {
        List<Integer> announcementPeriod = new ArrayList<>();
        // 숫자 부분만 추출
        String[] parts = string.split("[\\[\\],\"]");
        for (String part : parts) {
            if (part.matches("\\d+")) { // 숫자인지 확인
                announcementPeriod.add(Integer.parseInt(part)); // Integer로 변환 후 리스트에 추가
            }
        }
        student.setAnnouncementPeriod(announcementPeriod);
    }

    public void setEmail(Long id, String email) {
        studentRepository.findStudentById(id).setEmail(email);
    }

    public Student findLoginStudentByLoginId(String loginId) {
        if (loginId == null) {
            return null;
        }
        return studentRepository.findByLoginId(loginId);
    }

    public Student findStudentById(Long id) {
        return studentRepository.findStudentById(id);
    }

    public void setIclassInfo(Long id, String stuId, String password) {
        studentRepository.findStudentById(id).setIclassInfo(stuId, password);
    }

    public void addSubject(Long id, Long courseId) {
        studentRepository.findStudentById(id).addSubject(courseId);
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }
}
