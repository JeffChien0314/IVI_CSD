package com.fxc.ev.launcher.maps.search;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import com.fxc.ev.launcher.R;
import com.fxc.ev.launcher.activities.LauncherActivity;

public class FavoriteBaseDialog extends AlertDialog {
    private View mRootView;
    private AlertDialog mDialog;
    private ImageView dialogIcon;
    private EditText dialogEditText;
    private ImageView dialogClearAll;
    private ImageView dialogHomeIcon;
    private ImageView dialogOfficeIcon;
    private ImageView dialogStarIcon;
    private ImageView dialogHeartIcon;
    private ImageView dialogPinIcon;
    private Button dialogDoneBtn;
    private Button dialogCancelBtn;
    private ImageView dialogDeleteBtn;
    private FavEditItem mFavEditItem;
    private InputMethodManager mInputMethodManager;
    private LauncherActivity mLauncherActivity;

    protected FavoriteBaseDialog(LauncherActivity launcherActivity, FavEditItem favEditItem) {
        super(launcherActivity);
        mLauncherActivity = launcherActivity;
        mRootView = LayoutInflater.from(mLauncherActivity).inflate(R.layout.fav_item_edit_dialog, null, false);
        mInputMethodManager = (InputMethodManager) mLauncherActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        initDialogView();
        initDialogIcon(favEditItem);
        initEditText(favEditItem);
    }

    public void initDialogView() {
        mDialog = new AlertDialog.Builder(mLauncherActivity).setView(mRootView).create();
        Window dialogWindow = mDialog.getWindow();
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);

        dialogIcon = mRootView.findViewById(R.id.dialog_fav_icon);
        dialogEditText = mRootView.findViewById(R.id.dialog_fav_edit_text);
        dialogClearAll = mRootView.findViewById(R.id.dialog_fav_clear_all);

        dialogHomeIcon = mRootView.findViewById(R.id.dialog_home_icon);
        dialogOfficeIcon = mRootView.findViewById(R.id.dialog_office_icon);
        dialogStarIcon = mRootView.findViewById(R.id.dialog_star_icon);
        dialogHeartIcon = mRootView.findViewById(R.id.dialog_heart_icon);
        dialogPinIcon = mRootView.findViewById(R.id.dialog_pin_icon);

        dialogDoneBtn = mRootView.findViewById(R.id.dialog_btn_done);
        dialogCancelBtn = mRootView.findViewById(R.id.dialog_btn_cancel);
        dialogDeleteBtn = mRootView.findViewById(R.id.dialog_delete);
    }

    private void initDialogIcon(FavEditItem favEditItem) {
        if (favEditItem.getImage() == 0) {
            favEditItem.setImage(R.drawable.icon_star_normal);
        }
        dialogIcon.setImageResource(favEditItem.getImage());
        dialogIcon.setTag(favEditItem.getImage());
        setDialogIconBg(favEditItem.getImage());

        dialogHomeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogIcon.setImageResource(R.drawable.icon_home_normal);
                dialogIcon.setTag(R.drawable.icon_home_normal);
                setDialogIconBg(R.drawable.icon_home_normal);
                //favEditItem.setImage(R.drawable.icon_home_normal);
            }
        });
        dialogOfficeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogIcon.setImageResource(R.drawable.icon_office_normal);
                dialogIcon.setTag(R.drawable.icon_office_normal);
                setDialogIconBg(R.drawable.icon_office_normal);
            }
        });
        dialogStarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogIcon.setImageResource(R.drawable.icon_star_normal);
                dialogIcon.setTag(R.drawable.icon_star_normal);
                setDialogIconBg(R.drawable.icon_star_normal);
            }
        });
        dialogHeartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogIcon.setImageResource(R.drawable.icon_heart_normal);
                dialogIcon.setTag(R.drawable.icon_heart_normal);
                setDialogIconBg(R.drawable.icon_heart_normal);
            }
        });
        dialogPinIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogIcon.setImageResource(R.drawable.icon_pin_normal);
                dialogIcon.setTag(R.drawable.icon_pin_normal);
                setDialogIconBg(R.drawable.icon_pin_normal);
            }
        });
    }

    private void initEditText(FavEditItem favEditItem) {
        dialogEditText.setText(favEditItem.getName());
        dialogEditText.requestFocus();
        dialogEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(dialogEditText.getText())) {
                    dialogClearAll.setVisibility(View.GONE);
                    dialogDoneBtn.setFocusable(false);
                    dialogDoneBtn.setBackgroundResource(R.drawable.dialog_btn_disable_bg);
                } else {
                    dialogClearAll.setVisibility(View.VISIBLE);
                    dialogDoneBtn.setFocusable(true);
                    dialogDoneBtn.setBackgroundResource(R.drawable.dialog_btn_select_bg);
                }
            }
        });

        dialogClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEditText.setText("");
                dialogClearAll.setVisibility(View.GONE);
                dialogDoneBtn.setFocusable(false);
                dialogDoneBtn.setBackgroundResource(R.drawable.dialog_btn_disable_bg);
            }
        });

        setDoneBtnClickListener(favEditItem);
        setCancelBtnClickListener();
        setDeleteBtnClickListener(favEditItem);
    }

    private void setDoneBtnClickListener(FavEditItem favEditItem) {
        dialogDoneBtn.setBackgroundResource(R.drawable.dialog_btn_select_bg);
        dialogDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogDoneBtn.isFocusable()) {
                    favEditItem.setImage((int) dialogIcon.getTag());
                    favEditItem.setName(dialogEditText.getText().toString());
                    mDialog.dismiss();
                    hideSoftInput();
                    processDoneEvent();
                }
            }
        });
    }

    private void setCancelBtnClickListener() {
        dialogCancelBtn.setBackgroundResource(R.drawable.transparent);
        dialogCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDoneBtn.setBackgroundResource(R.drawable.transparent);
                dialogCancelBtn.setBackgroundResource(R.drawable.dialog_btn_select_bg);
                mDialog.dismiss();
                hideSoftInput();
            }
        });
    }

    private void setDeleteBtnClickListener(FavEditItem favEditItem) {
        dialogDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processDeleteEvent(favEditItem);
            }
        });
    }

    public void setDeleteBtnVisibility(int visibility) {
        dialogDeleteBtn.setVisibility(visibility);
    }

    public void processDoneEvent() {

    }

    public void processDeleteEvent(FavEditItem favEditItem) {

    }

    private void setDialogIconBg(int resId) {
        switch (resId) {
            case R.drawable.icon_home_normal:
                dialogHomeIcon.setBackgroundResource(R.drawable.dialog_fav_icon_select_bg);
                dialogOfficeIcon.setBackgroundResource(R.drawable.transparent);
                dialogStarIcon.setBackgroundResource(R.drawable.transparent);
                dialogHeartIcon.setBackgroundResource(R.drawable.transparent);
                dialogPinIcon.setBackgroundResource(R.drawable.transparent);
                break;
            case R.drawable.icon_office_normal:
                dialogHomeIcon.setBackgroundResource(R.drawable.transparent);
                dialogOfficeIcon.setBackgroundResource(R.drawable.dialog_fav_icon_select_bg);
                dialogStarIcon.setBackgroundResource(R.drawable.transparent);
                dialogHeartIcon.setBackgroundResource(R.drawable.transparent);
                dialogPinIcon.setBackgroundResource(R.drawable.transparent);
                break;
            case R.drawable.icon_star_normal:
                dialogHomeIcon.setBackgroundResource(R.drawable.transparent);
                dialogOfficeIcon.setBackgroundResource(R.drawable.transparent);
                dialogStarIcon.setBackgroundResource(R.drawable.dialog_fav_icon_select_bg);
                dialogHeartIcon.setBackgroundResource(R.drawable.transparent);
                dialogPinIcon.setBackgroundResource(R.drawable.transparent);
                break;
            case R.drawable.icon_heart_normal:
                dialogHomeIcon.setBackgroundResource(R.drawable.transparent);
                dialogOfficeIcon.setBackgroundResource(R.drawable.transparent);
                dialogStarIcon.setBackgroundResource(R.drawable.transparent);
                dialogHeartIcon.setBackgroundResource(R.drawable.dialog_fav_icon_select_bg);
                dialogPinIcon.setBackgroundResource(R.drawable.transparent);
                break;
            case R.drawable.icon_pin_normal:
                dialogHomeIcon.setBackgroundResource(R.drawable.transparent);
                dialogOfficeIcon.setBackgroundResource(R.drawable.transparent);
                dialogStarIcon.setBackgroundResource(R.drawable.transparent);
                dialogHeartIcon.setBackgroundResource(R.drawable.transparent);
                dialogPinIcon.setBackgroundResource(R.drawable.dialog_fav_icon_select_bg);
                break;
        }
    }

    public void hideSoftInput() {
        if (mInputMethodManager != null && mInputMethodManager.isActive()) {
            mInputMethodManager.hideSoftInputFromWindow(mLauncherActivity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    public void showDialog() {
        mDialog.show();
    }

    public void dismissDialog() {
        mDialog.dismiss();
    }

}
