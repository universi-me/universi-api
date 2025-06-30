package me.universi.user.services;

import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import me.universi.Sys;
import me.universi.group.entities.GroupEnvironment;
import me.universi.group.services.OrganizationService;
import me.universi.user.entities.User;
import me.universi.user.exceptions.UserException;
import me.universi.util.ConvertUtil;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private JavaMailSender emailSender;
    private final Executor emailExecutor;
    private final RequestService requestService;
    private final EnvironmentService environmentService;

    public EmailService(RequestService requestService, EnvironmentService environmentService) {
        this.emailExecutor = Executors.newFixedThreadPool(5);
        this.requestService = requestService;
        this.environmentService = environmentService;
    }

    // bean instance via context
    public static EmailService getInstance() {
        return Sys.context().getBean("emailService", EmailService.class);
    }

    public void setupEmailSender() {
        GroupEnvironment envG = OrganizationService.getInstance().getEnvironment();
        if(envG != null && envG.email_enabled) {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost(envG.email_host);
            mailSender.setPort(Integer.parseInt(envG.email_port == null ? "587" : envG.email_port));
            mailSender.setUsername(envG.email_username);
            mailSender.setPassword(envG.email_password);

            Properties props = System.getProperties();
            props.remove("mail.transport.protocol");
            props.remove("mail.smtp.ssl.trust");
            props.remove("mail.smtp.auth");
            props.remove("mail.smtp.starttls.enable");
            props.put("mail.transport.protocol", envG.email_protocol == null ? "smtp" : envG.email_protocol);
            props.put("mail.smtp.ssl.trust", mailSender.getHost());
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            mailSender.setJavaMailProperties(props);

            emailSender = (JavaMailSender) mailSender;
        } else {
            emailSender = null;
        }
    }

    public JavaMailSender getEmailSender() {
        if(emailSender == null) {
            setupEmailSender();
        }
        return emailSender;
    }

    public void sendSystemEmailToUser(UserDetails user, String subject, String text, boolean ignoreEmailUnavailable) throws UserException {

        if(getEmailSender() == null) {
            return;
        }

        String email = ((User) user).getEmail();
        if (email == null) {
            if(ignoreEmailUnavailable) {
                return;
            } else {
                throw new UserException("Usuário não possui um email.");
            }
        }

        sendEmail(email, subject, text);
    }

    private void sendEmail(String email, String subject, String htmlContent) {
        emailExecutor.execute(() -> {
            try {
                MimeMessage message = getEmailSender().createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(email);
                helper.setSubject(subject);
                helper.setText(htmlContent, true);
                getEmailSender().send(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // generate recovery password token sha256 for user
    public String generateRecoveryPasswordToken(User user, boolean useIntervalCheck) throws UserException {

        //check recovery date token if less than 15min
        if(useIntervalCheck && user.getRecoveryPasswordTokenDate() != null) {
            long diff = ConvertUtil.getDateTimeNow().getTime() - user.getRecoveryPasswordTokenDate().getTime();
            if(diff < 900000) {
                throw new UserException("Um email de recuperação de senha já foi enviado para esta conta, por favor tente novamente mais tarde.");
            }
        }

        String tokenRandom = UUID.randomUUID().toString();

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new UserException("Algoritmo sha256 não disponível.");
        }
        byte[] encodedHash = digest.digest(tokenRandom.getBytes(StandardCharsets.UTF_8));
        String tokenString = ConvertUtil.bytesToHex(encodedHash);

        user.setRecoveryPasswordToken(tokenString);
        if(useIntervalCheck) {
            user.setRecoveryPasswordTokenDate(ConvertUtil.getDateTimeNow());
        }

        UserService.getInstance().save(user);

        return tokenString;
    }

    // send recovery password email to user
    public void sendRecoveryPasswordEmail(User user) throws UserException {
        String userIp = requestService.getClientIpAddress();

        String token = generateRecoveryPasswordToken(user, true);

        String url = requestService.getPublicUrlWebClient() + "/recovery-password/" + token;
        String subject = "Universi.me - Recuperação de Senha";
        String text = "Olá " + user.getUsername() + ",<br/><br/>\n\n" +
                "Você solicitou a recuperação de senha para sua conta no Universi.me.<br/>\n" +
                "Para recuperar sua senha, clique no link abaixo:<br/><br/>\n\n" +
                url + "<br/><br/>\n\n" +
                "Se você não solicitou a recuperação de senha, por favor, ignore este email.<br/><br/>\n\n" +
                "Endereço IP: " + userIp + "<br/><br/>\n\n" +
                "Atenciosamente,<br/>\n" +
                "Equipe Universi.me";

        sendSystemEmailToUser(user, subject, text, false);
    }

    //send confirmation signup account email to user
    public void sendConfirmAccountEmail(User user, boolean signup) throws UserException {
        String userIp = requestService.getClientIpAddress();

        String token = generateRecoveryPasswordToken(user, false);

        String url = requestService.getPublicUrlApi() + "/confirm-account/" + token;
        String subject = "Universi.me - Confirmação de Conta";
        String messageExplain = (signup) ? "Seja bem-vindo(a) ao Universi.me, para continuar com o seu cadastro precisamos confirmar a sua conta do Universi.me." : "Você solicitou a confirmação de sua conta no Universi.me.";
        String text = "Olá " + user.getUsername() + ",<br/><br/>\n\n" +
                messageExplain + "<br/><br/>\n\n" +
                "Para confirmar sua conta, clique no link abaixo:<br/><br/>\n\n" +
                url + "<br/><br/>\n\n" +
                "Se você não solicitou a confirmação de conta, por favor, ignore este email.<br/><br/>\n\n" +
                "Endereço IP: " + userIp + "<br/><br/>\n\n" +
                "Atenciosamente,<br/>\n" +
                "Equipe Universi.me";

        sendSystemEmailToUser(user, subject, text, false);
    }


}