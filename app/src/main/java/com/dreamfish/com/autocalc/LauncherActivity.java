package com.dreamfish.com.autocalc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;

import com.dreamfish.com.autocalc.utils.PermissionsUtils;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        startMain(true);
    }

    private Thread myThread = null;
    private void startMain(Boolean agreementAllowed) {

        myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(500);
                    Intent it = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(it);
                    sleep(800);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        PermissionsUtils.getInstance().chekPermissions(this, PermissionsUtils.permissions, permissionsResult);
    }

    PermissionsUtils.IPermissionsResult permissionsResult = new PermissionsUtils.IPermissionsResult() {
        @Override
        public void passPermissons() {
            myThread.start();
        }
        @Override
        public void forbitPermissons() {
            myThread.start();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsUtils.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}
