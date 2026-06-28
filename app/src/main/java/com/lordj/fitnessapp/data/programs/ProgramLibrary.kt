package com.lordj.fitnessapp.data.programs

data class ProgramExercise(
    val exerciseName: String,
    val sets: Int,
    val reps: String,
    val restSeconds: Int,
    val coachNote: String = ""
)

data class ProgramTemplate(
    val id: String,
    val title: String,
    val subtitle: String,
    val bodyPart: String,
    val goal: String,
    val difficulty: Difficulty,
    val estimatedMinutes: Int,
    val frequency: String,
    val colorHex: String,
    val principleNote: String,
    val exercises: List<ProgramExercise>
)

enum class Difficulty(val label: String) {
    BEGINNER("Principiante"),
    INTERMEDIATE("Intermedio"),
    ADVANCED("Avanzado")
}

object ProgramLibrary {

    val all: List<ProgramTemplate> = listOf(

        // ─── PECHO ─────────────────────────────────────────────────────────
        ProgramTemplate(
            id = "chest_hypertrophy",
            title = "Pecho — Hipertrofia",
            subtitle = "Volumen óptimo para maximizar el crecimiento pectoral",
            bodyPart = "Pecho",
            goal = "Hipertrofia",
            difficulty = Difficulty.INTERMEDIATE,
            estimatedMinutes = 55,
            frequency = "2× semana",
            colorHex = "#EF4444",
            principleNote = "Los estudios (Schoenfeld 2016, RP) muestran que 12-20 series semanales en rangos de 6-20 reps con sobrecarga progresiva maximizan la hipertrofia pectoral. Los ejercicios inclinados activan mejor el pectoral superior, a menudo infra-desarrollado.",
            exercises = listOf(
                ProgramExercise("Press de Banca con Barra", 4, "5-7", 180,
                    "Compuesto principal — carga máxima. Retracción escapular, arco natural."),
                ProgramExercise("Press Inclinado en Smith Machine", 4, "8-10", 120,
                    "Banco a 30°. Por encima de 45° el trabajo pasa a los hombros."),
                ProgramExercise("Aperturas con Mancuernas", 3, "12-15", 90,
                    "Énfasis en el estiramiento. Codos ligeramente flexionados fijos."),
                ProgramExercise("Cable Crossover", 3, "15-20", 60,
                    "Tensión constante del cable. Poleas altas, cruza ligeramente abajo."),
                ProgramExercise("Fondos para Pecho", 3, "8-12", 90,
                    "Inclinación 45° hacia adelante. Si es difícil, usa banda asistida.")
            )
        ),

        ProgramTemplate(
            id = "chest_strength",
            title = "Pecho — Fuerza",
            subtitle = "Desarrolla un press de banca fuerte y sólido",
            bodyPart = "Pecho",
            goal = "Fuerza",
            difficulty = Difficulty.ADVANCED,
            estimatedMinutes = 60,
            frequency = "2× semana",
            colorHex = "#EF4444",
            principleNote = "Basado en metodología de powerlifting (Sheiko, Juggernaut). Trabajo en zona de fuerza (1-6 reps), series de trabajo pesadas + back-off sets para volumen. El pausa en el pecho aumenta la fuerza en el rango inferior.",
            exercises = listOf(
                ProgramExercise("Press de Banca con Barra", 5, "3-5", 240,
                    "Serie pesada al 85-90% 1RM. Pausa 1 seg en el pecho en las últimas 2 series."),
                ProgramExercise("Press en Smith Machine", 3, "8-10", 120,
                    "Back-off sets al 70% — volumen de calidad."),
                ProgramExercise("Press Inclinado en Smith Machine", 3, "8-10", 120,
                    "Debilita el pectoral superior, punto fuerte de mejora."),
                ProgramExercise("Fondos para Pecho", 3, "6-10", 120,
                    "Añade peso con cinturón cuando dominas las reps."),
                ProgramExercise("Cable Crossover", 2, "15-20", 60,
                    "Finisher de bajo impacto. Recuperación del tejido conectivo.")
            )
        ),

        // ─── ESPALDA ───────────────────────────────────────────────────────
        ProgramTemplate(
            id = "back_complete",
            title = "Espalda — Amplitud y Grosor",
            subtitle = "Construye un dorsal amplio y una espalda densa",
            bodyPart = "Espalda",
            goal = "Hipertrofia",
            difficulty = Difficulty.INTERMEDIATE,
            estimatedMinutes = 65,
            frequency = "2× semana",
            colorHex = "#3B82F6",
            principleNote = "La amplitud (dominadas/jalones) y el grosor (remos) requieren patrones distintos. RP recomienda 14-22 series semanales de espalda. El face pull es esencial para la salud del manguito rotador y equilibra el trabajo de press.",
            exercises = listOf(
                ProgramExercise("Dominadas", 4, "5-8", 180,
                    "Agarre prono, ancho de hombros + 15 cm. Bajada lenta 3 seg."),
                ProgramExercise("Remo con Barra", 4, "8-10", 120,
                    "Codos cerca del cuerpo = más dorsal. Inclinación ~45°."),
                ProgramExercise("Jalón al Pecho", 3, "10-12", 90,
                    "Complemento de dominadas. Lleva los codos abajo y hacia atrás."),
                ProgramExercise("Remo con Peso", 3, "10-12", 90,
                    "Énfasis en la retracción escapular. No uses impulso."),
                ProgramExercise("Face Pull", 4, "15-20", 60,
                    "Polea al nivel de los ojos. Trabajo bilateral del manguito rotador."),
                ProgramExercise("Hiperextensiones", 3, "12-15", 60,
                    "Fortalece la cadena posterior. Glúteos apretados en la cima.")
            )
        ),

        ProgramTemplate(
            id = "back_strength",
            title = "Espalda — Peso Muerto",
            subtitle = "Desarrolla una espalda fuerte con los levantamientos básicos",
            bodyPart = "Espalda",
            goal = "Fuerza",
            difficulty = Difficulty.ADVANCED,
            estimatedMinutes = 70,
            frequency = "1-2× semana",
            colorHex = "#3B82F6",
            principleNote = "El peso muerto es el ejercicio de mayor carga sistémica y potencial de fuerza. Layne Norton (PHAT) y Israetel coinciden: baja frecuencia, alta intensidad, recuperación completa. No te quedes sin fuerza para las series principales.",
            exercises = listOf(
                ProgramExercise("Peso Muerto", 5, "3-5", 300,
                    "El GRAN ejercicio. Espalda neutra siempre. Empuja el suelo, no tires."),
                ProgramExercise("Hiperextensiones", 3, "10-12", 90,
                    "Fortalece la zona lumbar para soportar el peso muerto."),
                ProgramExercise("Remo con Barra", 4, "6-8", 150,
                    "Remo pesado. Grosor de espalda y bíceps de apoyo."),
                ProgramExercise("Dominadas", 3, "max", 120,
                    "Al fallo — fuerza funcional y amplitud dorsal."),
                ProgramExercise("Face Pull", 3, "20", 60,
                    "Salud del manguito. Imprescindible junto a levantamientos pesados.")
            )
        ),

        // ─── HOMBROS ───────────────────────────────────────────────────────
        ProgramTemplate(
            id = "shoulders_complete",
            title = "Hombros — Desarrollo Completo",
            subtitle = "Los tres fascículos del deltoides de forma equilibrada",
            bodyPart = "Hombros",
            goal = "Hipertrofia",
            difficulty = Difficulty.INTERMEDIATE,
            estimatedMinutes = 55,
            frequency = "2× semana",
            colorHex = "#F97316",
            principleNote = "El deltoides lateral requiere alto volumen en rangos altos (15-30 reps) — estudios de EMG muestran que trabaja mejor con carga ligera y tensión continua. El OHP es el mejor constructor de deltoides anterior. El rear delt es crítico para la postura y salud del hombro.",
            exercises = listOf(
                ProgramExercise("Press Militar con Barra", 4, "6-8", 180,
                    "Press de pie con barra. Activa el core. No arquees la lumbar."),
                ProgramExercise("Press Arnold", 3, "10-12", 90,
                    "La rotación activa los tres fascículos del deltoides."),
                ProgramExercise("Elevaciones Laterales", 5, "15-20", 60,
                    "El volumen es clave aquí. Meñique más alto que el pulgar."),
                ProgramExercise("Elevaciones Laterales en Cable", 3, "15-20", 60,
                    "El cable mantiene tensión en todo el rango — superior a mancuernas."),
                ProgramExercise("Cable Reverse Fly de Pie", 4, "15-20", 60,
                    "Deltoides posterior. Esencial para equilibrio y postura."),
                ProgramExercise("Face Pull", 3, "20", 45,
                    "Salud del manguito rotador. Siempre en el entrenamiento de hombros.")
            )
        ),

        // ─── BRAZOS ────────────────────────────────────────────────────────
        ProgramTemplate(
            id = "arms_hypertrophy",
            title = "Brazos — Bíceps y Tríceps",
            subtitle = "Programa especializado para brazos grandes y definidos",
            bodyPart = "Brazos",
            goal = "Hipertrofia",
            difficulty = Difficulty.INTERMEDIATE,
            estimatedMinutes = 60,
            frequency = "2× semana",
            colorHex = "#A855F7",
            principleNote = "El tríceps compone ~2/3 del brazo — no lo descuides. Los estudios de Nippard muestran que el tríceps largo responde mejor a ejercicios sobre la cabeza (máximo estiramiento). El bíceps: 14-20 series semanales con énfasis en la supinación y el estiramiento.",
            exercises = listOf(
                ProgramExercise("Curl con Barra", 4, "8-10", 90,
                    "Muñecas neutras. Codos fijos a los lados. Aprieta bíceps en la cima."),
                ProgramExercise("Curl en Predicador", 3, "10-12", 75,
                    "Aísla el bíceps eliminando el trampa del impulso. Rango completo."),
                ProgramExercise("Curl Martillo", 3, "10-12", 75,
                    "Enfatiza el braquiorradial y braquial — da grosor al brazo."),
                ProgramExercise("Curl de Concentración", 3, "12-15", 60,
                    "Conexión mente-músculo máxima. Perfecto como finisher."),
                ProgramExercise("Skull Crushers", 4, "8-10", 90,
                    "Tríceps largo bajo máximo estiramiento. Barra EZ si tienes dolor de muñecas."),
                ProgramExercise("Extensión de Tríceps sobre la Cabeza", 3, "10-12", 75,
                    "Posición sobre la cabeza = máximo estiramiento del tríceps largo."),
                ProgramExercise("Extensión de Tríceps en Polea", 3, "12-15", 60,
                    "Tensión constante. Separa la cuerda al final para contracción total."),
                ProgramExercise("Fondos para Tríceps", 3, "8-12", 90,
                    "Cuerpo vertical. Añade peso cuando dominas las reps.")
            )
        ),

        // ─── PIERNAS ───────────────────────────────────────────────────────
        ProgramTemplate(
            id = "legs_complete",
            title = "Piernas — Programa Completo",
            subtitle = "Cuádriceps, isquiotibiales, glúteos y gemelos",
            bodyPart = "Piernas",
            goal = "Hipertrofia",
            difficulty = Difficulty.INTERMEDIATE,
            estimatedMinutes = 80,
            frequency = "2× semana",
            colorHex = "#22C55E",
            principleNote = "RP (Israetel) recomienda 12-20 series semanales de cuádriceps y 10-16 de isquiotibiales. La sentadilla y el peso muerto rumano son los pilares. Los gemelos responden mejor a alto volumen (15-25 reps) con rango completo de movimiento. El Hip Thrust es el rey de los glúteos.",
            exercises = listOf(
                ProgramExercise("Sentadilla", 4, "6-8", 180,
                    "Profundidad completa — muslos paralelos mínimo. Talones en el suelo."),
                ProgramExercise("Prensa de Piernas", 3, "10-12", 120,
                    "Después de sentadilla: más volumen de cuádriceps con menos fatiga sistémica."),
                ProgramExercise("Peso Muerto Rumano", 4, "8-10", 120,
                    "Cadena posterior. Baja hasta sentir el estiramiento en isquios. Espalda neutra."),
                ProgramExercise("Hip Thrust", 3, "10-12", 90,
                    "El mejor ejercicio de glúteos per EMG. Contrae 1 seg arriba."),
                ProgramExercise("Extensión de Cuádriceps", 3, "12-15", 75,
                    "Aislamiento de cuádriceps. Contrae 1 seg en la cima."),
                ProgramExercise("Curl Femoral", 3, "10-12", 75,
                    "Aislamiento de isquiotibiales. Baja lentamente (3 seg eccéntrico)."),
                ProgramExercise("Elevación de Talones de Pie", 4, "15-20", 60,
                    "Rango completo imprescindible. Estira bien abajo, contrae arriba."),
                ProgramExercise("Elevación de Talones Sentado", 3, "15-20", 60,
                    "Sóleo — diferente al gastrocnemio. Rodillas a 90°.")
            )
        ),

        ProgramTemplate(
            id = "legs_quads",
            title = "Cuádriceps — Fuerza y Masa",
            subtitle = "Enfoque en sentadilla y desarrollo anterior de muslo",
            bodyPart = "Piernas",
            goal = "Fuerza",
            difficulty = Difficulty.ADVANCED,
            estimatedMinutes = 65,
            frequency = "2× semana",
            colorHex = "#22C55E",
            principleNote = "El Hack Squat y la sentadilla búlgara son selecciones de élite para cuádriceps según múltiples estudios de EMG. La sentadilla búlgara produce un estiramiento único del cuádriceps que activa maximalmente el recto femoral — músculo frecuentemente subdesarrollado.",
            exercises = listOf(
                ProgramExercise("Sentadilla", 5, "4-6", 240,
                    "Trabajo de fuerza. Pausa 1 seg en el hoyo en las últimas series."),
                ProgramExercise("Hack Squat", 4, "8-10", 120,
                    "Pies bajos en la plataforma para máximo énfasis en cuádriceps."),
                ProgramExercise("Sentadilla Búlgara", 3, "10-12", 90,
                    "Unilateral — detecta y corrige desequilibrios. Paso largo hacia adelante."),
                ProgramExercise("Prensa de Piernas", 3, "12-15", 90,
                    "Volumen adicional de cuádriceps con fatiga sistémica mínima."),
                ProgramExercise("Extensión de Cuádriceps", 4, "15-20", 60,
                    "Alto volumen de aislamiento. Contrae 2 seg en la cima."),
                ProgramExercise("Elevación de Talones de Pie", 4, "15-20", 60,
                    "Gemelos — con rango completo de movimiento siempre.")
            )
        ),

        ProgramTemplate(
            id = "glutes_program",
            title = "Glúteos — Fuerza e Hipertrofia",
            subtitle = "El programa más efectivo para glúteos según la ciencia",
            bodyPart = "Piernas",
            goal = "Hipertrofia",
            difficulty = Difficulty.INTERMEDIATE,
            estimatedMinutes = 60,
            frequency = "2-3× semana",
            colorHex = "#22C55E",
            principleNote = "Bret Contreras (\"El Dr. Glúteo\") demostró que el Hip Thrust produce 3× más activación glútea que la sentadilla. La combinación de Hip Thrust + ejercicios de abducción + trabajo unilateral es la triple amenaza para glúteos. Los glúteos responden bien a alta frecuencia.",
            exercises = listOf(
                ProgramExercise("Hip Thrust", 4, "8-12", 120,
                    "Primero y más importante. Contrae los glúteos 1-2 seg en la cima."),
                ProgramExercise("Sentadilla Búlgara", 3, "10-12", 90,
                    "Unilateral — activa el glúteo de forma diferente al bilateral."),
                ProgramExercise("Peso Muerto Rumano", 3, "10-12", 90,
                    "Cadena posterior. Bisagra de cadera perfecta — empuja caderas atrás."),
                ProgramExercise("Abducción de Cadera", 4, "15-20", 60,
                    "Glúteo medio y menor — dan forma redondeada al glúteo."),
                ProgramExercise("Zancadas con Mancuernas", 3, "12", 75,
                    "12 repeticiones por pierna. Paso largo para mayor activación glútea."),
                ProgramExercise("Elevación de Talones de Pie", 3, "15", 60,
                    "Finaliza con gemelos para la cadena posterior completa.")
            )
        ),

        // ─── CORE ──────────────────────────────────────────────────────────
        ProgramTemplate(
            id = "core_functional",
            title = "Core — Fuerza Funcional",
            subtitle = "Un core fuerte protege la columna y mejora todos los ejercicios",
            bodyPart = "Core",
            goal = "Fuerza",
            difficulty = Difficulty.BEGINNER,
            estimatedMinutes = 35,
            frequency = "3× semana",
            colorHex = "#14B8A6",
            principleNote = "McGill (experto mundial en columna) defiende los ejercicios isométricos sobre los de flexión repetida. La plancha y variantes anti-rotación son más funcionales y seguros que los crunches para la mayoría. El core actúa como estabilizador, no como motor principal.",
            exercises = listOf(
                ProgramExercise("Plancha", 4, "45-60 seg", 45,
                    "Ombligo hacia la columna. Glúteos apretados. Cuerpo rígido."),
                ProgramExercise("Plancha Lateral", 3, "30-45 seg", 45,
                    "Cada lado. Anti-lateral-flexión — protege la columna."),
                ProgramExercise("Rueda Abdominal", 3, "8-12", 75,
                    "Avanzado — empieza con rango corto. El mejor ejercicio de core per EMG."),
                ProgramExercise("Elevaciones de Piernas", 3, "15", 60,
                    "Abdomen inferior. Sin arquear la lumbar."),
                ProgramExercise("Mountain Climbers", 3, "30 seg", 45,
                    "Cardio + core. Caderas bajas y estables."),
                ProgramExercise("Giros Rusos", 3, "20", 45,
                    "Oblicuos. Con peso para más dificultad.")
            )
        ),

        // ─── CUERPO COMPLETO ───────────────────────────────────────────────
        ProgramTemplate(
            id = "fullbody_3days",
            title = "Full Body — 3 Días",
            subtitle = "Entrena todo el cuerpo con los grandes movimientos",
            bodyPart = "Cuerpo Completo",
            goal = "Hipertrofia",
            difficulty = Difficulty.BEGINNER,
            estimatedMinutes = 65,
            frequency = "3× semana",
            colorHex = "#6366F1",
            principleNote = "Altamente respaldado por la ciencia para principiantes e intermedios (Schoenfeld 2015). 3 días full body > 5 días de splits para la mayoría. Cada músculo se estimula 3× por semana con volumen moderado. La frecuencia es el factor diferencial.",
            exercises = listOf(
                ProgramExercise("Sentadilla", 3, "8-10", 150,
                    "El movimiento más completo. Cuádriceps, glúteos, core. Perfecciona la técnica."),
                ProgramExercise("Press de Banca con Barra", 3, "8-10", 150,
                    "Movimiento de empuje horizontal. Pecho, tríceps, hombros."),
                ProgramExercise("Peso Muerto", 3, "6-8", 180,
                    "Movimiento de tirón total. Espalda, piernas, todo. Una vez a la semana."),
                ProgramExercise("Press Militar con Barra", 3, "8-10", 120,
                    "Empuje vertical. Hombros, tríceps, core estabilizador."),
                ProgramExercise("Jalón al Pecho", 3, "10-12", 90,
                    "Tirón vertical. Dorsal y bíceps. Alternativa a dominadas."),
                ProgramExercise("Remo con Peso", 3, "10-12", 90,
                    "Tirón horizontal. Grosor de espalda y bíceps."),
                ProgramExercise("Plancha", 3, "30-45 seg", 45,
                    "Core. Al final de cada sesión.")
            )
        ),

        ProgramTemplate(
            id = "ppl_push",
            title = "PPL — Push (Empuje)",
            subtitle = "Pecho, hombros y tríceps al máximo",
            bodyPart = "Cuerpo Completo",
            goal = "Hipertrofia",
            difficulty = Difficulty.INTERMEDIATE,
            estimatedMinutes = 65,
            frequency = "2× semana",
            colorHex = "#6366F1",
            principleNote = "El split PPL (Push/Pull/Legs) es el estándar de oro para intermedios con 3-6 días disponibles. Permite frecuencia 2× por músculo en 6 días o 1× en 3 días. El día Push combina pecho, hombros y tríceps con sinergia natural de movimientos.",
            exercises = listOf(
                ProgramExercise("Press de Banca con Barra", 4, "6-8", 180,
                    "Movimiento principal. Pesado. Técnica perfecta."),
                ProgramExercise("Press Inclinado en Smith Machine", 4, "8-10", 120,
                    "Pectoral superior — zona frecuentemente descuidada."),
                ProgramExercise("Press Militar con Barra", 3, "8-10", 120,
                    "Empuje vertical — hombros, tríceps."),
                ProgramExercise("Elevaciones Laterales", 4, "15-20", 60,
                    "Alto volumen. La anchura de los hombros viene de aquí."),
                ProgramExercise("Skull Crushers", 3, "8-10", 90,
                    "Tríceps largo — estiramiento máximo."),
                ProgramExercise("Extensión de Tríceps en Polea", 3, "12-15", 60,
                    "Finisher de tríceps. Tensión constante."),
                ProgramExercise("Cable Crossover", 2, "15-20", 60,
                    "Finisher de pecho de bajo impacto.")
            )
        ),

        ProgramTemplate(
            id = "ppl_pull",
            title = "PPL — Pull (Tirón)",
            subtitle = "Espalda y bíceps con patrones de tirón",
            bodyPart = "Cuerpo Completo",
            goal = "Hipertrofia",
            difficulty = Difficulty.INTERMEDIATE,
            estimatedMinutes = 65,
            frequency = "2× semana",
            colorHex = "#6366F1",
            principleNote = "El día Pull combina los patrones de tirón verticales (jalones/dominadas) y horizontales (remos) que trabajan en sinergia. Los bíceps reciben trabajo directo e indirecto. El face pull es el \"seguro de vida\" del manguito rotador para quien entrena push pesado.",
            exercises = listOf(
                ProgramExercise("Dominadas", 4, "6-8", 180,
                    "Agarre amplio prono. Si no puedes, usa el jalón al pecho."),
                ProgramExercise("Remo con Barra", 4, "8-10", 120,
                    "Codos a los lados para más dorsal. Inclinado 45°."),
                ProgramExercise("Jalón Agarre Cerrado", 3, "10-12", 90,
                    "Agarre supino para mayor activación del bíceps."),
                ProgramExercise("Remo con Peso", 3, "10-12", 90,
                    "Retracción escapular completa. No uses impulso."),
                ProgramExercise("Face Pull", 3, "20", 60,
                    "Polea a nivel de los ojos. Imprescindible siempre."),
                ProgramExercise("Curl con Barra", 3, "10-12", 75,
                    "Bíceps directo. Codos fijos."),
                ProgramExercise("Curl Martillo", 3, "10-12", 60,
                    "Braquiorradial y braquial — dan grosor al brazo.")
            )
        )
    )

    val bodyParts: List<String> = listOf(
        "Todos",
        "Pecho",
        "Espalda",
        "Hombros",
        "Brazos",
        "Piernas",
        "Core",
        "Cuerpo Completo"
    )

    val goals: List<String> = listOf("Todos", "Hipertrofia", "Fuerza")
}
