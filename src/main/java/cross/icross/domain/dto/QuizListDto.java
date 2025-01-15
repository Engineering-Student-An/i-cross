package cross.icross.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class QuizListDto {
    List<QuizForm> oxQuestions;
    List<QuizForm> multipleChoiceQuestions;
    List<QuizForm> shortAnswerQuestions;
    List<String> answers;
}