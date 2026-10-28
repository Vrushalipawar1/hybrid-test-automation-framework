package com.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class InventoryPage extends BasePage {

    private static final By TITLE = By.cssSelector("[data-test='title']");
    private static final By ITEMS = By.cssSelector("[data-test='inventory-item']");
    private static final By ITEM_NAMES = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ITEM_PRICES = By.cssSelector("[data-test='inventory-item-price']");
    private static final By SORT_DROPDOWN = By.cssSelector("[data-test='product-sort-container']");
    private static final By CART_LINK = By.cssSelector("[data-test='shopping-cart-link']");
    private static final By CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");
    private static final By MENU_BUTTON = By.id("react-burger-menu-btn");
    private static final By LOGOUT_LINK = By.id("logout_sidebar_link");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public String title() {
        return textOf(TITLE);
    }

    public int productCount() {
        visible(ITEMS); // wait for the list to render
        return all(ITEMS).size();
    }

    public List<String> productNames() {
        visible(ITEM_NAMES);
        return all(ITEM_NAMES).stream().map(WebElement::getText).toList();
    }

    public List<Double> productPrices() {
        visible(ITEM_PRICES);
        return all(ITEM_PRICES).stream()
                .map(e -> Double.parseDouble(e.getText().replace("$", "").trim()))
                .toList();
    }

    public InventoryPage sortBy(String visibleText) {
        new Select(visible(SORT_DROPDOWN)).selectByVisibleText(visibleText);
        return this;
    }

    public InventoryPage addToCart(String productName) {
        click(By.cssSelector("[data-test='add-to-cart-" + slug(productName) + "']"));
        return this;
    }

    public InventoryPage removeFromCart(String productName) {
        click(By.cssSelector("[data-test='remove-" + slug(productName) + "']"));
        return this;
    }

    /** Number shown on the cart badge; 0 when the badge is not displayed (empty cart). */
    public int cartBadgeCount() {
        return isPresent(CART_BADGE) ? Integer.parseInt(textOf(CART_BADGE)) : 0;
    }

    public CartPage openCart() {
        click(CART_LINK);
        return new CartPage(driver);
    }

    public LoginPage logout() {
        click(MENU_BUTTON);
        click(LOGOUT_LINK);
        return new LoginPage(driver);
    }
}
