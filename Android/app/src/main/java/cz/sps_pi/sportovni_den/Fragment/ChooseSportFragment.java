package cz.sps_pi.sportovni_den.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.List;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.db.SportRepository;
import cz.sps_pi.sportovni_den.entity.Sport;
import cz.sps_pi.sportovni_den.util.SportAdapter;

/**
 * Created by Martin Forejt on 10.01.2017.
 * forejt.martin97@gmail.com
 */

public class ChooseSportFragment extends SportDenFragment {

    private String nextClass;
    private boolean nextActivity = false;
    private int position;

    public ChooseSportFragment() {

    }

    @SuppressWarnings("ValidFragment")
    public ChooseSportFragment(String title) {
        super(title);
    }

    public static ChooseSportFragment newInstance(String nextClass, String title, int position) {
        ChooseSportFragment fragment = new ChooseSportFragment(title);
        Bundle bundle = new Bundle();
        bundle.putString("nextClass", nextClass);
        bundle.putInt("position", position);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ChooseSportFragment newInstance(String nextClass, String title, int position, boolean nextActivity) {
        ChooseSportFragment fragment = new ChooseSportFragment(title);
        Bundle bundle = new Bundle();
        bundle.putString("nextClass", nextClass);
        bundle.putInt("position", position);
        bundle.putBoolean("nextActivity", nextActivity);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            nextClass = bundle.getString("nextClass");
            position = bundle.getInt("position");
            nextActivity = bundle.getBoolean("nextActivity");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_sport, container, false);

        SportRepository sportRepository = new SportRepository();
        List<Sport> sports = sportRepository.getActiveSports();

        final GridView gridView = (GridView) view.findViewById(R.id.choose_sport_grid);
        gridView.setAdapter(new SportAdapter(getContext(), sports));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Sport sport = (Sport) gridView.getItemAtPosition(i);
                if (!nextActivity) {
                    changeFragment(sport);
                } else {
                    startActivity(sport);
                }
            }
        });

        return view;
    }

    private void changeFragment(Sport sport) {
        try {
            Fragment fragment = (Fragment) Class.forName(nextClass).newInstance();
            Bundle bundle = new Bundle();
            bundle.putInt("sport", sport.getId());
            fragment.setArguments(bundle);
            getCallback().changeFragmentFromFragment(fragment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startActivity(Sport sport) {
        try {
            Intent intent = new Intent(getContext(), Class.forName(nextClass));
            intent.putExtra("sport", sport.getId());
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (SportDenFragment.getFragment(position, getCallback()) instanceof ChooseSportFragment)
            return false;
        getCallback().changePositionFromFragment(position, true);
        return true;
    }
}
