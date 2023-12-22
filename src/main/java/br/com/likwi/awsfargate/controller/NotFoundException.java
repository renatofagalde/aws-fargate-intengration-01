package br.com.likwi.awsfargate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "id inválido")
public class NotFoundException extends RuntimeException{
}
