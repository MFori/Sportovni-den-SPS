package cz.sps_pi.sportovni_den.Fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.db.MessageRepository;
import cz.sps_pi.sportovni_den.entity.Message;
import cz.sps_pi.sportovni_den.util.NotificationsManager;

public class NotificationsFragment extends SportDenFragment {

    private List<Message> messages = new ArrayList<>();
    private MyAdapter adapter;
    private TextView emptyMessage;

    public NotificationsFragment() {
        super("Notifikace");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.notifications_recycler_view);

        FloatingActionButton newBtn = (FloatingActionButton) view.findViewById(R.id.notifications_add_btn);
        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCallback().changeFragmentFromFragment(new DetailFragment());
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        emptyMessage = (TextView) view.findViewById(R.id.notifications_empty_message);

        adapter = new MyAdapter(messages);
        recyclerView.setAdapter(adapter);

        DbLoader loader = new DbLoader();
        loader.execute(getUser().getId());

        return view;
    }

    private void showNotifications(List<Message> messages) {
        this.messages.clear();
        this.messages.addAll(messages);
        adapter.notifyDataSetChanged();
        if (messages.size() > 0) {
            emptyMessage.setVisibility(View.GONE);
        }
    }

    private class DbLoader extends AsyncTask<Integer, Void, List<Message>> {
        @Override
        protected List<Message> doInBackground(Integer... ids) {
            MessageRepository repo = new MessageRepository();
            if (ids[0] != null)
                return repo.getUsersMessages(true, ids[0]);
            return new ArrayList<Message>();
        }

        @Override
        protected void onPostExecute(List<Message> messages) {
            showNotifications(messages);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<Message> messages;

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView title;
            private TextView text;
            private TextView date;

            private ViewHolder(View v) {
                super(v);
                title = (TextView) v.findViewById(R.id.message_item_title);
                text = (TextView) v.findViewById(R.id.message_item_text);
                date = (TextView) v.findViewById(R.id.message_item_date);
            }
        }

        private MyAdapter(List<Message> messages) {
            this.messages = messages;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.notifications_list_item, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Message message = messages.get(position);

            holder.title.setText(message.getTitle());
            holder.text.setText(message.getMessage());

            SimpleDateFormat df = new SimpleDateFormat("dd. MM. yyyy HH:mm:ss", Locale.ENGLISH);

            holder.date.setText(df.format(message.getDate()));
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }
    }


    public static class DetailFragment extends SportDenFragment {
        private EditText inTitle, inText;
        private Map<String, Integer> addresses = new HashMap<>();
        private ListView list;
        private TextView to;

        public DetailFragment() {
            super("Nová notifikace", true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_notifications_new, container, false);

            inTitle = (EditText) view.findViewById(R.id.notifications_new_title);
            inText = (EditText) view.findViewById(R.id.notifications_new_text);

            Button submit = (Button) view.findViewById(R.id.notifications_new_submit);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendNotification();
                }
            });

            to = (TextView) view.findViewById(R.id.notifications_new_to);
            to.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog();
                }
            });

            return view;
        }

        private void showDialog() {
            final String[] addresses = NotificationsManager.getAddressesAsArray();
            final AddressesAdapter adapter = new AddressesAdapter(getContext(),
                    android.R.layout.simple_list_item_multiple_choice, addresses);
            final boolean[] itemsChecked = new boolean[addresses.length];
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Vyberte adresáty");
            builder.setMultiChoiceItems(addresses, itemsChecked, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                    itemsChecked[i] = b;
                    if (i == 0) {
                        for (int j = 0; j < addresses.length; j++) {
                            list.setItemChecked(j, b);
                            itemsChecked[j] = b;
                        }
                        adapter.onAll(b);
                    } else if (i == 2) {
                        for (int j = 2; j < addresses.length; j++) {
                            list.setItemChecked(j, b);
                            itemsChecked[j] = b;
                        }
                        adapter.onAthletes(b);
                    }
                }
            });
            builder.setPositiveButton("Vybrat", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    onChoose(itemsChecked);
                }
            }).setNegativeButton("Zrušit", null);

            AlertDialog dialog = builder.create();
            dialog.show();

            list = dialog.getListView();
            list.setAdapter(adapter);

            if (this.addresses != null) {
                for (Map.Entry<String, Integer> entry : this.addresses.entrySet()) {
                    int i = entry.getValue() - 1;
                    itemsChecked[i] = true;
                    list.setItemChecked(i, true);
                    if (i == 0) {
                        for (int j = 0; j < addresses.length; j++) {
                            list.setItemChecked(j, true);
                            itemsChecked[j] = true;
                        }
                        adapter.onAll(true);
                    } else if (i == 2) {
                        for (int j = 2; j < addresses.length; j++) {
                            list.setItemChecked(j, true);
                            itemsChecked[j] = true;
                        }
                        adapter.onAthletes(true);
                    }
                }
            }
        }

        private void onChoose(boolean[] enabled) {
            addresses = new HashMap<>();
            if (enabled[0] || (enabled[1] && enabled[2]))
                addresses.put(NotificationsManager.TITLE_ALL, NotificationsManager.ADDRESSEE_ALL);
            else {
                if (enabled[1])
                    addresses.put(NotificationsManager.TITLE_REFEREES, NotificationsManager.ADDRESSEE_REFEREES);
                if (enabled[2])
                    addresses.put(NotificationsManager.TITLE_ATHLETES, NotificationsManager.ADDRESSEE_ATHLETES);
                else {
                    int count = 0;
                    for (int i = 3; i < enabled.length; i++) {
                        if (enabled[i]) count++;
                    }
                    if (count == enabled.length - 3)
                        addresses.put(NotificationsManager.TITLE_ATHLETES, NotificationsManager.ADDRESSEE_ATHLETES);
                    else {
                        Map<String, Integer> values = NotificationsManager.getAsMap();
                        for (Map.Entry<String, Integer> entry : values.entrySet()) {
                            if (enabled[entry.getValue() - 1])
                                addresses.put(entry.getKey(), entry.getValue());
                        }
                    }
                }
            }

            String text = "";
            for (Map.Entry<String, Integer> entry : addresses.entrySet()) {
                if (!text.equals("")) text += ", ";
                text += entry.getKey();
            }

            if (text.equals("")) text = "Vyberte adresáty";

            to.setText(text);
        }

        private class AddressesAdapter extends ArrayAdapter<String> {
            private boolean[] enabled;

            private AddressesAdapter(Context context, int textViewResId, String[] objects) {
                super(context, textViewResId, objects);
                enabled = new boolean[objects.length];
                for (int i = 0; i < enabled.length; i++) {
                    enabled[i] = true;
                }
            }

            private void onAll(boolean all) {
                for (int i = 1; i < enabled.length; i++) {
                    enabled[i] = !all;
                }
            }

            private void onAthletes(boolean athletes) {
                for (int i = 3; i < enabled.length; i++) {
                    enabled[i] = !athletes;
                }
            }

            @Override
            public boolean isEnabled(int n) {
                return enabled[n];
            }
        }

        private boolean validNotification() {
            if (addresses.size() <= 0) {
                Toast.makeText(getContext(), "Vyberte adresáty!", Toast.LENGTH_LONG).show();
                return false;
            }
            if (inTitle.getText().toString().equals("")) {
                Toast.makeText(getContext(), "Zadejte nadpis!", Toast.LENGTH_LONG).show();
                return false;
            }
            if (inText.getText().toString().equals("")) {
                Toast.makeText(getContext(), "Zadejte text zprávy!", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }

        private void sendNotification() {
            if (!validNotification()) return;

            final Message message = new Message();
            message.setTitle(inTitle.getText().toString());
            message.setMessage(inText.getText().toString());
            message.setSender(getUser().getId());

            Integer addresses[] = new Integer[this.addresses.size()];
            int i = 0;
            for (Map.Entry<String, Integer> e : this.addresses.entrySet()) {
                addresses[i] = e.getValue();
                i++;
            }

            final ProgressDialog dialog = new ProgressDialog(getContext());
            dialog.setMessage("Odesílání...");
            dialog.setCancelable(false);
            dialog.show();

            NotificationsManager.sendNotification(message, addresses, new NotificationsManager.SendListener() {
                @Override
                public void onSend(Integer id) {
                    message.setId(id);
                    MessageRepository repository = new MessageRepository();
                    repository.addMessage(message);
                    dialog.dismiss();
                    getCallback().changeFragmentFromFragment(new NotificationsFragment());
                }

                @Override
                public void onError() {
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Notifikace se neodeslala.", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public boolean onBackPressed() {
            getCallback().changeFragmentFromFragment(new NotificationsFragment());
            return true;
        }

    }
}
