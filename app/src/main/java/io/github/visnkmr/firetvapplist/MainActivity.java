/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.github.visnkmr.firetvapplist;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
 * MainActivity class that loads {@link MainFragment}.
 */
public class MainActivity extends Activity {
    private PackageManager manager;
    private List<AppDetail> apps;
    private FirebaseAnalytics firebaseAnalytics;

    private int showsysapps=0;
    private EditText inputsearch;
    private TextView memusg;
    private int x=1;

    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }

    SharedPreferences aboutact;
    //    factstrings;
    public String fcheck = "WFirstTime";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());

        firebaseAnalytics.setAnalyticsCollectionEnabled(true);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null);
        firebaseAnalytics.setMinimumSessionDuration(5000);
        firebaseAnalytics.setSessionTimeoutDuration(1000000);

        aboutact = getSharedPreferences(fcheck, MODE_PRIVATE);
        boolean fnocheck = aboutact.getBoolean("fvalue", false);

        if (!fnocheck) {
            aboutact = getSharedPreferences(fcheck,0);
            SharedPreferences.Editor fceditor = aboutact.edit();
            fceditor.putBoolean("fvalue", true);
            fceditor.apply();
            Intent intent = new Intent(getApplicationContext(), intro.class);
            startActivity(intent);
            finish();
        }
        else {
//            inputsearch.isFocusable();
            Toast.makeText(this, getString(R.string.devtext), Toast.LENGTH_SHORT).show();
        }
            memusg=findViewById(R.id.memusg);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                    ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    if (activityManager != null) {

                        activityManager.getMemoryInfo(mi);
//            double availableMegs = mi.availMem / 0x100000L;

//Percentage can be calculated for API 16+
                        double percentAvail = mi.availMem / (double) mi.totalMem * 100.0;
                        percentAvail = 100 - percentAvail;
                        String formattedValue = String.format("%.0f", percentAvail);
                        memusg.setText("Memory Used: " + formattedValue + "%\t\tTotal Memory: "+mi.totalMem/(1024*1024)+" MB\t\tRemaining: "+mi.availMem/(1024*1024)+" MB");
                        Log.i("running","\t\t\tUpdated1111");
                        if(x==1)
                            handler.postDelayed(this, 1000);
                    }
                }
            }, 1000 );

            loadApps("");

            TextView header=findViewById(R.id.header);
            header.append(" running on "+Build.BRAND+" "+Build.DEVICE+", Android "+Build.VERSION.RELEASE+" ( Build Version Number: "+Build.VERSION.SDK_INT+" )");
            // Locate the EditText in listview_main.xml

            Button showsys=findViewById(R.id.showsys);
            showsys.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showsysapps=1;
                    loadApps("");
//                lastused();
                    // Code here executes on main thread after user presses button
                }
            });
            Button showuser=findViewById(R.id.showusr);

            showuser.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showsysapps=0;
                    loadApps("");
                    // Code here executes on main thread after user presses button
                }
            });

            inputsearch = (EditText) findViewById(R.id.inputSearch);

            // Capture Text in EditText
            inputsearch.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                    String text = inputsearch.getText().toString().toLowerCase(Locale.getDefault());
                    loadApps(text);
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1,
                                              int arg2, int arg3) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                    // TODO Auto-generated method stub
                }
            });
            Button appsettings=findViewById(R.id.appsettings);
            appsettings.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivityForResult(new Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS), 0);// ACTION_APPLICATION_SETTINGS
//                startActivity(new Intent(getApplication(), MosaicLayoutMain.class));
                    // Code here executes on main thread after user presses button
                }
            });
            ImageButton mosaic=findViewById(R.id.mosaiclayout);
            mosaic.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
//                startActivityForResult(new Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS), 0);// ACTION_APPLICATION_SETTINGS
                    startActivity(new Intent(getApplication(), MosaicLayoutMain.class));
                    finish();
                    // Code here executes on main thread after user presses button
                }
            });
            mosaic.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        Toast.makeText(getApplicationContext(),"Switch to Mosaic Layout",Toast.LENGTH_SHORT).show();
                    }
                }
            });
//        }
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        boolean handled = false;

        switch (keyCode){
            case KeyEvent.KEYCODE_MENU:
                inputsearch.requestFocus();
                handled=true;
                break;
        }
        return handled || super.onKeyDown(keyCode, event);
    }
    private void loadApps(String startwith) {


//        Intent i = new Intent(Intent.ACTION_MAIN, null);
//        i.addCategory(Intent.CATEGORY_LAUNCHER);
        /*PackageInfo mPackageInfo;
        PackageManager mPackageManager;

        mPackageManager = getPackageManager();*/
        apps = new ArrayList<>();

        int x;

        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for (int j = 0; j < packs.size(); j++){
            x = 0;
            PackageInfo p = packs.get(j);
            if(p.applicationInfo.loadLabel(getPackageManager()).toString().replace(" ","").toLowerCase(Locale.getDefault()).contains(startwith.replace(" ","").toLowerCase(Locale.getDefault()))){
                for (AppDetail test : apps) {
                    if (test.sourcedir.equals(p.applicationInfo.sourceDir)) {
                        x = 1;
                    }
                }
                if (x == 0) {
                        if (showsysapps == 0) {
                            if (!isSystemPackage(p)) {
                                addapp(p);
                            }
                        }
                        else if(showsysapps==1)
                            if (isSystemPackage(p))addapp(p);
                }
            }

            }


                /*ApplicationInfo applicationInfo;
        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);

        for (ResolveInfo ri : availableActivities) {
//            Log.i("test APPNAME:",ri.loadLabel(manager).toString());
            if(ri.loadLabel(manager).toString().replace(" ","").toLowerCase(Locale.getDefault()).contains(startwith.replace(" ","").toLowerCase(Locale.getDefault()))) {
                x = 0;
                try {
                    mPackageInfo = mPackageManager.getPackageInfo(ri.activityInfo.packageName, 0);
                    applicationInfo = mPackageInfo.applicationInfo;
                    for (AppDetail test : apps) {
                        if (test.sourcedir.equals(applicationInfo.sourceDir)) {
                            x = 1;
                        }
                    }
                    if (x == 0) {
                        if (isSystemPackage(mPackageInfo))*//*
                            app.type = "system";
                        else*//*
                        {

                            AppDetail app = new AppDetail();
                            app.label = ri.loadLabel(manager);
                            app.name = ri.activityInfo.packageName;
                            app.icon = ri.activityInfo.loadIcon(manager);
                            app.lastupdatetime = getTime(mPackageInfo.lastUpdateTime);
                            app.sourcedir = applicationInfo.sourceDir;
                            PackageInfo testinfo = mPackageManager.getPackageInfo(ri.activityInfo.packageName,
                                    PackageManager.GET_PERMISSIONS);
                            app.permissions = humanReadableByteCount(getFileSize(applicationInfo.publicSourceDir));
                            String[] reqPermissions = testinfo.requestedPermissions;
                            if (reqPermissions != null)
                                app.permissions = humanReadableByteCount(getFileSize(applicationInfo.publicSourceDir)) + " Permissions:" + testinfo.requestedPermissions.length + "";

                            app.type = "User";
                            apps.add(app);
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }*/
        loadListView();
        TextView noapps=findViewById(R.id.noapps);
//        int m=0;
        noapps.setText(apps.size()+" Apps found. Use the menu to quickly navigate to the searchbox.");
       /* List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        noapps.append(packs.size()+"Installed apps ");
        for (int j = 0; j < packs.size(); j++) {
            PackageInfo p = packs.get(j);
            if(isSystemPackage(p))m++;
//                noapps.append("\n"+p.applicationInfo.loadLabel(getPackageManager()).toString());
        }
        noapps.append(m+" System Apps");*/

        addClickListener();
    }

    @Override
    protected void onPause() {
        x=0;
        super.onPause();
    }

    void addapp(PackageInfo p){
            manager = getPackageManager();
        AppDetail app = new AppDetail();
        app.label = p.applicationInfo.loadLabel(manager);
        app.name = p.applicationInfo.packageName;
        app.icon = p.applicationInfo.loadIcon(manager);
        app.sourcedir = p.applicationInfo.sourceDir;
        app.lastupdatetime = getTime(p.lastUpdateTime);
            app.permissions = humanReadableByteCount(getFileSize(p.applicationInfo.publicSourceDir));
    try {
        PackageInfo packageInfo = manager.getPackageInfo(p.applicationInfo.packageName, PackageManager.GET_PERMISSIONS);
        String[] reqPermissions = packageInfo.requestedPermissions;
        if (reqPermissions != null){
            app.permissions = humanReadableByteCount(getFileSize(p.applicationInfo.publicSourceDir)) + " Permissions: " + packageInfo.requestedPermissions.length;

        }
    } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
    }
        apps.add(app);

}
        private boolean isSystemPackage(PackageInfo pkgInfo) {
            return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
        }
    private long getFileSize(String filePath) {
        return new File(filePath).length();
    }
    public static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < ((long) unit)) {
            return new StringBuilder(String.valueOf(bytes)).append(" B").toString();
        }
        String pre = new StringBuilder(String.valueOf(("KMGTPE").charAt(((int) (Math.log((double) bytes) / Math.log((double) unit))) - 1))).append("").toString();
        return String.format("%.1f %sB", new Object[]{Double.valueOf(((double) bytes) / Math.pow((double) unit, (double) ((int) (Math.log((double) bytes) / Math.log((double) unit))))), pre});
    }
    private SimpleDateFormat mDateFormatter = new SimpleDateFormat("EE LLL dd yyyy kk:mm:ss");

    public String getTime(long time) {
        return mDateFormatter.format(new Date(time));
    }
    ArrayAdapter<AppDetail> adapter;
    private void loadListView(){


        ListView list = findViewById(R.id.apps_list);

        adapter = new ArrayAdapter<AppDetail>(this,
                R.layout.list_item,
                apps) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.list_item, null);
                }

                ImageView appIcon = (ImageView)convertView.findViewById(R.id.item_app_icon);
                appIcon.setImageDrawable(apps.get(position).icon);

                TextView appLabel = (TextView)convertView.findViewById(R.id.item_app_label);
                appLabel.setText(apps.get(position).label);appLabel.append(" ");
/*
                TextView lm = (TextView)convertView.findViewById(R.id.lm);
                lm.setText("Installed On: ");
                lm.append(apps.get(position).lastmodified);*/

                TextView lut = (TextView)convertView.findViewById(R.id.lut);
                lut.setText("Last Updated On: ");
                lut.append(apps.get(position).lastupdatetime);

                TextView sdir = (TextView)convertView.findViewById(R.id.sdir);
                sdir.setText("Sourcedir: ");
                sdir.append(apps.get(position).sourcedir);

                TextView perm = (TextView)convertView.findViewById(R.id.perm);
                perm.setText("Size: ");
                perm.append(apps.get(position).permissions);



                return convertView;
            }
        };

        list.setAdapter(adapter);
    }
    /*public void lastused(){
        apps = new ArrayList<>();

        int x;

        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        // We get usage stats for the last 5 seconds
        final long timeEnd = System.currentTimeMillis();
        final long timeBegin = timeEnd - 30*1000; // +30 sec.
        List<UsageStats> stats;
        if (usageStatsManager != null) {
            stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, timeBegin, timeEnd);
            String lastUsedApp;
            for (UsageStats appStats : stats) {
                x=0;
//                if (appStats.getLastTimeUsed() > lastUsedTime ) {
                    long lastUsedTime = appStats.getLastTimeUsed();
                    lastUsedApp = appStats.getPackageName();
                    Log.i("test",lastUsedApp+lastUsedTime);

                    List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);

                    for (int j = 0; j < packs.size(); j++){
                        PackageInfo p = packs.get(j);
                        if (p.packageName.equals(lastUsedApp)){
                            for (AppDetail test : apps) {
                                if (test.sourcedir.equals(p.applicationInfo.sourceDir)) {
                                    x = 1;
                                }
                            }
                            if(x==0)addapp(p);
                        }
                    }
            }
        }
        loadListView();
        // Get the next-to-last app from this list.

    }*/
    private void addClickListener(){
        ListView list = findViewById(R.id.apps_list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, final int pos,
                                    long id) {
                /*btn.setOnClickListener(
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                            }
                        }
                );*/
                /*String appurl= "https://play.google.com/store/apps/details?id="+apps.get(pos).name.toString();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(appurl));*/
                /*Intent i = manager.getLaunchIntentForPackage(apps.get(pos).name.toString());//com.amazon.ssm
                startActivity(i);*/
                Intent intent = new Intent(getBaseContext(), AppDetails.class);
                intent.putExtra("applabel", apps.get(pos).label.toString());
//                Log.i(getApplication().toString(),apps.get(pos).label.toString());
                startActivity(intent);

//                Toast.makeText(MainActivity.this,apps.get(pos).name.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }
}


/*Button devinfo=findViewById(R.id.devinfo);
        devinfo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS), 0);
                // Code here executes on main thread after user presses button
            }
        });*/


        /*
        String copyToPath;
            copyToPath = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append(File.separator).append("apk").append(File.separator).toString();
        new PromptDialog(this, "Copy To...","File will be saved to "+ String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath()), copyToPath, "Copy", "Cancel", info.position) {
            public boolean onOkClicked(String input, int id) {
                MainActivity.onAppCopyToSD(id, input);
                return true;
            }
        }.show();*/
