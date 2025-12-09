package StepDefinitions;

import org.openqa.selenium.By;
import base.BaseTest;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LoginStepDefinitions extends BaseTest {

	// static WebDriver driver;

	@Given("User is on login page")
	public void when_user_on_loggingPage() {
		setup();

	}

	/*
	 * @When("User enters a valid login credentails") public void
	 * when_user_pass_Valid_credentials() {
	 * driver.findElement(By.id("user-name")).sendKeys("standard_user");
	 * driver.findElement(By.id("password")).sendKeys("secret_sauce"); }
	 */

	@When("User enters a {string} and {string}")
	public void when_user_pass_Valid_credentials(String username, String password) {
		driver.findElement(By.id("user-name")).sendKeys(username);
		driver.findElement(By.id("password")).sendKeys(password);
	}

	@And("Click on login Button")
	public void when_User_Clicks_On_LoginButton() {
		driver.findElement(By.id("login-button")).click();

	}

	@Then("User navigate to home page")
	public void when_User_get_LoggedIn() {

	}

	@And("Close the browser")
	public void when_user_close_browser() {
		tearup();
	}
}
