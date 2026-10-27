package br.edu.materialconstrucao.model.repository;

import br.edu.materialconstrucao.model.entity.ItemVenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemVendaRepository extends JpaRepository<ItemVenda, Long> {
  boolean existsByProdutoId(Long id);
}
