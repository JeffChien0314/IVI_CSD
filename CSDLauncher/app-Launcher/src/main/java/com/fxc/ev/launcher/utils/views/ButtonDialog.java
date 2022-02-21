package com.fxc.ev.launcher.utils.views;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fxc.ev.launcher.R;

public class ButtonDialog extends Dialog implements View.OnClickListener {

    private final String title;
    private final onDialogButtonListener listener;
    private final Context context;

    private String btn_str = null;
    private boolean isForce = false;

    public ButtonDialog(Context c, String title, int icon, onDialogButtonListener listener,
                        int theme) {
        super(c, theme);
        this.context = c;
        this.title = title;
        this.listener = listener;
    }

    /*
     * (non-Javadoc)
     * @see android.app.Dialog#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.button_dialog);
        setWindowStyle();
        initViews();
    }

    // 设置窗口属性
    private void setWindowStyle() {
        Window w = getWindow();
        Resources res = context.getResources();
        Drawable drab = res.getDrawable(R.drawable.transparent);
        w.setBackgroundDrawable(drab);
        WindowManager.LayoutParams lp = w.getAttributes();
        final float scale = res.getDisplayMetrics().density;
        // In the mid-point to calculate the offset x and y
        lp.width = (int) (580 * scale + 0.5f);
        lp.height = (int) (292 * scale + 0.5f);
        w.setAttributes(lp);
    }

    public void initViews() {

        TextView textview;
        LinearLayout linearLayout;
        Button btn1, btn2;

        textview = (TextView) findViewById(R.id.dialog_title);
        linearLayout = (LinearLayout) findViewById(R.id.dialog_content);
        linearLayout.addView(listener.createContent());
        btn1 = (Button) findViewById(R.id.buttondialog_btn1);
        btn2 = (Button) findViewById(R.id.buttondialog_btn2);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        textview.setText(title);

        if (btn_str != null) {
            btn1.setText(btn_str);
        }
        if (isForce) {
            btn2.setVisibility(View.GONE);
            ButtonDialog.this.setCancelable(false);
        }
        if (btn2.getVisibility() == View.VISIBLE) {
            btn2.requestFocus();
        }
    }

    public void setButton(String string, boolean force) {
        btn_str = string;
        isForce = force;
    }

    public interface onDialogButtonListener {
        public View createContent();

        public void doComfirm();

        public void doCancel();

    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.buttondialog_btn1) {
            listener.doComfirm();
        } else if (v.getId() == R.id.buttondialog_btn2) {
            listener.doCancel();
        }
        ButtonDialog.this.dismiss();
    }

}
