package com.dream.bobi.manage;

import com.dream.bobi.commons.enums.MsgCode;
import com.dream.bobi.support.BizException;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class TencentCosTools {
    private final static Logger log = LoggerFactory.getLogger(TencentCosTools.class);

    // COS的SecretId
    private static String secretId = "AKIDQoS3Wq6Ri1IROenooeHi0FJBqOksleec";
    // COS的SecretKey
    private static String secretKey = "04LXXbbVH7uWE652teP4nSozvdNBuRqC";
    //文件上传后访问路径的根路径，后面要最佳文件名字与类型
    private static String rootSrc = "https://storage-1321511650.cos.ap-shanghai.myqcloud.com/";
    //上传的存储桶的地域，可参考根路径https://qq-test-1303******.cos.地域.myqcloud.com,此参数在COS的后台能查询。
    private static String bucketAddr = "ap-shanghai";
    //存储桶的名字，是自己在存储空间自己创建的，我创建的名字是：qq-test-1303******
    private static String bucketName = "storage-1321511650";
    private static String appId = "1321511650";
    /**
     * 获取Cos客户端
     *
     * @return
     */
    public static COSClient getCOSClient() {
        // 1 初始化用户身份信息（secretId, secretKey）。
        // SECRETID 和 SECRETKEY 请登录访问管理控制台 https://console.cloud.tencent.com/cam/capi 进行查看和管理
        COSCredentials cred = new BasicCOSCredentials(appId, secretId, secretKey);
        // 2 设置 bucket 的地域, COS 地域的简称请参见 https://cloud.tencent.com/document/product/436/6224
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region region = new Region(bucketAddr);
        ClientConfig clientConfig = new ClientConfig(region);
        // 这里建议设置使用 https 协议
        // 从 5.6.54 版本开始，默认使用了 https
        //clientConfig.setHttpProtocol(HttpProtocol.https);
        // 3 生成 cos 客户端。
        COSClient cosClient = new COSClient(cred, clientConfig);
        return cosClient;
    }



    // 上传图片
    public static String upload(MultipartFile file) throws IOException {
        COSClient cosClient = getCOSClient();

        // 获取文件输入流
        InputStream inputStream = file.getInputStream();

        // 指定文件将要存放的存储桶
        //获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        log.info("origin file name:{}",originalFilename);
        int index = originalFilename.lastIndexOf('.');
        String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        String extname = originalFilename.substring(index);

        String newFileName = UUID.randomUUID() + extname;

        log.info("new file name:{}",newFileName);

        // 指定文件上传到 COS 上的路径，即对象键。例如对象键为 folder/picture.jpg，则表示将文件 picture.jpg 上传到 folder 路径下
        String key = "training/" + newFileName;
        try {
            // 创建上传Object的Metadata
            ObjectMetadata objectMetadata = new ObjectMetadata();
            // - 使用输入流存储，需要设置请求长度
            objectMetadata.setContentLength(inputStream.available());
            // - 设置缓存
            objectMetadata.setCacheControl("no-cache");
            // - 设置Content-Type
            objectMetadata.setContentType(fileType);
            //上传文件
            cosClient.putObject(bucketName, key, inputStream, objectMetadata);
            //关闭 cosClient，并释放 HTTP 连接的后台管理线程
            log.info("{} upload success!",newFileName);
            return key;
        } catch (Exception e) {
            log.error("upload failed,", e);
            throw new BizException(MsgCode.UPLOAD_FAILED);
        } finally {
            cosClient.shutdown();
            inputStream.close();
        }

    }


    //获取图片返回给前端
    public static void testGet(HttpServletResponse response) throws IOException {
        COSClient cosClient = getCOSClient();

        // 存储桶的命名格式为 BucketName-APPID，此处填写的存储桶名称必须为此格式
        String bucketName = "test-1318187204";
        // 对象键(Key)是对象在存储桶中的唯一标识。详情请参见 [对象键](https://cloud.tencent.com/document/product/436/13324)
        String key = "picture/test.jpg";

        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        COSObjectInputStream cosObjectInput = null;

        // 判断COS是否有用户头像，若没有则返回默认头像
        boolean objectExists = cosClient.doesObjectExist(bucketName, key);

        try {
            COSObject cosObject = cosClient.getObject(getObjectRequest);
            cosObjectInput = cosObject.getObjectContent();
        } catch (CosServiceException e) {
            e.printStackTrace();
        } catch (CosClientException e) {
            e.printStackTrace();
        }

        // 获取字节流
        byte[] bytes = null;
        try {
            bytes = IOUtils.toByteArray(cosObjectInput);
            // 将byte[]写入响应体
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 用完流之后一定要调用 close()
            cosObjectInput.close();
            cosClient.shutdown();
        }
    }
}