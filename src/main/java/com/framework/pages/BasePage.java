package com.framework.pages;

import com.framework.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/** Common actions with explicit waits, shared by every page object. */
public abstract class BasePage {

    protected final WebDriver driver;
    private final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicit.wait.seconds")));
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String text) {
        WebElement element = visible(locator);
        element.clear();
        if (text != null && !text.isEmpty()) {
            element.sendKeys(text);
        }
    }

    protected String textOf(By locator) {
        return visible(locator).getText().trim();
    }

    protected List<WebElement> all(By locator) {
        return driver.findElements(locator);
    }

    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    /** Turns "Sauce Labs Backpack" into "sauce-labs-backpack", the suffix SauceDemo uses in data-test ids. */
    protected static String slug(String productName) {
        return productName.toLowerCase().replace(" ", "-");
    }
}
