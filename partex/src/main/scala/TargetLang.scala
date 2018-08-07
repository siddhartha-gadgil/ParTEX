package partex

object TargetLang {

  case class Document(body: Body)
  case class Body(elems: Vector[BodyElem])
  sealed trait BodyElem

  case class Paragraph(frgs: Vector[Fragment]) extends BodyElem
  case class MathBlock(s: String) extends BodyElem
  case class Command(name: String, value: String) extends BodyElem
  case class Environment(name: String, value: Body) extends BodyElem
  case class List(xs: Vector[Body]) extends BodyElem

  sealed trait Fragment {
    val s : String
  }
  case class Text(s: String) extends Fragment
  case class InlineEq(s: String) extends Fragment
}