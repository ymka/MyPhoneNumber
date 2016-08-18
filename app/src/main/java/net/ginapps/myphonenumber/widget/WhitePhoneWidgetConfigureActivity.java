package net.ginapps.myphonenumber.widget;

import net.ginapps.myphonenumber.R;

public class WhitePhoneWidgetConfigureActivity extends PhoneWidgetConfigureActivity {
    @Override
    protected int getWidgetLayoutId() {
        return R.layout.white_widget_main;
    }

    @Override
    protected Class<? extends WidgetProvider> getProviderClass() {
        return WhiteWidgetProvider.class;
    }
}
