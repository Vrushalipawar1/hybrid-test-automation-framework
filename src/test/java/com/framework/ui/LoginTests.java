package com.framework.ui;

import com.framework.base.BaseUiTest;
import com.framework.config.ConfigReader;
import com.framework.pages.InventoryPage;
import com.framework.utils.ExcelReader;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.openqa.selenium.By;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Epic("Web Shop")
@Feature("Login")
public class LoginTests extends BaseUiTest {

    @DataProvider(name = "invalidLogins")
    public Object[][] invalidLogins() {
        return ExcelReader.readSheet("testdata/login-data.xlsx", "InvalidLogins");
    }

    @Test(groups = {"smoke", "ui"})
    @Description("A valid user lands on the Products page")
    public void validUser_canLogIn() {
        InventoryPage inventory = loginAsStandardUser();

        assertEquals(inventory.title(), "Products");
    }

    @Test(groups = {"ui"})
    @Description("A locked-out user is refused with a clear message")
    public void lockedOutUser_seesLockedOutMessage() {
        loginPage.attemptLogin("locked_out_user", ConfigReader.get("ui.password"));

        assertTrue(loginPage.errorMessage().contains("locked out"),
                "Expected a 'locked out' message but got: " + loginPage.errorMessage());
    }

    @Test(dataProvider = "invalidLogins", groups = {"ui"})
    @Description("Invalid or missing credentials show the right validation message (data from Excel)")
    public void invalidCredentials_showExpectedError(String username, String password, String expectedError) {
        loginPage.attemptLogin(username, password);

        assertTrue(loginPage.errorMessage().contains(expectedError),
                "Expected '" + expectedError + "' but got: " + loginPage.errorMessage());
    }

    @Test(groups = {"ui"})
    @Description("Logging out returns the user to the login page")
    public void loggedInUser_canLogOut() {
        InventoryPage inventory = loginAsStandardUser();

        assertTrue(inventory.logout().isLoginButtonDisplayed());
    }

    @Test(groups = {"ui"})
    @Description("Opening a protected page without logging in is blocked")
    public void protectedPage_withoutLogin_isBlocked() {
        driver.get(ConfigReader.get("base.url") + "/inventory.html");

        assertTrue(loginPage.errorMessage().contains("when you are logged in"),
                "Expected an access-denied message but got: " + loginPage.errorMessage());
        assertTrue(driver.findElements(By.cssSelector("[data-test='inventory-item']")).isEmpty());
    }
}
