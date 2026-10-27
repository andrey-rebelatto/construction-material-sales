package br.edu.materialconstrucao;

import static org.assertj.core.api.Assertions.assertThat;

import br.edu.materialconstrucao.model.service.*;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.*;
import java.util.List;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.datasource.url=jdbc:h2:mem:http-renderizacao;DB_CLOSE_DELAY=-1")
class HttpRenderizacaoTest {
  @LocalServerPort int port;
  @Autowired ClienteService clientes;
  @Autowired VendedorService vendedores;
  @Autowired ProdutoService produtos;

  @BeforeEach
  void preparar() {
    if (clientes.listar("").isEmpty()) clientes.salvar(null, "Ana Souza", "52998224725");
    if (vendedores.listar("").isEmpty()) vendedores.salvar(null, "Bruno Alves", "45317828791");
    if (produtos.listar("").isEmpty()) produtos.salvar(null, "Cimento", new BigDecimal("38.90"));
  }

  HttpResponse<String> abrir(String rota) throws Exception {
    // Cada chamada começa sem cookies, como um acesso direto em uma sessão nova.
    try (var client = HttpClient.newHttpClient()) {
      return client.send(
          HttpRequest.newBuilder(URI.create("http://localhost:" + port + rota))
              .header("Accept", "text/html")
              .build(),
          HttpResponse.BodyHandlers.ofString());
    }
  }

  @Test
  void renderizaFormulariosComCsrfEmNovaSessao() throws Exception {
    for (String rota :
        List.of("/clientes", "/vendedores", "/produtos", "/vendas/nova", "/clientes/novo", "/")) {
      var resposta = abrir(rota);
      assertThat(resposta.statusCode()).as(rota).isEqualTo(200);
      assertThat(resposta.body()).as(rota).contains("</html>").doesNotContain("Exception");
      assertThat(resposta.headers().firstValue("Set-Cookie")).as(rota).isPresent();
      if (!rota.equals("/")) assertThat(resposta.body()).contains("name=\"_csrf\"");
    }
  }

  @Test
  void renderizaPaginaHtmlDeErro404() throws Exception {
    for (String rota : List.of("/nao-existe", "/clientes/999999/editar")) {
      var resposta = abrir(rota);
      assertThat(resposta.statusCode()).isEqualTo(404);
      assertThat(resposta.body()).contains("Página não encontrada", "Ir para o início", "</html>");
    }
  }
}
