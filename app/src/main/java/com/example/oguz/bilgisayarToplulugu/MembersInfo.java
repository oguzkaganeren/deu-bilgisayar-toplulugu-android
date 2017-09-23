package com.example.oguz.bilgisayarToplulugu;


/**
 * Created by Oguz on 07-Aug-17.
 */

public class MembersInfo implements Comparable<MembersInfo>{
    protected String nameSurname;
    protected String status;
    protected String last_login;
    protected String github;
    protected String linkedin;
    protected String googlePlay;
    protected String appStore;
    protected String instagram;
    protected String skype;
    protected String slack;
    protected String snap;
    protected String twitter;
    protected String whatsapp;
    protected String youtube;
    protected String facebook;
    protected String email;
    protected String uid;
    protected String website;
    protected String imgSrc;
    protected Boolean online;
    @Override
    public int compareTo(MembersInfo member) {
        //write code here for compare name
        return (member.online == online ? 0 : (online ? 1 : -1));
    }
}
