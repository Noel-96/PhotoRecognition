package ua.rdev.facerecognition.main;

import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.rdev.facerecognition.Utill;
import ua.rdev.facerecognition.net.ApiServiceImpl;

public class MainPresenter implements MainContract.UserActionsListener {
    String photoPath;
    MainContract.View view;
    ApiServiceImpl apiService = new ApiServiceImpl();

    public MainPresenter(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void pickPhoto() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
        view.openImagePicker(chooserIntent);
    }

    @Override
    public void recognize() {
        if (photoPath == null) {
            view.showErrorMessage(ErrorType.ERROR_PHOTO_IS_EMPTY);
        } else {
            view.setProgressIndicator(true);
            apiService.recognizePhoto(photoPath, new Callback<Model>() {
                @Override
                public void onResponse(Call<Model> call, Response<Model> response) {
                    if (response.isSuccessful()) {
                        if (response.body().photos.get(0).tags.size() == 0) {
                            view.showErrorMessage(ErrorType.ERROR_PHOTO_WITHOUT_FACES);
                        } else if (response.body().photos.get(0).tags.get(0).uids.get(0).confidence < 35) {
                            view.showErrorMessage(ErrorType.ERROR_ANOTHER_FACE);
                        }
                        view.setProgressIndicator(false);
                        view.setRecognitionData(response.body());
                    } else {
                        view.setProgressIndicator(false);
                        //  view.showErrorMessage("Error");
                    }
                }

                @Override
                public void onFailure(Call<Model> call, Throwable t) {
                    if (!view.isNetworkConnected()) {
                        view.showErrorMessage(ErrorType.ERROR_NO_NET_CONNECTION);
                    } else if (t.getMessage().equals("Socket closed")) {
                    } else {
                        view.showErrorMessage(ErrorType.ERROR_REQUEST_TIMEOUT);
                    }
                    view.setProgressIndicator(false);
                }
            });
        }
    }


    @Override
    public void openCamera() {
        view.openCamera();
    }

    @Override
    public void onImageResult(Uri encodedPath) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = ((MainActivity) view).getContentResolver().query(encodedPath, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            photoPath = cursor.getString(column_index);
            Log.d("tag", "FIle: " + photoPath);
        }
        cursor.close();
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        int rotation = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotation = 90;
                break;
            case ExifInterface.ORIENTATION_NORMAL:
                rotation = 0;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotation = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotation = 270;
                break;
        }
        view.showImage(Utill.createBitmapFromPath(photoPath, rotation));
    }

    @Override
    public void errorLoadingPhoto() {
        view.showErrorMessage(ErrorType.ERROR_PHOTO_IS_EMPTY);
        photoPath = null;
        view.displayPickArea();
    }

    @Override
    public void cancelRequest() {
        apiService.cancelRequest();
    }
}
