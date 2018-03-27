package ua.rdev.facerecognition.main;

import java.util.ArrayList;


public class Photo {
    ArrayList<Tag> tags;
    float width;
    float height;

    class Tag {
        ArrayList<Uid> uids;
        boolean confirmed;
        float width;
        float height;
        String tid;
        Center center;
    }

    class Uid {
        String uid;
        int confidence;

    }

    class Center {
        float x;
        float y;
    }
}
