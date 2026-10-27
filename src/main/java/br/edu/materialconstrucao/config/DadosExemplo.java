package br.edu.materialconstrucao.config;

import br.edu.materialconstrucao.model.service.*;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "app.dados-exemplo", havingValue = "true")
public class DadosExemplo implements CommandLineRunner {
  private final ClienteService clientes;
  private final VendedorService vendedores;
  private final ProdutoService produtos;
  private final VendaService vendas;

  public DadosExemplo(
      ClienteService clientes,
      VendedorService vendedores,
      ProdutoService produtos,
      VendaService vendas) {
    this.clientes = clientes;
    this.vendedores = vendedores;
    this.produtos = produtos;
    this.vendas = vendas;
  }

  @Override
  @Transactional
  public void run(String... args) {
    if (!clientes.listar("").isEmpty()
        || !produtos.listar("").isEmpty()
        || !vendedores.listar("").isEmpty()
        || !vendas.listar().isEmpty()) return;
    var ana = clientes.salvar(null, "Ana Souza", "52998224725");
    var carlos = clientes.salvar(null, "Carlos Lima", "11144477735");
    clientes.salvar(null, "Juliana Pereira", "39053344705");
    var bruno = vendedores.salvar(null, "Bruno Alves", "45317828791");
    var marina = vendedores.salvar(null, "Marina Costa", "71460238001");
    var cimento = produtos.salvar(null, "Cimento CP II · saco 50 kg", new BigDecimal("38.90"));
    var areia = produtos.salvar(null, "Areia média · saco 20 kg", new BigDecimal("6.50"));
    var tijolo = produtos.salvar(null, "Tijolo cerâmico · 8 furos", new BigDecimal("1.20"));
    var vergalhao =
        produtos.salvar(null, "Vergalhão CA-50 · 10 mm / 12 m", new BigDecimal("54.00"));
    produtos.salvar(null, "Tinta acrílica branca · lata 18 L", new BigDecimal("289.90"));
    vendas.salvar(
        null,
        ana.getId(),
        bruno.getId(),
        List.of(
            new VendaService.ItemPedido(cimento.getId(), 10),
            new VendaService.ItemPedido(areia.getId(), 20)));
    vendas.salvar(
        null,
        carlos.getId(),
        marina.getId(),
        List.of(
            new VendaService.ItemPedido(tijolo.getId(), 500),
            new VendaService.ItemPedido(vergalhao.getId(), 4)));
  }
}
