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