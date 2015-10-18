package edu.sjsu.cmpe277.lab2.mytube.app;

import android.content.Context;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;

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

    public List<VideoItem> search(String keyword) {
        try {
            YouTube.Search.List searchCommand = youtube.search().list("id,snippet");
            searchCommand.setType("video");
            searchCommand.setFields("items(id/videoId,snippet/title,snippet/publishedAt,snippet/thumbnails/default/url)");
            searchCommand.setQ(keyword);

            SearchListResponse response = searchCommand.execute();
            List<SearchResult> results = response.getItems();
            List<VideoItem> items = new ArrayList<VideoItem>();

            for (SearchResult result : results) {
                VideoItem item = new VideoItem();
                item.setTitle(result.getSnippet().getTitle());
//                item.setNumOfViews(result.getSnippet().get);
                item.setPublishDate(result.getSnippet().getPublishedAt().toString());
                item.setThumbnailURL(result.getSnippet().getThumbnails().getDefault().getUrl());
                item.setVideoIdId(result.getId().getVideoId());
                items.add(item);
            }

            return items;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String playlistInsert(String title) {
        PlaylistSnippet playlistSnippet = new PlaylistSnippet();
        playlistSnippet.setTitle(title);
        playlistSnippet.setDescription("A private playlist created with the YouTube API v3");
        PlaylistStatus playlistStatus = new PlaylistStatus();
        playlistStatus.setPrivacyStatus("private");
        Playlist youTubePlaylist = new Playlist();
        youTubePlaylist.setSnippet(playlistSnippet);
        youTubePlaylist.setStatus(playlistStatus);

        try {
            YouTube.Playlists.Insert playlistInsertCommand =
                    youtube.playlists().insert("snippet,status", youTubePlaylist);

            Playlist playlistInserted = playlistInsertCommand.execute();
            return playlistInserted.getId();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getOrCreateFavorList() {
        try {
            YouTube.Playlists.List playlistListCommand = youtube.playlists().list("snippet,contentDetails");
            playlistListCommand.setMine(true);
            PlaylistListResponse playlistListResponse = playlistListCommand.execute();
            List<Playlist> playlists = playlistListResponse.getItems();
            Playlist favorPlaylist = null;
            for (Playlist playlist : playlists) {
                if (playlist.getSnippet().getTitle().equals("SJSU-CMPE-277")) {
                    favorPlaylist = playlist;
                    return favorPlaylist.getId();
                }
            }
            if (favorPlaylist == null) {
                return playlistInsert("SJSU-CMPE-277");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<VideoItem> getPlaylistVideos(String playlistId) {
        try {
            YouTube.PlaylistItems.List playlistItemCommand =
                    youtube.playlistItems().list("snippet");
            playlistItemCommand.setPlaylistId(playlistId);
            PlaylistItemListResponse playlistItemListResponse = playlistItemCommand.execute();

            List<PlaylistItem> results = playlistItemListResponse.getItems();
            List<VideoItem> items = new ArrayList<VideoItem>();

            for (PlaylistItem result : results) {
                VideoItem item = new VideoItem();
                item.setTitle(result.getSnippet().getTitle());
                item.setPublishDate(result.getSnippet().getPublishedAt().toString());
                item.setThumbnailURL(result.getSnippet().getThumbnails().getDefault().getUrl());
                item.setVideoIdId(result.getSnippet().getResourceId().getVideoId());
                item.setFavorPlaylistItemId(result.getId());
                items.add(item);
            }

            return items;


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean playlistItemInsert(String playlistId, String videoId) {
        // Define a resourceId that identifies the video being added to the
        // playlist.
        ResourceId resourceId = new ResourceId();
        resourceId.setKind("youtube#video");
        resourceId.setVideoId(videoId);

        // Set fields included in the playlistItem resource's "snippet" part.
        PlaylistItemSnippet playlistItemSnippet = new PlaylistItemSnippet();
        playlistItemSnippet.setPlaylistId(playlistId);
        playlistItemSnippet.setResourceId(resourceId);

        // Create the playlistItem resource and set its snippet to the
        // object created above.
        PlaylistItem playlistItem = new PlaylistItem();
        playlistItem.setSnippet(playlistItemSnippet);

        // Call the API to add the playlist item to the specified playlist.
        // In the API call, the first argument identifies the resource parts
        // that the API response should contain, and the second argument is
        // the playlist item being inserted.
        PlaylistItem returnedPlaylistItem = null;
        try {
            YouTube.PlaylistItems.Insert playlistItemsInsertCommand =
                    youtube.playlistItems().insert("snippet", playlistItem);
            returnedPlaylistItem = playlistItemsInsertCommand.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnedPlaylistItem != null;
    }

    public boolean playlistItemDetele(String playlistId, String favorPlaylistItemId) {
        try {
            YouTube.PlaylistItems.Delete playlistItemsDeleteCommand = youtube.playlistItems().delete(favorPlaylistItemId);
            playlistItemsDeleteCommand.execute();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

//    void play(VideoItem videoItem);
}
