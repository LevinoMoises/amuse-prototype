package levinomoises.prototype;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.amuse.client.android.AmuseClient;
import com.amuse.client.features.core.UserManagementFeature;
import com.amuse.utils.Callback;

/**
 * Home screen after user logs in
 */
public class HomeActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);

        // Retrieves AmuseClient from helper
		AmuseClient client = ((AmuseClientApp) getApplication()).getAmuseClient();
		if (client != null) {
			// Synchronize local user information
			UserManagementFeature umf = client.getFeature(UserManagementFeature.class);
			umf.synchLocalUserInfo(new Callback<Void>() {
                @Override
                public void onSuccess(Void arg0) {
                    Log.i(AmuseClientApp.TAG, "WelcomeActivity: Local User information synchronized.");
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.w(AmuseClientApp.TAG, "WelcomeActivity: Cannot synchronize local user information", t);
                }
            });
		}
		else {
			// Finish if AmuseClient cant be retrieved
			finish();
		}
	}

	@Override
    protected void onDestroy() {
        if( getApplication() instanceof AmuseClientApp){
            AmuseClientApp app = (AmuseClientApp) getApplication();
            if( app.getAmuseClient() != null){
                app.getAmuseClient().disconnect();
            }
        }
        super.onDestroy();
    }

    // Override onBackPressed to moveTaskToBack
    // Move the task containing this activity to the back of the activity
    // stack. The activity's order within the task is unchanged.
	@Override
	public void onBackPressed() {
        moveTaskToBack(true);
	}
	
	// Exit game and close connection to the AMUSE server
	public void exitGame(View v) {
		AlertDialog.Builder alertDialog  = new AlertDialog.Builder(HomeActivity.this);
		String text = getResources().getString(R.string.exit_confirm);
        LinearLayout ll = new LinearLayout(getBaseContext());
        alertDialog.setView(ll);
		alertDialog.setMessage(text);
		alertDialog.setTitle(R.string.exit);
		alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
                // Disconnect from server
                ((AmuseClientApp) getApplication()).exit();
                HomeActivity.this.finish();
			}
		});
		alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertDialog.setCancelable(true);
		alertDialog.create().show();
	}

}
