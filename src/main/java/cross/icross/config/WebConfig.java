package cross.icross.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://ec2-13-209-198-107.ap-northeast-2.compute.amazonaws.com:8082") // 프론트엔드가 실행 중인 도메인
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
