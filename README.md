# MotionCues - Vehicle Motion Visualizer

**MotionCues** é um aplicativo Android open-source projetado para reduzir o enjoo de movimento (cinetose) em veículos, fornecendo um efeito visual de "bolinhas que se movem" que ajuda o usuário a sincronizar a percepção visual com o movimento real. 

Este projeto segue as diretrizes de design **Material 3** para uma experiência de usuário moderna e fluida.

## 🌟 Funcionalidades Principais

*   **Detecção de Movimento de Veículo:** Utiliza sensores (acelerômetro, giroscópio) e GPS para inferir automaticamente quando o usuário está em um veículo em movimento.
*   **Serviço em Primeiro Plano:** Roda em segundo plano com uma notificação persistente para monitorar sensores e permitir o controle rápido do efeito visual.
*   **Efeito Visual Configurável:** O usuário pode configurar a cor, quantidade e tamanho das bolinhas.
*   **Modo Automático:** Ativa e desativa o efeito visual automaticamente com base na detecção de movimento do veículo.
*   **Interface Material 3:** Design moderno com animações fluidas e componentes Material 3.

## 🛠️ Tecnologias

*   **Linguagem:** Kotlin
*   **Interface:** Jetpack Compose (seguindo Material 3)
*   **Arquitetura:** Componentes do Android (Service, Activities, DataStore)
*   **Localização:** Google Play Services (Fused Location Provider)
*   **Sensores:** Acelerômetro, Giroscópio

## 📋 Requisitos

*   Android 8.0 (API 26) ou superior
*   Android Studio Flamingo ou superior
*   JDK 17 ou superior
*   Gradle 8.2 ou superior

## 🚀 Como Compilar

### Usando Android Studio

1. Clone o repositório:
   ```bash
   git clone https://github.com/Llucs/motioncues.git
   cd motioncues
   ```

2. Abra o projeto no Android Studio

3. Aguarde a sincronização do Gradle

4. Clique em **Build > Build Bundle(s) / APK(s) > Build APK(s)**

### Usando a Linha de Comando

1. Clone o repositório:
   ```bash
   git clone https://github.com/Llucs/motioncues.git
   cd motioncues
   ```

2. Execute o build:
   ```bash
   ./gradlew build
   ```

3. Para gerar um APK de release:
   ```bash
   ./gradlew assembleRelease
   ```

O APK será gerado em `app/build/outputs/apk/release/app-release.apk`

## 🧪 Testes

Para executar os testes unitários:

```bash
./gradlew test
```

Para executar o Android Lint:

```bash
./gradlew lint
```

## 📱 Instalação

Após compilar, você pode instalar o APK em um dispositivo Android conectado:

```bash
adb install app/build/outputs/apk/release/app-release.apk
```

## 🔧 Configuração

### Permissões Necessárias

O aplicativo requer as seguintes permissões:

*   `FOREGROUND_SERVICE` - Para executar um serviço em primeiro plano
*   `FOREGROUND_SERVICE_LOCATION` - Para acessar localização em primeiro plano
*   `ACCESS_FINE_LOCATION` - Para acessar GPS (modo automático)
*   `POST_NOTIFICATIONS` - Para exibir notificações (Android 13+)

### Estrutura do Projeto

```
MotionCues/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/llucs/motioncues/
│   │       │   ├── MainActivity.kt
│   │       │   ├── MainScreen.kt
│   │       │   ├── MotionService.kt
│   │       │   ├── SensorDetector.kt
│   │       │   ├── DotOverlayView.kt
│   │       │   ├── SettingsDataStore.kt
│   │       │   ├── Constants.kt
│   │       │   └── ui/theme/
│   │       ├── res/
│   │       │   ├── layout/
│   │       │   ├── drawable/
│   │       │   └── values/
│   │       └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
│   └── wrapper/
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── README.md
└── LICENSE.md
```

## 🔄 Integração Contínua

Este projeto utiliza **GitHub Actions** para automatizar o build a cada alteração no código-fonte. O workflow está configurado em `.github/workflows/android-build.yml` e realiza:

*   Build do projeto com Gradle
*   Geração de APK de release
*   Execução de testes unitários
*   Análise com Android Lint
*   Upload de artefatos (APK e relatório Lint)

O build é acionado automaticamente em:
*   Push para as branches `main` e `develop`
*   Pull requests para as branches `main` e `develop`

## 📖 Documentação

Para mais informações sobre o desenvolvimento, veja:

*   [Documentação do Android](https://developer.android.com/)
*   [Documentação do Jetpack Compose](https://developer.android.com/jetpack/compose)
*   [Documentação do Material 3](https://m3.material.io/)

## 🐛 Relatar Problemas

Se encontrar um bug ou tiver uma sugestão de melhoria, abra uma issue no repositório:

> [github.com/Llucs/motioncues/issues](https://github.com/Llucs/motioncues/issues)

## 💡 Contribuições

Contribuições são bem-vindas! Para contribuir:

1. Faça um fork do repositório
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está licenciado sob a Licença MIT. Veja o arquivo [LICENSE.md](LICENSE.md) para mais detalhes.

## 👨‍💻 Créditos

**Desenvolvedor:** Llucs

Este aplicativo foi desenvolvido com o objetivo de ajudar pessoas a reduzir o enjoo de movimento em veículos, oferecendo uma solução inovadora baseada em estímulos visuais.

## 📞 Suporte

Para dúvidas ou suporte, entre em contato através do repositório GitHub:

> [github.com/Llucs/motioncues/](https://github.com/Llucs/motioncues/)
