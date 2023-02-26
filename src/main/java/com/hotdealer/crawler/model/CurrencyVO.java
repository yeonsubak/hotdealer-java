package com.hotdealer.crawler.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lombok.Getter;

@Getter
public class CurrencyVO {

    private String alphabeticCode;
    private String numericCode;
    private String country;
    private String englishName;
    private String koreanName;

    public CurrencyVO(Object alphabeticCode) {
        Map<String, String[]> currencyMap = fetchCurrencyMap();

        if (currencyMap.containsKey(alphabeticCode) == false) {
            this.alphabeticCode = "NULL";
            this.numericCode = "000";
            this.country = "NULL";
            this.englishName = "NULL";
            this.koreanName = "NULL";
            return;
        }

        String[] currencyInfo = currencyMap.get(alphabeticCode);

        this.alphabeticCode = (String) alphabeticCode;
        this.numericCode = currencyInfo[0];
        this.country = currencyInfo[1];
        this.englishName = currencyInfo[2];
        this.koreanName = currencyInfo[3];

    }

    private static Map<String, String[]> fetchCurrencyMap() {
        return new HashMap<>() {{
            put("KRW", new String[]{"410", "KOREA (THE REPUBLIC OF)", "Won", "원"});
            put("USD", new String[]{"840", "UNITED STATES OF AMERICA (THE)", "US Dollar", "달러"});
            put("EUR", new String[]{"978", "EUROPEAN UNION", "Euro", "유로"});
            put("GBP", new String[]{"826", "UNITED KINGDOM OF GREAT BRITAIN AND NORTHERN IRELAND (THE)", "Pound Sterling", "파운드"});
            put("CNY", new String[]{"156", "CHINA", "Yuan Renminbi", "위안"});
            put("JPY", new String[]{"392", "JAPAN", "Yen", "엔"});
        }};
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.alphabeticCode.hashCode();
        result = 31 * result + this.numericCode.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CurrencyVO)) {
            return false;
        }
        CurrencyVO other = (CurrencyVO) obj;
        return Objects.equals(this.alphabeticCode, other.alphabeticCode) &&
                Objects.equals(this.numericCode, other.numericCode);
    }

    @Override
    public String toString() {
        return this.alphabeticCode;
    }
}
