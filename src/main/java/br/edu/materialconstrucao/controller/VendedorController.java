package br.edu.materialconstrucao.controller;

import br.edu.materialconstrucao.controller.form.PessoaForm;
import br.edu.materialconstrucao.model.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vendedores")
public class VendedorController {
  private final VendedorService service;

  public VendedorController(VendedorService service) {
    this.service = service;
  }

  @ModelAttribute
  void contexto(Model model) {
    model.addAttribute("secao", "vendedores");
    model.addAttribute("titulo", "Vendedores");
    model.addAttribute("singular", "vendedor");
    model.addAttribute("tipo", "cpf");
  }

  @GetMapping
  public String listar(@RequestParam(defaultValue = "") String busca, Model model) {
    model.addAttribute("registros", service.listar(busca));
    model.addAttribute("busca", busca);
    return "cadastros/lista";
  }

  @GetMapping("/novo")
  public String novo(Model model) {
    model.addAttribute("form", new PessoaForm());
    return "cadastros/form";
  }

  @GetMapping("/{id}/editar")
  public String editar(@PathVariable Long id, Model model) {
    var registro = service.buscar(id);
    var form = new PessoaForm();
    form.setNome(registro.getNome());
    form.setCpf(registro.getCpf());
    model.addAttribute("form", form);
    model.addAttribute("id", id);
    return "cadastros/form";
  }

  @PostMapping
  public String criar(
      @Valid @ModelAttribute("form") PessoaForm form,
      BindingResult result,
      Model model,
      RedirectAttributes redirect) {
    return salvar(null, form, result, model, redirect);
  }

  @PostMapping("/{id}")
  public String atualizar(
      @PathVariable Long id,
      @Valid @ModelAttribute("form") PessoaForm form,
      BindingResult result,
      Model model,
      RedirectAttributes redirect) {
    service.buscar(id);
    return salvar(id, form, result, model, redirect);
  }

  private String salvar(
      Long id, PessoaForm form, BindingResult result, Model model, RedirectAttributes redirect) {
    model.addAttribute("id", id);
    if (!result.hasErrors()) {
      try {
        service.salvar(id, form.getNome(), form.getCpf());
        redirect.addFlashAttribute("sucesso", "Cadastro salvo com sucesso.");
        return "redirect:/vendedores";
      } catch (RegraNegocioException ex) {
        result.reject("regra", ex.getMessage());
      } catch (org.springframework.dao.DataIntegrityViolationException ex) {
        result.reject(
            "integridade", "Não foi possível salvar. Verifique se o CPF já está cadastrado.");
      }
    }
    return "cadastros/form";
  }

  @PostMapping("/{id}/excluir")
  public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
    try {
      service.excluir(id);
      redirect.addFlashAttribute("sucesso", "Cadastro excluído.");
    } catch (RegraNegocioException ex) {
      redirect.addFlashAttribute("erro", ex.getMessage());
    } catch (org.springframework.dao.DataIntegrityViolationException ex) {
      redirect.addFlashAttribute(
          "erro", "Este cadastro está vinculado a uma venda e não pode ser excluído.");
    }
    return "redirect:/vendedores";
  }
}
