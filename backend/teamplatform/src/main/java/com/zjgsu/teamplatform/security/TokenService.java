package com.zjgsu.teamplatform.security;

import com.zjgsu.teamplatform.common.ErrorCode;
import com.zjgsu.teamplatform.exception.BizException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

/**
 * 轻量级 Token 服务，避免临近验收引入额外安全框架配置风险。
 */
@Service
public class TokenService {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final long TOKEN_TTL_SECONDS = 7 * 24 * 60 * 60L;

    @Value("${app.auth.secret:team-platform-secret}")
    private String secret;

    /**
     * 为用户签发带过期时间和签名的访问令牌。
     */
    public String generateToken(Long userId) {
        long expiresAt = Instant.now().getEpochSecond() + TOKEN_TTL_SECONDS;
        String payload = userId + ":" + expiresAt;
        return base64Url(payload) + "." + sign(payload);
    }

    /**
     * 校验令牌并解析用户 ID。
     */
    public Long parseUserId(String token) {
        if (token == null || token.isBlank()) {
            throw unauthorized();
        }
        String[] parts = token.split("\\.");
        if (parts.length != 2) {
            throw unauthorized();
        }
        String payload = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
        if (!sign(payload).equals(parts[1])) {
            throw unauthorized();
        }
        String[] fields = payload.split(":");
        if (fields.length != 2) {
            throw unauthorized();
        }
        long expiresAt = Long.parseLong(fields[1]);
        if (expiresAt < Instant.now().getEpochSecond()) {
            throw unauthorized();
        }
        return Long.parseLong(fields[0]);
    }

    private String base64Url(String value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String sign(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Token signing failed", e);
        }
    }

    private BizException unauthorized() {
        return new BizException(ErrorCode.UNAUTHORIZED, "登录已过期，请重新登录");
    }
}
