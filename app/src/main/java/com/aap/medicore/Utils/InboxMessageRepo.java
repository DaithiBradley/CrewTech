package com.aap.medicore.Utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.aap.medicore.DatabaseHandler.DatabaseHandler;
import com.aap.medicore.Models.InboxMessage;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Map;

public class InboxMessageRepo {

    private static InboxMessageRepo repo;

    private TinyDB tinyDB;
    private DatabaseHandler databaseHandler;
    private NotificationsHandler notificationsHandler;

    private InboxMessageRepo(Context context) {
        this.tinyDB = new TinyDB(context.getApplicationContext());
        this.databaseHandler = new DatabaseHandler(context);
        this.notificationsHandler=NotificationsHandler.getInstance(context);
    }

    public static InboxMessageRepo getInstance(Context context) {
        if (repo == null)
            repo = new InboxMessageRepo(context);
        return repo;
    }

    public int getUnreadMessagesCount(){
        return databaseHandler.hasUnreadMessages(getUsername());
    }

    public void checkAndRemoveExpired()
    {
        databaseHandler.checkAndRemoveMessagesExpiredBySentTime(getUsername(), 12);
    }

    public List<InboxMessage> getInboxMessages(){
        List<InboxMessage> result = databaseHandler.getInboxMessagesForUser(getUsername());
        return result;
    }

    public InboxMessage getInboxMessage(String id){
        InboxMessage result = databaseHandler.getInboxMessage(id);
        return result;
    }

    public InboxMessage createNewInboxMessage(@NonNull RemoteMessage remoteMessage)
    {
        Map<String, String> data = remoteMessage.getData();
        InboxMessage msg = new InboxMessage(remoteMessage.getMessageId(), data.get(Constants.message_title),
                data.get(Constants.message_content), remoteMessage.getSentTime(), 0);
        msg.username = getUsername();
        databaseHandler.insertMessage(msg);

        notificationsHandler.publishMessageNotification(msg);
        return msg;
    }

    public void removeInboxMessage(InboxMessage msg)
    {
        removeInboxMessage(msg.id);
        notificationsHandler.cancelNotification((int)msg.sentTime);
    }

    public void removeInboxMessage(String id)
    {
        databaseHandler.removeMessage(id);
    }

    public void setOpened(InboxMessage message)
    {
        message.isOpened = true;
        message.openTime = System.currentTimeMillis();
        databaseHandler.updateMessage(message);
        notificationsHandler.cancelNotification((int)message.sentTime);
    }

    public String getUsername()
    {
        return tinyDB.getString(Constants.username);
    }

}
