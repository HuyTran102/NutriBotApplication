package com.huytran.goodlife.local_helper;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

public class LocaleHelper {
    public static Context updateContextLocale(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }
}
