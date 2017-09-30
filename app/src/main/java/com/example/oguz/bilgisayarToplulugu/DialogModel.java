package com.example.oguz.bilgisayarToplulugu;

import com.stfalcon.chatkit.commons.models.IDialog;

import java.util.ArrayList;

/**
 * Created by Oguz on 30-Sep-17.
 */

/*
 * Created by troy379 on 04.04.17.
 */
public class DialogModel implements IDialog<MessageModel> {

    private String id;
    private String dialogPhoto;
    private String dialogName;
    private ArrayList<UserModel> users;
    private MessageModel lastMessage;

    private int unreadCount;

    public DialogModel(String id, String name, String photo,
                  ArrayList<UserModel> users, MessageModel lastMessage, int unreadCount) {

        this.id = id;
        this.dialogName = name;
        this.dialogPhoto = photo;
        this.users = users;
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDialogPhoto() {
        return dialogPhoto;
    }

    @Override
    public String getDialogName() {
        return dialogName;
    }

    @Override
    public ArrayList<UserModel> getUsers() {
        return users;
    }

    @Override
    public MessageModel getLastMessage() {
        return lastMessage;
    }

    @Override
    public void setLastMessage(MessageModel lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
