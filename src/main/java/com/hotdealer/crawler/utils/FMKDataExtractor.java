package com.hotdealer.crawler.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hotdealer.crawler.model.CurrencyVO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class FMKDataExtractor implements IDataExtractor {

    public List<Element> filterPosts(Document response) {
        return new ArrayList<>(response.select("li.li_best2_pop0"));
    }

    public String extractCategory(Element post) {
        return post.select("span.category").select("a").text();
    }

    public String extractTitle(Element post) {
        String orgTitle = post.select("h3.title").text();
        String regexTitle = orgTitle.replaceAll("([^]]*$)", "");
        regexTitle = regexTitle.replaceAll("\\[[^\\]]*\\]", "").trim(); // \[[^\]]*\]
        return regexTitle;
    }

    public BigDecimal[] extractPrice(Element post) {
        String orgPrice = post.select("div.hotdeal_info > span").get(1).text().replaceAll("가격: ", "");
        return DataFilter.filterPrice(orgPrice);
    }

    public CurrencyVO extractCurrency(Element post) {
        String orgPrice = post.select("div.hotdeal_info > span").get(1).text().replaceAll("가격: ", "");
        return new CurrencyVO(DataFilter.filterCurrency(orgPrice));
    }

    public String extractShippingCost(Element post) {
        return post.select("div.hotdeal_info > span").get(2).text().replaceAll("배송: ", "");
    }

    public String extractShop(Element post) {
        return post.select("div.hotdeal_info > span").get(0).text().replaceAll("쇼핑몰: ", "");
    }

    public String extractPostUrl(Element post) {
        return post.select("a").get(0).attr("href");
    }

    public Timestamp extractCreateAt(Element post) throws ParseException {
        String regDate = post.select("span.regdate").text().trim();
        String todayStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(todayStr + " " + regDate).getTime());
    }

}
