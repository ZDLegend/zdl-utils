package zdl.util.s3;

import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;
import software.amazon.awssdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static software.amazon.awssdk.core.SdkSystemSetting.*;

/**
 * @author ZDLegend
 * @version 1.0
 * @date 2021/05/25/ 17:34
 */
public class AwsS3Client {

    private static final Region DEFAULT_REGION = Region.of("default");

    private static final String SLASH = "/";

    private final String bucketName;
    private final S3Client s3Client;

    public AwsS3Client(String bucketName, String awsAccessKeyId, String awsSecretAccessKey, String awsSessionToken) {
        this.bucketName = bucketName;
        if (StringUtils.isNotBlank(awsAccessKeyId)) {
            System.setProperty(AWS_ACCESS_KEY_ID.property(), awsAccessKeyId);
        }
        if (StringUtils.isNotBlank(awsSecretAccessKey)) {
            System.setProperty(AWS_SECRET_ACCESS_KEY.property(), awsSecretAccessKey);
        }
        if (StringUtils.isNotBlank(awsSessionToken)) {
            System.setProperty(AWS_SESSION_TOKEN.property(), awsSessionToken);
        }
        s3Client = S3Client.builder()
                .credentialsProvider(SystemPropertyCredentialsProvider.create())
                .region(DEFAULT_REGION)
                .build();
    }

    public boolean existBucket(String bucketName) {
        ListBucketsResponse response = s3Client.listBuckets();
        List<Bucket> buckets = response.buckets();
        if (buckets.isEmpty()) {
            return false;
        }
        return buckets.stream()
                .anyMatch(bucket -> bucket.name().equals(bucketName));
    }

    public void createBucket(String bucketName) {
        S3Waiter s3Waiter = s3Client.waiter();

        CreateBucketRequest bucketRequest = CreateBucketRequest.builder().bucket(bucketName).build();
        s3Client.createBucket(bucketRequest);

        HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder().bucket(bucketName).build();
        WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketExists(bucketRequestWait);
        waiterResponse.matched().response().ifPresent(s -> System.out.println(s));
    }

    public void remove(String key) {
        ObjectIdentifier objectIdentifier = ObjectIdentifier.builder().key(key).build();
        s3Client.deleteObjects(DeleteObjectsRequest.builder().bucket(bucketName).delete(Delete.builder().objects(objectIdentifier).build()).build());
    }

    public void putObject(String key, byte[] object) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(key).build();
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(object));
    }

    public void putText(String key, String text) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(key).build();
        s3Client.putObject(putObjectRequest, RequestBody.fromString(text));
    }

    public byte[] readBytes(String key) {
        GetObjectRequest objectRequest = GetObjectRequest.builder().bucket(bucketName).key(key).build();
        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(objectRequest);
        return objectBytes.asByteArray();
    }

    public String reaTexts(String key) {
        return new String(readBytes(key));
    }

    public List<String> list(String path) {
        if (!path.endsWith(SLASH)) {
            path = path + SLASH;
        }
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName).delimiter(SLASH).prefix(path).build();
        List<String> result = new ArrayList<>();
        for (ListObjectsV2Response response : s3Client.listObjectsV2Paginator(request)) {
            result.addAll(response.contents().stream().map(S3Object::key).collect(Collectors.toList()));
            result.addAll(response.commonPrefixes().stream().map(CommonPrefix::prefix).collect(Collectors.toList()));
        }
        return result;
    }
}
