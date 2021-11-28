[![Build Status](https://app.travis-ci.com/KingCyrus20/Food-Oasis.svg?branch=master)](https://app.travis-ci.com/KingCyrus20/Food-Oasis)

Food Oasis is an Android app that lets users search for healthy food options, such as grocery stores or farmers' markets, in their vicinity. Users can also save locations as "favorites" and get notified weekly with any weekly ads published by these locations.

# Sprint 1 Features

## Map View

After the app is launched and it transitions away from the splash screen, the user sees a Google Map, which can be navigated the same way as in the Google Maps app.

## User Location

Upon first launch of Food Oasis, the user is asked to grant the app permission to access and use their precise location. If granted, the user's location appears as a blue dot on the map, and the map can be recentered on this location using a button at the top right.

## Search Near User

If location permissions were not granted, this feature is disabled. Tapping the "Search Near Your Location" button will search for grocery stores near the user's location, and display a map marker at each of these stores. Tapping a marker will show the name of the store.

# Sprint 2 Features

## Search Near Other Location

The app provides an autocomplete box for users to input any location. Tapping the "Search" button after selecting a location will search for grocery stores near that location, and results are displayed similarly to a search near user.

## Add to Favorites

Grocery stores found during a search can be added to a favorites list. After performing a search, clicking on a map marker will enable the "Add to Favorites" button. Tapping this button will add the selected location to a list of favorites.

## Favorites List View

Tapping the "Favorites" button will show the user's list of favorites that were added using the Add to Favorites feature. This list includes the name, website, and phone number of each favorite store. In addition, users can remove stores from the favorites list by tapping the "X" button on their card in the list. To return to the map view, use the phone's back button.
