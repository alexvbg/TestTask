
package com.samarin.rest.authentication.model.error;

import com.google.gson.annotations.SerializedName;
import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Error {

    @SerializedName("code")
    private Long mCode;
    @SerializedName("title")
    private String mTitle;

    public Long getCode() {
        return mCode;
    }

    public void setCode(Long code) {
        mCode = code;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

}
