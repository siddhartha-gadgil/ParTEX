/*****
STATUS - "inlineEq" fixed.
       - List(enumerate vs itemize) parsing rule is yet to be added.

*/

package partex

import fastparse.all._
import TargetLang._

object DeTeX {
  val ws = P(" "|"\n"|"\t"|"\\:")
  val alpha = P(CharIn('a' to 'z'))


  val document: P[Document] = P( ((!beginDoc ~ AnyChar).rep ~ beginDoc ~ body ~ endDoc).
    map((b: Body) => Document(b)) )
  val beginDoc: P[Unit] = P("\\begin{document}" ~ (&("\\")|ws.rep))
  val endDoc: P[Unit] = P("\\end{document}" ~ (&("\\")|ws.rep))


  val body: P[Body] = P(bodyElem.rep.map(_.toVector).map((bs: Vector[BodyElem]) => Body(bs)) )

  val bodyElem: P[BodyElem] = P(mathBlock | list | environment| paragraph | !"\\end{" ~ command)

  val mathBlock: P[MathBlock] = P((displayEnv | mathEnv | doubleDollar | sqBracket).
    map((s: String) => MathBlock(s)))
  val displayEnv: P[String] = P("\\begin{displaymath}" ~ (!"\\end{displaymath}" ~ AnyChar).rep(1).! ~
    "\\end{displaymath}")
  val mathEnv: P[String] = P("\\begin{" ~ ("equation*}"|"equation}") ~
    (!("\\end{"~("equation*}"|"equation}")) ~ AnyChar).rep(1).! ~ "\\end{"~("equation*}"|"equation}") )
  val doubleDollar : P[String] = P("$$" ~ (!"$$" ~ AnyChar).rep(1).! ~ "$$")
  val sqBracket : P[String] = P("\\[" ~ (!"\\]" ~ AnyChar).rep(1).! ~ "\\]")


  val list: P[List] = P((beginLs ~ lsItem.rep.map(_.toVector) ~ endLs).
    map((xs: Vector[Body]) => List(xs)))
  val lsItem: P[Body] = P("\\item" ~ ws.rep ~ body)
  val beginLs: P[Unit] = P("\\begin{" ~ lsType ~ "}" ~ box.rep ~ (&("\\")|ws.rep))
  val endLs: P[Unit] = P("\\end{" ~ lsType ~ "}" ~ (&("\\")|ws.rep))
  val lsType: P[Unit] = P("itemize"|"enumerate"|"description")

  val paragraph: P[Paragraph] = P(fragment.rep(1).map(_.toVector).
    map((frgs: Vector[Fragment]) => Paragraph(frgs)))

  val fragment: P[Fragment] = P(inlineEq|text)

  val text: P[Text] =
    P( (reserved.? ~ !("$"|"\\("|"\\["|"\\item") ~ !command ~ (wrapper|spSym|AnyChar.!)).rep(1).
    map(_.reduceLeft(_+_)).
    map((s: String) => Text(s)) )

  val reserved: P[Unit] = P(resvdWord|resvdCmd)
  val resvdWord: P[Unit] = P("\\" ~
    ("bigskip"|"break"|"centering"|"clearpage"|"cleardoublepage"|"footnotesize"|"hfill"|
      "indent"|"justify"|"large"|"Large"|"LARGE"|"huge"|"Huge"|
      "leftskip"|"listoffigures"|"listoftables"|"maketitle"|"medskip"|"normalsize"|"noindent"|"newline"|
      "newpage"|"parindent"|"parfillskip"|"parskip"|"par"|"raggedleft"|"ragggedright"|
      "rightskip"|"scriptsize"|"smallskip"|"small"|"tableofcontents"|"tiny"|"vfill"|"\\*"|"\\" ~ " ".rep) )
  val resvdCmd: P[Unit] = P("\\" ~
    ("hspace"|"linespread"|"setlength"|"vspace")
    ~ curlyBox.rep)
  val wrapper: P[String] = P("\\" ~ ("emph"|"lowercase"|"textbf"|"textit"|"textnormal"|
    "textrm"|"textsf"|"texttt"|"textup"|"textsl"|"textsc"|"textmd"|"textlf"|"textsubscript"|
    "textsuperscript"|"uppercase"|"underline") ~ cmdName)
  val spSym: P[String] = P("\\" ~ ("#"|"$"|"%"|"^"|"&"|"{"|"}"|"~").!)

  val inlineEq: P[InlineEq] = P((inlineEnv | singleDollar | roundBracket ).
    map((s: String)=> InlineEq(s)))
  val inlineEnv: P[String] = P("\\begin{math}" ~ (!"\\end{math}" ~ AnyChar).rep(1).! ~ "\\end{math}")
  val singleDollar : P[String] = P("$" ~ (!"$" ~ AnyChar).rep(1).! ~ "$")
  val roundBracket: P[String] = P("\\(" ~ (!"\\)" ~ AnyChar).rep(1).! ~ "\\)")

  val environment: P[Environment] = P( (begin ~ body ~ end).
    map((t:(String,Body)) => Environment(t._1,t._2)) )
  val begin: P[String] = P("\\begin" ~ cmdName ~ box.rep ~ (&("\\")|ws.rep) )
  val end: P[Unit] = P("\\end" ~ box.rep(1) ~ (&("\\")|ws.rep) )

  val command: P[Command] = P( !wrapper ~
    ("\\" ~ (alpha| "*").rep(1).! ~ cmdName ~ box.rep ~ (&("\\")|ws.rep)).
    map((t:(String,String)) => Command(t._1,t._2)) )
  val cmdName: P[String] = P("{" ~ (!"}" ~ AnyChar).rep.! ~ "}" )

  val box: P[Unit] = P(curlyBox|sqBox)
  val curlyBox: P[Unit] = P("{" ~ (!"}" ~ AnyChar).rep ~ "}" )
  val sqBox: P[Unit] = P("[" ~ (!"]" ~ AnyChar).rep ~ "]" )

}


object SourcesIO {

//  val input1 = scala.io.Source.fromFile("../grad-school-notes/Algebra/group_theory.tex").mkString
//  val input2 = scala.io.Source.fromFile("../stacks-project/algebra.tex").mkString

  def main(args: Array[String]): Unit = {
    def rmvComments(l: String) =
      if (l.startsWith("%")) ""
      else """[^\\]%""".r
      .findFirstMatchIn(l)
      .map((m) => m.before.toString + m.group(0).head)
      .getOrElse(l)

    val docString = input3.split('\n').map(rmvComments).mkString("\n")
    println(DeTeX.document.parse(docString))
  }

  val input3 =
  """
  \documentclass[master.tex]{subfiles}

  %%%%%%%%%%%%%% BEGIN CONTENT: %%%%%%%%%%%%%%

  \begin{document}
    \title{How to Structure a LaTeX Document}
    \author{Andrew Roberts}
    \date{December 2004}
    \maketitle

    \section{Testing}
    This follows from the second part of the \textit{remark} above.
    % parsing comments
    Now for some remakrs \% about centralizers.
    \vspace{1cm}
    \begin{rmk*}[1.1.3]
      This is a example of nested environment. \\
      Also test the line break token.
      \begin{enumerate}
      \item If $A \subgroup G$, \medskip then $A$ is abelian if and only if $A \subset
        C_G(A)$.
      \item Furthermore, if $A \subgroup Z(G)$, then $A \normsubgroup G$. \newline
        This follows using \emph{basic} commutativity arguments, which are as follows:
        \begin{itemize}
          % testing nested list
          \item First One.
          % testing comments.
          \item Second One.
        \end{itemize}
      \end{enumerate}
    \end{rmk*}
    \begin{defn*}[1.1.1]
        For $A \subset G$, we set $N_G(A) := \{g \in G | gAg^{-1} = A\}$ and
        $C_G(A) := \{g \in G | gag^{-1} = a, \forall a \in A\}$.
    \end{defn*}
    Note that $C_G(A) \subset N_G(A)$ and $Z(G) = C_G(G)$.

  \end{document}
  """

}
