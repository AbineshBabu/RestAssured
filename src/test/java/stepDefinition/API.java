package stepDefinition;


import DTO.Createuser;
import clients.APIClient;
import clients.UsersClient;
import config.RequestSpecFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import static support.ResponseAsserts.*;
import support.TestContext;
import testData.TestDataFactory;

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

    private UsersClient userClient(){
        return new UsersClient(api());
    }


    @And("user send request to {string}")
    public void user_send_request_to(String endpoint){
        Response response = userClient().getUsers();
        testContext.setResponse(response);
    }

    @Then("user verify the response contains {string}")
    public void user_verify_the_response_contains(String responseValue) {
        assertBodyContains(testContext.getResponse(), responseValue);
    }

    @And("user send a get request to {string} with the created userid as path parameter")
    public void user_send_request_to_with_the_created_userid_as_path_parameter(String endpoint) {
        Response response = userClient().getUserById(testContext.getUserId());
        testContext.setResponse(response);
    }

    @Then("user verify the response contains {string},{string},{string},{string},{string} for get users")
    public void user_verify_the_response_contains_for_get_users(String id, String name, String email, String gender, String status) {
        assertObjectHasKeys(testContext.getResponse(),id,name,email,gender,status);
    }


    @And("user send post request to {string}")
    public void user_send_post_request_to(String endpoint) {
        requestuser = TestDataFactory.randomUser();
        Response response= userClient().CreateUser(requestuser);
        testContext.setResponse(response);
        responseuser=response.as(Createuser.class);
        testContext.setUserId(responseuser.getId());
    }


    @Then("user verify the response contains {string},{string},{string},{string},{string} for post users")
    public void user_verify_the_response_contains_for_post_users(String id, String name, String email, String gender, String status) {
        assertObjectHasKeys(testContext.getResponse(),id,name,email,gender,status);

        assertEquals(requestuser.getName() ,responseuser.getName());
        assertEquals(requestuser.getGender() ,responseuser.getGender());
        assertEquals(requestuser.getStatus() ,responseuser.getStatus());
        assertEquals(requestuser.getEmail() ,responseuser.getEmail());
    }

    @And("user send put request to {string} with the created userid as path parameter")
    public void user_send_put_request_to_with_the_created_userid_as_path_parameter(String endpoint) {

        requestuser = TestDataFactory.randomUser();
        Response response= userClient().updateUser(requestuser, testContext.getUserId());
        testContext.setResponse(response);

        responseuser=response.as(Createuser.class);
    }


    @Then("user verify the response contains {string},{string},{string},{string},{string} for put users")
    public void user_verify_the_response_contains_for_put_users(String id, String name, String email, String status, String gender) {

        assertObjectHasKeys(testContext.getResponse(),id,name,gender,status,email);

        assertEquals(requestuser.getName(),responseuser.getName());
        assertEquals(requestuser.getGender(),responseuser.getGender());
        assertEquals(requestuser.getStatus(),responseuser.getStatus());
        assertEquals(requestuser.getEmail(),responseuser.getEmail());
    }


    @And("user send patch request to {string} with the created userid as path parameter to patch {string}")
    public void user_send_patch_request_to_with_the_created_userid_as_path_parameter_to_patch(String endpoint, String patchField) {
        requestuser=TestDataFactory.patchField(patchField);
        Response response= userClient().patchUser(requestuser, testContext.getUserId());
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

    @And("user send delete request to {string} with the created userid as path parameter")
    public void user_send_delete_request_to_endpoint_for(String endpoint) {
        Response response= userClient().deleteUser(testContext.getUserId());
        testContext.setResponse(response);
    }

    @Then("user verify the user {int} does not exist using {string}")
    public void user_verify_the_user_does_not_exist_using(int id, String endpoint) {
        Response response= userClient().getUserById(id);
        testContext.setResponse(response);
        assertEquals(404, testContext.getResponse().statusCode());
    }

}