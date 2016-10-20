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

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.feedhenry.sdk.api.FHAuthSession;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This class setups up the UI of the application
 */
public class OAuthWelcome extends FHOAuth {

    @Bind(R.id.repsonse)
    TextView response;
    @Bind(R.id.progress_bar)
    View progressBar;
    @Bind(R.id.log_in)
    View logInButton;
    @Bind(R.id.log_out)
    View logOutButton;


    private static final String TAG = "FHAuthActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth_welcome);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.log_in)
    public void login() {
        doOAuth();
    }

    @OnClick(R.id.log_out)
    public void logout() {
        try {
            FHAuthSession.clear(false);
            checkSession();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, e.getMessage(), e);
        }

    }


    @Override
    public void onFHReady() {
        try {
            checkSession();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void onNotLoggedIn() {

        if (response == null) { //Testing context doesn't work with butterknife correctly
            response = (TextView) findViewById(R.id.repsonse);
            progressBar = findViewById(R.id.progress_bar);
            logInButton = findViewById(R.id.log_in);
            logOutButton = findViewById(R.id.log_out);
        }

        response.setText(getString(R.string.not_logged_in_message));
        progressBar.setVisibility(View.GONE);
        logInButton.setVisibility(View.VISIBLE);
        logOutButton.setVisibility(View.GONE);
    }

    @Override
    public void onSessionValid(String sessionToken) {
        if (response == null) { //Testing context doesn't work with butterknife correctly
            response = (TextView) findViewById(R.id.repsonse);
            progressBar = findViewById(R.id.progress_bar);
            logInButton = findViewById(R.id.log_in);
            logOutButton = findViewById(R.id.log_out);
        }

        response.setText(String.format(

                getString(R.string.logged_in_message), sessionToken

        ));
        progressBar.setVisibility(View.GONE);
        logInButton.setVisibility(View.GONE);
        logOutButton.setVisibility(View.VISIBLE);
    }
}
