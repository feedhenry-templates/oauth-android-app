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

import android.content.ContextWrapper;
import android.content.Intent;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.feedhenry.oauth.OAuthWelcome;
import com.feedhenry.oauth.oauth_android_app.R;
import com.feedhenry.sdk.FH;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;
import com.feedhenry.sdk.utils.DataManager;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class OAuthWelcomeTest {

    private MockWebServer mockWebServer = null;
    private long startTime;
    private ContextWrapper context;

    @Rule
    public ActivityTestRule<OAuthWelcome> mActivityRule = new ActivityTestRule<>(
            OAuthWelcome.class, false, false);

    @Before
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start(9000);

        context = new AlternateAssetsContextWrapper(getInstrumentation().getTargetContext(), R.style.AppTheme, getInstrumentation().getContext());

        CountDownLatch latch = new CountDownLatch(1);

        FH.init(context, new FHActCallback() {
            @Override
            public void success(FHResponse pResponse) {
                latch.countDown();
            }

            @Override
            public void fail(FHResponse pResponse) {
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);


        startTime = System.currentTimeMillis();
        DataManager.init(context);
        DataManager.getInstance().remove("sessionToken");
    }

    @After
    public void tearDown() throws Exception {
        try {
            mockWebServer.shutdown();
        } catch (Exception ignore) {

        }
        Thread.sleep(5000);
    }

    @Test
    public void testLoginButtonShowsIfNotLoggedIn() throws IOException {

        OAuthWelcome main = mActivityRule.launchActivity(new Intent(context, OAuthWelcome.class));
//
//        getInstrumentation().runOnMainSync(
//            () -> {
//                main.onCreate(new Bundle());
//                main.onStart();
//            });


        while (main.findViewById(R.id.log_in).getVisibility() == View.GONE) {
            Assert.assertTrue("Timeout after 5 seconds", System.currentTimeMillis() - startTime < 5000);
        }

        main.finish();
    }

    @Test
    public void testLogoutButtonShowsIfLoggedIn() throws IOException, InterruptedException {

        DataManager.getInstance().save("sessionToken", "testToken");
        //Code will call verify which should call the server
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\"isValid\":\"true\"}"));

        OAuthWelcome main = mActivityRule.launchActivity(new Intent(context, OAuthWelcome.class));
//
//        getInstrumentation().runOnMainSync(
//                () -> {
//                    main.onStart();
//                });
//
        while (main.findViewById(R.id.log_out).getVisibility() == View.GONE) {
            Assert.assertTrue("Timeout after 5 seconds", System.currentTimeMillis() - startTime < 5000);
        }

        RecordedRequest request = mockWebServer.takeRequest(5, TimeUnit.SECONDS);
        Assert.assertEquals("/box/srv/1.1/admin/authpolicy/verifysession", request.getPath());
        Assert.assertEquals("{\"sessionToken\":\"testToken\"}", request.getBody().readString(Charset.forName("UTF-8")));

        main.finish();
    }

}
