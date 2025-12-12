package StepDefinitions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import pages.LoginPage;
import utils.ConfigReader;
import utils.DriverFactory;
import org.testng.Assert;

import java.time.Duration;

public class LoginStepDefinitions {

    private WebDriver driver;
    private LoginPage loginPage;

    private String normalize(String raw) {
        if (raw == null) return "";
        String s = raw.trim();
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
            s = s.substring(1, s.length() - 1).trim();
        }
        if (s.startsWith("<") && s.endsWith(">")) {
            s = s.substring(1, s.length() - 1).trim();
        }
        return s;
    }

    @Given("User is on login page")
    public void user_is_on_login_page() {
        String browser = ConfigReader.getBrowser();
        DriverFactory.initDriver(browser);
        driver = DriverFactory.getDriver();
        if (driver == null) {
            throw new IllegalStateException("Driver not initialized for browser: " + browser);
        }
        loginPage = new LoginPage(driver);

        String baseUrl = ConfigReader.getBaseUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("Base URL is null. Place config.properties in src/test/resources or pass -Dbase.url");
        }
        loginPage.open(baseUrl);
    }

    @When("User enters a {string} and {string}")
    public void user_enters_a_and(String usernameRaw, String passwordRaw) {
        String username = normalize(usernameRaw);
        String password = normalize(passwordRaw);

        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
    }

    @When("Click on login Button")
    public void click_on_login_button() {
        loginPage.clickLogin();
    }

    /**
     * Generic outcome assertion:
     * - expected = "success"  -> assert inventory page
     * - expected = "locked"   -> assert lockout error message
     * - expected = "error"    -> assert generic error message
     */
    @Then("User should see {string}")
    public void user_should_see(String expectedRaw) {
        String expected = normalize(expectedRaw).toLowerCase();

        if ("success".equals(expected)) {
            // wait for inventory URL or inventory element
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
            try {
                wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("/inventory.html"),
                    ExpectedConditions.visibilityOf(loginPage.getInventoryContainerElement()) // new Page Object method
                ));
            } catch (Exception e) {
                // continue to assert below (will fail with useful message)
            }
            boolean onInventory = loginPage.isOnInventoryPage(); // uses URL or element check
            Assert.assertTrue(onInventory, "Expected to be on inventory page after login. Current URL: " + driver.getCurrentUrl());
        } else if ("locked".equals(expected)) {
            // Saucedemo shows specific locked out message; check contains 'locked out'
            String err = loginPage.getErrorMessage();
            Assert.assertTrue(err != null && err.toLowerCase().contains("locked"), "Expected lockout error but got: " + err);
        } else { // treat as generic error expectation
            String err = loginPage.getErrorMessage();
            Assert.assertTrue(err != null && !err.isBlank(), "Expected an error message but none was displayed.");
        }
    }

    @Then("User navigate to home page")
    public void user_navigate_to_home_page() {
        // backward compatibility step (keeps your original step)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        try {
            wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/inventory.html"),
                ExpectedConditions.visibilityOf(loginPage.getInventoryContainerElement())
            ));
        } catch (Exception e) {
            // pass
        }
        boolean onInventory = loginPage.isOnInventoryPage();
        Assert.assertTrue(onInventory, "Expected to be on inventory page after login. Current URL: " + driver.getCurrentUrl());
    }

    @Then("Close the browser")
    public void close_the_browser() {
        try {
            DriverFactory.quitDriver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
