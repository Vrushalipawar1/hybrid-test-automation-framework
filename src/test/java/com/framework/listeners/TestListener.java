package com.framework.listeners;

import com.framework.driver.DriverFactory;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

/** Attaches a screenshot to the Allure report whenever a UI test fails. */
public class TestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        if (!DriverFactory.hasDriver()) {
            return; // API tests have no browser
        }
        try {
            byte[] png = ((TakesScreenshot) DriverFactory.getDriver()).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Screenshot on failure: " + result.getName(),
                    "image/png", new ByteArrayInputStream(png), "png");
        } catch (Exception e) {
            // never let a screenshot problem hide the real test failure
            System.err.println("Could not capture screenshot: " + e.getMessage());
        }
    }
}
