<div align="center">

# 🎓 StudyFlow
### *Seu espaço de organização e aprendizado.*

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4.svg?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Android SDK](https://img.shields.io/badge/Android%20SDK-24%2B%20%7C%2037-3DDC84.svg?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![SQLite](https://img.shields.io/badge/Database-SQLite-003B57.svg?style=for-the-badge&logo=sqlite&logoColor=white)](https://www.sqlite.org/)
[![DataStore](https://img.shields.io/badge/Storage-Jetpack%20DataStore-4285F4.svg?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/topic/libraries/architecture/datastore)
[![Retrofit](https://img.shields.io/badge/API-Google%20Books%20REST-FF6F00.svg?style=for-the-badge&logo=google&logoColor=white)](https://developers.google.com/books)

---

</div>

## 📌 Sobre o Projeto

O **StudyFlow** é um aplicativo Android moderno e intuitivo desenvolvido em **Kotlin** e **Jetpack Compose** para ajudar estudantes a organizarem sua rotina acadêmica. Com ele, é possível cadastrar disciplinas, controlar tarefas e trabalhos com prioridades, agendar exames, acompanhar o progresso das metas diárias de estudos e pesquisar livros recomendados consumindo a **API REST do Google Books**.

---

## 🎨 Identidade Visual & Paleta de Cores

A interface do **StudyFlow** foi projetada com base em uma paleta elegante e acolhedora em tons de Roxo, Lilás, Lavanda e Grafite, combinando cartões em tons pastel para destaques visuais:

| Tom | Hexadecimal | Elemento Visual |
| :--- | :---: | :--- |
| 🟣 **Roxo (Primary)** | `#7357D9` | Botões de ação, títulos principais, indicadores e navegação ativa |
| 💜 **Lilás (Secondary)** | `#B8A8F5` | Badges, realces e elemento primário no Modo Escuro |
| 🪻 **Lavanda (Background)** | `#F5F2FF` | Fundo principal da interface no Modo Claro |
| 🌑 **Grafite (Dark Surface)** | `#25233A` | Textos principais no Modo Claro e fundo das superfícies no Modo Escuro |
| 🍑 **Soft Peach** | `#FFF0ED` | Card de estatística de Meta Diária de Estudos |
| 💜 **Soft Purple** | `#EFE9FF` | Card de estatística de Tarefas Concluídas |
| 🌿 **Soft Mint** | `#E6F7F5` | Card de estatística de Provas e Avaliações |

---

## ✨ Funcionalidades Principais

- 🏠 **Dashboard de Estudos**:
  - Saudação personalizada ao estudante.
  - Indicador de progresso da **Meta Diária de Estudos** (calculada dinamicamente).
  - Resumo de estatísticas (Matérias, Tarefas Pendentes, Provas Agendadas).
  - Atalhos de ação rápida para cadastros instantâneos.
  - Lista de próximos prazos de entrega e exames.

- 📚 **Gestão de Matérias**:
  - Cadastro, edição e exclusão de disciplinas com código/sigla, nome do professor e seleção de cor.
  - **Pesquisa de Livros Recomendados (API REST Google Books)**: Busca livros e bibliografias com capas, autores, resumos e links diretos para leitura.

- ✅ **Tarefas e Trabalhos**:
  - Organização de entregas por matéria.
  - Classificação por níveis de prioridade (**Baixa**, **Média**, **Alta**).
  - Filtros dinâmicos (*Todas*, *Pendentes*, *Concluídas*).
  - Checkbox interativo para marcar status de conclusão.

- 📅 **Provas e Avaliações**:
  - Agendamento de exames com data, horário, sala/local e observações dos conteúdos cobrados.

- ⚙️ **Configurações e Preferências**:
  - Edição do nome do estudante.
  - Ajuste da meta diária de estudos (1h a 12h).
  - Alternância entre **Modo Claro** e **Modo Escuro (Dark Mode)**.
  - Ativação/desativação de lembretes.
  - Botão de teste de **Notificação Local Instantânea**.

---

## 🛠️ Requisitos Técnicos Obrigatórios Atendidos

1. 🧩 **Navegação entre Telas (`navigation-compose`)**:
   - `NavHost` integrado à `Scaffold` com `NavigationBar` (Bottom Navigation) em 5 telas principais (*Dashboard*, *Matérias*, *Tarefas*, *Provas*, *Ajustes*).

2. 💾 **Armazenamento de Preferências (Jetpack DataStore)**:
   - Implementado em `UserPreferencesRepository` com `preferencesDataStore` para persistir o nome do aluno, horas da meta diária, tema escuro e notificações.

3. 🗄️ **Persistência de Dados Locais (SQLite)**:
   - Estruturado em `StudyFlowDatabaseHelper` (`SQLiteOpenHelper`) e `StudyFlowRepository` com suporte a 3 tabelas com relacionamentos e chaves estrangeiras:
     - `materias` (id, nome, codigo, professor, cor)
     - `tarefas` (id, materia_id, titulo, descricao, data_entrega, concluida, prioridade)
     - `provas` (id, materia_id, titulo, data_prova, local, nota, nota_maxima, observacoes)

4. 🌐 **Consumo de API REST (Google Books API)**:
   - Serviço consumido via `Retrofit` e `Gson` (`https://www.googleapis.com/books/v1/volumes`).
   - Configurado com `OkHttpClient` e cabeçalhos customizados.
   - Tratamento específico de limites de cota HTTP 429 e fallback offline com sugestões acadêmicas recomendadas.

5. 🔔 **Notificações Locais e Agendamento**:
   - Sistema de alertas em `NotificationHelper` e `NotificationReceiver` utilizando `AlarmManager`, `NotificationChannel` e `NotificationCompat`.
   - Lembretes automáticos para prazos de tarefas e provas, com suporte à permissão `POST_NOTIFICATIONS` no Android 13+.

---

## 🏛️ Arquitetura do Projeto

```
br.gov.sp.cps.studyflow
 ├── data
 │   ├── api
 │   │   ├── GoogleBooksApi.kt          # Interface Retrofit e cliente OkHttp
 │   ├── datastore
 │   │   └── UserPreferencesRepository.kt # Repositório Jetpack DataStore
 │   └── db
 │       ├── StudyFlowDatabaseHelper.kt # SQLiteOpenHelper (Tabelas e DDL)
 │       └── StudyFlowRepository.kt     # Repositório CRUD SQLite
 ├── model
 │   └── Models.kt                     # Data Classes (Subject, TaskItem, ExamItem, BookModel, UserPreferences)
 ├── notifications
 │   ├── NotificationHelper.kt         # Gerenciamento de Canais e Alertas
 │   └── NotificationReceiver.kt       # BroadcastReceiver para AlarmManager
 ├── ui
 │   ├── components
 │   │   └── Dialogs.kt                # Diálogos de cadastro e seletores
 │   ├── navigation
 │   │   ├── BottomNavBar.kt           # NavigationBar do Jetpack Compose
 │   │   └── Screen.kt                 # Rotas da navegação
 │   ├── screens
 │   │   ├── DashboardScreen.kt        # Tela Inicial / Visão Geral
 │   │   ├── ExamsScreen.kt            # Tela de Provas e Avaliações
 │   │   ├── SettingsScreen.kt         # Tela de Ajustes e DataStore
 │   │   ├── SubjectsScreen.kt         # Tela de Matérias e API REST
 │   │   └── TasksScreen.kt            # Tela de Tarefas e Trabalhos
 │   └── theme
 │       ├── Color.kt                  # Paleta de Cores (Roxo, Lilás, Lavanda, Grafite)
 │       ├── Theme.kt                  # Tema Material3 Light e Dark
 │       └── Type.kt                   # Tipografia do aplicativo
 ├── viewmodel
 │   └── StudyFlowViewModel.kt         # ViewModel centralizado com StateFlows
 └── MainActivity.kt                   # Activity principal e NavHost
```

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
- **Android Studio** (versão Ladybug 2024.2+ ou superior)
- **JDK 11** ou superior
- Dispositivo Android físico ou Emulador (Android API 24+)

### Passos
1. Clone o repositório:
   ```bash
   git clone https://github.com/Priscilamendes18/StudyFlow.git
   ```
2. Abra o projeto no **Android Studio**.
3. Aguarde a sincronização do **Gradle**.
4. Selecione o dispositivo/emulador e clique no botão **Run (Shift + F10)**.

---

<div align="center">

Desenvolvido com 💜 em **Kotlin** & **Jetpack Compose** para facilitação do aprendizado acadêmico.

</div>
