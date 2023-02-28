package com.hotdealer.crawler.utils;

import com.hotdealer.crawler.model.CurrencyVO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;

public interface IDataExtractor {
    List<Element> filterPosts(Document response);
    String extractCategory(Element post);
    String extractTitle(Element post);
    BigDecimal[] extractPrice(Element post);
    CurrencyVO extractCurrency(Element post);
    String extractShippingCost(Element post);
    String extractShop(Element post);
    String extractPostUrl(Element post);
    Timestamp extractCreateAt(Element post) throws ParseException;
}
