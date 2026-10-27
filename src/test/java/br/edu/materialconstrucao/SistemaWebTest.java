package br.edu.materialconstrucao;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.edu.materialconstrucao.model.entity.*;
import br.edu.materialconstrucao.model.repository.*;
import br.edu.materialconstrucao.model.service.*;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SistemaWebTest {
  @Autowired MockMvc mvc;
  @Autowired ClienteService clientes;
  @Autowired VendedorService vendedores;
  @Autowired ProdutoService produtos;
  @Autowired VendaService vendas;
  @Autowired VendaRepository vendaRepository;
  @Autowired ItemVendaRepository itemRepository;
  @Autowired ClienteRepository clienteRepository;
  @Autowired VendedorRepository vendedorRepository;
  @Autowired ProdutoRepository produtoRepository;
  Cliente cliente;
  Vendedor vendedor;
  Produto produto;

  @BeforeEach
  void preparar() {
    vendaRepository.deleteAll();
    clienteRepository.deleteAll();
    vendedorRepository.deleteAll();
    produtoRepository.deleteAll();
    cliente = clientes.salvar(null, "Ana Souza", "52998224725");
    vendedor = vendedores.salvar(null, "Bruno Alves", "45317828791");
    produto = produtos.salvar(null, "Cimento 50 kg", new BigDecimal("38.90"));
  }

  Venda vender() {
    return vendas.salvar(
        null,
        cliente.getId(),
        vendedor.getId(),
        List.of(new VendaService.ItemPedido(produto.getId(), 2)));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/",
        "/clientes",
        "/clientes/novo",
        "/vendedores",
        "/vendedores/novo",
        "/produtos",
        "/produtos/novo",
        "/vendas",
        "/vendas/nova"
      })
  void renderizaTelas(String rota) throws Exception {
    mvc.perform(get(rota))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("construa")));
  }

  @Test
  void renderizaEdicaoDetalheEBusca() throws Exception {
    var venda = vender();
    for (String rota :
        List.of(
            "/clientes/" + cliente.getId() + "/editar",
            "/vendedores/" + vendedor.getId() + "/editar",
            "/produtos/" + produto.getId() + "/editar",
            "/vendas/" + venda.getId(),
            "/vendas/" + venda.getId() + "/editar",
            "/clientes?busca=Ana",
            "/produtos?busca=inexistente")) mvc.perform(get(rota)).andExpect(status().isOk());
  }

  @Test
  void crudClienteComCpfPontuado() throws Exception {
    mvc.perform(
            post("/clientes")
                .with(csrf())
                .param("nome", "Carlos Lima")
                .param("cpf", "111.444.777-35"))
        .andExpect(redirectedUrl("/clientes"));
    var carlos = clientes.listar("Carlos").getFirst();
    assertThat(carlos.getCpf()).isEqualTo("11144477735");
    mvc.perform(
            post("/clientes/" + carlos.getId())
                .with(csrf())
                .param("nome", "Carlos Silva")
                .param("cpf", carlos.getCpf()))
        .andExpect(redirectedUrl("/clientes"));
    assertThat(clientes.buscar(carlos.getId()).getNome()).isEqualTo("Carlos Silva");
    mvc.perform(post("/clientes/" + carlos.getId() + "/excluir").with(csrf()))
        .andExpect(redirectedUrl("/clientes"));
    assertThat(clientes.listar("Carlos")).isEmpty();
  }

  @Test
  void crudVendedor() throws Exception {
    mvc.perform(
            post("/vendedores")
                .with(csrf())
                .param("nome", "Marina Costa")
                .param("cpf", "71460238001"))
        .andExpect(redirectedUrl("/vendedores"));
    var marina = vendedores.listar("Marina").getFirst();
    mvc.perform(
            post("/vendedores/" + marina.getId())
                .with(csrf())
                .param("nome", "Marina Silva")
                .param("cpf", marina.getCpf()))
        .andExpect(redirectedUrl("/vendedores"));
    assertThat(vendedores.buscar(marina.getId()).getNome()).isEqualTo("Marina Silva");
    mvc.perform(post("/vendedores/" + marina.getId() + "/excluir").with(csrf()))
        .andExpect(redirectedUrl("/vendedores"));
    assertThat(vendedores.listar("Marina")).isEmpty();
  }

  @Test
  void crudProduto() throws Exception {
    mvc.perform(post("/produtos").with(csrf()).param("nome", "Areia").param("preco", "6.50"))
        .andExpect(redirectedUrl("/produtos"));
    var areia = produtos.listar("Areia").getFirst();
    mvc.perform(
            post("/produtos/" + areia.getId())
                .with(csrf())
                .param("nome", "Areia média")
                .param("preco", "7.90"))
        .andExpect(redirectedUrl("/produtos"));
    assertThat(produtos.buscar(areia.getId()).getPreco()).isEqualByComparingTo("7.90");
    mvc.perform(post("/produtos/" + areia.getId() + "/excluir").with(csrf()))
        .andExpect(redirectedUrl("/produtos"));
    assertThat(produtos.listar("Areia")).isEmpty();
  }

  @Test
  void rejeitaCpfInvalidoDuplicadoENomeVazio() throws Exception {
    mvc.perform(post("/clientes").with(csrf()).param("nome", "Fulano").param("cpf", "11111111111"))
        .andExpect(model().attributeHasFieldErrors("form", "cpf"));
    mvc.perform(
            post("/clientes").with(csrf()).param("nome", "Outro").param("cpf", cliente.getCpf()))
        .andExpect(model().hasErrors());
    mvc.perform(post("/vendedores").with(csrf()).param("nome", " ").param("cpf", "71460238001"))
        .andExpect(model().attributeHasFieldErrors("form", "nome"));
    assertThat(clientes.listar("")).hasSize(1);
  }

  @ParameterizedTest
  @ValueSource(strings = {"-1", "0", "2.345", "abc", "10000000000"})
  void rejeitaPrecoInvalido(String preco) throws Exception {
    mvc.perform(post("/produtos").with(csrf()).param("nome", "Areia").param("preco", preco))
        .andExpect(status().isOk())
        .andExpect(model().attributeHasFieldErrors("form", "preco"));
    assertThat(produtos.listar("")).hasSize(1);
  }

  @Test
  void vendaAgrupaProdutosEPreservaPrecoNaEdicao() {
    var venda =
        vendas.salvar(
            null,
            cliente.getId(),
            vendedor.getId(),
            List.of(
                new VendaService.ItemPedido(produto.getId(), 2),
                new VendaService.ItemPedido(produto.getId(), 3)));
    assertThat(venda.getItens()).hasSize(1);
    assertThat(venda.getTotal()).isEqualByComparingTo("194.50");
    produtos.salvar(produto.getId(), produto.getNome(), new BigDecimal("100.00"));
    assertThat(vendas.buscar(venda.getId()).getTotal()).isEqualByComparingTo("194.50");
    venda =
        vendas.salvar(
            venda.getId(),
            cliente.getId(),
            vendedor.getId(),
            List.of(new VendaService.ItemPedido(produto.getId(), 4)));
    assertThat(venda.getTotal()).isEqualByComparingTo("155.60");
  }

  @Test
  void edicaoIncluiERemoveItensSemDeixarOrfaos() {
    var venda = vender();
    var areia = produtos.salvar(null, "Areia", new BigDecimal("6.50"));
    var editada =
        vendas.salvar(
            venda.getId(),
            cliente.getId(),
            vendedor.getId(),
            List.of(new VendaService.ItemPedido(areia.getId(), 3)));
    assertThat(editada.getTotal()).isEqualByComparingTo("19.50");
    assertThat(itemRepository.count()).isEqualTo(1);
    assertThat(vendas.buscar(venda.getId()).getItens().getFirst().getProduto().getId())
        .isEqualTo(areia.getId());
  }

  @Test
  void falhaNaVendaDesfazAlteracoesNaTransacao() {
    var venda = vender();
    assertThatThrownBy(
            () ->
                vendas.salvar(
                    venda.getId(),
                    cliente.getId(),
                    vendedor.getId(),
                    List.of(new VendaService.ItemPedido(999999L, 1))))
        .isInstanceOf(RegistroNaoEncontradoException.class);
    assertThat(vendas.buscar(venda.getId()).getTotal()).isEqualByComparingTo("77.80");
    assertThat(itemRepository.count()).isEqualTo(1);
    assertThatThrownBy(
            () ->
                vendas.salvar(
                    null,
                    cliente.getId(),
                    vendedor.getId(),
                    List.of(new VendaService.ItemPedido(999999L, 1))))
        .isInstanceOf(RegistroNaoEncontradoException.class);
    assertThat(vendas.listar()).hasSize(1);
  }

  @Test
  void bloqueiaExclusaoDeCadastrosEmUsoELiberaAposExcluirVenda() {
    var venda = vender();
    assertThatThrownBy(() -> clientes.excluir(cliente.getId()))
        .isInstanceOf(RegraNegocioException.class);
    assertThatThrownBy(() -> vendedores.excluir(vendedor.getId()))
        .isInstanceOf(RegraNegocioException.class);
    assertThatThrownBy(() -> produtos.excluir(produto.getId()))
        .isInstanceOf(RegraNegocioException.class);
    vendas.excluir(venda.getId());
    assertThat(itemRepository.count()).isZero();
    clientes.excluir(cliente.getId());
    vendedores.excluir(vendedor.getId());
    produtos.excluir(produto.getId());
  }

  @Test
  void crudVendaViaFormulario() throws Exception {
    mvc.perform(
            post("/vendas")
                .with(csrf())
                .param("clienteId", cliente.getId().toString())
                .param("vendedorId", vendedor.getId().toString())
                .param("itens[0].produtoId", produto.getId().toString())
                .param("itens[0].quantidade", "2"))
        .andExpect(status().is3xxRedirection());
    var venda = vendas.listar().getFirst();
    mvc.perform(
            post("/vendas/" + venda.getId())
                .with(csrf())
                .param("clienteId", cliente.getId().toString())
                .param("vendedorId", vendedor.getId().toString())
                .param("itens[0].produtoId", produto.getId().toString())
                .param("itens[0].quantidade", "3"))
        .andExpect(redirectedUrl("/vendas/" + venda.getId()));
    assertThat(vendas.buscar(venda.getId()).getTotal()).isEqualByComparingTo("116.70");
    mvc.perform(post("/vendas/" + venda.getId() + "/excluir").with(csrf()))
        .andExpect(redirectedUrl("/vendas"));
    assertThat(vendas.listar()).isEmpty();
  }

  @Test
  void rejeitaVendaVaziaOuQuantidadeInvalida() throws Exception {
    mvc.perform(post("/vendas").with(csrf())).andExpect(model().hasErrors());
    mvc.perform(
            post("/vendas")
                .with(csrf())
                .param("clienteId", cliente.getId().toString())
                .param("vendedorId", vendedor.getId().toString())
                .param("itens[0].produtoId", produto.getId().toString())
                .param("itens[0].quantidade", "0"))
        .andExpect(model().hasErrors());
    assertThat(vendas.listar()).isEmpty();
  }

  @Test
  void exigeCsrfENaoExcluiPorGet() throws Exception {
    mvc.perform(post("/clientes").param("nome", "Carlos").param("cpf", "11144477735"))
        .andExpect(status().isForbidden());
    mvc.perform(get("/clientes/" + cliente.getId() + "/excluir"))
        .andExpect(status().isMethodNotAllowed());
    assertThat(clientes.listar("")).hasSize(1);
  }

  @Test
  void retorna404ParaRegistroInexistente() throws Exception {
    mvc.perform(get("/clientes/999999/editar")).andExpect(status().isNotFound());
    mvc.perform(get("/vendas/999999")).andExpect(status().isNotFound());
  }

  @Test
  void escapaHtmlDosCadastros() throws Exception {
    clientes.salvar(null, "<script>alert(1)</script>", "11144477735");
    mvc.perform(get("/clientes"))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("&lt;script&gt;")))
        .andExpect(
            content()
                .string(
                    org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("<script>alert(1)</script>"))));
  }
}
