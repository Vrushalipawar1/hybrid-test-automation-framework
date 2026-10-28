package com.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutPage extends BasePage {

    private static final By FIRST_NAME = By.cssSelector("[data-test='firstName']");
    private static final By LAST_NAME = By.cssSelector("[data-test='lastName']");
    private static final By POSTAL_CODE = By.cssSelector("[data-test='postalCode']");
    private static final By CONTINUE = By.cssSelector("[data-test='continue']");
    private static final By FINISH = By.cssSelector("[data-test='finish']");
    private static final By ERROR = By.cssSelector("[data-test='error']");
    private static final By COMPLETE_HEADER = By.cssSelector("[data-test='complete-header']");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public CheckoutPage enterDetails(String firstName, String lastName, String postalCode) {
        type(FIRST_NAME, firstName);
        type(LAST_NAME, lastName);
        type(POSTAL_CODE, postalCode);
        return this;
    }

    public CheckoutPage continueToOverview() {
        click(CONTINUE);
        return this;
    }

    public CheckoutPage finish() {
        click(FINISH);
        return this;
    }

    public String errorMessage() {
        return textOf(ERROR);
    }

    public String confirmationHeader() {
        return textOf(COMPLETE_HEADER);
    }
}
