package snoof.widget.manager;

import android.graphics.drawable.Drawable;

public class WidgetApp {
    String name;
    String packageName;
    Drawable icon;

    public WidgetApp(String name, String packageName, Drawable icon) {
        this.name = name;
        this.packageName = packageName;
        this.icon = icon;
    }
}