package com.bjnlmf.nerc.zhihu.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @Prject: nmPerson
 * @Package: com.bjnlmf.nerc.zhihu.config
 * @Description: 连接OSS参数
 * @author: cfQiao
 * @date: 2018/1/18 16:48
 */

@Component
@PropertySource(value = {"classpath:/application.properties"})
public class OSSConfig {

    @Value("${oss.endpoint}")
    private String endpoint;

    @Value("${oss.accessKeyId}")
    private String accessKeyId;

    @Value("${oss.accessKeySecret}")
    private String accessKeySecret;

    @Value("${oss.bucketName}")
    private String bucketName;

    public String getEndpoint() {
        return endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public String getBucketName() {
        return bucketName;
    }
}
