<![CDATA[<div align="center">

# ⚡ IronPulse

**Entrenamiento inteligente. Progreso real.**

[![Build APK](https://github.com/lord-jorx/super-waffle/actions/workflows/build.yml/badge.svg?branch=claude%2Fandroid-fitness-tracker-jzb07j)](https://github.com/lord-jorx/super-waffle/actions/workflows/build.yml)
![Platform](https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white)
![Min SDK](https://img.shields.io/badge/minSdk-26-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-4285F4?logo=jetpackcompose&logoColor=white)

</div>

---

## ¿Qué es IronPulse?

IronPulse es una aplicación Android de seguimiento de fitness construida con **Jetpack Compose** y **Material Design 3**. Combina el registro de rutinas de gimnasio con la sincronización de actividades desde **Garmin vía Health Connect**, formando un panel completo del estado físico del usuario.

No es otro clon de Strong. IronPulse unifica:
- el seguimiento de series/repeticiones/peso de una app de gym
- el historial de actividad cardiovascular de un tracker de wearables
- el control de composición corporal de una app de salud

todo en una sola interfaz nativa, sin suscripciones ni datos en la nube.

---

## Características principales

### 🏋️ Biblioteca de ejercicios
- Más de 50 ejercicios clasificados por grupo muscular (Pecho, Espalda, Hombros, Brazos, Piernas, Core, Cardio)
- **Pictogramas paso a paso** — 3 fotogramas Canvas que muestran la técnica de cada ejercicio (sentadilla, peso muerto, press banca, curl, remo, jalón, plancha…)
- Visualización de activación muscular con barras de intensidad (principal vs secundarios)
- Gráficas de progresión de peso y volumen por sesión
- Historial de las últimas 15 series por ejercicio
- Enlace directo a demostración en YouTube

### 📋 Rutinas y entrenamientos
- Creación de rutinas personalizadas con cualquier combinación de ejercicios
- Modo entrenamiento activo con cronómetro de descanso entre series
- Log de series con peso, repeticiones y warmup
- Registro automático de sesiones con duración, volumen total y número de series
- Modo "entrena solo" para ejercicios individuales

### 📊 Progreso e historial
- Historial completo de sesiones con resumen de volumen
- Exportación de datos en CSV y JSON
- Gráfica de progresión de peso corporal con línea de tendencia

### ⚖️ Peso corporal
- Registro diario de peso con notas opcionales
- Gráfica de evolución temporal (Canvas, nativo)
- Estadísticas: peso actual, cambio total, número de registros

### ⌚ Garmin / Health Connect
- Importación automática de sesiones Garmin desde **Google Health Connect**
- Muestra tipo de actividad, duración, calorías y frecuencia cardíaca media/máxima
- Compatible con el flujo de permisos de Android 14+ (incluyendo Samsung One UI)
- Marca las actividades ya importadas para evitar duplicados

### 🎨 Personalización
- Tema claro / oscuro / sistema con persistencia en preferencias
- Icono adaptativo moderno (mancuerna + línea de pulso cardíaco)

---

## Tech Stack

| Capa | Tecnología |
|---|---|
| UI | Jetpack Compose + Material Design 3 |
| Lenguaje | Kotlin 2.0.21 |
| Arquitectura | MVVM + Repository pattern |
| Base de datos | Room 2.6 (SQLite) con migraciones |
| Reactividad | StateFlow + collectAsStateWithLifecycle |
| Gráficas | Canvas (nativo, sin librerías externas) |
| Salud | Health Connect SDK 1.1.0-alpha07 |
| Navegación | Navigation Compose |
| DI | Manual (factory pattern en ViewModels) |
| CI | GitHub Actions → APK de debug |

---

## Arquitectura

```
app/
├── data/
│   ├── db/          # Room database, DAOs, entidades, migraciones
│   ├── health/      # HealthConnectManager, GarminSession
│   ├── model/       # Entidades: Exercise, Workout, SetLog, BodyWeight…
│   ├── preferences/ # AppPreferences (ThemeMode vía SharedPreferences)
│   ├── repository/  # ExerciseRepository, WorkoutRepository, BodyWeightRepository…
│   └── seeder/      # Seed inicial de ejercicios en español
├── ui/
│   ├── components/  # WeightProgressChart, VolumeBarChart
│   ├── navigation/  # NavGraph, Screen sealed class
│   ├── screens/     # Una carpeta por pantalla (home, exercises, workouts, …)
│   ├── theme/       # Color, Type, Theme con soporte ThemeMode
│   └── viewmodel/   # Un ViewModel por dominio
└── FitnessApp.kt    # Application class — lazy init de repositorios
```

### Flujo de datos

```
UI (Compose) ←→ ViewModel (StateFlow) ←→ Repository ←→ DAO (Room) / HealthConnectManager
```

Cada pantalla observa un `StateFlow` del ViewModel. Los repositorios son la única fuente de verdad: nunca hay llamadas a Room desde la UI.

---

## Pantallas

| Pantalla | Descripción |
|---|---|
| **Inicio** | Acceso rápido a última rutina, sesión activa, Garmin |
| **Ejercicios** | Búsqueda y filtro por categoría, acceso a detalle |
| **Detalle ejercicio** | Pictogramas técnica, músculos, stats, historial, YouTube |
| **Rutinas** | Lista de rutinas y programas predefinidos |
| **Crear rutina** | Selector de ejercicios con sets/reps objetivo |
| **Entrenamiento activo** | Log de series en tiempo real con timer |
| **Progreso** | Gráficas de volumen y PR por ejercicio |
| **Historial** | Todas las sesiones con exportación |
| **Garmin Sync** | Importar actividades vía Health Connect |
| **Peso corporal** | Gráfica de evolución + log diario |
| **Ajustes** | Tema, Garmin, peso corporal |

---

## Cómo compilar

**Requisitos:** Android Studio Hedgehog o superior, JDK 17.

```bash
git clone https://github.com/lord-jorx/super-waffle.git
cd super-waffle
git checkout claude/android-fitness-tracker-jzb07j
./gradlew assembleDebug
```

El APK se genera en `app/build/outputs/apk/debug/`.

También disponible vía **GitHub Actions**: cada push al branch de desarrollo genera un artefacto descargable.

---

## Permisos de Health Connect (Samsung / Android 14+)

Si el botón "Conceder permisos" no funciona directamente, ve a:

> Ajustes del móvil → busca **Health Connect** → Permisos de aplicación → IronPulse → activa Ejercicio, Frecuencia cardíaca, Calorías

La app incluye instrucciones en pantalla para este caso.

---

## Roadmap

- [ ] Gráfica de 1RM estimado (Epley) por ejercicio
- [ ] Notificaciones de timer de descanso
- [ ] Sincronización con Apple Health (iOS, futuro)
- [ ] Widget de pantalla de inicio
- [ ] Planificación semanal de rutinas

---

## Licencia

Proyecto personal. Todos los derechos reservados © 2026 lord-jorx.
]]>