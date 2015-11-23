package com.feedhenry.helloworld.test.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.feedhenry.oauth.oauth_android_app.FHOAuth;
import com.feedhenry.oauth.oauth_android_app.R;

public class StubFHOAuthActivity extends FHOAuth {

    private CallbackDelegate delegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stub_fhoauth);

    }

    public CallbackDelegate getCallbackDelegate() {
        return delegate;
    }

    public void setCallbackDelegate(CallbackDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onFHReady() {
        delegate.onFHReady();
    }

    @Override
    public void onNotLoggedIn() {
        delegate.onNotLoggedIn();
    }

    @Override
    public void onSessionValid(String sessionToken) {
        delegate.onSessionValid(sessionToken);
    }

    public interface CallbackDelegate {

        void onFHReady();
        void onNotLoggedIn();
        void onSessionValid(String sessionToken);
    }

}
