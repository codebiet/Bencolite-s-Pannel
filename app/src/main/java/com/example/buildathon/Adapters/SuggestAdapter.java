package com.example.buildathon.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buildathon.ChatActivity;
import com.example.buildathon.Model.RoomModel;
import com.example.buildathon.Model.UserModel;
import com.example.buildathon.R;
import com.example.buildathon.ViewProfile;

import java.io.Serializable;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class SuggestAdapter extends RecyclerView.Adapter<SuggestAdapter.MyViewHolder> {

    private List<UserModel> modelList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img;
        TextView name,title;
        LinearLayout suggest;
        public MyViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.imgSuggest);
            name = view.findViewById(R.id.txtNameSugg);
            title = view.findViewById(R.id.txtTitleSugg);
            suggest=view.findViewById(R.id.llSuggest);
        }
    }

    public SuggestAdapter(List<UserModel> modelList , Context context) {
        this.modelList = modelList;
        this.context = context;
    }

    @Override
    public SuggestAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_suggest, parent, false);

        return new SuggestAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SuggestAdapter.MyViewHolder holder, int position) {
        UserModel mList = modelList.get(position);
        if (mList.getImg_url()!=null)
        {
            Glide.with(context).load(mList.getImg_url()).into(holder.img);
        }
        holder.name.setText(mList.getName());
        holder.title.setText(mList.getTitle());
        Glide.with(context).load(mList.getImg_url()).into(holder.img);
        holder.suggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewProfile.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("model", (Serializable) mList);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

}


