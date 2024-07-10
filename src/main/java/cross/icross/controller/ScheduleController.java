package cross.icross.controller;

import cross.icross.domain.Student;
import cross.icross.domain.dto.ScheduleForm;
import cross.icross.service.ScheduleService;
import cross.icross.service.StudentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final StudentService studentService;

    @GetMapping("/add")
    public String addScheduleForm(Model model) {
        model.addAttribute("scheduleForm", new ScheduleForm());
        return "student/addSchedule";
    }

    @PostMapping("/add")
    public String addSchedule(@ModelAttribute ScheduleForm scheduleForm, Model model) {

        Long studentId = ((Student) model.getAttribute("loginStudent")).getId();
        scheduleService.saveByForm(studentId, scheduleForm);

        return "redirect:/";
    }

    @GetMapping("/{scheduleId}/delete")
    public String deleteSchedule(@PathVariable(name = "scheduleId") Long scheduleId, Model model) {

        scheduleService.delete(scheduleService.findById(scheduleId));
        return "redirect:/";
    }

    @GetMapping("/{scheduleId}/completed")
    public String completeSchedule(@PathVariable(name = "scheduleId") Long scheduleId, Model model) {

        Student loginStudent = (Student) model.getAttribute("loginStudent");

        // 스케줄 완료 처리
        scheduleService.setCompleted(scheduleId);

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
