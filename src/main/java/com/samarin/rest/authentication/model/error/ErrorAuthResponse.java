
package com.samarin.rest.authentication.model.error;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ErrorAuthResponse {

    @SerializedName("errors")
    private List<Error> mErrors;

    public List<Error> getErrors() {
        return mErrors;
    }

    public void setErrors(List<Error> errors) {
        mErrors = errors;
    }

}
