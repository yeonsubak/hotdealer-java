package com.hotdealer.crawler.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class DataExtractor {

    public static List<Element> filterHotDealList(Document response) {
        return new ArrayList<>(response.select("li.li_best2_pop0"));
    }

    public static String extractCategory(Element hotDeal) {
        return hotDeal.select("span.category").select("a").text();
    }

    public static String extractTitle(Element hotDeal) {
        String orgTitle = hotDeal.select("h3.title").text();
        String regexTitle = orgTitle.replaceAll("([^]]*$)", "");
        regexTitle = regexTitle.replaceAll("\\[[^\\]]*\\]", "").trim(); // \[[^\]]*\]
        return regexTitle;
    }

    public static BigDecimal[] extractPrice(Element hotDeal) {
        String orgPrice = hotDeal.select("div.hotdeal_info > span").get(1).text().replaceAll("가격: ", "");
        return filterPrice(orgPrice);
    }

    public static CurrencyVO extractCurrency(Element hotDeal) {
        String orgPrice = hotDeal.select("div.hotdeal_info > span").get(1).text().replaceAll("가격: ", "");
        return new CurrencyVO(filterCurrency(orgPrice));
    }

    public static String extractShippingCost(Element hotDeal) {
        return hotDeal.select("div.hotdeal_info > span").get(2).text().replaceAll("배송: ", "");
    }

    public static String extractShop(Element hotDeal) {
        return hotDeal.select("div.hotdeal_info > span").get(0).text().replaceAll("쇼핑몰: ", "");
    }

    public static String extractPostUrl(Element hotDeal) {
        return hotDeal.select("a").get(0).attr("href");
    }

    public static Timestamp extractCreateAt(Element hotDeal) throws ParseException {
        String regDate = hotDeal.select("span.regdate").text().trim();
        String todayStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(todayStr + " " + regDate).getTime());
    }

    public static BigDecimal[] filterPrice(String price) {
        if (price.matches("(.*)[0-9](.*)") == false) {
            String trimPrice = price.trim();
            switch (price) {
                case "무료":
                    return new BigDecimal[]{new BigDecimal(0)};
                case "다양":
                    return new BigDecimal[]{new BigDecimal(-100)};
                default:
                    return new BigDecimal[]{new BigDecimal(-300)};
            }
        }

        String regexPrice = price.replaceAll("[a-zA-zㄱ-ㅣ가-힣|,|\\\\p{Sc}]", "");
        regexPrice = regexPrice.trim();

        if (regexPrice.contains("~")) {
            String[] splitPrice = regexPrice.split("~");
            return new BigDecimal[]{new BigDecimal(splitPrice[0]), new BigDecimal(splitPrice[1])};
        }

        Matcher m = Pattern.compile("([0-9.])+").matcher(regexPrice);
        if (m.find()) {
            regexPrice = m.group();
        }
        return new BigDecimal[]{new BigDecimal(regexPrice)};
    }

    public static String filterCurrency(String price) {
        String trimPrice = price.trim();
        String firstChar = trimPrice.length() != 0 ? trimPrice.substring(0, 1) : "";
        switch (firstChar) {
            case "\\":
            case "₩":
                return "KRW";
            case "$":
                return "USD";
            case "€":
                return "EUR";
            case "£":
                return "GBP";
            default:
                break;
        }

        String fromLast = trimPrice.replaceFirst("^[가-힣]+(?=\\d)", "");
        fromLast = fromLast.replaceAll("([0-9])+|,|\\.", "").trim().toUpperCase();
        {
            if (Arrays.asList("원", "KRW").contains(fromLast)) {
                return "KRW";
            }

            if (fromLast.equals("") && filterPrice(price)[0].compareTo(new BigDecimal(0)) > 0) {
                return "KRW";
            }
        }

        if (Arrays.asList("달러", "USD", "$", "DOLLAR", "DOLLARS").contains(fromLast)) {
            return "USD";
        }

        if (Arrays.asList("유로", "EUR", "€", "EURO", "EUROS").contains(fromLast)) {
            return "EUR";
        }

        if (Arrays.asList("파운드", "GBP", "£", "POUND", "POUNDS", "POUND STERLING").contains(fromLast)) {
            return "GBP";
        }

        return "NULL";
    }

}
