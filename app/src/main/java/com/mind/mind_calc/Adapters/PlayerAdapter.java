package com.mind.mind_calc.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mind.mind_calc.Constructors.PlayersName;
import com.mind.mind_calc.R;

import java.util.ArrayList;

public class PlayerAdapter extends ArrayAdapter<PlayersName> {

    private Context mContext;
    private int mResource;

    public PlayerAdapter(@NonNull Context context, int resource, @NonNull ArrayList<PlayersName> objects) {
        super(context, resource, objects);

        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView textPlayerName = convertView.findViewById(R.id.textPlayerName);

        textPlayerName.setText(getItem(position).getPlayerName());

        return convertView;
    }

}