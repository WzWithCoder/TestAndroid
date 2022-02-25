package demo.example.com.accessibility_service;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class SampleAccessibilityService extends AccessibilityService {
    private final static String TAG = "=====--=====";

    /**
     * Callback for {@link AccessibilityEvent}s.
     *
     * @param event The new event. This event is owned by the caller and cannot be used after
     *              this method returns. Services wishing to use the event after this method returns should
     *              make a copy.
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        CharSequence pkgName = event.getPackageName();
        int eventType = event.getEventType();
        int action = event.getAction();
        Log.e(TAG, pkgName + "-" + eventType);
        AccessibilityNodeInfo nodeInfo = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            nodeInfo = event.getSource();
        } else {
            nodeInfo = getRootInActiveWindow();
        }
        if (nodeInfo == null) return;
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            //获取子节点中某个特定的node，这里通过以下方法通过ID查找
            //List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByViewId("com.dianping.v1" + ":id/main_listview");
            //performScroll(nodes);
            //通过text查找
            List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText("热点");
            performClick(nodes);
        }
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                Log.e(TAG, "AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED");
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                Log.e(TAG, "AccessibilityEvent.TYPE_VIEW_SCROLLED");
                break;
        }

    }

    private boolean performScroll(List<AccessibilityNodeInfo> nodes) {
        if (nodes == null) return false;
        for (AccessibilityNodeInfo node : nodes) {
            if (node.isScrollable()) {
                node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                return true;
            }
        }
        return false;
    }

    private boolean performClick(List<AccessibilityNodeInfo> nodes) {
        if (nodes == null) return false;
        for (AccessibilityNodeInfo node : nodes) {
            AccessibilityNodeInfo parent = node.getParent();
            //while循环,遍历父布局，直至找到可点击的为止
            while (parent != null) {
                Log.e(TAG, "InfoType:" + parent.getClassName());
                Log.e(TAG, "InfoText:" + parent.getText());
                Log.e(TAG, "InfoPkgName:" + parent.getPackageName());
                Log.e(TAG, "InfoViewId:" + parent.getViewIdResourceName());
                if (parent.isClickable()) {
                    parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }
                parent = parent.getParent();
            }
        }
        return false;
    }


    /**
     * This method is a part of the {@link AccessibilityService} lifecycle and is
     * called after the system has successfully bound to the service. If is
     * convenient to use this method for setting the AccessibilityServiceInfo.
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.e(TAG, "onServiceConnected");
    }

    /**
     * Callback for interrupting the accessibility feedback.
     */
    @Override
    public void onInterrupt() {
        Log.e(TAG, "onInterrupt");
    }

}
