package com.architjn.myapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.architjn.myapp.model.Chat;
import com.architjn.myapp.model.Contact;
import com.architjn.myapp.model.Conversation;
import com.architjn.myapp.model.UserProfile;
import com.architjn.myapp.xmpp.MessagePacketListener;
import com.architjn.myapp.xmpp.SmackInvocationException;

import java.util.ArrayList;

/**
 * Created by architjn on 10/27/2016.
 */

public class ConversationTable {

    public enum Type {
        IMAGE, VIDEO, TEXT
    }

    private static final String TABLE_NAME = "Conversation";

    private static final String ID = "conv_id";
    private static final String MESSAGE = "conv_msg";
    private static final String CHAT_ID = "conv_chat_id";
    private static final String SENDER_ID = "conv_sender_id";
    private static final String STARRED = "conv_starred";
    private static final String TIME = "conv_time";
    private static final String TYPE = "conv_type";

    public static void onCreate(SQLiteDatabase database) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( " +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MESSAGE + " TEXT," +
                CHAT_ID + " INTEGER," +
                SENDER_ID + " INTEGER," +
                STARRED + " INTEGER DEFAULT 0," +
                TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                TYPE + " INTEGER )";
        database.execSQL(SQL_CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database) {
    }

    static void addConversation(Context context, SQLiteDatabase db, Contact user, String msg, boolean sent)
            throws SmackInvocationException {
        String chatId = ChatTable.getChatId(db, ContactsTable.getUserWithMobile(context, db, user.getPhoneNumber(), false));
        ContentValues values = new ContentValues();
        values.putNull(ID);
        values.put(MESSAGE, msg);
        values.put(SENDER_ID, user.getPhoneNumber());
        values.put(CHAT_ID, chatId);
        values.put(STARRED, 0);
        if (sent) values.put(TYPE, 1);
        else values.put(TYPE, 0);
        db.insert(TABLE_NAME, null, values);
        ChatTable.updateTime(db, chatId);
        context.sendBroadcast(new Intent(MessagePacketListener.UPDATE_CHAT));
        context.sendBroadcast(new Intent(user.getPhoneNumber()));
    }

    public static ArrayList<Conversation> getAllConversation(SQLiteDatabase db, String chatId) {
        ArrayList<Conversation> conversations = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + CHAT_ID + " = ?" +
                " ORDER BY " + TIME, new String[]{chatId});
        if (c.moveToFirst()) {
            do {
                conversations.add(new Conversation(c.getString(c.getColumnIndex(ID)),
                        c.getString(c.getColumnIndex(MESSAGE)), c.getString(c.getColumnIndex(CHAT_ID)),
                        c.getString(c.getColumnIndex(SENDER_ID)), c.getString(c.getColumnIndex(STARRED)),
                        c.getString(c.getColumnIndex(TIME)), c.getInt(c.getColumnIndex(TYPE))));
            } while (c.moveToNext());
        }
        return conversations;
    }

    public static String getLastChatMsg(SQLiteDatabase db, String chatId) {
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + CHAT_ID +
                " = ? ORDER BY " + ID + " DESC LIMIT 1", new String[]{chatId});
        if (c.moveToFirst()) {
            return c.getString(c.getColumnIndex(MESSAGE));
        }
        return null;
    }

}
