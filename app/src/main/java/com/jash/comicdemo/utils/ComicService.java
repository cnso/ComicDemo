package com.jash.comicdemo.utils;


import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface ComicService {
    @GET("index.htm")
    Observable<ResponseBody> getHome();
    @GET("comiclist/{comicId}/index.htm")
    Observable<ResponseBody> getComic(@Path("comicId") long comicId);
    @GET("http://so.kukudm.com/search.asp")
    Observable<ResponseBody> searchComic(@Query("page") int page, @Query(value = "kw", encoded = true) String keyword);
}
