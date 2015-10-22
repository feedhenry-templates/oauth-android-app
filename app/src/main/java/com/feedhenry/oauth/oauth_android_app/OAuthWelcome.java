package com.feedhenry.oauth.oauth_android_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.feedhenry.sdk.FH;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHHttpClient;
import com.feedhenry.sdk.FHResponse;
import com.feedhenry.sdk.api.FHAuthRequest;
import com.feedhenry.sdk.api.FHAuthSession;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This class setups up the UI of the application
 */
public class OAuthWelcome extends FHOAuth {

    @Bind(R.id.repsonse) TextView response;
    @Bind(R.id.progress_bar) View progressBar;
    @Bind(R.id.log_in) View logInButton;
    @Bind(R.id.log_out) View logOutButton;


    private static final String TAG = "FHAuthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    void onFHReady() {
        try {
            checkSession();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    void onNotLoggedIn() {
        response.setText("You are not logged in");
        progressBar.setVisibility(View.GONE);
        logInButton.setVisibility(View.VISIBLE);
        logOutButton.setVisibility(View.GONE);
    }

    @Override
    void onSessionValid(String sessionToken) {
        response.setText("You are logged in : " + sessionToken);
        progressBar.setVisibility(View.GONE);
        logInButton.setVisibility(View.GONE);
        logOutButton.setVisibility(View.VISIBLE);
    }
}
