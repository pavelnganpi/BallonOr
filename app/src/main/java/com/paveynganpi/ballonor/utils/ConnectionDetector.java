package com.paveynganpi.ballonor.utils;

/**
 * Created by paveynganpi on 8/10/15.
 */

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;

public class ConnectionDetector {

    private Context _context;
    private Activity mActivity;

    public ConnectionDetector(Context context, Activity activity){
        this._context = context;
        this.mActivity = activity;
    }

    public boolean isConnectingToInternet(){
        ConnectivityManager connMgr = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            Log.d("networkconnected", "true in connector");
            return true;

        } else {
            // display error
            Log.d("networkconnected", "true in connector");
            return false;
        }
    }

    public void showAlertDialog(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(_context);

        alert.setTitle("No Internet Connection");
        alert.setMessage("You dont have internet connection");

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mActivity.finish();

                    }
                }
        );

        alert.show();
    }
}