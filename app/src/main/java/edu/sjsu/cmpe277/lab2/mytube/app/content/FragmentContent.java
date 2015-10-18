package edu.sjsu.cmpe277.lab2.mytube.app.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.sjsu.cmpe277.lab2.mytube.app.VideoItem;

public class FragmentContent {
    /**
     * An array of video items.
     */
    public static ArrayList<VideoItem> ITEMS = new ArrayList<VideoItem>();

    /**
     * A map of video items, by ID.
     */
    public static Map<String, VideoItem> ITEM_MAP = new HashMap<String, VideoItem>();

    static {
        // Add 3 sample items.
        addItem(new VideoItem("testTitle", "http://www.canon-europe.com/images/Android-logo_tcm13-1232684.png", 1, "1"));
        //addItem(new VideoItem("2", "Item 2"));
        //addItem(new VideoItem("3", "Item 3"));
    }

    private static void addItem(VideoItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getVideoId(), item);
    }


}
