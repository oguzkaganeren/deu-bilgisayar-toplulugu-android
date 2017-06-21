package com.example.oguz.topluluk;

/**
 * Created by Oguz on 21-Jun-17.
 */
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class WebDataAdapter extends RecyclerView.Adapter<WebDataAdapter.ContactViewHolder> {

    private List<WebDataInfo> dataList;

    public WebDataAdapter(List<WebDataInfo> dataList) {
        this.dataList = dataList;
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        WebDataInfo ci = dataList.get(i);
       // contactViewHolder.vName.setText(ci.name);
       // contactViewHolder.vSurname.setText(ci.surname);
       // contactViewHolder.vEmail.setText(ci.email);
       // contactViewHolder.vTitle.setText(ci.name + " " + ci.surname);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.fragment_content, viewGroup, false);

        return new ContactViewHolder(itemView);
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        protected TextView vName;
        protected TextView vSurname;
        protected TextView vEmail;
        protected TextView vTitle;

        public ContactViewHolder(View v) {
            super(v);
            /*vName =  (TextView) v.findViewById(R.id.txtName);
            vSurname = (TextView)  v.findViewById(R.id.txtSurname);
            vEmail = (TextView)  v.findViewById(R.id.txtEmail);
            vTitle = (TextView) v.findViewById(R.id.title);*/
        }
    }
}
