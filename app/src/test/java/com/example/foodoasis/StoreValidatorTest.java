package com.example.foodoasis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.List;

@RunWith(JUnit4.class)
public class StoreValidatorTest {

    @Test
    public void testFetchAllStores() throws IOException {
        StoreValidator storeValidator = new StoreValidator();
        List<StoreInfo> storeInfoList = storeValidator.fetchAllStores();
        assertEquals(4, storeInfoList.size());
    }

    @Test
    public void testIsGroceryStore() throws IOException {
        StoreValidator storeValidator = new StoreValidator();
        assertTrue(storeValidator.isGroceryStore("Food1"));
        assertFalse(storeValidator.isGroceryStore("Food2"));
        assertTrue(storeValidator.isGroceryStore("Food3"));
        assertFalse(storeValidator.isGroceryStore("Food4"));
    }
}
