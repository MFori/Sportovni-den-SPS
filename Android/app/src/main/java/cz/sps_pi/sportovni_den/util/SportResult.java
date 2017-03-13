package cz.sps_pi.sportovni_den.util;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import cz.sps_pi.sportovni_den.entity.Sport;

/**
 * Created by Martin Forejt on 19.01.2017.
 * forejt.martin97@gmail.com
 */

public abstract class SportResult implements Serializable {

    public static final int TYPE_GROUP_FINALE = 1;
    public static final int TYPE_GROUP_GROUP = 2;
    public static final int TYPE_ROBIN = 3;
    public static final int TYPE_INDIVIDUALS = 4;

    protected int scoring;
    private Sport sport;

    public int getScoring() {
        return scoring;
    }

    public void setScoring(int scoring) {
        this.scoring = scoring;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public static SportResult fromResponse(Response response) {
        SportResult result = null;
        try {
            int type = response.getData().getInt("type");

            switch (type) {
                case TYPE_GROUP_FINALE:
                case TYPE_GROUP_GROUP:
                case TYPE_ROBIN:
                    result = new TeamSportResult();
                    break;
                case TYPE_INDIVIDUALS:
                    result = new IndividualSportResult();
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (result != null) {
            try {
                result.scoring = response.getData().getInt("type");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            result.fromJSON(response.getData("results"));
        }

        return result;
    }

    protected abstract void fromJSON(JSONObject json);
}
