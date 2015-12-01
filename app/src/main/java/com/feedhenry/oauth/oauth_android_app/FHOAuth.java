/**
 * Copyright 2015 Red Hat, Inc., and individual contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.feedhenry.oauth.oauth_android_app;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.feedhenry.sdk.FH;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;
import com.feedhenry.sdk.api.FHAuthRequest;
import com.feedhenry.sdk.api.FHAuthSession;

/**
 * This abstract class contains all of the important FHOAuth code.
 */
public abstract class FHOAuth extends AppCompatActivity {

    private static final String TAG = "FHAuthActivity";
    private static final String FH_AUTH_POLICY = "Google";  //"Google" should be replaced with policy id you created;

    @Override
    public void onStart() {
        super.onStart();
        init();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FH.stop();
    }

    private void init() {
        FH.init(this, new FHActCallback() {
            @Override
            public void success(FHResponse pResponse) {
                Log.d(TAG, "FH.init - success");
                try {
                    onFHReady();
                } catch (Exception e) {
                    Toast.makeText(FHOAuth.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            @Override
            public void fail(FHResponse pResponse) {
                Toast.makeText(FHOAuth.this, "Init failed", Toast.LENGTH_LONG).show();
                Log.d(TAG, "FH.init - fail");
                Log.e(TAG, pResponse.getErrorMessage(), pResponse.getError());
            }
        });

    }

    protected void checkSession() throws Exception {

        //To check if user is already authe
        // nticated
        boolean exists = FHAuthSession.exists();
        if (exists) {
            //user is already authenticated
            //optionally we can also verify the session is actually valid from client. This requires network connection.
            FHAuthSession.verify(new FHAuthSession.Callback() {
                @Override
                public void handleSuccess(final boolean isValid) {
                    if (isValid)
                        onSessionValid(FHAuthSession.getToken());
                    else
                        onNotLoggedIn();
                }

                @Override
                public void handleError(FHResponse resp) {
                    Log.d(TAG, resp.getErrorMessage());
                    Toast.makeText(FHOAuth.this, "Error validating session", Toast.LENGTH_LONG).show();
                    onNotLoggedIn();
                }
            }, false);
        } else {
            onNotLoggedIn();
        }

    }

    protected void doOAuth() {
        try {
            FHAuthRequest authRequest = FH.buildAuthRequest();
            authRequest.setPresentingActivity(this);
            authRequest.setAuthPolicyId(FH_AUTH_POLICY);
            authRequest.executeAsync(new FHActCallback() {

                @Override
                public void success(FHResponse resp) {
                    onSessionValid(FHAuthSession.getToken());
                }

                @Override
                public void fail(FHResponse resp) {
                    Toast.makeText(FHOAuth.this, "Log in failed", Toast.LENGTH_LONG).show();
                    Log.d(TAG, resp.getErrorMessage());
                    onNotLoggedIn();
                }
            });
        } catch (Exception e) {
            Toast.makeText(FHOAuth.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, e.getMessage(), e);
            onNotLoggedIn();
        }
    }


    /**
     * Called when Feed Henry is ready to be used.
     */
    public abstract void onFHReady();

    /**
     * This is called after FH has determined the user is not logged in.
     */
    public abstract void onNotLoggedIn();

    /**
     * The user is logged in and has a valid session
     *
     * @param sessionToken this is the token for the current session.  FH will append it to all requests automatically
     */
    protected abstract void onSessionValid(String sessionToken);


}
