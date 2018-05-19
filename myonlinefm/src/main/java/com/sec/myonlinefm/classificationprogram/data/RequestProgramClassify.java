package com.sec.myonlinefm.classificationprogram.data;

import java.io.Serializable;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/4/13.
 */

public class RequestProgramClassify implements Serializable {
    private int id = 0;
    private String name = null;
    private int sectionId = 0;

    public void setId(int id) {
        this.id = id;
    }

    public int getId () {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public int getSectionId() {
        return sectionId;
    }
}
