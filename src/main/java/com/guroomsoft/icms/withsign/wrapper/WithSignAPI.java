package com.guroomsoft.icms.withsign.wrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;



/**
 * WithSign api wrapper
 *
 */
@Slf4j
@Component
public class WithSignAPI {

    /**
     * 인증 토큰정보 조회
     * @return
     */
    public static JSONObject getAccessToken()
    {
        JSONObject jsonObject = null;
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://api.widsign.com/v2/token")
                .method("GET", null)
                .addHeader("Accept", "*/*")
                .addHeader("x-api-id", "Gb8ka3Px7G")
                .addHeader("x-api-key", "RUUQfoOq1YkNx9R5jLATK1muxJbKVlxmKXt7ty0P")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                ResponseBody responseBody = response.body();
                JSONParser jsonParser = new JSONParser();
                jsonObject = (JSONObject) jsonParser.parse(responseBody.string());

                log.debug(">>>>> {}", jsonObject.toJSONString());
                return jsonObject;
            }
        } catch (IOException e) {
            log.error(" >>>>>> IOException :::: " + e.getMessage());
        } catch (ParseException e) {
            log.error(" >>>>>> ParseException :::: " + e.getMessage());
        }

        return null;
    }

    public static String getAccessTokenStr()
    {
        JSONObject jsonObject = null;
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://api.widsign.com/v2/token")
                .method("GET", null)
                .addHeader("Accept", "*/*")
                .addHeader("x-api-id", "Gb8ka3Px7G")
                .addHeader("x-api-key", "RUUQfoOq1YkNx9R5jLATK1muxJbKVlxmKXt7ty0P")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                ResponseBody responseBody = response.body();
                JSONParser jsonParser = new JSONParser();
                jsonObject = (JSONObject) jsonParser.parse(responseBody.string());

                log.debug(">>>>> {}", jsonObject.toJSONString());
                return (String) jsonObject.get("access_token");
            }
        } catch (IOException e) {
            log.error(" >>>>>> IOException :::: " + e.getMessage());
        } catch (ParseException e) {
            log.error(" >>>>>> ParseException :::: " + e.getMessage());
        }

        return "";
    }

    public static JSONObject getGroup()
    {
        String authTokenStr = getAccessTokenStr();
        if (StringUtils.isBlank(authTokenStr)) {
            return null;
        }

        JSONObject jsonObject = null;
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://api.widsign.com/v2/service/group?sort=name%7Casc")
                .method("GET", null)
                .addHeader("Accept", "*/*")
                .addHeader("x-api-key", "RUUQfoOq1YkNx9R5jLATK1muxJbKVlxmKXt7ty0P")
                .addHeader("x-access-token", authTokenStr)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                ResponseBody responseBody = response.body();
                JSONParser jsonParser = new JSONParser();
                jsonObject = (JSONObject) jsonParser.parse(responseBody.string());

                log.debug(">>>>> {}", jsonObject.toJSONString());
                return jsonObject;
            }
        } catch (IOException e) {
            log.error(" >>>>>> IOException :::: " + e.getMessage());
        } catch (ParseException e) {
            log.error(" >>>>>> ParseException :::: " + e.getMessage());
        }

        return null;
    }

    public static JSONObject getServiceUser(Map<String, Object> queryParams)
    {
        String authTokenStr = getAccessTokenStr();
        if (StringUtils.isBlank(authTokenStr)) {
            log.debug(">>>>> Failed to get auth token");
            return null;
        }
        String queryStr = paramToQueryString(queryParams);
        String fullUrl = "https://api.widsign.com/v2/service/member?" + queryStr;
        log.debug(">>>>> {}", fullUrl);
        JSONObject jsonObject = null;
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(fullUrl)
                .method("GET", null)
                .addHeader("Accept", "*/*")
                .addHeader("x-api-key", "RUUQfoOq1YkNx9R5jLATK1muxJbKVlxmKXt7ty0P")
                .addHeader("x-access-token", authTokenStr)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                ResponseBody responseBody = response.body();
                JSONParser jsonParser = new JSONParser();
                jsonObject = (JSONObject) jsonParser.parse(responseBody.string());

                log.debug(">>>>> {}", jsonObject.toJSONString());
                return jsonObject;
            } else {
                log.debug(">>>>> response code ::: {}", response.code());
            }
        } catch (IOException e) {
            log.error(" >>>>>> IOException :::: " + e.getMessage());
        } catch (ParseException e) {
            log.error(" >>>>>> ParseException :::: " + e.getMessage());
        }

        return null;
    }


    public static JSONObject getFormList(Map<String, Object> queryParams)
    {
        String authTokenStr = getAccessTokenStr();
        if (StringUtils.isBlank(authTokenStr)) {
            log.debug(">>>>> Failed to get auth token");
            return null;
        }
        String queryStr = paramToQueryString(queryParams);
        String fullUrl = "https://api.widsign.com/v2/form?" + queryStr;
        log.debug(">>>>> {}", fullUrl);
        JSONObject jsonObject = null;
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(fullUrl)
                .method("GET", null)
                .addHeader("Accept", "*/*")
                .addHeader("x-api-key", "RUUQfoOq1YkNx9R5jLATK1muxJbKVlxmKXt7ty0P")
                .addHeader("x-access-token", authTokenStr)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                ResponseBody responseBody = response.body();
                JSONParser jsonParser = new JSONParser();
                jsonObject = (JSONObject) jsonParser.parse(responseBody.string());

                log.debug(">>>>> {}", jsonObject.toJSONString());
                return jsonObject;
            } else {
                log.debug(">>>>> response code ::: {}", response.code());
            }
        } catch (IOException e) {
            log.error(" >>>>>> IOException :::: " + e.getMessage());
        } catch (ParseException e) {
            log.error(" >>>>>> ParseException :::: " + e.getMessage());
        }

        return null;
    }

    public static JSONObject getFormDetail(Map<String, Object> queryParams)
    {
        String authTokenStr = getAccessTokenStr();
        if (StringUtils.isBlank(authTokenStr)) {
            log.debug(">>>>> Failed to get auth token");
            return null;
        }
        String queryStr = paramToQueryString(queryParams);
        String fullUrl = "https://api.widsign.com/v2/form/detail?" + queryStr;
        log.debug(">>>>> {}", fullUrl);
        JSONObject jsonObject = null;
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(fullUrl)
                .method("GET", null)
                .addHeader("Accept", "*/*")
                .addHeader("x-api-key", "RUUQfoOq1YkNx9R5jLATK1muxJbKVlxmKXt7ty0P")
                .addHeader("x-access-token", authTokenStr)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                ResponseBody responseBody = response.body();
                JSONParser jsonParser = new JSONParser();
                jsonObject = (JSONObject) jsonParser.parse(responseBody.string());

                log.debug(">>>>> {}", jsonObject.toJSONString());
                return jsonObject;
            } else {
                log.debug(">>>>> response code ::: {}", response.code());
            }
        } catch (IOException e) {
            log.error(" >>>>>> IOException :::: " + e.getMessage());
        } catch (ParseException e) {
            log.error(" >>>>>> ParseException :::: " + e.getMessage());
        }

        return null;
    }

    public static JSONObject getFormPreview(Map<String, Object> params)
    {
        String authTokenStr = getAccessTokenStr();
        if (StringUtils.isBlank(authTokenStr)) {
            log.debug(">>>>> Failed to get auth token");
            return null;
        }
        String fullUrl = "https://api.widsign.com/v2/form/preview";
        JSONObject jsonObject = null;
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        String jsonBody = convertMapToJson(params).toJSONString();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        // RequestBody 생성
        RequestBody requestBody = RequestBody.create(jsonBody, mediaType);

        Request request = new Request.Builder()
                .url(fullUrl)
                .addHeader("Accept", "*/*")
                .addHeader("x-api-key", "RUUQfoOq1YkNx9R5jLATK1muxJbKVlxmKXt7ty0P")
                .addHeader("x-access-token", authTokenStr)
                .post(requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                ResponseBody responseBody = response.body();
                JSONParser jsonParser = new JSONParser();
                jsonObject = (JSONObject) jsonParser.parse(responseBody.string());

                log.debug(">>>>> {}", jsonObject.toJSONString());
                return jsonObject;
            } else {
                log.debug(">>>>> response code ::: {}", response.code());
            }
        } catch (IOException e) {
            log.error(" >>>>>> IOException :::: " + e.getMessage());
        } catch (ParseException e) {
            log.error(" >>>>>> ParseException :::: " + e.getMessage());
        }

        return null;
    }



    /**
     * Utility
     * convert to Map from Json Object
     * @param jsonObj
     * @return
     */
    public static Map<String, Object> getMapFromJsonObject(JSONObject jsonObj){
        Map<String, Object> map = null;
        try {
            map = new ObjectMapper().readValue(jsonObj.toString(), Map.class);
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return map;
    }

    /**
     * Utility
     * convert to Json from Map
     * @param map
     * @return
     */
    public static JSONObject convertMapToJson(Map<String, Object> map) {
        JSONObject json = new JSONObject();
        String key = "";
        Object value = null;
        for(Map.Entry<String, Object> entry : map.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            json.put(key, value);
        }
        return json;
    }

    public static Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date asDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Long asDateValue(String dt) {
        Long returnValue = null;
        if (StringUtils.isBlank(dt)) {
            return returnValue;
        }

        try {
            LocalDate ld = LocalDate.parse(dt);
            returnValue = Long.valueOf(Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime());
        } catch (Exception e) {
            returnValue = null;
        }

        return returnValue;
    }

    public static String paramToQueryString(Map<String, Object> paramMap){
        List<NameValuePair> params = Lists.newArrayList();
        if(paramMap != null){
            for(Map.Entry<String, Object> paramEntry : paramMap.entrySet()){
                Object value = paramEntry.getValue();
                if(value != null){
                    params.add(new BasicNameValuePair(paramEntry.getKey(), value.toString()));
                }
            }
        }
        return URLEncodedUtils.format(params, StandardCharsets.UTF_8);
    }
}
