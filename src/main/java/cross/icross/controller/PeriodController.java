package cross.icross.controller;

import cross.icross.domain.Student;
import cross.icross.service.StudentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/period")
public class PeriodController {

    private final StudentService studentService;

    @GetMapping("/add")
    public String setPeriod() {
        return "period/add";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> setPeriod(@RequestBody String selectedValues, Model model) {

        Student loginStudent = (Student) model.getAttribute("loginStudent");

        String decodedString = URLDecoder.decode(selectedValues, StandardCharsets.UTF_8);
        studentService.setAnnouncementPeriod(loginStudent, decodedString);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "처리가 성공적으로 완료되었습니다.");
        return ResponseEntity.ok(response);
    }


    @GetMapping("/edit")
    public String editPeriod() {
        return "period/edit";
    }

    @PostMapping("/edit")
    public String editPeriod(@RequestBody String selectedValues, Model model) {

        Student loginStudent = (Student) model.getAttribute("loginStudent");

        String decodedString = URLDecoder.decode(selectedValues, StandardCharsets.UTF_8);
        studentService.setAnnouncementPeriod(loginStudent, decodedString);

        return "redirect:/";
    }

    @ModelAttribute("loginStudent")
    public Student loginStudent(HttpSession session) {

        if (session.getAttribute("loginId") != null) {
            return studentService.findLoginStudentByLoginId((String) session.getAttribute("loginId"));
        }
        return null;
    }
}
