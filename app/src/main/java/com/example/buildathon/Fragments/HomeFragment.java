package com.example.buildathon.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.buildathon.Adapters.PostAdapter;
import com.example.buildathon.MainActivity;
import com.example.buildathon.Model.PostModel;
import com.example.buildathon.PostActivity;
import com.example.buildathon.R;
import com.example.buildathon.Utils.LoadingBar;
import com.example.buildathon.Utils.SessionManagment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.buildathon.Utils.Constants.KEY_PROFILE_IMG;
import static com.example.buildathon.Utils.Constants.POST_INFO;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    DatabaseReference postref;
    SessionManagment sessionManagment ;
    LoadingBar loadingBar;
    List<PostModel> modelList;
    PostAdapter postAdapter;
    RecyclerView recyclerView;
    CircleImageView home_img;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        loadingBar = new LoadingBar(getActivity());
        ((MainActivity)getActivity()).setTitle("Home");
        modelList = new ArrayList<>();
        sessionManagment = new SessionManagment(getActivity());
        home_img = v.findViewById(R.id.home_img);
        Glide.with(getActivity()).load(sessionManagment.getUserDetails().get(KEY_PROFILE_IMG)).into(home_img);
        recyclerView = v.findViewById(R.id.recyclerHome);
        CardView post = v.findViewById(R.id.post);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),1));
        postAdapter = new PostAdapter(modelList,getActivity());
        recyclerView.setAdapter(postAdapter);
        postAdapter.notifyDataSetChanged();
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PostActivity.class));
            }
        });
        postref = FirebaseDatabase.getInstance().getReference().child(POST_INFO);
        //getData();
        return  v;
    }

    private void getData() {
        loadingBar.show();
        modelList.clear();
        postref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelList.clear();
                for (DataSnapshot snap :snapshot.getChildren()
                     ) {
                    PostModel model = snap.getValue(PostModel.class);
                    modelList.add(model);
                    postAdapter.notifyDataSetChanged();
                }
                loadingBar.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingBar.dismiss();
                Toast.makeText(getActivity(),""+error,Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onStart() {
        getData();
        super.onStart();
    }
}