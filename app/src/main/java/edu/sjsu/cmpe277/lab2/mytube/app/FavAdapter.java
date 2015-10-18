package edu.sjsu.cmpe277.lab2.mytube.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

//adapter used to populate list view
class FavAdapter extends ArrayAdapter<VideoItem> {
    public FavAdapter(Context context, int resources, ArrayList<VideoItem> users){
        super(context, resources, users);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        VideoItem video = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fav_list_view, parent, false);
        }

        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView numOfViews = (TextView) convertView.findViewById(R.id.num_of_views);
        TextView publishDate = (TextView) convertView.findViewById(R.id.publish_date);
        ImageView thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);

        // Populate the data into the template view using the data object
        title.setText(video.getTitle());
        numOfViews.setText(Integer.toString(video.getNumOfViews()));
        publishDate.setText(video.getPublishDate());
        new DownloadImageTask(thumbnail).execute(video.getThumbnailURL());
        // Return the completed view to render on screen
        return convertView;
    }

    //class used to download image for thumbnail
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            int width=100;
            int height=100;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return Bitmap.createScaledBitmap(mIcon11, width, height, true);
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }


    }
}
