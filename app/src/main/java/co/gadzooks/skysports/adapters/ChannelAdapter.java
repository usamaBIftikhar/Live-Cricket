package co.gadzooks.skysports.adapters;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.squareup.picasso.Picasso;

import java.util.List;

import co.gadzooks.skysports.Details;
import co.gadzooks.skysports.R;
import co.gadzooks.skysports.ads.InterstitialAds;
import co.gadzooks.skysports.models.Channel;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ViewHolder> {
    List<Channel> channels;
    String type;

    public ChannelAdapter(List<Channel> channels, String type) {
        this.channels = channels;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View v;
        if(type.equals("slider")){
             v = LayoutInflater.from(parent.getContext()).inflate(R.layout.big_slider_view,parent,false);
        }else if(type.equals("details")){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.details_view,parent,false);
        }
        else {
            v = null;
            System.out.println("Not valid");
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull  ChannelAdapter.ViewHolder holder, int position) {
        holder.channelName.setText(channels.get(position).getTitle());
        Picasso.get().load(channels.get(position).getImage()).into(holder.channelImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InterstitialAds.loadInterAd(v.getContext());


                if (InterstitialAds.mInterstitialAd != null) {
                    InterstitialAds.mInterstitialAd.show((Activity) v.getContext());
                    InterstitialAds.mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            InterstitialAds.mInterstitialAd = null ;
                            InterstitialAds.loadInterAd(v.getContext());
                            Intent i = new Intent(v.getContext(), Details.class);
                            i.putExtra("channel",channels.get(position));
                            v.getContext().startActivity(i);

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                            Intent i = new Intent(v.getContext(), Details.class);
                            i.putExtra("channel",channels.get(position));
                            v.getContext().startActivity(i);
                        }
                    });
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView channelImage;
        TextView channelName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            channelImage = itemView.findViewById(R.id.channelThumbnail);
            channelName = itemView.findViewById(R.id.channelName);
        }
    }
}
