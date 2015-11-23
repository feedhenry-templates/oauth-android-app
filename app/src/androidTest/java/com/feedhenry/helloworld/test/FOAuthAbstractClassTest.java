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
package com.feedhenry.helloworld.test;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.test.ActivityUnitTestCase;
import android.view.ContextThemeWrapper;


import com.feedhenry.helloworld.test.activity.StubFHOAuthActivity;
import com.feedhenry.oauth.oauth_android_app.R;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.Assert;

import java.io.IOException;

public class FOAuthAbstractClassTest extends ActivityUnitTestCase<StubFHOAuthActivity> {



    private MockWebServer mockWebServer = null;
    private long startTime;
    public FOAuthAbstractClassTest() {
        super(StubFHOAuthActivity.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        mockWebServer = new MockWebServer();
        mockWebServer.start(9000);
        ContextThemeWrapper context = new AlternateAssetsContextWrapper(getInstrumentation().getTargetContext(), R.style.AppTheme, getInstrumentation().getContext());
        setActivityContext(context);
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        try{
            mockWebServer.shutdown();
        } catch (Exception ignore) {

        }
        Thread.sleep(5000);
    }

    public void testActivityCallsFHInitOnStartup() throws IOException {

        final Delegate delegate = new Delegate();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                StubFHOAuthActivity activity = startActivity(new Intent(), Bundle.EMPTY, null);
                activity.setCallbackDelegate(delegate);
                activity.onStart();
            }
        });


        StubFHOAuthActivity main = getActivity();

        while (!delegate.onFHReadyCalled) {
            assertTrue("Timeout after 5 seconds", System.currentTimeMillis() - startTime < 5000);
        }

        main.finish();

    }


    public void testLoginButtonShowsIfNotLoggedIn() throws IOException {

        final Delegate delegate = new Delegate();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                StubFHOAuthActivity activity = startActivity(new Intent(), Bundle.EMPTY, null);
                activity.setCallbackDelegate(delegate);
                activity.onStart();
            }
        });


        StubFHOAuthActivity main = getActivity();

        while (!delegate.onFHReadyCalled) {
            assertTrue("Timeout after 5 seconds", System.currentTimeMillis() - startTime < 5000);
        }

        assertTrue(delegate.onNotLoggedInCalled);

        main.finish();
    }


    private static class Delegate implements  StubFHOAuthActivity.CallbackDelegate {

        boolean onFHReadyCalled = false;
        boolean onNotLoggedInCalled  = false;
        boolean onSessionValidCalled  = false;


        @Override
        public void onFHReady() {
            onFHReadyCalled = true;
        }

        @Override
        public void onNotLoggedIn() {
            onNotLoggedInCalled = true;
        }

        @Override
        public void onSessionValid(String sessionToken) {
            onSessionValidCalled = true;
        }
    }

}
