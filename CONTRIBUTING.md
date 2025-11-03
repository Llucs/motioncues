# Guia de Contribuição - MotionCues

Obrigado por seu interesse em contribuir para o **MotionCues**! Este documento fornece diretrizes para contribuições ao projeto.

## 📋 Código de Conduta

Esperamos que todos os contribuidores sigam nosso código de conduta, que promove um ambiente respeitoso e inclusivo para todos.

## 🚀 Como Contribuir

### Reportar Bugs

Ao relatar um bug, inclua:

*   Uma descrição clara e concisa do problema
*   Passos para reproduzir o problema
*   Comportamento esperado vs. comportamento atual
*   Informações do dispositivo (modelo, versão do Android)
*   Logs ou screenshots, se aplicável

### Sugerir Melhorias

Para sugerir uma melhoria:

*   Descreva a melhoria de forma clara
*   Explique por que seria útil
*   Liste exemplos de como outras aplicações implementam funcionalidades similares

### Submeter Pull Requests

1. **Fork o repositório** e crie uma branch para sua feature:
   ```bash
   git checkout -b feature/sua-feature
   ```

2. **Faça suas mudanças** seguindo o estilo de código do projeto:
   - Use Kotlin para código Android
   - Siga as convenções de nomenclatura do Kotlin
   - Adicione comentários para código complexo

3. **Teste suas mudanças**:
   ```bash
   ./gradlew build
   ./gradlew test
   ./gradlew lint
   ```

4. **Commit suas mudanças** com mensagens descritivas:
   ```bash
   git commit -m "Adiciona feature X que faz Y"
   ```

5. **Push para sua branch**:
   ```bash
   git push origin feature/sua-feature
   ```

6. **Abra um Pull Request** com:
   - Título descritivo
   - Descrição detalhada das mudanças
   - Referência a issues relacionadas (se houver)

## 🎨 Estilo de Código

*   **Kotlin:** Siga as [convenções oficiais do Kotlin](https://kotlinlang.org/docs/coding-conventions.html)
*   **Nomes:** Use nomes descritivos e em inglês
*   **Formatação:** Use a formatação padrão do Android Studio
*   **Comentários:** Adicione comentários para código não óbvio

## 🧪 Testes

Todas as contribuições devem incluir testes apropriados:

*   Testes unitários para lógica de negócio
*   Testes de integração para componentes Android
*   Testes de UI para Composables

Execute os testes antes de submeter:

```bash
./gradlew test
```

## 📝 Documentação

*   Atualize o README.md se suas mudanças afetarem o uso do aplicativo
*   Adicione comentários KDoc para funções públicas
*   Documente APIs complexas

## 🔄 Processo de Review

Após submeter um Pull Request:

1. O código será revisado por mantenedores
2. Feedback será fornecido se necessário
3. Após aprovação, o PR será mergeado

## 📦 Versioning

Este projeto segue [Semantic Versioning](https://semver.org/):

*   **MAJOR:** Mudanças incompatíveis na API
*   **MINOR:** Novas funcionalidades compatíveis
*   **PATCH:** Correções de bugs

## 🏗️ Estrutura de Branches

*   `main` - Versão estável de produção
*   `develop` - Versão de desenvolvimento
*   `feature/*` - Novas features
*   `bugfix/*` - Correções de bugs
*   `hotfix/*` - Correções urgentes

## 📞 Dúvidas?

Se tiver dúvidas sobre como contribuir, abra uma issue ou entre em contato através do repositório.

Obrigado por contribuir para o **MotionCues**! 🎉
