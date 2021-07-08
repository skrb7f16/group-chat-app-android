package com.skrb7f16.chatapp.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.skrb7f16.chatapp.MainActivity;
import com.skrb7f16.chatapp.R;
import com.skrb7f16.chatapp.SigninActivity;

public class LogoutAsk {
    int answer=1;
    Context context;
    Button yes,no;
    public LogoutAsk(Context context) {
        this.context=context;
    }
    public void showDialog(Activity activity){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_logout);
        Button yes=(Button)dialog.findViewById(R.id.logoutPositive);
        Button no=(Button)dialog.findViewById(R.id.logoutNegative);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllData();
                dialog.dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleLogout();
                dialog.dismiss();
            }
        });

        dialog.show();

    }




    public int getAnswer() {
        return answer;
    }




    public void clearAllData(){
        try {

            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear com.skrb7f16.chatapp");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void simpleLogout(){
        Intent intent=new Intent(this.context, SigninActivity.class);
        FirebaseAuth.getInstance().signOut();
        context.startActivity(intent);
        ((Activity) context).finish();
    }
}