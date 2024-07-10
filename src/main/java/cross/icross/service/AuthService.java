//package cross.icross.service;
//
//import org.json.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//
//@Service
//public class AuthService extends HttpCallService{
//    private Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    private static final String AUTH_URL = "https://kauth.kakao.com/oauth/token";
//
//    public static String authToken;
//
//    public boolean getKakaoAuthToken(String code)  {
//        HttpHeaders header = new HttpHeaders();
//        String accessToken = "";
//        String refrashToken = "";
//        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
//
//        header.set("Content-Type", APP_TYPE_URL_ENCODED);
//
//        parameters.add("code", code);
//        parameters.add("grant_type", "authorization_code");
//        parameters.add("client_id", "48a5417b39fe6369d6f6ee6a3e1c733c");
//        parameters.add("redirect_url", "http://localhost:8080");
//        parameters.add("client_secret", "97lLhgeeDq0S5XZ6RWdAhh0SyCzBaOOd");
//
//        HttpEntity<?> requestEntity = httpClientEntity(header, parameters);
//
//        ResponseEntity<String> response = httpRequest(AUTH_URL, HttpMethod.POST, requestEntity);
//        JSONObject jsonData = new JSONObject(response.getBody());
//        accessToken = jsonData.get("access_token").toString();
//        refrashToken = jsonData.get("refresh_token").toString();
//        if(accessToken.isEmpty() || refrashToken.isEmpty()) {
//            System.out.println(" 토큰 발 급 실패");
//            logger.debug("토큰발급에 실패했습니다.");
//            return false;
//        }else {
//            authToken = accessToken;
//            return true;
//        }
//    }
//
//}

package cross.icross.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class AuthService extends HttpCallService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String AUTH_URL = "https://kauth.kakao.com/oauth/token";

    public static String authToken;

    public boolean getKakaoAuthToken(String code) {
        HttpHeaders header = new HttpHeaders();
        String accessToken = "";
        String refreshToken = "";
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

        header.set("Content-Type", APP_TYPE_URL_ENCODED);

        parameters.add("code", code);
        parameters.add("grant_type", "authorization_code");
        parameters.add("client_id", "48a5417b39fe6369d6f6ee6a3e1c733c");
        parameters.add("redirect_uri", "http://localhost:8080");
        parameters.add("client_secret", "97lLhgeeDq0S5XZ6RWdAhh0SyCzBaOOd");
        parameters.add("scope", "talk_message"); // 필요한 권한을 요청하는 부분 추가

        HttpEntity<?> requestEntity = httpClientEntity(header, parameters);

        ResponseEntity<String> response = httpRequest(AUTH_URL, HttpMethod.POST, requestEntity);
        JSONObject jsonData = new JSONObject(response.getBody());
        accessToken = jsonData.get("access_token").toString();
        refreshToken = jsonData.get("refresh_token").toString();

        if (accessToken.isEmpty() || refreshToken.isEmpty()) {
            logger.debug("토큰발급에 실패했습니다.");
            return false;
        } else {
            authToken = accessToken;
            return true;
        }
    }

    private HttpEntity<?> httpClientEntity(HttpHeaders headers, MultiValueMap<String, String> parameters) {
        return new HttpEntity<>(parameters, headers);
    }

}
