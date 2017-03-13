package cz.sps_pi.sportovni_den.Fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.activity.MainActivity;
import cz.sps_pi.sportovni_den.entity.User;

public class MenuFragment extends SportDenFragment implements View.OnClickListener {

    public MenuFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        if (getUser().getType() != User.TYPE_REFEREE) {
            hideRefereeItems(view);
        } else {
            view.findViewById(R.id.menu_item_results_ref).setOnClickListener(this);
            view.findViewById(R.id.menu_item_notifications).setOnClickListener(this);
        }

        view.findViewById(R.id.menu_item_results).setOnClickListener(this);
        view.findViewById(R.id.menu_item_rules).setOnClickListener(this);
        view.findViewById(R.id.menu_item_messages).setOnClickListener(this);
        view.findViewById(R.id.menu_item_settings).setOnClickListener(this);

        return view;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getCallback().changePositionFromFragment(MainActivity.POSITION_MENU, true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_item_results:
                getCallback().changePositionFromFragment(MainActivity.POSITION_RESULTS, false);
                break;
            case R.id.menu_item_rules:
                getCallback().changePositionFromFragment(MainActivity.POSITION_RULES, false);
                break;
            case R.id.menu_item_messages:
                getCallback().changePositionFromFragment(MainActivity.POSITION_MESSAGES, false);
                break;
            case R.id.menu_item_settings:
                getCallback().changePositionFromFragment(MainActivity.POSITION_SETTINGS, false);
                break;
            case R.id.menu_item_results_ref:
                getCallback().changePositionFromFragment(MainActivity.POSITION_RESULTS_REF, false);
                break;
            case R.id.menu_item_notifications:
                getCallback().changePositionFromFragment(MainActivity.POSITION_NOTIFICATIONS, false);
                break;
        }
    }

    private void hideRefereeItems(View parent) {
        LinearLayout layout = (LinearLayout) parent.findViewById(R.id.menu_group_referee);
        layout.setVisibility(View.INVISIBLE);
    }

}
