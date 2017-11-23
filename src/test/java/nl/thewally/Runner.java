package nl.thewally;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        monochrome = true,
        plugin = {"pretty", "json:target/cucumber/report.json"},
        dryRun = false,
        glue = {"nl.thewally.stepdefs"},
        features = {"src/test/resources/features"}
//        ,tags = {"@x"}
)
public class Runner {
}
