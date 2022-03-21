package com.sensorfilegen

import java.io.PrintWriter
import scala.util.Random

object SensorFileGen {
  def main(args: Array[String]) = {
    val random = new Random()
    val path = args.head
    (1 to 6).foreach { leader =>
      val printWriter = new PrintWriter(s"$path/leader-$leader.csv")
      printWriter.println("sensor-id,humidity")

      (1 to 10000000).foreach { _ =>
        val sensor = random.nextInt(10)
        val humidity = sensor * 10 + random.nextInt(12)
        val humidityStr =
          if (humidity > sensor * 10 + 9) "NaN" else humidity.toString

        printWriter.println(s"s$sensor,$humidityStr")
      }

      printWriter.close()
    }
  }
}
