package com.project.user.provider;

import com.project.user.exception.EmailSendErrorException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailProvider {

  private final JavaMailSender javaMailSender;
  private final String SUBJECT = "\uD83D\uDC4B Welcome to Shopping Wise!";

  private static final String LOGO_IMAGE = "https://training-diary-brand.s3.ap-northeast-2.amazonaws.com/training_diary_logo_and_text.png";

  @Async
  @Retryable(value = EmailSendErrorException.class, backoff = @Backoff(delay = 2000))
  public void sendVerificationEmail(String email, String verificationNumber,
      String expirationTime) {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

      String htmlContent = getVerificationMessage(verificationNumber, expirationTime);

      messageHelper.setFrom("kyoominmir41@gmail.com", "Training Diary");
      messageHelper.setTo(email);
      messageHelper.setSubject(SUBJECT);
      messageHelper.setText(htmlContent, true);
      javaMailSender.send(message);

    } catch (MessagingException | UnsupportedEncodingException e) {
      throw new EmailSendErrorException("Email sending Error: " + e.getMessage());
    }
  }

  private String getVerificationMessage(String verificationNumber, String expirationTime) {
    return
        "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; background-color: #FFFFFF; border: 1px solid #d3d3d3; border-radius: 10px;'>"
            + "<div style='background-color: #62CEAD; padding: 20px; border-radius: 10px 10px 0 0; text-align: center;'>"
            + "<img src='" + LOGO_IMAGE
            + "' alt='Training Diary Logo' style='width: 200px; height: auto;'/>"
            + "</div>"
            + "<div style='padding: 20px;'>"
            + "<h2 style='text-align: center; color: #333;'>이메일 인증</h2>"
            + "<p style='text-align: center; color: #555;'>다음 인증 코드를 사용하여 이메일 인증을 완료하세요:</p>"
            + "<h1 style='text-align: center; font-size: 48px; color: #333;'>" + verificationNumber
            + "</h1>"
            + "<p style='text-align: center; color: #777;'>이 인증코드는 <strong>" + expirationTime
            + "</strong> 까지 유효합니다.</p>"
            + "</div>"
            + "</div>";
  }
}
