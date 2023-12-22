package br.com.likwi.awsfargate.config.local;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.BucketNotificationConfiguration;
import com.amazonaws.services.s3.model.S3Event;
import com.amazonaws.services.s3.model.TopicConfiguration;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.util.Topics;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local") //diferente de local
public class S3ConfigLocal {

    private String BUCKET_NAME = "likwi-bucket-s3-files";

    private String SQS_TOPIC = "S3-FILES-EVENTS";

    private AmazonS3 amazonS3;

    public S3ConfigLocal() {
        this.amazonS3 = getAmazonS3();
        createBuckt();
        AmazonSNS snsClient = getAmazonSNS();
        String s3TopicArn = createTopic(snsClient);
        AmazonSQS sqsClient = getAmazonSQS();
        createQueue(snsClient, s3TopicArn, sqsClient);
        configureBuckt(s3TopicArn);
    }

    private void configureBuckt(String s3TopicArn) {
        TopicConfiguration topicConfiguration = new TopicConfiguration();
        topicConfiguration.setTopicARN(s3TopicArn);
        topicConfiguration.addEvent(S3Event.ObjectCreatedByPut);

        this.amazonS3.setBucketNotificationConfiguration(BUCKET_NAME,
                new BucketNotificationConfiguration()
                        .addConfiguration("putObject", topicConfiguration));
    }

    private void createQueue(AmazonSNS snsClient, String s3TopicArn, AmazonSQS sqsClient) {
        final String queueURL = sqsClient.createQueue(new CreateQueueRequest(SQS_TOPIC)).getQueueUrl();
        Topics.subscribeQueue(snsClient, sqsClient, s3TopicArn, queueURL);
    }

    private String createTopic(AmazonSNS snsClient) {
        CreateTopicRequest createTopicRequest = new CreateTopicRequest(SQS_TOPIC);
        return snsClient.createTopic(createTopicRequest).getTopicArn();
    }

    private AmazonSQS getAmazonSQS() {
        return AmazonSQSClient.builder()
                .withEndpointConfiguration(new AwsClientBuilder
                        .EndpointConfiguration("http://localhost:4566", Regions.US_EAST_1.getName()))
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
    }

    private AmazonSNS getAmazonSNS() {
        return AmazonSNSClient.builder()
                .withEndpointConfiguration(new AwsClientBuilder
                        .EndpointConfiguration("http://localhost:4566", Regions.US_EAST_1.getName()))
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();

    }

    private void createBuckt() {
        this.amazonS3.createBucket(BUCKET_NAME);
    }

    private AmazonS3 getAmazonS3() {
        AWSCredentials awsCredentials = new BasicAWSCredentials("test", "test");

        this.amazonS3 = AmazonS3Client.builder()
                .withEndpointConfiguration(new AwsClientBuilder
                        .EndpointConfiguration("http://localhost:4566", Regions.US_EAST_1.getName()))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .enablePathStyleAccess() //habilitar para acessar o localhost
                .build();

        return this.amazonS3;
    }

    @Bean
    public AmazonS3 amazonS3Client() {
        return this.amazonS3;
    }
}
