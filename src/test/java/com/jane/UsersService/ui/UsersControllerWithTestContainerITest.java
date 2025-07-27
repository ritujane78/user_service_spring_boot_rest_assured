package com.jane.UsersService.ui;

import com.jane.UsersService.ui.model.User;
import com.jane.UsersService.ui.model.UserRest;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
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

    public final String TEST_EMAIL = "abc@test.com";
    public final String TEST_PASSWORD = "12345678";

    public String userId;
    public String token;
    private final RequestLoggingFilter requestLoggingFilter = new RequestLoggingFilter();
    private final ResponseLoggingFilter responseLoggingFilter = new ResponseLoggingFilter();

//    Don't log evrything in the request but a few
//    private final RequestLoggingFilter requestLoggingFilter = RequestLoggingFilter.with(LogDetail.BODY,LogDetail.HEADERS);

    @BeforeAll
    void setup(){
        RestAssured.baseURI="http://localhost";
        RestAssured.port = port;
        RestAssured.filters(requestLoggingFilter,responseLoggingFilter);

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
//                .addFilter(new RequestLoggingFilter())
//                .addFilter(new RequestLoggingFilter())
                .build();

        RestAssured.responseSpecification = new ResponseSpecBuilder()
//                .expectStatusCode(anyOf(is(200), is(201), is(204)))
                .expectResponseTime(lessThan(2000L))
//                .expectBody("id",notNullValue())
                .build();
    }
    @Order(1)
    @Test
    void testContainerIsRunning() {
        assertTrue(mysqlContainer.isRunning());
    }

    @Order(2)
    @Test
    void testCreateUser_whenValidDetailsProvided_returnsCreatedUser(){
//        Arrange
//        We do not have to define headers indiduvally. It is done universally in @BeforeAll lifecycle
//        Headers headers = new Headers(
//                new Header("Content-Type", "application/json"),
//                new Header("Accept", "application/json")
//        );
        User newUser = new User("Ritu", "Bafna",TEST_EMAIL,TEST_PASSWORD);



//        Act
        given()
//                .log().all() //individual methods
//                .headers(headers)
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

    @Test
    @Order(3)
    @DisplayName("user credentials are authenticated")
    void testLogin_whenValidCredentialsProvided_returnsTokenAndValidUserIdHeaders(){
//        Arrange
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", TEST_EMAIL);
        credentials.put("password", TEST_PASSWORD);

//        Act
        Response response = given()
                .body(credentials)
        .when()
                .post("/login");
        this.userId = response.header("userId");
        this.token = response.header("token");

//        Assert

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        assertNotNull(userId);
        assertNotNull(token);
    }

    @Test
    @Order(4)
    @DisplayName("/GetUser method works")
    void testGetUser_whenValidAuthenticationToken_returnsUser(){
        given()  //Arrange
                .pathParam("userId", this.userId)
                .header("Authorization", "Bearer " + this.token)
//                .auth().oauth2(this.token)
        .when()  //Act
                .get("/users/{userId}")
        .then()  //Assert
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(this.userId))
                .body("email", equalTo(TEST_EMAIL))
                .body("firstName", notNullValue())
                .body("lastName", notNullValue());
    }

    @Test
    @Order(5)
    @DisplayName("pagination with limits work")
    void testGetUsers_whenValidTokenAndQueryParamsProvided_returnsPaginatedUsersList(){
        given()
                .header("Authorization", "Bearer " + this.token)
                .queryParam("page", 1)
                .queryParam("limit", 10)
        .when()
                .get("/users")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", equalTo(1));
    }
}
