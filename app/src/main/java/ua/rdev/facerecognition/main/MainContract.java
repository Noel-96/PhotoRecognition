package ua.rdev.facerecognition.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by Руслан on 25.03.2018.
 */

public interface MainContract {
    interface View {
        void showErrorMessage(int errorType);

        void setProgressIndicator(boolean active);

        void showImage(Bitmap bitmap);

        void setRecognitionData(Model model);

        void displayPickArea();

        boolean isNetworkConnected();


        void openImagePicker(Intent intent);

        void openCamera();
    }

    interface UserActionsListener {

        void pickPhoto();

        void recognize();

        void openCamera();

        void onImageResult(Uri imageUri);

        void errorLoadingPhoto();

        void cancelRequest();
    }
}
