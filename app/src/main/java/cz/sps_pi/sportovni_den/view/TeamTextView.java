package cz.sps_pi.sportovni_den.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.entity.Team;

/**
 * Created by Martin Forejt on 05.02.2017.
 * forejt.martin97@gmail.com
 */

public class TeamTextView extends TextView {

    public TeamTextView(Context context) {
        super(context);
    }

    public TeamTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Set team to view
     *
     * @param team Team
     */
    public void setTeam(Team team) {
        setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.team_icon));
        GradientDrawable gradientDrawable = (GradientDrawable) getBackground();
        gradientDrawable.setColor(ContextCompat.getColor(getContext(), team.getColor()));
        setText(team.getName());
        setTextColor(Color.WHITE);
    }

    /**
     * Set background color
     *
     * @param color color res id
     */
    public void setTeamColor(int color) {
        GradientDrawable gradientDrawable = (GradientDrawable) getBackground();
        gradientDrawable.setColor(color);
    }

    /**
     * View is for referee
     */
    public void isReferee() {
        GradientDrawable gradientDrawable = (GradientDrawable) getBackground();
        gradientDrawable.setColor(Color.WHITE);
        setText("R");
        setTextColor(Color.BLACK);
    }

    /**
     * View is for anonym user
     */
    public void isAnonym() {
        GradientDrawable gradientDrawable = (GradientDrawable) getBackground();
        gradientDrawable.setColor(Color.WHITE);
        setText("A");
        setTextColor(Color.BLACK);
    }

    /**
     * There is no team object
     */
    public void noTeam() {
        GradientDrawable gradientDrawable = (GradientDrawable) getBackground();
        gradientDrawable.setColor(Color.argb(0, 1, 1, 1));
        setText("x");
        setTextColor(Color.BLACK);
    }

}
