package config;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import support.TestContext;

public class APIHooks {

    private final TestContext testContext;

    public APIHooks(TestContext testContext) {
        this.testContext = testContext;
    }

    @Before
    public void setup() {
        // Keep empty for now – you can add global setup here later
    }

    @After
    public void tearDown(Scenario scenario) {

        // Attach extra info only when scenario fails
        if (scenario.isFailed()) {

            // 1) Basic scenario info
            Allure.addAttachment(
                    "Failure info",
                    "text/plain",
                    "Scenario: " + scenario.getName() +
                            "\nStatus: " + scenario.getStatus() +
                            "\nTags: " + scenario.getSourceTagNames(),
                    ".txt"
            );

        }

        // Always clear shared state after each scenario
        testContext.setResponse(null);
    }
}
