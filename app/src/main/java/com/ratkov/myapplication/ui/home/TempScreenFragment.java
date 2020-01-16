package com.ratkov.myapplication.ui.home;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.ratkov.myapplication.R;
import com.ratkov.myapplication.WeatherDataLoader;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class TempScreenFragment extends Fragment {

    private TextView tempTodayTopTextView, humidityTextView, pressureTextView, windTextView,
            overcastTextView, cityTextView, todayDateTextView, weatherIconTextView, tempMaxTextView,
            tempMinTextView, tempFeelsLikeTextView;
    private ImageButton updateBtn, backArrowBtn;
    private int humidityValue, pressureValue, windValue;
    private double tempToday, tempFeelsLike, tempMax, tempMin;
    private String cityName, overcastValue, country, updateOn, icon;
    private final String HUMIDITY_VALUE_KEY = "HUMIDITY_VALUE_KEY", OVERCAST_VALUE_KEY = "overcast",
            TEMP_TODAY_KEY = "TEMP_TODAY_KEY", CITY_NAME_KEY = "CITY_NAME_KEY", COUNTRY_KEY = "COUNTRY_KEY",
            TEMP_FEELS_LIKE_KEY = "TEMP_FEELS_LIKE_KEY", TEMP_MAX_KEY = "TEMP_MAX_KEY", TEMP_MIN_KEY = "TEMP_MIN_KEY",
            PRESSURE_KEY = "PRESSURE_KEY", WIND_KEY = "WIND_KEY", ICON_KEY = "ICON_KEY";

    private final Handler handler = new Handler();
    private static final String LOG_TAG = "WeatherFragment";
    private NestedScrollView nestedScrollView;
    private Typeface weatherFont;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            //Name
            cityName = savedInstanceState.getString(CITY_NAME_KEY);
            country = savedInstanceState.getString(COUNTRY_KEY);
            //Temp
            tempToday = savedInstanceState.getDouble(TEMP_TODAY_KEY);
            tempFeelsLike = savedInstanceState.getDouble(TEMP_FEELS_LIKE_KEY);
            tempMax = savedInstanceState.getDouble(TEMP_MAX_KEY);
            tempMin = savedInstanceState.getDouble(TEMP_MIN_KEY);
            //Descriptions
            humidityValue = savedInstanceState.getInt(HUMIDITY_VALUE_KEY);
            overcastValue = savedInstanceState.getString(OVERCAST_VALUE_KEY);
            pressureValue = savedInstanceState.getInt(PRESSURE_KEY);
            windValue = savedInstanceState.getInt(WIND_KEY);
            icon = savedInstanceState.getString(ICON_KEY);
            setValues();
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_temp_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initFont();
        onUpdateBtnClicked();
        showBackButton();
        onBackArrowBtnClicked();
        //При создании фрагмента, экран фокусировался на RecyclerView, только это помогло:
        if (nestedScrollView.getParent() != null) nestedScrollView.getParent().requestChildFocus(nestedScrollView, nestedScrollView);
    }

    private void initFont() {
        weatherFont = Typeface.createFromAsset(Objects.requireNonNull(getActivity()).getAssets(), "fonts/weathericons.ttf");
        weatherIconTextView.setTypeface(weatherFont);
    }

    private void showBackButton() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            backArrowBtn.setVisibility(View.INVISIBLE);
        else backArrowBtn.setVisibility(View.VISIBLE);
    }

    private void onBackArrowBtnClicked() {
        backArrowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) getActivity().finish();
            }
        });
    }

    private void initViews(View view) {
        //temp
        tempTodayTopTextView = view.findViewById(R.id.temperatureValue);
        tempFeelsLikeTextView = view.findViewById(R.id.temperatureFeelsLikeValue);
        tempMaxTextView = view.findViewById(R.id.tempValueMax);
        tempMinTextView =  view.findViewById(R.id.tempValueMin);
        //descriptions
        humidityTextView = view.findViewById(R.id.humidityValue);
        overcastTextView = view.findViewById(R.id.overcastValue);
        pressureTextView = view.findViewById(R.id.pressureValue);
        windTextView = view.findViewById(R.id.windValue);
        //other
        cityTextView = view.findViewById(R.id.cityTextView);
        updateBtn = view.findViewById(R.id.toWikiBtn);
        todayDateTextView = view.findViewById(R.id.todayDate);
        nestedScrollView = view.findViewById(R.id.ScrollView);
        backArrowBtn = view.findViewById(R.id.back_arrow);
        weatherIconTextView = view.findViewById(R.id.weatherIcon);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(CITY_NAME_KEY, cityName);
        savedInstanceState.putString(COUNTRY_KEY, country);
        savedInstanceState.putDouble(TEMP_TODAY_KEY, tempToday);
        savedInstanceState.putDouble(TEMP_FEELS_LIKE_KEY, tempFeelsLike);
        savedInstanceState.putDouble(TEMP_MAX_KEY, tempMax);
        savedInstanceState.putDouble(TEMP_MIN_KEY, tempMin);
        savedInstanceState.putInt(HUMIDITY_VALUE_KEY, humidityValue);
        savedInstanceState.putInt(WIND_KEY, windValue);
        savedInstanceState.putInt(PRESSURE_KEY, pressureValue);
        savedInstanceState.putString(OVERCAST_VALUE_KEY, overcastValue);
        savedInstanceState.putString(ICON_KEY, icon);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void setValues() {
        //city + country
        String cityText = cityName + ", " + country;
        cityTextView.setText(cityText);
        //date of update
        String updatedText = getString(R.string.last_update) + " " + updateOn;
        todayDateTextView.setText(updatedText);
        //temp today
        String tempTodayString = String.valueOf(Math.round(tempToday)).concat(getString(R.string.celsius));
        tempTodayTopTextView.setText(tempTodayString);
        //temp feels like
        String tempFeelsLikeString = getString(R.string.feels_like) + " " + Math.round(tempFeelsLike) + getString(R.string.celsius);
        tempFeelsLikeTextView.setText(tempFeelsLikeString);
        // temp min
        String tempMinString = "Min: " + String.valueOf(tempMin).concat(getString(R.string.celsius));
        tempMinTextView.setText(tempMinString);
        //temp max
        String tempMaxString = "Max: " + String.valueOf(tempMax).concat(getString(R.string.celsius));
        tempMaxTextView.setText(tempMaxString);
        //Descriptions
        //overcast
        overcastTextView.setText(overcastValue);
        //humidity
        humidityTextView.setText(String.valueOf(humidityValue).concat("%"));
        //pressure
        String pressureMmHg = String.valueOf(Math.round(pressureValue / 1.33322387415)).concat(getString(R.string.mmHg));
        pressureTextView.setText(pressureMmHg);
        //wind
        windTextView.setText(String.valueOf(windValue).concat(getString(R.string.ms)));
        //icon
        weatherIconTextView.setText(icon);
    }

    private void updateWeatherOnScreen(){
        if (getArguments()!= null) {
            updateWeatherData(getArguments().getString("index"));
        }
    }

    private void updateWeatherData(final String city) {
        new Thread() {
            public void run() {
                final JSONObject json = WeatherDataLoader.getJSONData(Objects.requireNonNull(getActivity()), city);
                if (json == null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), getString(R.string.city_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else handler.post(new Runnable() {
                    @Override
                    public void run() {
                        renderWeather(json);
                    }
                });
            }
        }.start();
    }

    private void renderWeather(JSONObject json) {
        Log.d(LOG_TAG, "json " + json.toString());
        try {
            //up
            cityName = json.getString("name");
            //sys
            country = json.getJSONObject("sys").getString("country");
            long sunrise = json.getJSONObject("sys").getLong("sunrise") * 1000;
            long sunset = json.getJSONObject("sys").getLong("sunset") * 1000;
            //weather
            JSONObject weather = json.getJSONArray("weather").getJSONObject(0);
            overcastValue = weather.getString("description");
            int id = weather.getInt("id");
            //main
            JSONObject main = json.getJSONObject("main");
            tempToday = main.getDouble("temp");
            tempFeelsLike = main.getDouble("feels_like");
            tempMin = main.getDouble("temp_min");
            tempMax = main.getDouble("temp_max");
            pressureValue = main.getInt("pressure");
            humidityValue = main.getInt("humidity");
            //wind
            windValue = json.getJSONObject("wind").getInt("speed");
            //date
            DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
            updateOn = dateFormat.format(new Date(json.getLong("dt") * 1000));

            icon = getWeatherIcon(id, sunrise, sunset);

            setValues();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "One or more fields not found in the JSON data");
        }
    }

    private String getWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        String icon = "";

        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                icon = getString(R.string.weather_sunny);
            } else {
                icon = getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2: {
                    icon = getString(R.string.weather_thunder);
                    break;
                }
                case 3: {
                    icon = getString(R.string.weather_drizzle);
                    break;
                }
                case 5: {
                    icon = getString(R.string.weather_rainy);
                    break;
                }
                case 6: {
                    icon = getString(R.string.weather_snowy);
                    break;
                }
                case 7: {
                    icon = getString(R.string.weather_foggy);
                    break;
                }
                case 8: {
                    icon = getString(R.string.weather_cloudy);
                    break;
                }
            }
        }
        return icon;
    }

    private void onUpdateBtnClicked() {
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateWeatherOnScreen();
                Toast.makeText(getContext(), getString(R.string.updating), Toast.LENGTH_SHORT).show();
            }
        });
    }

    static TempScreenFragment newInstance(String city) {
        Bundle args = new Bundle();
        TempScreenFragment fragment = new TempScreenFragment();
        args.putString("index", city);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        updateWeatherOnScreen();
        super.onAttach(context);
    }

    String getCityName() {
        cityName = Objects.requireNonNull(getArguments()).getString("index");
        try {
            return cityName;
        } catch (Exception e) {
            return "";
        }
    }
}

