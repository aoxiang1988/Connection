package com.sec.myonlinefm.data;

import android.graphics.Bitmap;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/2/26.
 */

public class SearchType {
    public static class ChannelLive {
        private int id;	//电台id
        private String title;	//标题
        private String type;	//类型
        private String freqs;	//调频，暂时不用
        private String category_name;	//所属分类名字
        private int category_id;	//所属分类id
        private Bitmap cover;	//配图
        private String keywords;	//搜索关键字

        public void setChannelLiveId(int id){
            this.id = id;
        }
        public int getChannelLiveId(){
            return id;
        }

        public void setChannelLiveTitle(String title){
            this.title = title;
        }
        public String getChannelLiveTitle(){
            return title;
        }

        public void setChannelLiveType(String type){
            this.type = type;
        }
        public String getChannelLiveType(){
            return type;
        }

        public void setChannelLiveFreqs(String freqs){
            this.freqs = freqs;
        }
        public String getChannelLiveFreqs(){
            return freqs;
        }

        public void setChannelLiveCategoryName(String category_name){
            this.category_name = category_name;
        }
        public String getChannelLiveCategoryName(){
            return category_name;
        }

        public void setChannelLiveCategoryId(int category_id){
            this.category_id = category_id;
        }
        public int getChannelLiveCategoryId(){
            return category_id;
        }

        public void setChannelLiveCover(Bitmap cover){
            this.cover = cover;
        }
        public Bitmap getChannelLiveCover(){
            return cover;
        }

        public void setChannelLiveKeywords(String keywords){
            this.keywords = keywords;
        }
        public String getChannelLiveKeywords(){
            return keywords;
        }
    }
    public static class ProgramLive {
        private int id;	//节目id
        private String title;	//标题
        private int parent_id;	//所属电台id
        private String parent_type;	//所属电台类型，channel表示直播电台
        private String category_name;	//所属分类名字
        private int category_id;	//所属分类id
        private String type;	//类型
        private Bitmap cover;	//配图

        public void setProgramLiveId(int id){
            this.id = id;
        }
        public int getProgramLiveId(){
            return id;
        }

        public void setProgramLiveTitle(String title){
            this.title = title;
        }
        public String getProgramLiveTitle(){
            return title;
        }

        public void setProgramLiveType(String type){
            this.type = type;
        }
        public String getProgramLiveType(){
            return type;
        }

        public void setProgramLiveParentId(int parent_id){
            this.parent_id = parent_id;
        }
        public int getProgramLiveParentId(){
            return parent_id;
        }

        public void setProgramLiveCategoryName(String category_name){
            this.category_name = category_name;
        }
        public String getProgramLiveCategoryName(){
            return category_name;
        }

        public void setProgramLiveCategoryId(int category_id){
            this.category_id = category_id;
        }
        public int getProgramLiveCategoryId(){
            return category_id;
        }

        public void setProgramLiveCover(Bitmap cover){
            this.cover = cover;
        }
        public Bitmap getProgramLiveCover(){
            return cover;
        }

        public void setProgramLiveParentType(String parent_type){
            this.parent_type = parent_type;
        }
        public String getProgramLiveParentType(){
            return parent_type;
        }
    }
}
