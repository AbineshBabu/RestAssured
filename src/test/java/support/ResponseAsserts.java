package support;

import io.qameta.allure.Allure;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.Assert.*;

public class ResponseAsserts {

    private  ResponseAsserts(){}

    public static void assertStatusCode(Response response, int expectedStatusCode){
        int actual=response.getStatusCode();

        if (expectedStatusCode != actual){
            Allure.addAttachment("Status mismatch","text/plain","Expected: " + expectedStatusCode +
                            "\nActual: " + actual +
                            "\nBody: " + safeBody(response),
                    ".txt");

        }

        assertEquals("Expected status code did not match",expectedStatusCode,actual);

    }

    public static void assertBodyContains(Response response, String expectedAttribute){
        String body=safeBody(response);

        if(!body.contains(expectedAttribute)){
                Allure.addAttachment("Expected attribute is not present","text/plain","Expected to contain: " + expectedAttribute +
                                "\nBody:\n" + body,
                        ".txt");
        }

        assertTrue("Expected attribute was not found",body.contains(expectedAttribute));
    }

    //Use for responses that are a single JSON object:
    //{ "id": 1, "name": "..." }
    public static void assertObjectHasKeys(Response response,String... keys){
        Map<String,Object> map=response.getBody().jsonPath().get("");

        for (String key:keys){
            if(!map.containsKey(key)){
                Allure.addAttachment("Expected key is missing","text/plain",
                        "Missing key: " + key +
                "\nFull JSON: " + safeBody(response),
                        ".txt");

            }
            assertTrue("Expected key is missing", map.containsKey(key));
        }
    }

    //Use for responses that are a JSON array:
    //[ { "id": 1, "name": "..." }, {...} ]
    //This checks keys in the first element.
    public static void assertFirstElementHasKeys(Response response, String... keys){
        List<Map<String, Object>> list = response.getBody().jsonPath().getList("");

        assertNotNull("Expected the response to be not null",list);
        assertFalse("Expected the response to be not empty",list.isEmpty());

        Map<String,Object> firstElement=list.get(0);

        for (String key:keys){
            if (!firstElement.containsKey(key)){
                Allure.addAttachment(
                        "Missing JSON key (first array element)",
                        "text/plain",
                        "Missing key: " + key +
                                "\nFirst element: " + firstElement +
                                "\nFull JSON:\n" + safeBody(response),
                        ".txt"
                );

            }

            assertTrue("Expected key is not found in the first element in response ", firstElement.containsKey(key));
        }

    }

    public static void assertMatchesSchema(Response response, String schemaClassPathLocation){
        try{
            response.then().assertThat().body(matchesJsonSchemaInClasspath(schemaClassPathLocation));
        } catch (Exception e) {
            Allure.addAttachment(
                    "Schema validation failure",
                    "text/plain",
                    "Schema: " + schemaClassPathLocation +
                            "\nError: " + e.getMessage() +
                            "\nBody:\n" + safeBody(response),
                    ".txt"
            );
            throw e;
        }
    }



    public static String safeBody(Response response){
        try {
            return response.getBody().asString();
        } catch (Exception e) {
            return "failed to read body "+e.getMessage();
        }
    }


}
