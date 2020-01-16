package com.ratkov.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class CityCard implements Parcelable {
    private int position;
    private String text;

    public CityCard(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    private CityCard(Parcel in) {
        position = in.readInt();
        text = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(position);
        dest.writeString(text);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CityCard> CREATOR = new Creator<CityCard>() {
        @Override
        public CityCard createFromParcel(Parcel in) {
            return new CityCard(in);
        }

        @Override
        public CityCard[] newArray(int size) {
            return new CityCard[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CityCard cityCard = (CityCard) o;
        return this.text.equalsIgnoreCase(cityCard.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }
}
