package com.hotdealer.crawler.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;

import lombok.Getter;

@Getter
public class HotDealDTO {

    private String dataSource;
    private String category;
    private String title;
    private BigDecimal[] price;
    private CurrencyVO currency;
    private String shippingCost;
    private String shop;
    private String postURL;
    private Timestamp createAt;

    public HotDealDTO(HashMap<String, Object> hotDealExtract) {

    }

}
