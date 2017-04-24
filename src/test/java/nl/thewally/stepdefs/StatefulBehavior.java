package nl.thewally.stepdefs;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import nl.thewally.freemarker.TemplateHandler;
import nl.thewally.freemarker.User;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class StatefulBehavior {

    @Rule
    public WireMockRule generic = new WireMockRule(8888);

    @Autowired
    private Environment env;

    @Before
    public void prepare() {
        if(!generic.isRunning()) {
            generic.start();
            WireMock.configureFor(env.getProperty("server.host"), 8888);
            WireMock.reset();
        }
    }

    @Given("^possible states are available$")
    public void possibleStatesAreAvailable(List<String> states) throws Throwable {
        TemplateHandler template = new TemplateHandler();
        for(String state:states) {
            template.setTemplate("response/setState.response.xml.ftl");
            template.setValue("state", state);

            UUID uuidVal = UUID.randomUUID();
            generic.stubFor(post(urlEqualTo("/SetState"))
                    .inScenario("SCENARIO")
                    .withId(uuidVal)
                    .withRequestBody(containing(state))
                    .willSetStateTo(state)
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "text/xml; charset=\"utf-8\"")
                            .withBody(template.getOutput())));

        }

        template = new TemplateHandler();

        for (String state : states) {
            template.setTemplate("response/getState.response.xml.ftl");
            template.setValue("state", state);
            UUID uuidVal = UUID.randomUUID();
            generic.stubFor(get(urlEqualTo("/GetState"))
                    .inScenario("SCENARIO")
                    .whenScenarioStateIs(state)
                    .withId(uuidVal)
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "text/xml; charset=\"utf-8\"")
                            .withBody(template.getOutput())));
        }
    }

    @When("^change state to (.*)$")
    public void usersAreAvailable(String states) throws Throwable {

    }

    @Then("^state is changed to (.*)$")
    public void stateIsChangedTo(String state) throws Throwable {

    }

    @After
    public void close() {
        if(generic.isRunning()) {
            generic.stop();
        }
    }

}
