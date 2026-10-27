package br.edu.materialconstrucao.controller.form;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProdutoForm {
  @NotBlank(message = "Informe o nome.")
  @Size(max = 120, message = "Use até 120 caracteres.")
  private String nome;

  @NotNull(message = "Informe o preço.")
  @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
  @Digits(integer = 10, fraction = 2, message = "Use até 10 dígitos e 2 casas decimais.")
  private BigDecimal preco;

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome == null ? null : nome.trim();
  }

  public BigDecimal getPreco() {
    return preco;
  }

  public void setPreco(BigDecimal preco) {
    this.preco = preco;
  }
}
