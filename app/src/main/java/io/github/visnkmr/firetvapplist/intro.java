package io.github.visnkmr.firetvapplist;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by asus on 11/16/2017.
 */

public class intro extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        if(Build.VERSION.SDK_INT >=23){
            Intent intent1 = new Intent();
            intent1.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Toast.makeText(intro.this, "Please provide the necessary permissions for the app to work as intended.", Toast.LENGTH_SHORT).show();
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent1.setData(uri);
            startActivity(intent1);
        }
        TextView proceed=findViewById(R.id.proceed);
        proceed.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                startActivityForResult(new Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS), 0);// ACTION_APPLICATION_SETTINGS
                Intent i = new Intent(intro.this, MainActivity.class);
                finish(); //Kill the activity from which you will go to next activity
                startActivity(i);
                // Code here executes on main thread after user presses button
            }
        });
        /*
        dev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("amzn://apps/android?s=VishnuNK"));
                startActivity(browserIntent);
            }
        });
        */
        Toast.makeText(intro.this, getString(R.string.devtext), Toast.LENGTH_SHORT).show();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event){
        boolean handled = false;

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_BUTTON_A:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(intro.this, (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)?android.R.style.Theme_Material_Dialog_Alert:android.R.style.Theme_Holo_Dialog);
                alertDialog.setIcon(R.mipmap.ic_launcher);
                alertDialog.setTitle(getString(R.string.devtext));
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
                handled=true;
                break;
            case KeyEvent.KEYCODE_MENU:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://visnkmr.github.io/"));
                startActivity(browserIntent);
                handled=true;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                Intent i = new Intent(intro.this, MainActivity.class);
                finish(); //Kill the activity from which you will go to next activity
                startActivity(i);
                handled=true;
                break;
        }
        return handled || super.onKeyDown(keyCode, event);
    }
}

