package br.com.jtech.tasklist.config.infra.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionsTest {

    @Test
    void shouldExposeMessages() {
        assertThat(new BusinessException("a").getMessage()).isEqualTo("a");
        assertThat(new NotFoundException("b").getMessage()).isEqualTo("b");
        assertThat(new ConflictException("c").getMessage()).isEqualTo("c");
        assertThat(new UnauthorizedException("d").getMessage()).isEqualTo("d");
        assertThat(new ForbiddenException("e").getMessage()).isEqualTo("e");
    }
}
