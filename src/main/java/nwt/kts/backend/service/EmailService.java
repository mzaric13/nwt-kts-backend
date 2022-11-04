package nwt.kts.backend.service;

import nwt.kts.backend.entity.Passenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private Environment env;

    private final String emailTo = "spring.mail.username";
    private final String emailFrom = "nwtktsproject@gmail.com";

    @Async
    public void sendActivationEmail(Passenger passenger) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(Objects.requireNonNull(env.getProperty(emailTo)));
        helper.setFrom(emailFrom);
        helper.setSubject("Account activation");
        helper.setText(buildEmail(passenger.getName() + " " + passenger.getSurname(), passenger.getId()), true);
        javaMailSender.send(mimeMessage);
    }

    private String buildEmail(String passengerName, int id) {
        return "<a href=\"" + "http://localhost:9000/passengers/activate-account/" + id  + "\">Activate Now</a>";
    }
}
