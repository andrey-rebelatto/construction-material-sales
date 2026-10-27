"use strict";
document.querySelectorAll("form[data-confirm]").forEach((form) => {
  form.addEventListener("submit", (event) => {
    if (!window.confirm(form.dataset.confirm)) event.preventDefault();
  });
});
document.querySelectorAll("[data-cpf]").forEach((input) => {
  input.addEventListener("input", () => {
    const value = input.value.replace(/\D/g, "").slice(0, 11);
    input.value = value
      .replace(/^(\d{3})(\d)/, "$1.$2")
      .replace(/^(\d{3}\.\d{3})(\d)/, "$1.$2")
      .replace(/(\d{3}\.\d{3}\.\d{3})(\d)/, "$1-$2");
  });
});
const cart = document.querySelector("#cart-items");
if (cart) {
  const money = new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  });
  const add = document.querySelector("#add-item");
  const template = cart.querySelector(".item-row")?.cloneNode(true);
  const update = () => {
    let total = 0;
    const rows = [...cart.querySelectorAll(".item-row")];
    rows.forEach((row, index) => {
      const product = row.querySelector("select");
      const quantity = row.querySelector("input");
      product.name = `itens[${index}].produtoId`;
      product.id = `itens${index}.produtoId`;
      quantity.name = `itens[${index}].quantidade`;
      quantity.id = `itens${index}.quantidade`;
      const cents = Math.round(
        Number(product.selectedOptions[0]?.dataset.price || 0) * 100,
      );
      const subtotal = cents * Number(quantity.value || 0);
      row.querySelector(".line-total").value = money.format(subtotal / 100);
      row.querySelector(".remove-item").disabled = rows.length === 1;
      total += subtotal;
    });
    document.querySelector("#cart-total").textContent = money.format(
      total / 100,
    );
    add.disabled = rows.length >= 100;
  };
  cart.addEventListener("input", update);
  cart.addEventListener("change", update);
  cart.addEventListener("click", (event) => {
    const remove = event.target.closest(".remove-item");
    if (remove && cart.children.length > 1) {
      remove.closest(".item-row").remove();
      update();
    }
  });
  add.addEventListener("click", () => {
    if (!template || cart.children.length >= 100) return;
    const row = template.cloneNode(true);
    row.querySelector("select").value = "";
    row.querySelector("input").value = "1";
    cart.append(row);
    update();
    row.querySelector("select").focus();
  });
  update();
}
