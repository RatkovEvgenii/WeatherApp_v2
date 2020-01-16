package com.ratkov.myapplication.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import com.ratkov.myapplication.CityCard;
import com.ratkov.myapplication.CityPreference;
import com.ratkov.myapplication.R;
import com.ratkov.myapplication.activity.TempScreenActivity;
import com.ratkov.myapplication.adapters.CitiesListRecyclerViewAdapter;
import com.ratkov.myapplication.callBackInterfaces.IAdapterCallbacks;
import com.ratkov.myapplication.callBackInterfaces.IAddCityCallback;

public class CitiesListScreenFragment extends Fragment implements IAdapterCallbacks, IAddCityCallback {

    private RecyclerView recyclerView;
    private CitiesListRecyclerViewAdapter adapter;
    private boolean isTempScreenExists;
    private String cityName;
    private ArrayList<CityCard> list;
    private CityPreference cityPreference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cities_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initList(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isTempScreenExists = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        cityPreference = new CityPreference(Objects.requireNonNull(getActivity()));
        cityName = cityPreference.getCity();
//        if (savedInstanceState != null) {
//            cityName = savedInstanceState.getString("city", "Moscow");
//        }
        if (isTempScreenExists) {
            showTempScreen(cityName);
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("city", cityName);
        outState.putParcelableArrayList("list", list);
        super.onSaveInstanceState(outState);
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
    }

    private void initList(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            CityCard[] data = new CityCard[]{
                    new CityCard(getString(R.string.moscow)),
                    new CityCard(getString(R.string.spb)),
                    new CityCard(getString(R.string.kazan)),
                    new CityCard(getString(R.string.ptz))
            };
            list = new ArrayList<>(data.length);
            list.addAll(Arrays.asList(data));
        } else {
            list = savedInstanceState.getParcelableArrayList("list");
        //    list = cityPreference.getList();
        }
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            adapter = new CitiesListRecyclerViewAdapter(list, this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
    }

    private void showTempScreen(String city) {
//        if (isTempScreenExists) {
//            TempScreenFragment detail = (TempScreenFragment) Objects.requireNonNull(getFragmentManager()).findFragmentById(R.id.temp_screen);
//            if (detail == null || !detail.getCityName().equals(city)) {
//                detail = TempScreenFragment.newInstance(city);
//                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.temp_screen, detail);
//                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//                fragmentTransaction.addToBackStack("Some_key");
//                fragmentTransaction.commit();
//            }
//        } else {
        Intent intent = new Intent();
        intent.setClass(Objects.requireNonNull(getActivity()), TempScreenActivity.class);
        intent.putExtra("index", city);
        startActivity(intent);
        //}
    }

    @Override
    public void startTempScreenFragment(String city) {
       // cityName = city;
        cityPreference.setCity(city);
        showTempScreen(city);
    }

    @Override
    public boolean addCityToList(String city) {
        if (!city.isEmpty() && !adapter.checkIsItemInData(city)) {
            city = city.substring(0, 1).toUpperCase() + city.substring(1);  //Make first letter of city name upper case
            CityCard cityCard = new CityCard(city);
            adapter.addItem(cityCard);
            recyclerView.scrollToPosition(0);
            return true;
        } return false;
    }

    @Override
    public void deleteCityFromList() {
        adapter.removeItem();
    }

}

