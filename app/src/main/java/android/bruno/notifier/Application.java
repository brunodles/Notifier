package android.bruno.notifier;

import at.abraxas.amarino.Amarino;

/**
 * Created by bruno on 17/08/14.
 */
public class Application extends android.app.Application {

    public static final String ARDUINO_BLUETOOTH_ADDRESS = "20:13:10:30:05:54";
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        Amarino.disconnect(this, ARDUINO_BLUETOOTH_ADDRESS);
        super.onTerminate();
    }
}
