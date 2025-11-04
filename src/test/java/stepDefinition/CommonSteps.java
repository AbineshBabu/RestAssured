package stepDefinition;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import support.TestContext;

import static org.junit.Assert.assertEquals;

public class CommonSteps {

    private final TestContext testContext;

    public CommonSteps(TestContext testContext){this.testContext=testContext;}

    @Then("user verify the {int} response status code")
    public void user_verify_the_response_status_code(int statuscode){
        assertEquals(statuscode,testContext.getResponse().getStatusCode());
    }

    @Given("user set service to {string}")
    public void user_set_service_to(String serviceName) {
        testContext.setServiceName(serviceName.toLowerCase());
    }
}
