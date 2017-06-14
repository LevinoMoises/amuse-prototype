package levinomoises.prototype;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.amuse.client.LoginManager;
import com.amuse.client.android.AmuseClient;
import com.amuse.utils.Callback;

import jade.core.MicroRuntime;
import jade.util.leap.Properties;

/**
 * Helper class to provide AMUSE functions
 */
public class AmuseClientApp extends Application {
    public static final String TAG = "PROTOTYPE";
    private AmuseClient amuseClient;

    // Helper method to connect to the AMUSE server
    public void connect(final LoginManager lm, final Callback<Void> callback) {
        Log.i(TAG, "Started connection");

        AmuseClient.getInstance(getResources().getString(R.string.registered_app_name), getApplicationContext(), new Callback<AmuseClient>() {
            public void onSuccess(AmuseClient amuseClient) {
                AmuseClientApp.this.amuseClient = amuseClient;
                Log.i(TAG, "Amuse client successfully retrieved");

                // Connect to the Amuse server
                Properties properties= new Properties();
                String ip = getResources().getString(R.string.amuse_host);
                properties.setProperty(MicroRuntime.HOST_KEY, ip);
                String port = getResources().getString(R.string.amuse_port);
                properties.setProperty(MicroRuntime.PORT_KEY,  port);
                String proto = getResources().getString(R.string.amuse_proto);
                properties.setProperty(MicroRuntime.PROTO_KEY,  proto);
                AmuseClientApp.this.amuseClient.setConnectionProperties(properties);
                AmuseClientApp.this.amuseClient.setLoginManager(lm);
                AmuseClientApp.this.amuseClient.connect(callback);
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "Error retrieving AmuseClient", error);
            }
        });
    }

    // Helper method to exit AMUSE platform
    public void exit() {
        amuseClient.disconnect();
        amuseClient = null;
    }

    // Getter for the amuseClient
    public AmuseClient getAmuseClient() {
        return amuseClient;
    }

}
