package com.quintessential.gypsy;
import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
public interface retrofitInterface {
    @GET("apitablet/")
    Call<List<Post>> getPosts();

    @POST("apitablet/")
    Call<Post> loginPost(
            @Body Post post
    );

    @Multipart
    @POST("apitablet/")
    Call<Post> logoutPost(
            @Part("userPass6") RequestBody userPass6,
            @Part MultipartBody.Part file
    );
    @Multipart
    @POST("apitablet/")
    Call<Post> logoutPostWithGypsyDB(
            @Part("userPass6") RequestBody userPass6,
            @Part MultipartBody.Part file,
            @Part MultipartBody.Part file2
    );
    @Multipart
    @POST("apitablet/")
    Call<Post> loginPostWithFailLogout(
            @Part("userEmail6") RequestBody userEmail6,
            @Part("userPass6") RequestBody userPass6,
            @Part("logoutDate6") RequestBody logoutDate6,
            @Part("logoutTime6") RequestBody logoutTime6,
            @Part MultipartBody.Part file
    );

}
