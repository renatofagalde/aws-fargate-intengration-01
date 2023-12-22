package br.com.likwi.awsfargate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class URLResponse {
    private String url;
    private long expirationTime;
}
