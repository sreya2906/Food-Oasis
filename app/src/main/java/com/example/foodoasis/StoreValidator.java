package com.example.foodoasis;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class StoreValidator {

    private static final String JSON_FILE_PATH = "src/main/res/raw/store_info.json";
    private static final String STORE_TYPE = "Grocery";
    private static final String STORE_STATUS = "open";

    //This method parses the json file to get info for all the stores and stores them in the list of StoreInfo objects
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public List<StoreInfo> fetchAllStores() throws IOException {
        String jsonString = FileUtils.readFileToString(new File(JSON_FILE_PATH), StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString, new TypeReference<List<StoreInfo>>(){});
    }

    //This method verifies the store name passed as a parameter against the list of stores' info and
    // finds if the store is a grocery store and open
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean isGroceryStore(String storeName) throws IOException {
        List<StoreInfo> allStoreInfoList = fetchAllStores();
        for(StoreInfo storeInfo: allStoreInfoList)
        {
            if(storeInfo.store_name.equalsIgnoreCase(storeName)
                    && storeInfo.getStore_type().equalsIgnoreCase(STORE_TYPE)
                    && storeInfo.getStore_status().equalsIgnoreCase(STORE_STATUS))
            {
                return true;
            }
        }
        return false;
    }

}
