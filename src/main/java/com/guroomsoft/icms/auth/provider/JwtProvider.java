package com.guroomsoft.icms.auth.provider;

import com.guroomsoft.icms.auth.dto.JwtToken;
import com.guroomsoft.icms.auth.dto.PrincipalDetails;
import com.guroomsoft.icms.auth.dto.User;
import com.guroomsoft.icms.auth.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Jwt 토큰 관련 기능
 */
@Slf4j
@Component
public class JwtProvider {
    private static final String CLAIMS_KEY_USER_UID = "userUid";
    private static final String CLAIMS_KEY_ACCOUNT = "accountId";
    private static final String CLAIMS_KEY_ACCOUNT_NAME = "accountName";
    private static final String CLAIMS_KEY_PWD = "accountPwd";
    private static final String CLAIMS_KEY_ROLES = "roles";
    private static final String CLAIMS_KEY_ORG = "orgCd";
    private static final String CLAIMS_KEY_BP = "bpCd";

    public static final long ACCESSTOKEN_TIME = 1000 * 60 * 60 * 24;            // 30분
    public static final long REFRESHTOKEN_TIME = 1000 * 60 * 60 * 24 * 7;  //7일
    public static final String ACCESS_PREFIX_STRING = "Bearer ";
    public static final String ACCESS_HEADER_STRING = "Authorization";
    public static final String REFRESH_HEADER_STRING = "RefreshToken";
    private final Key key;

    private final RedisTemplate<String, String> redisTemplate;
    @Autowired
    private UserService userService;

    public JwtProvider(@Value("${jwt.secret}") String secret, RedisTemplate<String, String> redisTemplate)
    {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.redisTemplate = redisTemplate;
    }

    // Create access token and refresh token
    // 로그인 시
    public JwtToken createJwtToken(Long userUid, String accountId, String accountNm, String role)
    {
        log.info("👉 [JwtProvider] createJwtToken");
        //엑세스 토큰
        String accessToken = ACCESS_PREFIX_STRING + Jwts.builder()
                .setSubject(String.valueOf(userUid))
                .claim(CLAIMS_KEY_USER_UID, String.valueOf(userUid))
                .claim(CLAIMS_KEY_ACCOUNT,accountId)
                .claim(CLAIMS_KEY_ACCOUNT_NAME, accountNm)
                .claim(CLAIMS_KEY_ROLES, role)
                .setExpiration(new Date(System.currentTimeMillis() + ACCESSTOKEN_TIME))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        //리프레시 토큰
        String refreshToken = Jwts.builder()
                .setSubject(String.valueOf(userUid) + "_refresh")
                .claim("userUid", userUid)
                .setExpiration(new Date(System.currentTimeMillis() + REFRESHTOKEN_TIME))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return new JwtToken(accessToken, refreshToken);
    }

    // Create only access token -> It's used when request refresh token
    public String createAccessToken(Long userUid, String accountId, String accountNm, String role)
    {
        log.info("👉 [JwtProvider] createAccessToken");
        return  ACCESS_PREFIX_STRING + Jwts.builder()
                .setSubject(String.valueOf(userUid))
                .claim(CLAIMS_KEY_USER_UID, String.valueOf(userUid))
                .claim(CLAIMS_KEY_ACCOUNT,accountId)
                .claim(CLAIMS_KEY_ACCOUNT_NAME, accountNm)
                .claim(CLAIMS_KEY_ROLES, role)
                .setExpiration(new Date(System.currentTimeMillis() + ACCESSTOKEN_TIME))
                .signWith(this.key, SignatureAlgorithm.HS512)
                .compact();
    }

    // Create authentication object from access token
    // 스프링 시큐리티 인증 필터에서 JWT토큰 인증이 정상적으로 이루어졌을 때,
    // 시큐리티 인가 검증 필터를 위해서 인증객체(Authentication)을 생성하는 메서드
    public Authentication getAuthentication(String token)
    {
        log.info("👉 [JwtProvider] getAuthentication");
        //이전에 토큰 검증은 끝냈으니 Claims 를 받아와도 에러가 발생하지 않는다.
        Claims claims = parseClaims(token);

        if (claims == null) {
            log.info("👉 [JwtProvider] getAuthentication-claims is null");
            return null;
        }

        //1. 토큰안에 필요한 Claims 가 있는지 확인
        if(claims.get("userUid")==null && claims.get("accountId")==null && claims.get("accountNm")==null && claims.get("role")==null)
            return null;

        //2. DB 에 사용자가 있는지 확인 -> 탈퇴했을 경우를 위해서
        Long userUid = Long.valueOf(claims.get("userUid").toString());
        User user = null;
        try {
            user = userService.findUserByKey(userUid);
            if (user == null) {
                return null;
            } else {
                user.setAuthorities(userService.findUserAuthoritiesByKey(userUid));
                PrincipalDetails details = new PrincipalDetails(user);
                return UsernamePasswordAuthenticationToken.authenticated(details.getUsername(), "", details.getAuthorities());
            }
        } catch (Exception e) {
            return null;
        }
    }
    public User getUserInfo(String token)
    {
        Long userUid = getUserUid(token);
        try {
            return userService.findUserByKey(userUid);
        } catch (Exception e) {
            return null;
        }
    }
    // 토큰 유효성 검사
    public Boolean validationToken(String token)
    {
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            log.error("ExpiredJwtException", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.error("MalformedJwtException", e.getMessage());
            return false;
        }catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException", e.getMessage());
            return false;
        }catch (Exception e ){
            log.error("Exception", e.getMessage());
            return false;
        }

        return true;
    }

    // 엑세스 토큰의 만료시간
    // 로그아웃 요청시 Redis에 엑세스 토큰을 담기 위해 사용됨
    public Long getExpiration(String accessToken)
    {
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody().getExpiration();

        long now = new Date().getTime();
        return expiration.getTime() - now;
    }

    /**
     * 토큰 값에서 User UID 값 가져오기
     * @param token
     * @return
     */
    public Long getUserUid(String token)
    {
        String userUid = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
        if (StringUtils.isNumeric(userUid)) {
            if (StringUtils.isNotBlank(userUid)) {
                return Long.valueOf(userUid);
            } else {
                return Long.valueOf(-1);
            }
        } else {
            log.error("👉 {} is not number", userUid);
            return Long.valueOf(-1);
        }
    }

    // 토큰 Claims 가져오기
    public Claims parseClaims(String accessToken)
    {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
    }

    // 로그아웃된 엑세스 토큰인지 검증
    public Boolean validBlackToken(String accessToken)
    {
        //Redis 에 있는 엑세스 토큰인 경우 로그아웃 처리된 엑세스 토큰임.
        String blackToken = redisTemplate.opsForValue().get(accessToken);
        if(StringUtils.isNotBlank(blackToken))
            return false;
        return true;
    }

}
