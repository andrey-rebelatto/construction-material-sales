package br.edu.materialconstrucao.view;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component("fmt")
public class Formatos {
  public String moeda(BigDecimal valor) {
    return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(valor);
  }

  public String cpf(String cpf) {
    return cpf.substring(0, 3)
        + "."
        + cpf.substring(3, 6)
        + "."
        + cpf.substring(6, 9)
        + "-"
        + cpf.substring(9);
  }
}
