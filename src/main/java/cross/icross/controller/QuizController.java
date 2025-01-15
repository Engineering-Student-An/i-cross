package cross.icross.controller;

import cross.icross.domain.Student;
import cross.icross.domain.dto.QuizListDto;
import cross.icross.domain.dto.QuizRequestDto;
import cross.icross.service.StudentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {

    private final StudentService studentService;

    @GetMapping
    public String home(Model model) {

        model.addAttribute("quizRequestDto", new QuizRequestDto());
        return "quiz/quiz";
    }

    @GetMapping("/loading")
    public String loading() {

        return "quiz/loadingQuiz";
    }


    @GetMapping("/list")
    public String listQuiz(HttpSession session, Model model) {

        model.addAttribute("quizList", (QuizListDto) session.getAttribute("quizList"));
        return "quiz/quizList";
    }

    @ModelAttribute("loginStudent")
    public Student loginStudent(HttpSession session) {

        if (session.getAttribute("loginId") != null) {
            return studentService.findLoginStudentByLoginId((String) session.getAttribute("loginId"));
        }
        return null;
    }
}
