package cz.sps_pi.sportovni_den.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.db.MessageRepository;
import cz.sps_pi.sportovni_den.entity.Message;
import cz.sps_pi.sportovni_den.util.MessageAdapter;

/**
 * Created by Martin Forejt on 10.01.2017.
 * forejt.martin97@gmail.com
 */

public class MessagesFragment extends SportDenFragment {

    private int previous = -1;

    public MessagesFragment() {
        super("Zprávy", false);
    }

    public static MessagesFragment newInstance(int position) {
        MessagesFragment fragment = new MessagesFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("previous", position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            previous = bundle.getInt("previous");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        ListView listView = (ListView) view.findViewById(R.id.messages_list);

        final MessageRepository repo = new MessageRepository();
        final List<Message> messages = repo.getUsersMessages(false, getUser().getId());

        MessageAdapter adapter = new MessageAdapter(getContext(), messages);
        listView.setAdapter(adapter);

        if (previous != -1) {
            listView.setSelection(previous);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getCallback().changeFragmentFromFragment(DetailFragment.newInstance(messages.get(i).getId(), i));
            }
        });

        if (messages.size() > 0) {
            view.findViewById(R.id.messages_empty_message).setVisibility(View.GONE);
        }

        return view;
    }

    public static class DetailFragment extends SportDenFragment {

        private Message message;
        private int position;

        public DetailFragment() {
            super("Zprávy", true);
        }

        public static DetailFragment newInstance(int id, int position) {
            DetailFragment fragment = new DetailFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("message", id);
            bundle.putInt("position", position);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Bundle bundle = this.getArguments();
            if (bundle != null) {
                int messageId = bundle.getInt("message");
                position = bundle.getInt("position");
                MessageRepository repository = new MessageRepository();
                message = repository.getMessage(messageId);
                if (getCallback() != null)
                    getCallback().setTitle(message.getTitle());
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_messages_detail, container, false);

            TextView date = (TextView) view.findViewById(R.id.message_detail_date);
            TextView text = (TextView) view.findViewById(R.id.message_detail_text);

            SimpleDateFormat df = new SimpleDateFormat("dd. MM. yyyy HH:mm:ss", Locale.ENGLISH);
            date.setText(df.format(message.getDate()));
            text.setText(message.getMessage());

            return view;
        }

        @Override
        public boolean onBackPressed() {
            getCallback().changeFragmentFromFragment(MessagesFragment.newInstance(position));
            return true;
        }

    }

}
