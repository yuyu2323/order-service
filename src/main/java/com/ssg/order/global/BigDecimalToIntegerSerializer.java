package com.ssg.order.global;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

public class BigDecimalToIntegerSerializer extends JsonSerializer<BigDecimal> {
    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // 소수점 제거 후 정수로 직렬화 (소수점 버림)
        gen.writeNumber(value.setScale(0, BigDecimal.ROUND_DOWN));
    }
}