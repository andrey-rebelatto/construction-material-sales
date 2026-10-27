package br.edu.materialconstrucao.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
public class Produto {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 120)
  private String nome;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal preco;

  public Long getId() {
    return id;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public BigDecimal getPreco() {
    return preco;
  }

  public void setPreco(BigDecimal preco) {
    this.preco = preco;
  }
}
