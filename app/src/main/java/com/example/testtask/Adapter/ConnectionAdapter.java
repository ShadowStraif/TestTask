package com.example.testtask.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.testtask.MainActivity;
import com.example.testtask.Models.ConnectionModel;
import com.example.testtask.R;

import java.util.List;

public class ConnectionAdapter extends RecyclerView.Adapter<ConnectionAdapter.ViewHolder> {

    private List<ConnectionModel> connectionList;
    private MainActivity activity;

    public ConnectionAdapter(MainActivity activity)
    {
        this.activity = activity;
            }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rel_layout,parent,false );
        return new ViewHolder(itemView);
    }

    public void setConnection(List<ConnectionModel> connectionList)
    {
        this.connectionList = connectionList;
        notifyDataSetChanged();
    }

    public void onBindViewHolder(ViewHolder holder, int position)
    {
        final ConnectionModel item = connectionList.get(position);
        holder.conIP.setText(item.getIpAdress());
        holder.port.setText(item.getPortNum());
        holder.msg.setText(item.getMessage());

    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
      TextView conIP;
      TextView port;
      TextView msg;
         ViewHolder(View view)
             {
                 super(view);
                 conIP = view.findViewById(R.id.ipAddress);
                 port = view.findViewById(R.id.textPort);
                 msg = view.findViewById(R.id.textMess);

             }
    }

    public void deleteConection() {

        int size = connectionList.size();
        connectionList.clear();
        notifyItemRangeRemoved(0, size);
        /*connectionList.remove(position);
        notifyItemRemoved(position);*/
    }


    public int getItemCount(){
        return connectionList.size();
    }

}
