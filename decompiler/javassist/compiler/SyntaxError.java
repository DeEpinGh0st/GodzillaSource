package javassist.compiler;
















public class SyntaxError
  extends CompileError
{
  private static final long serialVersionUID = 1L;
  
  public SyntaxError(Lex lexer) {
    super("syntax error near \"" + lexer.getTextAround() + "\"", lexer);
  }
}
