package net.ginapps.myphonenumber.widget;

import net.ginapps.myphonenumber.R;

public class DarkPhoneWidgetConfigureActivity extends PhoneWidgetConfigureActivity {
    @Override
    protected int getWidgetLayoutId() {
        return R.layout.dark_widget_main;
    }

    @Override
    protected Class<? extends WidgetProvider> getProviderClass() {
        return DarkWidgetProvider.class;
    }
}
