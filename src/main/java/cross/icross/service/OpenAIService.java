package cross.icross.service;

import cross.icross.domain.dto.QuizForm;
import cross.icross.domain.dto.QuizListDto;
import cross.icross.domain.dto.QuizRequestDto;
import cross.icross.exception.ConvertPdfToStringException;
import cross.icross.exception.OpenAIServiceException;
import cross.icross.exception.QuizOutOfRangeException;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    @Value("${secret.apiKey}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public String schedule(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + apiKey);

        JSONObject messageUser = new JSONObject();
        messageUser.put("role", "user");
        messageUser.put("content", prompt);

        JSONObject messageSystem = new JSONObject();
        messageSystem.put("role", "system");
        messageSystem.put("content", "저는 인공지능 챗봇이고 이름은 인하대학교 맞춤 AI 스케줄 생성기 입니다." +
                "저는 오늘 하루의 스케줄을 추천해 줍니다. 저에게는 모든 요일의 강의 리스트, 남은 과제, 남은 웹강 등을 입력해야 합니다." +
                "모든 요일의 강의 리스트에서는 요일시간 형태로 해당 강의의 강의 시간을 알려줍니다. 이때 강의는 하루만 하는 것이 아닌 여러 요일에도 가능하며" +
                "예시로 월10:30-12:00, 수15:00-16:30 이라면 월요일 10시30분부터 12시 그리고 수요일 15시부터 16시 30분 이렇게 일주일에 2번 수업하는 것입니다." +
                "강의 시간이 웹강인 경우 동영상 강의를 의미하고 정해진 수업시간은 없습니다." +
                "저는 이 입력값들을 토대로 하루 스케줄을 알려주는데 30분 단위로 알려줍니다. 또한 오늘 요일을 고려해서 오늘 하는 강의는 반드시 오늘 추천 스케줄에 넣습니다. " +
                "다른 요일에 하는 강의는 오늘 스케줄에 넣지 않습니다. 반드시 오전9시부터 오전 00시까지의 스케줄을 추천합니다." +
                "또한 남은 과제와 웹강을 남은 기한에 따라 중요도를 판단해서 오늘 할일에 넣을지 말지를 결정합니다. " +
                "이때 제가 하는 답변은 시간과 할일 형태로 보내주며 시간은 시작시간-끝시간 형식을 지키고 또 " +
                "시간과 할일은 / 로 구분하고 각 할일들은 , 로 구분합니다. 예를 들면 9:00-10:30/강의(강의 이름), 10:30-12:00/과제(해당 과제의 설명), 12:00-13:00/점심, 13:00-14:00/휴식, 14:00-16:00/복습(강의 이름), 16:00-17:30/웹강(해당 과제의 설명) 등입니다. " +
                "여기서 복습은 과제와 웹강 과는 다른 카테고리로 구분해야 합니다." +
                "이외의 대답은 하지 않고 오로지 시간과 할일만 보내줍니다. <h1>반드시 줄바꿈을 하지 않고 일렬로 출력하되 스케줄은 , 로 구분해서 출력해야만 합니다." +
                "스케줄을 구분할때를 제외하고는 , 를 절대 사용하지 않습니다." +
                "시간과 할일을 구분할때를 제외하고는 / 를 절대 사용하지 않습니다." +
                "오늘 요일에 하는 강의가 아니면 절대 스케줄에 넣지말고 오늘 요일에 하는 강의라면 무조건 스케줄에 넣어야 합니다.</h1>");


        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-4o");
        requestBody.put("messages", new JSONArray(Arrays.asList(messageSystem, messageUser)));

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        String apiEndpoint = "https://api.openai.com/v1/chat/completions";
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiEndpoint, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                return "Error occurred while calling the OpenAI API";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception occurred during API call: " + e.getMessage();
        }
    }

    public String announcement(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        headers.set("Authorization", "Bearer " + apiKey);

        JSONObject messageUser = new JSONObject();
        messageUser.put("role", "user");
        messageUser.put("content", prompt);

        JSONObject messageSystem = new JSONObject();
        messageSystem.put("role", "system");
        messageSystem.put("content", "저는 인공지능 챗봇이고 이름은 인하대학교 맞춤 AI 스케줄 생성기 입니다." +
                "저는 과제나 웹강이 며칠 남았는지를 알려줍니다. 저에게는 학생의 이름, 과목 이름, 웹강 혹은 과제의 내용, 마감기한이 며칠 남았는지, 그리고 스타일을 입력해야 합니다." +
                "입력받은 값 중 스타일을 사용해서 말투를 지정합니다. 그리고 그 말투로 해당 학생에게 어떤 웹강 혹은 과제가 며칠 남았는지를 알려줍니다.");

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-4o");
        requestBody.put("messages", new JSONArray(Arrays.asList(messageSystem, messageUser)));

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        String apiEndpoint = "https://api.openai.com/v1/chat/completions";
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiEndpoint, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                return "Error occurred while calling the OpenAI API";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception occurred during API call: " + e.getMessage();
        }
    }

    public QuizListDto quiz(QuizRequestDto quizRequestDto, byte[] lectureNoteData) {

        String lectureNote = parseFileToString(lectureNoteData);

        int ox = quizRequestDto.getOx();
        int multipleChoice = quizRequestDto.getMultipleChoice();
        int shortAnswer = quizRequestDto.getShortAnswer();

        // 문제의 총 합 > 20개 인 경우 예외 발생
        if(ox + multipleChoice + shortAnswer > 20) {
            throw new QuizOutOfRangeException("문제 개수의 총 합은 20개를 넘을 수 없습니다!");
        }


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + apiKey);

        JSONObject messageUser = new JSONObject();
        messageUser.put("role", "user");
        messageUser.put("content", lectureNote);

        JSONObject messageSystem = new JSONObject();
        messageSystem.put("role", "system");
        messageSystem.put("content", "저는 인공지능 챗봇입니다. " +
                "사용자가 입력한 내용은 강의노트 내용이고 이를 토대로 문제를 생성합니다. " +
                "이때 ox 형태의 문제는 " + ox + "개, 객관식(4지선다) 형태의 문제는 " + multipleChoice + "개, 단답식 형태의 문제는 " + shortAnswer + "개를 생성합니다. " +
                "문제는 다음과 같은 형식을 반드시 따릅니다. " +
                "각 유형의 문제 중 처음에는 문제 유형을 적고 개행문자를 적습니다. 그리고 각 문제의 마지막에는 개행문자를 넣어서 문제별로 구분합니다. " +
                "ox 문제는 (문제번호):(문제내용) " +
                "객관식 문제는 (문제번호):(문제내용) {1.선지1번내용 / 2.선지2번내용 / 3.선지3번내용 / 4.선지4번내용} " +
                "단답식 문제는 (문제번호):(문제내용) 형식을 지킵니다." +
                "이때 문제 유형은 반드시 ox(반드시 소문자) 문제, 객관식 문재, 단답식 문제 라고 작성합니다. " +
                "*모든 문제의 답은 순서대로 나열해서 답:(모든 문제의 답을/로 구분함) 형식으로 마지막에 반환합니다. 이때 객관식의 답은 #반드시 선지 번호(ex. 1 or 2 or 3 or 4)로 반환하고# 단답식의 답은 단답식 답을 그대로 반환합니다.*" +
                "*아래의 예시와 같은 형식을 반드시 지켜서 문제와 답을 반환합니다.*\n" +
                "ox 문제\n1:부울대수는 오직 두 가지 값(0과 1)을 허용한다.\n2:OR 작업의 부울 표현은 X = A AND B이다." +
                "\n\n객관식 문제\n1:AND 연산으로 인해 출력이 HIGH가 되는 조건은? {1.모든 입력이 HIGH일 때 / 2.모든 입력이 LOW일 때 / 3.하나의 입력이 HIGH일 때 / 4.두 입력 중 하나가 LOW일 때}\n2:부울 표현 X = A + B에서 \\\"+\\\" 기호는 어떤 연산을 나타내는가? {1.곱셈 / 2.뺄셈 / 3.덧셈 / 4.OR}" +
                "\n\n단답식 문제\n1:논리 회로의 입력과 출력 간의 관계를 설명하는 표의 이름은 무엇인가?\n2:전파 지연은 시스템이 입력을 받은 후 출력을 생성하는 데 걸리는 ________를 나타낸다.\n\n답: o/x/1/4/논리관계표/시간");

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-4o");
        requestBody.put("messages", new JSONArray(Arrays.asList(messageSystem, messageUser)));

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        String apiEndpoint = "https://api.openai.com/v1/chat/completions";
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiEndpoint, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // jsonString을 JSONObject로 변환
                JSONObject obj = new JSONObject(response.getBody());

                // "choices" 배열에서 첫 번째 요소를 가져옴
                JSONObject firstChoice = obj.getJSONArray("choices").getJSONObject(0);

                // "message" 객체에서 "content" 값을 추출
                String content = firstChoice.getJSONObject("message").getString("content");

                return splitQuizs(content, ox, multipleChoice, shortAnswer);
            }
        } catch (Exception e) {
            throw new OpenAIServiceException(e.getMessage());
        }
        throw new OpenAIServiceException("챗gpt api 호출 중 예외가 발생했습니다!");
    }

    private String parseFileToString(byte[] lectureNoteBytes) {
        try (PDDocument document = PDDocument.load(lectureNoteBytes)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);

            // 개행문자를 띄어쓰기로 변환
            text = text.replaceAll("\\r?\\n", " ");

            return text;

        } catch (IOException e) {
            throw new ConvertPdfToStringException("파일을 문자열로 변환하는 과정에서 예외가 발생했습니다!");
        }
    }

    static QuizListDto splitQuizs(String content, int ox, int multipleChoice, int shortAnswer) {

        // 문제와 답을 저장할 ArrayList
        List<QuizForm> oxQuestions = new ArrayList<>();
        List<QuizForm> multipleChoiceQuestions = new ArrayList<>();
        List<QuizForm> shortAnswerQuestions = new ArrayList<>();
        List<String> answers = new ArrayList<>();

        // 문제 문자열을 문제 유형에 따라 나누기
        String[] sections = content.split("\n\n"); // 두 개의 개행으로 구분된 섹션 나누기

        // 답변 파싱
        String[] answerSections = content.split("답: ")[1].split("/");
        int sectionIndex = 0;
        int index = 0;

        // OX 문제 파싱
        if(ox > 0) {
            String[] oxQuestionsRaw = sections[sectionIndex ++].split("\n");
            for (String question : oxQuestionsRaw) {
                if (!question.equals("ox 문제") && !question.isBlank()) {
                    answers.add(answerSections[index]);
                    oxQuestions.add(new QuizForm(question, answerSections[index++]));
                }
            }
        }

        // 객관식 문제 파싱
        if(multipleChoice > 0) {
            String[] multipleChoiceQuestionsRaw = sections[sectionIndex ++].split("\n");
            for (String question : multipleChoiceQuestionsRaw) {
                if(!question.equals("객관식 문제") && !question.isBlank()) {
                    // 선지 내용 파싱
                    String[] parts = question.split("\\{");
                    String questionText = parts[0].trim(); // 질문 부분
                    String choicesText = parts[1].replace("}", "").trim(); // 선지 부분
                    List<String> choices = List.of(choicesText.split(" / ")); // 선지를 리스트로 변환
                    answers.add(answerSections[index]);
                    multipleChoiceQuestions.add(new QuizForm(questionText, answerSections[index++], choices));
                }
            }
        }

        // 단답식 문제 파싱
        if(shortAnswer > 0) {
            String[] shortAnswerQuestionsRaw = sections[sectionIndex].split("\n");
            for (String question : shortAnswerQuestionsRaw) {
                if(!question.equals("단답식 문제") && !question.isBlank()) {
                    answers.add(answerSections[index]);
                    shortAnswerQuestions.add(new QuizForm(question, answerSections[index++]));
                }
            }
        }


        return new QuizListDto(oxQuestions, multipleChoiceQuestions, shortAnswerQuestions, answers);
    }
}
