package com.hotdealer.crawler;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import com.hotdealer.crawler.model.CurrencyVO;
import com.hotdealer.crawler.utils.DataFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hotdealer.crawler.utils.FMKDataExtractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class FMKDataExtractorTest {

    private List<Element> hotDealItems;
    private FMKDataExtractor fmkDataExtractor;

    @BeforeEach
    void setUp() throws IOException {
        File input = new File("src/main/resources/test/com/hotdealer/crawler/fmkorea_hotdeal_webpage_sample.html");
        Document resp = Jsoup.parse(input, "UTF-8");
        fmkDataExtractor = new FMKDataExtractor();
        hotDealItems = fmkDataExtractor.filterPosts(resp);
    }

    @Test
    void extractTitleTest() {
        String[] titleExpArr = {
                "Crucial P5 Plus 2TB PCIe 4.0 3D NAND",
                "레오폴드 FC660MBT PD 저소음적축",
                "레오폴드 FC900RBT PD 저소음적축",
                "컬처랜드 문화상품권 5만원",
                "밀텍 개파카 역대가 또다시 경신",
                "마이크론 크루셜 P5 Plus NVMe M.2 SSD (1TB)",
                "고래사어묵 꼬치 어묵 926g 2봉",
                "솔내농원 해남 꿀고구마 특상3kg",
                "SK하이닉스 P41 2TB SSD",
                "로스팅 고추기름 짜장라면 16개 / 1봉 550원 / 카드 x",
                "로지텍코리아 LIFT for mac 컴팩트 인체공학 무선 블루투스 버티컬 마우스",
                "삼성 32인치 4K 퀀텀닷 IPS 모니터 S32B800",
                "PUMA 엑셀러레이터 더플백 블랙/골드",
                "LG 27인치 FHD 모니터 27GN650",
                "AMD 라이젠 R5 5600X (멀티팩) 버미어",
                "닭가슴살 소시지 스테이크 5+5",
                "크리스탈 라이트 슈가프리 버라이어티 언더고 파우더",
                "해남 세척 꿀고구마 특상 5kg",
                "아디다스 런팔콘 3.0 런닝화 공용",
                "주방용 고무장갑 5개"
        };

        for (int i = 0; i < hotDealItems.size(); i++) {
            String actual = fmkDataExtractor.extractTitle(hotDealItems.get(i));
            assertEquals(titleExpArr[i], actual);
        }
    }

    @Test
    void extractCategoryTest() {
        List<String> categoryExpArr = Arrays.asList(
                "먹거리", "SW/게임", "PC제품", "가전제품", "생활용품", "의류", "세일정보",
                "화장품", "모바일/상품권", "패키지/이용권", "기타", "해외핫딜", "공지"
        );

        for (Element hotDeal : hotDealItems) {
            String categoryExt = fmkDataExtractor.extractCategory(hotDeal);
            assertTrue(categoryExpArr.contains(categoryExt));
        }
    }

    @Test
    void extractPriceTest() {
        List<?> priceExpArr = Arrays.asList(
                new BigDecimal(170120), new BigDecimal(136940), new BigDecimal(154000),
                new BigDecimal(46050), new BigDecimal("70.71"), new BigDecimal(104720),
                new BigDecimal(23480), new BigDecimal(9600), new BigDecimal("169.99"),
                new BigDecimal(8800), new BigDecimal(58905), new BigDecimal(599000),
                new BigDecimal(26910), new BigDecimal(279110), new BigDecimal(169840),
                new BigDecimal[]{new BigDecimal(4900), new BigDecimal(5800)},
                new BigDecimal(14410), new BigDecimal(11820), new BigDecimal(36700),
                new BigDecimal(6900)
        );

        for (int i = 0; i < hotDealItems.size(); i++) {
            if (priceExpArr.get(i).getClass().isArray()) {
                assertArrayEquals((Object[]) priceExpArr.get(i), fmkDataExtractor.extractPrice(hotDealItems.get(i)));
                continue;
            }

            assertEquals(priceExpArr.get(i), fmkDataExtractor.extractPrice(hotDealItems.get(i))[0]);
        }

    }

    @Test
    void extractCurrencyTest() {
        List<CurrencyVO> currencyExpArr = Arrays.asList(
                new CurrencyVO("KRW"), new CurrencyVO("KRW"),
                new CurrencyVO("KRW"), new CurrencyVO("KRW"),
                new CurrencyVO("EUR"), new CurrencyVO("KRW"),
                new CurrencyVO("KRW"), new CurrencyVO("KRW"),
                new CurrencyVO("USD"), new CurrencyVO("KRW"),
                new CurrencyVO("KRW"), new CurrencyVO("KRW"),
                new CurrencyVO("KRW"), new CurrencyVO("KRW"),
                new CurrencyVO("KRW"), new CurrencyVO("NULL"),
                new CurrencyVO("KRW"), new CurrencyVO("KRW"),
                new CurrencyVO("KRW"), new CurrencyVO("KRW")
        );

        for (int i = 0; i < hotDealItems.size(); i++) {
            assertEquals(currencyExpArr.get(i), fmkDataExtractor.extractCurrency(hotDealItems.get(i)));
        }
    }

    @Test
    void extractShippingCostTest() {
        List<String> shippingCostExpArr = Arrays.asList(
                "무료", "2,500원", "2,500원", "무료", "20유로", "무배", "무료",
                "무료", "배대지필요", "무료", "0원", "무료", "우주패스무료",
                "무료", "3,000원", "3,000원", "우주패스무료", "무료", "무료", "무료"
        );

        for (int i = 0; i < hotDealItems.size(); i++) {
            assertEquals(shippingCostExpArr.get(i), fmkDataExtractor.extractShippingCost(hotDealItems.get(i)));
        }
    }

    @Test
    void extractShopTest() {
        List<String> shopExpArr = Arrays.asList(
                "11마존", "지마켓", "지마켓", "펫박스", "컬티즘", "지마켓", "지마켓",
                "티몬", "미마존", "지마켓", "ssg", "네이버", "11마존", "11번가", "지마켓",
                "랭킹닭컴", "11마존", "11번가", "cj온스타일", "카카오톡딜"
        );

        for (int i = 0; i < hotDealItems.size(); i++) {
            assertEquals(shopExpArr.get(i), fmkDataExtractor.extractShop(hotDealItems.get(i)));
        }
    }

    @Test
    void extractPostURLTest() {
        List<String> postUrlExpArr = Arrays.asList(
                "/5519990905", "/5519963650", "/5519953373", "/5519915442", "/5519868573", "/5519871788",
                "/5519845744", "/5519826160", "/5519761115", "/5519728846", "/5519703420", "/5519664841",
                "/5519667881", "/5519657353", "/5519621840", "/5519610331", "/5519562635", "/5519470259",
                "/5518995881", "/5519453919"
        );

        for (int i = 0; i < hotDealItems.size(); i++) {
            assertEquals(postUrlExpArr.get(i), fmkDataExtractor.extractPostUrl(hotDealItems.get(i)));
        }
    }

    @Test
    void extractCreateAt() throws ParseException {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String todayStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        List<String> createAtExpArr = Arrays.asList(
                "22:38", "22:29", "22:26", "22:12", "22:00", "21:59", "21:56", "21:47", "21:31", "21:23", "21:13",
                "21:11", "21:06", "21:02", "20:50", "20:47", "20:43", "20:16", "20:14", "19:57"
        );
        for (int i = 0; i < hotDealItems.size(); i++) {
            String createAtExp = String.format("%s %s", todayStr, createAtExpArr.get(i));
            Timestamp timestamp = new Timestamp(timeFormat.parse(createAtExp).getTime());
            assertEquals(timestamp, fmkDataExtractor.extractCreateAt(hotDealItems.get(i)));
        }
    }

    @Test
    void filterPriceTest() {
        LinkedHashMap<String, ?> krwTestSet = new LinkedHashMap<>() {{
            put("\\148,000", new BigDecimal(148000));
            put("\\ 148,000", new BigDecimal(148000));
            put("₩148,000", new BigDecimal(148000));
            put("₩ 148,000", new BigDecimal(148000));
            put("148,000원", new BigDecimal(148000));
            put("400원", new BigDecimal(400));
            put("3,900,000원", new BigDecimal(3900000));
            put("3,900,000 원", new BigDecimal(3900000));
            put("3900000", new BigDecimal(3900000));
            put("스클11,830 ", new BigDecimal(11830));
            put("0원", new BigDecimal(0));
            put("4,900~5,800", new BigDecimal[]{new BigDecimal(4900), new BigDecimal(5800)});
        }};

        for (String key : krwTestSet.keySet()) {
            if (krwTestSet.get(key).getClass().isArray()) {
                assertArrayEquals((Object[]) krwTestSet.get(key), DataFilter.filterPrice(key));
                continue;
            }

            assertEquals(krwTestSet.get(key), DataFilter.filterPrice(key)[0]);
        }

        LinkedHashMap<String, ?> usdTestSet = new LinkedHashMap<>() {{
            put("$15", new BigDecimal(15));
            put("$ 15", new BigDecimal(15));
            put("$15.95", new BigDecimal("15.95"));
            put("$ 15.95", new BigDecimal("15.95"));
            put("15달러", new BigDecimal(15));
            put("15.95달러", new BigDecimal("15.95"));
            put("15.95 달러", new BigDecimal("15.95"));
            put("USD 15.95", new BigDecimal("15.95"));
            put("USD15.95", new BigDecimal("15.95"));
            put("15.95 Dollar", new BigDecimal("15.95"));
            put("15.95Dollar", new BigDecimal("15.95"));
            put("15.95 Dollars", new BigDecimal("15.95"));
            put("15.95Dollars", new BigDecimal("15.95"));
        }};

        for (String key : usdTestSet.keySet()) {
            assertEquals(usdTestSet.get(key), DataFilter.filterPrice(key)[0]);
        }

        LinkedHashMap<String, ?> notPriceTestSet = new LinkedHashMap<>() {{
            put("무료", new BigDecimal(0));
            put("다양", new BigDecimal(-100));
            put("", new BigDecimal(-300));
            put("asdyqwery", new BigDecimal(-300));
        }};

        for (String key : notPriceTestSet.keySet()) {
            assertEquals(notPriceTestSet.get(key), DataFilter.filterPrice(key)[0]);
        }
    }

    @Test
    void filterCurrencyTest() {
        LinkedHashMap<String, String> filterCurrencyTestSet = new LinkedHashMap<>() {{
            put("\\148,000", "KRW");
            put("\\ 148,000", "KRW");
            put("₩148,000원", "KRW");
            put("₩ 148,000원", "KRW");
            put("400원", "KRW");
            put("3,900,000원", "KRW");
            put("3900000", "KRW");
            put("스클11,830 ", "KRW");
            put("0원", "KRW");
            put("4,900~5,800", "NULL");

            // USD Test
            put("$15", "USD");
            put("$ 15", "USD");
            put("$15.95", "USD");
            put("$ 15.95", "USD");
            put("15달러", "USD");
            put("15 달러", "USD");
            put("15.95달러", "USD");
            put("15.95 달러", "USD");
            put("USD 15.95", "USD");
            put("USD15.95", "USD");
            put("15.95Dollar", "USD");
            put("15.95 Dollar", "USD");
            put("15.95Dollars", "USD");
            put("15.95 Dollars", "USD");

            // EUR Test
            put("€15", "EUR");
            put("€ 15", "EUR");
            put("€15.95", "EUR");
            put("€ 15.95", "EUR");
            put("15유로", "EUR");
            put("15 유로", "EUR");
            put("15.95유로", "EUR");
            put("15.95 유로", "EUR");
            put("EUR 15.95", "EUR");
            put("EUR15.95", "EUR");
            put("15.95EUR", "EUR");
            put("15.95 EUR", "EUR");
            put("15.95Euro", "EUR");
            put("15.95 Euro", "EUR");
            put("15.95Euros", "EUR");
            put("15.95 Euros", "EUR");
            put("15.95 euro", "EUR");
            put("15.95euro", "EUR");

            // GBP Test
            put("£15", "GBP");
            put("£ 15", "GBP");
            put("£15.95", "GBP");
            put("£ 15.95", "GBP");
            put("15파운드", "GBP");
            put("15 파운드", "GBP");
            put("15.95파운드", "GBP");
            put("15.95 파운드", "GBP");
            put("GBP 15.95", "GBP");
            put("GBP15.95", "GBP");
            put("15.95GBP", "GBP");
            put("15.95 GBP", "GBP");
            put("15.95Pound", "GBP");
            put("15.95 Pound", "GBP");
            put("15.95Pounds", "GBP");
            put("15.95 Pounds", "GBP");
            put("15.95Pound sterling", "GBP");
            put("15.95 Pound sterling", "GBP");

            // Null Test
            put("무료", "NULL");
            put("다양", "NULL");
            put("13,000", "KRW");
            put("", "NULL");
            put("HELLOWORLD", "NULL");
        }};

        for (String key : filterCurrencyTestSet.keySet()) {
            assertEquals(filterCurrencyTestSet.get(key), DataFilter.filterCurrency(key), "Given Param: " + key);
        }
    }

    @Test
    void currencyVOTest() {
        List<String> testSet = Arrays.asList("KRW", "USD", "EUR", "GBP", "CNY", "JPY");
        List<String> numericCodeExpArr = Arrays.asList("410", "840", "978", "826", "156", "392");
        for (int i = 0; i < testSet.size(); i++) {
            assertEquals(numericCodeExpArr.get(i), new CurrencyVO(testSet.get(i)).getNumericCode());
        }

        // Null Test
        List<String> nullTestSet = Arrays.asList("NULL", "ASDF", null, "HELLOWORLD", "RWM");
        for (Object nullExp : nullTestSet) {
            assertEquals("000", new CurrencyVO(nullExp).getNumericCode());
        }

        for (Object nullExp : nullTestSet) {
            assertEquals("NULL", new CurrencyVO(nullExp).getAlphabeticCode());
        }
    }
}
