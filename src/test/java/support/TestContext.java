package support;

import io.restassured.response.Response;

public class TestContext {

    private Response response;
    private String seriveName;
    private int userId;

    public Response getResponse(){
        return this.response;
    }

    public void setResponse(Response response){
        this.response=response;
    }

    public String getService(){
        return this.seriveName;
    }

    public void setServiceName(String seriveName){
        this.seriveName=seriveName;
    }

    public void setUserId(int userId){
        this.userId=userId;
    }

    public int getUserId(){
        return this.userId;
    }

}
