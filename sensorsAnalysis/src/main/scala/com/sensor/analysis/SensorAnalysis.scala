package com.sensor.analysis

import cats.effect.kernel.Resource
import cats.effect.{ExitCode, IO, IOApp}
import com.github.tototoshi.csv.CSVReader
import cats.implicits._

import java.io.File

object SensorAnalysis extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    runScript(args.head)
      .flatMap(s => IO.println(s.prettyString))
      .as(ExitCode.Success)
  }

  def runScript(path: String): IO[SystemOverview] = {
    csvReadersResource(path).use { csvList =>
      val systemOverviewIOSeq: IO[List[SystemOverview]] = csvList.parTraverse {
        csv =>
          IO {
            csv
              .toLazyList()
              .tail
              .map(SensorStatistics.from)
              .foldLeft(SystemOverview(1, Map()))(_ + _)
          }
      }

      systemOverviewIOSeq.map(_.reduceLeft(_ ++ _))
    }
  }

  private def csvReadersResource(
      path: String
  ): Resource[IO, List[CSVReader]] = {
    val directory = new File(path)
    val csvReadersIO = IO {
      directory
        .listFiles { (file: File, name: String) =>
          name.toLowerCase.endsWith(".csv")
        }
        .toList
        .map(CSVReader.open)
    }

    val csvReadersResource =
      Resource.make(csvReadersIO)(f => IO(f.foreach(_.close)))
    csvReadersResource
  }
}
