package com.jash.comicdemo.utils;

import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.jash.comicdemo.entities.Chapter;
import com.jash.comicdemo.entities.Comic;
import com.jash.comicdemo.entities.ComicDao;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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

    public static Comic parseComicFromList(Element ele, ComicDao dao) {
        String url = ele.select("a").first().attr("href");
        Matcher matcher = Pattern.compile("\\d+").matcher(url);
        long id = 0;
        if (matcher.find()) {
            id = Long.parseLong(matcher.group());
        }
        Comic comic = dao.load(id);
        if (comic == null) {
            comic = new Comic();
            comic.setId(id);
        }
        String text = ele.select("a:eq(1)").text();
        if (text.contains("[")) {
            text = text.substring(0, text.lastIndexOf('['));
        } else {
            text = text.substring(0, text.length() - 4);
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

    public static Pair<String, String> parsePicUrl(Element ele) {
        Matcher matcher = Pattern.compile("\"(.+)\"").matcher(ele.data());
        if (matcher.find()) {
            Document document = Jsoup.parse(matcher.group());
            String img1 = document.select("body > img").first().attr("src").replaceAll("\".+\"", "http://n.kukudm.com/");
            String img2 = document.select("span > img").first().attr("src").replaceAll("\".+\"", "http://n.kukudm.com/");
            return Pair.create(img1, img2);
        }
        return Pair.create("", "");
    }

    public static List<Pair<Integer, String>> parsePicture(Element element, Chapter chapter) {
        List<Pair<Integer, String>> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("共(\\d+)页");
        Matcher matcher = pattern.matcher(element.text());
        if (matcher.find()) {
            chapter.setPicCount(Integer.parseInt(matcher.group(1)));
            Element script = element.select("script:eq(3)").first();
            Pair<String, String> pair = parsePicUrl(script);
            String img1 = pair.first;
            String img2 = pair.second;
            Pattern fileP = Pattern.compile("(.+)([\\d]{3}).*?([\\da-zA-Z]{3})(\\.jpg)");
            Matcher m1 = fileP.matcher(img1);
            if (m1.find()) {
                String url = String.format(Locale.getDefault(), "%s%%s%s", m1.group(1), m1.group(4));
                String tag1 = m1.group(3);
                int start = Integer.parseInt(m1.group(2));
                if (m1.reset(img2).find()) {
                    String tag2 = m1.group(3);
                    String format = String.format(Locale.getDefault(), "%%0%dd%%3s", m1.group(2).length());
                    int i1 = Integer.parseInt(tag1, 35);
                    int i2 = Integer.parseInt(tag2, 35);
                    int step = i2 - i1;
                    for (int i = 0; i < chapter.getPicCount(); i++) {
                        String tag = String.format(Locale.getDefault(), format, i + start, Integer.toString(i1 + i * step, 35)).replaceAll(" ", "0");
                        list.add(Pair.create(i + 1, String.format(Locale.getDefault(), url, tag)));
                    }
                }
            } else {
                for (int i = 0; i < chapter.getPicCount(); i++) {
                    list.add(Pair.create(i + 1, null));
                }
            }
        }
        return list;
    }

}
