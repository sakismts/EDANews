package com.sakismts.athanasiosmoutsioulis.finalproject;

/**
 * Created by AthanasiosMoutsioulis on 23/02/16.
 */
public class NewsItem {


    private String title;
    private String short_description;
    private String date;
    private String image_url;
    private int record_id;
    private String description;
    private String webUrl;

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public int getRecord_id() {
        return record_id;
    }

    public void setRecord_id(int record_id) {
        this.record_id = record_id;
    }

    public NewsItem(String title, int record_id, String short_description, String date, String image_url, String webUrl) {
        this.title = title;
        this.date = date;
        this.short_description = short_description;
        this.record_id = record_id;
        this.image_url= image_url;
        this.webUrl = webUrl;
    }

    public NewsItem(String title, String short_description, String date, String image_url, int record_id, String description, String webUrl) {
        this.title = title;
        this.short_description = short_description;
        this.date = date;
        this.image_url = image_url;
        this.record_id = record_id;
        this.description = description;
        this.webUrl = webUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
