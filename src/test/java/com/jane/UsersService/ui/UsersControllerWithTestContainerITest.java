package com.jane.UsersService.ui;

import com.jane.UsersService.ui.model.User;
import com.jane.UsersService.ui.model.UserRest;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
public class UsersControllerWithTestContainerITest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:9.2.0");

    @LocalServerPort
    private int port;

    @BeforeAll
    void setup(){
        RestAssured.baseURI="http://localhost";
        RestAssured.port = port;
    }
    @Order(1)
    @Test
    void testContainerIsRunning() {
        assertTrue(mysqlContainer.isRunning());
    }

    @Order(2)
    @Test
    void testCreateMethod_whenValidDetailsProvided_returnsCreatedUser(){
//        Arrange
        Headers headers = new Headers(
                new Header("Content-Type", "application/json"),
                new Header("Accept", "application/json")
        );
        User newUser = new User("Ritu", "Bafna","abc@test.com", "12345678");



//        Act
        Response response = given()
                .headers(headers)
                .body(newUser)
        .when().post("/users")
        .then()
                .extract()
                .response();
//        Assert
//        UserRest createdUser = response.as(UserRest.class);
//        assertEquals(201, response.statusCode());
//        assertEquals(newUser.getFirstName(), createdUser.getFirstName(),"Created first name doesn't match the returned one");
//        assertEquals(newUser.getLastName(), createdUser.getLastName(), " Created last name doesn't match the returned one");
//        assertEquals(newUser.getEmail(), createdUser.getEmail(), " Created email doesn't match the returned one");
//        assertNotNull(createdUser.getId());

//        or

        assertEquals(201, response.statusCode());
        assertEquals(newUser.getFirstName(), response.jsonPath().getString("firstName"),"Created first name doesn't match the returned one");
        assertEquals(newUser.getLastName(), response.jsonPath().getString("lastName"), " Created last name doesn't match the returned one");
        assertEquals(newUser.getEmail(), response.jsonPath().getString("email"), " Created email doesn't match the returned one");
        assertNotNull(response.jsonPath().getString("id"));
    }


}
