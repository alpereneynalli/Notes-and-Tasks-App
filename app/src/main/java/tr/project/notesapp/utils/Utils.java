package tr.project.notesapp.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import java.text.SimpleDateFormat;
import java.util.Date;

import tr.project.notesapp.R;
import tr.project.notesapp.activities.LoginActivity;

public class Utils {

    public static void signedUpDialog(Context context, String message) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.signedupdialog);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialogbox);
        AppCompatButton ok = dialog.findViewById(R.id.signup_ok_button);
        TextView dialogText = dialog.findViewById(R.id.dialog_text);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Intent intent = new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialogText.setText(message);

        dialog.show();


    }

    public static Dialog deleteDialog(Context context, String message) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.deletedialog);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialogbox);
        TextView dialogText = dialog.findViewById(R.id.delete_dialog_text);
        dialogText.setText(message);

        return dialog;
    }

    public static String dateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy hh:mm a");
        String strDate = formatter.format(date);
        return strDate;
    }

}
