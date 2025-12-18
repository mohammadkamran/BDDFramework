package utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Simple reusable wait utilities used across page objects.
 */
public final class WaitUtils {
    private WaitUtils() {}

    public static void waitForDocumentComplete(WebDriver driver, int seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        try {
            wait.until((ExpectedCondition<Boolean>) d -> {
                try {
                    Object state = ((JavascriptExecutor) d).executeScript("return document.readyState");
                    return state != null && "complete".equals(state.toString());
                } catch (Exception e) {
                    return false;
                }
            });
        } catch (Exception e) {
            System.err.println("Warning: waitForDocumentComplete timed out: " + e.getMessage());
        }
    }

    public static WebElement waitForVisibility(WebDriver driver, WebElement element, int seconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
            return wait.until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            return null;
        }
    }

    public static void waitForVisibilityByLocator(WebDriver driver, org.openqa.selenium.By locator, int seconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (Exception e) {
            System.err.println("Warning: waitForVisibilityByLocator timed out for: " + locator + ", " + e.getMessage());
        }
    }
}