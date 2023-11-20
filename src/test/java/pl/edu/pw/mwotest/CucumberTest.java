package pl.edu.pw.mwotest;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;


@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features_it",
        glue = "pl.edu.pw.mwotest.cucumber_it",
        plugin = {"pretty", "html:target/cucumber_it/cucumber-report.html", "json:target/cucumber_it/cucumber-report.json"}
)
public class CucumberTest {
}
