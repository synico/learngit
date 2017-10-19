package progscala2.implicits {
  package database_api {

    case class InvalidColumnName(name: String) extends RuntimeException(s"Invalid column name $name")

    trait Row {
      def getInt(colName: String): Int
      def getDouble(colName: String): Double
      def getText(colName: String): String
    }
  }

  package javadb {
    import database_api._

    case class JRow(representation: Map[String, Any]) extends Row {
      private def get(colName: String): Any = {
        println("call JRow.get(colName: String):Any")
        representation.getOrElse(colName, throw InvalidColumnName(colName))
      }
      def getInt(colName: String): Int = get(colName).asInstanceOf[Int]
      def getDouble(colName: String): Double = get(colName).asInstanceOf[Double]
      def getText(colName: String): String = get(colName).asInstanceOf[String]
    }

    object JRow {
      def apply(pairs: (String,Any)*) = new JRow(Map(pairs :_*))
    }
  }
}

package progscala2.implicits {
  package scaladb {
    object implicits {
      import javadb.JRow

      implicit class SRow(jrow: JRow) {
        def get[T](colName: String)(implicit toT: (JRow,String) => T): T = {
          println(s"call SRow.get[$colName]")
          toT(jrow, colName)
        }
      }

//      implicit val jrowToInt: (JRow,String) => Int = {
//            println("call implicits.jrowToInt")
//        (jrow: JRow, colName: String) => jrow.getInt(colName)
//      }
//      implicit val jrowToDouble: (JRow,String) => Double =
//        (jrow: JRow, colName: String) => jrow.getDouble(colName)
//      implicit val jrowToString: (JRow,String) => String =
//        (jrow: JRow, colName: String) => jrow.getText(colName)
    }

    object DB {
      import implicits._

      def main(args: Array[String]): Unit = {
        val row = javadb.JRow("one" -> 1, "two" -> 2.2, "three" -> "THERE!")

        val oneValue1: Int = row.get("one")
        val twoValue1: Double = row.get("two")
        val threeValue1: String = row.get("three")

        println(s"one1    -> $oneValue1")
        println(s"two1    -> $twoValue1")
        println(s"three1  -> $threeValue1")

        val oneValue2 = row.get[Int]("one")
        val twoValue2 = row.get[Double]("two")
        val threeValue2 = row.get[String]("three")

        println(s"one2    -> $oneValue2")
        println(s"two2    -> $twoValue2")
        println(s"three2  -> $threeValue2")
      }
    }
  }
}
