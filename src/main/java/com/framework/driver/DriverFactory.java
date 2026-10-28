package com.framework.driver;

import com.framework.config.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import java.util.Map;

/**
 * Creates one WebDriver per thread (ThreadLocal) so tests can run in parallel without
 * sharing a browser. Supports local Chrome/Firefox or a remote Selenium (e.g. Docker).
 */
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static void initDriver() {
        String browser = ConfigReader.get("browser").toLowerCase();
        boolean remote = "remote".equalsIgnoreCase(ConfigReader.get("execution"));
        boolean headless = ConfigReader.getBoolean("headless");

        WebDriver driver = switch (browser) {
            case "chrome" -> remote ? remote(chromeOptions(headless)) : new ChromeDriver(chromeOptions(headless));
            case "firefox" -> remote ? remote(firefoxOptions(headless)) : new FirefoxDriver(firefoxOptions(headless));
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };

        driver.manage().timeouts().implicitlyWait(Duration.ZERO); // explicit waits only
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getInt("page.load.timeout.seconds")));
        if (!headless) {
            driver.manage().window().maximize();
        }
        DRIVER.set(driver);
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver not initialised for this thread; call initDriver() first");
        }
        return driver;
    }

    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }

    private static ChromeOptions chromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--disable-notifications");
        // Stops Chrome's "change your password" pop-up from covering the page after login
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false));
        return options;
    }

    private static FirefoxOptions firefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        return options;
    }

    private static WebDriver remote(org.openqa.selenium.Capabilities capabilities) {
        try {
            return new RemoteWebDriver(URI.create(ConfigReader.get("remote.url")).toURL(), capabilities);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid remote.url: " + ConfigReader.get("remote.url"), e);
        }
    }
}
