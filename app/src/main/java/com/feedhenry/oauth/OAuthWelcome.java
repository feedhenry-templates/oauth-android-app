/**
 * Copyright 2015 Red Hat, Inc., and individual contributors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.feedhenry.oauth;

import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.feedhenry.sdk.api.*;

import butterknife.*;

/**
 * This class setups up the UI of the application
 */
public class OAuthWelcome extends FHOAuth {

    @BindView(R.id.repsonse)
    TextView response;
    @BindView(R.id.progress_bar)
    View progressBar;
    @BindView(R.id.log_in)
    View logInButton;
    @BindView(R.id.log_out)
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
