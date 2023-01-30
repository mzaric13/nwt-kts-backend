package nwt.kts.backend.controller;

import nwt.kts.backend.dto.login.EmailPasswordLoginDTO;
import nwt.kts.backend.dto.returnDTO.TokenDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class DriveControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String accessTokenPassenger;

    private String accessTokenDriver;

    @BeforeEach
    public void loginDriver() {
        ResponseEntity<TokenDTO> responseEntity =
                restTemplate.postForEntity("/auth/login-credentials",
                        new EmailPasswordLoginDTO("mirko.ivanic@gmail.com", "sifra123"),
                        TokenDTO.class);
        TokenDTO tokenDTO = responseEntity.getBody();
        accessTokenDriver = tokenDTO.getAccessToken();
    }

    @BeforeEach
    public void loginPassenger() {
        ResponseEntity<TokenDTO> responseEntity =
                restTemplate.postForEntity("/auth/login-credentials",
                        new EmailPasswordLoginDTO("darko.darkovic@gmail.com", "sifra123"),
                        TokenDTO.class);
        TokenDTO tokenDTO = responseEntity.getBody();
        accessTokenPassenger = tokenDTO.getAccessToken();
    }

    @Test
    public void basicTest() {
        System.out.println(accessTokenPassenger);
        System.out.println(accessTokenDriver);
    }

}
