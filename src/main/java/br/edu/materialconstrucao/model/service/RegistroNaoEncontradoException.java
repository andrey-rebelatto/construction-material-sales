package br.edu.materialconstrucao.model.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RegistroNaoEncontradoException extends RuntimeException {
  public RegistroNaoEncontradoException() {
    super("Registro não encontrado.");
  }
}
