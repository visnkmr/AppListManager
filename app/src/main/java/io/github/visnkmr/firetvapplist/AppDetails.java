package io.github.visnkmr.firetvapplist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
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
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class AppDetails extends Activity {
    public ProgressDialog progressDialog;
    private FirebaseAnalytics firebaseAnalytics;
    private String dir,apkname,name,Apkbuiltdate;
    private LinearLayout btnlyt;
    private TextView lm;

    private TableRow tr;
    private TableLayout tl;
    private TextView tv1,tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details);

        firebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());

        firebaseAnalytics.setAnalyticsCollectionEnabled(true);
        firebaseAnalytics.logEvent("AppDetails", null);
        firebaseAnalytics.setMinimumSessionDuration(5000);
        firebaseAnalytics.setSessionTimeoutDuration(1000000);

        tl = (TableLayout) findViewById(R.id.maintable);


        ImageView icon = findViewById(R.id.app_icon);
        TextView appLabel = (TextView) findViewById(R.id.app_label);
        lm = (TextView) findViewById(R.id.lm);
        TextView lut = (TextView) findViewById(R.id.lut);
        TextView sdir = (TextView) findViewById(R.id.sdir);
//        TextView permdet = (TextView) findViewById(R.id.permissions);

        final Button playstore=findViewById(R.id.playstore);
        Button saveapk=findViewById(R.id.saveapk);
        Button appsettings=findViewById(R.id.appsettings);
        Button openapp=findViewById(R.id.openapp);

        btnlyt=findViewById(R.id.btnlyt);

        String s= getIntent().getStringExtra("applabel");

//        Log.i(getApplication().toString(),s);
        PackageManager manager;
        manager = getPackageManager();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for (int j = 0; j < packs.size(); j++){
            PackageInfo p = packs.get(j);

            if (s.equals(p.applicationInfo.loadLabel(manager))) {

                            name = p.applicationInfo.packageName;

                            icon.setImageDrawable(p.applicationInfo.loadIcon(manager));
                            appLabel.setText(p.applicationInfo.loadLabel(manager));

                            Apkbuiltdate= apkbuiltdate(p);
                            sdir.setText("APK Built on: " + Apkbuiltdate);

                            lm.setText("Installed On: ");
                            lm.append(getTime(p.firstInstallTime));

                            lut.append("Updated On: ");
                            lut.append(getTime(p.lastUpdateTime));

                            dir=p.applicationInfo.publicSourceDir;

                            if (dir.indexOf("/asec/") > 0) {
                                dir = dir.substring(0, dir.lastIndexOf("/")) + "/pkg.apk";
                            }
                            String[] pathComponents = dir.split(Pattern.quote(File.separator));
                            apkname=pathComponents[pathComponents.length - 1];

                try {
                    PackageInfo packageInfo = manager.getPackageInfo(p.applicationInfo.packageName, PackageManager.GET_PERMISSIONS);
                    String[] reqPermissions = packageInfo.requestedPermissions;
                    if (reqPermissions != null){
                        addPermissionHeader();
                        addPermissions(reqPermissions);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    PackageInfo packageInfo = manager.getPackageInfo(p.applicationInfo.packageName, PackageManager.GET_ACTIVITIES);
                    ActivityInfo[] activities = packageInfo.activities;
                    if (activities != null) {
                        addActivityHeader();
                        addActivities(activities);
                    }
//                    else Toast.makeText(getApplication(),"NULL.",Toast.LENGTH_SHORT).show();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    PackageInfo packageInfo = manager.getPackageInfo(p.applicationInfo.packageName, PackageManager.GET_RECEIVERS);
                    ActivityInfo[] activities = packageInfo.receivers;
                    if (activities != null) {
                        addRecieverHeader();
                        addRecievers(activities);
                    }
//                    else Toast.makeText(getApplication(),"NULL.",Toast.LENGTH_SHORT).show();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    PackageInfo packageInfo = manager.getPackageInfo(p.applicationInfo.packageName, PackageManager.GET_SERVICES);
                    ServiceInfo[] activities = packageInfo.services;
                    if (activities != null) {
                        addServicesHeader();
                        addServices(activities);
                    }
//                    else Toast.makeText(getApplication(),"NULL.",Toast.LENGTH_SHORT).show();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                appLabel.append(" " + p.versionName);

                            if((p.applicationInfo.flags&1)!=0){
                                appLabel.append(" (System App) ");
//                                saveapk.setAlpha(.5f);
//                                saveapk.setClickable(false);
                                saveapk.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        String copyTo = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append(File.separator).append("apk").append(File.separator).append(apkname).toString();
                                        onAppCopyToSD(dir,copyTo);
                                        Toast.makeText(getApplication(),"We do not recommend saving apks of system apps as they may not work on other devices.",Toast.LENGTH_SHORT).show();
                                        // Code here executes on main thread after user presses button
                                    }
                                });
                                appsettings.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        Toast.makeText(getApplication(),"Be careful while modifying settings or clearing data of system as they can affect your system performance adversely.",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", name, null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        // Code here executes on main thread after user presses button
                                    }
                                });
                                openapp.setVisibility(View.GONE);
                                btnlyt.setWeightSum(3);

                            }
                            else{
                                appLabel.append(" (User App)");
                                saveapk.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        String copyTo = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append(File.separator).append("apk").append(File.separator).append(apkname).toString();
                                        onAppCopyToSD(dir,copyTo);
                                        // Code here executes on main thread after user presses button
                                    }
                                });
                                appsettings.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", name, null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        // Code here executes on main thread after user presses button
                                    }
                                });
                                if(name!=null)
                                {
                                    final Intent LaunchIntent = getPackageManager().getLeanbackLaunchIntentForPackage(p.applicationInfo.packageName);
                                    final Intent LaunchIntentl = getPackageManager().getLaunchIntentForPackage(p.applicationInfo.packageName);
                                    if(null!=LaunchIntent){
                                        openapp.setVisibility(View.VISIBLE);
                                        openapp.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {
                                                Toast.makeText(getApplication(),name,Toast.LENGTH_LONG).show();
                                                startActivity( LaunchIntent );
                                                // Code here executes on main thread after user presses button
                                            }
                                        });
                                        btnlyt.setWeightSum(4);
                                    }
                                    else if(null!=LaunchIntentl){

                                        openapp.setVisibility(View.VISIBLE);
                                        openapp.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {
                                                Toast.makeText(getApplication(),name,Toast.LENGTH_LONG).show();
                                                startActivity( LaunchIntentl );
                                                // Code here executes on main thread after user presses button
                                            }
                                        });
                                        btnlyt.setWeightSum(4);
                                    }
                                    else
                                        openapp.setVisibility(View.GONE);
                                }
                            }
                appLabel.append("\tTarget SDK: "+ p.applicationInfo.targetSdkVersion);
                            String asize=humanReadableByteCount(getFileSize(p.applicationInfo.publicSourceDir));
                            if(!asize.isEmpty())
appLabel.append("\tSize: "+asize);
                        }
            }
            playstore.setVisibility(View.VISIBLE);
            playstore.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent storeintent=new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + name));
                    try{
                        startActivity(storeintent);
                    }
                    catch (ActivityNotFoundException e){
                        Toast.makeText(getApplication(), "NO APPSTORE FOUND.", Toast.LENGTH_SHORT).show();
                        playstore.setVisibility(View.GONE);
                        btnlyt.setWeightSum(3);

                    }
                    // Code here executes on main thread after user presses button
                }
            });
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
    public void addActivityHeader() {
        tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT));
        int pad=15;
        tr.setPadding(0,pad,0,pad);

        /** Create a TableRow dynamically **/

        tv1=new TextView(this);
        tv1.setText("ACTIVITIES");
        tv1.setTypeface(Typeface.DEFAULT_BOLD);
        //tv1.setGravity(Gravity.CENTER);
        tv1.setTextSize(20);
        tv1.setPadding(10,10,10,10);
        tv1.setBackgroundResource(R.color.default_background);
//            iv1.setBackground(null);
        tr.addView(tv1);

        tl.addView(tr, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT));
    }
    PackageManager pm;
    public void addActivities(ActivityInfo []actnames) {
        pm=getPackageManager();
        for (final ActivityInfo activityInfo : actnames) {
            tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));


            /** Create a TableRow dynamically **/

            tv2 = new TextView(this);
            tv2.setText(activityInfo.name);
            tv2.setTypeface(Typeface.DEFAULT_BOLD);
            //tv2.setGravity(Gravity.CENTER);
            tv2.setTextSize(15);tv2.setPadding(15,0,0,0);
            tr.addView(tv2);
/*
            final Button b1=new Button(this);
            b1.setText("OPEN");
            b1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent i = pm.getLaunchIntentForPackage(activityInfo.name);
                    try{
                        startActivity(i);

                    }
                    catch (ActivityNotFoundException e){
                        Toast.makeText(getApplication(), "NOT FOUND", Toast.LENGTH_SHORT).show();
                    }
                    // Code here executes on main thread after user presses button
                }
            });
            tr.addView(b1);*/

            tl.addView(tr, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    public void addPermissionHeader() {
            tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            int pad=15;
            tr.setPadding(0,pad,0,pad);

            /** Create a TableRow dynamically **/

            tv1=new TextView(this);
            tv1.setText("PERMISSIONS GRANTED");
            tv1.setTypeface(Typeface.DEFAULT_BOLD);
            //tv1.setGravity(Gravity.CENTER);
            tv1.setTextSize(20);
            tv1.setPadding(10,10,10,10);
        tv1.setBackgroundResource(R.color.default_background);
//            iv1.setBackground(null);
            tr.addView(tv1);

            tl.addView(tr, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
    }

    public void addPermissions(String []pername) {
        for (String permname : pername) {
            tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));


            /** Create a TableRow dynamically **/

            tv2 = new TextView(this);
            tv2.setText(permname);
            tv2.setTypeface(Typeface.DEFAULT_BOLD);
            //tv2.setGravity(Gravity.CENTER);
            tv2.setTextSize(15);tv2.setPadding(15,0,0,0);
//            iv1.setBackground(null);
            tr.addView(tv2);

            tl.addView(tr, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    public void addRecieverHeader() {
        tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        int pad=15;
        tr.setPadding(0,pad,0,pad);

        /** Create a TableRow dynamically **/

        tv1=new TextView(this);
        tv1.setText("RECIVERS");
        tv1.setTypeface(Typeface.DEFAULT_BOLD);
        //tv1.setGravity(Gravity.CENTER);
        tv1.setTextSize(20);
        tv1.setPadding(10,10,10,10);
        tv1.setBackgroundResource(R.color.default_background);
//            iv1.setBackground(null);
        tr.addView(tv1);

        tl.addView(tr, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }
    public void addRecievers(ActivityInfo []actnames) {
        for (ActivityInfo activityInfo : actnames) {
            tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));


            /** Create a TableRow dynamically **/

            tv2 = new TextView(this);
            tv2.setText(activityInfo.name);
            if(activityInfo.permission!=null)tv2.append(" ("+activityInfo.permission+" )");
            tv2.setTypeface(Typeface.DEFAULT_BOLD);
            //tv2.setGravity(Gravity.CENTER);
            tv2.setTextSize(15);tv2.setPadding(15,0,0,0);
//            iv1.setBackground(null);
            tr.addView(tv2);

            tl.addView(tr, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    public void addServicesHeader() {
        tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        int pad=15;
        tr.setPadding(0,pad,0,pad);

        /** Create a TableRow dynamically **/

        tv1=new TextView(this);
        tv1.setText("SERVICES");
        tv1.setTypeface(Typeface.DEFAULT_BOLD);
        //tv1.setGravity(Gravity.CENTER);
        tv1.setTextSize(20);
        tv1.setPadding(10,10,10,10);
        tv1.setBackgroundResource(R.color.default_background);
//            iv1.setBackground(null);
        tr.addView(tv1);

        tl.addView(tr, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }
    public void addServices(ServiceInfo []actnames) {
        for (ServiceInfo activityInfo : actnames) {
            tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));


            /** Create a TableRow dynamically **/

            tv2 = new TextView(this);
            tv2.setText(activityInfo.name);
            if(activityInfo.permission!=null)tv2.append(" ("+activityInfo.permission+" )");
            tv2.setTypeface(Typeface.DEFAULT_BOLD);
            //tv2.setGravity(Gravity.CENTER);
            tv2.setTextSize(15);tv2.setPadding(15,0,0,0);
//            iv1.setBackground(null);
            tr.addView(tv2);

            tl.addView(tr, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        boolean handled = false;

        switch (keyCode){
            case KeyEvent.KEYCODE_MENU:
                if(name!=null){
                    Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(name);
                    startActivity( LaunchIntent );
                }
                else Toast.makeText(getApplication(),"Cannot Open app.",Toast.LENGTH_SHORT).show();


                handled=true;
                break;
        }
        return handled || super.onKeyDown(keyCode, event);
    }
    public String apkbuiltdate(PackageInfo p){
        try{
            ZipFile zf = new ZipFile(p.applicationInfo.sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            long time = ze.getTime();
            String s = getTime(time);
            zf.close();
            return s;
        }catch(Exception e){
            return null;
        }
    }

    static final int CopyFilesFinished = 1;

    private void onAppCopyToSD(String copyTo, String copyFrom){
        Toast.makeText(getApplication(),copyFrom+ "  "+ copyTo, Toast.LENGTH_LONG).show();

        File file = new File(dir);
        new File(copyTo).mkdirs();
        ArrayList<String> files = new ArrayList();
//                if (itemId != -1) {
        files.add(copyTo);
        files.add(copyFrom);
//                } else {
//                    for (int i = 0; i < this.listAdapter.getFiltered().size(); i += CopyFilesFinished) {
//                        int item = i;
//                        if (((APKInfoEntry) this.listAdapter.getFiltered().get(i)).isSelected()) {
//                            files.add(((APKInfoEntry) this.listAdapter.getFiltered().get(item)).getApkPath());
//                            files.add(new StringBuilder(String.valueOf(copyTo)).append(((APKInfoEntry) this.listAdapter.getFiltered().get(item)).getApkName()).toString());
//                        }
//                    }
//                }
        if (file.exists()) {
            progressDialog = ProgressDialog.show(this, "In Progress", "Copying files...", true, false);
            CopyFileTask copyFileTask = new CopyFileTask(this);
            ArrayList[] arrayListArr = new ArrayList[CopyFilesFinished];
            arrayListArr[0] = files;
            copyFileTask.execute(arrayListArr);
        }
    }
    public void onCopyDone() {
        progressDialog.dismiss();
    }
    private SimpleDateFormat mDateFormatter = new SimpleDateFormat("EE LLL dd yyyy kk:mm:ss");

    public String getTime(long time) {
        return mDateFormatter.format(new Date(time));
    }

}
