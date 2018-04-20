package com.sec.connecttoapilibrary.onlinefm.liveRadioData;

import java.io.Serializable;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/4/10.
 */

public class ErrorCode implements Serializable {
    private int error_code;
    private String error_string;

    public void setErrorCode(int error_code) {
        this.error_code = error_code;
    }
    public int getErrorCode() {
        return error_code;
    }

    public void setErrorString(String error_string) {
        this.error_string = error_string;
    }
    public String getErrorString() {
        return error_string;
    }
}
