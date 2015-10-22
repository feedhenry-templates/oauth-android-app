# Android OAuth2 Template

This is a demonstration of how a basic OAuth 2 secured app will work.

You will need to setup your cloud application to handle OAuth calls.

## Prerequisites 
 * fh-android-sdk : 3.0.0-SNAPSHOT
 * Android Studio 1.4.0
 * Android SDK 22+

## Build instructions
 * Add fhconfig.properties
 * Edit [FHOauth](app/src/main/java/com/feedhenry/oauth/oauth_android_app/FHOAuth.java) and set "FH_AUTH_POLICY" to your auth policy.
 * Attach running Android Device with API 16+ running
 * ./gradlew installDebug
