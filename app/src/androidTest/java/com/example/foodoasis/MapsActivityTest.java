package com.example.foodoasis;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import android.view.View;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MapsActivityTest {

    @Rule
    public ActivityTestRule<MapsActivity> mActivityTestRule = new ActivityTestRule<>(MapsActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION");

    @Test
    public void initialUiStateTest() {
        ViewInteraction button = onView(
                allOf(withId(R.id.nearCurrentButton), withText("SEARCH NEAR YOUR LOCATION"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction button2 = onView(
                allOf(withId(R.id.nearInputButton), withText("SEARCH"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));
        button2.check(matches(isNotEnabled()));

        ViewInteraction button4 = onView(
                allOf(withId(R.id.addToFavoritesButton), withText("ADD TO FAVORITES"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        button4.check(matches(isDisplayed()));
        button4.check(matches(isNotEnabled()));

        ViewInteraction button6 = onView(
                allOf(withId(R.id.showFavoriteButton), withText("FAVORITES"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        button6.check(matches(isDisplayed()));
    }

    @Test
    public void mapTest() {
        assertNotNull(mActivityTestRule.getActivity().googleMap);
    }

    @Test
    public void searchTest() throws IOException {
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=29.7604267,-95.3698028&keyword=grocery_store&maxprice=3&radius=10000&key=AIzaSyAEjrtmyNsg7Y5KLtmYV_FDGqZLi0Qw-Pk";
        String result = mActivityTestRule.getActivity().downloadUrl(url);
        assertNotEquals("", result);
    }
}
