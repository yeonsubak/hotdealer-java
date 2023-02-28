package com.hotdealer.crawler;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FMKCrawler {

    public Document runReq() throws IOException {
        final String CRAWLER_SOURCE = "https://www.fmkorea.com/hotdeal";
        return Jsoup.connect(CRAWLER_SOURCE)
                .userAgent("Mozilla")
                .timeout(3000)
                .get();
    }

    

    public static void main(String[] args) throws IOException {

        FMKCrawler FMKCrawler = new FMKCrawler();
        // Document resp = fmKoreaCrawler.runReq();
        File input = new File("src/main/resources/test/com/hotdealer/crawler/fmkorea_hotdeal_webpage_sample.html");
        Document resp = Jsoup.parse(input, "UTF-8");
        Elements hotDealList = resp.select("li.li_best2_pop0");
        for (Element hotDeal : hotDealList) {
            // System.out.println(hotDeal);
            System.out.println(hotDeal.select("h3.title").text());
        }
        // System.out.println(hotdealList);

    }

}
