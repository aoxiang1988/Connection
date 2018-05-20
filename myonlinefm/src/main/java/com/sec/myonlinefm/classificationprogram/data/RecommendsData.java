package com.sec.myonlinefm.classificationprogram.data;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SRC-TJ-MM-BinYang on 2018/5/7.
 */

public class RecommendsData {

    private String brief_name; //简称
    private String name; //推荐类标题，特殊值“banner”表示数据属于banner推荐位

    public void setBriefName(String brief_name) {
        this.brief_name = brief_name;
    }

    public String getBriefName() {
        return brief_name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private List<Recommends> recommendsList = null; //推荐节目列表
    public void setRecommendsList() {
        recommendsList = new ArrayList<>();
    }
    public List<Recommends> getRecommendsList() {
        return recommendsList;
    }

    public static class Recommends {

        private String title; //名称
        private int id; //推荐电台id
        private String thumb; //原图地址
        private String thumbs; //节目缩略图。没有时为null，有的时候有三个字段：small, medium, large，分别代表小中大三个分辨率
        private String update_time; //更新时间
        private String sub_title; //副标题
        private int object_id; //recommends.detail的节目id

        public void setTitle(String title) {
            this.title = title;
        }
        public void setId(int id) {
            this.id = id;
        }
        public void setThumb(String thumb) {
            this.thumb = thumb;
        }
        public void setThumbs(String thumbs) {
            this.thumbs = thumbs;
        }
        public void setUpdateTime(String update_time) {
            this.update_time = update_time;
        }
        public void setSubTitle(String sub_title) {
            this.sub_title = sub_title;
        }
        public void setObjectID(int object_id) {
            this.object_id = object_id;
        }

        public String getTitle() {
            return title;
        }
        public int getId () {
            return id;
        }
        public String getThumb() {
            return thumb;
        }
        public String getThumbs() {
            return thumbs;
        }
        public String getUpdateTime() {
            return update_time;
        }
        public String getSubTitle() {
            return sub_title;
        }
        public int getObjectID( ) {
            return object_id;
        }

        private Detail detail = null; //有多重类型，通过type区分：program_ondemand: 点播节目；topic: 专题；activity: 活动
        public void setDetail() {
            detail = new Detail();
        }
        public Detail getDetail() {
            return detail;
        }
        public class Detail {

            private int channel_star;
            private int chatgroup_id;
            private String description;
            private int duration;
            private int id;
            private int original_fee;
            private int price;
            private String redirect_url;
            private String sale_status;
            private String thumbs;
            private String title;
            private int sequence;
            private String type;
            private String update_time;

            public void setType(String type) {
                this.type = type;
            }

            public String getType() {
                return type;
            }

            public void setUpdateTime(String update_time) {
                this.update_time = update_time;
            }

            public String getUpdateTime() {
                return update_time;
            }

            public void setSaleStatus(String sale_status) {
                this.sale_status = sale_status;
            }

            public String getSaleStatus() {
                return sale_status;
            }

            public void setThumbs(String thumbs) {
                this.thumbs = thumbs;
            }

            public String getThumbs() {
                return thumbs;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getTitle() {
                return title;
            }

            public void setSequence(int sequence) {
                this.sequence = sequence;
            }

            public int getSequence() {
                return sequence;
            }

            public void setPrice(int price) {
                this.price = price;
            }

            public int getPrice() {
                return price;
            }

            public void setRedirectUrl(String redirect_url) {
                this.redirect_url = redirect_url;
            }

            public String getRedirectUrl() {
                return redirect_url;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getId() {
                return id;
            }

            public void setOriginalFee(int original_fee) {
                this.original_fee = original_fee;
            }

            public int getOriginalFee() {
                return original_fee;
            }

            public void setChannelStar(int channel_star) {
                this.channel_star = channel_star;
            }

            public int getChannelStar() {
                return channel_star;
            }

            public void setChatGroupID(int chatgroup_id) {
                this.chatgroup_id = chatgroup_id;
            }

            public int getChatGroupID() {
                return chatgroup_id;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getDescription() {
                return description;
            }

            public void setDuration(int duration) {
                this.duration = duration;
            }

            public int getDuration() {
                return duration;
            }
        }

        private ParentInfo parent_info = null; //推荐节目的父类数据
        public void setParentInfo() {
            parent_info = new ParentInfo();
        }
        public ParentInfo getParentInfo() {
            return parent_info;
        }
        public class ParentInfo {
            private int parent_id;
            private String parent_name;

            public void setParentID(int parent_id) {
                this.parent_id = parent_id;
            }
            public void setParentName(String parent_name) {
                this.parent_name = parent_name;
            }

            public int getParentID() {
                return parent_id;
            }
            public String getParentName() {
                return parent_name;
            }

            private ParentExtra parentExtra;
            public void setParentExtra() {
                parentExtra = new ParentExtra();
            }
            public ParentExtra getParentExtra() {
                return parentExtra;
            }

            public class ParentExtra {
                private int category_id;
                private String tag;

                public void setCategoryID(int category_id) {
                    this.category_id = category_id;
                }
                public void setTag(String tag) {
                    this.tag = tag;
                }

                public int getCategoryID() {
                    return category_id;
                }
                public String getTag() {
                    return tag;
                }
            }
        }
    }
}
