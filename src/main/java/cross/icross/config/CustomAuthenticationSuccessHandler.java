package cross.icross.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import java.io.IOException;

@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        response.sendRedirect("/loginSuccess");

        // loginStudent 를 세션에 저장
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomOauth2UserDetails userDetails) {

            String loginId = userDetails.getUsername(); // CustomUserDetails에서 Student 정보를 가져올 수 있는 메서드 제공 가정

            // 세션에 Student 정보 저장
            HttpSession session = request.getSession();
            session.setAttribute("loginId", loginId);
        }
    }
}
