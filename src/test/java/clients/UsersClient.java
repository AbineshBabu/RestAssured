package clients;

import io.restassured.response.Response;

public class UsersClient {

    private APIClient apiClient;

    public UsersClient(APIClient apiClient){
        this.apiClient=apiClient;
    }

    public Response getUsers(){
        return apiClient.get("/users");
    }

    public Response getUserById(int id){
        return apiClient.get("/users/{id}",id);
    }

    public Response CreateUser(Object payload){
        return apiClient.post("/users",payload);
    }

    public Response updateUser(Object payload, int id){
        return apiClient.put("/users/{id}",payload,id);
    }

    public Response patchUser(Object payload,int id){
        return apiClient.patch("/users/{id}",payload,id);
    }

    public Response deleteUser(int id){
        return apiClient.delete("/users/{id}",id);
    }
}
