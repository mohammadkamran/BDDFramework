package utils;

import io.cucumber.java.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class TestHooks {

    @Before
    public void beforeScenario() {
        String browser = ConfigReader.getBrowser();
        DriverFactory.initDriver(browser);
    }

    @After
    public void afterScenario(Scenario scenario) {
        try {
            if (scenario.isFailed() && DriverFactory.getDriver() != null) {
                byte[] screenshot = ((TakesScreenshot) DriverFactory.getDriver()).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "screenshot");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DriverFactory.quitDriver();
        }
    }
}
