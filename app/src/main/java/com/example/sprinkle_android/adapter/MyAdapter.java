package com.example.sprinkle_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprinkle_android.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<DataItem> myDataList = null;

    public MyAdapter(ArrayList<DataItem> dataList)
    {
        myDataList = dataList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(viewType == Code.ViewType.LEFT_CONTENT)
        {
            view = inflater.inflate(R.layout.left_content, parent, false);
            return new LeftViewHolder(view);
        }
        else if(viewType == Code.ViewType.RIGHT_CONTENT)
        {
            view = inflater.inflate(R.layout.right_content, parent, false);
            return new RightViewHolder(view);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if(viewHolder instanceof LeftViewHolder)
        {
            ((LeftViewHolder) viewHolder).leftContent_tv.setText(myDataList.get(position).getContent());
        }
        else if(viewHolder instanceof RightViewHolder)
        {
            ((RightViewHolder) viewHolder).rightContent_tv.setText(myDataList.get(position).getContent());
        }
    }

    @Override
    public int getItemCount() {
        return myDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return myDataList.get(position).getViewType();
    }


    public class LeftViewHolder extends RecyclerView.ViewHolder{

        TextView leftContent_tv;

        LeftViewHolder(View itemView)
        {
            super(itemView);

            leftContent_tv = itemView.findViewById(R.id.leftContent_tv_systemContent);
        }
    }

    public class RightViewHolder extends RecyclerView.ViewHolder{

        TextView rightContent_tv;

        RightViewHolder(View itemView)
        {
            super(itemView);

            rightContent_tv = itemView.findViewById(R.id.rightContent_tv_userContent);
        }
    }
}
