package com.guroomsoft.icms.sap;

import com.sap.conn.jco.JCoField;

import java.text.SimpleDateFormat;

public class StringUtils {
    /**
     *
     * @param strPropertiesNM
     * RFC Date형 일 경우 '-'제거
     * @return
     */
    public static String getJCoFieldValue (JCoField field)
    {

        String s_return ="";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            if (field.getValue() != null){
                if( field.getTypeAsString().equals("DATE") ){
                    s_return = dateFormat.format( field.getDate()) ;
                }else if ( field.getTypeAsString().equals("CHAR") ){
                    s_return = field.getString();
                }else if ( field.getTypeAsString().equals("NUM") ){
                    s_return = field.getString();
                }else{
                    s_return = field.getString();
                }
            }else{
                s_return = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return s_return;
    }

}
