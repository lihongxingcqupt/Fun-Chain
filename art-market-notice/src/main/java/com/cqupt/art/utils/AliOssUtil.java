package com.cqupt.art.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class AliOssUtil {
    private static final String endpoint = "oss-cn-zhangjiakou.aliyuncs.com";
    private static final String accessKeyId = "LTAI5t5jGPYhn7L1LUmBnLxt";
    private static final String accessKeySecret = "k6uz1pD18kvYQHCMYrOcODW9AC3i0U";
    private static final String bucketName = "nft-demo";


    public static String uploadFile(File uploadFile, String objectName) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try (InputStream input = new FileInputStream(uploadFile)) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, uploadFile);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentDisposition("public");
            metadata.setObjectAcl(CannedAccessControlList.PublicRead);
            putObjectRequest.setMetadata(metadata);
            ossClient.putObject(putObjectRequest);
            return "https://" + bucketName + "." + endpoint + "/" + objectName;
        } catch (OSSException oe) {
            log.error(oe.getMessage());
        } catch (ClientException ce) {
            log.error(ce.getMessage());
        } catch (IOException ie) {
            log.error(ie.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return null;
    }

    public static String uploadFile(InputStream is, String objectName) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, is);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentDisposition("public");
        metadata.setObjectAcl(CannedAccessControlList.PublicRead);
        putObjectRequest.setMetadata(metadata);
        ossClient.putObject(putObjectRequest);
        return "https://" + bucketName + "." + endpoint + "/" + objectName;
    }

    public static void main(String[] args) {
        File file = new File("F:\\WorkSpace\\chain\\meta_data_json\\测试作家1\\测试作品1.json");
        String objectName = "json/测试作家1/测试作品1.json";
        String url = uploadFile(file, objectName);
        System.out.println(url);
    }
}
