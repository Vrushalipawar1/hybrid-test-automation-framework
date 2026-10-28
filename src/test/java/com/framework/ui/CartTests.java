package com.framework.ui;

import com.framework.base.BaseUiTest;
import com.framework.pages.CartPage;
import com.framework.pages.InventoryPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

@Epic("Web Shop")
@Feature("Shopping cart")
public class CartTests extends BaseUiTest {

    private static final String BACKPACK = "Sauce Labs Backpack";
    private static final String BIKE_LIGHT = "Sauce Labs Bike Light";

    @Test(groups = {"smoke", "ui"})
    @Description("Adding one product sets the cart badge to 1")
    public void addOneItem_badgeShowsOne() {
        InventoryPage inventory = loginAsStandardUser().addToCart(BACKPACK);

        assertEquals(inventory.cartBadgeCount(), 1);
    }

    @Test(groups = {"ui"})
    @Description("Adding two products sets the cart badge to 2")
    public void addTwoItems_badgeShowsTwo() {
        InventoryPage inventory = loginAsStandardUser().addToCart(BACKPACK).addToCart(BIKE_LIGHT);

        assertEquals(inventory.cartBadgeCount(), 2);
    }

    @Test(groups = {"ui"})
    @Description("Removing the only product clears the cart badge")
    public void removeItem_badgeDisappears() {
        InventoryPage inventory = loginAsStandardUser().addToCart(BACKPACK).removeFromCart(BACKPACK);

        assertEquals(inventory.cartBadgeCount(), 0);
    }

    @Test(groups = {"ui"})
    @Description("The cart page lists exactly the products that were added")
    public void cartPage_listsAddedItems() {
        CartPage cart = loginAsStandardUser().addToCart(BACKPACK).addToCart(BIKE_LIGHT).openCart();

        assertEquals(cart.itemNames(), List.of(BACKPACK, BIKE_LIGHT));
    }
}
