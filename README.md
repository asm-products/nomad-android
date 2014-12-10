# Nomad

## Find the best places to work remotely

This is a product being built by the Assembly community. You can help push this idea forward by visiting [https://assembly.com/nomad](https://assembly.com/nomad).

## Setup

1. Sign up for a Google Places API Key by following these instructions:
[https://developers.google.com/places/documentation/#Authentication](https://developers.google.com/places/documentation/#Authentication)

Copy `app/src/main/res/values/api_keys.xml-sample` to `app/src/main/res/values/api_keys.xml` and copy your API key to `google_places_api_key` and uncomment the line.

## Building

Requires Java 8

Ensure you have installed the latest versions of "Android Support Library", "Android Support Repository", "Google Play Services" and "Google Repository" from the Android SDK Manager.

### Android Studio

Currently requires Android Studio 1.0 or newer.

Clone the sourcecode.

File > Import Project... > Select "/nomad-android/settings.gradle"

Click the green Run button

### Command Line

#### OS X/Linux

Clone the sourcecode.

    cd nomad-android
    ./gradlew assembleRelease

### How Assembly Works

Assembly products are like open-source and made with contributions from the community. Assembly handles the boring stuff like hosting, support, financing, legal, etc. Once the product launches we collect the revenue and split the profits amongst the contributors.

Visit [https://assembly.com](https://assembly.com) to learn more.
