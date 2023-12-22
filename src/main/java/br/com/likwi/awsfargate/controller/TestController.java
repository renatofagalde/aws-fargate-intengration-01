package br.com.likwi.awsfargate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@Slf4j
public class TestController {

    @GetMapping("/dog/{name}")
    public ResponseEntity<?> dogTest(@PathVariable String name){
        name = String.format("Name of dog: %s", name);
        log.info(name);

        /*
        para fazer uma pesquisa no cloudwatch insights
        fields @timestamp, @message |filter @message like /java/
        | sort @timestamp desc
        | limit 20
         */

      return ResponseEntity.ok(name);
    }
    @GetMapping("/cat/{name}")
    public ResponseEntity<?> catTest(@PathVariable String name){
        name = String.format("Name of cat v2: %s", name);
        log.info(name);

        return ResponseEntity.ok(name);
    }

}
