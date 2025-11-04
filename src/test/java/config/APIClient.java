package config;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public final class APIClient {

    private final RequestSpecification requestSpecification;

    public APIClient(RequestSpecification requestSpecification){
        this.requestSpecification=requestSpecification;
    }

    public Response get(String endpoint){
        return RestAssured.given().spec(requestSpecification).get(endpoint);
    }

    public Response get(String endpoint,Object... param){
        return RestAssured.given().spec(requestSpecification).get(endpoint, param);
    }

    public Response post(String endpoint,Object payload){
        return RestAssured.given().spec(requestSpecification).body(payload).post(endpoint);
    }

    public Response put(String endpoint,Object payload,Object... param){
        return RestAssured.given().spec(requestSpecification).body(payload).put(endpoint,param);
    }

    public Response patch(String endpoint,Object payload,Object... param){
        return RestAssured.given().spec(requestSpecification).body(payload).patch(endpoint,param);
    }

    public Response delete(String endpoint,Object... param){
        return RestAssured.given().spec(requestSpecification).delete(endpoint,param);
    }


}
