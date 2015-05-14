package com.parse.starter;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCrashReporting;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

public class ParseApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    ParseCrashReporting.enable(this);

    Parse.enableLocalDatastore(this);

    Parse.initialize(this);

    ParseInstallation.getCurrentInstallation();

    Parse.initialize(this, "xnD3cRRrX8K6HiLYE3x98Ytct8yBmseGSz4qTs85", "5mvT0OAzCR4E8K4INSUc6xGn5fT5HGouh1SWWSVK");
  }
}
