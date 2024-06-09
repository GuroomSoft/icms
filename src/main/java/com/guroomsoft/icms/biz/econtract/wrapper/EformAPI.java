package com.guroomsoft.icms.biz.econtract.wrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guroomsoft.icms.common.dao.AppConfigDAO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;


/**
 * Eformsign api wrapper
 *
 */
@Slf4j
@Component
public class EformAPI {
    @Autowired
    private AppConfigDAO appConfigDAO;
//    private static AppConfigDAO appConfigConnection;
    private static String memberId = null;
    private static String apiKey = null;
    private static String secretKey = null;

    public static final String KEY_EFORMSIGN_API = "eformsign-api";
    public static final String KEY_EFORMSIGN_MEMBER = "eformsign-member-id";
    public static final String KEY_EFORMSIGN_SECRET = "eformsign-secret";

    @PostConstruct
    private void initialize() {
        try {
//            this.appConfigConnection = appConfigDAO;
            this.apiKey = appConfigDAO.selectValueByKey(KEY_EFORMSIGN_API);
            this.secretKey = appConfigDAO.selectValueByKey(KEY_EFORMSIGN_SECRET);
            this.memberId = appConfigDAO.selectValueByKey(KEY_EFORMSIGN_MEMBER);

        } catch (Exception e) {
            log.error("👉 Failed to initialize EformAPI");
            throw new RuntimeException(e);
        }
    }

    /**
     * 인증 토큰정보 조회
     * @param pMemberId     미지정 시 환경설저에 등록된 멤버 사용자 계정을 사용한다.
     * @return
     */
    public static JSONObject getAccessToken(String  pMemberId)
    {

        log.debug("👉 인증을 요청합니다. apiKey ::: {}  secretKey ::: {}  memberId ::: {}", apiKey, secretKey, memberId);
        if (StringUtils.isBlank(apiKey) || StringUtils.isBlank(secretKey)) {
            return null;
        }

        JSONObject jsonObject = null;
        Map<String, Object> params = new LinkedHashMap<>();
        long currentTimeMillis = new Date().getTime();
        params.put("execution_time", Long.valueOf(currentTimeMillis));
        params.put("member_id", StringUtils.defaultString(pMemberId, memberId));

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String jsonBody = convertMapToJson(params).toJSONString();
        log.debug("👉 jsonBody ::: {}" + jsonBody);
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        // API KEY BASE64 ENCODING
        // 문자열을 byte 배열로 변환
        byte[] byteData = apiKey.getBytes();
        String encodedString = Base64.getEncoder().encodeToString(byteData);

        // RequestBody 생성
        RequestBody requestBody = RequestBody.create(jsonBody, mediaType);

        Request request = new Request.Builder()
                .url("https://api.eformsign.com/v2.0/api_auth/access_token")
                .addHeader("Accept", "*/*")
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("eformsign_signature", String.format("Bearer %s", secretKey))
                .addHeader("Authorization", "Bearer " + encodedString)
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(responseBody.string());

            if (response.code() == 200) {
                log.debug("👉 인증을 완료합니다.");
                log.debug(">>>>> {}", jsonObject.toJSONString());
                return jsonObject;
            } else {
                log.debug(">>>>> {}", jsonObject.toJSONString());
                log.debug(">>>> response ::: response code = " + response.code());
                return jsonObject;
            }
        } catch (IOException e) {
            log.error(" >>>>>> IOException :::: " + e.getMessage());
        } catch (ParseException e) {
            log.error(" >>>>>> ParseException :::: " + e.getMessage());
        }

        return null;
    }

    /**
     * 이폼 그룹 목록 조회
     * @param configMap
     * @param queryMap
     *          include_member      멤버 정보 포함여부 false ( default )
     *          include_field       그룹 커스텀 필트 포맷 정보 포함여부 false ( default )
     * @return
     */
    public JSONObject getGroups(Map<String, Object> configMap, Map<String, Object> queryMap)
    {
        log.debug("👉 그룹 목록을 요청합니다. ");
        JSONObject jsonObject = null;

        String fullUrl = (String)configMap.get("apiUrl") + "/v2.0/api/groups";
        OkHttpClient client = new OkHttpClient()
                .newBuilder()
                .build();
        String queryStr = paramToQueryString(queryMap);
        if (StringUtils.isNotBlank(queryStr)) {
            fullUrl = fullUrl + "?" + queryStr;
        }

        Request request = new Request.Builder()
                .url(fullUrl)
                .addHeader("Accept", "*/*")
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("eformsign_signature", String.format("Bearer %s", secretKey))
                .addHeader("Authorization", "Bearer " + (String)configMap.get("accessToken"))
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    String resStr = response.body().string();
                    log.debug("👉 Response body ::: {}", resStr);
                    JSONParser jsonParser = new JSONParser();
                    jsonObject = (JSONObject) jsonParser.parse(resStr);
                } else {
                    log.debug("👉 Response is null ");
                }
            }

            if (response.code() == 200) {
                log.debug("👉 HTTP Status code ::: {} ", String.valueOf(response.code()));
            } else {
                log.debug("👉 response ::: response code = " + response.code());
            }

            return jsonObject;
        } catch (IOException e) {
            log.error("👉 IOException :::: " + e.getMessage());
        } catch (ParseException e) {
            log.error("👉 ParseException :::: " + e.getMessage());
        } catch (Exception e) {
            log.error("👉 Exception :::: " + e.getMessage());
        }

        return null;
    }



    /**
     * 이폼사인 멤버목록 조회
     * @param configMap
     * @param queryMap
     *          member_all      전체 멤버 조회여부 false (default)
     *          include_field   구성원 커스텀 필드 포맷 정보 포함여부 false (default)
     *          include_delete  삭제 멤버 조회여부 false (default)
     *          eb_name_search  입력한 쿼리와 이름(members[].name) 또는 계정 ID (members[].account_id) 가 일치하는 구성원 검색
     * @return
     */
    public JSONObject getMembers(Map<String, Object> configMap, Map<String, Object> queryMap)
    {
        log.debug("👉 멤버 목록을 요청합니다. ");
        JSONObject jsonObject = null;

        String fullUrl = (String)configMap.get("apiUrl") + "/v2.0/api/members";
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String queryStr = paramToQueryString(queryMap);
        fullUrl = fullUrl + "?" + queryStr;

        log.debug("👉 URL : {} ", fullUrl);

        Request request = new Request.Builder()
                .url(fullUrl)
                .addHeader("Accept", "*/*")
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("eformsign_signature", String.format("Bearer %s", secretKey))
                .addHeader("Authorization", "Bearer " + (String)configMap.get("accessToken"))
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();

            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(responseBody.string());

            if (response.code() == 200) {
                log.debug("👉 HTTP Status code ::: {} ", String.valueOf(response.code()));
                log.debug("👉 {}", jsonObject.toJSONString());
                return jsonObject;
            } else {
                log.debug("👉 response ::: response code = " + response.code());
                log.debug("👉 {}", jsonObject.toJSONString());
            }
            return jsonObject;
        } catch (IOException e) {
            log.error(" >>>>>> IOException :::: " + e.getMessage());
        } catch (ParseException e) {
            log.error(" >>>>>> ParseException :::: " + e.getMessage());
        }

        return null;
    }

    /**
     * 템플릿 목록
     * @param configMap
     * @return
     */
    public JSONObject getForms(Map<String, Object> configMap)
    {
        log.debug("👉 템플릿 목록을 요청합니다. ");
        JSONObject jsonObject = null;

        String fullUrl = (String)configMap.get("apiUrl") + "/v2.0/api/forms";
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        log.debug("👉 URL : {} ", fullUrl);

        Request request = new Request.Builder()
                .url(fullUrl)
                .addHeader("Accept", "*/*")
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("eformsign_signature", String.format("Bearer %s", secretKey))
                .addHeader("Authorization", "Bearer " + (String)configMap.get("accessToken"))
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();

            log.debug("👉 response.toString() ::: {}", response.toString());
            log.debug("👉 responseBody.toString() ::: {}", responseBody.toString());
            log.debug("👉 responseBody.string() ::: {}", responseBody.string());

            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(responseBody.string());

            if (response.code() == 200) {
                log.debug("👉 {}", jsonObject.toJSONString());
            } else {
                log.debug("👉 response ::: response code = " + response.code());
                log.debug("👉 {}", jsonObject.toJSONString());
            }
            return jsonObject;
        } catch (IOException e) {
            log.error(" >>>>>> IOException :::: " + e.getMessage());
        } catch (ParseException e) {
            log.error(" >>>>>> ParseException :::: " + e.getMessage());
        }

        return null;
    }

    /**
     * 문서목록 (API 문제로 사용안함)
     * @param configMap
     * @param params
     * @return
     */
    public static JSONObject getDocumentList(Map<String, Object> configMap, Map<String, Object> params)
    {
        JSONObject jsonObject = null;
        String authToken = "Bearer " + (String)configMap.get("accessToken");
        String fullUrl = (String)configMap.get("apiUrl")
                + "/v2.0/api/documents?include_fields=true&include_histories=true&include_previous_status=true&include_next_status=true";
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String jsonBody = convertMapToJson(params).toJSONString();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        // RequestBody 생성
        RequestBody requestBody = RequestBody.create(jsonBody, mediaType);

        Request request = new Request.Builder()
                .url(fullUrl)
                .addHeader("Accept", "*/*")
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("eformsign_signature", String.format("Bearer %s", secretKey))
                .addHeader("Authorization", authToken)
                .method("get", requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();

            log.debug("👉 response.toString() ::: {}", response.toString());
            log.debug("👉 responseBody.toString() ::: {}", responseBody.toString());
            log.debug("👉 responseBody.string() ::: {}", responseBody.string());

            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(responseBody.string());

            if (response.code() == 200) {
                log.debug("👉 status code ::: {}", response.code());
                log.debug("👉 {}", jsonObject.toJSONString());
            } else {
                log.debug("👉 response ::: response code = " + response.code());
                log.debug("👉 {}", jsonObject.toJSONString());
            }
            return jsonObject;
        } catch (IOException e) {
            log.error(" >>>>>> IOException :::: " + e.getMessage());
        } catch (ParseException e) {
            log.error(" >>>>>> ParseException :::: " + e.getMessage());
        } catch (Exception e) {
            log.error(" >>>>>> ParseException :::: " + e.getMessage());
        }

        return null;
    }

    /**
     * 문서함의 문서목록
     * @param configMap
     * @param params
     * @return
     */
    public JSONObject getDocumentListFromBox(Map<String, Object> configMap, Map<String, Object> params)
    {
        JSONObject jsonObject = null;

        String fullUrl = (String)configMap.get("apiUrl") + "/v2.0/api/list_document";
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String jsonBody = convertMapToJson(params).toJSONString();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        // RequestBody 생성
        RequestBody requestBody = RequestBody.create(jsonBody, mediaType);

        Request request = new Request.Builder()
                .url(fullUrl)
                .addHeader("Accept", "*/*")
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("eformsign_signature", String.format("Bearer %s", secretKey))
                .addHeader("Authorization", "Bearer " + (String)configMap.get("accessToken"))
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(responseBody.string());

            if (response.code() == 200) {
                log.debug("👉 {}", jsonObject.toJSONString());
            } else {
                log.debug("👉 {}", jsonObject.toJSONString());
            }
            return jsonObject;
        } catch (IOException e) {
            log.error("👉 IOException :::: " + e.getMessage());
        } catch (ParseException e) {
            log.error("👉 ParseException :::: " + e.getMessage());
        } catch (Exception e) {
            log.error("👉 Exception :::: " + e.getMessage());
        }

        return null;
    }

    /**
     * 문서상세정보 조회
     * @param configMap
     * @param docId
     *          include_fields      필드 포함 여부를 설정    Default :: false
     *          include_histories   문서이력 포함여부       Default :: false    >>> 이력만 포함시킴
     *          include_previous_status 이전단계 포함여부   Default :: false
     *          include_next_status 다음단계 정보 포함여부   Default :: false
     *          include_external_token  참여자 및 참여자의 토큰 정보 포함여부 Default :: false
     * @return
     */
    public JSONObject getDocumentDetail(Map<String, Object> configMap, String docId)
    {
        if (StringUtils.isBlank(docId)) {
            return null;
        }

        JSONObject jsonObject = null;

        String baseUrl = (String)configMap.get("apiUrl") + "/v2.0/api/documents";
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String fullUrl = baseUrl + "/" + docId + "?include_histories=true";

        Request request = new Request.Builder()
                .url(fullUrl)
                .addHeader("Accept", "*/*")
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("eformsign_signature", String.format("Bearer %s", secretKey))
                .addHeader("Authorization", "Bearer " + (String)configMap.get("accessToken"))
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(responseBody.string());

            if (response.code() == 200) {
                log.debug("👉 {}", jsonObject.toJSONString());
            } else {
                log.debug("👉 {}", jsonObject.toJSONString());
            }
            return jsonObject;
        } catch (IOException e) {
            log.error("👉 IOException :::: " + e.getMessage());
        } catch (ParseException e) {
            log.error("👉 ParseException :::: " + e.getMessage());
        } catch (Exception e) {
            log.error("👉 Exception :::: " + e.getMessage());
        }

        return null;
    }

    /**
     * 문서 생성
     * @param configMap
     * @param queryMap
     * @param content
     * @return
     */
    public static JSONObject createDocument(Map<String, Object> configMap, Map<String, Object> queryMap, JSONObject content)
    {
        JSONObject jsonObject = null;
        String queryStr = paramToQueryString(queryMap);
        // https://kr-api.eformsign.com/v2.0/api/documents?template_id={{template ID}}
        String fullUrl = (String)configMap.get("apiUrl") + "/v2.0/api/documents?" + queryStr;
        log.debug("👉 fullUrl ::: {}", fullUrl);
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String jsonBody = content.toJSONString();
        log.debug("👉 jsonBody ::: {}", jsonBody);
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        // RequestBody 생성
        RequestBody requestBody = RequestBody.create(jsonBody, mediaType);

        Request request = new Request.Builder()
                .url(fullUrl)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Authorization", "Bearer " + (String)configMap.get("accessToken"))
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            JSONParser jsonParser = new JSONParser();

            log.debug("👉 response.toString() ::: {}", response.toString());
            log.debug("👉 responseBody.toString() ::: {}", responseBody.toString());
            log.debug("👉 responseBody.string() ::: {}", responseBody.string());

            jsonObject = (JSONObject) jsonParser.parse(responseBody.string());
            if (response.code() == 200) {
                log.debug("👉 {}", jsonObject.toJSONString());
            } else {
                log.debug("👉 response ::: response code = " + response.code());
                log.debug("👉 response ::: response code = " + response.body().string());
                log.debug("👉 {}", jsonObject.toJSONString());
            }
            return jsonObject;
        } catch (IOException e) {
            log.error(" >>>>>> IOException :::: " + e.getMessage());
        } catch (ParseException e) {
            log.error(" >>>>>> ParseException :::: " + e.getMessage());
        }

        return null;
    }

    public static JSONObject createDocument2(Map<String, Object> configMap, Map<String, Object> queryMap, JSONObject content) throws IOException {
        String authToken = "Bearer " + (String)configMap.get("accessToken");
        log.debug("👉 인증토큰 ::: {}", authToken);

        //http 통신 요청 후 응답 받은 데이터를 담기 위한 변수
        String returnData = "";

        // URL 설정
        String queryStr = paramToQueryString(queryMap);
        //String fullUrl = (String)configMap.get("apiUrl") + "/v2.0/api/documents?" + queryStr;
        String fullUrl = ((String)configMap.get("apiUrl")).trim() + "/v2.0/api/documents?template_id=e395be10a89b4d89b58b153a90a2d9ce";
        log.debug("👉 요청 URL ::: {}", fullUrl);

        // 전송할 요청 데이터
        String jsonBody = content.toJSONString();
        log.debug("👉 요청 본문 ::: {}", jsonBody);

        //http 요청 시 필요한 url 주소를 변수 선언
        URL url = new URL(fullUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            conn = (HttpURLConnection) url.openConnection();

            //http 요청에 필요한 타입 정의 실시
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(50000); //서버에 연결되는 Timeout 시간 설정

            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", authToken);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setDefaultUseCaches(false);

            try (OutputStream os = conn.getOutputStream()){
                byte request_data[] = jsonBody.getBytes("utf-8");
                log.debug("👉 연결을 요청합니다.");
                os.write(request_data);
                os.flush();
                os.close();
                log.debug("👉 요청 본문 작성 완료");
            }
            catch(Exception e) {
                log.debug("👉 " + e.getMessage());
            }


            //http 요청 후 응답 받은 데이터를 버퍼에 쌓는다
            InputStream is = conn.getInputStream();
            log.debug("👉 연결 후 InputStream을 오픈합니다.");

            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            log.debug("👉 연결 요청 후 데이터를 조회합니다.");

            String responseData = "";
            while ((responseData = br.readLine()) != null) {
                sb.append(responseData); //StringBuffer에 응답받은 데이터 순차적으로 저장 실시
            }

            //메소드 호출 완료 시 반환하는 변수에 버퍼 데이터 삽입 실시
            returnData = sb.toString();

            //http 요청 응답 코드 확인 실시
            String responseCode = String.valueOf(conn.getResponseCode());
            log.debug("👉 CreateDocument[responsecode] : " + responseCode);
            log.debug("👉 CreateDocument[result] : " + returnData);

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(returnData);
            log.debug("👉 조회결과 ::: {}", jsonObject.toJSONString());
            JSONObject document = (JSONObject) jsonObject.get("document");

            String docID = (String) document.get("id");
            log.debug("👉 response.toString() ::: {}", docID);


        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            //http 요청 및 응답 완료 후 BufferedReader를 닫아줍니다
            try {
                if (br != null) {
                    br.close();
                }
                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    /**
     * Utility
     * convert to Map from Json Object
     * @param jsonObj
     * @return
     */
    public static Map<String, Object> getMapFromJsonObject(JSONObject jsonObj)
    {
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
    public static JSONObject convertMapToJson(Map<String, Object> map)
    {
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

    /**
     * Http Log Interceptor
     */
    private class HttpLogInterceptor implements Interceptor
    {

        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            Request req = chain.request();
            Response res = chain.proceed(req);
            log.debug("👉 Interceptor :: Http Status Code ::: {} ", String.valueOf(res.code()));

            log.debug("👉 Interceptor :: Request method ::: {} ", req.method());
            log.debug("👉 Interceptor :: Request headers ::: {} ", req.headers());
            log.debug("👉 Interceptor :: Request url ::: {} ", req.url());

            log.debug("👉 Interceptor :: response requested url ::: {} ", res.request().url());
            log.debug("👉 Interceptor :: response header ::: {} ", res.headers());
            log.debug("👉 Interceptor :: response body ::: {} ", res.body().string());

            return res;
        }
    }
}
