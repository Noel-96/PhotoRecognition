package ua.rdev.facerecognition.net;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import ua.rdev.facerecognition.main.Model;

public interface ApiService {
    @Multipart
    @POST("recognize")
    Call<Model> postImage(@Part MultipartBody.Part image,
                          @Part("name") RequestBody name,
                          @Query("api_key") String key,
                          @Query("api_secret") String sekret,
                          @Query("uids") String uids);
}