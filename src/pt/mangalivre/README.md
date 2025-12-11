# MangaLivre Extension

Extensão Mihon/Tachiyomi para o site **MangaLivre.blog**

## Recursos

- Busca de mangás
- Listagem de mangás populares
- Últimas atualizações
- Leitura de capítulos
- Suporte a português (pt-BR)

## Estrutura

```
src/pt/mangalivre/
├── src/main/kotlin/eu/kanade/tachiyomi/extension/pt/mangalivre/
│   └── MangaLivre.kt
├── build.gradle.kts
└── README.md
```

## Instalação

1. Clone este repositório
2. Compile com Gradle: `gradle build`
3. Copie o `.jar` gerado para a pasta de extensões do Mihon

## Status

- [x] Estrutura básica
- [x] Seletores CSS configurados
- [ ] Testes de integração
- [ ] Otimizações de performance
