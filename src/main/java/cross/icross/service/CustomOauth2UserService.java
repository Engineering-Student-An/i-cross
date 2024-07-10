package cross.icross.service;

import cross.icross.config.CustomOauth2UserDetails;
import cross.icross.config.KakaoUserDetails;
import cross.icross.config.OAuth2UserInfo;
import cross.icross.domain.Student;
import cross.icross.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final StudentRepository studentRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo = new KakaoUserDetails(oAuth2User.getAttributes());

        String providerId = oAuth2UserInfo.getProviderId();
        String email = oAuth2UserInfo.getEmail();
        String loginId = provider + "_" + providerId;
        String name = oAuth2UserInfo.getName();

        Student findStudent = studentRepository.findByLoginId(loginId);
        Student student;

        if (findStudent == null) {
            student = Student.builder()
                    .loginId(loginId)
                    .name(name)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            studentRepository.save(student);
        } else{
            student = findStudent;
        }

        return new CustomOauth2UserDetails(student, oAuth2User.getAttributes());
    }
}
