package com.architjn.myapp.xmpp;

import android.content.Context;
import android.util.Log;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;


public class PresencePacketListener implements PacketListener {

    private Context context;

    public PresencePacketListener(Context context) {
        this.context = context;
    }

    @Override
    public void processPacket(Packet packet) {
        Presence presence = (Presence) packet;
        Presence.Type presenceType = presence.getType();

        Log.v("Arr", "=======================");
        Log.v("Arr", StringUtils.parseBareAddress(presence.getFrom()));
        Log.v("Arr", String.valueOf(presenceType.ordinal()));
        Log.v("Arr", presence.getStatus() + " !!");
        Log.v("Arr", "=======================");
    }
}