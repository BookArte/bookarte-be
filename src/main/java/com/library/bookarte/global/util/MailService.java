package com.library.bookarte.global.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.display-name}")
    private String displayName;

    public void sendAuthMail(String toEmail, String authCode) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, displayName);
            helper.setTo(toEmail);
            helper.setSubject("[BookArte] 비밀번호 찾기 인증 코드입니다.");

            // HTML 형식으로 이메일 본문 작성
            String content = "<h3>안녕하세요, BookArte입니다.</h3>" +
                    "<p>비밀번호 재설정을 위한 인증 코드입니다.</p>" +
                    "<h2 style='color: #0056b3;'>" + authCode + "</h2>" +
                    "<p>5분 이내에 입력해주세요.</p>";

            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("메일 발송 중 오류가 발생했습니다.");
        }
    }
}
