package com.framework.listeners;

import com.framework.config.ConfigReader;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/** Re-runs a failed test up to retry.count times, to absorb occasional network or browser flakiness. */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int attempts = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (attempts < ConfigReader.getInt("retry.count")) {
            attempts++;
            return true;
        }
        return false;
    }
}
