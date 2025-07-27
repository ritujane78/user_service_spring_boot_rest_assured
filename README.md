# User Service REST Assured
                            given() -> when() -> then()
Fluent API with BDD syntax


REST Assured test methods are run with database created in a Docker container.

Request and Response logs are printed( methodwise and overall).
Request and Response specifications are configured in @BeforeAll lifecycyle (for overall) to avoid redundant codes in methods.
The methods are ordered because a user needs to be created first, then valid user id and token are generated, then users are verified with these params.

Test Methods in UsersControllerWithTestContainerITest.java:

testCreateUser_whenValidDetailsProvided_returnsCreatedUser
testLogin_whenValidCredentialsProvided_returnsTokenAndValidUserIdHeaders
testGetUser_whenValidAuthenticationToken_returnsUser
testGetUsers_whenValidTokenAndQueryParamsProvided_returnsPaginatedUsersList