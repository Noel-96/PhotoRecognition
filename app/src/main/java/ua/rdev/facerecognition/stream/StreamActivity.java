package ua.rdev.facerecognition.stream;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ua.rdev.facerecognition.R;
import ua.rdev.facerecognition.stream.VideoViewFragment;

public class StreamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        VideoViewFragment fragment = new VideoViewFragment();
        ft.add(R.id.contentPanels, fragment);
        ft.commit();
    }
}
