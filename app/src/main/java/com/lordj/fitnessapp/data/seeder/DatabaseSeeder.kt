package com.lordj.fitnessapp.data.seeder

import com.lordj.fitnessapp.data.db.AppDatabase
import com.lordj.fitnessapp.data.model.Exercise
import com.lordj.fitnessapp.data.model.Workout
import com.lordj.fitnessapp.data.model.WorkoutExercise

object DatabaseSeeder {

    suspend fun seed(db: AppDatabase) {
        if (db.exerciseDao().getCount() > 0) return

        val ids = mutableMapOf<String, Long>()

        fun ex(
            name: String, nameEn: String = "", cat: String, muscle: String,
            secondary: String = "", equipment: String, desc: String,
            steps: String = "", tips: String = "", fromRoutine: Boolean = false
        ) = Exercise(name = name, nameEn = nameEn, category = cat, primaryMuscle = muscle,
            secondaryMuscles = secondary, equipment = equipment, description = desc,
            executionSteps = steps, tips = tips, isFromUserRoutine = fromRoutine)

        val exercises = listOf(
            // ── PIERNAS (from user routine) ──
            ex("Extensión de Cuádriceps", "Weighted Leg Extensions", "Piernas", "Cuádriceps",
                equipment = "Máquina",
                desc = "Ejercicio de aislamiento para cuádriceps en máquina de extensión.",
                steps = "1. Siéntate en la máquina ajustando el respaldo.\n2. Coloca los pies bajo las almohadillas.\n3. Extiende las piernas completamente contrayendo los cuádriceps.\n4. Baja de forma controlada hasta 90°.",
                tips = "Mantén la contracción 1 segundo en la cima. No uses impulso.", fromRoutine = true),
            ex("Sentadilla", "Squat", "Piernas", "Cuádriceps",
                secondary = "Glúteos,Isquiotibiales,Core",
                equipment = "Barra",
                desc = "El rey de los ejercicios de piernas. Trabaja todo el tren inferior.",
                steps = "1. Coloca la barra en los trapecios.\n2. Pies a la anchura de los hombros, ligeramente hacia afuera.\n3. Desciende controladamente hasta que los muslos queden paralelos al suelo.\n4. Empuja con los talones para subir manteniendo el pecho erguido.",
                tips = "Rodillas alineadas con los pies. No dejes que colapsen hacia dentro.", fromRoutine = true),
            ex("Curl Femoral", "Leg Curl", "Piernas", "Isquiotibiales",
                equipment = "Máquina",
                desc = "Aislamiento de isquiotibiales en máquina de curl femoral tumbado.",
                steps = "1. Túmbate boca abajo en la máquina.\n2. Coloca los talones bajo las almohadillas.\n3. Flexiona las rodillas llevando los talones hacia los glúteos.\n4. Baja de forma lenta y controlada.",
                tips = "No levantes las caderas. Contrae los isquios en la cima.", fromRoutine = true),
            ex("Prensa de Piernas", "Leg Press", "Piernas", "Cuádriceps",
                secondary = "Glúteos,Isquiotibiales",
                equipment = "Máquina",
                desc = "Ejercicio multiarticular de piernas en prensa a 45 grados.",
                steps = "1. Siéntate con la espalda y cabeza apoyadas en el respaldo.\n2. Pies a la anchura de los hombros en la plataforma.\n3. Desbloquea los seguros y baja la plataforma hacia el pecho.\n4. Empuja hasta casi extender las rodillas (sin bloquear).",
                tips = "No bloquees las rodillas arriba. Mantén la zona lumbar apoyada.", fromRoutine = true),
            ex("Elevación de Talones de Pie", "Calf Raise", "Piernas", "Gemelos",
                secondary = "Sóleo",
                equipment = "Máquina",
                desc = "Ejercicio de aislamiento para gemelos en máquina o con peso libre.",
                steps = "1. Colócate en la máquina con los hombros bajo las almohadillas.\n2. Talones fuera del escalón.\n3. Sube de puntillas lo máximo posible.\n4. Mantén 1 segundo arriba y baja lentamente estirando bien los gemelos.",
                tips = "Realiza el rango completo de movimiento para máxima activación.", fromRoutine = true),

            // PIERNAS extra
            ex("Peso Muerto Rumano", "Romanian Deadlift", "Piernas", "Isquiotibiales",
                secondary = "Glúteos,Zona Lumbar",
                equipment = "Barra",
                desc = "Movimiento de bisagra de cadera que estira e hipercarga los isquiotibiales.",
                steps = "1. De pie, barra en las manos a la anchura de los hombros.\n2. Dobla ligeramente las rodillas, lleva las caderas hacia atrás.\n3. Baja la barra por las piernas hasta sentir el estiramiento.\n4. Vuelve a la posición inicial apretando glúteos.",
                tips = "Espalda neutra durante todo el movimiento. No redondees la zona lumbar."),
            ex("Sentadilla Búlgara", "Bulgarian Split Squat", "Piernas", "Cuádriceps",
                secondary = "Glúteos,Isquiotibiales",
                equipment = "Mancuerna",
                desc = "Sentadilla unilateral con el pie trasero elevado. Excelente para la hipertrofia.",
                steps = "1. Pie trasero apoyado en un banco.\n2. Da un paso largo hacia adelante con el pie delantero.\n3. Baja la rodilla trasera hacia el suelo manteniendo el torso erguido.\n4. Empuja con el talón delantero para subir.",
                tips = "Mantén el peso sobre el talón del pie delantero."),
            ex("Hip Thrust", "Hip Thrust", "Piernas", "Glúteos",
                secondary = "Isquiotibiales,Core",
                equipment = "Barra",
                desc = "El mejor ejercicio para los glúteos. Empuje de cadera con barra.",
                steps = "1. Apoya los omóplatos en un banco, barra sobre las caderas.\n2. Pies apoyados en el suelo a la anchura de los hombros.\n3. Empuja las caderas hacia arriba contrayendo los glúteos al máximo.\n4. Mantén 1 segundo arriba y baja de forma controlada.",
                tips = "Contrae glúteos en la cima. Mentón metido hacia el pecho."),
            ex("Zancadas con Mancuernas", "Dumbbell Lunges", "Piernas", "Cuádriceps",
                secondary = "Glúteos,Isquiotibiales",
                equipment = "Mancuerna",
                desc = "Ejercicio unilateral para el tren inferior con mancuernas.",
                steps = "1. De pie con una mancuerna en cada mano.\n2. Da un paso largo hacia adelante.\n3. Baja la rodilla trasera hasta casi tocar el suelo.\n4. Vuelve a la posición inicial y alterna piernas.",
                tips = "Rodilla delantera alineada con el pie. No dejes que supere los dedos."),
            ex("Elevación de Talones Sentado", "Seated Calf Raise", "Piernas", "Sóleo",
                secondary = "Gemelos",
                equipment = "Máquina",
                desc = "Trabaja principalmente el sóleo al realizar las elevaciones sentado.",
                steps = "1. Siéntate en la máquina con las rodillas a 90°.\n2. Almohadillas sobre los muslos.\n3. Sube de puntillas lo máximo posible.\n4. Baja lentamente estirando bien.",
                tips = "Rodillas a 90° para mayor activación del sóleo."),
            ex("Hack Squat", "Hack Squat", "Piernas", "Cuádriceps",
                secondary = "Glúteos",
                equipment = "Máquina",
                desc = "Sentadilla en máquina guiada que enfatiza el trabajo de cuádriceps.",
                steps = "1. Apoya la espalda en la almohadilla, pies en la plataforma.\n2. Desbloquea y baja flexionando las rodillas.\n3. Baja hasta 90° o más si la movilidad lo permite.\n4. Empuja para volver arriba.",
                tips = "Pies más altos = más glúteos. Pies más bajos = más cuádriceps."),
            ex("Abducción de Cadera", "Hip Abduction", "Piernas", "Abductores",
                equipment = "Máquina",
                desc = "Ejercicio de aislamiento para los abductores en máquina.",
                steps = "1. Siéntate en la máquina con las piernas juntas.\n2. Empuja las almohadillas hacia fuera separando las piernas.\n3. Vuelve lentamente a la posición inicial.",
                tips = "Usa el rango completo de movimiento."),

            // ── ESPALDA (from user routine) ──
            ex("Jalón al Pecho", "Lat Pull-down", "Espalda", "Dorsal Ancho",
                secondary = "Bíceps,Romboides",
                equipment = "Polea",
                desc = "Ejercicio básico para el dorsal ancho en polea alta con agarre amplio.",
                steps = "1. Agarra la barra a más de la anchura de los hombros.\n2. Siéntate con los muslos bajo las almohadillas.\n3. Lleva la barra hacia el pecho superior arqueando ligeramente la espalda.\n4. Sube de forma controlada estirando el dorsal.",
                tips = "Lleva los codos hacia abajo y hacia atrás. No uses impulso.", fromRoutine = true),
            ex("Jalón Agarre Cerrado", "Close-grip Lat Pull-down", "Espalda", "Dorsal Ancho",
                secondary = "Bíceps,Romboides",
                equipment = "Polea",
                desc = "Variante de jalón con agarre cerrado que enfatiza la parte interior del dorsal.",
                steps = "1. Agarra el triángulo o barra estrecha con palmas enfrentadas.\n2. Siéntate con los muslos bajo las almohadillas.\n3. Lleva el agarre hacia el pecho contrayendo el dorsal.\n4. Sube de forma controlada.",
                tips = "Mantén el pecho ligeramente elevado durante todo el movimiento.", fromRoutine = true),
            ex("Remo con Peso", "Weighted Row", "Espalda", "Dorsal Ancho",
                secondary = "Romboides,Trapecio,Bíceps",
                equipment = "Máquina",
                desc = "Remo en máquina o polea baja para trabajar el grosor de la espalda.",
                steps = "1. Siéntate en la máquina, pecho contra el apoyo.\n2. Agarra las empuñaduras con los brazos extendidos.\n3. Lleva los codos hacia atrás juntando los omóplatos.\n4. Vuelve lentamente a la posición inicial.",
                tips = "Retracta los omóplatos antes de tirar. No uses el cuerpo.", fromRoutine = true),
            ex("Cable Reverse Fly de Pie", "Single-arm Standing Cable Reverse Fly", "Espalda", "Deltoides Posterior",
                secondary = "Romboides,Infraespinoso",
                equipment = "Cable",
                desc = "Vuelo inverso en cable para el deltoides posterior y romboides.",
                steps = "1. De pie frente a la polea, agarra con la mano contraria al cable.\n2. Con el codo ligeramente flexionado, lleva el brazo hacia atrás y afuera.\n3. Mantén 1 segundo en la contracción máxima.\n4. Vuelve controladamente.",
                tips = "Usa poco peso y enfócate en la contracción. Mantén la espalda neutra.", fromRoutine = true),

            // ESPALDA extra
            ex("Dominadas", "Pull-ups", "Espalda", "Dorsal Ancho",
                secondary = "Bíceps,Romboides",
                equipment = "Barra",
                desc = "El ejercicio por excelencia para desarrollar el dorsal ancho con peso corporal.",
                steps = "1. Agarra la barra con palmas hacia afuera a más de los hombros.\n2. Cuelga con los brazos extendidos.\n3. Tira hacia arriba hasta que la barbilla supere la barra.\n4. Baja de forma lenta y controlada.",
                tips = "Retracta los omóplatos antes de subir. Evita el balanceo."),
            ex("Peso Muerto", "Deadlift", "Espalda", "Zona Lumbar",
                secondary = "Cuádriceps,Isquiotibiales,Glúteos,Trapecio",
                equipment = "Barra",
                desc = "Uno de los tres levantamientos básicos. Trabaja prácticamente todo el cuerpo.",
                steps = "1. Barra sobre los metatarsos, pies a la anchura de las caderas.\n2. Agarra la barra justo fuera de las piernas.\n3. Pecho arriba, espalda neutra, caderas atrás.\n4. Empuja el suelo con los pies y estira las caderas al mismo tiempo.\n5. Lleva la barra pegada al cuerpo durante todo el recorrido.",
                tips = "Espalda SIEMPRE neutra. Empuja el suelo, no tires de la barra."),
            ex("Remo con Barra", "Barbell Row", "Espalda", "Dorsal Ancho",
                secondary = "Romboides,Trapecio,Bíceps",
                equipment = "Barra",
                desc = "Remo inclinado con barra para desarrollar el grosor de la espalda.",
                steps = "1. Inclínate hacia adelante con la espalda casi paralela al suelo.\n2. Agarra la barra a la anchura de los hombros.\n3. Lleva la barra hacia el ombligo apretando los omóplatos.\n4. Baja de forma controlada.",
                tips = "Mantén la espalda neutra. Codos cerca del cuerpo para más dorsal."),
            ex("Hiperextensiones", "Back Extensions", "Espalda", "Zona Lumbar",
                secondary = "Glúteos,Isquiotibiales",
                equipment = "Máquina",
                desc = "Ejercicio para fortalecer la zona lumbar en banco de hiperextensiones.",
                steps = "1. Colócate en el banco con las caderas sobre la almohadilla.\n2. Baja el torso hacia el suelo de forma controlada.\n3. Sube hasta que el cuerpo forme una línea recta.\n4. No hiperextiendas la espalda.",
                tips = "Glúteos apretados en la cima para proteger la zona lumbar."),
            ex("Face Pull", "Face Pull", "Espalda", "Deltoides Posterior",
                secondary = "Romboides,Trapecio Medio",
                equipment = "Cable",
                desc = "Ejercicio con cuerda en polea alta para salud del manguito rotador y rear delts.",
                steps = "1. Polea a la altura de los ojos, agarra la cuerda con ambas manos.\n2. Da un paso atrás, brazos extendidos.\n3. Lleva la cuerda hacia la cara separando las manos.\n4. Los codos quedan en línea con los hombros.",
                tips = "Peso ligero y técnica perfecta. Esencial para la salud del hombro."),

            // ── HOMBROS (from user routine) ──
            ex("Press con Mancuernas", "Dumbbell Shoulder Press", "Hombros", "Deltoides",
                secondary = "Tríceps,Trapecio",
                equipment = "Mancuerna",
                desc = "Press de hombros con mancuernas para desarrollar el deltoides.",
                steps = "1. Siéntate con respaldo vertical, mancuernas a la altura de los hombros.\n2. Palmas hacia adelante.\n3. Empuja las mancuernas hacia arriba hasta casi extender los codos.\n4. Baja de forma controlada.",
                tips = "No bloquees los codos arriba. Codos ligeramente por delante del cuerpo.", fromRoutine = true),
            ex("Elevaciones Laterales", "Dumbbell Lateral Raise", "Hombros", "Deltoides Lateral",
                equipment = "Mancuerna",
                desc = "El mejor ejercicio para el deltoides lateral y dar anchura a los hombros.",
                steps = "1. De pie con una mancuerna en cada mano a los lados.\n2. Con el codo ligeramente flexionado, sube los brazos lateralmente.\n3. Hasta que queden paralelos al suelo o ligeramente por encima.\n4. Baja lentamente.",
                tips = "El meñique ligeramente más alto que el pulgar. Usa poco peso.", fromRoutine = true),

            // HOMBROS extra
            ex("Press Militar con Barra", "Barbell Overhead Press", "Hombros", "Deltoides",
                secondary = "Tríceps,Trapecio,Core",
                equipment = "Barra",
                desc = "Press de hombros con barra. Ejercicio compuesto básico para el deltoides.",
                steps = "1. Barra a la altura de los hombros, agarre a la anchura de los hombros.\n2. Empuja la barra hacia arriba extendiendo los brazos.\n3. Mueve la cabeza hacia atrás al subir la barra.\n4. Baja de forma controlada.",
                tips = "Activa el core. No arquees la zona lumbar excesivamente."),
            ex("Press Arnold", "Arnold Press", "Hombros", "Deltoides",
                secondary = "Tríceps",
                equipment = "Mancuerna",
                desc = "Variante del press de hombros con rotación para trabajar todos los fascículos del deltoides.",
                steps = "1. Mancuernas frente a los hombros, palmas hacia ti.\n2. Al subir, rota las palmas hacia afuera.\n3. Termina con los brazos extendidos y palmas hacia adelante.\n4. Invierte el movimiento al bajar.",
                tips = "Movimiento lento y controlado en la rotación."),
            ex("Elevaciones Frontales", "Front Raises", "Hombros", "Deltoides Anterior",
                equipment = "Mancuerna",
                desc = "Aislamiento del deltoides anterior levantando mancuernas al frente.",
                steps = "1. De pie con mancuernas frente a los muslos.\n2. Sube un brazo (o ambos) hacia adelante hasta la altura de los ojos.\n3. Baja de forma controlada.",
                tips = "No uses impulso. Peso ligero y control total."),
            ex("Elevaciones Laterales en Cable", "Cable Lateral Raise", "Hombros", "Deltoides Lateral",
                equipment = "Cable",
                desc = "Elevaciones laterales en cable para tensión constante en el deltoides.",
                steps = "1. Polea baja a un lado.\n2. Agarra con la mano contraria cruzando por delante.\n3. Eleva el brazo lateralmente hasta la altura del hombro.\n4. Baja de forma controlada.",
                tips = "La tensión constante del cable es superior a las mancuernas para este ejercicio."),

            // ── PECHO (from user routine) ──
            ex("Press en Smith Machine", "Smith Machine Bench Press", "Pecho", "Pectorales",
                secondary = "Tríceps,Deltoides Anterior",
                equipment = "Smith Machine",
                desc = "Press de pecho en máquina Smith. Guiado para mayor seguridad.",
                steps = "1. Túmbate en el banco bajo la barra de la Smith Machine.\n2. Agarre a más de la anchura de los hombros.\n3. Desbloquea girando y baja la barra hacia el pecho.\n4. Empuja hacia arriba contrayendo los pectorales.",
                tips = "La Smith Machine permite trabajar más seguro sin compañero.", fromRoutine = true),
            ex("Press Inclinado en Smith Machine", "Incline Smith Machine Bench Press", "Pecho", "Pectorales Superiores",
                secondary = "Tríceps,Deltoides Anterior",
                equipment = "Smith Machine",
                desc = "Press inclinado en Smith Machine para enfatizar el pectoral superior.",
                steps = "1. Banco inclinado a 30-45° bajo la barra de la Smith Machine.\n2. Baja la barra al tercio superior del pecho.\n3. Empuja hacia arriba contrayendo el pectoral superior.",
                tips = "Ángulo de 30-45° para máxima activación del pectoral superior.", fromRoutine = true),

            // PECHO extra
            ex("Press de Banca con Barra", "Barbell Bench Press", "Pecho", "Pectorales",
                secondary = "Tríceps,Deltoides Anterior",
                equipment = "Barra",
                desc = "El press de pecho clásico. Uno de los tres grandes levantamientos.",
                steps = "1. Túmbate en el banco, barra sobre el pecho.\n2. Agarre a más de la anchura de los hombros.\n3. Baja la barra al pecho de forma controlada.\n4. Empuja hacia arriba sin rebotar.",
                tips = "Pies apoyados en el suelo. Muñecas neutras. Arco natural en lumbar."),
            ex("Press de Banca Inclinado", "Incline Barbell Bench Press", "Pecho", "Pectorales Superiores",
                secondary = "Tríceps,Deltoides Anterior",
                equipment = "Barra",
                desc = "Press de pecho inclinado con barra para el pectoral superior.",
                steps = "1. Banco a 30-45°.\n2. Baja la barra hacia la parte superior del pecho.\n3. Empuja hacia arriba.",
                tips = "No subas el ángulo más de 45° o el trabajo pasa a los hombros."),
            ex("Aperturas con Mancuernas", "Dumbbell Chest Fly", "Pecho", "Pectorales",
                equipment = "Mancuerna",
                desc = "Aislamiento del pectoral con mancuernas para mayor estiramiento.",
                steps = "1. Túmbate con una mancuerna en cada mano, brazos extendidos.\n2. Baja los brazos en arco abriendo el pecho, codos ligeramente flexionados.\n3. Vuelve a juntar las mancuernas en la cima apretando el pectoral.",
                tips = "Codos siempre ligeramente flexionados. No bajes demasiado."),
            ex("Cable Crossover", "Cable Crossover", "Pecho", "Pectorales",
                secondary = "Deltoides Anterior",
                equipment = "Cable",
                desc = "Cruce de poleas para el pectoral con tensión constante.",
                steps = "1. Poleas altas a ambos lados.\n2. Un paso adelante, un cable en cada mano.\n3. Lleva los brazos hacia el centro y abajo, cruzando ligeramente.\n4. Vuelve de forma controlada.",
                tips = "Tensión constante gracias al cable. Ideal para finalizar el entreno de pecho."),
            ex("Flexiones", "Push-ups", "Pecho", "Pectorales",
                secondary = "Tríceps,Deltoides Anterior,Core",
                equipment = "Peso Corporal",
                desc = "El ejercicio de pecho más básico y versátil. Se puede hacer en cualquier lugar.",
                steps = "1. Manos a la anchura de los hombros o más separadas.\n2. Cuerpo en línea recta desde cabeza hasta talones.\n3. Baja el pecho hasta casi tocar el suelo.\n4. Empuja para volver arriba.",
                tips = "Cuanto más separadas las manos, más pecho. Más juntas, más tríceps."),
            ex("Fondos para Pecho", "Chest Dips", "Pecho", "Pectorales",
                secondary = "Tríceps,Deltoides Anterior",
                equipment = "Peso Corporal",
                desc = "Fondos inclinados hacia adelante para enfatizar el pecho.",
                steps = "1. Agárrate a las barras paralelas.\n2. Inclínate hacia adelante (45°).\n3. Baja hasta que los hombros queden por debajo de los codos.\n4. Empuja para volver arriba.",
                tips = "La inclinación hacia adelante es clave para activar el pecho."),

            // ── BÍCEPS (from user routine) ──
            ex("Curl Alterno con Mancuerna", "Alternating Dumbbell Biceps Curl", "Brazos", "Bíceps",
                secondary = "Braquial,Braquiorradial",
                equipment = "Mancuerna",
                desc = "Curl de bíceps alternando brazos con mancuernas.",
                steps = "1. De pie con una mancuerna en cada mano, palmas hacia ti.\n2. Curl con un brazo llevando la palma hacia arriba al subir.\n3. Baja lentamente y repite con el otro brazo.",
                tips = "Codo fijo a un lado del cuerpo. Gira la muñeca en supinación al subir.", fromRoutine = true),
            ex("Curl con Mancuerna", "Dumbbell Biceps Curl", "Brazos", "Bíceps",
                secondary = "Braquial",
                equipment = "Mancuerna",
                desc = "Curl de bíceps bilateral con mancuernas.",
                steps = "1. De pie con mancuernas a los lados, palmas hacia adelante.\n2. Flexiona los codos llevando las mancuernas hacia los hombros.\n3. Baja de forma controlada.",
                tips = "No balancees el cuerpo. Codos pegados a los lados.", fromRoutine = true),

            // BÍCEPS extra
            ex("Curl con Barra", "Barbell Curl", "Brazos", "Bíceps",
                secondary = "Braquial,Braquiorradial",
                equipment = "Barra",
                desc = "Curl de bíceps con barra. Permite usar más peso para mayor sobrecarga.",
                steps = "1. De pie con la barra, palmas hacia arriba, agarre a la anchura de los hombros.\n2. Flexiona los codos llevando la barra hacia los hombros.\n3. Aprieta los bíceps en la cima.\n4. Baja de forma controlada.",
                tips = "Codos fijos a los lados. Muñecas neutras."),
            ex("Curl Martillo", "Hammer Curl", "Brazos", "Braquiorradial",
                secondary = "Bíceps,Braquial",
                equipment = "Mancuerna",
                desc = "Curl con agarre neutro que enfatiza el braquiorradial y braquial.",
                steps = "1. De pie con mancuernas, palmas hacia dentro durante todo el movimiento.\n2. Sube las mancuernas como un martillo.\n3. Baja de forma controlada.",
                tips = "Movimiento limpio sin balanceo. Trabaja también el antebrazo."),
            ex("Curl en Predicador", "Preacher Curl", "Brazos", "Bíceps",
                secondary = "Braquial",
                equipment = "Máquina",
                desc = "Curl en banco predicador que aísla el bíceps eliminando el balanceo.",
                steps = "1. Apoya los brazos en el banco predicador.\n2. Agarra la barra o mancuernas con palmas hacia arriba.\n3. Sube sin levantar los brazos del banco.\n4. Baja completamente para el estiramiento.",
                tips = "No levantes los brazos del banco. Rango completo para máximo estiramiento."),
            ex("Curl de Concentración", "Concentration Curl", "Brazos", "Bíceps",
                equipment = "Mancuerna",
                desc = "Curl sentado con el codo apoyado en la rodilla para máxima contracción.",
                steps = "1. Siéntate en un banco, codo apoyado en la rodilla interior.\n2. Sube la mancuerna rotando la muñeca.\n3. Aprieta el bíceps en la cima.\n4. Baja lentamente.",
                tips = "Ideal para la conexión mente-músculo con el bíceps."),

            // ── TRÍCEPS (from user routine) ──
            ex("Extensión de Tríceps en Cable Tumbado", "Cable Lying Triceps Extension", "Brazos", "Tríceps",
                secondary = "Anconeo",
                equipment = "Cable",
                desc = "Extensión de tríceps en banco con cable. Tensión constante en el tríceps.",
                steps = "1. Túmbate en un banco frente a la polea baja.\n2. Agarra la barra por encima de la cabeza.\n3. Extiende los codos llevando la barra hacia las espinillas.\n4. Vuelve controladamente sin mover los codos.",
                tips = "Codos fijos apuntando hacia arriba. Movimiento solo de codos.", fromRoutine = true),

            // TRÍCEPS extra
            ex("Extensión de Tríceps en Polea", "Tricep Pushdown", "Brazos", "Tríceps",
                secondary = "Anconeo",
                equipment = "Cable",
                desc = "Pushdown de tríceps en polea alta con cuerda o barra.",
                steps = "1. Polea alta, agarra la cuerda o barra.\n2. Codos pegados a los lados, empuja hacia abajo.\n3. Extiende completamente los codos.\n4. Vuelve controladamente.",
                tips = "Separa la cuerda al final para mayor contracción. Codos fijos."),
            ex("Skull Crushers", "Skull Crushers", "Brazos", "Tríceps",
                equipment = "Barra",
                desc = "Extensión de tríceps con barra EZ tumbado. Máximo estiramiento del tríceps.",
                steps = "1. Túmbate con la barra EZ sobre el pecho.\n2. Levanta la barra con los brazos extendidos.\n3. Baja la barra hacia la frente flexionando solo los codos.\n4. Extiende los codos para volver.",
                tips = "Codos apuntando hacia el techo durante todo el movimiento."),
            ex("Extensión de Tríceps sobre la Cabeza", "Overhead Tricep Extension", "Brazos", "Tríceps",
                equipment = "Mancuerna",
                desc = "Extensión de tríceps sobre la cabeza para máximo estiramiento del tríceps largo.",
                steps = "1. De pie o sentado, mancuerna sobre la cabeza con ambas manos.\n2. Baja la mancuerna por detrás de la cabeza flexionando los codos.\n3. Extiende los codos hacia arriba.",
                tips = "El codo cercano a la cabeza activa mejor la porción larga del tríceps."),
            ex("Fondos para Tríceps", "Tricep Dips", "Brazos", "Tríceps",
                secondary = "Deltoides Anterior,Pectorales",
                equipment = "Peso Corporal",
                desc = "Fondos en banco o barras paralelas con el cuerpo erguido para tríceps.",
                steps = "1. Manos en el banco o barras, cuerpo erguido.\n2. Baja flexionando los codos hasta 90°.\n3. Empuja para volver arriba.",
                tips = "Cuerpo vertical para más tríceps. Inclinado hacia adelante = más pecho."),

            // ── CORE ──
            ex("Plancha", "Plank", "Core", "Core",
                secondary = "Zona Lumbar,Hombros",
                equipment = "Peso Corporal",
                desc = "Ejercicio isométrico fundamental para fortalecer todo el core.",
                steps = "1. Apóyate en los antebrazos y dedos de los pies.\n2. Cuerpo en línea recta.\n3. Mantén la posición el tiempo indicado.\n4. No eleves ni bajes las caderas.",
                tips = "Activa el ombligo hacia la columna. Respira de forma controlada."),
            ex("Crunches", "Crunches", "Core", "Abdominales",
                equipment = "Peso Corporal",
                desc = "El crunch abdominal básico. Movimiento controlado de flexión de columna.",
                steps = "1. Túmbate boca arriba, rodillas flexionadas.\n2. Manos detrás de la cabeza o cruzadas en el pecho.\n3. Contrae el abdomen levantando los omóplatos del suelo.\n4. Baja de forma controlada.",
                tips = "No tires del cuello. El movimiento es de la columna, no de la cabeza."),
            ex("Giros Rusos", "Russian Twists", "Core", "Oblicuos",
                secondary = "Abdominales",
                equipment = "Peso Corporal",
                desc = "Rotación de tronco sentado para trabajar los oblicuos.",
                steps = "1. Siéntate con las rodillas flexionadas, torso inclinado 45°.\n2. Junta las manos o sujeta un peso.\n3. Rota el tronco llevando las manos de lado a lado.",
                tips = "Levanta los pies para mayor dificultad."),
            ex("Elevaciones de Piernas", "Leg Raises", "Core", "Abdominales Inferiores",
                secondary = "Flexores de Cadera",
                equipment = "Peso Corporal",
                desc = "Elevaciones de piernas para trabajar el abdomen inferior.",
                steps = "1. Túmbate boca arriba con las manos bajo los glúteos.\n2. Con las piernas juntas y ligeramente flexionadas, súbelas hasta 90°.\n3. Baja de forma controlada sin tocar el suelo.",
                tips = "No arquees la zona lumbar. Baja lentamente para más activación."),
            ex("Rueda Abdominal", "Ab Wheel Rollout", "Core", "Core",
                secondary = "Zona Lumbar,Hombros,Bíceps",
                equipment = "Rueda Abdominal",
                desc = "Ejercicio avanzado de core con rueda abdominal.",
                steps = "1. Arrodíllate con la rueda delante.\n2. Empuja la rueda hacia adelante extendiendo los brazos.\n3. Llega lo más lejos posible sin hundir las caderas.\n4. Vuelve a la posición inicial usando el core.",
                tips = "Empieza con un rango corto y aumenta progresivamente."),
            ex("Mountain Climbers", "Mountain Climbers", "Core", "Core",
                secondary = "Hombros,Flexores de Cadera",
                equipment = "Peso Corporal",
                desc = "Ejercicio dinámico de core y cardio en posición de plancha.",
                steps = "1. Posición de plancha alta.\n2. Lleva una rodilla hacia el pecho.\n3. Vuelve y repite con la otra pierna alternando rápidamente.",
                tips = "Caderas bajas. Cuanto más rápido, mayor componente cardiovascular."),
            ex("Plancha Lateral", "Side Plank", "Core", "Oblicuos",
                secondary = "Core",
                equipment = "Peso Corporal",
                desc = "Plancha lateral para trabajar los oblicuos de forma isométrica.",
                steps = "1. Apóyate en un antebrazo y el borde del pie.\n2. Cuerpo en línea recta lateral.\n3. Mantén la posición el tiempo indicado.",
                tips = "No dejes que caigan las caderas."),

            // ── CARDIO ──
            ex("Cardio", "Cardio", "Cardio", "Sistema Cardiovascular",
                equipment = "Cardio",
                desc = "Sesión de cardio general. Puede ser cinta, bicicleta, elíptica u otro.",
                steps = "1. Elige tu máquina de cardio.\n2. Calienta 2-3 minutos a ritmo suave.\n3. Realiza el cardio al ritmo y duración indicados.\n4. Enfría 2-3 minutos.",
                tips = "Zona 2 (conversación posible) para cardio base. Zona 4-5 para HIIT.", fromRoutine = true),
            ex("Cinta de Correr", "Treadmill", "Cardio", "Sistema Cardiovascular",
                equipment = "Cardio",
                desc = "Carrera o caminata en cinta de correr.",
                steps = "1. Comienza con una velocidad de calentamiento.\n2. Aumenta progresivamente.\n3. Mantén la cadencia y postura correctas.",
                tips = "No te agarres a los pasamanos. Postura erguida."),
            ex("Bicicleta Estática", "Stationary Bike", "Cardio", "Sistema Cardiovascular",
                equipment = "Cardio",
                desc = "Cardio de bajo impacto en bicicleta estática.",
                steps = "1. Ajusta el sillín a la altura correcta (rodilla casi extendida abajo).\n2. Pedalea a ritmo constante o varía la intensidad.",
                tips = "Sin impacto articular. Ideal para recuperación activa."),
            ex("Elíptica", "Elliptical", "Cardio", "Sistema Cardiovascular",
                equipment = "Cardio",
                desc = "Cardio de bajo impacto en máquina elíptica.",
                steps = "1. Agarra los manillares.\n2. Movimiento elíptico coordinando brazos y piernas.\n3. Mantén el ritmo constante.",
                tips = "La elíptica combina el movimiento de correr con el remo."),
            ex("Comba / Saltar la Cuerda", "Jump Rope", "Cardio", "Sistema Cardiovascular",
                equipment = "Comba",
                desc = "Salto a la comba. Excelente cardio de alta intensidad.",
                steps = "1. Sostén la comba a los lados.\n2. Gira la muñeca para balancear la comba.\n3. Salta con ambos pies o alternando.\n4. Mantén el ritmo constante.",
                tips = "Aterriza suavemente sobre los metatarsos. Activa el core."),
            ex("Remo en Máquina", "Rowing Machine", "Cardio", "Sistema Cardiovascular",
                secondary = "Espalda,Piernas,Brazos",
                equipment = "Cardio",
                desc = "Remo en ergómetro. Cardio de cuerpo completo muy eficiente.",
                steps = "1. Siéntate en el remo, pies en las trabillas.\n2. Empuja con las piernas primero.\n3. Luego inclínate hacia atrás.\n4. Finalmente tira con los brazos.\n5. Invierte el orden para volver.",
                tips = "Secuencia: piernas 60%, cadera 20%, brazos 20%."),
            ex("HIIT", "HIIT", "Cardio", "Sistema Cardiovascular",
                equipment = "Peso Corporal",
                desc = "Entrenamiento de alta intensidad por intervalos.",
                steps = "1. Calienta 3-5 minutos.\n2. Realiza 20-40 segundos a máxima intensidad.\n3. Descansa 10-20 segundos.\n4. Repite 8-12 rondas.\n5. Enfría 3-5 minutos.",
                tips = "El HIIT mejora el VO2max y quema más calorías que el cardio estable.")
        )

        exercises.forEach { exercise ->
            val id = db.exerciseDao().insertExercise(exercise)
            ids[exercise.name] = id
        }

        // ── WORKOUTS ──
        data class WE(val name: String, val sets: Int, val reps: String, val rest: Int = 90, val notes: String = "")

        data class WorkoutDef(val workout: Workout, val exercises: List<WE>)

        val workoutDefs = listOf(
            WorkoutDef(
                Workout(name = "Día A – Piernas", description = "Tu rutina de piernas: Cuádriceps, Isquiotibiales, Gemelos",
                    dayLabel = "Día A", colorHex = "#FF4CAF50", estimatedMinutes = 70, isUserRoutine = true, orderIndex = 0),
                listOf(
                    WE("Cardio", 1, "10 min", 0, "Calentamiento cardiovascular en zona 2 (ritmo de conversación). Bici estática o cinta a ritmo suave. Activa la musculatura de piernas antes de los ejercicios compuestos."),
                    WE("Extensión de Cuádriceps", 4, "10", 90, "Contrae 1 seg en la cima para máxima activación del cuádriceps. Primero en la sesión para una pre-fatiga controlada — llegará a sentadilla con los cuádriceps ya activados."),
                    WE("Sentadilla", 3, "10", 120, "El Rey. Profundidad completa — muslos paralelos al suelo mínimo. Talones plantados. Rodillas siguiendo la dirección de los dedos del pie."),
                    WE("Curl Femoral", 4, "10", 90, "Bajada lenta (3 seg eccéntrico). No levantes las caderas del banco. Contrae los isquios en la cima."),
                    WE("Prensa de Piernas", 3, "10-20", 120, "Después de sentadilla: más volumen de cuádriceps con menor fatiga sistémica. Rango completo sin bloquear las rodillas arriba."),
                    WE("Elevación de Talones de Pie", 4, "10", 60, "Rango completo imprescindible: estiramiento total abajo, contracción máxima arriba. Los gemelos responden al volumen, no al peso máximo.")
                )
            ),
            WorkoutDef(
                Workout(name = "Día B – Espalda y Hombros", description = "Tu rutina de espalda, deltoides posterior y hombros",
                    dayLabel = "Día B", colorHex = "#FF2196F3", estimatedMinutes = 75, isUserRoutine = true, orderIndex = 1),
                listOf(
                    WE("Cardio", 1, "7 min", 0, "Calentamiento en zona 2. Elíptica o bici a ritmo ligero para activar el tren superior sin agotar las reservas energéticas."),
                    WE("Jalón al Pecho", 4, "8-10", 90, "Primer ejercicio de tirón. Lleva los codos hacia abajo y hacia atrás — no tires con los bíceps. Retracción escapular antes de cada repetición."),
                    WE("Jalón Agarre Cerrado", 4, "10", 90, "El agarre supino (palmas hacia ti) recluta más el bíceps como asistente. Buen complemento al jalón con agarre abierto."),
                    WE("Remo con Peso", 4, "10", 90, "Retracta los omóplatos antes de tirar. El movimiento empieza en la espalda, no en los brazos."),
                    WE("Cable Reverse Fly de Pie", 3, "6-10", 60, "Peso ligero y técnica perfecta. Esencial para el deltoides posterior y la salud del manguito rotador. No lo saltes."),
                    WE("Press con Mancuernas", 4, "8-10", 90, "Codos ligeramente por delante del cuerpo para proteger el manguito rotador. No bloquees los codos arriba."),
                    WE("Elevaciones Laterales", 3, "10", 60, "El músculo más difícil de activar. Meñique ligeramente más alto que el pulgar. Sin impulso de ningún tipo.")
                )
            ),
            WorkoutDef(
                Workout(name = "Día C – Pecho y Brazos", description = "Tu rutina de pecho, bíceps y tríceps",
                    dayLabel = "Día C", colorHex = "#FFFF5722", estimatedMinutes = 75, isUserRoutine = true, orderIndex = 2),
                listOf(
                    WE("Cardio", 1, "10 min", 0, "Calentamiento cardiovascular en zona 2. Cinta o bici a ritmo suave para activar el pecho y los brazos antes de los ejercicios compuestos."),
                    WE("Press en Smith Machine", 4, "10", 90, "La guía de la Smith permite enfocarse totalmente en la técnica. Baja la barra al pecho medio-inferior. Agarre ligeramente más ancho que los hombros."),
                    WE("Press Inclinado en Smith Machine", 4, "8-10", 90, "30-45° de inclinación para el pectoral superior — el punto débil de la mayoría. Priorizarlo al inicio de la sesión da mejores resultados."),
                    WE("Curl Alterno con Mancuerna", 4, "6-10", 60, "Alterna brazos para mayor concentración en cada repetición. Gira la muñeca en supinación al subir. Codo fijo al costado del cuerpo."),
                    WE("Curl con Mancuerna", 4, "8-10", 60, "Bilateral para más sobrecarga total. Codos pegados a los lados durante todo el movimiento. Controla la bajada."),
                    WE("Extensión de Tríceps en Cable Tumbado", 4, "10", 60, "La posición de cable crea tensión constante en todo el rango de movimiento. Codos fijos apuntando al techo durante toda la ejecución.")
                )
            ),
            WorkoutDef(
                Workout(name = "Cuerpo Completo Básico", description = "Entrena todo el cuerpo en una sola sesión",
                    dayLabel = "Full Body", colorHex = "#FF9C27B0", estimatedMinutes = 60, isUserRoutine = false, orderIndex = 3),
                listOf(
                    WE("Sentadilla", 3, "10", 90),
                    WE("Press de Banca con Barra", 3, "10", 90),
                    WE("Peso Muerto", 3, "8", 120),
                    WE("Press Militar con Barra", 3, "10", 90),
                    WE("Curl con Barra", 3, "10", 60),
                    WE("Extensión de Tríceps en Polea", 3, "12", 60),
                    WE("Plancha", 3, "30-60 seg", 45)
                )
            ),
            WorkoutDef(
                Workout(name = "Push – Empuje", description = "Pecho, hombros y tríceps",
                    dayLabel = "Push", colorHex = "#FFFF9800", estimatedMinutes = 65, isUserRoutine = false, orderIndex = 4),
                listOf(
                    WE("Press de Banca con Barra", 4, "8-10", 90),
                    WE("Press Inclinado en Smith Machine", 3, "10", 90),
                    WE("Press Militar con Barra", 3, "10", 90),
                    WE("Elevaciones Laterales", 3, "12-15", 60),
                    WE("Skull Crushers", 3, "10", 75),
                    WE("Extensión de Tríceps en Polea", 3, "12", 60)
                )
            ),
            WorkoutDef(
                Workout(name = "Pull – Tirón", description = "Espalda y bíceps",
                    dayLabel = "Pull", colorHex = "#FF00BCD4", estimatedMinutes = 65, isUserRoutine = false, orderIndex = 5),
                listOf(
                    WE("Jalón al Pecho", 4, "10", 90),
                    WE("Remo con Barra", 4, "8-10", 90),
                    WE("Remo con Peso", 3, "10", 90),
                    WE("Face Pull", 3, "15", 60),
                    WE("Curl con Barra", 3, "10", 60),
                    WE("Curl Martillo", 3, "10", 60)
                )
            ),
            WorkoutDef(
                Workout(name = "Core y Cardio", description = "Abdominales y cardiovascular",
                    dayLabel = "Core", colorHex = "#FF607D8B", estimatedMinutes = 40, isUserRoutine = false, orderIndex = 6),
                listOf(
                    WE("Bicicleta Estática", 1, "15 min", 0),
                    WE("Plancha", 3, "45 seg", 45),
                    WE("Crunches", 3, "20", 45),
                    WE("Giros Rusos", 3, "20", 45),
                    WE("Elevaciones de Piernas", 3, "15", 60),
                    WE("Mountain Climbers", 3, "30 seg", 30)
                )
            )
        )

        workoutDefs.forEach { def ->
            val workoutId = db.workoutDao().insertWorkout(def.workout)
            def.exercises.forEachIndexed { idx, we ->
                val exerciseId = ids[we.name] ?: return@forEachIndexed
                db.workoutDao().insertWorkoutExercise(
                    WorkoutExercise(
                        workoutId = workoutId,
                        exerciseId = exerciseId,
                        orderIndex = idx,
                        targetSets = we.sets,
                        targetReps = we.reps,
                        restSeconds = we.rest,
                        notes = we.notes
                    )
                )
            }
        }
    }

    // Patches coach notes for existing installations (safe to run every launch — only updates empty notes)
    suspend fun patchNotes(db: AppDatabase) {
        val notes = mapOf(
            "Cardio" to "Calentamiento cardiovascular en zona 2 (ritmo de conversación). Bici estática, cinta o elíptica a ritmo suave. La duración está indicada en la ficha de la sesión.",
            "Extensión de Cuádriceps" to "Contrae 1 seg en la cima para máxima activación del cuádriceps. Primero en la sesión para una pre-fatiga controlada.",
            "Sentadilla" to "El Rey. Profundidad completa — muslos paralelos al suelo mínimo. Talones plantados. Rodillas siguiendo la dirección de los dedos del pie.",
            "Curl Femoral" to "Bajada lenta (3 seg eccéntrico). No levantes las caderas del banco. Contrae los isquios en la cima.",
            "Prensa de Piernas" to "Después de sentadilla: más volumen de cuádriceps con menor fatiga sistémica. Rango completo sin bloquear las rodillas arriba.",
            "Elevación de Talones de Pie" to "Rango completo imprescindible: estiramiento total abajo, contracción máxima arriba. Los gemelos responden al volumen, no al peso máximo.",
            "Jalón al Pecho" to "Lleva los codos hacia abajo y hacia atrás — no tires con los bíceps. Retracción escapular antes de cada repetición.",
            "Jalón Agarre Cerrado" to "El agarre supino (palmas hacia ti) recluta más el bíceps como asistente. Buen complemento al jalón con agarre abierto.",
            "Remo con Peso" to "Retracta los omóplatos antes de tirar. El movimiento empieza en la espalda, no en los brazos.",
            "Cable Reverse Fly de Pie" to "Peso ligero y técnica perfecta. Esencial para el deltoides posterior y la salud del manguito rotador. No lo saltes.",
            "Press con Mancuernas" to "Codos ligeramente por delante del cuerpo para proteger el manguito rotador. No bloquees los codos arriba.",
            "Elevaciones Laterales" to "El músculo más difícil de activar. Meñique ligeramente más alto que el pulgar. Sin impulso de ningún tipo.",
            "Press en Smith Machine" to "La guía de la Smith permite enfocarse totalmente en la técnica. Baja la barra al pecho medio-inferior.",
            "Press Inclinado en Smith Machine" to "30-45° de inclinación para el pectoral superior — el punto débil de la mayoría. Priorizarlo al inicio de la sesión da mejores resultados.",
            "Curl Alterno con Mancuerna" to "Alterna brazos para mayor concentración en cada repetición. Gira la muñeca en supinación al subir. Codo fijo al costado.",
            "Curl con Mancuerna" to "Bilateral para más sobrecarga total. Codos pegados a los lados durante todo el movimiento. Controla la bajada.",
            "Extensión de Tríceps en Cable Tumbado" to "La posición de cable crea tensión constante en todo el rango de movimiento. Codos fijos apuntando al techo durante toda la ejecución."
        )
        notes.forEach { (name, note) ->
            val exercise = db.exerciseDao().getExerciseByName(name) ?: return@forEach
            db.workoutDao().patchNotesForExercise(exercise.id, note)
        }
    }
}
