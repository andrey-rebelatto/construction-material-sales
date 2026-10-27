"use strict";
document.querySelectorAll("form").forEach((form) => {
  form.addEventListener("submit", (event) => {
    if (event.defaultPrevented) return;
    event.preventDefault();
    const search = form.querySelector('input[name="busca"]');
    if (search) {
      const query = search.value.trim().toLocaleLowerCase("pt-BR");
      document.querySelectorAll("tbody tr").forEach((row) => {
        row.hidden = !row.textContent
          .toLocaleLowerCase("pt-BR")
          .includes(query);
      });
      return;
    }
    document.querySelector(".prototype-toast")?.remove();
    const toast = document.createElement("div");
    toast.className = "prototype-toast";
    toast.setAttribute("role", "status");
    toast.textContent =
      "Demonstração visual. Execute o Spring Boot para salvar ou excluir dados.";
    document.body.append(toast);
    setTimeout(() => toast.remove(), 4500);
  });
});
