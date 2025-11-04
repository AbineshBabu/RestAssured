package stepDefinition;


import DTO.Createuser;
import config.APIClient;
import config.RequestSpecFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import support.TestContext;
import testData.TestDataFactory;

import java.util.Map;
import static org.junit.Assert.*;


public class API {

    private final TestContext testContext;
    public Createuser requestuser;
    public Createuser responseuser;

    public API(TestContext testContext){
        this.testContext=testContext;
    }

    private APIClient api(){
        String service = testContext.getService();
        if (service == null){
            throw new IllegalArgumentException("please specify the valid service name");
        }

        return new APIClient(RequestSpecFactory.requestspec(service));
    }


    @And("user send request to {string}")
    public void user_send_request_to(String endpoint){
        Response response=api().get(endpoint);
        testContext.setResponse(response);
    }

    @Then("user verify the response contains {string}")
    public void user_verify_the_response_contains(String responseValue) {
        assertTrue(testContext.getResponse().getBody().asString().contains(responseValue));
    }

    @And("user send request to {string} with path parameter as {int}")
    public void user_send_request_to_with_path_parameter_as(String endpoint, int id) {
        Response response=api().get(endpoint+"/{id}",id);
        testContext.setResponse(response);
    }

    @Then("user verify the response contains {string},{string},{string},{string},{string} for get users")
    public void user_verify_the_response_contains_for_get_users(String id, String name, String email, String gender, String status) {
        Map<String,Object> responseMap=testContext.getResponse().getBody().jsonPath().getMap("");
        assertTrue(responseMap.containsKey(id));
        assertTrue(responseMap.containsKey(name));
        assertTrue(responseMap.containsKey(email));
        assertTrue(responseMap.containsKey(gender));
        assertTrue(responseMap.containsKey(status));


    }


    @And("user send post request to {string}")
    public void user_send_post_request_to(String endpoint) {
        requestuser = TestDataFactory.randomUser();
        Response response=api().post(endpoint,requestuser);
        testContext.setResponse(response);
        responseuser=response.as(Createuser.class);
    }


    @Then("user verify the response contains {string},{string},{string},{string},{string} for post users")
    public void user_verify_the_response_contains_for_post_users(String id, String name, String email, String gender, String status) {
        assertNotNull(responseuser.getId());
        assertNotNull(responseuser.getName());
        assertNotNull(responseuser.getStatus());
        assertNotNull(responseuser.getGender());
        assertNotNull(responseuser.getEmail());

        assertEquals(requestuser.getName() ,responseuser.getName());
        assertEquals(requestuser.getGender() ,responseuser.getGender());
        assertEquals(requestuser.getStatus() ,responseuser.getStatus());
        assertEquals(requestuser.getEmail() ,responseuser.getEmail());
    }

    @And("user send put request to {string} endpoint for {int}")
    public void user_send_put_request_to_endpoint_for(String endpoint, int id) {

        requestuser = TestDataFactory.randomUser();
        Response response=api().put(endpoint+"/{id}",requestuser,id);
        testContext.setResponse(response);

        responseuser=response.as(Createuser.class);
    }


    @Then("user verify the response contains {string},{string},{string},{string},{string} for put users")
    public void user_verify_the_response_contains_for_put_users(String id, String name, String email, String status, String gender) {

        Map<String,Object> responseMap=testContext.getResponse().getBody().jsonPath().getMap("");

        assertTrue(responseMap.containsKey(id));
        assertTrue(responseMap.containsKey(name));
        assertTrue((responseMap.containsKey(email)));
        assertTrue(responseMap.containsKey((status)));
        assertTrue(responseMap.containsKey(gender));

        assertEquals(requestuser.getName(),responseuser.getName());
        assertEquals(requestuser.getGender(),responseuser.getGender());
        assertEquals(requestuser.getStatus(),responseuser.getStatus());
        assertEquals(requestuser.getEmail(),responseuser.getEmail());
    }


    @And("user send patch request to {string} endpoint for {int} to patch {string}")
    public void user_send_patch_request_to_endpoint_for_to_patch(String endpoint, int id, String patchField) {
        requestuser=TestDataFactory.patchField(patchField);
        Response response=api().patch(endpoint+"/{id}",requestuser,id);
        testContext.setResponse(response);
        responseuser=response.as(Createuser.class);
    }

    @Then("user verify the response contains updated value for users {string}")
    public void user_verify_the_response_contains_updated_value_for_users(String patchedfield) {
        String fieldName=patchedfield.toLowerCase();

        switch (fieldName){
            case "name":
                assertEquals(requestuser.getName(),responseuser.getName());
                break;
            case "email":
                assertEquals(requestuser.getEmail(),responseuser.getEmail());
                break;
            case "gender":
                assertEquals(requestuser.getGender(),responseuser.getGender());
                break;
            case "status":
                assertEquals(requestuser.getStatus(),responseuser.getStatus());
                break;
            default: throw  new IllegalArgumentException("unsupported field "+patchedfield);
        }

    }

    @And("user send delete request to {string} endpoint for {int}")
    public void user_send_delete_request_to_endpoint_for(String endpoint, int id) {
        Response response=api().delete(endpoint+"/{id}",id);
        testContext.setResponse(response);
    }

    @Then("user verify the user {int} does not exist using {string}")
    public void user_verify_the_user_does_not_exist_using(int userId, String endpoint) {
        Response response=api().get(endpoint+"/{id}",userId);
        testContext.setResponse(response);
        assertEquals(404, testContext.getResponse().statusCode());
    }

}