package Controller;

import Main.MainClass;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainClass.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserAPITest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    ParameterizedTypeReference<Map<String, Object>> typeRef = new ParameterizedTypeReference<Map<String, Object>>() {};

    private String getRootUrl() {
        return "http://localhost:" + port;
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void testSignup() {
        String url = getRootUrl() + "/signup";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> data = new HashMap<>();
        String username = "test_" + RandomStringUtils.randomAlphanumeric(5);
        data.put("Username", username);
        data.put("EmailID", username + "@test.com");
        data.put("Password", "1234");
        data.put("MobileNo", "1234567899");
        data.put("Picture", username + "Picture");
        HttpEntity<Map<String, String>> request = new HttpEntity<>(data, headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.POST, request, typeRef);
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertTrue(body.containsKey("message"));
        assertTrue(body.containsKey("UserID"));
        assertTrue(body.get("message").equals("Success"));
    }

    @Test
    public void testLogin() {
        String url = getRootUrl() + "/login";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> data = new HashMap<>();
        data.put("Username", "user1");
        data.put("Password", "1234");
        HttpEntity<Map<String, String>> request = new HttpEntity<>(data, headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.POST, request, typeRef);
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertTrue(body.containsKey("message"));
        assertTrue(body.containsKey("UserID"));
        assertTrue(body.get("message").equals("Success"));
    }

    @Test
    public void testViewUser() {
        String url = getRootUrl() + "/viewUser?username=user1";
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.GET, null, typeRef);
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> user = response.getBody();
        assertTrue(user.containsKey("UserID"));
        assertTrue(user.containsKey("Username"));
        assertTrue(user.containsKey("EmailID"));
        assertTrue(user.containsKey("MobileNo"));
        assertTrue(user.get("Username").equals("user1"));
    }
}