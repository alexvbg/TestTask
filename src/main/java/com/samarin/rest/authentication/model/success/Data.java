
package com.samarin.rest.authentication.model.success;

import com.google.gson.annotations.SerializedName;
import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Data {

    @SerializedName("ssid")
    private String mSsid;

    public String getSsid() {
        return mSsid;
    }

    public void setSsid(String ssid) {
        mSsid = ssid;
    }

}
