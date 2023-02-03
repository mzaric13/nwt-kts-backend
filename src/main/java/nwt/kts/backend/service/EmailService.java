package nwt.kts.backend.service;

import nwt.kts.backend.entity.Drive;
import nwt.kts.backend.entity.Passenger;
import nwt.kts.backend.entity.TempDrive;
import nwt.kts.backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
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
        return "<a href=\"" + "http://localhost:4200/activated-account/" + id  + "\">Activate Now</a>";
    }

    @Async
    public void sendPasswordResetEmail(User user) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(Objects.requireNonNull(env.getProperty(emailTo)));
        helper.setFrom(emailFrom);
        helper.setSubject("Password reset");
        helper.setText("You can reset your password " + "<a href=\"" + "http://localhost:4200/reset-password/" + user.getEmail()  + "\">here</a>.", true);
        javaMailSender.send(mimeMessage);
    }

    @Async
    public void sendDriveConfirmationEmail(TempDrive tempDrive, Passenger passenger) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(Objects.requireNonNull(env.getProperty(emailTo)));
        helper.setFrom(emailFrom);
        helper.setSubject("Drive confirmation");
        helper.setText(buildDriveConfirmationEmail(tempDrive, passenger), true);
        javaMailSender.send(mimeMessage);
    }

    private String buildDriveConfirmationEmail(TempDrive tempDrive, Passenger passenger) {
        StringBuilder builder = new StringBuilder();
        builder.append("Hello ")
                .append(passenger.getName())
                .append(" ")
                .append(passenger.getSurname())
                .append(",<br/>")
                .append("You have been added to a drive on the route ")
                .append(shortenRouteName(tempDrive.getRoute().getRouteName()))
                .append(". The time this drive will start is at ")
                .append(tempDrive.getStartDate())
                .append(".<br/>Do you consent to this drive?<br/>")
                .append("<a href=\"http://localhost:4200/user/give-consent?tempDriveId=")
                .append(tempDrive.getId())
                .append("&passengerId=")
                .append(passenger.getId())
                .append("&driveAccepted=true")
                .append("\">Yes</a>  |  <a href=\"http://localhost:4200/user/give-consent?tempDriveId=")
                .append(tempDrive.getId())
                .append("&passengerId=")
                .append(passenger.getId())
                .append("&driveAccepted=false")
                .append("\">No</a>");
        return builder.toString();
    }

    private String shortenRouteName(String routeName) {
        List<String> waypoints = new ArrayList<>();
        for (String waypoint : routeName.split("-")) {
            waypoints.add(waypoint.split(",")[0]);
        }
        return String.join("-", waypoints);
    }

    @Async
    public void sendDriveRejectedEmail(TempDrive tempDrive, Passenger passenger, Passenger rejectPassenger) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(Objects.requireNonNull(env.getProperty(emailTo)));
        helper.setFrom(emailFrom);
        helper.setSubject("Drive rejection");
        helper.setText(buildDriveRejectionEmail(tempDrive, passenger, rejectPassenger), true);
        javaMailSender.send(mimeMessage);
    }

    private String buildDriveRejectionEmail(TempDrive tempDrive, Passenger passenger, Passenger rejectPassenger) {
        StringBuilder builder = new StringBuilder();
        builder.append("Hello ")
                .append(passenger.getName())
                .append(" ")
                .append(passenger.getSurname())
                .append(",<br/>")
                .append("We regret to inform you that the drive on route ")
                .append(shortenRouteName(tempDrive.getRoute().getRouteName()))
                .append(", which was supposed to start at ")
                .append(tempDrive.getStartDate())
                .append(" will not happen because passenger ")
                .append(rejectPassenger.getName().charAt(0))
                .append(".")
                .append(rejectPassenger.getName().charAt(0))
                .append(".")
                .append(" rejected the ride.<br/>")
                .append("You can still make another drive order and hope for the best!");
        return builder.toString();
    }

    @Async
    public void sendDriverRejectedDriveEmail(Drive drive, String rejectReason, Passenger passenger) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(Objects.requireNonNull(env.getProperty(emailTo)));
        helper.setFrom(emailFrom);
        helper.setSubject("Driver rejected drive");
        helper.setText(buildDriverRejectedDrive(drive, passenger, rejectReason), true);
        javaMailSender.send(mimeMessage);
    }

    private String buildDriverRejectedDrive(Drive drive, Passenger passenger, String reasonOfCancellation) {
        StringBuilder builder = new StringBuilder();
        builder.append("Hello ")
                .append(passenger.getName())
                .append(" ")
                .append(passenger.getSurname())
                .append(",<br/>")
                .append("We regret to inform you that the drive on route ")
                .append(shortenRouteName(drive.getRoute().getRouteName()))
                .append(" will not happen because driver cancelled drive. ")
                .append("The reason of cancellation is next: <br/>")
                .append(reasonOfCancellation)
                .append(". <br/>")
                .append("Your tokens will be added back. You can still make another drive order and hope for the best!");
        return builder.toString();
    }
}
