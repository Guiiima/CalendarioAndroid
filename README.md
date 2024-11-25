# Gerenciador de Eventos

Um aplicativo simples e funcional para gerenciar eventos e categorias, desenvolvido com Kotlin, Room e Jetpack Compose.

---

## Funcionalidades

- **Gerenciamento de Categorias**:
  - Criar, listar e excluir categorias com cores personalizadas.
- **Gerenciamento de Eventos**:
  - Criar, editar, excluir e pesquisar eventos.
  - Destaque para eventos do dia atual.
- **Interface Moderna**:
  - Design reativo utilizando Jetpack Compose.

---

## Estrutura do Projeto

- **Banco de Dados Local**:
  - Configurado com Room.
  - Entidades: `Evento` e `Categoria`.

- **Arquitetura MVVM**:
  - Separação clara entre camadas de interface, lógica de negócios e persistência de dados.

- **Interface do Usuário**:
  - Componentes declarativos com Compose.
  - Telas:
    - Cadastro de Categorias.
    - Cadastro e Pesquisa de Eventos.
    - Lista de Eventos.

---

## Tecnologias e Ferramentas

- **Kotlin**
- **Jetpack Compose**
- **Room Database**
- **ViewModel**
- **Coroutines**

---

## Como Rodar

1. Clone este repositório:
   ```bash
   git clone <link-do-repositorio>
