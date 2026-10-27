package br.edu.materialconstrucao.model.repository;

import br.edu.materialconstrucao.model.entity.Cliente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
  List<Cliente> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

  boolean existsByCpfAndIdNot(String cpf, Long id);
}
