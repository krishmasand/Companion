package ap.myapplication;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;

public class BluetoothService extends Service {
    public BluetoothService() {
    }
    public int onStartCommand(Intent intent, int flags, int startID) {
        BluetoothAdapter adapter = (BluetoothAdapter)getSystemService(BLUETOOTH_SERVICE);
        Set<BluetoothDevice> deviceList = adapter.getBondedDevices();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
