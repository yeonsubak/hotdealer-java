package com.hotdealer.crawler.utils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataFilter {

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

        String regexPrice = price.replaceAll("[a-zA-zㄱ-ㅣ가-힣,\\\\p{Sc}]", "");
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
