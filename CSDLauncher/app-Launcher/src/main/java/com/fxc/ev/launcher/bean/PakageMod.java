package com.fxc.ev.launcher.bean;

import android.graphics.drawable.Drawable;

public class PakageMod {
    public String pakageName;
    public String name;
    public Drawable icon;
    public Drawable editIcon;

    public PakageMod() {
        super();
    }

    public PakageMod(String pakageName, String name, Drawable icon, Drawable editIcon) {
        super();
        this.pakageName = pakageName;
        this.name = name;
        this.icon = icon;
        this.editIcon = editIcon;
    }
}
