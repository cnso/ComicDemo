package com.jash.comicdemo.utils;

import android.databinding.ObservableInt;

import org.greenrobot.greendao.converter.PropertyConverter;

public class ObservableIntConvert implements PropertyConverter<ObservableInt, Integer> {

    @Override
    public ObservableInt convertToEntityProperty(Integer databaseValue) {
        return new ObservableInt(databaseValue);
    }

    @Override
    public Integer convertToDatabaseValue(ObservableInt entityProperty) {
        return entityProperty.get();
    }
}
