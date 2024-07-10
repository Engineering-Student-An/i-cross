package cross.icross.controller;

import cross.icross.domain.Student;
import cross.icross.service.AssignmentService;
import cross.icross.service.StudentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/assignment")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final StudentService studentService;

    @GetMapping("/{assignId}/completed")
    public String completeAssignment(@PathVariable(name = "assignId") Long assignId, Model model) {

        model.addAttribute("errorMessage", "과제 제출 완료 처리하시겠습니까?\n완료 처리 이후에는 다시 불러올 수 없습니다!");

        model.addAttribute("yesUrl", "/assignment/" + assignId + "/completed/confirm");
        model.addAttribute("noUrl", "/remain");

        return "error/yesOrNoMessage";
    }

    @GetMapping("/{assignId}/completed/confirm")
    public String confirmAssignment(@PathVariable(name = "assignId") Long assignId, Model model) {

        Student loginStudent = (Student) model.getAttribute("loginStudent");

        // 웹강 시청 완료 처리
        assignmentService.setCompleted(assignId, loginStudent.getId());

        return "redirect:/remain";
    }

    @ModelAttribute("loginStudent")
    public Student loginStudent(HttpSession session) {

        if (session.getAttribute("loginId") != null) {
            return studentService.findLoginStudentByLoginId((String) session.getAttribute("loginId"));
        }
        return null;
    }
}
