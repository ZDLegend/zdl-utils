package zdl.util.s3;

import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.utils.StringUtils;

import java.util.List;

import static software.amazon.awssdk.core.SdkSystemSetting.*;

/**
 * @author ZDLegend
 * @version 1.0
 * @date 2021/05/25/ 17:34
 */
public class AwsS3Client {

    private static final Region DEFAULT_REGION = Region.of("default");

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

    }

}
