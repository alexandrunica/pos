package com.example.vladnica.pos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Vlad on 10/19/2018.
 */
public class CommandAdapter extends ArrayAdapter<CommandModel> {


    public CommandAdapter(@NonNull Context context, @NonNull List<CommandModel> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CommandModel commandModel = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.command_item_row, parent, false);
        }
        TextView commandId = (TextView) convertView.findViewById(R.id.commandId);
        TextView commandName = (TextView) convertView.findViewById(R.id.commandName);

        commandId.setText(String.valueOf(commandModel.getId()));
        commandName.setText(commandModel.getCommandName());

        return convertView;

    }
}
