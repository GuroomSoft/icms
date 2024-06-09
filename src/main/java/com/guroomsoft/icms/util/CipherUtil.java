package com.guroomsoft.icms.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class CipherUtil {
    private static final String	ENCRYPT_KEY	= "3ffb2c1382315feb7caf8219ee7add61";
    private static final String	IV			= "322a5c3b1ff591abfe3cdf99faeeb344";

    private static int fromDigit(char ch)
    {
        if ( ( ch >= '0' ) && ( ch <= '9' ) )
        { return ch - 48; }

        if ( ( ch >= 'A' ) && ( ch <= 'F' ) )
        { return ( ch - 65 ) + 10; }
        if ( ( ch >= 'a' ) && ( ch <= 'f' ) )
        {
            return ( ch - 97 ) + 10;
        }
        else
        {
            throw new IllegalArgumentException( ( new StringBuilder( "invalid hex digit '" ) ).append( ch ).append( "'" ).toString() );
        }
    }

    /**
     * <pre>
     * 이진 테이터를 hexa 문자열로 변환
     * </pre>
     * @param buf
     * @return
     */
    private static String byteToHex(byte buf[])
    {
        StringBuffer strbuf = new StringBuffer( buf.length * 2 );

        for ( byte element : buf )
        {
            if ( ( element & 0xff ) < 16 )
            { strbuf.append( "0" ); }

            strbuf.append( Long.toString( element & 0xff, 16 ) );
        }

        return strbuf.toString();
    }

    /**
     * <pre>
     * hexa 문자열을 이진 테이터로 변환
     * </pre>
     * @param hex
     * @return
     */
    private static byte[ ] hexToByte(String hex)
    {
        int	len	= hex.length();
        int	i	= 0;
        int	j	= 0;

        byte buf[] = new byte[ ( len + 1 ) / 2 ];

        if ( ( len % 2 ) == 1 )
        { buf[ j++ ] = ( byte ) fromDigit( hex.charAt( i++ ) ); }

        while ( i < len )
        { buf[ j++ ] = ( byte ) ( ( fromDigit( hex.charAt( i++ ) ) << 4 ) | fromDigit( hex.charAt( i++ ) ) ); }

        return buf;
    }

    private static String AES_Encode(String str, String encryptKey, String iv, String encodingType) throws Exception
    {
        IvParameterSpec ivspec	= new IvParameterSpec( hexToByte( iv ) );
        SecretKeySpec keyspec	= new SecretKeySpec( hexToByte( encryptKey ), "AES" );

        Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5Padding" );
        cipher.init( Cipher.ENCRYPT_MODE, keyspec, ivspec );

        byte[ ] encryptedData = cipher.doFinal( str.getBytes( encodingType ) );

        return byteToHex( encryptedData );

    }

    public static String AES_Decode(String str, String encryptKey, String iv, String encodingType) throws Exception {
        IvParameterSpec ivspec	= new IvParameterSpec( hexToByte( iv ) );
        SecretKeySpec keyspec	= new SecretKeySpec( hexToByte( encryptKey ), "AES" );

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

        byte[] decryptedData = cipher.doFinal(hexToByte( str ));

        return new String( decryptedData, encodingType );
    }

    /**
     * 문자열 대칭 암호화
     * @param str
     * @return
     */
    public static String encrypt(String str)
    {
        String result = "";

        try
        {
            if ( ( str == null ) || ( str.length() == 0 ) )
            { return result; }

            result = AES_Encode( str, ENCRYPT_KEY, IV, "UTF-8" );
        }
        catch ( Exception e )
        {
            log.error(e.getMessage());
        }
        return result;
    }

    public static String decrypt(String str) {
        String result = "";
        if (StringUtils.isBlank(str)) {
            return result;
        }

        try {
            result = AES_Decode(str, ENCRYPT_KEY, IV, "UTF-8");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return result;
    }

    public static String sha512encrypt(String pwd) {
        String encryptPwd = "";

        try {
            MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
            sha512.update(pwd.getBytes());
            encryptPwd = convertByteToHex(sha512.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return encryptPwd;
    }

    public static String convertByteToHex(byte[] data) {
        StringBuilder result = new StringBuilder();
        for(byte el: data) {
            result.append(Integer.toString((el & 0xff ) + 0x100, 16).substring(1));
        }

        return result.toString();
    }

}
