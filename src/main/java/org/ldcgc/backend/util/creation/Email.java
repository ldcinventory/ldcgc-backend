package org.ldcgc.backend.util.creation;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ldcgc.backend.exception.RequestException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.ldcgc.backend.util.process.Process.runInBackground;
import static org.ldcgc.backend.util.retrieving.Message.AppMessage.COPYRIGHT;
import static org.ldcgc.backend.util.retrieving.Message.AppMessage.CREDENTIALS_EMAIL_TEMPLATE;
import static org.ldcgc.backend.util.retrieving.Message.AppMessage.CREDENTIALS_RECOVERY_SUBJECT;
import static org.ldcgc.backend.util.retrieving.Message.AppMessage.EMAIL_IMAGE_PARAMETER;
import static org.ldcgc.backend.util.retrieving.Message.AppMessage.EMAIL_IMAGE_PNG;
import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.EMAIL_CREDENTIALS_SENDING_ERROR;
import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.EMAIL_NOT_SENT;
import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.EMAIL_SENDING_ERROR;
import static org.ldcgc.backend.util.retrieving.Message.InfoMessage.CREDENTIALS_EMAIL_SENT;
import static org.ldcgc.backend.util.retrieving.Message.InfoMessage.EMAIL_SENT;
import static org.ldcgc.backend.util.retrieving.Message.getAppMessage;
import static org.ldcgc.backend.util.retrieving.Message.getErrorMessage;
import static org.ldcgc.backend.util.retrieving.Message.getInfoMessage;

@Getter
@RequiredArgsConstructor
@Component
@Slf4j
public class Email {

    private final TemplateEngine templateEngine;
    private final JavaMailSender sender;
    private static Email INSTANCE;

    @PostConstruct
    public void init() {
        Email.INSTANCE = this;
    }

    private static final String logo = "static/img/ldcgc8-logo.png";

    public static ResponseEntity<?> sendRecoveringCredentials(String email, String jwt) {
        try {
            final MimeMessage mimeMessage = Email.INSTANCE.sender.createMimeMessage();
            buildCredentialsEmail(mimeMessage, email, jwt);
            Email.INSTANCE.sender.send(mimeMessage);
            log.info(getInfoMessage(EMAIL_SENT));
        } catch (MessagingException | IOException e) {
            log.error(getErrorMessage(EMAIL_CREDENTIALS_SENDING_ERROR), email, e.getLocalizedMessage());
            throw new RequestException(HttpStatus.INTERNAL_SERVER_ERROR, getErrorMessage(EMAIL_NOT_SENT));
        }
        return Constructor.buildResponseMessage(HttpStatus.CREATED, getInfoMessage(CREDENTIALS_EMAIL_SENT));

    }

    private static void buildCredentialsEmail(MimeMessage mimeMessage, String email, String jwt)
            throws MessagingException, IOException {

        final Context context = new Context();
        context.setVariable("ldcgcLogo", getAppMessage(EMAIL_IMAGE_PARAMETER));
        context.setVariable("userTempToken", jwt);
        context.setVariable("copyright", String.format(getAppMessage(COPYRIGHT), LocalDateTime.now().getYear()));

        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED, "UTF-8");
        message.setTo(email);
        message.setSubject(getAppMessage(CREDENTIALS_RECOVERY_SUBJECT));

        String processedEmail = Email.INSTANCE.templateEngine.process(getAppMessage(CREDENTIALS_EMAIL_TEMPLATE), context);

        message.setText(processedEmail, true);

        // process logo inline as b64
        processLogo(message);

    }

    public static ResponseEntity<?> sendEmail(String email, String subject, String template, final Context context) {
        try {
            final MimeMessage mimeMessage = Email.INSTANCE.sender.createMimeMessage();
            buildEmail(mimeMessage, email, subject, template, context);
            runInBackground(() -> Email.INSTANCE.sender.send(mimeMessage));
            log.info(getInfoMessage(EMAIL_SENT));
        } catch (MessagingException | IOException e) {
            log.error(getErrorMessage(EMAIL_SENDING_ERROR), subject, email, e.getLocalizedMessage());
            throw new RequestException(HttpStatus.INTERNAL_SERVER_ERROR, getErrorMessage(EMAIL_NOT_SENT));
        }
        return Constructor.buildResponseMessage(HttpStatus.CREATED, getInfoMessage(EMAIL_SENT));
    }

    public static void buildEmail(MimeMessage mimeMessage, String emailAddress, String subject, String template, Context context) throws MessagingException, IOException {

        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED, "UTF-8");
        message.setTo(emailAddress);
        message.setSubject(subject);

        String processedEmail = Email.INSTANCE.getTemplateEngine().process(template, context);

        message.setText(processedEmail, true);

        // process logo inline as b64
        processLogo(message);

    }

    private static void processLogo(MimeMessageHelper helper) throws MessagingException, IOException {
        processInlineImage(helper, logo);
        byte[] ldcgc8logo = new ClassPathResource(logo).getInputStream().readAllBytes();
        final InputStreamSource imageSource = new ByteArrayResource(ldcgc8logo);
        helper.addInline(getAppMessage(EMAIL_IMAGE_PARAMETER), imageSource, getAppMessage(EMAIL_IMAGE_PNG));
    }

    private static void processInlineImage(MimeMessageHelper helper, String classPathResource) throws MessagingException, IOException {
        byte[] ldcgc8logo = new ClassPathResource(classPathResource).getInputStream().readAllBytes();
        final InputStreamSource imageSource = new ByteArrayResource(ldcgc8logo);
        helper.addInline(getAppMessage(EMAIL_IMAGE_PARAMETER), imageSource, getAppMessage(EMAIL_IMAGE_PNG));
    }

}
