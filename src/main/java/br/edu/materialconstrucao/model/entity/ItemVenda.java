package br.edu.materialconstrucao.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "itens_venda")
public class ItemVenda {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "venda_id")
  private Venda venda;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "produto_id")
  private Produto produto;

  @Column(nullable = false)
  private int quantidade;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal precoUnitario;

  public Long getId() {
    return id;
  }

  public Venda getVenda() {
    return venda;
  }

  public void setVenda(Venda venda) {
    this.venda = venda;
  }

  public Produto getProduto() {
    return produto;
  }

  public void setProduto(Produto produto) {
    this.produto = produto;
  }

  public int getQuantidade() {
    return quantidade;
  }

  public void setQuantidade(int quantidade) {
    this.quantidade = quantidade;
  }

  public BigDecimal getPrecoUnitario() {
    return precoUnitario;
  }

  public void setPrecoUnitario(BigDecimal precoUnitario) {
    this.precoUnitario = precoUnitario;
  }

  public BigDecimal getSubtotal() {
    return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
  }
}
