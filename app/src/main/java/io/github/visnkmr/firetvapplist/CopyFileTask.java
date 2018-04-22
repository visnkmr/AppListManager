package io.github.visnkmr.firetvapplist;

/**
 * Created by asus on 12/26/2017.
 */

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class CopyFileTask extends AsyncTask<ArrayList<String>, Long, Boolean> {
    String mMessage = null;
    Activity parentActivity;

    CopyFileTask(Activity _parent) {
        this.parentActivity = _parent;
    }

    protected Boolean doInBackground(ArrayList<String>... arg) {
        ArrayList<String> files = arg[0];
        for (int i = 0; i < files.size(); i += 2) {
//            Log.i("tset",(String) files.get(i)+ "podooooooooooooo"+(String) files.get(i + 1));
            copyFile((String) files.get(i), (String) files.get(i + 1));
        }
        return Boolean.valueOf(true);
    }

    protected void onProgressUpdate(Long... progress) {
    }

    protected void onPostExecute(Boolean result) {
        if (this.mMessage != null) {
            Toast.makeText(this.parentActivity, this.mMessage, Toast.LENGTH_SHORT).show();
        }
        ((AppDetails) this.parentActivity).onCopyDone();
    }

    protected boolean copyFile(String srcPath, String dstPath) {
        FileChannel srcChannel = null;
        FileChannel dstChannel = null;
        this.mMessage = null;
        try {
            srcChannel = new FileInputStream(srcPath).getChannel();
            dstChannel = new FileOutputStream(dstPath).getChannel();
            dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
            if (srcChannel != null) {
                try {
                    srcChannel.close();
                } catch (IOException e) {
                }
            }
            if (dstChannel != null) {
                try {
                    dstChannel.close();
                } catch (IOException e2) {
                }
            }
            this.mMessage = "file saved to "+dstPath;
            return true;
        } catch (IOException e3) {
            this.mMessage = "Failed to copy application package. Access denied";
            if (srcChannel != null) {
                try {
                    srcChannel.close();
                } catch (IOException e4) {
                }
            }
            if (dstChannel != null) {
                try {
                    dstChannel.close();
                } catch (IOException e5) {
                }
            }
            return false;
        } catch (Throwable th) {
            if (srcChannel != null) {
                try {
                    srcChannel.close();
                } catch (IOException e6) {
                }
            }
            if (dstChannel != null) {
                try {
                    dstChannel.close();
                } catch (IOException e7) {
                }
            }
            return false;
        }
    }
}

