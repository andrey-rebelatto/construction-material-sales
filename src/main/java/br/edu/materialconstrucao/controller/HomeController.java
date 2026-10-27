package br.edu.materialconstrucao.controller;

import br.edu.materialconstrucao.model.entity.Venda;
import br.edu.materialconstrucao.model.service.*;
import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
  private final VendaService vendas;
  private final ClienteService clientes;
  private final ProdutoService produtos;
  private final VendedorService vendedores;

  public HomeController(
      VendaService vendas,
      ClienteService clientes,
      ProdutoService produtos,
      VendedorService vendedores) {
    this.vendas = vendas;
    this.clientes = clientes;
    this.produtos = produtos;
    this.vendedores = vendedores;
  }

  @GetMapping("/")
  public String index(Model model) {
    var lista = vendas.listar();
    model.addAttribute("titulo", "Visão geral");
    model.addAttribute("secao", "inicio");
    model.addAttribute(
        "total", lista.stream().map(Venda::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
    model.addAttribute("quantidadeVendas", lista.size());
    model.addAttribute("quantidadeClientes", clientes.listar("").size());
    model.addAttribute("quantidadeProdutos", produtos.listar("").size());
    model.addAttribute("quantidadeVendedores", vendedores.listar("").size());
    model.addAttribute("vendas", lista.stream().limit(5).toList());
    return "home";
  }
}
