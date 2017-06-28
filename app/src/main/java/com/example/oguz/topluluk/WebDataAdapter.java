package com.example.oguz.topluluk;

/**
 * Created by Oguz on 21-Jun-17.
 */
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

public class WebDataAdapter extends RecyclerView.Adapter<WebDataAdapter.ContentViewHolder> {

    private List<WebDataInfo> dataList;

    public WebDataAdapter(List<WebDataInfo> dataList) {
        this.dataList = dataList;
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onBindViewHolder(ContentViewHolder contentViewHolder, int i) {
        WebDataInfo wb = dataList.get(i);
        contentViewHolder.title.setText(wb.title);
        contentViewHolder.description.setText(wb.description);
        contentViewHolder.imgSrc.setImageDrawable(wb.imgSrc);
        contentViewHolder.date.setText(wb.date);
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
