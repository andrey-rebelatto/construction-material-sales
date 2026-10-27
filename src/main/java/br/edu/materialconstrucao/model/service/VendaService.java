package br.edu.materialconstrucao.model.service;

import br.edu.materialconstrucao.model.entity.*;
import br.edu.materialconstrucao.model.repository.VendaRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Transactional(readOnly = true)
public class VendaService {
  private final VendaRepository repository;
  private final ClienteService clientes;
  private final VendedorService vendedores;
  private final ProdutoService produtos;

  public record ItemPedido(@NotNull Long produtoId, @Min(1) @Max(1000000) int quantidade) {}

  public VendaService(
      VendaRepository repository,
      ClienteService clientes,
      VendedorService vendedores,
      ProdutoService produtos) {
    this.repository = repository;
    this.clientes = clientes;
    this.vendedores = vendedores;
    this.produtos = produtos;
  }

  public List<Venda> listar() {
    return repository.findAllByOrderByDataHoraDesc();
  }

  public Venda buscar(Long id) {
    return repository.findOneById(id).orElseThrow(RegistroNaoEncontradoException::new);
  }

  @Transactional
  public Venda salvar(
      Long id,
      @NotNull Long clienteId,
      @NotNull Long vendedorId,
      @NotEmpty @Size(max = 100) List<@Valid ItemPedido> pedidos) {
    Venda venda = id == null ? new Venda() : buscar(id);
    venda.setCliente(clientes.buscar(clienteId));
    venda.setVendedor(vendedores.buscar(vendedorId));
    if (id == null) venda.setDataHora(LocalDateTime.now());
    Map<Long, ItemVenda> existentes = new HashMap<>();
    venda.getItens().forEach(item -> existentes.put(item.getProduto().getId(), item));
    Map<Long, Integer> quantidades = new LinkedHashMap<>();
    for (ItemPedido pedido : pedidos) {
      int quantidade = quantidades.getOrDefault(pedido.produtoId(), 0) + pedido.quantidade();
      if (quantidade > 1000000)
        throw new RegraNegocioException("A quantidade máxima por produto é 1.000.000.");
      quantidades.put(pedido.produtoId(), quantidade);
    }
    venda.getItens().removeIf(item -> !quantidades.containsKey(item.getProduto().getId()));
    quantidades.forEach(
        (produtoId, quantidade) -> {
          ItemVenda item = existentes.get(produtoId);
          if (item == null) {
            Produto produto = produtos.buscar(produtoId);
            item = new ItemVenda();
            item.setProduto(produto);
            item.setPrecoUnitario(produto.getPreco());
            venda.adicionarItem(item);
          }
          item.setQuantidade(quantidade);
        });
    return repository.saveAndFlush(venda);
  }

  @Transactional
  public void excluir(Long id) {
    repository.delete(buscar(id));
    repository.flush();
  }
}
