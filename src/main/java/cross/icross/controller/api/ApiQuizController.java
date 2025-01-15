package cross.icross.controller.api;

import cross.icross.domain.dto.QuizRequestDto;
import cross.icross.service.OpenAIService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiQuizController {

    private final OpenAIService openAIService;

    @PostMapping("/quiz/loading")
    public ResponseEntity<String> loadingQuiz(@ModelAttribute QuizRequestDto quizRequestDto, HttpSession session) {
        try {
            byte[] fileData = quizRequestDto.getLectureNote().getBytes();
            session.setAttribute("lectureNoteData", fileData);
            session.setAttribute("quizRequestDto", quizRequestDto);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 처리 오류");
        }

        return ResponseEntity.ok("/quiz/loading");
    }

    @PostMapping("/quiz")
    public ResponseEntity<Map<String, String>> createQuiz(HttpSession session) {

        Map<String, String> response = new HashMap<>();

        try {
            session.setAttribute("quizList", openAIService.quiz((QuizRequestDto) session.getAttribute("quizRequestDto"), (byte[]) session.getAttribute("lectureNoteData")));
            response.put("nextUrl", "/quiz/list");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("nextUrl", "/quiz");
            response.put("message", "예상 문제 생성 중 오류가 발생했습니다!\n다시 시도해주세요!\n");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
