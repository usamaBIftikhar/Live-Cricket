package co.gadzooks.skysports;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.gadzooks.skysports.adapters.ChannelAdapter;
import co.gadzooks.skysports.models.Channel;
import co.gadzooks.skysports.services.ChannelDataService;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "TAG";
    RecyclerView bigSliderList,newsChannelList,sportsChannelList,enterChannelList;
    ChannelAdapter bigSliderAdapter,newsChannelAdapter,sportsChannelAdapter,enterChannelAdapter;
    List<Channel> channelList,newsChannels,sportsChannel,enterChannel;
    ChannelDataService service;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    ProgressBar progressBar2;
    AdView bannerHome ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });



        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        toggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        channelList = new ArrayList<>();
        service = new ChannelDataService(this);

        bigSliderList = findViewById(R.id.big_slider_list);
        progressBar2 = findViewById(R.id.progressBar2);
        bigSliderList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        bigSliderAdapter = new ChannelAdapter(channelList,"slider");
        bigSliderList.setAdapter(bigSliderAdapter);
        progressBar2.setVisibility(View.VISIBLE);
        getSliderData("https://livecricket.gadzookssolutions.com/fetchusers.php");
        bannerHome = findViewById(R.id.bannerHome);
        AdRequest adRequest = new AdRequest.Builder().build();
        bannerHome.loadAd(adRequest);


    }

    public void getSliderData(String url){
        service.getChannelData(url, new ChannelDataService.OnDataResponse() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                JSONArray matchObj = response.getJSONArray("matches") ;
                for(int i = 0; i<matchObj.length();i++){
                    try {
                        JSONObject channelData = matchObj.getJSONObject(i);
                        Channel c = new Channel();
                        c.setTitle(channelData.getString("title"));
                        c.setImage(channelData.getString("image"));
                        c.setLive_url(channelData.getString("URL"));


                        channelList.add(c);
                        bigSliderAdapter.notifyDataSetChanged();
                        progressBar2.setVisibility(View.GONE);


                        Log.d(TAG, "onResponse: " + c.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "onErrorResponse: " + error);
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if(item.getItemId() == R.id.home){

        }
        if(item.getItemId() == R.id.rate){
            final String appPackageName = getApplication().getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
        if(item.getItemId() == R.id.share){
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            StringBuilder sb = new StringBuilder();
            sb.append("Download this amazing App ");
            sb.append(getResources().getString(R.string.app_name));
            sb.append(" app from play store\n\n");
            String sb2 = sb.toString();
            StringBuilder sb3 = new StringBuilder();
            sb3.append(sb2);
            sb3.append("https://play.google.com/store/apps/details?id=");
            sb3.append(getPackageName());
            sb3.append("\n\n");
            intent.putExtra(Intent.EXTRA_TEXT, sb3.toString());
            startActivity(Intent.createChooser(intent, "Choose one"));
        }
        return false;
    }

}