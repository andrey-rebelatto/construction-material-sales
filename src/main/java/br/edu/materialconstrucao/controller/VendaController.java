package br.edu.materialconstrucao.controller;

import br.edu.materialconstrucao.controller.form.VendaForm;
import br.edu.materialconstrucao.model.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vendas")
public class VendaController {
  private final VendaService service;
  private final ClienteService clientes;
  private final VendedorService vendedores;
  private final ProdutoService produtos;

  public VendaController(
      VendaService service,
      ClienteService clientes,
      VendedorService vendedores,
      ProdutoService produtos) {
    this.service = service;
    this.clientes = clientes;
    this.vendedores = vendedores;
    this.produtos = produtos;
  }

  @ModelAttribute
  void contexto(Model model) {
    model.addAttribute("secao", "vendas");
    model.addAttribute("titulo", "Vendas");
  }

  @GetMapping
  public String listar(Model model) {
    model.addAttribute("vendas", service.listar());
    return "vendas/lista";
  }

  @GetMapping("/{id}")
  public String detalhe(@PathVariable Long id, Model model) {
    model.addAttribute("venda", service.buscar(id));
    return "vendas/detalhe";
  }

  private String formulario(Model model) {
    var form = (VendaForm) model.getAttribute("form");
    if (form.getItens().isEmpty()) form.getItens().add(new VendaForm.ItemForm());
    model.addAttribute("clientes", clientes.listar(""));
    model.addAttribute("vendedores", vendedores.listar(""));
    model.addAttribute("produtos", produtos.listar(""));
    return "vendas/form";
  }

  @GetMapping("/nova")
  public String nova(Model model) {
    var form = new VendaForm();
    form.getItens().add(new VendaForm.ItemForm());
    model.addAttribute("form", form);
    return formulario(model);
  }

  @GetMapping("/{id}/editar")
  public String editar(@PathVariable Long id, Model model) {
    var venda = service.buscar(id);
    var form = new VendaForm();
    form.setClienteId(venda.getCliente().getId());
    form.setVendedorId(venda.getVendedor().getId());
    venda
        .getItens()
        .forEach(
            item -> {
              var linha = new VendaForm.ItemForm();
              linha.setProdutoId(item.getProduto().getId());
              linha.setQuantidade(item.getQuantidade());
              form.getItens().add(linha);
            });
    model.addAttribute("form", form);
    model.addAttribute("id", id);
    model.addAttribute(
        "precosHistoricos",
        venda.getItens().stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    i -> i.getProduto().getId(), i -> i.getPrecoUnitario())));
    return formulario(model);
  }

  @PostMapping
  public String criar(
      @Valid @ModelAttribute("form") VendaForm form,
      BindingResult result,
      Model model,
      RedirectAttributes redirect) {
    return salvar(null, form, result, model, redirect);
  }

  @PostMapping("/{id}")
  public String atualizar(
      @PathVariable Long id,
      @Valid @ModelAttribute("form") VendaForm form,
      BindingResult result,
      Model model,
      RedirectAttributes redirect) {
    service.buscar(id);
    return salvar(id, form, result, model, redirect);
  }

  private String salvar(
      Long id, VendaForm form, BindingResult result, Model model, RedirectAttributes redirect) {
    model.addAttribute("id", id);
    if (!result.hasErrors()) {
      try {
        var pedidos =
            form.getItens().stream()
                .map(i -> new VendaService.ItemPedido(i.getProdutoId(), i.getQuantidade()))
                .toList();
        var venda = service.salvar(id, form.getClienteId(), form.getVendedorId(), pedidos);
        redirect.addFlashAttribute("sucesso", "Venda salva com sucesso.");
        return "redirect:/vendas/" + venda.getId();
      } catch (RegistroNaoEncontradoException ex) {
        result.reject(
            "referencia", "Um dos cadastros selecionados não existe mais. Selecione novamente.");
      } catch (RegraNegocioException ex) {
        result.reject("regra", ex.getMessage());
      }
    }
    if (id != null)
      model.addAttribute(
          "precosHistoricos",
          service.buscar(id).getItens().stream()
              .collect(
                  java.util.stream.Collectors.toMap(
                      i -> i.getProduto().getId(), i -> i.getPrecoUnitario())));
    return formulario(model);
  }

  @PostMapping("/{id}/excluir")
  public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
    service.excluir(id);
    redirect.addFlashAttribute("sucesso", "Venda e itens excluídos.");
    return "redirect:/vendas";
  }
}
