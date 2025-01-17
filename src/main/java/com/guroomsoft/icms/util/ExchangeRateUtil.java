package com.guroomsoft.icms.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 환율 조회 서비스
 * Sample
 * (요청) https://www.koreaexim.go.kr/site/program/financial/exchangeJSON?authkey=AUTHKEY1234567890&searchdate=20180102&data=AP01
 * (응답) [{"result":1,"cur_unit":"AED","ttb":"288.78","tts":"294.61","deal_bas_r":"291.7","bkpr":"291","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"291","kftc_deal_bas_r":"291.7","cur_nm":"아랍에미리트 디르함"},"result":1,"cur_unit":"ATS","ttb":"0","tts":"0","deal_bas_r":"93.52","bkpr":"0","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"0","kftc_deal_bas_r":"93.52","cur_nm":"오스트리아 실링"},{"result":1,"cur_unit":"AUD","ttb":"827.91","tts":"844.64","deal_bas_r":"836.28","bkpr":"836","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"836","kftc_deal_bas_r":"836.28","cur_nm":"호주 달러"},{"result":1,"cur_unit":"BEF","ttb":"0","tts":"0","deal_bas_r":"31.9","bkpr":"0","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"0","kftc_deal_bas_r":"31.9","cur_nm":"벨기에 프랑"},{"result":1,"cur_unit":"BHD","ttb":"2,811.48","tts":"2,868.27","deal_bas_r":"2,839.88","bkpr":"2,839","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"2,839","kftc_deal_bas_r":"2,839.88","cur_nm":"바레인 디나르"},{"result":1,"cur_unit":"CAD","ttb":"844.72","tts":"861.79","deal_bas_r":"853.26","bkpr":"853","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"853","kftc_deal_bas_r":"853.26","cur_nm":"캐나다 달러"},{"result":1,"cur_unit":"CHF","ttb":"1,088.44","tts":"1,110.43","deal_bas_r":"1,099.44","bkpr":"1,099","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"1,099","kftc_deal_bas_r":"1,099.44","cur_nm":"스위스 프랑"},{"result":1,"cur_unit":"CNH","ttb":"162.01","tts":"165.28","deal_bas_r":"163.65","bkpr":"163","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"163","kftc_deal_bas_r":"163.65","cur_nm":"위안화"},{"result":1,"cur_unit":"DEM","ttb":"0","tts":"0","deal_bas_r":"657.98","bkpr":"0","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"0","kftc_deal_bas_r":"657.98","cur_nm":"독일 마르크"},{"result":1,"cur_unit":"DKK","ttb":"171.14","tts":"174.59","deal_bas_r":"172.87","bkpr":"172","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"172","kftc_deal_bas_r":"172.87","cur_nm":"덴마아크 크로네"},{"result":1,"cur_unit":"ESP(100)","ttb":"0","tts":"0","deal_bas_r":"773","bkpr":"0","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"0","kftc_deal_bas_r":"773","cur_nm":"스페인 페세타"},{"result":1,"cur_unit":"EUR","ttb":"1,274.04","tts":"1,299.77","deal_bas_r":"1,286.91","bkpr":"1,286","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"1,286","kftc_deal_bas_r":"1,286.91","cur_nm":"유로"},{"result":1,"cur_unit":"FIM","ttb":"0","tts":"0","deal_bas_r":"216.44","bkpr":"0","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"0","kftc_deal_bas_r":"216.44","cur_nm":"핀란드 마르카"},{"result":1,"cur_unit":"FRF","ttb":"0","tts":"0","deal_bas_r":"196.18","bkpr":"0","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"0","kftc_deal_bas_r":"196.18","cur_nm":"프랑스 프랑"},{"result":1,"cur_unit":"GBP","ttb":"1,432.82","tts":"1,461.77","deal_bas_r":"1,447.3","bkpr":"1,447","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"1,447","kftc_deal_bas_r":"1,447.3","cur_nm":"영국 파운드"},{"result":1,"cur_unit":"HKD","ttb":"135.75","tts":"138.5","deal_bas_r":"137.13","bkpr":"137","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"137","kftc_deal_bas_r":"137.13","cur_nm":"홍콩 달러"},{"result":1,"cur_unit":"IDR(100)","ttb":"7.82","tts":"7.97","deal_bas_r":"7.9","bkpr":"7","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"7","kftc_deal_bas_r":"7.9","cur_nm":"인도네시아 루피아"},{"result":1,"cur_unit":"ITL(100)","ttb":"0","tts":"0","deal_bas_r":"66.46","bkpr":"0","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"0","kftc_deal_bas_r":"66.46","cur_nm":"이태리 리라"},{"result":1,"cur_unit":"JPY(100)","ttb":"941.53","tts":"960.56","deal_bas_r":"951.05","bkpr":"951","yy_efee_r":"0.96833","ten_dd_efee_r":"0.0242","kftc_bkpr":"951","kftc_deal_bas_r":"951.05","cur_nm":"일본 옌"},{"result":1,"cur_unit":"KRW","ttb":"0","tts":"0","deal_bas_r":"1","bkpr":"1","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"1","kftc_deal_bas_r":"1","cur_nm":"한국 원"},{"result":1,"cur_unit":"KWD","ttb":"3,509.87","tts":"3,580.78","deal_bas_r":"3,545.33","bkpr":"3,545","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"3,545","kftc_deal_bas_r":"3,545.33","cur_nm":"쿠웨이트 디나르"},{"result":1,"cur_unit":"MYR","ttb":"262.09","tts":"267.38","deal_bas_r":"264.74","bkpr":"264","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"264","kftc_deal_bas_r":"264.74","cur_nm":"말레이지아 링기트"},{"result":1,"cur_unit":"NLG","ttb":"0","tts":"0","deal_bas_r":"583.97","bkpr":"0","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"0","kftc_deal_bas_r":"583.97","cur_nm":"네델란드 길더"},{"result":1,"cur_unit":"NOK","ttb":"129.44","tts":"132.05","deal_bas_r":"130.75","bkpr":"130","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"130","kftc_deal_bas_r":"130.75","cur_nm":"노르웨이 크로네"},{"result":1,"cur_unit":"NZD","ttb":"752.76","tts":"767.97","deal_bas_r":"760.37","bkpr":"760","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"760","kftc_deal_bas_r":"760.37","cur_nm":"뉴질랜드 달러"},{"result":1,"cur_unit":"SAR","ttb":"282.84","tts":"288.55","deal_bas_r":"285.7","bkpr":"285","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"285","kftc_deal_bas_r":"285.7","cur_nm":"사우디 리얄"},{"result":1,"cur_unit":"SEK","ttb":"129.3","tts":"131.91","deal_bas_r":"130.61","bkpr":"130","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"130","kftc_deal_bas_r":"130.61","cur_nm":"스웨덴 크로나"},{"result":1,"cur_unit":"SGD","ttb":"793.06","tts":"809.09","deal_bas_r":"801.08","bkpr":"801","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"801","kftc_deal_bas_r":"801.08","cur_nm":"싱가포르 달러"},{"result":1,"cur_unit":"THB","ttb":"32.57","tts":"33.22","deal_bas_r":"32.9","bkpr":"32","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"32","kftc_deal_bas_r":"32.9","cur_nm":"태국 바트"},{"result":1,"cur_unit":"USD","ttb":"1,056.23","tts":"1,077.56","deal_bas_r":"1,066.9","bkpr":"1,066","yy_efee_r":"2.69465","ten_dd_efee_r":"0.07485","kftc_bkpr":"1,071","kftc_deal_bas_r":"1,071.4","cur_nm":"미국 달러"},{"result":1,"cur_unit":"XOF","ttb":"0","tts":"0","deal_bas_r":"1.9618","bkpr":"0","yy_efee_r":"0","ten_dd_efee_r":"0","kftc_bkpr":"0","kftc_deal_bas_r":"1.9618","cur_nm":"씨에프에이 프랑(비씨에이오)"}]
 *
 * Requester Parameter
 *  authkey     String(필수)      인증키              OpenAPI 신청시 발급된 인증키
 *  searchdate  String           검색요청날짜          ex) 2015-01-01, 20150101, (DEFAULT)현재일
 *  data        String(필수)      검색요청API타입       AP01 : 환율, AP02 : 대출금리, AP03 : 국제금리
 *
 *  Response Element
 *  RESULT      Integer     조회 결과       1 : 성공, 2 : DATA코드 오류, 3 : 인증코드 오류, 4 : 일일제한횟수 마감
 *  CUR_UNIT    String      통화코드
 *  CUR_NM      String      국가/통화명
 *  TTB         String      전신환(송금) 받으실때
 *  TTS         String      전신환(송금) 보내실때
 *  DEAL_BAS_R  String      매매 기준율
 *  BKPR        String      장부가격
 *  YY_EFEE_R   String      년환가료율
 *  TEN_DD_EFEE_R   String  10일환가료율
 *  KFTC_DEAL_BAS_R String  서울외국환중개 매매기준율
 *  KFTC_BKPR   String      서울외국환중개 장부가격
 *
 *  고려사항
 *  비영업일의 데이터, 혹은 영업당일 11시 이전에 해당일의 데이터를 요청할 경우 null 값이 반환
 *
 *  북경 염성 창주 만누르 오르가담 푸네 뱅갈로 트지네츠 질니나 비스코비체 미코우프 이즈밋 인디애나 알라바마 브카시
 *  중국(CNY) 인도(INR - 루피)  터키 ( TRY - 리라) 체코 (CZK - 코루나) 슬로바키아(EUR - 유로), 미국(USD-달러) 인도네시아 ( IDR 루피아 )
 *
 */
public class ExchangeRateUtil {
    private static HttpURLConnection connection;
    private static BigDecimal defaultExchangeRate = BigDecimal.valueOf(1300);

    public static BigDecimal getExchangeRateUtil() {
        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
        JSONParser parser = new JSONParser();

        String authKey = "4ncW2r0lYUnf9wbFVVMaheRoGrwTT9Jn";
        String searchDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String dataType = "AP01";
        BigDecimal exchangeRate = null;
        try {
            // Request URL
            URL url = new URL("https://www.koreaexim.go.kr/site/program/financial/exchangeJSON?authkey=" + authKey
                    + "&searchdate=" + searchDate
                    + "&data=" + dataType);
            connection = (HttpURLConnection) url.openConnection();

            // Request 초기 세팅
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();

            // API 호출
            // 실패했을 경우 Connection Close
            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            } else { // 성공했을 경우 환율 정보 추출
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    JSONArray exchangeRateInfoList = (JSONArray) parser.parse(line);

                    // KRW -> USD에 대한 환율 정보 조회
                    for (Object o : exchangeRateInfoList) {
                        JSONObject exchangeRateInfo = (JSONObject) o;
                        if (exchangeRateInfo.get("cur_unit").equals("USD")) {

                            // 쉼표가 포함되어 String 형태로 들어오는 데이터를 Double로 파싱하기 위한 부분
                            NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
                            exchangeRate = new BigDecimal(format.parse(exchangeRateInfo.get("deal_bas_r").toString()).doubleValue());
                        }
                    }
                }
                reader.close();
            }
            System.out.println(responseContent.toString());

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect();
        }

        if (exchangeRate == null) {
            exchangeRate = defaultExchangeRate;
        }

        return exchangeRate;
    }

    public static JSONArray getExchangeRateUtil(String searchDate) {
        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
        JSONParser parser = new JSONParser();
        JSONArray totalArray = new JSONArray();

        String authKey = "4ncW2r0lYUnf9wbFVVMaheRoGrwTT9Jn";
        String dataType = "AP01";
        try {
            // Request URL
            URL url = new URL("https://www.koreaexim.go.kr/site/program/financial/exchangeJSON?authkey=" + authKey
                    + "&searchdate=" + searchDate
                    + "&data=" + dataType);
            connection = (HttpURLConnection) url.openConnection();

            // Request 초기 세팅
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();

            // API 호출
            // 실패했을 경우 Connection Close
            if (status > 299) {
                return null;
            } else { // 성공했을 경우 환율 정보 추출
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    JSONArray exchangeRateInfoList = (JSONArray) parser.parse(line);

                    // KRW -> USD에 대한 환율 정보 조회
                    for (Object o : exchangeRateInfoList)
                    {
                        totalArray.add(o);
                    }

                }
                reader.close();

                return totalArray;
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect();
        }
    }
}
