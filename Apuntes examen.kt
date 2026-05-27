TODO 1: Crear la tabla de la base de datos
override fun onCreate(db: SQLiteDatabase) {
    val sql = """
        CREATE TABLE $NOMBRE_TABLA (
            $COLUMNA_ID INTEGER PRIMARY KEY,
            $COLUMNA_CODIGO TEXT,
            $COLUMNA_TITULO TEXT,
            $COLUMNA_SALA TEXT,
            $COLUMNA_TIPO TEXT,
            $COLUMNA_PATROCINADOR TEXT,
            $COLUMNA_AUDIOGUIA_ESCUCHADA INTEGER
        )
    """.trimIndent()
    db.execSQL(sql)
}


TODO 2: Guardar el patrocinador controlando si es nulo
put(COLUMNA_PATROCINADOR, visita.patrocinador)


TODO 3: Consultar y ordenar los registros
val sqlConsulta = "SELECT * FROM $NOMBRE_TABLA ORDER BY $COLUMNA_ID"


TODO 4: Leer el patrocinador desde el Cursor
val patrocinador = if (cursor.isNull(5)) null else cursor.getString(5)


TODO 5: Actualizar la audioguía a "escuchada"
val valores = ContentValues().apply {
    put(COLUMNA_AUDIOGUIA_ESCUCHADA, 1)
}


TODO 6: Construir la URL con parámetros codificados
val codigoCodificado = URLEncoder.encode(codigo, "UTF-8")
val rutaCompleta = "$URL_SERVIDOR_MUSEO?peticion=exposicion&codigo=$codigoCodificado"


TODO 7: Parsear la respuesta JSON del servidor
val json = JSONObject(respuesta)
return InfoExposicionServidor(
    codigo = json.getString("codigo"),
    titulo = json.getString("titulo"),
    sala = json.getString("sala"),
    tipo = json.getString("tipo"),
    patrocinador = patrocinador,
    disponible = json.getBoolean("disponible")
)


TODO 8: Implementar la petición HTTP GET nativa
var conexion: HttpURLConnection? = null
try {
    val url = URL(rutaCompleta)
    conexion = url.openConnection() as HttpURLConnection
    conexion.requestMethod = "GET"
    conexion.connectTimeout = 5000
    conexion.readTimeout = 5000

    if (conexion.responseCode == HttpURLConnection.HTTP_OK) {
        val lector = BufferedReader(InputStreamReader(conexion.inputStream))
        val resultado = StringBuilder()
        var linea: String?
        while (lector.readLine().also { linea = it } != null) {
            resultado.append(linea)
        }
        lector.close()
        return@withContext resultado.toString()
    } else {
        throw IOException("Error en el servidor: ${conexion.responseCode}")
    }
} finally {
    conexion?.disconnect()
}


TODO 9: Validar el código de entrada antes de la corrutina
val codigo = codigoInput.trim()
if (codigo.isEmpty()) {
    onReproducirError()
    mensaje = "Error: introduce el código de una exposición."
    return
}


TODO 10: Guardar la visita obtenida en la base de datos
val nuevaVisita = VisitaExposicion(
    id = siguienteId,
    codigo = info.codigo,
    titulo = info.titulo,
    sala = info.sala,
    tipo = info.tipo,
    patrocinador = info.patrocinador,
    audioguiaEscuchada = false
)

val resultado = visitaCRUD.insertarVisita(nuevaVisita)


TODO 11: Guardar el estado del Switch asíncronamente
coroutineScope.launch {
    context.dataStore.edit { preferencias ->
        preferencias[MOSTRAR_ESCUCHADAS] = nuevoValor
    }
}


TODO 12: Calcular el porcentaje de progreso
if (duracionMilisegundos <= 0) return 0.0
return (posicionMilisegundos.toDouble() / duracionMilisegundos.toDouble()) * 100.0


TODO 14: Avanzar y retrasar el audio en intervalos de 30 segundos
fun retrasarAudio30Segundos() {
    val player = mediaPlayer ?: return
    val nuevaPosicion = (player.currentPosition - 30000).coerceAtLeast(0)
    player.seekTo(nuevaPosicion)
    posicionAudioMs = nuevaPosicion
}

fun adelantarAudio30Segundos() {
    val player = mediaPlayer ?: return
    val nuevaPosicion = (player.currentPosition + 30000).coerceAtMost(duracionAudioMs)
    player.seekTo(nuevaPosicion)
    posicionAudioMs = nuevaPosicion
}


TODO 15: Controlar la lógica de activación de los anuncios
fun comprobarAnuncios() {
    anuncioUnoVisible = posicionVideoMs >= 3000
    anuncioDosVisible = porcentajeVideo >= 50.0
}


TODO 16: Avanzar y retrasar el vídeo en intervalos de 10 segundos
fun retrasarVideo10Segundos() {
    val nuevaPosicion = (exoPlayer.currentPosition - 10000).coerceAtLeast(0L)
    exoPlayer.seekTo(nuevaPosicion)
    posicionVideoMs = nuevaPosicion.toInt()
}

fun adelantarVideo10Segundos() {
    val nuevaPosicion = (exoPlayer.currentPosition + 10000).coerceAtMost(duracionVideoMs.toLong())
    exoPlayer.seekTo(nuevaPosicion)
    posicionVideoMs = nuevaPosicion.toInt()
}




/*🗄️ 1. En la Base de Datos (SQLite)
El mecanismo para guardar cosas en el móvil nunca cambia. Lo único que varía es qué estás guardando.

Lo que se QUEDA igual (Estructura): Los comandos en mayúsculas (CREATE TABLE, SELECT * FROM, ORDER BY), la palabra ContentValues().apply, el método db.insert, y los bucles del Cursor (if (cursor.moveToFirst())).

Lo que tienes que CAMBIAR:

Las constantes de texto: Si el examen ya no va de un "museo" sino de una "tienda de coches", tendrás que cambiar "visita_exposicion" por "coches", y las columnas por COLUMNA_MARCA, COLUMNA_PRECIO, etc.

Los tipos en el Cursor: Fíjate en el orden de las columnas. Si la columna 0 es un número, usas cursor.getInt(0). Si la columna 1 es texto, usas cursor.getString(1). Solo tienes que seguir el orden que tú mismo hayas puesto en el CREATE TABLE.

🌐 2. En Internet (HTTP y JSON)
Las conexiones a un servidor son siempre un calco de sí mismas.

Lo que se QUEDA igual (Estructura): Todo el bloque de código del HttpURLConnection, los BufferedReader, el StringBuilder, y el withContext(Dispatchers.IO). Eso es código "de infraestructura", es idéntico para un museo, para la NASA o para Netflix.

Lo que tienes que CAMBIAR:

La URL base: Cambiar el enlace de la constante de arriba por el nuevo archivo .php que os dé el profesor.

Los parámetros: En ?peticion=exposicion&codigo=..., cambias los nombres de las variables por lo que te pida el enunciado (por ejemplo: ?accion=buscar&id=...).

Las llaves del JSON: Si el servidor te devuelve datos de un libro, en lugar de json.getString("titulo") o json.getString("sala"), escribirás json.getString("autor") o json.getInt("paginas"). Mira el archivo .php que os dé para saber cómo se llaman las etiquetas.

🎬 3. En los Reproductores (Audio y Vídeo)
Tanto MediaPlayer como ExoPlayer funcionan por estados que se manejan con botones.

Lo que se QUEDA igual (Estructura): Los métodos nativos de los reproductores: .start(), .pause(), .seekTo(), .isPlaying y .currentPosition. El truco del .coerceIn(...) o .coerceAtLeast() para no salirte del tiempo del audio también sirve para siempre.

Lo que tienes que CAMBIAR:

Los números de los saltos de tiempo: El tiempo en Android se mide en milisegundos. En este examen os pedían avanzar de 10 en 10 segundos (10000 ms) o de 30 en 30 (30000 ms). Si en el próximo examen te pide saltos de 5 segundos, cambias el número a 5000.

Los porcentajes de las condiciones: Si para dar por escuchada una pista te pide un 80%, tu condición es >= 80.0. Si un anuncio salta a mitad de vídeo, buscas el >= 50.0. Solo cambia el número según lo que dicte el enunciado.*/