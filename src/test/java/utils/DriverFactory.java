package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.edge.EdgeDriver;

import java.time.Duration;

/**
 * Thread-safe WebDriver factory suitable for parallel tests (ThreadLocal).
 */
public final class DriverFactory {
	private static final ThreadLocal<WebDriver> TL_DRIVER = new ThreadLocal<>();

	private DriverFactory() {
		/* util */ }

	public static WebDriver getDriver() {
		return TL_DRIVER.get();
	}

	public static WebDriver getDriverOrThrow() {
		WebDriver d = getDriver();
		if (d == null) {
			throw new IllegalStateException(
					"WebDriver is not initialized for current thread. Call initDriver(browser) first.");
		}
		return d;
	}

	public static void setDriver(WebDriver driver) {
		TL_DRIVER.set(driver);
	}

	public static void initDriver(String browserRaw) {
		String browser = (browserRaw == null || browserRaw.isBlank()) ? ConfigReader.getBrowser()
				: browserRaw.trim().toLowerCase();
		WebDriver driver;

		switch (browser) {
		case "firefox":
		case "ff":
			WebDriverManager.firefoxdriver().setup();
			FirefoxOptions ffOptions = new FirefoxOptions();
			ffOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
			if (ConfigReader.isHeadless())
				ffOptions.addArguments("--headless=new");
			driver = new FirefoxDriver(ffOptions);
			break;

		case "edge":
			WebDriverManager.edgedriver().setup();
			EdgeOptions edgeOptions = new EdgeOptions();
			edgeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
			if (ConfigReader.isHeadless())
				edgeOptions.addArguments("--headless=new");
			driver = new EdgeDriver(edgeOptions);
			break;

		case "chrome":
		default:
			WebDriverManager.chromedriver().setup();
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
			// keep remote allow origins for certain ChromeDriver/Chrome combos
			chromeOptions.addArguments("--remote-allow-origins=*");
			if (ConfigReader.isHeadless())
				chromeOptions.addArguments("--headless=new");
			// add other useful args if needed:
			chromeOptions.addArguments("--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
			driver = new ChromeDriver(chromeOptions);
			break;
		}

		// timeouts
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
		// optional page load timeout
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

		setDriver(driver);
	}

	public static void quitDriver() {
		WebDriver d = getDriver();
		if (d != null) {
			try {
				d.quit();
			} catch (Exception e) {
				// log and continue
				System.err.println("Warning: error while quitting WebDriver: " + e.getMessage());
			} finally {
				TL_DRIVER.remove();
			}
		}
	}
}
