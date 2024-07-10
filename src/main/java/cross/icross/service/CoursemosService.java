package cross.icross.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cross.icross.controller.HomeController;
import cross.icross.domain.dto.Course;
import cross.icross.domain.dto.Response;
import cross.icross.domain.dto.VideoLectureDTO;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CoursemosService {


    private final RestTemplate restTemplate;
    private final SubjectService subjectService;
    private final AllVideoLectureService allVideoLectureService;
    private final AllAssignmentService allAssignmentService;

    public String getWstoken() {
        String url = "https://api2.naddle.kr/api/v1/cos_com_school?keyword=" + "인하대학교" + "&lang=ko";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Host", "https://api2.naddle.kr");
        headers.setConnection("keep-alive");
        headers.setAccept(Collections.singletonList(MediaType.ALL));
        headers.add("User-Agent", "coursemos_swift/2.2.3 (kr.coursemos.ios2; build:9995; iOS 17.4.1) Alamofire/4.9.1");
        headers.add("Accept-Language", "ko-US;q=1.0, en-US;q=0.9");
        headers.add("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJcYkNvdXJzZW1vcyIsImlhdCI6MTY5NzUyMzU1NiwiZXhwIjoyMDQ0NTkyNjI1LCJhdWQiOiJuYWRkbGUua3IiLCJzdWIiOiJyYWJiaXRAZGFsYml0c29mdC5jb20iLCJ1c2VyX2lkIjoiMSJ9.-799jl5c466FLKWoKld1PuOzfDb6FUHjauT-_XNVj0k");

        HttpEntity<String> request = new HttpEntity<>(headers);

        // Setting up the proxy
        System.setProperty("http.proxyHost", "localhost");
        System.setProperty("http.proxyPort", "9494");

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        // Clear the proxy settings
        System.clearProperty("http.proxyHost");
        System.clearProperty("http.proxyPort");

        // Parse the JSON response to extract the wstoken
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode dataNode = rootNode.path("data");
            if (dataNode.isArray() && dataNode.size() > 0) {
                JsonNode firstItem = dataNode.get(0);
                return firstItem.path("wstoken").asText();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public String login(String userId, String password, String wsToken) {

        String url = "https://learn.inha.ac.kr/webservice/rest/server.php";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setConnection("keep-alive");
        headers.setAccept(Collections.singletonList(MediaType.ALL));
        headers.add("User-Agent", "coursemos_swift/2.2.2 (kr.coursemos.ios2; build:9663; iOS 17.4.1) Alamofire/4.9.1");
        headers.add("Accept-Language", "ko-KR;q=1.0, en-KR;q=0.9");

        String body = "lang=ko&moodlewsrestformat=json&password=" + password + "&userid=" + userId + "&wsfunction=coursemos_user_login_v2&wstoken=" + wsToken;

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        try {
            String jsonString = response.getBody();
            // ObjectMapper 인스턴스 생성
            ObjectMapper objectMapper = new ObjectMapper();
            // JSON 문자열을 JsonNode 객체로 변환
            JsonNode rootNode = objectMapper.readTree(jsonString);
            // "data" 객체로 이동
            JsonNode dataNode = rootNode.path("data");
            // "utoken" 값 추출
            String utoken = dataNode.get("utoken").textValue();

            return utoken;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Long> getCourseIds(String utoken) {
        String url = "https://learn.inha.ac.kr/webservice/rest/server.php";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Host", "learn.inha.ac.kr");
        headers.set("Cookie", "_ga_E323M45YWM=GS1.1.1715901782.18.0.1715901782.0.0.0; _ga=GA1.1.33679238.1714311933");
        headers.set("Connection", "keep-alive");
        headers.set("Accept", "*/*");
        headers.set("User-Agent", "coursemos_swift/2.2.3 (kr.coursemos.ios2; build:9995; iOS 17.4.1) Alamofire/4.9.1");
        headers.setAcceptLanguageAsLocales(Collections.singletonList(Locale.forLanguageTag("ko-KR")));

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("lang", "ko");
        map.add("moodlewsrestformat", "json");
        map.add("wsfunction", "coursemos_course_get_mycourses_v2");
        map.add("wstoken", utoken);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);


        String jsonString = response.getBody();

        ObjectMapper mapper = new ObjectMapper();
        List<Long> ids = new ArrayList<>();
        try {
            Response response1 = mapper.readValue(jsonString, Response.class);
            for (Course course : response1.getData()) {
                if (course.getCu_visible() == 1 && !course.getDay_cd().isEmpty()) {
                    ids.add(course.getId());

                    // 존재하지 않는 과목은 저장
                    if(!subjectService.existsById(course.getId())) {
                        System.out.println("존재하지 않는 subjectId = " + course.getId());
                        subjectService.save(course.getId(), course.getIdnumber(), course.getFullname(), course.getDay_cd(), course.getHour1());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 학생의 수강 전체 리스트 (courseId 리스트) 반환
        return ids;
    }

    // 웹강과 과제 리스트 가져오기
    public HomeController.ListPair getList(String utoken, Long courseId) {

        try {

            // ========================== 각 과목 페이지 입장 ============================

            // URL 설정
            String url = "https://learn.inha.ac.kr/webservice/rest/server.php";

            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Host", "learn.inha.ac.kr");
            headers.set("Connection", "keep-alive");
            headers.set("Accept", "*/*");
            headers.set("User-Agent", "coursemos_swift/2.2.3 (kr.coursemos.ios2; build:9995; iOS 17.4.1) Alamofire/4.9.1");
            headers.set("Accept-Language", "ko-KR;q=1.0, io-KR;q=0.9");
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            headers.set("Cookie", "_ga_E323M45YWM=GS1.1.1716901219.1.1.1716901326.0.0.0; _ga=GA1.1.833759194.1716901220; MoodleSession=8s9fovhei8rtj145u1rb82ma3m; ubboard_read=%25AA%25A5ej%25C8%2593%25F6%25BEa%250B%2500i%25BA%2596");

            // 바디 설정
            String body = "courseid=" + courseId + "&lang=ko&moodlewsrestformat=json&wsfunction=coursemos_course_get_contents_v2&wstoken=" + utoken;

            // HttpEntity 생성
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            // RestTemplate 생성
            RestTemplate restTemplate = new RestTemplate();

            // 요청 실행
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // 응답 출력

            // ========================== 각 페이지에서 동영상, 과제 찾음 =========================
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                // JSON 문자열을 JsonNode로 파싱
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode dataArray = rootNode.get("data");

                // 결과를 저장할 리스트
                List<VideoLectureDTO> videos = new ArrayList<>();
                List<VideoLectureDTO> assigns = new ArrayList<>();
                List<VideoLectureDTO> quizs = new ArrayList<>();

                // 모든 요소를 순회하며 조건에 맞는 id 추출
                if (dataArray.isArray()) {
                    for (JsonNode section : dataArray) {
                        JsonNode modulesArray = section.get("modules");
                        if (modulesArray != null && modulesArray.isArray()) {
                            for (JsonNode module : modulesArray) {
                                String modname = module.get("modname").asText();
                                // 동영상
                                if ("vod".equals(modname)) {
                                    Long id = module.get("id").asLong();
                                    if(!allVideoLectureService.existsByWebId(id)) {
                                        videos.add(new VideoLectureDTO(id, subjectService.findById(courseId).getName(), module.get("name").asText()));

                                    }
                                }
                                // 과제
                                if("assign".equals(modname)) {
                                    Long id = module.get("id").asLong();
                                    if(!allAssignmentService.existsByWebId(id)) {
                                        assigns.add(new VideoLectureDTO(id, subjectService.findById(courseId).getName(), module.get("name").asText()));
                                    }
                                }
                                // 퀴즈
                                if("quiz".equals(modname)) {
                                    Long id = module.get("id").asLong();
                                    if(!allAssignmentService.existsByWebId(id)) {
                                        quizs.add(new VideoLectureDTO(id, subjectService.findById(courseId).getName(), module.get("name").asText()));
                                    }
                                }
                            }
                        }
                    }
                }
                System.out.println("videos = " + videos);
                System.out.println("assigns = " + assigns);
                System.out.println("quizs = " + quizs);
                return new HomeController.ListPair(videos, assigns, quizs);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ResourceAccessException e) {
            System.err.println("ResourceAccessException: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private final VideoLectureService videoLectureService;
    public Long saveVideo(String utoken, VideoLectureDTO videoLectureDTO) {

        Long videoId = videoLectureDTO.getWebId();
        System.out.println("videoId = " + videoId);

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Host", "learn.inha.ac.kr");
        headers.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.set("Sec-Fetch-Site", "none");
        headers.set("Accept-Language", "ko-KR,ko;q=0.9");
        headers.set("Sec-Fetch-Mode", "navigate");
        headers.set("Origin", "null");
        headers.set("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 17_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148");
        headers.set("Connection", "keep-alive");
        headers.set("Sec-Fetch-Dest", "document");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Cookie", "_ga_E323M45YWM=GS1.1.1716912481.2.0.1716912481.0.0.0; MoodleSession=464jlra7n88lpos8t8im2l0pjt; _ga=GA1.1.1505350824.1716908448");

        // 바디 데이터 설정
        String body = "utoken="+utoken+"&modurl=https%3A//learn.inha.ac.kr/mod/vod/view.php?id%3D" + videoId;


        // HttpEntity에 헤더와 데이터 설정
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        // RestTemplate에 HttpClient 사용 설정
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        // 첫 번째 요청을 실행하고, 자동으로 리다이렉트됨
        String response = restTemplate.postForObject("https://learn.inha.ac.kr/local/coursemos/webviewapi.php?lang=ko",
                entity, // 첫 번째 요청의 HttpEntity
                String.class);

//        System.out.println(response); // 처리된 응답 출력

        Document doc = (Document) Jsoup.parse(response);

//         "출석인정기간"에 해당하는 span 요소를 찾습니다.
        Elements vodInfoElements = doc.select("div.vod_info");
        for (Element element : vodInfoElements) {
            Elements infoLabels = element.getElementsByClass("vod_info");
            for (Element label : infoLabels) {
                if (label.text().contains("출석인정기간:")) {
                    Element valueElement = element.selectFirst(".vod_info_value");
                    if (valueElement != null) {
                        // DateTimeFormatter 정의 (문자열 형태에 맞춤)
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

                        // 문자열을 LocalDateTime 객체로 변환
                        LocalDateTime dateTime = LocalDateTime.parse(valueElement.text(), formatter);

                        System.out.println("dateTime = " + dateTime);
                        // 마감기한이 오늘보다 뒤에 있거나 아직 존재하지 않을때 AllVideoLecture 저장
                        if(!allVideoLectureService.existsByWebId(videoId)) {
                            allVideoLectureService.save(videoLectureDTO, dateTime);
                            return videoLectureDTO.getWebId();
                        }

                    }
                }
            }
        }
        return null;
    }
    public Long saveAssign(String utoken, VideoLectureDTO assign) {

        Long assignId = assign.getWebId();
        System.out.println("assignId = " + assignId);

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Host", "learn.inha.ac.kr");
        headers.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.set("Sec-Fetch-Site", "none");
        headers.set("Accept-Language", "ko-KR,ko;q=0.9");
        headers.set("Sec-Fetch-Mode", "navigate");
        headers.set("Origin", "null");
        headers.set("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 17_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148");
        headers.set("Connection", "keep-alive");
        headers.set("Sec-Fetch-Dest", "document");
        headers.set("Content-Type", "application/x-www-form-urlencoded");
        headers.set("Cookie", "_ga_E323M45YWM=GS1.1.1716918157.3.0.1716918157.0.0.0; MoodleSession=b9bgopakhbjc0v661qkvjtkqdb; _ga=GA1.1.1505350824.1716908448");

        // 바디 데이터 설정
        String body = "utoken="+utoken+"&modurl=https%3A//learn.inha.ac.kr/mod/assign/view.php?id%3D" + assignId;



        // HttpEntity에 헤더와 데이터 설정
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        // RestTemplate에 HttpClient 사용 설정
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        // 첫 번째 요청을 실행하고, 자동으로 리다이렉트됨
        String response = restTemplate.postForObject("https://learn.inha.ac.kr/local/coursemos/webviewapi.php?lang=ko",
                entity, // 첫 번째 요청의 HttpEntity
                String.class);


        Document doc = (Document) Jsoup.parse(response);

        // "종료 일시"가 포함된 행을 선택
        Element endDateRow = doc.select("td:contains(종료 일시)").first();

        if(endDateRow != null) {
            Element endDate = endDateRow.nextElementSibling();
            // DateTimeFormatter 정의 (문자열 형태에 맞춤)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            // 문자열을 LocalDateTime 객체로 변환
            LocalDateTime dateTime = LocalDateTime.parse(endDate.text(), formatter);

            // 마감기한이 오늘보다 뒤에 있거나 아직 존재하지 않을때 AllVideoLecture 저장
            if(!allAssignmentService.existsByWebId(assignId)) {
                allAssignmentService.save(assign, dateTime);
                return assignId;
            }
        }

        return null;
    }

    public Long saveQuiz(String utoken, VideoLectureDTO quiz) {

        Long quizId = quiz.getWebId();
        System.out.println("quizId = " + quizId);

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Host", "learn.inha.ac.kr");
        headers.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.set("Sec-Fetch-Site", "none");
        headers.set("Accept-Language", "ko-KR,ko;q=0.9");
        headers.set("Sec-Fetch-Mode", "navigate");
        headers.set("Origin", "null");
        headers.set("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 17_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148");
        headers.set("Connection", "keep-alive");
        headers.set("Sec-Fetch-Dest", "document");
        headers.set("Content-Type", "application/x-www-form-urlencoded");
        headers.set("Cookie", "_ga_E323M45YWM=GS1.1.1716918157.3.0.1716918157.0.0.0; MoodleSession=b9bgopakhbjc0v661qkvjtkqdb; _ga=GA1.1.1505350824.1716908448");

        // 바디 데이터 설정
        String body = "utoken="+utoken+"&modurl=https%3A//learn.inha.ac.kr/mod/quiz/view.php?id%3D" + quizId;



        // HttpEntity에 헤더와 데이터 설정
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        // RestTemplate에 HttpClient 사용 설정
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        // 첫 번째 요청을 실행하고, 자동으로 리다이렉트됨
        String response = restTemplate.postForObject("https://learn.inha.ac.kr/local/coursemos/webviewapi.php?lang=ko",
                entity, // 첫 번째 요청의 HttpEntity
                String.class);


        Document doc = (Document) Jsoup.parse(response);

        // "종료 일시"가 포함된 행을 선택
        Element endDate = doc.select("p:contains(종료일시)").getFirst();

        String[] date = endDate.text().split(" ");
        String endDateString = date[2] + " " + date[3];


        // DateTimeFormatter 정의 (문자열 형태에 맞춤)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // 문자열을 LocalDateTime 객체로 변환
        LocalDateTime dateTime = LocalDateTime.parse(endDateString, formatter);

        // 마감기한이 오늘보다 뒤에 있거나 아직 존재하지 않을때 AllVideoLecture 저장
        if(!allAssignmentService.existsByWebId(quizId)) {
            allAssignmentService.save(quiz, dateTime);
            return quizId;
        }
        return null;
    }
}
