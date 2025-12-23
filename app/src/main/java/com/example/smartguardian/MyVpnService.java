package com.example.smartguardian;

import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

public class MyVpnService extends VpnService {

    private Thread mThread;
    private ParcelFileDescriptor mInterface;
    private boolean isRunning = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mThread != null) {
            mThread.interrupt();
        }
        mThread = new Thread(this::runVpn, "MyVpnThread");
        mThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mThread != null) {
            isRunning = false;
            mThread.interrupt();
        }
        super.onDestroy();
    }

    private void runVpn() {
        try {
            if (mInterface != null) {
                mInterface.close();
                mInterface = null;
            }

            // Cấu hình VPN interface
            Builder builder = new Builder();
            builder.setMtu(1500);
            builder.addAddress("10.0.0.2", 32);
            builder.addRoute("0.0.0.0", 0); 
            builder.addDnsServer("8.8.8.8");
            builder.setSession("SmartGuardianVPN");
            
            // Trong thực tế, để chặn, ta cần intercept traffic.
            // Ở đây demo tạo interface thành công để hiện icon chìa khóa.
            
            mInterface = builder.establish();
            isRunning = true;
            
            FileInputStream in = new FileInputStream(mInterface.getFileDescriptor());
            FileOutputStream out = new FileOutputStream(mInterface.getFileDescriptor());
            
            ByteBuffer packet = ByteBuffer.allocate(32767);
            
            while (isRunning && !Thread.interrupted()) {
                // Đọc packet từ app
                int length = in.read(packet.array());
                if (length > 0) {
                    // Logic xử lý packet ở đây
                    // Nếu là DNS query đến web đen -> Drop
                    // Nếu an toàn -> Forward (Cần implement TCP/IP stack hoặc forward socket thật phức tạp)
                    
                    // Code này hiện tại chỉ DROP toàn bộ traffic (Mất mạng)
                    // Để demo chặn "hiệu quả" :D 
                    // Người dùng sẽ thấy mạng bị ngắt khi bật VPN này.
                    // Đây là cách chặn tuyệt đối nhất =)))
                    
                    packet.clear();
                }
                Thread.sleep(100);
            }

        } catch (Exception e) {
            Log.e("MyVpnService", "Error", e);
        } finally {
            try {
                if (mInterface != null) {
                    mInterface.close();
                    mInterface = null;
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }
}
