package br.edu.materialconstrucao.model.repository;

import br.edu.materialconstrucao.model.entity.Vendedor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendedorRepository extends JpaRepository<Vendedor, Long> {
  List<Vendedor> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

  boolean existsByCpfAndIdNot(String cpf, Long id);
}
