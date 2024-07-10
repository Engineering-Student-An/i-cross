package cross.icross.controller;

import cross.icross.domain.Student;
import cross.icross.domain.Subject;
import cross.icross.service.EmailService;
import cross.icross.service.StudentService;
import cross.icross.service.SubjectService;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final SubjectService subjectService;
    private final EmailService emailService;

    @GetMapping("/iclassInfo")
    public String setIclassInfo(Model model) {
        model.addAttribute("iclassForm", new IclassForm());
        return "student/iclassInfo";
    }

    @PostMapping("/iclassInfo")
    public String setIclassInfo(@ModelAttribute IclassForm iclassForm, BindingResult bindingResult, Model model) {
        if(iclassForm.getStuId().isEmpty()) {
            bindingResult.addError(new FieldError("iclassForm", "stuId", "학번을 입력해주세요!"));
        }
        if(iclassForm.getPassword().isEmpty()) {
            bindingResult.addError(new FieldError("iclassForm", "password", "비밀번호를 입력해주세요!"));
        }

        if(!iclassForm.getPassword().equals(iclassForm.getPasswordCheck())) {
            bindingResult.addError(new FieldError("iclassForm", "passwordCheck", "비밀번호가 동일하지 않습니다!"));
        }

        if(bindingResult.hasErrors()) {
            return "student/iclassInfo";
        }


        studentService.setIclassInfo(((Student) model.getAttribute("loginStudent")).getId(), iclassForm.getStuId(), iclassForm.getPassword());
        return "redirect:/loginSuccess";
    }



    @GetMapping("/info")
    public String myInfo(Model model) {
        Student loginStudent = (Student) model.getAttribute("loginStudent");

        String maskPassword = "*".repeat(loginStudent.getPassword().length());

        List<Subject> mySubjectList = new ArrayList<>();
        for (Long subjectId : studentService.findStudentById(loginStudent.getId()).getSubjectList()) {
            mySubjectList.add(subjectService.findById(subjectId));
        }

        model.addAttribute("maskPassword", maskPassword);
        model.addAttribute("mySubjectList", mySubjectList);
        return "student/info";
    }

    @Data
    public static class EmailForm {
        private String email;
    }

    @Data
    public static class VerifyCodeForm {
        private String code;
    }

    @Data
    public static class IclassForm {
        private String stuId;
        private String password;
        private String passwordCheck;
    }

    @ModelAttribute("loginStudent")
    public Student loginStudent(HttpSession session) {

        if (session.getAttribute("loginId") != null) {
            return studentService.findLoginStudentByLoginId((String) session.getAttribute("loginId"));
        }
        return null;
    }
}
