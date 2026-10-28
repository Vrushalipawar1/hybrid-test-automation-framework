package com.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CartPage extends BasePage {

    private static final By TITLE = By.cssSelector("[data-test='title']");
    private static final By ITEM_NAMES = By.cssSelector("[data-test='inventory-item-name']");
    private static final By CHECKOUT_BUTTON = By.cssSelector("[data-test='checkout']");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public String title() {
        return textOf(TITLE);
    }

    public List<String> itemNames() {
        visible(TITLE); // page loaded; the list itself may legitimately be empty
        return all(ITEM_NAMES).stream().map(WebElement::getText).toList();
    }

    public CheckoutPage checkout() {
        click(CHECKOUT_BUTTON);
        return new CheckoutPage(driver);
    }
}
