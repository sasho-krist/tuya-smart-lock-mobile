# Смарт брава — Android

Android приложение за [tuya-smart-lock](https://github.com/sasho-krist/tuya-smart-lock). Отваря уеб интерфейса
на бравата в приложение, с Android споделяне и копиране на кодове, обновяване с дръпване надолу и запомнен вход.

## Изтегляне

APK-то се компилира автоматично от GitHub Actions при всеки push в `main`:
**Releases → последната версия → `SmartLock-1.0.X.apk`**.

На телефона: отвори файла → разреши „Инсталиране от неизвестни източници“ за браузъра → Инсталирай.

## Първо пускане

Въведи адреса на уеб страницата, например `https://lock.example.bg/` или `http://34.63.135.185/tuya-smart-lock/`.
Адресът се сменя от менюто (⋮) → „Смени адреса“.

## Сигурност

- При адрес с `http://` паролата минава некриптирана. Използвай HTTPS, когато имаш домейн.
- APK-то се подписва с `app/release.keystore` от repo-то, за да се инсталират новите версии върху старите.
  Ако repo-то е публично, замени ключа със собствен чрез GitHub secrets:
  `SIGNING_KEYSTORE_BASE64`, `SIGNING_STORE_PASSWORD`, `SIGNING_KEY_ALIAS`, `SIGNING_KEY_PASSWORD`.
  При смяна на ключа старото приложение трябва да се деинсталира веднъж.

## Локално компилиране

Нужни са Android SDK (API 35) и JDK 17:

```bash
./gradlew assembleRelease
# app/build/outputs/apk/release/app-release.apk
```
