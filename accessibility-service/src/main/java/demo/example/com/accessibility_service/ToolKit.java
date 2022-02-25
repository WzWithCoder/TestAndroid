package demo.example.com.accessibility_service;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

/**
 * Create by wangzheng on 2018/11/28
 */
public class ToolKit {
    private final static String TAG = "AccessibilityService";

    /**
     * 该辅助功能开关是否打开了
     *
     * @param accessibilityServiceName：指定辅助服务名字
     * @param context：上下文
     * @return
     */
    public static  boolean isAccessibilitySettingsOn(String accessibilityServiceName, Context context) {
        String serviceName = context.getPackageName() + "/" + accessibilityServiceName;
        int accessibilityEnable = Settings.Secure.getInt(
                context.getContentResolver(),
                Settings.Secure.ACCESSIBILITY_ENABLED, 0);
        if (accessibilityEnable == 1) {
            TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
            String settingValue = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (!TextUtils.isEmpty(settingValue)) {
                colonSplitter.setString(settingValue);
                while (colonSplitter.hasNext()) {
                    String accessibilityService = colonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(serviceName)) {
                        Log.i(TAG, "Accessibility service enable!");
                        return true;
                    }
                }
            }
        } else {
            Log.d(TAG, "Accessibility service disable");
        }
        return false;
    }

    /**
     * 跳转到系统设置页面开启辅助功能
     *
     * @param context：上下文
     */
    public static void openAccessibility(Context context) {
        String accessibilityServiceName = SampleAccessibilityService.class.getCanonicalName();
        if (!isAccessibilitySettingsOn(accessibilityServiceName, context)) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            context.startActivity(intent);
        }
    }
}
