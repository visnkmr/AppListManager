package io.github.visnkmr.firetvapplist;

/**
 * Created by asus on 12/25/2017.
 */
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;

public abstract class PromptDialog extends Builder implements OnClickListener {
    private int id;
    private final EditText input;

    public abstract boolean onOkClicked(String str, int i);

    public PromptDialog(Context context, String title, String message, String defaultValue, String positiveTitle, String negativeTitle, int id) {
        super(context);
        setTitle(title);
        setMessage(message);
        this.input = new EditText(context);
        this.input.setText(defaultValue);
        setView(this.input);
        setPositiveButton(positiveTitle, this);
        setNegativeButton(negativeTitle, this);
        this.id = id;
    }

    public void onCancelClicked(DialogInterface dialog) {
        dialog.dismiss();
    }

    public void onClick(DialogInterface dialog, int which) {
        if (which != -1) {
            onCancelClicked(dialog);
        } else if (onOkClicked(this.input.getText().toString(), this.id)) {
            dialog.dismiss();
        }
    }
}
