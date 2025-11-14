Feature: To test the api methods

  @P2 @P1
  Scenario: To verify the get method
    Given user set service to "users"
    And user send request to "/users"
    Then user verify the 200 response status code
    Then user verify the response contains "name"
    Then response matches "users-list" schema

  @P2 @P1
  Scenario: To verify the get method with a path parameter
    Given user set service to "users"
    And user send post request to "/users"
    And user send a get request to "/users" with the created userid as path parameter
    Then user verify the 200 response status code
    Then user verify the response contains "id","name","email","gender","status" for get users
    Then response matches "users-details" schema

  @P1
  Scenario: To verify the post method with a path parameter
    Given user set service to "users"
    And user send post request to "/users"
    Then user verify the 201 response status code
    Then user verify the response contains "id","name","email","gender","status" for post users

  @P1
  Scenario: To verify the put method with a path parameter
    Given user set service to "users"
    And user send post request to "/users"
    And user send put request to "/users" with the created userid as path parameter
    Then user verify the 200 response status code
    Then user verify the response contains "id","name","email","gender","status" for put users

  @P1
  Scenario: To verify the patch method with a path parameter
    Given user set service to "users"
    And user send post request to "/users"
    And user send patch request to "/users" with the created userid as path parameter to patch "name"
    Then user verify the 200 response status code
    Then user verify the response contains updated value for users "name"

  @P1
  Scenario: To verify the delete method with a path parameter
    Given user set service to "users"
    And user send post request to "/users"
    And user send delete request to "/users" with the created userid as path parameter
    Then user verify the 204 response status code
    Then user verify the user 8225394 does not exist using "/users"