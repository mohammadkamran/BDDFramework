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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Thread-safe WebDriver factory suitable for parallel tests (ThreadLocal).
 * - Uses WebDriverManager to provision drivers
 * - Respects ConfigReader (browser, headless, implicit wait)
 * - Defensive: cleans partial drivers on error and never sets null into ThreadLocal
 */
public final class DriverFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> TL_DRIVER = new ThreadLocal<>();

    private DriverFactory() { /* util */ }

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
        if (driver == null) {
            LOG.warn("Attempt to set null WebDriver in ThreadLocal — ignoring.");
            return;
        }
        TL_DRIVER.set(driver);
    }

    /**
     * Initialize WebDriver for a given browser.
     * Resolution order: browserRaw (param) -> System property "browser" -> config.properties
     *
     * @param browserRaw optional browser name (e.g., "chrome", "firefox", "edge")
     */
    public static void initDriver(String browserRaw) {
        // resolve browser: param -> system prop -> config
        String browser = (browserRaw == null || browserRaw.isBlank()) ? ConfigReader.getBrowser()
                : browserRaw.trim().toLowerCase();
        LOG.info("DriverFactory.initDriver -> resolved browser: {}", browser);

        WebDriver driver = null;
        try {
            switch (browser) {
                case "firefox":
                case "ff":
                    WebDriverManager.firefoxdriver().setup();
                    FirefoxOptions ffOptions = new FirefoxOptions();
                    ffOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);

                    if (ConfigReader.isHeadless()) {
                        // use headless arg (setHeadless may be unavailable depending on Selenium version)
                        ffOptions.addArguments("-headless");
                        LOG.debug("Firefox configured for headless via -headless");
                    }
                    driver = new FirefoxDriver(ffOptions);
                    break;

                case "edge":
                    WebDriverManager.edgedriver().setup();
                    EdgeOptions edgeOptions = new EdgeOptions();
                    edgeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
                    if (ConfigReader.isHeadless()) {
                        // Edge is chromium-based; use headless arg
                        edgeOptions.addArguments("--headless=new");
                        LOG.debug("Edge configured for headless via --headless=new");
                    }
                    driver = new EdgeDriver(edgeOptions);
                    break;

                case "chrome":
                default:
                    // Optionally pin chromedriver version via system property "chromedriver.version"
                    String pinned = System.getProperty("chromedriver.version");
                    if (pinned != null && !pinned.isBlank()) {
                        WebDriverManager.chromedriver().driverVersion(pinned).setup();
                        LOG.info("Using pinned chromedriver version: {}", pinned);
                    } else {
                        WebDriverManager.chromedriver().setup();
                    }

                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
                    chromeOptions.addArguments("--remote-allow-origins=*"); // compatibility
                    if (ConfigReader.isHeadless()) {
                        // modern Chrome prefers --headless=new
                        chromeOptions.addArguments("--headless=new");
                        LOG.debug("Chrome configured for headless via --headless=new");
                    }
                    chromeOptions.addArguments("--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
                    driver = new ChromeDriver(chromeOptions);
                    break;
            }

            // Defensive: ensure driver was actually created
            if (driver == null) {
                throw new IllegalStateException("WebDriver instance was not created for browser: " + browser);
            }

            // timeouts & window — guarded so we can cleanup gracefully if fail
            try {
                driver.manage().window().maximize();
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
                driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
            } catch (Exception e) {
                LOG.error("Failed to configure WebDriver timeouts/window. Will cleanup driver.", e);
                try { driver.quit(); } catch (Exception ignored) {}
                throw e;
            }

            setDriver(driver);
            LOG.info("Initialized WebDriver for browser: {}", browser);
        } catch (Exception e) {
            LOG.error("Failed to initialize WebDriver for browser: {}", browser, e);
            if (driver != null) {
                try { driver.quit(); } catch (Exception ignored) {}
            }
            TL_DRIVER.remove();
            throw new RuntimeException("Failed to initialize WebDriver for: " + browser, e);
        }
    }

    public static void quitDriver() {
        WebDriver d = getDriver();
        if (d != null) {
            try {
                d.quit();
            } catch (Exception e) {
                LOG.warn("Error while quitting WebDriver", e);
            } finally {
                TL_DRIVER.remove();
            }
        }
    }

    static {
        // best-effort cleanup on JVM exit (helps local runs)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            WebDriver d = TL_DRIVER.get();
            if (d != null) {
                try { d.quit(); } catch (Exception ignored) {}
                TL_DRIVER.remove();
            }
        }));
    }
}
