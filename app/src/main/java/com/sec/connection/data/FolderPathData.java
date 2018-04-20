package com.sec.connection.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by SRC-TJ-MM-BinYang on 2017/10/26.
 * 创建一个关于 folder path 的 list
 */

public class FolderPathData {
    private List<Audio> mAllList;
    private List<String> mPathList;
    private Map<String, List<Audio>> mPathMap;
    private Map<String, Boolean> mPathSelected;

    public FolderPathData (List<Audio> mAllList){
        this.mAllList = new ArrayList<>();
        this.mAllList = mAllList; //将要处理的list先期传入

        mPathList = setPathList();
        mPathMap = setPathMap();
        mPathSelected = init();
    }



    /**获取路径的list**/
    private List<String> setPathList(){
        List<String> mPathList = new ArrayList<>();
        List<String> artists1 = new ArrayList<>();
//        String[] temp = new String[this.mAllList.size()];
        for (int a = 0; a < this.mAllList.size(); a++) {
            artists1.add( this.mAllList.get(a).getFolderPath());
//            temp[a] = this.mAllList.get(a).getTitle();
        }
        mPathList = removeDuplicate(artists1);
        return mPathList;
    }

    /**去重**/
    private static List<String> removeDuplicate(List<String> list) {
        Set<String> set = new LinkedHashSet<>();
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;
    }

    /**获取各个路径与音乐的map集合**/
    private Map<String, List<Audio>> setPathMap() {
        Map<String, List<Audio>> mPathMap = new HashMap<>();
        List<Audio> temp;
        for (int b = 0; b < mPathList.size(); b++) {
            int m = 0;
            temp = new ArrayList<>();
            for (int a = 0; a < mAllList.size(); a++) {
                if (Objects.equals(mPathList.get(b), mAllList.get(a).getFolderPath())) {
                    temp.add(m, mAllList.get(a));
                    m = m+1;
                }
            }
            mPathMap.put(mPathList.get(b), temp);
        }
        return mPathMap;
    }

    /**初始化文件夹选择条件**/
    private Map<String, Boolean> init() {
        Map<String, Boolean> mPathSelected = new HashMap<>();
        for (int b = 0; b < mPathList.size(); b++) {
            mPathSelected.put(mPathList.get(b),false);
        }
        return mPathSelected;
    }

    /**对外函数**/
    public List<String> getPathList(){
        return mPathList;
    }

    public boolean isPathSelected(int position) {
        String key = mPathList.get(position);
        return mPathSelected.get(key);
    }
    public void setWhicPathOn(int position){
        String key = mPathList.get(position);
        if(mPathSelected.get(key))
            mPathSelected.put(key,false);
        else
            mPathSelected.put(key,true);
    }

    public void setallpahtoff(){
        for (int b = 0; b < mPathList.size(); b++) {
            String key = mPathList.get(b);
            if(mPathSelected.get(key))
                mPathSelected.put(key,false);
        }
    }

    public List<Audio> getfilterlist(){
        List<Audio> filterlist = new ArrayList<>();
        for (int b = 0; b < mPathList.size(); b++) {
            String key = mPathList.get(b);
            if(mPathSelected.get(key)) {
                for (int i=0; i<mPathMap.get(key).size(); i++){
                    filterlist.add(mPathMap.get(key).get(i));
                }
            }
        }
        return filterlist;
    }
    public int getlistlangth(int position){
        String key = mPathList.get(position);
        return mPathMap.get(key).size();
    }
}
