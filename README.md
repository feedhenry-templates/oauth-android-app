# Android OAuth2 Template
---------
Author: Summers Pittman (supittma@redhat.com, secondsun@gmail.com)   
Level: Intermediate  
Technologies: Java, Android, RHMAP, OAuth2 Auth Policy  
Summary: A demonstration of how to use Google to authenticate in RHMAP using OAuth2  
Community Project : [Feed Henry](http://feedhenry.org)
Target Product: RHMAP  
Product Versions: RHMAP 3.8.0+   
Source: https://github.com/feedhenry-templates/oauth-android-app  
Prerequisites: fh-android-sdk : 3.0.+, Android Studio : 1.4.0 or newer, Android SDK : 22+ or newer, Google Account

## What is it?

This application demonstrates how to use the FeedHenry Android SDK to connect to an Google secured OAuth2 Auth Policy managed by Studio.  The [FeedHenry docs](http://docs.feedhenry.com/v3/guides/auth_policy_oauth_google.html) detail how to setup the Auth Policy.

If you do not have access to a RHMAP instance, you can sign up for a free instance at [https://openshift.feedhenry.com/](https://openshift.feedhenry.com/).

## How do I run it?  

### RHMAP Studio

This source repository may be imported into any project by using the "Import Existing App" feature when you add a new client app to a project.  It may be imported into any project.

**If you do not name your AuthPolicy "Google" you will have to edit `app/src/main/java/com/feedhenry/oauth/oauth_android_app/FHOAuth.java` and set "FH_AUTH_POLICY" field to your auth policy's name.**

### Local Clone (ideal for Open Source Development)
If you wish to contribute to this template, the following information may be helpful; otherwise, RHMAP and its build facilities are the preferred solution.

###  Prerequisites  
 * fh-android-sdk : 3.0.+
 * Android Studio : 1.4.0 or newer
 * Android SDK : 22+ or newer

## Build instructions
 * Edit `app/src/main/assets/fhconfig.properties` to include the relevant information from RHMAP.  
 * Attach running Android Device with API 16+ running  
 * Edit `app/src/main/java/com/feedhenry/oauth/oauth_android_app/FHOAuth.java` and set "FH_AUTH_POLICY" to your auth policy if you did not name yoru policy "Google".
 * ./gradlew installDebug  
 
## How does it work?

### Checking if a user is logged in

After having called `init()` in `app/src/main/java/com/feedhenry/oauth/oauth_android_app/FHOAuth.java` to initialize the SDK, you may use `FHAuthSession` to check a user's logged in status.  `FHAuthSession.getToken()` will display a user's cached token and `FHAuthSession.verify` can be used to ensure the session is still valid on the server.

```java
//To check if user is already authenticated
boolean exists = FHAuthSession.exists();
if (exists) {
    //user is already authenticated
    //optionally we can also verify the session is actually valid from client. This requires network connection.
    FHAuthSession.verify(new FHAuthSession.Callback() {
        @Override
        public void handleSuccess(final boolean isValid) {
            if (isValid)
                //User is logged in, use FHAuthSession.getToken() to view the token.
            else
              //User is not logged in
        }

        @Override
        public void handleError(FHResponse resp) {
    /* User there was an error verifying the user's session.
                Log the error and perform a log-in as if the user were 
                not logged in.*/
        }
    }, false);
} else {
    //User is not logged in
}
```



### Logging a User In

The following code is taken from `doOAuth()` in `app/src/main/java/com/feedhenry/oauth/oauth_android_app/FHOAuth.java`.

```java
protected void doOAuth() {
  try {
      FHAuthRequest authRequest = FH.buildAuthRequest();
      authRequest.setPresentingActivity(this);/*this is an instance of an Activity*/
      authRequest.setAuthPolicyId(FH_AUTH_POLICY);
      authRequest.executeAsync(new FHActCallback() {

    @Override
    public void success(FHResponse resp) {
        /*  The user is successfully logged in.
                Use FHAuthSession.getToken() to view the token.
                The OAuth details including user account information 
                are in the body of resp.
            */
    }

    @Override
    public void fail(FHResponse resp) {
            /* User failed to log in.*/
    }
      });
  } catch (Exception e) {
       /* there was an exception.  Log the exception an dupdate the UI as appropriate.*/
  }
}
```



