package com.sensor.analysis

import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

class SensorAnalysisSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers {
  private val pathBaseTest = getClass.getResource("/basetest").getPath
  private val pathBigFiles = getClass.getResource("/bigFiles").getPath
  private val pathVeryBigFiles = getClass.getResource("/veryBigFiles").getPath

  val baseExpected =
    s"""
       |Num of processed files: 2
       |Num of processed measurements: 7
       |Num of failed measurements: 2
       |
       |Sensors with highest avg humidity:
       |
       |sensor-id,min,avg,max
       |s2, 78,82,88
       |s1, 10,54,98
       |s3,NaN,NaN,NaN
       |""".stripMargin

  val bigExpected =
    s"""
       |Num of processed files: 6
       |Num of processed measurements: 6000000
       |Num of failed measurements: 1000526
       |
       |Sensors with highest avg humidity:
       |
       |sensor-id,min,avg,max
       |s9, 90,94.50075757727388269861634565538941,99
       |s8, 80,84.49950298011519638297787059045161,89
       |s7, 70,74.50127130840822592509674736943103,79
       |s6, 60,64.50076953019732311285444800431176,69
       |s5, 50,54.50207505412749676835832602962305,59
       |s4, 40,44.50754331273062915332696631688122,49
       |s3, 30,34.49574721026184077763014615530627,39
       |s2, 20,24.50245281283858612431583113772946,29
       |s1, 10,14.50106302626793856827146333958099,19
       |s0, 0,4.501858736059479553903345724907067,9
       |""".stripMargin

  val veryBigExpected =
    s"""
       |Num of processed files: 6
       |Num of processed measurements: 60000000
       |Num of failed measurements: 9997151
       |
       |Sensors with highest avg humidity:
       |
       |sensor-id,min,avg,max
       |s9, 90,94.50138362602947592079028258265934,99
       |s8, 80,84.49981224123214518039530665033762,89
       |s7, 70,74.49923598476731499416659313632807,79
       |s6, 60,64.50010177382377252570638831692493,69
       |s5, 50,54.50104770686610034223490796061184,59
       |s4, 40,44.49760590512067682309339980274424,49
       |s3, 30,34.49966847553349437188464508680747,39
       |s2, 20,24.49906772704135389305431654575575,29
       |s1, 10,14.50171747166256735570148804888018,19
       |s0, 0,4.497583930701369910191060994111459,9
       |""".stripMargin

  "base test given as example " - {
    "works" in {
      SensorAnalysis
        .runScript(pathBaseTest)
        .map(s => s.prettyString)
        .asserting(_ shouldBe baseExpected)
    }
  }

  "bigFiles test " - {
    "works" in {
      SensorAnalysis
        .runScript(pathBigFiles)
        .map(s => s.prettyString)
        .asserting(_ shouldBe bigExpected)
    }
  }

  "veryBigFiles test " - {
    "works" in {
      SensorAnalysis
        .runScript(pathVeryBigFiles)
        .map(s => s.prettyString)
        .asserting(_ shouldBe veryBigExpected)
    }
  }
}
