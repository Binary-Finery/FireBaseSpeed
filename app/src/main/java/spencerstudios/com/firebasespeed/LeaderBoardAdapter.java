package spencerstudios.com.firebasespeed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class LeaderBoardAdapter extends BaseAdapter {

    private ArrayList<Data> userData;
    private LayoutInflater layoutInflater;

    public LeaderBoardAdapter(Context context, ArrayList<Data> userData) {
        this.userData = userData;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return userData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder{
        TextView tvRank;
        TextView tvUser;
        TextView tvMake;
        TextView tvTime;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.leader_board_item, null);
            holder = new ViewHolder();

            holder.tvRank = (TextView) convertView.findViewById(R.id.text_view_rank);
            holder.tvUser = (TextView) convertView.findViewById(R.id.text_view_username);
            holder.tvMake = (TextView) convertView.findViewById(R.id.text_view_make);
            holder.tvTime = (TextView) convertView.findViewById(R.id.text_view_time);

            convertView.setTag(holder);
        }else{

            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvRank.setText(String.format(Locale.getDefault(), "%d", position + 1));
        holder.tvUser.setText(userData.get(position).getUserName());
        holder.tvMake.setText(userData.get(position).getMake()+" "+ userData.get(position).getModel());
        holder.tvTime.setText(NumberFormat.getNumberInstance(Locale.getDefault()).format(userData.get(position).getTime()).concat(" ms"));

        return convertView;
    }
}
