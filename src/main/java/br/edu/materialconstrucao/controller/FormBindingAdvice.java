package br.edu.materialconstrucao.controller;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
public class FormBindingAdvice {
  @InitBinder
  void configurar(WebDataBinder binder) {
    binder.setAutoGrowCollectionLimit(100);
  }
}
