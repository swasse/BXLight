package be.ehb.bxlight.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ehb.bxlight.R;
import be.ehb.bxlight.model.ComicDatabase;
import be.ehb.bxlight.util.ComicRecyclerViewAdapter;

public class ComicListFragment extends Fragment {

    public ComicListFragment() {
    }

    public static ComicListFragment newInstance() {
        return new ComicListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comic_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new ComicRecyclerViewAdapter( ComicDatabase.getInstance(getActivity()).getComicDAO().getAllComicArt() ));
        }
        return view;
    }
}
