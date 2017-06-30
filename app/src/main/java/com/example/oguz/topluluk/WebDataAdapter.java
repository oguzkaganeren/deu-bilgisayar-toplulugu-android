package com.example.oguz.topluluk;

/**
 * Created by Oguz on 21-Jun-17.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
        WebDataInfo wb = dataList.get(i);
        contentViewHolder.title.setText(wb.title);
        contentViewHolder.description.setText(wb.description);
        Picasso.with(context)
                .load(wb.imgSrc)
                .placeholder(R.drawable.progress_animation)
                .fit()
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

        public ContentViewHolder(View v) {
            super(v);
            title =  (TextView) v.findViewById(R.id.title);
            description = (TextView)  v.findViewById(R.id.descript);
            date = (TextView) v.findViewById(R.id.date);
            imgSrc=(ImageView)v.findViewById(R.id.thumbnail);
        }
    }
}
