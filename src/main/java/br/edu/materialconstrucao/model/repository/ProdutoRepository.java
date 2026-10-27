package br.edu.materialconstrucao.model.repository;

import br.edu.materialconstrucao.model.entity.Produto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
  List<Produto> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);
}
