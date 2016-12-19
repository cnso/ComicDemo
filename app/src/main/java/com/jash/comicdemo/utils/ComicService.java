package com.jash.comicdemo.utils;


import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface ComicService {
    @GET("index.htm")
    Observable<ResponseBody> getHome();
    @GET("comiclist/{comicId}/index.htm")
    Observable<ResponseBody> getComic(@Path("comicId") long comicId);
    @GET("http://so.kukudm.com/search.asp")
    Observable<ResponseBody> searchComic(@Query("page") int page, @Query(value = "kw", encoded = true) String keyword);
    @GET("comiclist/{comicId}/{chapterId}/1.htm")
    Observable<ResponseBody> getPicture(@Path("comicId") long comicId, @Path("chapterId") long chapterId);
    @GET("comiclist/{comicId}/{chapterId}/{picture}.htm")
    Observable<ResponseBody> getPicture(@Path("comicId") long comicId, @Path("chapterId") long chapterId, @Path("picture") long picture);
    @HEAD
    Observable<Response<Void>> tryPicture(@Url String url);
}
