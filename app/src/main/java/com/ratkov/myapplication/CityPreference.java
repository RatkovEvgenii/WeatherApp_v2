package com.ratkov.myapplication;

import android.app.Activity;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CityPreference {

    private static final String KEY = "city";
    private static final String MOSCOW = "Moscow";
    private SharedPreferences userPreference;

    public CityPreference(Activity activity) {
        userPreference = activity.getPreferences(Activity.MODE_PRIVATE); //Данные доступны только этому приложению
    }

    public String getCity() {
        return userPreference.getString(KEY, MOSCOW);
    }

    public void setCity(String city) {
        userPreference.edit().putString(KEY, city).apply();
    }

    public void setCity(String city, String key) {
        userPreference.edit().putString(key, city).apply();
    }

    public void setList(ArrayList<CityCard> list){
        Set<String> set = new HashSet<>();
        while (list.iterator().hasNext()) {
            String city = list.iterator().next().getText();
            set.add(city);
        }
        userPreference.edit().putStringSet("citySet", set).apply();
    }

    public ArrayList<CityCard> getList(){
        Set<String> set = userPreference.getStringSet("citySet", null);
        ArrayList<CityCard> arrayList = new ArrayList<>();
        if (set != null) {
            while (set.iterator().hasNext()) {
                String city = set.iterator().next();
                CityCard cityCard = new CityCard(city);
                arrayList.add(cityCard);
            }
        }
        return arrayList;
    }
}
