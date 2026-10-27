package br.edu.materialconstrucao.model.service;

import br.edu.materialconstrucao.model.entity.Cliente;
import br.edu.materialconstrucao.model.repository.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ClienteService {
  private final ClienteRepository repository;
  private final VendaRepository vendas;

  public ClienteService(ClienteRepository repository, VendaRepository vendas) {
    this.repository = repository;
    this.vendas = vendas;
  }

  public List<Cliente> listar(String busca) {
    return repository.findByNomeContainingIgnoreCaseOrderByNomeAsc(
        busca == null ? "" : busca.trim());
  }

  public Cliente buscar(Long id) {
    return repository.findById(id).orElseThrow(RegistroNaoEncontradoException::new);
  }

  @Transactional
  public Cliente salvar(Long id, String nome, String cpf) {
    if (repository.existsByCpfAndIdNot(cpf, id == null ? -1L : id))
      throw new RegraNegocioException("Este CPF já está cadastrado.");
    Cliente entidade = id == null ? new Cliente() : buscar(id);
    entidade.setNome(nome);
    entidade.setCpf(cpf);
    return repository.saveAndFlush(entidade);
  }

  @Transactional
  public void excluir(Long id) {
    Cliente entidade = buscar(id);
    if (vendas.existsByClienteId(id))
      throw new RegraNegocioException(
          "Este cadastro está vinculado a uma venda e não pode ser excluído.");
    repository.delete(entidade);
    repository.flush();
  }
}
