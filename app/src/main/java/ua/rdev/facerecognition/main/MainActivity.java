package ua.rdev.facerecognition.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ua.rdev.facerecognition.R;
import ua.rdev.facerecognition.stream.StreamActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MainContract.View {

    private MainContract.UserActionsListener mActionListener;
    CardView loadPhotoCard;
    ImageView previewPhoto;
    ImageButton streamBtn;
    TextView loadPhotoTv;
    RelativeLayout relativeLayout;

    RecyclerView recyclerView;

    Snackbar snackbar;

    int CAMERA = 1050;

    ProgressDialog dialog;
    MainRecyclerViewAdapter adapter;
    Bitmap preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        relativeLayout = findViewById(R.id.relative);
        mActionListener = new MainPresenter(this);

        recyclerView = findViewById(R.id.mainRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MainRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);

        loadPhotoCard = findViewById(R.id.loadPhotoCard);
        loadPhotoCard.setOnClickListener(this);

        streamBtn = findViewById(R.id.streamBtn);
        streamBtn.setOnClickListener(this);

        loadPhotoTv = findViewById(R.id.loadPhotoTv);

        previewPhoto = findViewById(R.id.preview_photo);
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.streamBtn) {
            startActivity(new Intent(this, StreamActivity.class));
        } else if (view.getId() == R.id.loadPhotoCard) {
            mActionListener.pickPhoto();
        } else if (view.getId() == R.id.recognize_btn) {
            mActionListener.recognize();
        } else if (view.getId() == R.id.openCameraBtn) {
            mActionListener.openCamera();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (data != null) {
                mActionListener.onImageResult(data.getData());
            } else {
                mActionListener.errorLoadingPhoto();
            }
        } else if (requestCode == CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                mActionListener.onImageResult(data.getData());
            }
        }

    }

    @Override
    public void showErrorMessage(int errorType) {
        snackbar = Snackbar.make(relativeLayout, "", 10000);
        snackbar.setActionTextColor(Color.GREEN);
        snackbar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        switch (errorType) {
            case ErrorType.ERROR_NO_NET_CONNECTION:
                snackbar.setText(R.string.no_net_error);
                snackbar.setAction("Refresh", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mActionListener.recognize();
                    }
                });
                break;
            case ErrorType.ERROR_ANOTHER_FACE:
                snackbar.setText(R.string.error_another_face);
                break;
            case ErrorType.ERROR_PHOTO_IS_EMPTY:
                snackbar.setText(R.string.select_a_picture);
                snackbar.setAction(R.string.select, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mActionListener.pickPhoto();
                    }
                });
                break;
            case ErrorType.ERROR_PHOTO_WITHOUT_FACES:
                snackbar.setText(R.string.cant_detect_face_error);
                break;
            case ErrorType.ERROR_REQUEST_TIMEOUT:
                snackbar.setText(R.string.request_time_error);
                break;
        }
        snackbar.show();

    }

    @Override
    public void setProgressIndicator(boolean active) {
        if (active) {
            dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.load_msg));
            dialog.setTitle("");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mActionListener.cancelRequest();
                }
            });
            dialog.show();
        } else {
            dialog.cancel();
        }
    }


    public void drawBounds(Model model) {
        //draw rect arround face
        Bitmap mutableBitmap = preview.copy(Bitmap.Config.ARGB_8888, true);
        Canvas tempCanvas = new Canvas(mutableBitmap);
        Paint myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        myPaint.setStrokeWidth(5);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setColor(Color.GREEN);

        for (int i = 0; i < model.photos.get(0).tags.size(); i++) {
            Photo.Tag tag = model.photos.get(0).tags.get(i);

            float height = tag.height * mutableBitmap.getHeight() / 100;
            float width = tag.width * mutableBitmap.getWidth() / 100;

            float centerX = tag.center.x * model.photos.get(0).width / 100;
            float centerY = tag.center.y * model.photos.get(0).height / 100;

            tempCanvas.drawBitmap(mutableBitmap, 0, 0, null);
            tempCanvas.drawRoundRect(new RectF(centerX - height / 2, (centerY - height / 2), centerX + width / 2, centerY + height / 2), 2, 2, myPaint);

        }
        previewPhoto.setImageDrawable(new BitmapDrawable(getResources(), mutableBitmap));

    }

    @Override
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void showImage(Bitmap bitmap) {
        preview = bitmap;
        previewPhoto.setImageBitmap(preview);
        loadPhotoTv.setVisibility(View.INVISIBLE);
        cleanRV();
    }

    @Override
    public void setRecognitionData(Model model) {
        drawBounds(model);
        adapter.setData(model);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void displayPickArea() {
        previewPhoto.setImageResource(R.drawable.ic_add_green_96dp);
        loadPhotoTv.setVisibility(View.VISIBLE);
        cleanRV();
    }

    private void cleanRV() {
        try {
            adapter.model.photos.clear();
        } catch (NullPointerException e) {
            /* DO NOTHING */
        }
        adapter.items = 0;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void openImagePicker(Intent intent) {
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), 0);
    }

    @Override
    public void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA);
    }
}
