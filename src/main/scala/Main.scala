import sttp.client4._
import org.json4s._
import org.json4s.native.JsonMethods._

import java.io.File
import com.github.tototoshi.csv._

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneId}

object Main extends App {
  private val DEFAULT_OUTPUT_FILE = "temp/output.csv"
  private val WEATHER_REQUEST_URI = uri"https://api.openweathermap.org/data/2.5/weather?lat=45.23&lon=19.82&appid=85f41b4ae0dd98de437bed7a11bf8bad"
  private val DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"

  private val response = requestWeatherData()

  if (response.nonEmpty) {
    val parsedResponseData = parseAndExtractData(response)
    writeToCsv(parsedResponseData)
  }

  def writeToCsv(data: Map[String, Any]): Unit = {
    val outputFile = new File(DEFAULT_OUTPUT_FILE)

    if(outputFile.getParentFile.ne(null) && !outputFile.getParentFile.exists()) {
      outputFile.getParentFile.mkdirs()
    }

    val writer = CSVWriter.open(outputFile, append = true)

    if(outputFile.length() == 0) writer.writeRow(data.keys.toSeq)

    writer.writeRow(data.values.toSeq)
    writer.close()
  }

  def requestWeatherData(): String = {
    val request = basicRequest
      .get(WEATHER_REQUEST_URI)
    val backend = DefaultSyncBackend()
    val response = request.send(backend)

    response.body match {
      case Right(bodyString) =>
        bodyString
      case Left(_) => ""
    }
  }

  def parseAndExtractData(data: String): Map[String, Any] = {
    implicit val formats: DefaultFormats.type = DefaultFormats
    val parsed = parse(data)

    Map(
      "cityName" -> (parsed \ "name").extract[String],
      "lat" -> (parsed \ "coord" \ "lat").extract[Float],
      "lon" -> (parsed \ "coord" \ "lon").extract[Float],
      "date" -> tsToLocalDateTimeFormated((parsed \ "dt").extract[String]),
      "temp" -> (parsed \ "main" \ "temp").extract[Float],
      "feelsLike" -> (parsed \ "main" \ "feels_like").extract[Float],
      "tempMin" -> (parsed \ "main" \ "temp_min").extract[Float],
      "tempMax" -> (parsed \ "main" \ "temp_max").extract[Float],
      "pressure" -> (parsed \ "main" \ "pressure").extract[Float],
      "humidity" -> (parsed \ "main" \ "humidity").extract[Float],
      "seaLevel" -> (parsed \ "main" \ "sea_level").extract[Float],
      "sunrise" -> tsToLocalDateTimeFormated((parsed \ "sys" \ "sunrise").extract[String]),
      "sunset" -> tsToLocalDateTimeFormated((parsed \ "sys" \ "sunset").extract[String]),
    )
  }

  def dateTimeToDefaultFormat(dateTime: LocalDateTime): String = {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern (DEFAULT_DATE_TIME_FORMAT)
    dateTime.format(formatter)
  }

  def tsStringToLocalDateTime(ts: String): LocalDateTime = {
    val instant = Instant.ofEpochSecond(ts.toLong)
    LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
  }

  def tsToLocalDateTimeFormated(ts: String): String = {
    dateTimeToDefaultFormat(tsStringToLocalDateTime(ts))
  }
}


