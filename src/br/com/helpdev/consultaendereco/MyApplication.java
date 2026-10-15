package br.com.helpdev.consultaendereco;

import android.app.Application;
import com.google.android.gms.ads.MobileAds;

public class MyApplication extends Application {
    public static final String TAG = "br.com.helpdev.ConsultaEnderecoV3";

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, initializationStatus -> { });
    }
}
