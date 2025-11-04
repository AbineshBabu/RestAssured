package config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public final class RequestSpecFactory {

    private RequestSpecFactory(){}

    public static RequestSpecification requestspec(String serviceName){
        String serviceNameNormalized= serviceName.toLowerCase();
        String baseurl=ServiceRegistry.getBaseUrl(serviceNameNormalized);


        String token=System.getenv("GOREST_TOKEN");

        if (token == null || token.isBlank()){
            String serviceToken=serviceNameNormalized+".token";
            token=ConfigManager.getOrDefault(serviceToken,"");

        }

        if(token == null || token.isBlank()){
            token=ConfigManager.getOrDefault("auth.token","");
        }


        RequestSpecBuilder requestSpecBuilder=new RequestSpecBuilder()
                .setBaseUri(baseurl).setContentType(ContentType.JSON).setAccept(ContentType.JSON);


        if (token != null && !token.isBlank()){
            requestSpecBuilder.addHeader("Authorization","Bearer "+token);
        }

        return requestSpecBuilder.build();

    }

}
