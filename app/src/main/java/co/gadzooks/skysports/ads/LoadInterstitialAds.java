package co.gadzooks.skysports.ads;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

public class LoadInterstitialAds extends AppCompatActivity {
    @Override
    protected void onChildTitleChanged(Activity childActivity, CharSequence title) {
        super.onChildTitleChanged(childActivity, title);
        InterstitialAds.loadInterAd(LoadInterstitialAds.this);
    }
}
