package cz.sps_pi.sportovni_den.db;

/**
 * Created by Martin Forejt on 20.01.2017.
 * forejt.martin97@gmail.com
 */

public class ManagerNotInitializedException extends IllegalStateException {

    public ManagerNotInitializedException() {
        super("DatabaseManager is not initialized, call initInstance.");
    }

}
