package br.edu.materialconstrucao.controller.form;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

public class VendaForm {
  @NotNull(message = "Selecione o cliente.")
  private Long clienteId;

  @NotNull(message = "Selecione o vendedor.")
  private Long vendedorId;

  @Valid
  @NotEmpty(message = "Inclua ao menos um produto.")
  @Size(max = 100, message = "Use até 100 itens por venda.")
  private List<@NotNull(message = "Informe um item válido.") ItemForm> itens = new ArrayList<>();

  public Long getClienteId() {
    return clienteId;
  }

  public void setClienteId(Long clienteId) {
    this.clienteId = clienteId;
  }

  public Long getVendedorId() {
    return vendedorId;
  }

  public void setVendedorId(Long vendedorId) {
    this.vendedorId = vendedorId;
  }

  public List<ItemForm> getItens() {
    return itens;
  }

  public void setItens(List<ItemForm> itens) {
    this.itens = itens;
  }

  public static class ItemForm {
    @NotNull(message = "Selecione o produto.")
    private Long produtoId;

    @NotNull(message = "Informe a quantidade.")
    @Min(value = 1, message = "A quantidade deve ser positiva.")
    @Max(value = 1000000, message = "A quantidade máxima é 1.000.000.")
    private Integer quantidade = 1;

    public Long getProdutoId() {
      return produtoId;
    }

    public void setProdutoId(Long produtoId) {
      this.produtoId = produtoId;
    }

    public Integer getQuantidade() {
      return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
      this.quantidade = quantidade;
    }
  }
}
