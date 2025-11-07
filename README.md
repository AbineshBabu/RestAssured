````markdown
# RestAssured – API Test Automation Framework

Backend API test framework built with:

- **Java 21**
- **Maven**
- **Cucumber 7 + JUnit 4**
- **Rest Assured 5**
- **Allure** for reports
- **GitHub Actions** CI

Supports multiple environments (`dev`, `qa`, …) and multiple microservices (currently `users`).

---

## 1. Project Structure

```text
src
└── test
    ├── java
    │   ├── clients
    │   │   ├── APIClient.java
    │   │   └── UsersClient.java
    │   ├── config
    │   │   ├── APIHooks.java
    │   │   ├── ConfigManager.java
    │   │   ├── RequestSpecFactory.java
    │   │   └── ServiceRegistry.java
    │   ├── DTO
    │   │   └── Createuser.java
    │   ├── feature
    │   │   └── api.feature
    │   ├── runner
    │   │   ├── RegressionRunner.java
    │   │   └── SmokeRunner.java
    │   ├── stepDefinition
    │   │   ├── API.java
    │   │   └── CommonSteps.java
    │   ├── support
    │   │   ├── ResponseAsserts.java
    │   │   └── TestContext.java
    │   └── testData
    │       └── TestDataFactory.java
    └── resources
        ├── allure.properties
        ├── config-dev.properties
        ├── config-qa.properties
        └── schemas
            ├── users-list.json
            └── users-details.json
````

### 1.1 Folder overview

**`clients/` – HTTP & microservice clients**

* `APIClient`

    * Thin wrapper over Rest Assured.
    * Exposes `get`, `post`, `put`, `patch`, `delete` using a given `RequestSpecification`.

* `UsersClient`

    * Microservice-specific client for the `users` API.
    * Higher-level methods:

        * `getUsers()`
        * `getUser(int id)`
        * `createUser(Createuser payload)`
        * `updateUser(int id, Createuser payload)`
        * `patchUser(int id, Createuser payload)`
        * `deleteUser(int id)`

**`config/` – environment & request configuration**

* `ConfigManager`

    * Determines active environment in this order:

        1. JVM system property: `-Denv=qa`
        2. OS env var: `TEST_ENV=qa`
        3. Default: `dev`
    * Loads `config-<env>.properties` from `src/test/resources`.
    * Provides:

        * `get(String key)`
        * `getOrDefault(String key, String defaultValue)`

* `ServiceRegistry`

    * Maps logical service names to base URLs using `ConfigManager`.
    * Reads e.g. `users.baseUrl` from `config-qa.properties`.

* `RequestSpecFactory`

    * Builds a Rest Assured `RequestSpecification` per service:

        * `baseUri` from `ServiceRegistry`
        * `ContentType.JSON` + `Accept(JSON)`
        * `Authorization: Bearer <token>`
    * Token resolution order:

        1. Env var `GOREST_TOKEN`
        2. `<service>.token` in `config-<env>.properties` (e.g. `users.token`)
        3. Fallback `auth.token` in `config-<env>.properties`

* `APIHooks`

    * Cucumber `@Before` / `@After` hooks.
    * On scenario failure:

        * Attaches scenario info (name, status, tags) to Allure.
    * After each scenario:

        * Clears `TestContext` (`response` reset to `null`).

**`DTO/` – data transfer objects**

* `Createuser`

    * Lombok-based POJO matching the user JSON:

        * `id`, `name`, `email`, `gender`, `status`.

**`feature/` – Cucumber feature files**

* `api.feature`

    * Contains scenarios for:

        * GET `/users`
        * GET `/users/{id}`
        * POST `/users`
        * PUT `/users/{id}`
        * PATCH `/users/{id}`
        * DELETE `/users/{id}`
    * Scenarios tagged with `@P1` and/or `@P2` to support regression vs smoke suites.
    * Some steps also validate JSON schemas, e.g.

        * `Then response matches "users-list" schema`

**`runner/` – Cucumber JUnit runners**

* `RegressionRunner`

    * Runs all regression tests:

        * `tags = "@P1"`
    * Uses:

        * `features = "src/test/java/feature"`
        * `glue = {"stepDefinition","config","support"}`
        * Allure plugin: `"io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"`

* `SmokeRunner`

    * Runs only smoke tests:

        * `tags = "@P2 and not @P1"`
    * Same glue and plugins as RegressionRunner.
    * Used by GitHub Actions CI.

**`stepDefinition/` – Cucumber step implementations**

* `CommonSteps`

    * `Given user set service to "<service>"`

        * Stores `service` in `TestContext`.
    * `Then user verify the <status> response status code`

        * Uses `ResponseAsserts.assertStatus(context.getResponse(), status)`.

* `API`

    * Implements “send request” and “verify response” steps.
    * Typical flow:

        * Build `UsersClient` from the `serviceName` in `TestContext`.
        * Execute request (GET/POST/PUT/PATCH/DELETE).
        * Store `Response` in `TestContext`.
        * Use `ResponseAsserts` for:

            * body text checks,
            * presence of keys,
            * schema validation, etc.

**`support/` – shared utilities**

* `TestContext`

    * Scenario-scoped state shared across step definitions:

        * `Response response`
        * `String serviceName`
    * PicoContainer injects the same instance into steps and hooks.

* `ResponseAsserts`

    * Central place for assertions + Allure attachments.
    * Methods:

        * `assertStatus(Response, int)`
        * `assertBodyContains(Response, String)`
        * `assertObjectHasKeys(Response, String...)`

            * For single JSON object: `{ "id":1, "name":"..." }`
        * `assertFirstElementHasKeys(Response, String...)`

            * For JSON arrays: `[ { "id":1,... }, {...} ]` – checks keys on first element.
        * `assertMatchesSchema(Response, String schemaPath)`

            * Validates JSON against a schema from `src/test/resources/schemas`.
    * On failure, each method attaches helpful details to Allure (expected vs actual, full body, missing keys, schema error).

**`testData/` – test data generation**

* `TestDataFactory`

    * Uses `DataFaker` to generate realistic random data:

        * `randomUser()` – full `Createuser` payload for POST/PUT.
        * `patchField(String)` – partial `Createuser` payload for PATCH (only one field set).

**`src/test/resources/` – config, schemas, Allure**

* `config-dev.properties`, `config-qa.properties`

    * Per-environment configuration:

        * `env.name=qa`
        * `users.baseUrl=https://gorest.co.in/public/v2`
        * Optional: `orders.baseUrl`, `payments.baseUrl`, etc.
        * `auth.token=` / `users.token=` – fallback tokens (CI usually passes `GOREST_TOKEN` instead).

* `allure.properties`

    * Forces Allure results to `target/allure-results`.

* `schemas/users-list.json`

    * JSON Schema for GET `/users` (list).

* `schemas/users-details.json`

    * JSON Schema for GET `/users/{id}` (single user).

---

## 2. Environment & Configuration

### 2.1 Selecting environment

Environment is resolved in this order:

1. JVM property: `-Denv=qa`
2. OS env var: `TEST_ENV=qa`
3. Default: `dev`

`ConfigManager` then loads `config-<env>.properties` from `src/test/resources`.

You can see which env is used by the log printed from `ConfigManager`, e.g.:

```
>>> Using test ENV = qa
```

### 2.2 Authentication token

`RequestSpecFactory` resolves a token in this order:

1. Env var `GOREST_TOKEN`
2. `<service>.token` in `config-<env>.properties` (e.g. `users.token`)
3. Fallback `auth.token` in `config-<env>.properties`

If a token value is not empty, it is added as:

```http
Authorization: Bearer <token>
```

---

## 3. Running Tests Locally

Make sure you have:

* JDK 21
* Maven 3.8+
* (Optional) Allure CLI for local `allure serve`

From the project root:

```
mvn clean
```

### 3.1 Sequential execution

#### A) Smoke suite only (P2, not P1)

```
mvn test -Dtest=runner.SmokeRunner -Denv=qa
```

#### B) Regression suite only (P1)

```
mvn test -Dtest=runner.RegressionRunner -Denv=qa
```

#### C) All runners sequentially (no parallel)

In `pom.xml`, Surefire is wired to use Maven properties:

```xml
<properties>
    <parallel.mode>classes</parallel.mode>
    <parallel.threads>4</parallel.threads>
</properties>
```

To **disable** parallel (and run all runners sequentially) without editing the POM:

```
mvn test -Denv=qa -Dparallel.mode=none
```

This runs all `*Runner.java` classes one by one.

You can also run runners sequentially yourself:

```
mvn test -Dtest=runner.SmokeRunner -Denv=qa
mvn test -Dtest=runner.RegressionRunner -Denv=qa
```

### 3.2 Parallel execution

Surefire is configured roughly as:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-plugin</artifactId>
  <version>${surefire.version}</version>
  <configuration>
    <useModulePath>false</useModulePath>

    <parallel>${parallel.mode}</parallel>
    <threadCount>${parallel.threads}</threadCount>
    <perCoreThreadCount>false</perCoreThreadCount>

    <includes>
      <include>**/*Runner.java</include>
    </includes>

    <systemPropertyVariables>
      <allure.results.directory>
        ${project.build.directory}/allure-results
      </allure.results.directory>
    </systemPropertyVariables>
  </configuration>
</plugin>
```

By default (no overrides):

```
mvn test -Denv=qa
```

* `parallel.mode=classes`
* `parallel.threads=4`
* `SmokeRunner` and `RegressionRunner` run in parallel.
* Scenarios inside each runner run sequentially (Cucumber 7 + JUnit 4 behaviour).

Change thread count on the fly:

```
mvn test -Denv=qa -Dparallel.threads=2
```

Disable parallel entirely:

```
mvn test -Denv=qa -Dparallel.mode=none
```

---

## 4. Allure Reporting

### 4.1 Generate report locally

1. Run any tests, for example:

   ```
   mvn test -Dtest=runner.SmokeRunner -Denv=qa
   ```

   This produces raw results in:

   ```
   target/allure-results
   ```

2. Generate HTML report:

   ```
   mvn allure:report
   ```

   Output folder:

   ```text
   target/allure-report
   ```

3. Open `target/allure-report/index.html` in a browser.

   With Allure CLI installed, you can also do:

   ```
   allure serve target/allure-results
   ```

### 4.2 What gets attached on failures

* `ResponseAsserts`:

    * On status mismatch:

        * Attachment “Status mismatch” with expected, actual, and body.
    * On missing JSON keys:

        * Attachment with missing key name + full JSON or first element.
    * On schema validation failure:

        * Attachment with schema path, error message, and full body.
* `APIHooks`:

    * When scenario fails:

        * Attachment “Failure info” with:

            * Scenario name
            * Status
            * Tags

This makes debugging failures much easier inside the Allure UI.

---

## 5. Execution Flow (High Level)

1. JUnit runner (`SmokeRunner` or `RegressionRunner`) starts.
2. Cucumber loads `api.feature` and discovers scenarios based on tags.
3. For each scenario:

    * `@Before` hook in `APIHooks` runs.
    * `Given user set service to "users"`

        * `CommonSteps` stores `"users"` into `TestContext`.
    * A “send request” step (e.g. `user send request to "/users"`):

        * `API` step uses `TestContext` to know service = `users`.
        * It uses `UsersClient` (which uses `RequestSpecFactory` + `APIClient`) to call the API.
        * The response is stored in `TestContext`.
    * Assertion steps call `ResponseAsserts`:

        * Status checks
        * JSON key checks
        * Schema checks (`users-list.json`, `users-details.json`)
    * On failure, assertions throw `AssertionError` and attach Allure diagnostics.
    * `@After` hook in `APIHooks` clears the `TestContext` response.

---

## 6. GitHub Actions CI

Workflow file: `.github/workflows/ci.yml`

```yaml
name: API Tests (Smoke - QA)

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]
  # Every Monday 08:00 CET ≈ 07:00 UTC
  schedule:
    - cron: "0 7 * * MON"

jobs:
  smoke-tests-qa:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout repository
        uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'
          cache: maven

      - name: Run Smoke tests (QA)
        run: mvn -B test -Dtest=runner.SmokeRunner -Denv=qa
        env:
          # token is taken from repo secret
          GOREST_TOKEN: ${{ secrets.GOREST_TOKEN_QA }}

      - name: Generate Allure report
        run: mvn -B allure:report

      - name: Upload Allure report artifact
        uses: actions/upload-artifact@v4
        with:
          name: allure-report
          path: target/allure-report
```

**Behaviour:**

* Triggers on:

    * Push to `main`
    * Pull requests into `main`
    * Weekly cron schedule (Monday morning CET)
* Uses `SmokeRunner` (`@P2 and not @P1`) against the `qa` environment.
* Auth token is supplied from GitHub secret `GOREST_TOKEN_QA`.
* Generates an Allure report and uploads it as artifact `allure-report`.
  You can download it from the workflow run and open `index.html` locally.

---

## 7. Extending the Framework (High Level)

* To add a new microservice:

    1. Add `<service>.baseUrl` (and optional `<service>.token`) to each `config-<env>.properties`.
    2. Create `<ServiceName>Client` in `clients/` using `RequestSpecFactory.requestspec("<service>")`.
    3. Add steps in `stepDefinition/` that use this client.
    4. Add feature scenarios and tag them with `@P1` / `@P2`.

* To add new contract checks:

    1. Create a JSON schema under `src/test/resources/schemas`.
    2. Use `ResponseAsserts.assertMatchesSchema(response, "schemas/<file>.json")` in a step.
    3. Optionally create a step definition like:

       ```java
       @Then("response matches {string} schema")
       public void response_matches_schema(String schemaName) {
           ResponseAsserts.assertMatchesSchema(
               testContext.getResponse(),
               "schemas/" + schemaName + ".json"
           );
       }
       ```



