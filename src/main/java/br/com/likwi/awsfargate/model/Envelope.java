package br.com.likwi.awsfargate.model;

import br.com.likwi.awsfargate.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Envelope {

    private EventType eventType;

    private String data;
}
