package com.example.oguz.bilgisayarToplulugu;

/**
 * Created by Oguz on 21-Jun-17.
 */
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class WebDataAdapter extends RecyclerView.Adapter<WebDataAdapter.ContentViewHolder> {
    private Context context;
    private List<WebDataInfo> dataList;
    public WebDataAdapter(Context context, List<WebDataInfo> dataList) {
        this.context=context;
        this.dataList = dataList;
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onBindViewHolder(final ContentViewHolder contentViewHolder, int i) {
        final WebDataInfo wb = dataList.get(i);
        contentViewHolder.title.setText(wb.title);
        contentViewHolder.description.setText(wb.description);
        contentViewHolder.urlS=wb.link;
        contentViewHolder.source.setText("Source:"+wb.source);
        Glide.with(((Activity) context).getApplication().getApplicationContext())
                .load(wb.imgSrc)
                .centerCrop()
                .into(contentViewHolder.imgSrc);
        String cDate=wb.date;
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.getDefault());
        try {
            format.setLenient(false);
            Date date = format.parse(cDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            String formatedDate = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" +         cal.get(Calendar.YEAR);
            contentViewHolder.date.setText(formatedDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            contentViewHolder.date.setText(wb.date);
        }
        contentViewHolder.theCard.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                   /*Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlS));
                    v.getContext().startActivity(myIntent);*/
                //there is a problem

                /*WebView mWebView =new WebView(context);
                mWebView.getSettings().setJavaScriptEnabled(true);
                mWebView.loadUrl(wb.link);*/
                String url = wb.link;
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.enableUrlBarHiding();
                builder.setShowTitle(true);
                builder.setCloseButtonIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow_back_black_24dp));
                builder.setToolbarColor(context.getResources().getColor(R.color.md_yellow_500));
                builder.setStartAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                builder.setExitAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |  Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                customTabsIntent.launchUrl(context, Uri.parse(url));


            }
        });

    }

    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.fragment_content, viewGroup, false);
        return new ContentViewHolder(itemView);
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder {
        protected TextView title;
        protected TextView description;
        protected TextView date;
        protected ImageView imgSrc;
        protected String urlS;
        protected TextView source;
        protected CardView theCard;
        public ContentViewHolder(View v) {
            super(v);
            title =  (TextView) v.findViewById(R.id.title);
            description = (TextView)  v.findViewById(R.id.descript);
            date = (TextView) v.findViewById(R.id.date);
            imgSrc=(ImageView)v.findViewById(R.id.thumbnail);
            source=(TextView)v.findViewById(R.id.source);
            theCard=(CardView)v.findViewById(R.id.card_view);

        }

    }
}
