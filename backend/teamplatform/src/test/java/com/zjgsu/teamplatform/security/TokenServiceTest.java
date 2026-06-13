package com.zjgsu.teamplatform.security;

import com.zjgsu.teamplatform.exception.BizException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Token 服务单元测试，覆盖签发、解析和非法令牌拦截。
 */
class TokenServiceTest {

    private TokenService tokenService;

    /**
     * 初始化固定密钥，确保签名结果可重复验证。
     */
    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "test-secret");
    }

    /**
     * 签发后的 token 可以解析回原始用户 ID。
     */
    @Test
    void generateToken_thenParseUserId_returnsUserId() {
        String token = tokenService.generateToken(42L);

        assertThat(tokenService.parseUserId(token)).isEqualTo(42L);
    }

    /**
     * 空 token 会被判定为未认证，避免接口误放行。
     */
    @Test
    void parseUserId_whenBlankToken_throwsBizException() {
        assertThatThrownBy(() -> tokenService.parseUserId(" "))
            .isInstanceOf(BizException.class);
    }

    /**
     * 被篡改的签名不能通过校验。
     */
    @Test
    void parseUserId_whenSignatureChanged_throwsBizException() {
        String token = tokenService.generateToken(7L);
        String tamperedToken = token.substring(0, token.lastIndexOf('.') + 1) + "bad-sign";

        assertThatThrownBy(() -> tokenService.parseUserId(tamperedToken))
            .isInstanceOf(BizException.class);
    }
}
