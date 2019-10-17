package com.bid.launcherwatch;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

public class TargetDialog extends Dialog {
    private final String STEP_TARGET = "step_target";
    ImageView rate;

    public TargetDialog(Context context) {
        super(context);
        requestWindowFeature(1);
        getWindow().setType(2003);
        setContentView(R.layout.pup_target_rate);
        getWindow().setBackgroundDrawableResource(R.color.alph);
        this.rate = (ImageView) findViewById(R.id.iv_target);
        this.rate.setImageResource(R.drawable.target_100);
//        this.rate.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                TargetDialog.this.dismiss();
//            }
//        });


    }



    public void show() {
        super.show();
        LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = 80;
        layoutParams.width = -1;
        layoutParams.height = -2;
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);
    }
}


