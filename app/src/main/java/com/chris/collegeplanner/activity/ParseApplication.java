package com.chris.collegeplanner.activity;

import android.app.Application;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

/**
 * Created by chris on 04/11/15.
 */
public class ParseApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);

    //    ParseCrashReporting.enable(this);
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "jHff55KsNNo2V1KqGiE8WeixJv1M72OqByXATrwX", "ZojNXQRS0b2XJeKnENyqqYxtaUXqURJwu2ky2Sti");
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

//        ParseUser.enableAutomaticUser();
//        ParseACL defaultACL = new ParseACL();
//
//        // If you would like all objects to be private by default, remove this
//        // line.
//        defaultACL.setPublicReadAccess(true);
//
//        ParseACL.setDefaultACL(defaultACL, true);

    }
}