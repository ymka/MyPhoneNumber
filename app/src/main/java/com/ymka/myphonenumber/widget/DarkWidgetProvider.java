package com.ymka.myphonenumber.widget;

import com.ymka.myphonenumber.R;

/**
 * Created by Alexander Kondenko.
 */
public class DarkWidgetProvider extends WidgetProvider {

    @Override
    protected int getLayoutWidgetId() {
        return R.layout.dark_widget_main;
    }

    @Override
    protected int getLayoutDisabledWidgetId() {
        return R.layout.dark_widget_buttons_disabled;
    }

}
