package com.sensor.analysis

import scala.util.Try

case class SystemOverview(
    processedFiles: Int,
    sensorsMap: Map[String, SensorStatistics]
) {
  def +(sensorStatistics: SensorStatistics): SystemOverview = {
    val sensorId = sensorStatistics.sensorId
    copy(sensorsMap =
      sensorsMap.updated(sensorId, sensorStatistics + sensorsMap.get(sensorId))
    )
  }

  def ++(that: SystemOverview): SystemOverview = {
    val allSensors = sensorsMap.keySet ++ that.sensorsMap.keySet
    val newSensorMap = allSensors.iterator.map { sensorId =>
      sensorId -> {
        sensorsMap.get(sensorId) ++ that.sensorsMap.get(sensorId)
      }.reduceLeft(_ + _)
    }.toMap

    SystemOverview(processedFiles + that.processedFiles, newSensorMap)
  }

  private lazy val sensorsMapPrettyString = {
    val (withValidMeasurements, withoutValidMeasurements) =
      sensorsMap.values.partition(_.validMeasurements > 0)

    val withStrings = withValidMeasurements.toSeq
      .sortBy(s => -s.avgMeasurement)
      .map(m =>
        s"${m.sensorId}, ${m.minMeasurement},${m.avgMeasurement},${m.maxMeasurement}"
      )
    val withoutStrings =
      withoutValidMeasurements.map(m => s"${m.sensorId},NaN,NaN,NaN")
    (withStrings ++ withoutStrings).mkString("\n")
  }

  def prettyString: String =
    s"""
       |Num of processed files: $processedFiles
       |Num of processed measurements: $processedMeasurements
       |Num of failed measurements: $failedMeasurements
       |
       |Sensors with highest avg humidity:
       |
       |sensor-id,min,avg,max
       |$sensorsMapPrettyString
       |""".stripMargin

  lazy val totalValidMeasurements: Int =
    sensorsMap.map(_._2.validMeasurements).sum
  lazy val failedMeasurements: Int = sensorsMap.map(_._2.failedMeasurements).sum
  lazy val processedMeasurements: Int =
    totalValidMeasurements + failedMeasurements
}

case class SensorStatistics(
    sensorId: String,
    validMeasurements: Int,
    failedMeasurements: Int,
    minMeasurement: Int,
    maxMeasurement: Int,
    sumOfMeasurements: Long
) {
  def +(that: SensorStatistics): SensorStatistics =
    if (sensorId == that.sensorId) {
      val totalMeasurements = validMeasurements + that.validMeasurements
      SensorStatistics(
        sensorId = sensorId,
        failedMeasurements = failedMeasurements + that.failedMeasurements,
        validMeasurements = totalMeasurements,
        minMeasurement = minMeasurement.min(that.minMeasurement),
        maxMeasurement = maxMeasurement.max(that.maxMeasurement),
        sumOfMeasurements = sumOfMeasurements + that.sumOfMeasurements
      )
    } else sys.error("Only of the same sensor can be added")

  def +(that: Option[SensorStatistics]): SensorStatistics =
    that.map { t => this + t }.getOrElse(this)

  lazy val avgMeasurement: BigDecimal =
    BigDecimal(sumOfMeasurements) / validMeasurements
}

object SensorStatistics {
  def from(seq: Seq[String]): SensorStatistics = {
    val measurement = Try(seq(1).toInt).toOption
    val validMeasurements = measurement.map(_ => 1).sum

    SensorStatistics(
      sensorId = seq.head,
      failedMeasurements = 1 - validMeasurements,
      validMeasurements = validMeasurements,
      minMeasurement = measurement.getOrElse(100),
      maxMeasurement = measurement.getOrElse(0),
      sumOfMeasurements = measurement.getOrElse(0).toLong
    )
  }
}
