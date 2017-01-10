package com.jash.comicdemo.utils;


import com.jash.comicdemo.entities.Chapter;
import com.jash.comicdemo.entities.Comic;
import com.jash.comicdemo.entities.Response;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface ComicService {
    @GET("comic_home/")
    Observable<Response<List<Comic>>> getHome();
    @GET("comic_home/info")
    Observable<Response<Comic>> getComic(@Query("id") long comicId);
    @GET("comic_home/search")
    Observable<Response<List<Comic>>> searchComic(@Query("page") int page, @Query(value = "keyword") String keyword);
    @GET("comic_home/pager_list")
    Observable<Response<Chapter>> getPicture(@Query("comicId") long comicId, @Query("chapterId") long chapterId, @Query("start") int picture, @Query("count") int count);
}
