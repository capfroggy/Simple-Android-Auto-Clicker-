# Simple Android Auto Clicker

![Simple Android Auto Clicker](docs/assets/simple-android-auto-clicker-banner.webp)

**Un autoclicker pequeño para Android que permanece bajo tu control.**

Sin anuncios. Sin tracking. Sin cuentas. Sin permiso de Internet. Gratis y de código abierto.

[**Descargar el APK más reciente**](https://github.com/capfroggy/Simple-Android-Auto-Clicker-/releases/latest/download/Simple-Android-Auto-Clicker.apk)

[English](README.md)

## Por qué existe

Muchos autoclickers están llenos de anuncios, rastreadores, suscripciones o permisos difíciles de justificar. Este proyecto busca deliberadamente hacer menos y hacerlo de forma transparente:

- un punto de click movible;
- una velocidad configurable;
- un atajo mediante botón físico;
- sin nube ni control remoto.

## Funciones

- Posicionador flotante arrastrable.
- Intervalo configurable de **50 ms a 2000 ms**.
- Dos opciones para activar/desactivar:
  - mantener **Volumen + durante 3 segundos**;
  - hacer **doble pulsación en Volumen +**.
- El mismo gesto elegido activa y desactiva el autoclicker.
- No requiere root.
- Toda la configuración permanece en el teléfono.

## Privacidad por diseño

| Función | ¿Incluida? |
|---|---|
| Publicidad | No |
| Analytics / telemetría | No |
| SDKs de tracking | No |
| Cuenta | No |
| Permiso de Internet | No |
| Control remoto | No |
| Sincronización en nube | No |
| Ajustes locales | Sí |
| Servicio de Accesibilidad | Sí — solo para atajo, overlay y taps |

Consulta [PRIVACY.md](PRIVACY.md) para una explicación completa.

## Instalación

1. Abre la [última release](https://github.com/capfroggy/Simple-Android-Auto-Clicker-/releases/latest).
2. Descarga **Simple-Android-Auto-Clicker.apk**.
3. Instálalo en Android.
4. Abre la app y sigue los dos botones de configuración.

### Android 13+ — Configuración restringida

Android puede bloquear el permiso de Accesibilidad para APKs instalados fuera de una tienda.

Si **Simple Auto Clicker** aparece deshabilitado:

1. Ve a **Ajustes → Apps → Simple Auto Clicker**.
2. Toca **⋮** arriba a la derecha.
3. Selecciona **Permitir configuración restringida**.
4. Confirma con PIN, huella o bloqueo de pantalla si Android lo solicita.
5. Regresa a **Ajustes → Accesibilidad → Apps descargadas → Simple Auto Clicker**.
6. Activa el servicio.

Es una medida de seguridad de Android. La aplicación no intenta saltársela.

## Uso

1. Activa el Servicio de Accesibilidad.
2. Arrastra el posicionador al punto donde quieres hacer click.
3. Elige tu atajo:
   - mantener **Volumen +** durante 3 segundos, o
   - doble pulsación de **Volumen +**.
4. Usa el atajo para iniciar.
5. Usa el mismo atajo para detener.

## Por qué necesita Accesibilidad

Android no permite a una app normal generar taps dentro de otras apps. El Servicio de Accesibilidad se usa únicamente para:

- detectar el atajo elegido con Volumen +;
- mostrar el posicionador flotante;
- generar taps en la posición seleccionada.

La app **no solicita acceso a Internet** y no transmite el contenido de la pantalla ni información personal.

## Verificar el APK

Cada release incluye:

- `Simple-Android-Auto-Clicker.apk`
- `Simple-Android-Auto-Clicker.apk.sha256`

Puedes verificarlo con:

```bash
sha256sum Simple-Android-Auto-Clicker.apk
```

## Compilar desde código fuente

Requisitos:

- JDK 17
- Android SDK 35
- Gradle 8.9

```bash
gradle assembleDebug
```

El APK se genera en:

```
app/build/outputs/apk/debug/app-debug.apk
```

## Contribuciones

Se aceptan reportes de errores, mejoras pequeñas y pull requests enfocados. La idea central debe mantenerse: **simple, local, transparente y sin publicidad**.

Consulta [CONTRIBUTING.md](CONTRIBUTING.md).

## Licencia

MIT — consulta [LICENSE](LICENSE).
