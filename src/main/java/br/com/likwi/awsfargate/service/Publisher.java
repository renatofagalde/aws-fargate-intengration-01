package br.com.likwi.awsfargate.service;

import br.com.likwi.awsfargate.enums.EventType;
import br.com.likwi.awsfargate.model.Envelope;
import br.com.likwi.awsfargate.model.Product;
import br.com.likwi.awsfargate.model.ProductEvent;
import br.com.likwi.awsfargate.useCase.ProductPublisherUseCase;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.Topic;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Publisher implements ProductPublisherUseCase {
    private AmazonSNS amazonSNS;
    private Topic topic;
    private ObjectMapper objectMapper;

    public Publisher(AmazonSNS amazonSNS,
                     @Qualifier("productEventsTopic") Topic topic,
                     ObjectMapper objectMapper) {

        this.amazonSNS = amazonSNS;
        this.topic = topic;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    @Override
    public void publishProductEvent(Product product, EventType eventType, String userName) {
        final ProductEvent productEvent = ProductEvent.builder()
                .productId(product.getId())
                .code(product.getCode())
                .userName(userName).build();

        final Envelope envelope = Envelope.builder()
                .eventType(eventType)
                .data(objectMapper.writeValueAsString(productEvent))
                .build();

        log.info(String.format("Publicando o evento: %s", this.objectMapper.writeValueAsString(envelope)));

        final PublishResult publishResult = this.amazonSNS.publish(this.topic.getTopicArn(),
                this.objectMapper.writeValueAsString(envelope));

        log.info(String.format("Publicado o evento: %s. Id: %s",
                this.objectMapper.writeValueAsString(envelope), publishResult.getMessageId()));

    }
}
