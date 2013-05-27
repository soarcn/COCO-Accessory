package com.cocosw.accessory.utils;

import android.content.Context;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationUtils {

    /**
     * 根据经度和纬度返回具体的地址信息
     *
     * @param longitude
     * @param latitude
     * @return
     * @throws IOException
     */
    public String getAddressByLatlong(Context context, double longitude, double latitude) throws IOException {
        Geocoder ge = new Geocoder(context, Locale.CHINA);
        List<android.location.Address> addrList = ge.getFromLocation(latitude, longitude, 1);
        android.location.Address address = addrList.get(0);
        String addressMessage = "";
        for (int i = 0; i < address.getMaxAddressLineIndex() - 1; i++) {
            addressMessage += address.getAddressLine(i);
        }
        return addressMessage;
    }


}
