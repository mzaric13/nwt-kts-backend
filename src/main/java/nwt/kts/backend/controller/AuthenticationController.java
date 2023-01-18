package nwt.kts.backend.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import nwt.kts.backend.dto.login.EmailPasswordLoginDTO;
import nwt.kts.backend.dto.login.FacebookLoginData;
import nwt.kts.backend.dto.login.GoogleTokenDTO;
import nwt.kts.backend.dto.returnDTO.TokenDTO;
import nwt.kts.backend.entity.Passenger;
import nwt.kts.backend.entity.User;
import nwt.kts.backend.repository.UserRepository;
import nwt.kts.backend.service.DriverService;
import nwt.kts.backend.service.PassengerService;
import nwt.kts.backend.service.UserService;
import nwt.kts.backend.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.Console;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

    @Value("${google.clientId}")
    private String googleClientId;

    @Value("${secretPsw}")
    private String secretPsw;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private PassengerService passengerService;

    @PostMapping("/login-credentials")
    public ResponseEntity<TokenDTO> createAuthenticationToken(
            @RequestBody EmailPasswordLoginDTO emailPasswordLoginDTO, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                emailPasswordLoginDTO.getUsername(), emailPasswordLoginDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        if (user.getRole().getName().equals("ROLE_DRIVER")) {
            driverService.setDriverLoginTime(user.getUsername());
        }
        String jwt = tokenUtils.generateToken(user.getUsername(), user.getRole().getName());
        int expiresIn = tokenUtils.getExpiredIn();
        return ResponseEntity.ok(new TokenDTO(jwt, expiresIn));
    }

    @PostMapping("/login-google")
    public ResponseEntity<TokenDTO> google(@RequestBody GoogleTokenDTO googleTokenDTO) throws IOException {
        final NetHttpTransport transport = new NetHttpTransport();
        final JacksonFactory jacksonFactory = JacksonFactory.getDefaultInstance();
        GoogleIdTokenVerifier.Builder verifier =
                new GoogleIdTokenVerifier.Builder(transport, jacksonFactory)
                        .setAudience(Collections.singletonList(googleClientId));
        final GoogleIdToken googleIdToken = GoogleIdToken.parse(verifier.getJsonFactory(), googleTokenDTO.getValue());
        final GoogleIdToken.Payload payload = googleIdToken.getPayload();
        Passenger passenger = passengerService.createPassengerGoogleLogin(payload);
        String jwt = tokenUtils.generateToken(passenger.getUsername(), passenger.getRole().getName());
        int expiresIn = tokenUtils.getExpiredIn();
        return ResponseEntity.ok(new TokenDTO(jwt, expiresIn));
    }

    @PostMapping("/login-facebook")
    public ResponseEntity<TokenDTO> facebook(@RequestBody FacebookLoginData facebookLoginData) throws IOException {
        Facebook facebook = new FacebookTemplate(facebookLoginData.getValue());
        final String [] fields = {"email", "picture"};
        org.springframework.social.facebook.api.User user = facebook.fetchObject("me", org.springframework.social.facebook.api.User.class, fields);
        Map<String, Object> picture = user.getExtraData();
        Map<String, Object> pic1 = (Map<String, Object>) picture.get("picture");
        Map<String, Object> pic2 = (Map<String, Object>) pic1.get("data");
        String pict = (String) pic2.get("url");
        Passenger passenger = passengerService.createPassengerFacebookLogin(facebookLoginData, user.getEmail(), pict);
        String jwt = tokenUtils.generateToken(passenger.getUsername(), passenger.getRole().getName());
        int expiresIn = tokenUtils.getExpiredIn();
        return ResponseEntity.ok(new TokenDTO(jwt, expiresIn));
    }

}
