package stepDefinition;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import support.ResponseAsserts;
import support.TestContext;

import static org.junit.Assert.assertEquals;
import static support.ResponseAsserts.assertMatchesSchema;

public class CommonSteps {

    private final TestContext testContext;

    public CommonSteps(TestContext testContext){this.testContext=testContext;}

    @Then("user verify the {int} response status code")
    public void user_verify_the_response_status_code(int statuscode){
        ResponseAsserts.assertStatusCode(testContext.getResponse(),statuscode);
    }

    @Given("user set service to {string}")
    public void user_set_service_to(String serviceName) {
        testContext.setServiceName(serviceName.toLowerCase());
    }

    @Then("response matches {string} schema")
    public void response_matches_schema(String schemaName) {
        String path="schemas/"+schemaName+".json";
        assertMatchesSchema(testContext.getResponse(), path);
    }
}
