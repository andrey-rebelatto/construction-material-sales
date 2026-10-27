package br.edu.materialconstrucao.model.repository;

import br.edu.materialconstrucao.model.entity.Venda;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;

public interface VendaRepository extends JpaRepository<Venda, Long> {
  @EntityGraph(attributePaths = {"cliente", "vendedor", "itens", "itens.produto"})
  List<Venda> findAllByOrderByDataHoraDesc();

  @EntityGraph(attributePaths = {"cliente", "vendedor", "itens", "itens.produto"})
  Optional<Venda> findOneById(Long id);

  boolean existsByClienteId(Long id);

  boolean existsByVendedorId(Long id);
}
