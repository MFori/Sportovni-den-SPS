package cz.sps_pi.sportovni_den.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import cz.sps_pi.sportovni_den.db.DBHelper;
import cz.sps_pi.sportovni_den.db.DatabaseManager;
import cz.sps_pi.sportovni_den.db.ManagerNotInitializedException;
import cz.sps_pi.sportovni_den.db.RequestRepository;
import cz.sps_pi.sportovni_den.listener.RequestListener;
import cz.sps_pi.sportovni_den.util.Error;
import cz.sps_pi.sportovni_den.util.Request;
import cz.sps_pi.sportovni_den.util.RequestManager;
import cz.sps_pi.sportovni_den.util.Response;

/**
 * Created by Martin Forejt on 14.01.2017.
 * forejt.martin97@gmail.com
 */

public class UploadDataService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            DatabaseManager.getInstance();
        } catch (ManagerNotInitializedException e) {
            DatabaseManager.initInstance(new DBHelper(getApplicationContext()));
        }

        final RequestRepository repository = new RequestRepository();
        List<Request> requests = repository.getRequest();
        Log.d("size", String.valueOf(requests.size()));
        for (Request r : requests) {
            final Request request = r;
            RequestManager.createRequest(r)
                    .setListener(new RequestListener() {
                        @Override
                        public void onRequestError(Response response) {
                            if (!response.hasError(Error.NO_SERVER))
                                repository.deleteRequest(request);
                        }

                        @Override
                        public void onRequestSuccess(Response response) {
                            repository.deleteRequest(request);
                        }

                        @Override
                        public void onNoConnection(boolean saved) {

                        }
                    }).execute();
        }

        return super.onStartCommand(intent, flags, startId);
    }
}
