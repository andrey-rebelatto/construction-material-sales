package br.edu.materialconstrucao.model.service;

import br.edu.materialconstrucao.model.entity.Vendedor;
import br.edu.materialconstrucao.model.repository.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class VendedorService {
  private final VendedorRepository repository;
  private final VendaRepository vendas;

  public VendedorService(VendedorRepository repository, VendaRepository vendas) {
    this.repository = repository;
    this.vendas = vendas;
  }

  public List<Vendedor> listar(String busca) {
    return repository.findByNomeContainingIgnoreCaseOrderByNomeAsc(
        busca == null ? "" : busca.trim());
  }

  public Vendedor buscar(Long id) {
    return repository.findById(id).orElseThrow(RegistroNaoEncontradoException::new);
  }

  @Transactional
  public Vendedor salvar(Long id, String nome, String cpf) {
    if (repository.existsByCpfAndIdNot(cpf, id == null ? -1L : id))
      throw new RegraNegocioException("Este CPF já está cadastrado.");
    Vendedor entidade = id == null ? new Vendedor() : buscar(id);
    entidade.setNome(nome);
    entidade.setCpf(cpf);
    return repository.saveAndFlush(entidade);
  }

  @Transactional
  public void excluir(Long id) {
    Vendedor entidade = buscar(id);
    if (vendas.existsByVendedorId(id))
      throw new RegraNegocioException(
          "Este cadastro está vinculado a uma venda e não pode ser excluído.");
    repository.delete(entidade);
    repository.flush();
  }
}
