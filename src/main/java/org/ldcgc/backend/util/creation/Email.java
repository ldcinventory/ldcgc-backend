package org.ldcgc.backend.util.creation;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.util.constants.Messages;
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

import static org.ldcgc.backend.util.process.Threads.runInBackground;

@Slf4j
@Getter
@RequiredArgsConstructor
@Component
public class Email {

    private final TemplateEngine templateEngine;
    private final JavaMailSender sender;
    @Setter private static Email INSTANCE;

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
            log.info(Messages.Info.EMAIL_SENT);
        } catch (MessagingException | IOException e) {
            log.error(Messages.Error.EMAIL_CREDENTIALS_SENDING_ERROR, email, e.getLocalizedMessage());
            throw new RequestException(HttpStatus.INTERNAL_SERVER_ERROR, Messages.Error.EMAIL_NOT_SENT);
        }
        return Constructor.buildResponseMessage(HttpStatus.CREATED, Messages.Info.CREDENTIALS_EMAIL_SENT);

    }

    private static void buildCredentialsEmail(MimeMessage mimeMessage, String email, String jwt)
            throws MessagingException, IOException {

        final Context context = new Context();
        context.setVariable("ldcgcLogo", Messages.App.EMAIL_IMAGE_PARAMETER);
        context.setVariable("userTempToken", jwt);
        context.setVariable("copyright", String.format(Messages.App.COPYRIGHT, LocalDateTime.now().getYear()));

        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED, "UTF-8");
        message.setTo(email);
        message.setSubject(Messages.App.CREDENTIALS_RECOVERY_SUBJECT);

        String processedEmail = Email.INSTANCE.templateEngine.process(Messages.App.CREDENTIALS_EMAIL_TEMPLATE, context);

        message.setText(processedEmail, true);

        // process logo inline as b64
        processLogo(message);

    }

    public static ResponseEntity<?> sendEmail(String email, String subject, String template, final Context context) {
        try {
            final MimeMessage mimeMessage = Email.INSTANCE.sender.createMimeMessage();
            buildEmail(mimeMessage, email, subject, template, context);
            runInBackground(() -> Email.INSTANCE.sender.send(mimeMessage));
            log.info(Messages.Info.EMAIL_SENT);
        } catch (MessagingException | IOException e) {
            log.error(Messages.Error.EMAIL_SENDING_ERROR, subject, email, e.getLocalizedMessage());
            throw new RequestException(HttpStatus.INTERNAL_SERVER_ERROR, Messages.Error.EMAIL_NOT_SENT);
        }
        return Constructor.buildResponseMessage(HttpStatus.CREATED, Messages.Info.EMAIL_SENT);
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
        helper.addInline(Messages.App.EMAIL_IMAGE_PARAMETER, imageSource, Messages.App.EMAIL_IMAGE_PNG);
    }

    private static void processInlineImage(MimeMessageHelper helper, String classPathResource) throws MessagingException, IOException {
        byte[] ldcgc8logo = new ClassPathResource(classPathResource).getInputStream().readAllBytes();
        final InputStreamSource imageSource = new ByteArrayResource(ldcgc8logo);
        helper.addInline(Messages.App.EMAIL_IMAGE_PARAMETER, imageSource, Messages.App.EMAIL_IMAGE_PNG);
    }

}
