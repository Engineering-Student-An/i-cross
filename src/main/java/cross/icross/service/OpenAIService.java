package cross.icross.service;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final RestTemplate restTemplate;

    public String schedule(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        String apiKey = "";
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
        requestBody.put("model", "gpt-4o-2024-05-13");
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

        String apiKey = "";
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
        requestBody.put("model", "gpt-4o-2024-05-13");
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
}
