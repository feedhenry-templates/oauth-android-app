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
package com.feedhenry.helloworld.test.activity;

import android.os.Bundle;

import com.feedhenry.oauth.FHOAuth;
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
    public void checkSession() throws Exception {
        super.checkSession();
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
