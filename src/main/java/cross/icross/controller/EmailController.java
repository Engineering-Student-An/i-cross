package cross.icross.controller;

import cross.icross.domain.Student;
import cross.icross.service.EmailService;
import cross.icross.service.StudentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;
    private final StudentService studentService;

    @GetMapping("")
    public String setEmail(Model model) {

        model.addAttribute("emailForm", new StudentController.EmailForm());
        model.addAttribute("verifyCodeForm", new StudentController.VerifyCodeForm());
        model.addAttribute("isSent", false);

        return "student/email";
    }

    @PostMapping("")
    public String verifyEmail(@ModelAttribute StudentController.EmailForm emailForm, BindingResult bindingResult,
                              @ModelAttribute StudentController.VerifyCodeForm verifyCodeForm, HttpSession session, Model model) {

        String email = emailForm.getEmail();
        if(email.isEmpty()) {
            bindingResult.addError(new FieldError("emailForm",
                    "email", "이메일 주소를 입력해주세요!"));
        } else if(!email.contains("@") || !email.contains(".")) {
            bindingResult.addError(new FieldError("emailForm",
                    "email", "이메일 주소가 올바르지 않습니다!"));
        }

        if(bindingResult.hasErrors()) {
            return "student/email";
        }

        String authCode = emailService.createVerifyCode();
        emailService.sendEmail(email, authCode, "emailForm/verify");
        session.setAttribute("verifyCode", authCode);
        System.out.println("authCode = " + authCode);

        model.addAttribute("isSent", true);

        return "student/email";
    }

    @PostMapping("/verify")
    public String verifyCode(@ModelAttribute("verifyCodeForm") StudentController.VerifyCodeForm verifyCodeForm,
                             @RequestParam("email") String email,
                             HttpSession session, Model model) {

        // 메일 보낸 인증 문자
        String verifyCode = (String) session.getAttribute("verifyCode");
        System.out.println("verifyCode = " + verifyCode);

        // 인증 문자와 동일한지 검증
        if(!verifyCodeForm.getCode().equals(verifyCode)) {
            model.addAttribute("errorMessage", "인증 문자가 일치하지 않습니다! 다시 시도해주세요!");
            model.addAttribute("nextUrl", "/email");
            return "error/errorMessage";
        }

        // 인증 문자 동일하면 이메일 설정
        Student loginStudent = (Student) model.getAttribute("loginStudent");

        studentService.setEmail(loginStudent.getId(), email);

        return "redirect:/loginSuccess";

    }


    @GetMapping("/edit")
    public String editEmail(Model model) {

        model.addAttribute("emailForm", new StudentController.EmailForm());
        model.addAttribute("verifyCodeForm", new StudentController.VerifyCodeForm());
        model.addAttribute("isSent", false);

        return "student/editEmail";
    }

    @PostMapping("/edit")
    public String editEmail(@ModelAttribute StudentController.EmailForm emailForm, BindingResult bindingResult,
                              @ModelAttribute StudentController.VerifyCodeForm verifyCodeForm, HttpSession session, Model model) {

        String email = emailForm.getEmail();
        if(email.isEmpty()) {
            bindingResult.addError(new FieldError("emailForm",
                    "email", "이메일 주소를 입력해주세요!"));
        } else if(!email.contains("@") || !email.contains(".")) {
            bindingResult.addError(new FieldError("emailForm",
                    "email", "이메일 주소가 올바르지 않습니다!"));
        }

        if(bindingResult.hasErrors()) {
            return "student/editEmail";
        }

        String authCode = emailService.createVerifyCode();
        emailService.sendEmail(email, authCode, "emailForm/verify");
        session.setAttribute("verifyCode", authCode);
        System.out.println("authCode = " + authCode);

        model.addAttribute("isSent", true);

        return "student/editEmail";
    }

    @PostMapping("/edit/verify")
    public String verifyCodeEdit(@ModelAttribute("verifyCodeForm") StudentController.VerifyCodeForm verifyCodeForm,
                             @RequestParam("email") String email,
                             HttpSession session, Model model) {

        // 메일 보낸 인증 문자
        String verifyCode = (String) session.getAttribute("verifyCode");
        System.out.println("verifyCode = " + verifyCode);

        // 인증 문자와 동일한지 검증
        if(!verifyCodeForm.getCode().equals(verifyCode)) {
            model.addAttribute("errorMessage", "인증 문자가 일치하지 않습니다! 다시 시도해주세요!");
            model.addAttribute("nextUrl", "/email/edit");
            return "error/errorMessage";
        }

        // 인증 문자 동일하면 이메일 설정
        Student loginStudent = (Student) model.getAttribute("loginStudent");

        studentService.setEmail(loginStudent.getId(), email);

        return "redirect:/info";

    }

    @ModelAttribute("loginStudent")
    public Student loginStudent(HttpSession session) {

        if (session.getAttribute("loginId") != null) {
            return studentService.findLoginStudentByLoginId((String) session.getAttribute("loginId"));
        }
        return null;
    }
}
