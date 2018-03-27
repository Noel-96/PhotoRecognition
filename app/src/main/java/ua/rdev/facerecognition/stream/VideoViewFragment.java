package ua.rdev.facerecognition.stream;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.net.HttpURLConnection;
import java.net.URL;

import ua.rdev.facerecognition.R;

public class VideoViewFragment extends Fragment {

    private static final String TAG = "MjpegActivity";

    private MjpegView mv;

    String URL = "http://217.197.157.7:7070/axis-cgi/mjpg/video.cgi?resolution=640x480";

    //another working public cam

    // String URL = "http://webcam01.bigskyresort.com/mjpg/video.mjpg";
    //String URL = "http://webcams.hotelcozumel.com.mx:6001/axis-cgi/mjpg/video.cgi?resolution=320x240";
    //String URL = "http://200.36.58.250/mjpg/video.mjpg?resolution=640x480";
    //String URL = "http://192.168.43.134:5432/XMLParser/Video1.mp4";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.video_view, container, false);

        mv = (MjpegView) viewGroup.findViewById(R.id.surfaceView);
        new DoRead().execute(URL);
        return viewGroup;
    }

    public void onPause() {
        super.onPause();
        mv.stopPlayback();
    }

    public void onResume() {
        super.onResume();
    }

    public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {
        protected MjpegInputStream doInBackground(String... Url) {
            //TODO: if camera has authentication deal with it and don't just not work

            Log.d(TAG, "1. Sending http request");
            try {
                java.net.URL url = new URL(Url[0]); // here is your URL path
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                int responseCode = conn.getResponseCode();

                Log.d(TAG, "2. Request finished, status = " + responseCode);
                if (responseCode == 401) {
                    //You must turn off camera User Access Control before this will work
                    return null;
                }
                return new MjpegInputStream(conn.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed-ClientProtocolException", e);
                //Error connecting to camera
            }
            return null;
        }

        protected void onPostExecute(MjpegInputStream result) {
            if (result != null) {
                mv.setSource(result);
                mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
                mv.showFps(true);
            }
        }
    }
}