package com.dreamfish.com.autocalc.utils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.dreamfish.com.autocalc.dialog.CommonDialogs;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsUtils {

  
  public static String[] permissions = new String[]{
          Manifest.permission.READ_EXTERNAL_STORAGE,
          Manifest.permission.WRITE_EXTERNAL_STORAGE
  };
  
  public static boolean showSystemSetting = true;

  private PermissionsUtils() {
  }

  private static PermissionsUtils permissionsUtils;
  private IPermissionsResult mPermissionsResult;

  public static PermissionsUtils getInstance() {
    if (permissionsUtils == null) {
      permissionsUtils = new PermissionsUtils();
    }
    return permissionsUtils;
  }

  public void chekPermissions(Activity context, String[] permissions, @NonNull IPermissionsResult permissionsResult) {
    mPermissionsResult = permissionsResult;

    
    List<String> mPermissionList = new ArrayList<>();
    
    for (int i = 0; i < permissions.length; i++) {
      if (ContextCompat.checkSelfPermission(context, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
        mPermissionList.add(permissions[i]);
      }
    }

    
    if (mPermissionList.size() > 0) {
      ActivityCompat.requestPermissions(context, permissions, CommonDialogs.RESULT_REQUEST_PERMISSION);
    } else {
      
      permissionsResult.passPermissons();
      return;
    }


  }

  
  public void onRequestPermissionsResult(Activity context, int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    boolean hasPermissionDismiss = false;
    if (CommonDialogs.RESULT_REQUEST_PERMISSION == requestCode) {
      for (int i = 0; i < grantResults.length; i++) {
        if (grantResults[i] == -1) {
          hasPermissionDismiss = true;
        }
      }
      
      if (hasPermissionDismiss) {
        if (showSystemSetting) {
          showSystemPermissionsSettingDialog(context);
        } else {
          mPermissionsResult.forbitPermissons();
        }
      } else {
        
        mPermissionsResult.passPermissons();
      }
    }

  }

  
  AlertDialog mPermissionDialog;

  public void goSettingsPage(Activity context) {
    final String mPackName = context.getPackageName();
    Uri packageURI = Uri.parse("package:" + mPackName);
    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
    context.startActivity(intent);
  }

  private void showSystemPermissionsSettingDialog(final Activity context) {

    if (mPermissionDialog == null) {
      mPermissionDialog = new AlertDialog.Builder(context)
              .setMessage("您似乎禁用了软件的部分权限，这可能会导致应用部分功能不可用或不正常，是否继续？")
              .setPositiveButton("الذهاب للإعدادات", (dialog, which) -> {
                cancelPermissionDialog();
                goSettingsPage(context);
                context.finish();
              })
              .setNegativeButton("الاستمرار على أي حال", (dialog, which) -> {
                
                cancelPermissionDialog();
                //mContext.finish();
                mPermissionsResult.forbitPermissons();
              })
              .create();
    }
    mPermissionDialog.show();
  }

  
  private void cancelPermissionDialog() {
    if (mPermissionDialog != null) {
      mPermissionDialog.cancel();
      mPermissionDialog = null;
    }

  }

  public interface IPermissionsResult {
    void passPermissons();
    void forbitPermissons();
  }

}
