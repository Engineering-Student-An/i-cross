package cross.icross.service;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WeatherService {

    @Value("${secret.weatherApiKey}")
    private String weatherApiKey;

    public Map<String, String> returnWeather() {

        // 인하대학교의 위도, 경도
        String lat = "37.449768103";
        String lon = "126.657023505";

        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?lat="
                + lat + "&lon=" + lon + "&appid=" + weatherApiKey;

        Map<String, String> weather = new LinkedHashMap<>();

        try {
            // URL 객체 생성
            URL url = new URL(apiUrl);
            // 연결 설정
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // 응답 코드 확인
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                // 응답 내용 읽기
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // JSON 응답 파싱
                JSONObject jsonResponse = new JSONObject(response.toString());

                weather.put("날씨", jsonResponse.getJSONArray("weather").getJSONObject(0).getString("main"));

                BigDecimal temp = jsonResponse.getJSONObject("main").getBigDecimal("temp");
                BigDecimal tempCelsius = BigDecimal.valueOf(temp.doubleValue() - 273.15).setScale(2, RoundingMode.HALF_UP); // 소수점 둘째 자리에서 반올림

                weather.put("기온", String.valueOf(tempCelsius) + " °C");

                int humidity = jsonResponse.getJSONObject("main").getInt("humidity");
                weather.put("습도", Integer.toString(humidity) + "%");
            } else {
                System.out.println("Error: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return weather;
    }
}
