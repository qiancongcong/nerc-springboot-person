package com.bjnlmf.nerc.zhihu.util;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.bjnlmf.nerc.zhihu.config.OSSConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

/**
 * @Description: 从OSS上传和下载图片
 * @author: cfQiao
 * @date: 2018/1/18 9:49
 */
public class OSSImgUitl {

    private static Logger logger = LoggerFactory.getLogger(OSSImgUitl.class);

    //上传图片
    public static void uploadImg(MultipartFile image, String imageName, OSSConfig ossConfig) {
        OSSClient ossClient = new OSSClient(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
        try {
            ossClient.putObject(ossConfig.getBucketName(), imageName, image.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OSSException oe) {
            logger.debug("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            logger.debug("Error Message: " + oe.getErrorCode());
            logger.debug("Error Code:       " + oe.getErrorCode());
            logger.debug("Request ID:      " + oe.getRequestId());
            logger.debug("Host ID:           " + oe.getHostId());
        } catch (ClientException ce) {
            logger.debug("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            logger.debug("Error Message: " + ce.getMessage());
        } finally {
        /*
         * Do not forget to shut down the client finally to release all allocated resources.
         */
            ossClient.shutdown();
        }
    }

    /**
     * @param key   图片名
     * @param style 图片处理样式
     * @return
     */
    public static String getImg(String key, String style, OSSConfig ossConfig) {

        OSSClient ossClient = new OSSClient(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());

        String path = null;
        try {
        
        /*
         * Download an object from your bucket
         */
            //获取上传文件key的url 
            GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(ossConfig.getBucketName(), key, HttpMethod.GET);
            // 图片处理样式 
            // String style = "image/watermark,text_SGVsbG8g5Zu-54mH5pyN5YqhIQ";
            if (null != style && !"".equals(style)) {
                req.setProcess(style);
            }

            //  设置URL过期时间为100年  1000*3600L*24*365*100
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 3600L * 24 * 365 * 100);
            req.setExpiration(expiration);

            URL signedUrl = ossClient.generatePresignedUrl(req);
            path = signedUrl.getFile();

        } catch (OSSException oe) {
            logger.debug("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            logger.debug("Error Message: " + oe.getErrorCode());
            logger.debug("Error Code:       " + oe.getErrorCode());
            logger.debug("Request ID:      " + oe.getRequestId());
            logger.debug("Host ID:           " + oe.getHostId());
        } catch (ClientException ce) {
            logger.debug("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            logger.debug("Error Message: " + ce.getMessage());
        } finally {
        /*
         * Do not forget to shut down the client finally to release all allocated resources.
         */
            ossClient.shutdown();
        }
        return path;
    }
}
    