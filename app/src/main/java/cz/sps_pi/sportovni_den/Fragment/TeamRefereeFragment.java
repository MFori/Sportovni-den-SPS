package cz.sps_pi.sportovni_den.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.db.GroupRepository;
import cz.sps_pi.sportovni_den.db.RefereeMatchRepository;
import cz.sps_pi.sportovni_den.db.SportRepository;
import cz.sps_pi.sportovni_den.entity.Group;
import cz.sps_pi.sportovni_den.entity.Match;
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

public class TeamRefereeFragment extends ResultsRefFragment implements SwipeRefreshLayout.OnRefreshListener {

    private boolean fromDetail = false;
    private MyAdapter adapter;
    private Sport sport;
    private List<Group> groups = new ArrayList<>();
    private SwipeRefreshLayout refresh;

    public static TeamRefereeFragment newInstance(int sport) {
        TeamRefereeFragment fragment = new TeamRefereeFragment();
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
        View view = inflater.inflate(R.layout.fragment_team_referee_list, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.team_referee_listView);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.team_referee_swipe_layout);
        refresh.setOnRefreshListener(this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        adapter = new MyAdapter(groups);
        recyclerView.setAdapter(adapter);

        DbLoader loader = new DbLoader();
        loader.execute(getSport());

        if (!fromDetail) {
            new Thread(new WebLoader()).start();
            refresh.setRefreshing(true);
            fromDetail = true;
        }

        return view;
    }

    private void show(List<Group> groups) {
        this.groups.clear();
        for (int i = 0; i < groups.size(); i++) {
            Group group = groups.get(i);
            if (group.getMatches().size() == 0) {
                groups.remove(i);
                i--;
            }
        }
        this.groups.addAll(groups);
        Collections.sort(this.groups, new Comparator<Group>() {
            @Override
            public int compare(Group group, Group t1) {
                return group.getGroup() < t1.getGroup() ? -1 : 1;
            }
        });
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        new Thread(new WebLoader()).start();
    }

    private class DbLoader extends AsyncTask<Sport, Void, List<Group>> {
        @Override
        protected List<Group> doInBackground(Sport... sports) {
            GroupRepository groupRepository = new GroupRepository();
            RefereeMatchRepository repository = new RefereeMatchRepository();

            List<Group> groups = groupRepository.getGroups(sports[0].getId());
            for (Group group : groups) {
                group.setMatches(repository.getMatches(sports[0], group.getGroup()));
            }

            return groups;
        }

        @Override
        protected void onPostExecute(List<Group> groups) {
            show(groups);
        }
    }

    private class WebLoader implements Runnable {
        @Override
        public void run() {
            RequestManager.createRequest(Route.get(Route.GET_MATCHES))
                    .addParameter("sport", sport.getId())
                    .setListener(new RequestListener() {
                        @Override
                        public void onRequestError(Response response) {
                            refresh.setRefreshing(false);
                        }

                        @Override
                        public void onRequestSuccess(Response response) {
                            final List<Group> groups = fromResponse(response);
                            saveToDb(groups);

                            if (canAccessUi())
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        show(groups);
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

        private List<Group> fromResponse(Response response) {
            List<Group> groups = new ArrayList<>();
            JSONArray arrGroups = response.getArray("groups");
            if (arrGroups == null) return groups;
            for (int i = 0; i < arrGroups.length(); i++) {
                try {
                    Group group = new Group();
                    JSONObject groupO = arrGroups.getJSONObject(i);
                    group.setGroup(groupO.optInt("group", 1));
                    group.setName(groupO.getString("name"));
                    groups.add(group);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            List<Match> matches = new ArrayList<>();
            JSONArray arrMatches = response.getArray("matches");
            for (int i = 0; i < arrMatches.length(); i++) {
                try {
                    Match match = new Match();
                    JSONObject matchO = arrMatches.getJSONObject(i);

                    match.setId(matchO.getInt("id"));
                    match.setGroup(matchO.optInt("group", 1));

                    int score1 = matchO.optInt("score_1", -1);
                    int score2 = matchO.optInt("score_2", -1);

                    match.setScore1(score1 == -1 ? null : score1);
                    match.setScore2(score2 == -1 ? null : score2);

                    JSONObject sportO = matchO.getJSONObject("sport");
                    JSONObject team1O = matchO.optJSONObject("team_1");
                    JSONObject team2O = matchO.optJSONObject("team_2");
                    JSONObject statusO = matchO.getJSONObject("status");

                    match.setSport(new Sport(
                            sportO.getInt("id"),
                            sportO.getString("name"),
                            sportO.getBoolean("active")
                    ));

                    if (team1O != null)
                        match.setTeam1(Team.getById(team1O.getInt("id")));
                    if (team2O != null)
                        match.setTeam2(Team.getById(team2O.getInt("id")));
                    match.setStatus(statusO.getInt("id"));

                    matches.add(match);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < groups.size(); i++) {
                List<Match> groupMatches = new ArrayList<>();
                for (int j = 0; j < matches.size(); j++) {
                    if (matches.get(j).getGroup() == groups.get(i).getGroup()) {
                        groupMatches.add(matches.get(j));
                    }
                }
                groups.get(i).setMatches(groupMatches);
            }

            return groups;
        }

        private void saveToDb(List<Group> groups) {
            GroupRepository groupRepository = new GroupRepository();
            RefereeMatchRepository repository = new RefereeMatchRepository();

            for (Group group : groups) {
                groupRepository.insert(group.getGroup(), getSport().getId(), group.getName());
                for (Match match : group.getMatches()) {
                    repository.insertMatch(match);
                }
            }
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_MATCH = 1;
        private static final int TYPE_GROUP_DIVIDER = 2;
        private List<Group> groups;

        private class MatchViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener,
                View.OnClickListener {
            private TeamTextView team1;
            private TeamTextView team2;
            private TextView score1;
            private TextView score2;

            private MatchViewHolder(View v) {
                super(v);
                team1 = (TeamTextView) v.findViewById(R.id.team_referee_list_item_team_1);
                team2 = (TeamTextView) v.findViewById(R.id.team_referee_list_item_team_2);
                score1 = (TextView) v.findViewById(R.id.team_referee_list_item_score_1);
                score2 = (TextView) v.findViewById(R.id.team_referee_list_item_score_2);
                v.setOnClickListener(this);
                v.setOnCreateContextMenuListener(this);
            }

            @Override
            public void onClick(View view) {
                Match match = getMatch(getAdapterPosition());
                if (match != null && match.getTeam1() != null && match.getTeam2() != null)
                    editItem(match);
                else
                    Toast.makeText(getContext(), "Zápas nelze upravit", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {
                Match match = getMatch(getAdapterPosition());
                if (match != null && match.getTeam1() != null && match.getTeam2() != null) {
                    menu.setHeaderTitle((match.getTeam1() != null ? match.getTeam1() : "Neurčen") + " : " +
                            (match.getTeam2() != null ? match.getTeam2() : "Neurčen"));
                    menu.add(getAdapterPosition(), 0, 0, "Upravit");
                    menu.add(getAdapterPosition(), 1, 1, "Odstranit");
                } else
                    Toast.makeText(getContext(), "Zápas nelze upravit", Toast.LENGTH_SHORT).show();
            }
        }

        private class TitleViewHolder extends RecyclerView.ViewHolder {
            private TextView groupName;

            private TitleViewHolder(View v) {
                super(v);
                groupName = (TextView) v.findViewById(R.id.team_referee_list_item_title);
            }
        }

        private MyAdapter(List<Group> groups) {
            this.groups = groups;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case TYPE_MATCH:
                    View vMatch = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.team_referee_list_item_match, parent, false);
                    return new MatchViewHolder(vMatch);
                case TYPE_GROUP_DIVIDER:
                    View vTitle = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.team_referee_list_item_title, parent, false);
                    return new TitleViewHolder(vTitle);
            }

            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case TYPE_MATCH:
                    MatchViewHolder mHolder = (MatchViewHolder) holder;
                    Match match = getMatch(position);
                    if (match.getTeam1() != null)
                        mHolder.team1.setTeam(match.getTeam1());
                    else
                        mHolder.team1.noTeam();
                    if (match.getTeam2() != null)
                        mHolder.team2.setTeam(match.getTeam2());
                    else
                        mHolder.team2.noTeam();
                    mHolder.score1.setText(match.getScore1() != null ? String.valueOf(match.getScore1()) : "");
                    mHolder.score2.setText(match.getScore2() != null ? String.valueOf(match.getScore2()) : "");
                    break;
                case TYPE_GROUP_DIVIDER:
                    TitleViewHolder tHolder = (TitleViewHolder) holder;
                    Group group = getGroup(position);
                    tHolder.groupName.setText(String.valueOf(group.getName()));
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) return groups.size() > 1 ? TYPE_GROUP_DIVIDER : TYPE_MATCH;

            int count = 0;
            for (Group group : groups) {
                if (position == count) return TYPE_GROUP_DIVIDER;
                count += group.getMatches().size() + 1;
            }

            return TYPE_MATCH;
        }

        @Override
        public int getItemCount() {
            int count = groups.size() > 1 ? groups.size() : 0;
            for (Group group : groups) {
                count += group.getMatches().size();
            }
            return count;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                Match match = getMatch(item.getGroupId());
                if (match != null) editItem(match);
                break;
            case 1:
                removeItem(item.getGroupId());
                break;
        }
        return false;
    }

    private void editItem(Match match) {
        changeFragment(TeamRefereeDetailFragment.newInstance(match.getId()));
    }

    private void removeItem(final int position) {
        Match match = null;
        int pos = 0;
        for (Group g : groups) {
            pos++;
            for (Match m : g.getMatches()) {
                if (pos == position) {
                    match = m;
                    m.setScore1(null);
                    m.setScore2(null);
                    break;
                }
                pos++;
            }
        }

        adapter.notifyDataSetChanged();

        final Match mMatch = match;

        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestManager.createRequest(Route.get(Route.UPDATE_MATCH))
                        .addParameter("id", mMatch.getId())
                        .addNullParameter("score_1")
                        .addNullParameter("score_2")
                        .saveIfNoConn(true)
                        .execute();

                RefereeMatchRepository refereeMatchRepository = new RefereeMatchRepository();
                refereeMatchRepository.insertMatch(mMatch);
            }
        }).start();
    }

    private Match getMatch(int position) {
        Match match = null;
        int pos = 0;
        for (Group g : groups) {
            if (groups.size() > 1) pos++;
            for (Match match1 : g.getMatches()) {
                if (pos == position) {
                    match = match1;
                    break;
                }
                pos++;
            }
        }

        return match;
    }

    private Group getGroup(int position) {
        Group group = null;
        int count_ = 0;
        for (Group g : groups) {
            if (position == count_) {
                group = g;
                break;
            }
            count_ += g.getMatches().size() + 1;
        }

        return group;
    }
}