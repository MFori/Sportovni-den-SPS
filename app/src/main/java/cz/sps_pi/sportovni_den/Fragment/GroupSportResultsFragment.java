package cz.sps_pi.sportovni_den.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.entity.Group;
import cz.sps_pi.sportovni_den.listener.ResultFragmentCallback;
import cz.sps_pi.sportovni_den.listener.ResultsRefreshListener;
import cz.sps_pi.sportovni_den.util.MatchesLinesAdapter;
import cz.sps_pi.sportovni_den.util.SportResult;
import cz.sps_pi.sportovni_den.util.TablesLinesAdapter;
import cz.sps_pi.sportovni_den.util.TeamSportResult;
import cz.sps_pi.sportovni_den.view.ResultRecyclerView;

/**
 * Created by Martin Forejt on 20.01.2017.
 * forejt.martin97@gmail.com
 */

public class GroupSportResultsFragment extends Fragment implements ResultsRefreshListener {

    private MatchesFragment matchesFragment;
    private TablesFragment tablesFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tablesFragment = new TablesFragment();
        matchesFragment = new MatchesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_sport_results, container, false);

        Button tables = (Button) view.findViewById(R.id.group_sport_results_tables);
        Button matches = (Button) view.findViewById(R.id.group_sport_results_matches);

        final ViewPager pager = (ViewPager) view.findViewById(R.id.group_sport_results_pager);
        pager.setAdapter(new PagerAdapter(getChildFragmentManager()));
        tables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pager.setCurrentItem(0, true);
            }
        });
        matches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pager.setCurrentItem(1, true);
            }
        });
        TeamSportResult result = (TeamSportResult) ((ResultFragmentCallback) getActivity()).getResult();
        onUpdate(result);

        return view;
    }

    @Override
    public void onUpdate(SportResult result) {
        matchesFragment.onUpdate(((TeamSportResult) result).getGroups());
        tablesFragment.onUpdate(((TeamSportResult) result).getGroups());
    }

    public static class TablesFragment extends Fragment {

        private List<Group> groups = new ArrayList<>();
        private TablesAdapter adapter;
        private ResultRecyclerView recyclerView;

        public void onUpdate(List<Group> groups) {
            this.groups.clear();
            this.groups.addAll(groups);
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_group_sport_results_tables, container, false);

            recyclerView = new ResultRecyclerView(getContext(),
                    ((ResultFragmentCallback) getActivity()).getRefreshLayout());
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            adapter = new TablesAdapter(groups);
            recyclerView.setAdapter(adapter);
            ((LinearLayout) view.findViewById(R.id.team_results_tables_container)).addView(recyclerView);

            return view;
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            if (isVisibleToUser && recyclerView != null)
                recyclerView.reVisible();
        }

        private class TablesAdapter extends RecyclerView.Adapter<TablesAdapter.ViewHolder> {
            private List<Group> groups;

            private TablesAdapter(List<Group> groups) {
                this.groups = groups;
            }

            public class ViewHolder extends RecyclerView.ViewHolder {
                private TextView group;
                private ListView listView;

                private ViewHolder(View v) {
                    super(v);
                    group = (TextView) v.findViewById(R.id.results_team_tables_group_name);
                    listView = (ListView) v.findViewById(R.id.results_team_tables_group_list_view);
                }
            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.team_results_tables_list_item, parent, false);
                return new ViewHolder(v);
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                Group group = groups.get(position);
                if (groups.size() == 1) {
                    holder.group.setVisibility(View.GONE);
                } else {
                    holder.group.setText(group.getName());
                }
                holder.listView.setAdapter(new TablesLinesAdapter(getContext(), group.getTable().getLines()));
            }

            public int getItemCount() {
                return groups.size();
            }
        }
    }

    public static class MatchesFragment extends Fragment {
        private List<Group> groups = new ArrayList<>();
        private MatchesAdapter adapter;
        private ResultRecyclerView listView;

        public void onUpdate(List<Group> groups) {
            this.groups.clear();
            this.groups.addAll(groups);
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_group_sport_results_matches, container, false);

            listView = new ResultRecyclerView(getContext(),
                    ((ResultFragmentCallback) getActivity()).getRefreshLayout());
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            listView.setLayoutManager(mLayoutManager);
            listView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            adapter = new MatchesAdapter(groups);
            listView.setAdapter(adapter);
            ((LinearLayout) view.findViewById(R.id.team_results_matches_container)).addView(listView);

            return view;
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            if (isVisibleToUser && listView != null)
                listView.reVisible();
        }

        private class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.ViewHolder> {
            private List<Group> groups;

            private MatchesAdapter(List<Group> groups) {
                this.groups = groups;
            }

            public class ViewHolder extends RecyclerView.ViewHolder {
                private TextView group;
                private ListView listView;

                private ViewHolder(View v) {
                    super(v);
                    group = (TextView) v.findViewById(R.id.results_team_matches_group_name);
                    listView = (ListView) v.findViewById(R.id.results_team_matches_group_list_view);
                }
            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.team_results_matches_list_item, parent, false);
                return new ViewHolder(v);
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                Group group = groups.get(position);
                if (groups.size() == 1) {
                    holder.group.setVisibility(View.GONE);
                } else {
                    holder.group.setText(group.getName());
                }
                holder.listView.setAdapter(new MatchesLinesAdapter(getContext(), group.getMatches()));
            }

            public int getItemCount() {
                return groups.size();
            }
        }
    }

    public class PagerAdapter extends FragmentPagerAdapter {
        private static final int ITEMS_COUNT = 2;

        private PagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return tablesFragment;
                case 1:
                    return matchesFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return ITEMS_COUNT;
        }
    }
}
