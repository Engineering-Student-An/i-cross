package cross.icross.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class QuizForm {
    private String question;
    private String answer;
    private List<String> choices;

    public QuizForm(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
}
