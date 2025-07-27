package com.jane.UsersService.ui;

import com.jane.UsersService.ui.model.User;
import com.jane.UsersService.ui.model.UserRest;
import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
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
    private final RequestLoggingFilter requestLoggingFilter = new RequestLoggingFilter();
    private final ResponseLoggingFilter responseLoggingFilter = new ResponseLoggingFilter();

//    Don't log evrything in the request but a few
//    private final RequestLoggingFilter requestLoggingFilter = RequestLoggingFilter.with(LogDetail.BODY,LogDetail.HEADERS);

    @BeforeAll
    void setup(){
        RestAssured.baseURI="http://localhost";
        RestAssured.port = port;
        RestAssured.filters(requestLoggingFilter,responseLoggingFilter);
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
        given()
//                .log().all() //individual methods
                .headers(headers)
                .body(newUser)
        .when().post("/users")
        .then()
//                .log().all() //individual methods
                .statusCode(201)
                .body("id", notNullValue())
                .body("firstName", equalTo(newUser.getFirstName()))
                .body("lastName", equalTo(newUser.getLastName()))
                .body("email",equalTo(newUser.getEmail()));

//        Assert


    }


}
