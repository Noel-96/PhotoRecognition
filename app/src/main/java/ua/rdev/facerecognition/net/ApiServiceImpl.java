package ua.rdev.facerecognition.net;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ua.rdev.facerecognition.main.Model;

import static ua.rdev.facerecognition.main.MainActivity.getMimeType;

/**
 * Created by Руслан on 25.03.2018.
 */

public class ApiServiceImpl {
    ApiService apiService;
    public static String key = "tmhct8cgbk738mtpiqha6dcvfm";
    public static String sekret = "74kd0tql0bhvd87ngfop24bv28";
    String uids = "13cj015307_computer-engineering@mydocs," +
            "13cj015316_computer_engineering@mydocs," +
            "Dr_Felix_Agbetuyi_Engineering@mydocs," +
            "13cj015293_computer_engineering@mydocs," +
            "Dr_adewale_EIE@mydocs,HOD_DR_Matthews@mydocs,"+
            "Dr._Anthony_Adoghe_Engineering@mydocs";

    public ApiServiceImpl() {
        createClient();
    }

    Call<Model> req;

    public void recognizePhoto(String photoPath, Callback<Model> callBack) {
        File file = new File(photoPath);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/" + getMimeType(file.getAbsolutePath())), file);
        final MultipartBody.Part body = MultipartBody.Part.createFormData("0", file.getName(), reqFile);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");
        req = apiService.postImage(body, name, key, sekret, uids);

        req.enqueue(callBack);
    }

    public void cancelRequest() {
        req.cancel();
    }

    private void createClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS).addInterceptor(interceptor).build();
        apiService = new Retrofit.Builder()
                .baseUrl("https://api.skybiometry.com/fc/faces/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }
}
