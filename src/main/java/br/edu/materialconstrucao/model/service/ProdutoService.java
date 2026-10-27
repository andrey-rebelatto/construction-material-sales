package br.edu.materialconstrucao.model.service;

import br.edu.materialconstrucao.model.entity.Produto;
import br.edu.materialconstrucao.model.repository.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProdutoService {
  private final ProdutoRepository repository;
  private final ItemVendaRepository itens;

  public ProdutoService(ProdutoRepository repository, ItemVendaRepository itens) {
    this.repository = repository;
    this.itens = itens;
  }

  public List<Produto> listar(String busca) {
    return repository.findByNomeContainingIgnoreCaseOrderByNomeAsc(
        busca == null ? "" : busca.trim());
  }

  public Produto buscar(Long id) {
    return repository.findById(id).orElseThrow(RegistroNaoEncontradoException::new);
  }

  @Transactional
  public Produto salvar(Long id, String nome, java.math.BigDecimal preco) {
    Produto entidade = id == null ? new Produto() : buscar(id);
    entidade.setNome(nome);
    entidade.setPreco(preco);
    return repository.saveAndFlush(entidade);
  }

  @Transactional
  public void excluir(Long id) {
    Produto entidade = buscar(id);
    if (itens.existsByProdutoId(id))
      throw new RegraNegocioException(
          "Este cadastro está vinculado a uma venda e não pode ser excluído.");
    repository.delete(entidade);
    repository.flush();
  }
}
