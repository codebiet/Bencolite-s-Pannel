package com.example.buildathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.buildathon.Adapters.PostAdapter;
import com.example.buildathon.Model.PostModel;
import com.example.buildathon.Utils.LikeHandler;
import com.example.buildathon.Utils.SessionManagment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.buildathon.Utils.Constants.KEY_ID;
import static com.example.buildathon.Utils.Constants.KEY_MOBILE;
import static com.example.buildathon.Utils.Constants.POST_INFO;
import static com.example.buildathon.Utils.LikeHandler.COLUMN_ID;

public class LikedPostsActivity extends AppCompatActivity {

    LikeHandler likeHandler;
    SessionManagment sessionManagment;
    DatabaseReference postref;
    PostAdapter postAdapter;
    ArrayList<HashMap<String,String>> likedpostList;
    List<PostModel> likedpost;
    RecyclerView recyclerLiked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_posts);
        likeHandler  = new LikeHandler(this);
        sessionManagment = new SessionManagment(this);
        likedpostList = new ArrayList<>();
        likedpost = new ArrayList<>();
        recyclerLiked = findViewById(R.id.rec_like);
        postref = FirebaseDatabase.getInstance().getReference().child(POST_INFO);
        postAdapter = new PostAdapter(likedpost,this);
        recyclerLiked.setLayoutManager(new GridLayoutManager(this,1));
        recyclerLiked.setAdapter(postAdapter);
        Log.e("id",sessionManagment.getUserDetails().get(KEY_ID));
        likedpostList = likeHandler.getWishtableAll(sessionManagment.getUserDetails().get(KEY_MOBILE));
        Log.e("  d",likedpostList.toString());
        for (HashMap<String,String > hashMap:likedpostList) {
            Log.e( "onCreate: ",hashMap.toString() );
            String postid = hashMap.get(COLUMN_ID);
            postref.child(postid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    PostModel model = snapshot.getValue(PostModel.class);
                    likedpost.add(model);
                    postAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}