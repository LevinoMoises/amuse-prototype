package levinomoises.prototype;


import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.amuse.client.LoginInfo;
import com.amuse.client.android.CallbackManager;
import com.amuse.client.android.DefaultLoginManager;
import com.amuse.utils.Callback;


/**
 * First screen. A login screen that offers login via name/password.
 */
public class LoginActivity extends Activity {

    // DefaultLogin class to manage the login
    private DefaultLoginManager loginManager;
    private CallbackManager callbackManager;
    private volatile boolean destroyed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        destroyed = false;
        callbackManager = new CallbackManager(this);

        // DefaultLoginManager class to set visibility of loginForm and progressBar
        // Full API can be found at http://jade.tilab.com/amuse/doc/api/index.html
        loginManager = new DefaultLoginManager(this);
        loginManager.setVisibleLoginFields(new int[]{R.id.loginForm});
        loginManager.setHiddenLoginFields(new int[]{R.id.startupProgressbar});

        // Helper class to connect to the AMUSE server
        // It returns a callback, like every other AMUSE server function
        ((AmuseClientApp) getApplication()).connect(loginManager,
                callbackManager.wrap(Void.class, new Callback<Void>() {
            @Override
            public void onSuccess(Void v) {
                Log.i(AmuseClientApp.TAG, "Connection successful");

                // Check to see if the app has been closed. If true, return it
                if( destroyed){return;}

                // Starts the HomeActivity and finish this activity
                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(i);
                LoginActivity.this.finish();
            }

            @Override
            public void onFailure(final Throwable th) {
                Log.w(AmuseClientApp.TAG, "Connection error", th);
                // Connects to the Amuse server and log error
                showError(th);
            }
        }));

    }
    // Login function loginDone accepts a LoginInfo
    // The LoginInfo constructor requires username, password and a boolean
    // True for false for login, true for sign up
    public void login(View v){
        EditText usernameText = (EditText) findViewById(R.id.usernameText);
        EditText passwordText = (EditText) findViewById(R.id.passwordText);
        String username = usernameText.getText().toString().trim();
        String password = passwordText.getText().toString();
        loginManager.loginDone(new LoginInfo(username, password, false));
    }

    // SignUp function loginDone accepts a LoginInfo
    // The LoginInfo constructor requires username, password and a boolean
    // True for false for login, true for sign up
    public void signUp(View v){
        EditText usernameText = (EditText) findViewById(R.id.usernameText);
        EditText passwordText = (EditText) findViewById(R.id.passwordText);
        String username = usernameText.getText().toString().trim();
        String password = passwordText.getText().toString();
        loginManager.loginDone(new LoginInfo(username, password, true));
    }

    // Helper function to log error
    private void showError(final Throwable th) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(LoginActivity.this);
        dlgAlert.setMessage(LoginActivity.this.getResources().getString(R.string.join_error)+": "+th.getMessage());
        dlgAlert.setTitle(getResources().getString(R.string.error));
        dlgAlert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ((AmuseClientApp)getApplication()).getAmuseClient().clearCredentials();

                // Terminate
                LoginActivity.this.finish();
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

}

