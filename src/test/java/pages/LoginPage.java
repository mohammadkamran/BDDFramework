package pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage {
    private final WebDriver driver;
    private final By username = By.id("user-name");
    private final By password = By.id("password");
    private final By loginBtn = By.id("login-button");
    private final By errorMessage = By.cssSelector("h3[data-test='error']");
    private final By inventoryContainer = By.id("inventory_container"); // element on inventory page

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void open(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Base URL is null or blank. Check config.");
        }
        driver.get(url);
    }

    public void enterUsername(String user) {
        driver.findElement(username).clear();
        driver.findElement(username).sendKeys(user);
    }

    public void enterPassword(String pass) {
        driver.findElement(password).clear();
        driver.findElement(password).sendKeys(pass);
    }

    public void clickLogin() {
        driver.findElement(loginBtn).click();
    }

    public String getErrorMessage() {
        try {
            return driver.findElement(errorMessage).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isOnInventoryPage() {
        try {
            return driver.getCurrentUrl().contains("/inventory.html") || driver.findElement(inventoryContainer).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // expose inventory element for explicit waits
    public WebElement getInventoryContainerElement() {
        try {
            return driver.findElement(inventoryContainer);
        } catch (Exception e) {
            return null;
        }
    }
}
