package com.gdevelopers.movies.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.gdevelopers.movies.R;
import com.gdevelopers.movies.activities.MainActivity;
import com.gdevelopers.movies.adapters.HomeAdapter;
import com.gdevelopers.movies.adapters.PopularActorsAdapter;
import com.gdevelopers.movies.database.DatabaseHandler;
import com.gdevelopers.movies.helpers.MovieDB;
import com.gdevelopers.movies.helpers.OnClickHelper;
import com.gdevelopers.movies.model.KFragment;
import com.gdevelopers.movies.model.KObject;
import com.gdevelopers.movies.model.ModelService;
import com.gdevelopers.movies.rest.services.MovieService;
import com.gdevelopers.movies.rest.services.PeopleService;
import com.gdevelopers.movies.rest.services.TVShowService;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


@SuppressWarnings("WeakerAccess")
public class FragmentHome extends KFragment implements View.OnClickListener {
    private MainActivity activity;
    @BindView(R.id.movies_list)
    RecyclerView moviesRv;
    @BindView(R.id.tv_list)
    RecyclerView tvShowsRv;
    @BindView(R.id.progressBar1)
    ProgressBar progressBar1;
    @BindView(R.id.progressBar2)
    ProgressBar progressBar2;
    @BindView(R.id.progressBar3)
    ProgressBar progressBar3;
    @BindView(R.id.progressBar4)
    ProgressBar progressBar4;
    @BindView(R.id.now_playing_movies)
    RecyclerView nowPlayingRv;
    @BindView(R.id.popular_tv_list)
    RecyclerView popularRv;
    @BindView(R.id.popular_people)
    RecyclerView actorsRv;
    private Context context;
    @BindView(R.id.offline_layout)
    LinearLayout offlineLayout;
    @BindView(R.id.container_layout)
    LinearLayout containerLayout;
    private Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        activity = (MainActivity) getActivity();
        context = getContext();

        activity.setVisibleFragment(this);

        MovieService.getInstance().getUpComingMovies(context, false, 1, response -> {
            final HomeAdapter moviesAdapter = new HomeAdapter(context, response.getMovies(), null, true);
            moviesRv.setAdapter(moviesAdapter);
            progressBar1.setVisibility(View.GONE);
        });

        MovieService.getInstance().getNowPlayingMovies(context, false, 1, response -> {
            final HomeAdapter moviesAdapter = new HomeAdapter(context, response.getMovies(), null, true);
            nowPlayingRv.setAdapter(moviesAdapter);
            progressBar3.setVisibility(View.GONE);
        });

        TVShowService.getInstance().getOnTV(context, false, 1, response -> {
            final HomeAdapter tvShowsAdapter = new HomeAdapter(getContext(), null, response.getTvShows(), false);
            tvShowsRv.setAdapter(tvShowsAdapter);
            progressBar2.setVisibility(View.GONE);
        });

        TVShowService.getInstance().getPopular(context, false, 1, response -> {
            HomeAdapter tvShowsAdapter = new HomeAdapter(getContext(), null, response.getTvShows(), false);
            popularRv.setAdapter(tvShowsAdapter);
            progressBar4.setVisibility(View.GONE);
        });

        PeopleService.getInstance().getPopularPeople(false, 1, response -> {
            final PopularActorsAdapter actorsAdapter = new PopularActorsAdapter(getContext(), response.getActorList(), true);
            actorsRv.setAdapter(actorsAdapter);

            actorsAdapter.setOnItemClickListener((actor, imageView) -> OnClickHelper.actorClicked(context, actor, imageView));

        });

        DatabaseHandler databaseHandler = MovieDB.getAppContext().getDatabaseHandler();
        long count = databaseHandler.getCount();

        tvShowsRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        tvShowsRv.setNestedScrollingEnabled(false);
        moviesRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        moviesRv.setNestedScrollingEnabled(false);


        nowPlayingRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        nowPlayingRv.setNestedScrollingEnabled(false);
        popularRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        popularRv.setNestedScrollingEnabled(false);
        actorsRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        actorsRv.setNestedScrollingEnabled(false);

        Button searchBt = rootView.findViewById(R.id.advanced_search);
        searchBt.setOnClickListener(this);

        LinearLayout movies1 = rootView.findViewById(R.id.go_to_movies_1);
        LinearLayout movies2 = rootView.findViewById(R.id.go_to_movies_2);
        LinearLayout tv1 = rootView.findViewById(R.id.go_to_tv_1);
        LinearLayout tv2 = rootView.findViewById(R.id.go_to_tv_2);
        movies1.setOnClickListener(this);
        movies2.setOnClickListener(this);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        return rootView;
    }

    private void replaceFragment(KFragment kFragment) {
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main, kFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void serviceResponse(int responseID, List<KObject> objects) {
        /*if (responseID == Constants.UPCOMING && objects != null) {
            Section section = (Section) objects.get(0);


            if (count == 0)
                for (Movie movie : section.getMovieList())
                    databaseHandler.addMovie(movie);

            final HomeAdapter moviesAdapter = new HomeAdapter(getContext(), section.getMovieList(), null, true);
            moviesRv.setAdapter(moviesAdapter);
            progressBar1.setVisibility(View.GONE);

        }*/
    }


    @Override
    public void update(ModelService service, boolean reload) {
    }

    private void isOffline() {
        containerLayout.setVisibility(View.GONE);
        offlineLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setTitle(R.string.home);

        activity.getNavigationView().setCheckedItem(R.id.nav_home);
        activity.getAddFab().hide();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.advanced_search:
                replaceFragment(new FragmentAdvancedSearch());
                break;
            case R.id.go_to_movies_1:
            case R.id.go_to_movies_2:
                replaceFragment(new FragmentMovies());
                break;
            case R.id.go_to_tv_1:
            case R.id.go_to_tv_2:
                replaceFragment(new FragmentTvShows());
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
