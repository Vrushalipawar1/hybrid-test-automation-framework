package com.framework.base;

import com.framework.config.ConfigReader;
import com.framework.driver.DriverFactory;
import com.framework.pages.InventoryPage;
import com.framework.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/** Starts a fresh browser on the login page before every UI test and closes it afterwards. */
public abstract class BaseUiTest {

    protected WebDriver driver;
    protected LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverFactory.initDriver();
        driver = DriverFactory.getDriver();
        driver.get(ConfigReader.get("base.url"));
        loginPage = new LoginPage(driver);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    /** Shortcut for tests that need a logged-in session as their starting point. */
    protected InventoryPage loginAsStandardUser() {
        return loginPage.loginAs(ConfigReader.get("ui.username"), ConfigReader.get("ui.password"));
    }
}
