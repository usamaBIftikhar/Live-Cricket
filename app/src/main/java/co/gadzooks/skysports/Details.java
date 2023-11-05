package co.gadzooks.skysports;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import co.gadzooks.skysports.models.Channel;

public class Details extends AppCompatActivity {
    PlayerView playerView;
    ImageView fbLink,twtLink,ytLink,webLink;
    TextView description;
    ImageView fullScreen;
    boolean isFullScreen = false;
    SimpleExoPlayer player;
    ProgressBar progressBar;
    AdView bannerPlayer ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        AdRequest adRequest ;




        Channel channel = (Channel) getIntent().getSerializableExtra("channel");
        getSupportActionBar().setTitle(channel.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        playerView = findViewById(R.id.playerView);
        playerView.setKeepScreenOn(true);
        bannerPlayer = findViewById(R.id.bannerPlayer);
        fullScreen = playerView.findViewById(R.id.exo_fullscreen_icon);
        progressBar = findViewById(R.id.progressBar);
        adRequest = new AdRequest.Builder().build();
        bannerPlayer.loadAd(adRequest);
        fullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFullScreen){
                    bannerPlayer.loadAd(adRequest);
                    bannerPlayer.setVisibility(View.VISIBLE);

                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

                    if(getSupportActionBar() != null){
                        getSupportActionBar().show();
                    }

                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = (int) ( 200 * getApplicationContext().getResources().getDisplayMetrics().density);
                    playerView.setLayoutParams(params);

//                    Toast.makeText(Details.this, "We are Now going back to normal mode.", Toast.LENGTH_SHORT).show();
                    isFullScreen = false;                    
                }else {
                    bannerPlayer.destroy();
                    bannerPlayer.setVisibility(View.GONE);
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                    if(getSupportActionBar() != null){
                        getSupportActionBar().hide();
                    }

                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = params.MATCH_PARENT;
                    playerView.setLayoutParams(params);

//                    Toast.makeText(Details.this, "We are going to FullScreen Mode.", Toast.LENGTH_SHORT).show();
                    isFullScreen = true;
                }
            }
        });

        playChannel(channel.getLive_url());

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void playChannel(String live_url){
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory();
        HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory).
                createMediaSource(MediaItem.fromUri(live_url));
        player.setMediaSource(mediaSource);
        player.prepare();
        player.setPlayWhenReady(true);
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if(state == Player.STATE_READY){
                    progressBar.setVisibility(View.GONE);
                    player.setPlayWhenReady(true);
                }else if(state == Player.STATE_BUFFERING){
                    progressBar.setVisibility(View.VISIBLE);
                    playerView.setKeepScreenOn(true);
                }else {
                    progressBar.setVisibility(View.GONE);
                    player.setPlayWhenReady(true);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        player.seekToDefaultPosition();
        player.setPlayWhenReady(true);
    }

    @Override
    protected void onPause() {
        player.setPlayWhenReady(false);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        player.release();
        super.onDestroy();
    }
}