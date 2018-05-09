package com.bjnlmf.nerc.zhihu.util;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class JsonUtil {
    private static ObjectMapper singleton;

    public static ObjectMapper ObjectMapper(){
        if (singleton != null) { return singleton; }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        singleton = objectMapper;
        return singleton;
    }

    public static String jsonify(Object value) {

        try {
            return ObjectMapper().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw  new RuntimeException(e.getMessage());
        }
    }

    public static <T> T parse(String src, Class<T> valueType) {
        try {
            return ObjectMapper().readValue(src,valueType);
        } catch (IOException e) {
            throw  new RuntimeException(e.getMessage());
        }
    }

    public static <T> T parse(String src, TypeReference valueTypeRef) {
        try {
            return ObjectMapper().readValue(src,valueTypeRef);
        } catch (IOException e) {
            throw  new RuntimeException(e.getMessage());
        }
    }
}
