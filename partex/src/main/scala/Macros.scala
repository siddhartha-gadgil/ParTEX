package partex

import fastparse.all._
import TargetLang._

object Macros {
/********************          preamble parser        ************************/

  val preambleParser: P[Map[String,(Vector[String],String)]] =
    P((!defCmd ~ AnyChar).rep ~ usrCmd).rep.map(_.toVector).map(_.toMap)
  val usrCmd: P[(String,(Vector[String],String))] =
    P( defCmd ~ name ~ argBox.? ~ (default.rep.map(_.toVector) ~ definition))
  val defCmd: P[Unit] = P("\\def" | "\\newcommand" | "\\renewcommand")
  val name: P[String] = P("{".? ~ ("\\" ~ alpha.rep(1)).! ~ "}".? )
  val argBox: P[Unit] = P("[" ~ num ~ "]" )
  val default: P[String] = P("[" ~ (!"]" ~ AnyChar).rep(1).! ~ "]")
  val definition: P[String] = P("{" ~ (!"}" ~ AnyChar).rep(1).! ~ "}")
  val alpha: P[Unit] = P( CharIn('a' to 'z') | CharIn('A' to 'Z') )
  val num: P[Unit] = P( CharIn('0' to '9').rep(1) )


  val usrCmdList: Map[String,(Vector[String],String)] =
    preambleParser.parse(SourcesIO.preamble).get.value


/***********************       resolving raw file       **********************/

  def resolve(l: String): String = {
    val res = usrCmdList.keys.foldLeft(l)(replaceIn)
    if (res == l) res else resolve(res)
  }

  def replaceIn(l: String,key: String): String =
    l.replaceAllLiterally( key, usrCmdList(key)._2 )


}
