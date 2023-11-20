package pl.edu.pw.mwotest;


import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberOptions(
        features = "src/test/resources/features_it",
        glue = "pl.edu.pw.mwotest.cucumber_it",
        plugin = {"pretty", "html:target/cucumber_it/cucumber-report.html", "json:target/cucumber_it/cucumber-report.json"}
)
@RunWith(Cucumber.class)
public class CucumberIntegrationTestsEntrypoint {
}
