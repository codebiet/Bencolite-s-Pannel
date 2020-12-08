package com.example.buildathon.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.buildathon.Adapters.SuggestAdapter;
import com.example.buildathon.MainActivity;
import com.example.buildathon.Model.UserModel;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
    EditText search;
    DatabaseReference userref;
    List<UserModel> users,suggestions;
    RecyclerView listView;
    SuggestAdapter suggestAdapter;
    LoadingBar loadingBar ;
    SessionManagment sessionManagment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        ((MainActivity)getActivity()).setTitle("Search");
        userref = FirebaseDatabase.getInstance().getReference().child("user_id");
        loadingBar = new LoadingBar(getActivity());
        sessionManagment = new SessionManagment(getActivity());
        loadingBar.show();
        getData();
        users = new ArrayList<>();
        listView = v.findViewById(R.id.listSuggest);
        suggestions = new ArrayList<>();
        suggestAdapter = new SuggestAdapter(suggestions,getActivity());
        search = v.findViewById(R.id.etsearch);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = String.valueOf(s);
                if (str.length()>1){
                    findsuggestion(s);
                    listView.setVisibility(View.VISIBLE);
                }
                else
                {
                    suggestions.clear();
                    listView.setVisibility(View.GONE);
                    suggestAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                findsuggestion(s);
            }
        });
        return v;
    }

    private void findsuggestion(CharSequence s) {
        suggestions.clear();
        String str = String.valueOf(s);
        for (UserModel m:users) {
            if (m.getName().toLowerCase().contains(str.toLowerCase()))
            {
                suggestions.add(m);
            }
        }
        listView.setLayoutManager(new GridLayoutManager(getActivity(),1));
        listView.setAdapter(suggestAdapter);
        suggestAdapter.notifyDataSetChanged();

    }

    private void getData() {
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()) {
                    UserModel model = ds.getValue(UserModel.class);
                    users.add(model);
                }
                loadingBar.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}