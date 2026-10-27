# Vendas de materiais de construção

Sistema acadêmico com cadastro, edição, exclusão e consulta de clientes, vendedores, produtos e vendas.

Java 21, Spring MVC, Thymeleaf e Spring Data JPA. Arquitetura MVC, com controllers acessando os services.

## Executar

```bash
./mvnw spring-boot:run
```

Acesse http://localhost:8080. O banco H2 fica em `data/` e recebe dados de exemplo na primeira execução.

Para rodar os testes: `./mvnw verify`.

## Protótipos

[Abrir protótipos de tela](https://andrey-rebelatto.github.io/construction-material-sales/prototipos/). São demonstrações estáticas; o CRUD funciona na aplicação local.

## Diagramas

<details>
<summary>Diagrama de classes</summary>

```mermaid
classDiagram
    direction LR
    class Cliente {
        -Long id
        -String nome
        -String cpf
    }
    class Vendedor {
        -Long id
        -String nome
        -String cpf
    }
    class Produto {
        -Long id
        -String nome
        -BigDecimal preco
    }
    class Venda {
        -Long id
        -LocalDateTime dataHora
        -Cliente cliente
        -Vendedor vendedor
        -List~ItemVenda~ itens
        +getTotal() BigDecimal
        +adicionarItem(ItemVenda) void
    }
    class ItemVenda {
        -Long id
        -Venda venda
        -Produto produto
        -int quantidade
        -BigDecimal precoUnitario
        +getSubtotal() BigDecimal
    }
    Cliente "1" <-- "0..*" Venda : cliente
    Vendedor "1" <-- "0..*" Venda : responsável
    Venda "1" *-- "1..*" ItemVenda : contém
    Produto "1" <-- "0..*" ItemVenda : material
```

</details>

<details>
<summary>Diagrama MER</summary>

```mermaid
erDiagram
    CLIENTES ||--o{ VENDAS : realiza
    VENDEDORES ||--o{ VENDAS : registra
    VENDAS ||--|{ ITENS_VENDA : contem
    PRODUTOS ||--o{ ITENS_VENDA : participa
    CLIENTES {
        bigint id PK
        varchar_120 nome
        varchar_11 cpf UK
    }
    VENDEDORES {
        bigint id PK
        varchar_120 nome
        varchar_11 cpf UK
    }
    PRODUTOS {
        bigint id PK
        varchar_120 nome
        numeric_12_2 preco
    }
    VENDAS {
        bigint id PK
        timestamp data_hora
        bigint cliente_id FK
        bigint vendedor_id FK
    }
    ITENS_VENDA {
        bigint id PK
        bigint venda_id FK
        bigint produto_id FK
        integer quantidade
        numeric_12_2 preco_unitario
    }
```

</details>
