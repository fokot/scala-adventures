package lisp

import scala.util.parsing.combinator.RegexParsers

/**
  * Mini LISP interpreter
  * I did this as interview task to Recurse center
  */

// LISP AST
sealed abstract class SExpression
case class Number(value: Double) extends SExpression
case class Call(function: String, params: List[SExpression]) extends SExpression

// PARSER
object LispParser extends RegexParsers {

  def number: Parser[Number] = """-?\d+(\.\d+)?""".r ^^ { s => Number(s.toDouble) }

  def functionName: Parser[String] = """[a-zA-Z\+\-\*\:\-\_]*""".r

  def call: Parser[Call] = "(" ~ functionName ~ rep(sExpression) ~ ")" ^^ {
    case _ ~ functionName ~ sExpressions ~ _ => Call(functionName, sExpressions)
  }

  def sExpression: Parser[SExpression] = number | call

  def apply(input: String): Option[SExpression] = parseAll(sExpression, input) match {
    case Success(result, _) => Some(result)
    case NoSuccess(_, _) => None
  }
}

// MAIN OBJECT FOR TESTING
object Lisp {

  implicit def string2SExpression(s: String): {def lisp: Option[SExpression]; def e:Any} = new  {
    def lisp = LispParser(s)
    def e = eval(LispParser(s).get)
  }

  def eval(e: SExpression): Any = e match {
    case Number(value) => value
    case Call("+", a :: b :: Nil) => eval(a).asInstanceOf[Double] + eval(b).asInstanceOf[Double]
    case Call("first", x :: Nil) => eval(x).asInstanceOf[List[Any]].head
    case Call("list", args) => args.map(eval(_))
    case _ => throw new RuntimeException
  }

  def main(args: Array[String]) {

    // parsing
    assert("5".lisp == Some(Number(5)))
    assert("-5".lisp == Some(Number(-5)))
    assert("5.333".lisp == Some(Number(5.333)))
    assert("5.333x".lisp == None)
    assert("+5.333".lisp == None)
    assert("(first (list 1 (+ 2 3) 9))".lisp == Some(Call("first", List(Call("list", List(Number(1), Call("+", List(Number(2), Number(3))), Number(9)))))))
    assert("(first (list 1 (+ 2 3) 9)".lisp == None)

    // evaluation
    assert("(+ 2 3)".e ==  5)
    assert("(list 1 (+ 2 3) 9)".e ==  List(1, 5, 9))
    assert("(first (list 1 (+ 2 3) 9))".e ==  1)
  }
}