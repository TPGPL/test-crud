package pl.edu.pw.mwotest.cucumber_it;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;

public class ExampleTestSteps {
    private static int a;
    private static int b;
    private static int result;

    @Given("a is {int}")
    public void aIs(int arg0) {
        a = arg0;
    }

    @And("b is {int}")
    public void bIs(int arg0) {
        b = arg0;
    }

    @When("I add a and b")
    public void iAddAAndB() {
        result = a + b;
    }

    @Then("the result is {int}")
    public void theResultIs(int arg0) {
        Assertions.assertThat(result).isEqualTo(arg0);
    }

    @When("I subtract a from b")
    public void iSubtractAFromB() {
        result = b - a;
    }
}
