package com.framework.ui;

import com.framework.base.BaseUiTest;
import com.framework.pages.CheckoutPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Epic("Web Shop")
@Feature("Checkout")
public class CheckoutTests extends BaseUiTest {

    private static final String BACKPACK = "Sauce Labs Backpack";

    @Test(groups = {"smoke", "ui"})
    @Description("End to end: add a product, check out, and see the order confirmation")
    public void completeOrder_showsConfirmation() {
        CheckoutPage checkout = loginAsStandardUser()
                .addToCart(BACKPACK)
                .openCart()
                .checkout()
                .enterDetails("Asha", "Tester", "400001")
                .continueToOverview()
                .finish();

        assertEquals(checkout.confirmationHeader(), "Thank you for your order!");
    }

    @Test(groups = {"ui"})
    @Description("Checkout cannot continue without a first name")
    public void missingFirstName_showsValidationError() {
        CheckoutPage checkout = loginAsStandardUser()
                .addToCart(BACKPACK)
                .openCart()
                .checkout()
                .enterDetails("", "Tester", "400001")
                .continueToOverview();

        assertTrue(checkout.errorMessage().contains("First Name is required"),
                "Unexpected message: " + checkout.errorMessage());
    }
}
