package br.com.jtech.tasklist.adapters.output;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class BCryptPasswordEncoderAdapterTest {

    private final BCryptPasswordEncoderAdapter adapter = new BCryptPasswordEncoderAdapter(new BCryptPasswordEncoder(12));

    @Test
    void shouldEncodeAndMatchPassword() {
        String hash = adapter.encode("secret123");
        assertThat(hash).isNotEqualTo("secret123");
        assertThat(adapter.matches("secret123", hash)).isTrue();
        assertThat(adapter.matches("wrong", hash)).isFalse();
    }
}
