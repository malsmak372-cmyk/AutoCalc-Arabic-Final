package com.dreamfish.com.autocalc.dialog;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.dreamfish.com.autocalc.R;
import com.dreamfish.com.autocalc.SettingsActivity;

public class CommonDialogs {

  public static final int RESULT_SETTING_ACTIVITY = 0;
  public static final int RESULT_REQUEST_PERMISSION = 101;

  public static void showSettings(Activity activity) {
    activity.startActivityForResult(new Intent(activity, SettingsActivity.class), RESULT_SETTING_ACTIVITY);
  }

}
