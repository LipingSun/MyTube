package edu.sjsu.cmpe277.lab2.mytube.app;

import java.util.List;


public interface YouTube {
    List<VideoItem> search(String keyword);
    void play(VideoItem videoItem);
    void createFavoriteList();
    List<VideoItem> getFavoriteList();
    void addFavorite(VideoItem videoItem);
}
