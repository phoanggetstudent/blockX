package com.example.smartguardian;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;

public class ContentAccessibilityService extends AccessibilityService {

    private static final String TAG = "ContentAccessService";
    private static final String YOUTUBE_PACKAGE = "com.google.android.youtube";
    private static final String FACEBOOK_PACKAGE = "com.facebook.katana";
    private static final String CHROME_PACKAGE = "com.android.chrome";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null || event.getPackageName() == null) return;

        String packageName = event.getPackageName().toString();
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();

        if (rootNode == null) return;

        // 1. Chặn YouTube Shorts
        if (packageName.equals(YOUTUBE_PACKAGE)) {
            checkForShorts(rootNode);
        }

        // 2. Chặn Facebook Reels (Simple logic)
        if (packageName.equals(FACEBOOK_PACKAGE)) {
            checkForKeywords(rootNode, "Reels");
        }
        
        // 3. Chặn web đen trên Chrome (Simple logic - đọc URL bar hoặc content)
        if (packageName.equals(CHROME_PACKAGE)) {
           checkForKeywords(rootNode, "xxx", "porn", "jav"); // Ví dụ từ khóa
        }
    }

    private void checkForShorts(AccessibilityNodeInfo node) {
        if (node == null) return;
        
        // Cách đơn giản: Tìm text "Shorts" trên màn hình hoặc ID cụ thể của Shorts player
        // Lưu ý: ID có thể thay đổi theo version app.
        
        List<AccessibilityNodeInfo> list = node.findAccessibilityNodeInfosByText("Shorts");
        if (list != null && !list.isEmpty()) {
            // Nếu tìm thấy chữ Shorts, kiểm tra xem nó có phải là title của player không
            // Đây là logic đơn giản, thực tế cần check kỹ hơn cấu trúc view
             Log.d(TAG, "Detected Shorts!");
             performGlobalAction(GLOBAL_ACTION_BACK);
             Toast.makeText(this, "Shorts bị chặn!", Toast.LENGTH_SHORT).show();
        }
        
        // Đệ quy duyệt nếu cần (nhưng findAccessibilityNodeInfosByText đã duyệt rồi)
    }

    private void checkForKeywords(AccessibilityNodeInfo node, String... keywords) {
        if (node == null) return;
        
        // Duyệt cây view để tìm text
        if (node.getText() != null) {
            String content = node.getText().toString().toLowerCase();
            for (String keyword : keywords) {
                if (content.contains(keyword.toLowerCase())) {
                    Log.d(TAG, "Blocked content: " + keyword);
                    performGlobalAction(GLOBAL_ACTION_BACK);
                    Toast.makeText(this, "Nội dung bị hạn chế!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            checkForKeywords(node.getChild(i), keywords);
        }
    }

    @Override
    public void onInterrupt() {
        Log.e(TAG, "Accessibility Service Interrupted");
    }
}
