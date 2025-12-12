package utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * Loads config.properties from the classpath (src/test/resources). System
 * properties (-Dkey=value) override file values.
 */
public final class ConfigReader {
    private static final Properties props = new Properties();

    static {
        try (InputStream is = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                props.load(is);
                // helpful runtime debug when tests start
                System.out.println("[ConfigReader] Loaded config.properties from classpath. browser="
                        + props.getProperty("browser"));
            } else {
                System.err.println("[ConfigReader] config.properties NOT FOUND in classpath!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ConfigReader() {
        /* utility */
    }

    private static String getProp(String key) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank())
            return sys;
        String val = props.getProperty(key);
        return (val != null && !val.isBlank()) ? val : null;
    }

    public static String getBaseUrl() {
        String url = getProp("base.url");
        return (url == null) ? "" : url.trim();
    }

    public static String getBrowser() {
        String b = getProp("browser");
        // default to chrome if not present
        return (b == null) ? "chrome" : b.trim().toLowerCase();
    }

    public static int getImplicitWait() {
        String v = getProp("implicit.wait");
        if (v == null)
            return 10;
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            System.err.println("Warning: implicit.wait is not a number. Using default 10s.");
            return 10;
        }
    }

    public static boolean isHeadless() {
        String v = getProp("headless");
        return v != null && (v.equalsIgnoreCase("true") || v.equals("1"));
    }
}
