package com.jash.comicdemo.utils;

import android.text.TextUtils;

import com.jash.comicdemo.entities.Chapter;
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
    private static final Pattern PATTERN = Pattern.compile("漫画作者：(\\S+) \\| 漫画状态：(\\S+) \\| 漫画更新：(\\S+ \\S+)");
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
        comic.setTitle(text);
        Element img = ele.select("a > img").first();
        comic.setImg(img.attr("src"));
        comic.setWidth(Integer.parseInt(img.attr("width")));
        comic.setHeight(Integer.parseInt(img.attr("height")));
        try {
            String source = ele.ownText();
            if (!TextUtils.isEmpty(source)) {
                comic.setUpdateTime(SDF.parse(source));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return comic;
    }

    public static void updateComicFromInfo(Element ele, Comic comic) {
        comic.setInfo(ele.select("#ComicInfo").first().text());
        comic.setImg(ele.select("td[align] > img").first().attr("src"));
        comic.setWidth(120);
        comic.setHeight(160);
        String text = ele.select("td[colspan='2'][align]:not([bgcolor])").first().ownText();
        Matcher matcher = PATTERN.matcher(text);
        if (matcher.find()) {
            comic.setAuthor(matcher.group(1));
            comic.setStatus(matcher.group(2));
            try {
                comic.setUpdateTime(SDF.parse(matcher.group(3)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
    public static Chapter parseChapter(Element ele) {
        Chapter chapter = new Chapter();
        Pattern compile = Pattern.compile("\\d+");
        Matcher matcher = compile.matcher(ele.attr("href"));
        if (matcher.find()) {
            chapter.setComicId(Long.parseLong(matcher.group()));
        }
        if (matcher.find()) {
            chapter.setId(Long.parseLong(matcher.group()));
        }
        chapter.setName(ele.ownText());
        return chapter;
    }
}
