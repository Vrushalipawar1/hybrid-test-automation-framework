package com.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final By USERNAME = By.cssSelector("[data-test='username']");
    private static final By PASSWORD = By.cssSelector("[data-test='password']");
    private static final By LOGIN_BUTTON = By.cssSelector("[data-test='login-button']");
    private static final By ERROR = By.cssSelector("[data-test='error']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    /** Fills the form and clicks Login without assuming it succeeds (use for negative tests). */
    public LoginPage attemptLogin(String username, String password) {
        type(USERNAME, username);
        type(PASSWORD, password);
        click(LOGIN_BUTTON);
        return this;
    }

    /** Logs in and returns the next page (use when login is expected to succeed). */
    public InventoryPage loginAs(String username, String password) {
        attemptLogin(username, password);
        return new InventoryPage(driver);
    }

    public String errorMessage() {
        return textOf(ERROR);
    }

    public boolean isLoginButtonDisplayed() {
        return visible(LOGIN_BUTTON).isDisplayed();
    }
}
