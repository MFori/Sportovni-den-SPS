package cz.sps_pi.sportovni_den.service;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import cz.sps_pi.sportovni_den.db.DBHelper;
import cz.sps_pi.sportovni_den.db.DatabaseManager;
import cz.sps_pi.sportovni_den.db.ManagerNotInitializedException;
import cz.sps_pi.sportovni_den.db.MessageRepository;
import cz.sps_pi.sportovni_den.entity.Message;
import cz.sps_pi.sportovni_den.entity.User;
import cz.sps_pi.sportovni_den.util.LoginManager;
import cz.sps_pi.sportovni_den.util.NotificationsManager;

/**
 * Created by Martin Forejt on 29.01.2017.
 * forejt.martin97@gmail.com
 */

public class MessagesService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("tag", "From: " + remoteMessage.getFrom());
        Log.d("tag", "Message data payload: " + remoteMessage.getData());

        Map<String, String> data = remoteMessage.getData();
        Message message = new Message();
        message.setId(Integer.parseInt(data.get("id")));
        message.setTitle(data.get("title"));
        message.setMessage(data.get("text"));
        message.setSender(Integer.parseInt(data.get("sender")));

        User user = LoginManager.getUser();

        if (user != null && user.getId() != message.getSender()) {
            try {
                DatabaseManager.getInstance();
            } catch (ManagerNotInitializedException e) {
                DatabaseManager.initInstance(new DBHelper(getApplicationContext()));
            }
            MessageRepository repository = new MessageRepository();
            repository.addMessage(message);
            NotificationsManager.showNotification(message, this);
        }
    }
}
