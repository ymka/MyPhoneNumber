package com.ymka.myphonenumber.widget;

import com.ymka.myphonenumber.R;

/**
 * Created by Alexander Kondenko.
 */
public class WhiteWidgetProvider extends WidgetProvider {
    @Override
    protected int getLayoutWidgetId() {
        return R.layout.white_widget_main;
    }

    @Override
    protected int getLayoutDisabledWidgetId() {
        return R.layout.white_widget_buttons_disabled;
    }

    @Override
    protected Class<? extends WidgetProvider> getProviderClass() {
        return WhiteWidgetProvider.class;
    }
}
