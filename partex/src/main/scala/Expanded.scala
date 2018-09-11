package partex

object Expanded {

  sealed trait Labelable
  sealed trait Math
  sealed trait Float
  case class Document(top: TopMatter, body: Body)

  case class TopMatter(title: Option[String], date: Option[String],
    author: Option[String], abs: Option[String])

  case class Body(elems: Vector[BodyElem])

  sealed trait BodyElem
  case class Paragraph(frgs: Vector[Fragment]) extends BodyElem
  case class Command(name: String, s: String) extends BodyElem with Fragment
  case class Heading(name: String, alias: Option[String], label: Option[String],
    value: String) extends BodyElem with Labelable
  case class Graphics(src: String, width: String) extends BodyElem with Float
  case class Environment(name: String, value: Body) extends BodyElem
  case class Theorem(name: String, alias: Option[String], label: Option[String],
    value: Body) extends BodyElem with Labelable
  case class DisplayMath(label: Option[String], value: String) extends BodyElem
    with Math with Labelable
  case class CodeBlock(value: String) extends BodyElem
  case class Figure(cap: Option[Caption], incl: Graphics) extends BodyElem with Float
  case class Table(cap: Option[Caption], tb: Vector[Rows]) extends BodyElem with Float
  sealed trait TexList extends BodyElem

  case class Ordered(name: String, xs: Vector[Item]) extends TexList
  case class Unordered(name: String,xs: Vector[Item]) extends TexList
  case class Custom(name: String,xs: Vector[Item]) extends TexList
  case class Item(alias: Option[String], label: Option[String], value: Body) extends Labelable

  case class Rows(tr: Vector[Paragraph])
  case class Caption(s: String, label: Option[String]) extends Labelable

  sealed trait Fragment
  case class Text(s: String) extends Fragment
  case class InlineMath(s: String) extends Fragment with Math
  case class Quoted(s: String) extends Fragment
  case class Linked(s: String) extends Fragment
  case class References(s: String) extends Fragment
  case class Note(s: String) extends Fragment
  sealed trait Styled extends Fragment

  case class Emph(s: String) extends Styled
  case class Strong(s: String) extends Styled
  case class Italic(s: String) extends Styled
  case class Strikeout(s: String) extends Styled
  case class Superscript(s: String) extends Styled
  case class Subscript(s: String) extends Styled
  case class SmallCaps(s: String) extends Styled


}
