package testRunners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)

@CucumberOptions(
        features = {"src/test/java/testRunners/LoginPageRunner.java"},
        glue = {"stepDefinitions"},
        plugin = {"pretty", "json:target/cucumber-reports/cucumber.json", "html:target/cucumber-reports/index.html", "pretty:target/cucumber-reports/cucumber-pretty.txt", "usage:target/cucumber-reports/cucumber-usage.json", "junit:target/cucumber-reports/cucumber-results.xml"},
        strict = true,
        monochrome = true,
        dryRun = true,
        publish = true
)


public class LoginPageRunner {
}
