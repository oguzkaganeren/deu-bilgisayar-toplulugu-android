package com.example.oguz.topluluk;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Oguz on 29-Aug-17.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventsViewHolder>{
    private Context context;
    private List<EventsInfo> dataList;
    public EventAdapter(Context context, List<EventsInfo> dataList) {
        this.context=context;
        this.dataList = dataList;
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onBindViewHolder(final EventAdapter.EventsViewHolder eventsViewHolder, int i) {
        EventsInfo wb = dataList.get(i);
        eventsViewHolder.address.setText(wb.address);
        eventsViewHolder.title.setText(wb.title);
        eventsViewHolder.description.setText(wb.description);
        eventsViewHolder.date.setText(wb.date.toString());
        Glide.with(context)
                .load(R.drawable.event)
                .centerCrop()
                .into(eventsViewHolder.image);


    }

    @Override
    public EventAdapter.EventsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.fragment_event, viewGroup, false);
        return new EventAdapter.EventsViewHolder(itemView);
    }
    public static class EventsViewHolder extends RecyclerView.ViewHolder {
        protected TextView address;
        protected TextView title;
        protected TextView description;
        protected TextView date;
        protected ImageView image;
        public EventsViewHolder(View v) {
            super(v);
            address =  (TextView) v.findViewById(R.id.address_event_frag);
            title = (TextView)  v.findViewById(R.id.title_event_frag);
            description = (TextView) v.findViewById(R.id.description_event_frag);
            date = (TextView) v.findViewById(R.id.date_event_frag);
            image=(ImageView)v.findViewById(R.id.thumbnail_event_frag);
            //image üzerine uzun süre basıldığında yapılacaklar...
        }

    }


}

