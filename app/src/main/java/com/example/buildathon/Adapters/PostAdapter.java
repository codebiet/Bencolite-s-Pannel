package com.example.buildathon.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buildathon.ChatActivity;
import com.example.buildathon.Model.PostModel;
import com.example.buildathon.Model.RoomModel;
import com.example.buildathon.Model.UserModel;
import com.example.buildathon.R;
import com.example.buildathon.Utils.LikeHandler;
import com.example.buildathon.Utils.LoadingBar;
import com.example.buildathon.Utils.SessionManagment;
import com.example.buildathon.ViewProfile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.buildathon.Utils.Constants.KEY_MOBILE;
import static com.example.buildathon.Utils.Constants.USER_REF;
import static com.example.buildathon.Utils.LikeHandler.COLUMN_ID;
import static com.example.buildathon.Utils.LikeHandler.COLUMN_USER_ID;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    private List<PostModel> modelList;
    private Context context;
    private List<UserModel> userModelList;
    private UserModel userModel;
    private SessionManagment sessionManagment;
    private LoadingBar loadingBar;
    private DatabaseReference userref;
    LikeHandler likeHandler;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imguser;
        TextView postuser , posttime , textpost;
        ImageView postimage;
        LinearLayout llLike , llComment;
        ImageView imgLike;
        TextView textLike;
        public MyViewHolder(View view) {
            super(view);
            imguser = view.findViewById(R.id.postuserimg);
            postuser = view.findViewById(R.id.postuser);
            posttime = view.findViewById(R.id.timepost);
            textpost = view.findViewById(R.id.textpost);
            postimage = view.findViewById(R.id.imgpost);
            llComment = view.findViewById(R.id.llComment);
            llLike = view.findViewById(R.id.llLike);
            imgLike = view.findViewById(R.id.imgLike);
            textLike = view.findViewById(R.id.txtLike);

        }
    }

    public PostAdapter(List<PostModel> modelList , Context context) {
        this.modelList = modelList;
        this.context = context;
    }

    @Override
    public PostAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_post, parent, false);

        return new PostAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PostAdapter.MyViewHolder holder, int position) {
        PostModel mList = modelList.get(position);
        likeHandler = new LikeHandler(context);
        sessionManagment = new SessionManagment(context);
        userModelList = new ArrayList<>();
        userref = FirebaseDatabase.getInstance().getReference().child(USER_REF).child(mList.getUsernumber());
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModel = snapshot.getValue(UserModel.class);
                userModelList.add(userModel);
                holder.postuser.setText(userModel.getName());
                Glide.with(context).load(userModel.getImg_url()).into(holder.imguser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (mList.getPosttext().trim()==null||mList.getPosttext().trim().equalsIgnoreCase(""))
        {
            holder.textpost.setVisibility(View.GONE);
        }
        if (likeHandler.isInLiketable(mList.getPostId(),sessionManagment.getUserDetails().get(KEY_MOBILE)))
        {
            holder.imgLike.setColorFilter(context.getResources().getColor(R.color.com_facebook_blue), PorterDuff.Mode.SRC_ATOP);
            holder.textLike.setTextColor(context.getResources().getColor(R.color.com_facebook_blue));
            holder.textLike.setText("Liked");
        }

        holder.posttime.setText("Posted on "+mList.getPosttime()+"  "+ mList.getPostdate());
        holder.textpost.setText(mList.getPosttext().trim());
        Glide.with(context).load(mList.getPostimg()).into(holder.postimage);

        holder.postuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewProfile.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("model", (Serializable) userModelList.get(position));
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        holder.llLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Liked",likeHandler.getWishtableAll(sessionManagment.getUserDetails().get(KEY_MOBILE)).toString());
                if (likeHandler.isInLiketable(mList.getPostId(),sessionManagment.getUserDetails().get(KEY_MOBILE)))
                {
                    likeHandler.removeItemFromWishtable(mList.getPostId(),sessionManagment.getUserDetails().get(KEY_MOBILE));
                //    Toast.makeText(context,String.valueOf(b),Toast.LENGTH_LONG).show();
                    holder.imgLike.setColorFilter(context.getResources().getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP);
                    holder.textLike.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                    holder.textLike.setText("Like");
                }
                else
                {
                    HashMap<String,String> map = new HashMap<>();
                    map.put(COLUMN_ID,mList.getPostId());
                    map.put(COLUMN_USER_ID,sessionManagment.getUserDetails().get(KEY_MOBILE));
                    boolean b = likeHandler.setwishTable(map);
                    //Toast.makeText(context,String.valueOf(b),Toast.LENGTH_LONG).show();
                    holder.imgLike.setColorFilter(context.getResources().getColor(R.color.com_facebook_blue),PorterDuff.Mode.SRC_ATOP);
                    holder.textLike.setTextColor(context.getResources().getColor(R.color.com_facebook_blue));
                    holder.textLike.setText("Liked");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

}


