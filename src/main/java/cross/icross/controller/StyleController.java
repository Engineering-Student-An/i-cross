package cross.icross.controller;

import cross.icross.domain.Student;
import cross.icross.service.StudentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/style")
public class StyleController {

    private final StudentService studentService;

    @GetMapping("/add")
    public String setStyle(Model model) {
        String style = "";
        model.addAttribute("style", style);
        return "style/add";
    }

    @PostMapping("/add")
    public String setStyle(@RequestParam("style") String style, Model model) {

        Student loginStudent = (Student) model.getAttribute("loginStudent");

        studentService.setAnnouncementStyle(loginStudent.getId(), style);

        return "redirect:/loginSuccess";
    }

    @GetMapping("/edit")
    public String editStyle(Model model) {
        String style = "";
        model.addAttribute("style", style);
        System.out.println("!!");
        return "style/edit";
    }

    @PostMapping("/edit")
    public String editStyle(@RequestParam("style") String style, Model model) {

        Student loginStudent = (Student) model.getAttribute("loginStudent");

        studentService.setAnnouncementStyle(loginStudent.getId(), style);

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
