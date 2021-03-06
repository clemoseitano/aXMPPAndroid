package com.architjn.myapp.xmpp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.architjn.myapp.database.DbHelper;
import com.architjn.myapp.model.Contact;
import com.architjn.myapp.model.UserProfile;
import com.architjn.myapp.ui.activity.ConversationActivity;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

public class MessagePacketListener implements PacketListener {
    public static final String UPDATE_CHAT = "update_chat";
    private Context context;

    public MessagePacketListener(Context context) {
        this.context = context;
    }

    @Override
    public void processPacket(Packet packet) {
        Message msg = (Message) packet;
        try {
            UserProfile user = XMPPHelper.getInstance(context).search(StringUtils.parseName(msg.getFrom()));
            Contact contact = DbHelper.getInstance(context)
                    .getContactWithNumber(StringUtils.parseName(msg.getFrom()));
            DbHelper.getInstance(context).addConversation(contact, msg.getBody(), false);
        } catch (SmackInvocationException e) {
            e.printStackTrace();
        }
    }
//
//	private void processPacketExtension(Intent intent, Message msg) {
//		Collection<PacketExtension> extensions = msg.getExtensions();
//		if (extensions != null) {
//			Iterator<PacketExtension> iterator = extensions.iterator();
//			if (iterator.hasNext()) {
//				PacketExtension extension = iterator.next();
//				if (extension instanceof UserLocation) {
//					intent.putExtra(MessageService.EXTRA_DATA_NAME_LOCATION, (UserLocation)extension);
//					intent.putExtra(MessageService.EXTRA_DATA_NAME_TYPE, ChatMessageTableHelper.TYPE_INCOMING_LOCATION);
//				}
//			}
//		}
//	}
}