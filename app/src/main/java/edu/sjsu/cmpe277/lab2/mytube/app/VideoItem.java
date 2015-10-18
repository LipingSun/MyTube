package edu.sjsu.cmpe277.lab2.mytube.app;

public class VideoItem {

    private String title;
    private String thumbnailURL;
    private String id;
    private int numOfViews;
    private String publishDate;

    public VideoItem(String title, String thumbnailURL, int numOfViews, String publishDate){
        this.title = title;
        this.thumbnailURL = thumbnailURL;
        this.numOfViews = numOfViews;
        this.publishDate = publishDate;
    }

    VideoItem() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNumOfViews() {
        return numOfViews;
    }

    public void setNumOfViews(int numOfViews) {
        this.numOfViews = numOfViews;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }
}
