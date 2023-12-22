package br.com.likwi.awsfargate.config.local;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.Topic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local") //apenas no perfil local
@Slf4j
public class SNSCreateLocal {

    private AmazonSNS snsClient;
    private String productEventsTopic;

    public SNSCreateLocal() {
        this.snsClient = AmazonSNSClient.builder()
                .withEndpointConfiguration(new AwsClientBuilder
                        .EndpointConfiguration("http://localhost:4566", Regions.US_EAST_1.getName()))
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
        CreateTopicRequest createTopicRequest = new CreateTopicRequest("PRODUCT-EVENTS");
        this.productEventsTopic = this.snsClient.createTopic(createTopicRequest).getTopicArn();
        log.info(String.format("SNS topic ARN: %s",this.productEventsTopic));
    }

    @Bean
    public AmazonSNS snsClient() {
        return this.snsClient;
    }

    @Bean(name = "productEventsTopic")
    public Topic snsProductEventsTopic() {
        return new Topic().withTopicArn(this.productEventsTopic);
    }
}
