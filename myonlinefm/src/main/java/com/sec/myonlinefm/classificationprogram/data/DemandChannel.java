package com.sec.myonlinefm.classificationprogram.data;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/4/17.
 */

public class DemandChannel implements Serializable {

    public static final String TYPE_CHANNEL_ON_DEMAND = "channel_ondemand";
    public static final String TYPE_CHANNEL_LIVE = "channel_live";

    public static final int FREE_SALE_TYPE = 0;
    public static final int BOUGHT_SALE_TYPE = 1;
    public static final int UN_BOUGHT_SALE_TYPE = 2;

    private int category_id; //分类id
    private int id;    //专辑id
    private int total; //专辑总数
    private int program_count; //节目数量
    private int sale_type; //付费类型 0免费，1已购买，2未购买
    private int is_finished;
    private int ordered;

    private String description; //专辑简介
    private String playcount; //播放次数
    private String title;  //标题
    private String type;  //类型：channel_ondemand表示专辑；channel_live表示电台
    private String update_time; //更新时间
    private String latest_program; //最后一个节目
    private String tags;
    private Bitmap thumbs;
    private String thumbsUrl;

    public void setCategoryId(int category_id) {
        this.category_id = category_id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setTotal(int total) {
        this.total = total;
    }
    public void setProgramCount(int program_count) {
        this.program_count = program_count;
    }
    public void setSaleType(int sale_type) {
        this.sale_type = sale_type;
    }
    public void setIsFinished(int is_finished) {
        this.is_finished = is_finished;
    }
    public void setOrdered(int ordered) {
        this.ordered = ordered;
    }

    public int getCategoryId() {
        return category_id;
    }
    public int getId() {
        return id;
    }
    public int getTotal() {
        return total;
    }
    public int getProgramCount() {
        return program_count;
    }
    public int getSaleType() {
        return sale_type;
    }
    public int getIsFinished() {
        return is_finished;
    }
    public int getOrdered() {
        return ordered;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public void setPlayCount(String playcount) {
        this.playcount = playcount;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setUpdateTime(String update_time) {
        this.update_time = update_time;
    }
    public void setLatestProgram(String latest_program) {
        this.latest_program = latest_program;
    }
    public void setTags(String tags) {
        this.tags = tags;
    }
    public void setThumbs(Bitmap thumbs) {
        this.thumbs = thumbs;
    }

    public String getDescription(){
        return description;
    }
    public String getPlayCount(){
        return playcount;
    }
    public String getTitle(){
        return title;
    }
    public String getType(){
        return type;
    }
    public String getUpdateTime(){
        return update_time;
    }
    public String getLatestProgram(){
        return latest_program;
    }
    public String getTags(){
        return tags;
    }
    public Bitmap getThumbs(){
        return thumbs;
    }

    private PurchaseItem purchase_item = null;
    private Detail detail = null;
    private PodCasters podCasters = null;

    public void setPodCasters() {
        podCasters = new PodCasters();
    }

    public PodCasters getPodCasters() {
        return podCasters;
    }

    public void setPurchaseItem() {
        purchase_item = new PurchaseItem();
    }
    public PurchaseItem getPurchaseItem() {
        return purchase_item;
    }

    public void setDetail() {
        detail = new Detail();
    }
    public Detail getDetail() {
        return detail;
    }

    public void setThumbsUrl(String thumbsUrl) {
        this.thumbsUrl = thumbsUrl;
    }

    public String getThumbsUrl() {
        return thumbsUrl;
    }

    public class PurchaseItem {
        private float fee;
        private float original_fee;

        public void setFee(float fee) {
            this.fee = fee;
        }

        public float getFee() {
            return fee;
        }

        public void setOriginalFee(float original_fee) {
            this.original_fee = original_fee;
        }

        public float getOriginalFee() {
            return original_fee;
        }
    }

    public class Detail {

        private int program_count;

        private String fav_count;
        private String playcount;

        public void setProgramCount(int program_count) {
            this.program_count = program_count;
        }

        public int getProgramCount() {
            return program_count;
        }

        public void setFavCount(String fav_count) {
            this.fav_count = fav_count;
        }

        public String getFavCount() {
            return fav_count;
        }

        public void setPlayCount(String playcount) {
            this.playcount = playcount;
        }

        public String getPlayCount() {
            return playcount;
        }

        private List<AuthorsBroadcasters> authors = null;
        private List<AuthorsBroadcasters> broadcasters = null;
        private List<PodCasters> podCasters = null;

        public void setAuthors() {
            authors = new ArrayList<>();
        }

        public List<AuthorsBroadcasters> getAuthors() {
            return authors;
        }

        public void setBroadcasters() {
            broadcasters = new ArrayList<>();
        }

        public List<AuthorsBroadcasters> getBroadcasters() {
            return broadcasters;
        }

        public void setPodCasters() {
            podCasters = new ArrayList<>();
        }

        public List<PodCasters> getPodCasters() {
            return podCasters;
        }

    }

    public static class AuthorsBroadcasters {

        private int id;

        private String username;
        private Bitmap thumb;
        private String weibo_name;
        private String weibo_id;
        private String qq_name;
        private String qq_id;

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setWeiboName(String weibo_name) {
            this.weibo_name = weibo_name;
        }

        public String getWeiboName() {
            return weibo_name;
        }

        public void setWeiboId(String weibo_id) {
            this.weibo_id = weibo_id;
        }

        public String getWeiboId() {
            return weibo_id;
        }

        public void setQQName(String qq_name) {
            this.qq_name = qq_name;
        }

        public String getQQName() {
            return qq_name;
        }

        public void setQQId(String qq_id) {
            this.qq_id = qq_id;
        }

        public String getQQId() {
            return qq_id;
        }

        public void setUserName(String username) {
            this.username = username;
        }

        public String getUserName() {
            return username;
        }

        public void setThumb(Bitmap thumb) {
            this.thumb = thumb;
        }

        public Bitmap getThumb() {
            return thumb;
        }
    }

    public static class PodCasters {

        private int id; //主播id
        private int fan_num; //粉丝数。
        private int user_id; //主播qingting_id。可以用来查询主播信息

        private String desc; //主播简介
        private Bitmap img_url; //主播头像。
        private String nickname; //主播名字

        public void setId(int id) {
            this.id = id;
        }
        public void setFanNum(int fan_num) {
            this.fan_num = fan_num;
        }
        public void setUserId(int user_id) {
            this.user_id = user_id;
        }

        public int getId() {
            return id;
        }
        public int getFanNum() {
            return fan_num;
        }
        public int getUserId() {
            return user_id;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
        public void setImgUrl(Bitmap img_url) {
            this.img_url = img_url;
        }
        public void setNickName(String nickname) {
            this.nickname = nickname;
        }

        public String getDesc() {
            return desc;
        }
        public Bitmap getImgUrl() {
            return img_url;
        }
        public String getNickName() {
            return nickname;
        }

    }
}
