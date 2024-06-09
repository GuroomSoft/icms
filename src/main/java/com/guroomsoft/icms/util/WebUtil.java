package com.guroomsoft.icms.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

@Slf4j
public class WebUtil {
    public static String clearXSSMinimum(String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }

        String returnValue = value;

        returnValue = returnValue.replaceAll("&", "&amp;");
        returnValue = returnValue.replaceAll("<", "&lt;");
        returnValue = returnValue.replaceAll(">", "&gt;");
        returnValue = returnValue.replaceAll("\"", "&#34;");
        returnValue = returnValue.replaceAll("\'", "&#39;");
        returnValue = returnValue.replaceAll("\\.", "&#46;");
        returnValue = returnValue.replaceAll("%2E", "&#46;");
        returnValue = returnValue.replaceAll("%2F", "&#47;");
        return returnValue;
    }

    public static String clearXSSMaximum(String value) {
        String returnValue = value;
        returnValue = clearXSSMinimum(returnValue);

        returnValue = returnValue.replaceAll("%00", null);

        returnValue = returnValue.replaceAll("%", "&#37;");

        // \\. => .

        returnValue = returnValue.replaceAll("\\.\\./", ""); // ../
        returnValue = returnValue.replaceAll("\\.\\.\\\\", ""); // ..\
        returnValue = returnValue.replaceAll("\\./", ""); // ./
        returnValue = returnValue.replaceAll("%2F", "");

        return returnValue;
    }

    public static String filePathBlackList(String value) {
        String returnValue = value;
        if (returnValue == null || returnValue.trim().equals("")) {
            return "";
        }

        returnValue = returnValue.replaceAll("\\.\\.", "");

        return returnValue;
    }

    /**
     * 행안부 보안취약점 점검 조치 방안.
     *
     * @param value
     * @return
     */
    public static String filePathReplaceAll(String value) {
        String returnValue = value;
        if (returnValue == null || returnValue.trim().equals("")) {
            return "";
        }

        returnValue = returnValue.replaceAll("/", "");
        returnValue = returnValue.replaceAll("\\\\", "");
        returnValue = returnValue.replaceAll("\\.\\.", ""); // ..
        returnValue = returnValue.replaceAll("&", "");

        return returnValue;
    }

    public static String fileInjectPathReplaceAll(String value) {
        String returnValue = value;
        if (returnValue == null || returnValue.trim().equals("")) {
            return "";
        }

        returnValue = returnValue.replaceAll("/", "");
        returnValue = returnValue.replaceAll("\\..", ""); // ..
        returnValue = returnValue.replaceAll("\\\\", "");// \
        returnValue = returnValue.replaceAll("&", "");

        return returnValue;
    }

    public static String filePathWhiteList(String value) {
        return value;
    }

    public static boolean isIPAddress(String str) {
        Pattern ipPattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");

        return ipPattern.matcher(str).matches();
    }

    public static String removeCRLF(String parameter) {
        return parameter.replaceAll("\r", "").replaceAll("\n", "");
    }

    public static String removeSQLInjectionRisk(String parameter) {
        return parameter.replaceAll("\\p{Space}", "").replaceAll("\\*", "").replaceAll("%", "").replaceAll(";", "").replaceAll("-", "").replaceAll("\\+", "").replaceAll(",", "");
    }

    public static String removeOSCmdRisk(String parameter) {
        return parameter.replaceAll("\\p{Space}", "").replaceAll("\\*", "").replaceAll("|", "").replaceAll(";", "");
    }

    // 유니코드에서 String으로 변환
    public static String convertString(String val) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < val.length(); i++) {

            if ('\\' == val.charAt(i) && 'u' == val.charAt(i + 1)) {
                // 그 뒤 네글자는 유니코드의 16진수 코드이다. int형으로 바꾸어서 다시 char 타입으로 강제 변환한다.
                Character r = (char) Integer.parseInt(val.substring(i + 2, i + 6), 16);
                // 변환된 글자를 버퍼에 넣는다.
                sb.append(r);
                // for의 증가 값 1과 5를 합해 6글자를 점프
                i += 5;
            } else {
                // ascii코드면 그대로 버퍼에 넣는다.
                sb.append(val.charAt(i));
            }
        }
        // 결과 리턴
        return sb.toString();
    }


}
