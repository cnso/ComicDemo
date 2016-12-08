package com.jash.comicdemo.utils;

import android.util.Log;

import com.jash.comicdemo.entities.Comic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;

public class Parser {
    public static final String TAG = Parser.class.getSimpleName();
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
    public static Document parse(ResponseBody body) {
        try {
            return Jsoup.parse(body.byteStream(), "GBK", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Comic parseComicFromList(Element ele) {
        Comic comic = new Comic();
        String url = ele.select("a").first().attr("href");
        Matcher matcher = Pattern.compile("\\d+").matcher(url);
        if (matcher.find()) {
            comic.setId(Long.parseLong(matcher.group()));
        }
        String text = ele.select("a:eq(1)").text();
        if (text.contains("[")) {
            text = text.substring(0, text.lastIndexOf('['));
        }
        comic.setText(text);
        Element img = ele.select("a > img").first();
        comic.setImg(img.attr("src"));
        comic.setWidth(Integer.parseInt(img.attr("width")));
        comic.setHeight(Integer.parseInt(img.attr("height")));
        try {
            comic.setUpdateTime(SDF.parse(ele.ownText()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return comic;
    }
}
