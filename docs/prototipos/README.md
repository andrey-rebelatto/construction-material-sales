# Protótipos de tela

[Abrir a versão navegável](https://andrey-rebelatto.github.io/construction-material-sales/prototipos/).

Para visualizar localmente, abra `index.html` ou execute `python3 -m http.server 8000 --directory docs` na raiz do projeto e acesse `http://localhost:8000/prototipos/`.

As 16 telas HTML demonstram início, listagens, cadastro, edição e detalhes. Usam os mesmos estilos e componentes visuais da aplicação Thymeleaf. Há busca local nas listagens e simulação do carrinho, com inclusão, remoção e cálculo do total. Os dados são fictícios e nenhuma ação grava informações no banco.

As imagens em `imagens/` documentam as principais telas em desktop e celular. Os diagramas estão em `../diagramas/` e no README principal.

Para atualizar os HTMLs, execute a aplicação com os dados iniciais de demonstração e rode `python3 scripts/exportar-prototipos.py`. Os dados de exemplo devem manter os IDs iniciais. O script remove os tokens CSRF e troca as rotas por links locais. Ele copia CSS/JS da aplicação e mantém `assets/prototipo.js`, responsável por impedir o envio dos formulários estáticos.
