package com.framework.ui;

import com.framework.base.BaseUiTest;
import com.framework.pages.InventoryPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;

@Epic("Web Shop")
@Feature("Product catalogue")
public class InventoryTests extends BaseUiTest {

    @Test(groups = {"smoke", "ui"})
    @Description("The catalogue shows all six products")
    public void catalogue_showsSixProducts() {
        InventoryPage inventory = loginAsStandardUser();

        assertEquals(inventory.productCount(), 6);
    }

    @Test(groups = {"ui"})
    @Description("Sorting by price low to high puts the cheapest first")
    public void sortByPrice_lowToHigh() {
        InventoryPage inventory = loginAsStandardUser().sortBy("Price (low to high)");

        List<Double> actual = inventory.productPrices();
        List<Double> expected = new ArrayList<>(actual);
        Collections.sort(expected);
        assertEquals(actual, expected);
    }

    @Test(groups = {"ui"})
    @Description("Sorting by price high to low puts the most expensive first")
    public void sortByPrice_highToLow() {
        InventoryPage inventory = loginAsStandardUser().sortBy("Price (high to low)");

        List<Double> actual = inventory.productPrices();
        List<Double> expected = new ArrayList<>(actual);
        expected.sort(Collections.reverseOrder());
        assertEquals(actual, expected);
    }

    @Test(groups = {"ui"})
    @Description("Sorting by name Z to A reverses the alphabetical order")
    public void sortByName_zToA() {
        InventoryPage inventory = loginAsStandardUser().sortBy("Name (Z to A)");

        List<String> actual = inventory.productNames();
        List<String> expected = new ArrayList<>(actual);
        expected.sort(Collections.reverseOrder());
        assertEquals(actual, expected);
    }
}
