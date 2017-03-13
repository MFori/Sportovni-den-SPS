package cz.sps_pi.sportovni_den.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.db.PerformanceRepository;
import cz.sps_pi.sportovni_den.db.SportRepository;
import cz.sps_pi.sportovni_den.entity.Performance;
import cz.sps_pi.sportovni_den.entity.Sport;
import cz.sps_pi.sportovni_den.entity.Team;
import cz.sps_pi.sportovni_den.listener.RequestListener;
import cz.sps_pi.sportovni_den.util.RequestManager;
import cz.sps_pi.sportovni_den.util.Response;
import cz.sps_pi.sportovni_den.util.Route;
import cz.sps_pi.sportovni_den.view.TeamTextView;

/**
 * Created by Martin Forejt on 20.01.2017.
 * forejt.martin97@gmail.com
 */

public class IndividualRefereeFragment extends ResultsRefFragment implements SwipeRefreshLayout.OnRefreshListener {

    private boolean fromDetail = false;
    private MyAdapter adapter;
    private Sport sport;
    private List<Performance> performances = new ArrayList<>();
    private SwipeRefreshLayout refresh;
    private TextView emptyMessage;

    public static IndividualRefereeFragment newInstance(int sport) {
        IndividualRefereeFragment fragment = new IndividualRefereeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("sport", sport);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            SportRepository sportRepository = new SportRepository();
            sport = sportRepository.getSport(bundle.getInt("sport"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_individual_referee_list, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.individual_referee_listView);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.individual_referee_swipe_layout);
        final FloatingActionButton newBtn = (FloatingActionButton) view.findViewById(R.id.individual_referee_add_btn);

        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(IndividualRefereeAddFragment.newInstance());
            }
        });

        refresh.setOnRefreshListener(this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        adapter = new MyAdapter(performances);
        recyclerView.setAdapter(adapter);

        emptyMessage = (TextView) view.findViewById(R.id.individual_referee_empty_message);

        DbLoader loader = new DbLoader();
        loader.execute(getSport());

        if (!fromDetail) {
            new Thread(new WebLoader()).start();
            refresh.setRefreshing(true);
            fromDetail = true;
        }

        return view;
    }

    private void show(List<Performance> performances) {
        if (performances.size() > 0) emptyMessage.setVisibility(View.GONE);
        this.performances.clear();
        this.performances.addAll(performances);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        new Thread(new WebLoader()).start();
    }

    private class DbLoader extends AsyncTask<Sport, Void, List<Performance>> {
        @Override
        protected List<Performance> doInBackground(Sport... sports) {
            PerformanceRepository repository = new PerformanceRepository();
            return repository.getPerformances(sports[0]);
        }

        @Override
        protected void onPostExecute(List<Performance> performances) {
            show(performances);
        }
    }

    private class WebLoader implements Runnable {
        @Override
        public void run() {
            RequestManager.createRequest(Route.get(Route.GET_PERFORMANCES))
                    .addParameter("sport", sport.getId())
                    .addParameter("order", "id")
                    .setListener(new RequestListener() {
                        @Override
                        public void onRequestError(Response response) {
                            refresh.setRefreshing(false);
                        }

                        @Override
                        public void onRequestSuccess(Response response) {
                            final List<Performance> performances = fromResponse(response);
                            saveToDb(performances);

                            if (canAccessUi())
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        show(performances);
                                        refresh.setRefreshing(false);
                                    }
                                });
                        }

                        @Override
                        public void onNoConnection(boolean saved) {
                            refresh.setRefreshing(false);
                        }
                    }).execute();
        }

        private List<Performance> fromResponse(Response response) {
            List<Performance> performances = new ArrayList<>();
            Log.d("data", response.getRawData());
            JSONArray arrPerformances = response.getArray("performances");
            if (arrPerformances == null) return performances;
            for (int i = 0; i < arrPerformances.length(); i++) {
                try {
                    Performance performance = new Performance();
                    JSONObject perfO = arrPerformances.getJSONObject(i);
                    JSONObject team0 = perfO.getJSONObject("team");
                    JSONObject sportO = perfO.getJSONObject("sport");

                    performance.setId(perfO.getInt("id"));
                    performance.setTeam(Team.getById(team0.getInt("id")));
                    performance.setSport(new Sport(
                            sportO.getInt("id"),
                            sportO.getString("name"),
                            sportO.getBoolean("active")
                    ));
                    performance.setPoints(perfO.getInt("points"));

                    performances.add(performance);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return performances;
        }

        private void saveToDb(List<Performance> performances) {
            PerformanceRepository repository = new PerformanceRepository();
            repository.deletePerformances(sport.getId());
            for (Performance performance : performances) {
                repository.insertPerformance(performance);
            }
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<Performance> performances;

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
            public TeamTextView team;
            public TextView points;

            private ViewHolder(View v) {
                super(v);
                team = (TeamTextView) v.findViewById(R.id.individual_referee_list_item_team);
                points = (TextView) v.findViewById(R.id.individual_referee_list_item_points);
                v.setOnCreateContextMenuListener(this);
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle(performances.get(getAdapterPosition()).getTeam().getName() +
                        " - " + String.valueOf(performances.get(getAdapterPosition()).getPoints()));
                menu.add(getAdapterPosition(), 0, 0, "Upravit");
                menu.add(getAdapterPosition(), 1, 1, "Odstranit");
            }
        }

        private MyAdapter(List<Performance> performances) {
            this.performances = performances;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.individual_referee_list_item, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.team.setTeam(performances.get(position).getTeam());
            holder.points.setText(String.valueOf(performances.get(position).getPoints()));
        }

        @Override
        public int getItemCount() {
            return performances.size();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                changeFragment(IndividualRefereeAddFragment.newInstance(performances.get(item.getGroupId()).getId()));
                break;
            case 1:
                removeItem(item.getGroupId());
                break;
        }
        return false;
    }

    private void removeItem(final int position) {
        final Performance performance = performances.get(position);

        performances.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, performances.size());

        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestManager.createRequest(Route.get(Route.DELETE_PERFORMANCE))
                        .addParameter("id", performance.getId())
                        .saveIfNoConn(true)
                        .execute();

                PerformanceRepository performanceRepository = new PerformanceRepository();
                performanceRepository.deletePerformance(performance);
            }
        }).start();
    }
}
