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
import android.test.ActivityUnitTestCase;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.feedhenry.oauth.oauth_android_app.OAuthWelcome;
import com.feedhenry.oauth.oauth_android_app.R;
import com.feedhenry.sdk.utils.DataManager;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class OAuthWelcomeTest extends ActivityUnitTestCase<OAuthWelcome> {



    private MockWebServer mockWebServer = null;
    private long startTime;
    public OAuthWelcomeTest() {
        super(OAuthWelcome.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        mockWebServer = new MockWebServer();
        mockWebServer.start(9000);
        ContextThemeWrapper context = new AlternateAssetsContextWrapper(getInstrumentation().getTargetContext(), R.style.AppTheme, getInstrumentation().getContext());
        setActivityContext(context);
        startTime = System.currentTimeMillis();
        DataManager.init(context);
        DataManager.getInstance().remove("sessionToken");
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

    public void testLoginButtonShowsIfNotLoggedIn() throws IOException {

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                OAuthWelcome activity = startActivity(new Intent(), Bundle.EMPTY, null);

                activity.onStart();
            }
        });


        OAuthWelcome main = getActivity();

        while (main.findViewById(R.id.log_in).getVisibility() == View.GONE) {
            assertTrue("Timeout after 5 seconds", System.currentTimeMillis() - startTime < 5000);
        }

        main.finish();
    }




    public void testLogoutButtonShowsIfLoggedIn() throws IOException, InterruptedException {

        DataManager.getInstance().save("sessionToken", "testToken");
        //Code will call verify which should call the server
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\"isValid\":\"true\"}"));

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                OAuthWelcome activity = startActivity(new Intent(), Bundle.EMPTY, null);

                activity.onStart();
            }
        });


        OAuthWelcome main = getActivity();

        while (main.findViewById(R.id.log_out).getVisibility() == View.GONE) {
            assertTrue("Timeout after 5 seconds", System.currentTimeMillis() - startTime < 5000);
        }

        RecordedRequest request = mockWebServer.takeRequest(5, TimeUnit.SECONDS);
        assertEquals("/box/srv/1.1/verifysession", request.getPath());
        assertEquals("{\"sessionToken\":\"testToken\"}", request.getBody().readString(Charset.forName("UTF-8")));


        main.finish();
    }


}
