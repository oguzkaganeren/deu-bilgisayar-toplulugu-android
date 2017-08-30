package com.example.oguz.bilgisayarToplulugu;

import java.util.Date;

/**
 * Created by Oguz on 29-Aug-17.
 */

public class EventsInfo {
        protected String address;
        protected String title;
        protected String description;
        protected Date date;
        protected String uid;
        protected String location;
        protected Long addingDate;

        public Date getAddingDate() {
                Long cDate=addingDate;
                Date dt=new Date(cDate*1000);
           return dt;
        }

}
