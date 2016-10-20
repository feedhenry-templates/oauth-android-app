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


import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.feedhenry.helloworld.test.activity.StubFHOAuthActivity;
import com.feedhenry.sdk.FH;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static android.support.test.InstrumentationRegistry.getInstrumentation;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class FOAuthAbstractClassTest {

    Context context;

    private MockWebServer mockWebServer = null;
    private long startTime;

    @Rule
    public ActivityTestRule<StubFHOAuthActivity> mActivityRule = new ActivityTestRule<>(
            StubFHOAuthActivity.class, false, false);


    @Before
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start(9000);
        context = new ContextWrapper(InstrumentationRegistry.getTargetContext()) {
            @Override
            public AssetManager getAssets() {
                return InstrumentationRegistry.getContext().getAssets();
            }
        };

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
    public void testActivityCallsFHInitOnStartup() throws IOException {

        Delegate delegate = new Delegate();

        StubFHOAuthActivity activity = mActivityRule.launchActivity(new Intent(context, StubFHOAuthActivity.class));
        getInstrumentation().runOnMainSync(() -> {
            activity.setCallbackDelegate(delegate);
            activity.onStart();
        });

        while (!delegate.onFHReadyCalled) {
            Assert.assertTrue("Timeout after 15 seconds", System.currentTimeMillis() - startTime < 15000);
        }

        activity.finish();

    }

    @Test
    public void testLoginButtonShowsIfNotLoggedIn() throws Exception {

        Delegate delegate = new Delegate();

        StubFHOAuthActivity activity = mActivityRule.launchActivity(new Intent(context, StubFHOAuthActivity.class));
        getInstrumentation().runOnMainSync(() -> {
            activity.setCallbackDelegate(delegate);
            activity.onStart();
        });

        while (!delegate.onFHReadyCalled) {
            Assert.assertTrue("Timeout after 15 seconds", System.currentTimeMillis() - startTime < 15000);
        }

        activity.checkSession();

        Assert.assertTrue(delegate.onNotLoggedInCalled);

        activity.finish();
    }


    private static class Delegate implements StubFHOAuthActivity.CallbackDelegate {

        boolean onFHReadyCalled = false;
        boolean onNotLoggedInCalled = false;
        boolean onSessionValidCalled = false;


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
