package edu.sjsu.cmpe277.lab2.mytube.app;

import android.content.Context;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class YouTubeService {

    private YouTube youtube;


    public YouTubeService(Context context, String token) {
        GoogleCredential credential = new GoogleCredential().setAccessToken(token);
        youtube = new YouTube.Builder(AndroidHttp.newCompatibleTransport(), JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName(context.getString(R.string.app_name)).build();
    }

    List<VideoItem> search(String keyword) {
        try {
            //TODO: liping, please add required fields for video list
            YouTube.Search.List search = youtube.search().list("id,snippet");
            search.setType("video");
            search.setFields("items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url)");
            search.setQ(keyword);

            SearchListResponse response = search.execute();
            List<SearchResult> results = response.getItems();
            List<VideoItem> items = new ArrayList<VideoItem>();

            for (SearchResult result : results) {
                VideoItem item = new VideoItem();
                item.setTitle(result.getSnippet().getTitle());
                item.setDescription(result.getSnippet().getDescription());
                item.setThumbnailURL(result.getSnippet().getThumbnails().getDefault().getUrl());
                item.setId(result.getId().getVideoId());
                items.add(item);
            }

            return items;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    void play(VideoItem videoItem);
//    void createFavoriteList();
//    List<VideoItem> getFavoriteList();
//    void addFavorite(VideoItem videoItem);
}
