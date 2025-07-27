# User Service REST Assured
                            given() -> when() -> then()
Fluent API with BDD syntax


REST Assured test methods are run with database created in a Docker container.

Request and Response logs are printed( methodwise and overall).
Request and Response specifications are configured in @BeforeAll lifecycyle (for overall) to avoid redundant codes in methods.
Test Methods in UsersControllerWithTestContainerITest.java:

testCreateMethod_whenValidDetailsProvided_returnsCreatedUser
testLogin_whenValidCredentialsProvided_returnsTokenAndValidUserIdHeaders