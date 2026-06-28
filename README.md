# IronPulse

[![Build](https://github.com/lord-jorx/super-waffle/actions/workflows/build.yml/badge.svg?branch=claude%2Fandroid-fitness-tracker-jzb07j)](https://github.com/lord-jorx/super-waffle/actions/workflows/build.yml)

App Android de fitness que empecé porque ninguna de las que existen hace exactamente lo que quiero. Strong es buena para el gym pero no sabe que existo cuando salgo a correr o cojo la bici con el Garmin. Strava no le importa cuánto peso levanto. Y apps como MyFitnessPal tienen suscripción para la mitad de las cosas.

IronPulse lo mete todo en el mismo sitio: lo que hago en el gym, lo que registra el Garmin, y cómo evoluciona mi cuerpo. Sin cuenta, sin cloud, todo en el móvil.

---

### Lo que hace

**Gym.** Creas tus rutinas, le das a iniciar entrenamiento y vas apuntando series conforme las haces. Timer de descanso entre series, calcula el volumen total, guarda el historial. Nada del otro mundo pero funciona bien.

**Ejercicios.** Hay unos 50 ejercicios con descripción, músculos que trabajan y pasos de técnica dibujados con stick figures animados (sí, dibujados a mano en Canvas porque no quería meter una librería solo para eso). Cada ejercicio te muestra tu progresión de peso a lo largo del tiempo y te manda a YouTube si quieres ver la ejecución real.

**Garmin / Health Connect.** La parte más complicada de hacer. Conecta con Health Connect para importar tus sesiones de Garmin — distancia, calorías, frecuencia cardíaca, todo. En Samsung con Android 14+ hay un comportamiento raro con los permisos; la app tiene instrucciones específicas para eso porque me tocó depurarlo.

**Peso corporal.** Registro diario con gráfica de evolución. Simple pero útil para ver la tendencia a lo largo de semanas.

**Temas.** Claro, oscuro o según el sistema. Se guarda, obviamente.

---

### Stack

Kotlin + Jetpack Compose con Material3. Room para la base de datos local con migraciones. StateFlow para la reactividad. Las gráficas están hechas con Canvas nativo — no hay ninguna librería de charts, me apetecía hacerlo así. Health Connect SDK para todo lo de Garmin. CI con GitHub Actions que genera el APK en cada push.

La arquitectura es MVVM clásico con repositorios. Cada pantalla tiene su ViewModel, los repositorios son la única capa que toca Room o Health Connect, y la UI solo observa StateFlows. Nada experimental.

```
data/
  db/           Room + DAOs + migraciones
  health/       HealthConnectManager
  repository/   una clase por dominio
ui/
  screens/      una carpeta por pantalla
  viewmodel/    un ViewModel por pantalla
  theme/        colores, tipografía, modo oscuro
```

---

### Compilar

Necesitas Android Studio y JDK 17.

```bash
git checkout claude/android-fitness-tracker-jzb07j
./gradlew assembleDebug
```

O descarga el APK directamente desde los artefactos de GitHub Actions.

---

### Health Connect en Samsung

Si el botón de permisos te lleva a la pantalla de info de la app en lugar de al diálogo de Health Connect, ve a Ajustes → busca "Health Connect" → Permisos de aplicación → IronPulse y activa los permisos manualmente. Es un bug conocido en algunos modelos Samsung con One UI.
