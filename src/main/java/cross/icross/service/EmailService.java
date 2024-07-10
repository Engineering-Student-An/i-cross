package cross.icross.service;


import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendEmail(String receiver, String authCode, String type) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try{
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            // 메일을 받을 수신자 설정
            mimeMessageHelper.setTo(receiver);

            // 메일의 제목 설정
            switch (type) {
                case "emailForm/verify" -> mimeMessageHelper.setSubject("이메일 인증");
                case "emailForm/assignmentAnnouncement" -> mimeMessageHelper.setSubject("과제/퀴즈 마감 기한 알림");
                case "emailForm/videoAnnouncement" -> mimeMessageHelper.setSubject("웹강 마감 기한 알림");
                default -> mimeMessageHelper.setSubject("I-Cross 이메일 서비스");
            }

            // 발신자 이메일 대신 서비스 이름 설정
            mimeMessageHelper.setFrom("gdscicross@gmail.com", "I-Cross");


            // 메일의 내용 설정
            mimeMessageHelper.setText(setContext(authCode, type), true);

            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String setContext(String verifyCode, String type) {
        Context context = new Context();
        String formattedAuthCode = verifyCode.replace("\n", "<br>");
        context.setVariable("verifyCode", formattedAuthCode);
        return templateEngine.process(type, context);
    }

    public String createVerifyCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(4);

            switch (index) {
                case 0 : key.append((char) ((int) random.nextInt(26) + 97)); break;
                case 1 : key.append((char) ((int) random.nextInt(26) + 65)); break;
                default: key.append(random.nextInt(9));
            }
        }

        return key.toString();
    }
}
