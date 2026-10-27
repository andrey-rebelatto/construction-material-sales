# Roteiro de apresentação

1. Abra o README e apresente o tema, o diagrama de classes e o MER. Mostre as cinco tabelas e explique as relações entre venda, item e produto.
2. Abra o link dos protótipos. Navegue pela página inicial, pelas listagens e pela tela de nova venda. Explique que essas páginas demonstram o fluxo visual e não salvam dados.
3. Execute `./mvnw spring-boot:run` e abra `http://localhost:8080`. Mostre a página inicial com dados de exemplo.
4. Cadastre um cliente com nome e CPF válido. Edite o nome, busque por ele e exclua o cadastro. Tente um CPF inválido para mostrar a validação. Use um CPF de teste diferente dos dados iniciais, como `123.456.789-09`.
5. Cadastre um vendedor e um produto. Edite e exclua um registro sem vínculo para demonstrar os demais CRUDs.
6. Crie uma venda para Ana Souza, com Bruno Alves como vendedor. Adicione 2 sacos de cimento a R$ 38,90 e 3 sacos de areia a R$ 6,50. O total deve ser R$ 97,30. Adicione uma segunda linha do mesmo produto e mostre o agrupamento após salvar.
7. Abra os detalhes e edite a venda, alterando a quantidade e removendo ou incluindo um material.
8. Altere o preço de um produto no catálogo. Volte à venda e confira que o preço histórico continua igual.
9. Tente excluir Ana Souza ou um produto usado em venda. Mostre a mensagem de bloqueio. Exclua uma venda para mostrar a remoção dos seus itens.
10. Abra `controller/VendaController.java`, `model/service/VendaService.java` e `model/repository/VendaRepository.java`. Explique o caminho Controller → Service → Repository e a transação.
11. Execute `./mvnw verify` e mostre os testes de arquitetura, integração e regras de negócio.

Os dados de exemplo são incluídos somente no primeiro início com os cadastros vazios. Para repetir a demonstração desde o começo, pare a aplicação e faça um backup da pasta `data/` antes de usar um banco novo.
