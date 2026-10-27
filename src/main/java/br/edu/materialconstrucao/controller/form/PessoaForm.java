package br.edu.materialconstrucao.controller.form;

import jakarta.validation.constraints.*;

public class PessoaForm {
  @NotBlank(message = "Informe o nome.")
  @Size(max = 120, message = "Use até 120 caracteres.")
  private String nome;

  @NotBlank(message = "Informe o CPF.")
  @org.hibernate.validator.constraints.br.CPF(message = "Informe um CPF válido.")
  private String cpf;

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome == null ? null : nome.trim();
  }

  public String getCpf() {
    return cpf;
  }

  public void setCpf(String cpf) {
    this.cpf = cpf == null ? null : cpf.replaceAll("[^0-9]", "");
  }
}
