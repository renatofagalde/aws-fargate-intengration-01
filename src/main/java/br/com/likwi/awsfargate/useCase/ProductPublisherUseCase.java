package br.com.likwi.awsfargate.useCase;

import br.com.likwi.awsfargate.enums.EventType;
import br.com.likwi.awsfargate.model.Product;

public interface ProductPublisherUseCase {

     void publishProductEvent(Product product, EventType eventType, String userName);
}
