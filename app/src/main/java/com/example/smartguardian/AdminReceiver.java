package com.example.smartguardian;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AdminReceiver extends DeviceAdminReceiver {
    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        Toast.makeText(context, "SmartGuardian Admin đã được kích hoạt", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        Toast.makeText(context, "SmartGuardian Admin đã bị tắt", Toast.LENGTH_SHORT).show();
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "Bạn có chắc chắn muốn tắt quyền bảo vệ? Ứng dụng sẽ không thể ngăn chặn việc gỡ cài đặt nữa.";
    }
}
