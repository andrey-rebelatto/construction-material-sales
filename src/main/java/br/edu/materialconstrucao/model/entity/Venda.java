package br.edu.materialconstrucao.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendas")
public class Venda {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDateTime dataHora;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "cliente_id")
  private Cliente cliente;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "vendedor_id")
  private Vendedor vendedor;

  @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("id")
  private List<ItemVenda> itens = new ArrayList<>();

  public Long getId() {
    return id;
  }

  public LocalDateTime getDataHora() {
    return dataHora;
  }

  public void setDataHora(LocalDateTime dataHora) {
    this.dataHora = dataHora;
  }

  public Cliente getCliente() {
    return cliente;
  }

  public void setCliente(Cliente cliente) {
    this.cliente = cliente;
  }

  public Vendedor getVendedor() {
    return vendedor;
  }

  public void setVendedor(Vendedor vendedor) {
    this.vendedor = vendedor;
  }

  public List<ItemVenda> getItens() {
    return itens;
  }

  public BigDecimal getTotal() {
    return itens.stream().map(ItemVenda::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public void adicionarItem(ItemVenda item) {
    itens.add(item);
    item.setVenda(this);
  }
}
