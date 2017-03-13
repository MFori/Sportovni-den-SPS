package cz.sps_pi.sportovni_den.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cz.sps_pi.sportovni_den.R;
import cz.sps_pi.sportovni_den.entity.Message;

/**
 * Created by Martin Forejt on 17.01.2017.
 * forejt.martin97@gmail.com
 */

public class MessageAdapter extends BaseAdapter {

    private List<Message> messages;
    private Context context;

    public MessageAdapter(Context context, List<Message> messages) {
        this.messages = messages;
        this.context = context;
    }

    public static class ViewHolder {
        public TextView title;
        public TextView text;
        public TextView date;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.message_list_item, viewGroup, false);

            holder = new ViewHolder();
            holder.title = (TextView) view.findViewById(R.id.message_item_title);
            holder.text = (TextView) view.findViewById(R.id.message_item_text);
            holder.date = (TextView) view.findViewById(R.id.message_item_date);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (messages.size() > 0) {
            Message message = messages.get(i);

            holder.title.setText(message.getTitle());
            holder.text.setText(message.getMessage());

            SimpleDateFormat df = new SimpleDateFormat("dd. MM. yyyy HH:mm:ss", Locale.ENGLISH);

            holder.date.setText(df.format(message.getDate()));
        }

        return view;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    public int getCount() {
        return messages.size();
    }

}
