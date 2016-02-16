package org.dianahep.scaroot

import scala.language.experimental.macros 
// import scala.reflect.macros.blackbox.Context
import scala.reflect.macros.Context

package object api {
  def scalaCodeClass(name: String, fields: Seq[(String, FieldType)], caseClass: Boolean = true): String = {
    // all Unicode letters (with marks), currency symbols, and underscores
    def wrap(x: String) =
      if (x.matches("""((\p{L}\p{M}*)|[0-9]|\p{Sc}|_)+""")) x else "`" + x + "`"

    def scalaType(x: FieldType) = x match {
      case FieldType.Byte => "Byte"
      case FieldType.Short => "Short"
      case FieldType.Int => "Int"
      case FieldType.Long => "Long"
      case FieldType.Float => "Float"
      case FieldType.Double => "Double"
      case FieldType.String => "String"
    }

    val header = (if (caseClass) "case class " else "class ") + wrap(name) + "("
    val indent = " " * header.size

    val body = fields map {case (n, t) => wrap(n) + ": " + scalaType(t)} mkString(",\n" + indent)

    header + body + ")"
  }
}

package api {
  class RootApiException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message, cause.getOrElse(null))

  sealed trait FieldType
  object FieldType {
    case object Byte extends FieldType
    case object Short extends FieldType
    case object Int extends FieldType
    case object Long extends FieldType
    case object Float extends FieldType
    case object Double extends FieldType
    case object String extends FieldType
  }

  trait RootTTreeRowBuilder[CASE] {
    def build[ID](rootTTree: RootTTreeReader[CASE, ID], row: Long): CASE
    def nameTypes: Seq[(String, FieldType)]
  }
  object RootTTreeRowBuilder {
    implicit def compileRootTTreeRowBuilder[CASE]: RootTTreeRowBuilder[CASE] = macro compileRootTTreeRowBuilderImpl[CASE]

    def compileRootTTreeRowBuilderImpl[CASE : c.WeakTypeTag](c: Context): c.Expr[RootTTreeRowBuilder[CASE]] = {
      import c.universe._
      val caseType = weakTypeOf[CASE]

      // val fields = caseType.decls.collectFirst {
      //   case m: MethodSymbol if (m.isPrimaryConstructor) => m
      // }.get.paramLists.head
      val fields = caseType.declarations.collectFirst {
        case m: MethodSymbol if (m.isPrimaryConstructor) => m
      }.get.paramss.head

      val (buildParams, nameTypes) = fields.zipWithIndex.map {case (field, index) =>
        val name = field.asTerm.name
        val leafName = name.decodedName.toString
        // val NullaryMethodType(leafType) = caseType.decl(name).typeSignature
        val NullaryMethodType(leafType) = caseType.declaration(name).typeSignature

        // use c.parse; compare to result of tb.parse on Scala 2.10 REPL and quasiquote on Scala 2.11 REPL

        val (leafMethod, t) =
          if (leafType =:= typeOf[Byte])
            (q"rootTTree.getValueLeafB", q"FieldType.Byte")
          else if (leafType =:= typeOf[Short])
            (q"rootTTree.getValueLeafS", q"FieldType.Short")
          else if (leafType =:= typeOf[Int])
            (q"rootTTree.getValueLeafI", q"FieldType.Int")
          else if (leafType =:= typeOf[Long])
            (q"rootTTree.getValueLeafL", q"FieldType.Long")
          else if (leafType =:= typeOf[Float])
            (q"rootTTree.getValueLeafF", q"FieldType.Float")
          else if (leafType =:= typeOf[Double])
            (q"rootTTree.getValueLeafD", q"FieldType.Double")
          else if (leafType =:= typeOf[String])
            (q"rootTTree.getValueLeafC", q"FieldType.String")
          else
            throw new NotImplementedError(s"no handler for type $leafType")

        (q"$leafMethod(rootTTree.getId($index), row)", q"$leafName -> $t")
      }.unzip

      c.Expr[RootTTreeRowBuilder[CASE]](q"""
        import org.dianahep.scaroot.api._
        new RootTTreeRowBuilder[$caseType] {
          def build[ID](rootTTree: RootTTreeReader[$caseType, ID], row: Long): $caseType = {
            rootTTree.setupToGetRow(row)
            new $caseType(..$buildParams)
          }
          val nameTypes = Vector(..$nameTypes)
        }
      """)
    }
  }

  abstract class RootTTreeReader[CASE, ID](rowBuilder: RootTTreeRowBuilder[CASE]) {
    def rootFileLocation: String
    def ttreeLocation: String
    def size: Long

    def get(row: Long): CASE = rowBuilder.build(this, row)

    def getId(index: Int): ID
    def setupToGetRow(row: Long): Unit
    def getValueLeafB(leaf: ID, row: Long): Byte
    def getValueLeafS(leaf: ID, row: Long): Short
    def getValueLeafI(leaf: ID, row: Long): Int
    def getValueLeafL(leaf: ID, row: Long): Long
    def getValueLeafF(leaf: ID, row: Long): Float
    def getValueLeafD(leaf: ID, row: Long): Double
    def getValueLeafC(leaf: ID, row: Long): String

    def released: Boolean
    def release(): Unit
  }

  trait RootTTreeIterator[CASE, ID] extends Iterator[CASE] {
    def rootTTreeReader: RootTTreeReader[CASE, ID]
    var row = 0L
    def hasNext = {
      val out = row < rootTTreeReader.size
      if (!out  &&  !rootTTreeReader.released)
        rootTTreeReader.release()
      out
    }
    def next() = {
      val out = rootTTreeReader.get(row)
      row += 1L
      out
    }
  }
}
