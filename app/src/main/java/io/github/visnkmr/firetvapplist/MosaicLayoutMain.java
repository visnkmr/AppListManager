package io.github.visnkmr.firetvapplist;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
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
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MosaicLayoutMain extends Activity {

    private PackageManager manager;
    private List<AppDetail> apps;
    private FirebaseAnalytics firebaseAnalytics;

    private int showsysapps=0;
    private LinearLayout maindet;
    private EditText inputsearch;
    private TextView memusg;
    private TableLayout tl;
    private int x=1;

    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mosaic_layout_main);
        firebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());

        firebaseAnalytics.setAnalyticsCollectionEnabled(true);
        firebaseAnalytics.logEvent("MosaicLayout", null);
        firebaseAnalytics.setMinimumSessionDuration(5000);
        firebaseAnalytics.setSessionTimeoutDuration(1000000);

        tl = (TableLayout) findViewById(R.id.maintable);
        maindet=findViewById(R.id.maindet);
maindet.setVisibility(View.GONE);
        loadApps("");
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
                    memusg.setText("Memory Used: " + formattedValue + "%\t\tTotal Memory: "+mi.totalMem/(1024*1024)+" MB\t\tRemainig: "+mi.availMem/(1024*1024)+" MB");
                    Log.i("running","\t\t\tUpdated2222");
                    if(x==1)
                        handler.postDelayed(this, 1000);
                }
            }
        }, 1000 );



        TextView header=findViewById(R.id.header);
        header.append(" running on "+ Build.BRAND+" "+Build.DEVICE+", Android "+Build.VERSION.RELEASE);
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

        /*Button appusg=findViewById(R.id.appusg);
        appusg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), 0);// ACTION_APPLICATION_SETTINGS
                }
                catch (ActivityNotFoundException e){
                    Toast.makeText(MainActivity.this,"No Activity found",Toast.LENGTH_SHORT).show();
                }
                // Code here executes on main thread after user presses button
            }
        });*/

        Button appsettings=findViewById(R.id.appsettings);
        appsettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS), 0);// ACTION_APPLICATION_SETTINGS
                // Code here executes on main thread after user presses button
            }
        });
        ImageButton back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                startActivityForResult(new Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS), 0);// ACTION_APPLICATION_SETTINGS
                startActivity(new Intent(getApplication(), MainActivity.class));
                finish();
                // Code here executes on main thread after user presses button
            }
        });
        back.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    Toast.makeText(getApplicationContext(),"Switch to Normal Layout",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void loadApps(String startwith) {
        tl.removeAllViewsInLayout();
/*        int count = tl.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = tl.getChildAt(i);
            if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
        }*/
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
       addDataOAT(6);
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
    @Override
    protected void onPause() {
        x=0;
        super.onPause();
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
    private SimpleDateFormat mDateFormatter = new SimpleDateFormat("dd/MM/yyyy  hh:mm");

    public String getTime(long time) {
        return mDateFormatter.format(new Date(time));
    }
    void addapp(PackageInfo p){
        manager = getPackageManager();
        AppDetail app = new AppDetail();
        app.label = p.applicationInfo.loadLabel(manager);
        app.name = p.applicationInfo.packageName;
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
//

        /*Resources res = null;
        try {
            res = manager.getResourcesForApplication(p.applicationInfo.packageName);
            Configuration config = res.getConfiguration();
            config.densityDpi = DisplayMetrics.DENSITY_XHIGH;

// Update the configuration with the desired resolution
            DisplayMetrics dm = res.getDisplayMetrics();
            res.updateConfiguration(config, dm);

// Grab the app icon
            app.icon = res.getDrawable(p.applicationInfo.icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();*/
            app.icon = p.applicationInfo.loadIcon(manager);
//        }
        // Get a copy of the configuration, and set it to the desired resolution


        app.sourcedir=p.applicationInfo.sourceDir;
        apps.add(app);

    }
    /** This function add the headers to the table **/
    TableRow tr;
ImageView iv1;

    public void addDataOAT(int maxno) {
        int k=0;
        while(k<apps.size()){
            if(k%maxno==0){
                tr = new TableRow(this);
                tr.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                int pad=15;
                tr.setPadding(pad,pad,pad,pad);
            }


                /** Create a TableRow dynamically **/

                iv1=new ImageButton(this);
                iv1.setImageDrawable(apps.get(k).icon);
                final int z=k;
                int pad=5;
                iv1.setPadding(pad,pad,pad,pad);
                int maxpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
                int minpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
            iv1.setAdjustViewBounds(true);
                iv1.setMinimumHeight(minpx);
                iv1.setMinimumWidth(minpx);
                iv1.setMaxWidth(maxpx);
                iv1.setMaxHeight(maxpx);

            iv1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        maindet.setVisibility(View.VISIBLE);
//                        memusg.setText(apps.get(z).label);
                        ImageView appIcon = (ImageView)findViewById(R.id.item_app_icon);
                        appIcon.setImageDrawable(apps.get(z).icon);

                        TextView appLabel = (TextView)findViewById(R.id.item_app_label);
                        appLabel.setText(apps.get(z).label);appLabel.append(" ");

                        TextView lut = (TextView)findViewById(R.id.lut);
                        lut.setText("Last Updated On: ");
                        lut.append(apps.get(z).lastupdatetime);

                        TextView sdir = (TextView)findViewById(R.id.sdir);
                        sdir.setText("Sourcedir: ");
                        sdir.append(apps.get(z).sourcedir);

                        TextView perm = (TextView)findViewById(R.id.perm);
                        perm.setText("Size: ");
                        perm.append(apps.get(z).permissions);
                    }
                    else maindet.setVisibility(View.GONE);
                }
            });
//            iv1.setBackground(null);
            iv1.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), AppDetails.class);
                        intent.putExtra("applabel", apps.get(z).label.toString());
//                Log.i(getApplication().toString(),apps.get(pos).label.toString());
                        startActivity(intent);
//                lastused();
                        // Code here executes on main thread after user presses button
                    }
                });
                tr.addView(iv1);

            if(k%maxno==0||k>=apps.size()) {
                tl.addView(tr, new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));
            }

            k++;
        }

    }

}
