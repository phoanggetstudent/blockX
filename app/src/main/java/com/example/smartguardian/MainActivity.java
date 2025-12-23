package com.example.smartguardian;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnEnableAdmin, btnVpn, btnAccessibility;
    private TextView txtStatus;
    private ComponentName adminComponent;
    private DevicePolicyManager devicePolicyManager;

    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;
    private static final int REQUEST_CODE_VPN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnEnableAdmin = findViewById(R.id.btnEnableAdmin);
        btnVpn = findViewById(R.id.btnVpn);
        btnAccessibility = findViewById(R.id.btnAccessibility);
        txtStatus = findViewById(R.id.txtStatus);

        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminComponent = new ComponentName(this, AdminReceiver.class);

        updateUI();

        btnEnableAdmin.setOnClickListener(v -> {
            if (devicePolicyManager.isAdminActive(adminComponent)) {
                devicePolicyManager.removeActiveAdmin(adminComponent);
                updateUI();
                Toast.makeText(this, "Đã tắt quyền Admin", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Kích hoạt quyền Admin để ngăn chặn gỡ cài đặt.");
                startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
            }
        });

        btnVpn.setOnClickListener(v -> {
            Intent intent = VpnService.prepare(this);
            if (intent != null) {
                startActivityForResult(intent, REQUEST_CODE_VPN);
            } else {
                onActivityResult(REQUEST_CODE_VPN, RESULT_OK, null);
            }
        });

        btnAccessibility.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        // Check Admin
        boolean isAdmin = devicePolicyManager.isAdminActive(adminComponent);
        btnEnableAdmin.setText(isAdmin ? "Tắt Quyền Admin (Chống Gỡ)" : "Bật Quyền Admin (Chống Gỡ)");
        
        // VPN Status is hard to check directly without binding, for simple UI we just toggle start intent
        // Here we assume if service is running... (simplified for MVP)
        
        // Check Accessibility
        boolean isAccessibilityEnabled = isAccessibilityServiceEnabled(this, ContentAccessibilityService.class);
        btnAccessibility.setText(isAccessibilityEnabled ? "Đã bật Giám Sát Nội Dung" : "Bật Giám Sát Nội Dung");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
            updateUI();
        } else if (requestCode == REQUEST_CODE_VPN && resultCode == RESULT_OK) {
            Intent intent = new Intent(this, MyVpnService.class);
            startService(intent);
            Toast.makeText(this, "VPN đã được bật", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isAccessibilityServiceEnabled(Context context, Class<?> serviceClass) {
        ComponentName expectedComponentName = new ComponentName(context, serviceClass);
        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(),  Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null)
            return false;
        
        String colonSplitter = ":";
        String[] enabledServices = enabledServicesSetting.split(colonSplitter);
        for (String enabledService : enabledServices) {
            ComponentName enabledServiceComponentName = ComponentName.unflattenFromString(enabledService);
            if (enabledServiceComponentName != null && enabledServiceComponentName.equals(expectedComponentName))
                return true;
        }
        return false;
    }
}
