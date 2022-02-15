package org.mozilla.javascript;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mozilla.javascript.ast.ArrayComprehension;
import org.mozilla.javascript.ast.ArrayComprehensionLoop;
import org.mozilla.javascript.ast.ArrayLiteral;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Block;
import org.mozilla.javascript.ast.BreakStatement;
import org.mozilla.javascript.ast.CatchClause;
import org.mozilla.javascript.ast.Comment;
import org.mozilla.javascript.ast.ConditionalExpression;
import org.mozilla.javascript.ast.ContinueStatement;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.ElementGet;
import org.mozilla.javascript.ast.EmptyExpression;
import org.mozilla.javascript.ast.EmptyStatement;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.ForInLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.GeneratorExpression;
import org.mozilla.javascript.ast.GeneratorExpressionLoop;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Jump;
import org.mozilla.javascript.ast.KeywordLiteral;
import org.mozilla.javascript.ast.Label;
import org.mozilla.javascript.ast.LabeledStatement;
import org.mozilla.javascript.ast.LetNode;
import org.mozilla.javascript.ast.Loop;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NewExpression;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.ParenthesizedExpression;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.SwitchCase;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.Symbol;
import org.mozilla.javascript.ast.ThrowStatement;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.UnaryExpression;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.mozilla.javascript.ast.WhileLoop;
import org.mozilla.javascript.ast.WithStatement;
import org.mozilla.javascript.ast.XmlDotQuery;
import org.mozilla.javascript.ast.XmlElemRef;
import org.mozilla.javascript.ast.XmlLiteral;
import org.mozilla.javascript.ast.XmlPropRef;

public class Parser {
  public static final int ARGC_LIMIT = 65536;
  static final int CLEAR_TI_MASK = 65535;
  static final int TI_AFTER_EOL = 65536;
  private int currentFlaggedToken = 0;
  
  static final int TI_CHECK_LABEL = 131072;
  CompilerEnvirons compilerEnv;
  private ErrorReporter errorReporter;
  private IdeErrorReporter errorCollector;
  private String sourceURI;
  private char[] sourceChars;
  boolean calledByCompileFunction;
  private boolean parseFinished;
  private TokenStream ts;
  private int currentToken;
  private int syntaxErrorCount;
  private List<Comment> scannedComments;
  private Comment currentJsDocComment;
  protected int nestingOfFunction;
  private LabeledStatement currentLabel;
  private boolean inDestructuringAssignment;
  protected boolean inUseStrictDirective;
  ScriptNode currentScriptOrFn;
  Scope currentScope;
  private int endFlags;
  private boolean inForInit;
  private Map<String, LabeledStatement> labelSet;
  private List<Loop> loopSet;
  private List<Jump> loopAndSwitchSet;
  private int prevNameTokenStart;
  private String prevNameTokenString = ""; private int prevNameTokenLineno; private static final int PROP_ENTRY = 1;
  private static final int GET_ENTRY = 2;
  private static final int SET_ENTRY = 4;
  
  private static class ParserException extends RuntimeException {
    static final long serialVersionUID = 5882582646773765630L;
    
    private ParserException() {} }
  
  public Parser() {
    this(new CompilerEnvirons());
  }
  
  public Parser(CompilerEnvirons compilerEnv) {
    this(compilerEnv, compilerEnv.getErrorReporter());
  }
  
  public Parser(CompilerEnvirons compilerEnv, ErrorReporter errorReporter) {
    this.compilerEnv = compilerEnv;
    this.errorReporter = errorReporter;
    if (errorReporter instanceof IdeErrorReporter) {
      this.errorCollector = (IdeErrorReporter)errorReporter;
    }
  }

  
  void addStrictWarning(String messageId, String messageArg) {
    int beg = -1, end = -1;
    if (this.ts != null) {
      beg = this.ts.tokenBeg;
      end = this.ts.tokenEnd - this.ts.tokenBeg;
    } 
    addStrictWarning(messageId, messageArg, beg, end);
  }

  
  void addStrictWarning(String messageId, String messageArg, int position, int length) {
    if (this.compilerEnv.isStrictMode())
      addWarning(messageId, messageArg, position, length); 
  }
  
  void addWarning(String messageId, String messageArg) {
    int beg = -1, end = -1;
    if (this.ts != null) {
      beg = this.ts.tokenBeg;
      end = this.ts.tokenEnd - this.ts.tokenBeg;
    } 
    addWarning(messageId, messageArg, beg, end);
  }
  
  void addWarning(String messageId, int position, int length) {
    addWarning(messageId, null, position, length);
  }


  
  void addWarning(String messageId, String messageArg, int position, int length) {
    String message = lookupMessage(messageId, messageArg);
    if (this.compilerEnv.reportWarningAsError()) {
      addError(messageId, messageArg, position, length);
    } else if (this.errorCollector != null) {
      this.errorCollector.warning(message, this.sourceURI, position, length);
    } else {
      this.errorReporter.warning(message, this.sourceURI, this.ts.getLineno(), this.ts.getLine(), this.ts.getOffset());
    } 
  }

  
  void addError(String messageId) {
    addError(messageId, this.ts.tokenBeg, this.ts.tokenEnd - this.ts.tokenBeg);
  }
  
  void addError(String messageId, int position, int length) {
    addError(messageId, null, position, length);
  }
  
  void addError(String messageId, String messageArg) {
    addError(messageId, messageArg, this.ts.tokenBeg, this.ts.tokenEnd - this.ts.tokenBeg);
  }


  
  void addError(String messageId, String messageArg, int position, int length) {
    this.syntaxErrorCount++;
    String message = lookupMessage(messageId, messageArg);
    if (this.errorCollector != null) {
      this.errorCollector.error(message, this.sourceURI, position, length);
    } else {
      int lineno = 1, offset = 1;
      String line = "";
      if (this.ts != null) {
        lineno = this.ts.getLineno();
        line = this.ts.getLine();
        offset = this.ts.getOffset();
      } 
      this.errorReporter.error(message, this.sourceURI, lineno, line, offset);
    } 
  }


  
  private void addStrictWarning(String messageId, String messageArg, int position, int length, int line, String lineSource, int lineOffset) {
    if (this.compilerEnv.isStrictMode()) {
      addWarning(messageId, messageArg, position, length, line, lineSource, lineOffset);
    }
  }


  
  private void addWarning(String messageId, String messageArg, int position, int length, int line, String lineSource, int lineOffset) {
    String message = lookupMessage(messageId, messageArg);
    if (this.compilerEnv.reportWarningAsError()) {
      addError(messageId, messageArg, position, length, line, lineSource, lineOffset);
    } else if (this.errorCollector != null) {
      this.errorCollector.warning(message, this.sourceURI, position, length);
    } else {
      this.errorReporter.warning(message, this.sourceURI, line, lineSource, lineOffset);
    } 
  }


  
  private void addError(String messageId, String messageArg, int position, int length, int line, String lineSource, int lineOffset) {
    this.syntaxErrorCount++;
    String message = lookupMessage(messageId, messageArg);
    if (this.errorCollector != null) {
      this.errorCollector.error(message, this.sourceURI, position, length);
    } else {
      this.errorReporter.error(message, this.sourceURI, line, lineSource, lineOffset);
    } 
  }
  
  String lookupMessage(String messageId) {
    return lookupMessage(messageId, null);
  }
  
  String lookupMessage(String messageId, String messageArg) {
    return (messageArg == null) ? ScriptRuntime.getMessage0(messageId) : ScriptRuntime.getMessage1(messageId, messageArg);
  }


  
  void reportError(String messageId) {
    reportError(messageId, null);
  }
  
  void reportError(String messageId, String messageArg) {
    if (this.ts == null) {
      reportError(messageId, messageArg, 1, 1);
    } else {
      reportError(messageId, messageArg, this.ts.tokenBeg, this.ts.tokenEnd - this.ts.tokenBeg);
    } 
  }


  
  void reportError(String messageId, int position, int length) {
    reportError(messageId, null, position, length);
  }


  
  void reportError(String messageId, String messageArg, int position, int length) {
    addError(messageId, position, length);
    
    if (!this.compilerEnv.recoverFromErrors()) {
      throw new ParserException();
    }
  }



  
  private int getNodeEnd(AstNode n) {
    return n.getPosition() + n.getLength();
  }
  
  private void recordComment(int lineno, String comment) {
    if (this.scannedComments == null) {
      this.scannedComments = new ArrayList<Comment>();
    }
    Comment commentNode = new Comment(this.ts.tokenBeg, this.ts.getTokenLength(), this.ts.commentType, comment);


    
    if (this.ts.commentType == Token.CommentType.JSDOC && this.compilerEnv.isRecordingLocalJsDocComments())
    {
      this.currentJsDocComment = commentNode;
    }
    commentNode.setLineno(lineno);
    this.scannedComments.add(commentNode);
  }
  
  private Comment getAndResetJsDoc() {
    Comment saved = this.currentJsDocComment;
    this.currentJsDocComment = null;
    return saved;
  }

  
  private int getNumberOfEols(String comment) {
    int lines = 0;
    for (int i = comment.length() - 1; i >= 0; i--) {
      if (comment.charAt(i) == '\n') {
        lines++;
      }
    } 
    return lines;
  }




















  
  private int peekToken() throws IOException {
    if (this.currentFlaggedToken != 0) {
      return this.currentToken;
    }
    
    int lineno = this.ts.getLineno();
    int tt = this.ts.getToken();
    boolean sawEOL = false;

    
    while (tt == 1 || tt == 161) {
      if (tt == 1) {
        lineno++;
        sawEOL = true;
      }
      else if (this.compilerEnv.isRecordingComments()) {
        String comment = this.ts.getAndResetCurrentComment();
        recordComment(lineno, comment);

        
        lineno += getNumberOfEols(comment);
      } 
      
      tt = this.ts.getToken();
    } 
    
    this.currentToken = tt;
    this.currentFlaggedToken = tt | (sawEOL ? 65536 : 0);
    return this.currentToken;
  }


  
  private int peekFlaggedToken() throws IOException {
    peekToken();
    return this.currentFlaggedToken;
  }
  
  private void consumeToken() {
    this.currentFlaggedToken = 0;
  }


  
  private int nextToken() throws IOException {
    int tt = peekToken();
    consumeToken();
    return tt;
  }


  
  private int nextFlaggedToken() throws IOException {
    peekToken();
    int ttFlagged = this.currentFlaggedToken;
    consumeToken();
    return ttFlagged;
  }


  
  private boolean matchToken(int toMatch) throws IOException {
    if (peekToken() != toMatch) {
      return false;
    }
    consumeToken();
    return true;
  }







  
  private int peekTokenOrEOL() throws IOException {
    int tt = peekToken();
    
    if ((this.currentFlaggedToken & 0x10000) != 0) {
      tt = 1;
    }
    return tt;
  }


  
  private boolean mustMatchToken(int toMatch, String messageId) throws IOException {
    return mustMatchToken(toMatch, messageId, this.ts.tokenBeg, this.ts.tokenEnd - this.ts.tokenBeg);
  }



  
  private boolean mustMatchToken(int toMatch, String msgId, int pos, int len) throws IOException {
    if (matchToken(toMatch)) {
      return true;
    }
    reportError(msgId, pos, len);
    return false;
  }
  
  private void mustHaveXML() {
    if (!this.compilerEnv.isXmlAvailable()) {
      reportError("msg.XML.not.available");
    }
  }
  
  public boolean eof() {
    return this.ts.eof();
  }
  
  boolean insideFunction() {
    return (this.nestingOfFunction != 0);
  }
  
  void pushScope(Scope scope) {
    Scope parent = scope.getParentScope();

    
    if (parent != null) {
      if (parent != this.currentScope)
        codeBug(); 
    } else {
      this.currentScope.addChildScope(scope);
    } 
    this.currentScope = scope;
  }
  
  void popScope() {
    this.currentScope = this.currentScope.getParentScope();
  }
  
  private void enterLoop(Loop loop) {
    if (this.loopSet == null)
      this.loopSet = new ArrayList<Loop>(); 
    this.loopSet.add(loop);
    if (this.loopAndSwitchSet == null)
      this.loopAndSwitchSet = new ArrayList<Jump>(); 
    this.loopAndSwitchSet.add(loop);
    pushScope((Scope)loop);
    if (this.currentLabel != null) {
      this.currentLabel.setStatement((AstNode)loop);
      this.currentLabel.getFirstLabel().setLoop((Jump)loop);



      
      loop.setRelative(-this.currentLabel.getPosition());
    } 
  }
  
  private void exitLoop() {
    Loop loop = this.loopSet.remove(this.loopSet.size() - 1);
    this.loopAndSwitchSet.remove(this.loopAndSwitchSet.size() - 1);
    if (loop.getParent() != null) {
      loop.setRelative(loop.getParent().getPosition());
    }
    popScope();
  }
  
  private void enterSwitch(SwitchStatement node) {
    if (this.loopAndSwitchSet == null)
      this.loopAndSwitchSet = new ArrayList<Jump>(); 
    this.loopAndSwitchSet.add(node);
  }
  
  private void exitSwitch() {
    this.loopAndSwitchSet.remove(this.loopAndSwitchSet.size() - 1);
  }









  
  public AstRoot parse(String sourceString, String sourceURI, int lineno) {
    if (this.parseFinished) throw new IllegalStateException("parser reused"); 
    this.sourceURI = sourceURI;
    if (this.compilerEnv.isIdeMode()) {
      this.sourceChars = sourceString.toCharArray();
    }
    this.ts = new TokenStream(this, null, sourceString, lineno);
    try {
      return parse();
    } catch (IOException iox) {
      
      throw new IllegalStateException();
    } finally {
      this.parseFinished = true;
    } 
  }







  
  public AstRoot parse(Reader sourceReader, String sourceURI, int lineno) throws IOException {
    if (this.parseFinished) throw new IllegalStateException("parser reused"); 
    if (this.compilerEnv.isIdeMode()) {
      return parse(readFully(sourceReader), sourceURI, lineno);
    }
    try {
      this.sourceURI = sourceURI;
      this.ts = new TokenStream(this, sourceReader, null, lineno);
      return parse();
    } finally {
      this.parseFinished = true;
    } 
  }

  
  private AstRoot parse() throws IOException {
    int pos = 0;
    AstRoot root = new AstRoot(pos);
    this.currentScope = (Scope)(this.currentScriptOrFn = (ScriptNode)root);
    
    int baseLineno = this.ts.lineno;
    int end = pos;
    
    boolean inDirectivePrologue = true;
    boolean savedStrictMode = this.inUseStrictDirective;
    
    this.inUseStrictDirective = false;
    try {
      while (true) {
        AstNode astNode;
        int tt = peekToken();
        if (tt <= 0) {
          break;
        }

        
        if (tt == 109) {
          consumeToken();
          try {
            FunctionNode functionNode = function(this.calledByCompileFunction ? 2 : 1);
          
          }
          catch (ParserException e) {
            break;
          } 
        } else {
          astNode = statement();
          if (inDirectivePrologue) {
            String directive = getDirective(astNode);
            if (directive == null) {
              inDirectivePrologue = false;
            } else if (directive.equals("use strict")) {
              this.inUseStrictDirective = true;
              root.setInStrictMode(true);
            } 
          } 
        } 
        
        end = getNodeEnd(astNode);
        root.addChildToBack((Node)astNode);
        astNode.setParent((AstNode)root);
      } 
    } catch (StackOverflowError ex) {
      String msg = lookupMessage("msg.too.deep.parser.recursion");
      if (!this.compilerEnv.isIdeMode()) {
        throw Context.reportRuntimeError(msg, this.sourceURI, this.ts.lineno, null, 0);
      }
    } finally {
      this.inUseStrictDirective = savedStrictMode;
    } 
    
    if (this.syntaxErrorCount != 0) {
      String msg = String.valueOf(this.syntaxErrorCount);
      msg = lookupMessage("msg.got.syntax.errors", msg);
      if (!this.compilerEnv.isIdeMode()) {
        throw this.errorReporter.runtimeError(msg, this.sourceURI, baseLineno, null, 0);
      }
    } 

    
    if (this.scannedComments != null) {

      
      int last = this.scannedComments.size() - 1;
      end = Math.max(end, getNodeEnd((AstNode)this.scannedComments.get(last)));
      for (Comment c : this.scannedComments) {
        root.addComment(c);
      }
    } 
    
    root.setLength(end - pos);
    root.setSourceName(this.sourceURI);
    root.setBaseLineno(baseLineno);
    root.setEndLineno(this.ts.lineno);
    return root;
  }


  
  private AstNode parseFunctionBody() throws IOException {
    boolean isExpressionClosure = false;
    if (!matchToken(85)) {
      if (this.compilerEnv.getLanguageVersion() < 180) {
        reportError("msg.no.brace.body");
      } else {
        isExpressionClosure = true;
      } 
    }
    this.nestingOfFunction++;
    int pos = this.ts.tokenBeg;
    Block pn = new Block(pos);
    
    boolean inDirectivePrologue = true;
    boolean savedStrictMode = this.inUseStrictDirective;

    
    pn.setLineno(this.ts.lineno);
    try {
      if (isExpressionClosure) {
        ReturnStatement n = new ReturnStatement(this.ts.lineno);
        n.setReturnValue(assignExpr());
        
        n.putProp(25, Boolean.TRUE);
        pn.putProp(25, Boolean.TRUE);
        pn.addStatement((AstNode)n);
      } else {
        while (true) {
          FunctionNode functionNode; AstNode astNode;
          int tt = peekToken();
          switch (tt) {
            case -1:
            case 0:
            case 86:
              break;
            
            case 109:
              consumeToken();
              functionNode = function(1);
              break;
            default:
              astNode = statement();
              if (inDirectivePrologue) {
                String directive = getDirective(astNode);
                if (directive == null) {
                  inDirectivePrologue = false; break;
                }  if (directive.equals("use strict")) {
                  this.inUseStrictDirective = true;
                }
              } 
              break;
          } 
          pn.addStatement(astNode);
        } 
      } 
    } catch (ParserException e) {
    
    } finally {
      this.nestingOfFunction--;
      this.inUseStrictDirective = savedStrictMode;
    } 
    
    int end = this.ts.tokenEnd;
    getAndResetJsDoc();
    if (!isExpressionClosure && mustMatchToken(86, "msg.no.brace.after.body"))
      end = this.ts.tokenEnd; 
    pn.setLength(end - pos);
    return (AstNode)pn;
  }
  
  private String getDirective(AstNode n) {
    if (n instanceof ExpressionStatement) {
      AstNode e = ((ExpressionStatement)n).getExpression();
      if (e instanceof StringLiteral) {
        return ((StringLiteral)e).getValue();
      }
    } 
    return null;
  }


  
  private void parseFunctionParams(FunctionNode fnNode) throws IOException {
    if (matchToken(88)) {
      fnNode.setRp(this.ts.tokenBeg - fnNode.getPosition());
      
      return;
    } 
    
    Map<String, Node> destructuring = null;
    Set<String> paramNames = new HashSet<String>();
    do {
      int tt = peekToken();
      if (tt == 83 || tt == 85) {
        AstNode expr = destructuringPrimaryExpr();
        markDestructuring(expr);
        fnNode.addParam(expr);


        
        if (destructuring == null) {
          destructuring = new HashMap<String, Node>();
        }
        String pname = this.currentScriptOrFn.getNextTempName();
        defineSymbol(87, pname, false);
        destructuring.put(pname, expr);
      }
      else if (mustMatchToken(39, "msg.no.parm")) {
        fnNode.addParam((AstNode)createNameNode());
        String paramName = this.ts.getString();
        defineSymbol(87, paramName);
        if (this.inUseStrictDirective) {
          if ("eval".equals(paramName) || "arguments".equals(paramName))
          {
            
            reportError("msg.bad.id.strict", paramName);
          }
          if (paramNames.contains(paramName))
            addError("msg.dup.param.strict", paramName); 
          paramNames.add(paramName);
        } 
      } else {
        fnNode.addParam((AstNode)makeErrorNode());
      }
    
    } while (matchToken(89));
    
    if (destructuring != null) {
      Node destructuringNode = new Node(89);
      
      for (Map.Entry<String, Node> param : destructuring.entrySet()) {
        Node assign = createDestructuringAssignment(122, param.getValue(), createName(param.getKey()));
        
        destructuringNode.addChildToBack(assign);
      } 
      
      fnNode.putProp(23, destructuringNode);
    } 
    
    if (mustMatchToken(88, "msg.no.paren.after.parms")) {
      fnNode.setRp(this.ts.tokenBeg - fnNode.getPosition());
    }
  }


  
  private FunctionNode function(int type) throws IOException {
    int syntheticType = type;
    int baseLineno = this.ts.lineno;
    int functionSourceStart = this.ts.tokenBeg;
    Name name = null;
    AstNode memberExprNode = null;
    
    if (matchToken(39)) {
      name = createNameNode(true, 39);
      if (this.inUseStrictDirective) {
        String id = name.getIdentifier();
        if ("eval".equals(id) || "arguments".equals(id)) {
          reportError("msg.bad.id.strict", id);
        }
      } 
      if (!matchToken(87)) {
        if (this.compilerEnv.isAllowMemberExprAsFunctionName()) {
          Name name1 = name;
          name = null;
          memberExprNode = memberExprTail(false, (AstNode)name1);
        } 
        mustMatchToken(87, "msg.no.paren.parms");
      } 
    } else if (!matchToken(87)) {

      
      if (this.compilerEnv.isAllowMemberExprAsFunctionName())
      {

        
        memberExprNode = memberExpr(false);
      }
      mustMatchToken(87, "msg.no.paren.parms");
    } 
    int lpPos = (this.currentToken == 87) ? this.ts.tokenBeg : -1;
    
    if (memberExprNode != null) {
      syntheticType = 2;
    }
    
    if (syntheticType != 2 && name != null && name.length() > 0)
    {
      
      defineSymbol(109, name.getIdentifier());
    }
    
    FunctionNode fnNode = new FunctionNode(functionSourceStart, name);
    fnNode.setFunctionType(type);
    if (lpPos != -1) {
      fnNode.setLp(lpPos - functionSourceStart);
    }
    fnNode.setJsDocNode(getAndResetJsDoc());
    
    PerFunctionVariables savedVars = new PerFunctionVariables(fnNode);
    try {
      parseFunctionParams(fnNode);
      fnNode.setBody(parseFunctionBody());
      fnNode.setEncodedSourceBounds(functionSourceStart, this.ts.tokenEnd);
      fnNode.setLength(this.ts.tokenEnd - functionSourceStart);
      
      if (this.compilerEnv.isStrictMode() && !fnNode.getBody().hasConsistentReturnUsage()) {
        
        String msg = (name != null && name.length() > 0) ? "msg.no.return.value" : "msg.anon.no.return.value";

        
        addStrictWarning(msg, (name == null) ? "" : name.getIdentifier());
      } 
    } finally {
      savedVars.restore();
    } 
    
    if (memberExprNode != null) {
      
      Kit.codeBug();
      fnNode.setMemberExprNode(memberExprNode);
    } 









    
    fnNode.setSourceName(this.sourceURI);
    fnNode.setBaseLineno(baseLineno);
    fnNode.setEndLineno(this.ts.lineno);




    
    if (this.compilerEnv.isIdeMode()) {
      fnNode.setParentScope(this.currentScope);
    }
    return fnNode;
  }








  
  private AstNode statements(AstNode parent) throws IOException {
    if (this.currentToken != 85 && !this.compilerEnv.isIdeMode())
      codeBug(); 
    int pos = this.ts.tokenBeg;
    AstNode block = (parent != null) ? parent : (AstNode)new Block(pos);
    block.setLineno(this.ts.lineno);
    
    int tt;
    while ((tt = peekToken()) > 0 && tt != 86) {
      block.addChild(statement());
    }
    block.setLength(this.ts.tokenBeg - pos);
    return block;
  }
  
  private AstNode statements() throws IOException {
    return statements(null);
  }
  private static class ConditionData { AstNode condition;
    private ConditionData() {}
    
    int lp = -1;
    int rp = -1; }




  
  private ConditionData condition() throws IOException {
    ConditionData data = new ConditionData();
    
    if (mustMatchToken(87, "msg.no.paren.cond")) {
      data.lp = this.ts.tokenBeg;
    }
    data.condition = expr();
    
    if (mustMatchToken(88, "msg.no.paren.after.cond")) {
      data.rp = this.ts.tokenBeg;
    }

    
    if (data.condition instanceof Assignment) {
      addStrictWarning("msg.equal.as.assign", "", data.condition.getPosition(), data.condition.getLength());
    }

    
    return data;
  }

  
  private AstNode statement() throws IOException
  {
    int pos = this.ts.tokenBeg;
    try {
      AstNode pn = statementHelper();
      if (pn != null) {
        if (this.compilerEnv.isStrictMode() && !pn.hasSideEffects()) {
          int beg = pn.getPosition();
          beg = Math.max(beg, lineBeginningFor(beg));
          addStrictWarning((pn instanceof EmptyStatement) ? "msg.extra.trailing.semi" : "msg.no.side.effects", "", beg, nodeEnd(pn) - beg);
        } 


        
        return pn;
      } 
    } catch (ParserException e) {}



    
    while (true) {
      int tt = peekTokenOrEOL();
      consumeToken();
      switch (tt) {
        case -1:
        case 0:
        case 1:
        case 82:
          break;
      } 


    
    } 
    return (AstNode)new EmptyStatement(pos, this.ts.tokenBeg - pos); } private AstNode statementHelper() throws IOException { ThrowStatement throwStatement; BreakStatement breakStatement;
    ContinueStatement continueStatement;
    VariableDeclaration variableDeclaration;
    AstNode astNode2;
    KeywordLiteral keywordLiteral;
    EmptyStatement emptyStatement;
    AstNode astNode1;
    if (this.currentLabel != null && this.currentLabel.getStatement() != null) {
      this.currentLabel = null;
    }
    AstNode pn = null;
    int tt = peekToken(), pos = this.ts.tokenBeg;
    
    switch (tt)
    { case 112:
        return (AstNode)ifStatement();
      
      case 114:
        return (AstNode)switchStatement();
      
      case 117:
        return (AstNode)whileLoop();
      
      case 118:
        return (AstNode)doLoop();
      
      case 119:
        return (AstNode)forLoop();
      
      case 81:
        return (AstNode)tryStatement();
      
      case 50:
        throwStatement = throwStatement();













































































        
        autoInsertSemicolon((AstNode)throwStatement);
        return (AstNode)throwStatement;case 120: breakStatement = breakStatement(); autoInsertSemicolon((AstNode)breakStatement); return (AstNode)breakStatement;case 121: continueStatement = continueStatement(); autoInsertSemicolon((AstNode)continueStatement); return (AstNode)continueStatement;case 123: if (this.inUseStrictDirective) reportError("msg.no.with.strict");  return (AstNode)withStatement();case 122: case 154: consumeToken(); lineno = this.ts.lineno; variableDeclaration = variables(this.currentToken, this.ts.tokenBeg, true); variableDeclaration.setLineno(lineno); autoInsertSemicolon((AstNode)variableDeclaration); return (AstNode)variableDeclaration;case 153: astNode2 = letStatement(); if (!(astNode2 instanceof VariableDeclaration) || peekToken() != 82) return astNode2;  autoInsertSemicolon(astNode2); return astNode2;case 4: case 72: astNode2 = returnOrYield(tt, false); autoInsertSemicolon(astNode2); return astNode2;case 160: consumeToken(); keywordLiteral = new KeywordLiteral(this.ts.tokenBeg, this.ts.tokenEnd - this.ts.tokenBeg, tt); keywordLiteral.setLineno(this.ts.lineno); autoInsertSemicolon((AstNode)keywordLiteral); return (AstNode)keywordLiteral;case 85: return block();case -1: consumeToken(); return (AstNode)makeErrorNode();case 82: consumeToken(); pos = this.ts.tokenBeg; emptyStatement = new EmptyStatement(pos, this.ts.tokenEnd - pos); emptyStatement.setLineno(this.ts.lineno); return (AstNode)emptyStatement;case 109: consumeToken(); return (AstNode)function(3);case 116: astNode1 = defaultXmlNamespace(); autoInsertSemicolon(astNode1); return astNode1;case 39: astNode1 = nameOrLabel(); if (!(astNode1 instanceof ExpressionStatement)) return astNode1;  autoInsertSemicolon(astNode1); return astNode1; }  int lineno = this.ts.lineno; ExpressionStatement expressionStatement = new ExpressionStatement(expr(), !insideFunction()); expressionStatement.setLineno(lineno); autoInsertSemicolon((AstNode)expressionStatement); return (AstNode)expressionStatement; }

  
  private void autoInsertSemicolon(AstNode pn) throws IOException {
    int ttFlagged = peekFlaggedToken();
    int pos = pn.getPosition();
    switch (ttFlagged & 0xFFFF) {
      
      case 82:
        consumeToken();
        
        pn.setLength(this.ts.tokenEnd - pos);
        return;
      
      case -1:
      case 0:
      case 86:
        warnMissingSemi(pos, nodeEnd(pn));
        return;
    } 
    if ((ttFlagged & 0x10000) == 0) {
      
      reportError("msg.no.semi.stmt");
    } else {
      warnMissingSemi(pos, nodeEnd(pn));
    } 
  }




  
  private IfStatement ifStatement() throws IOException {
    if (this.currentToken != 112) codeBug(); 
    consumeToken();
    int pos = this.ts.tokenBeg, lineno = this.ts.lineno, elsePos = -1;
    ConditionData data = condition();
    AstNode ifTrue = statement(), ifFalse = null;
    if (matchToken(113)) {
      elsePos = this.ts.tokenBeg - pos;
      ifFalse = statement();
    } 
    int end = getNodeEnd((ifFalse != null) ? ifFalse : ifTrue);
    IfStatement pn = new IfStatement(pos, end - pos);
    pn.setCondition(data.condition);
    pn.setParens(data.lp - pos, data.rp - pos);
    pn.setThenPart(ifTrue);
    pn.setElsePart(ifFalse);
    pn.setElsePosition(elsePos);
    pn.setLineno(lineno);
    return pn;
  }


  
  private SwitchStatement switchStatement() throws IOException {
    if (this.currentToken != 114) codeBug(); 
    consumeToken();
    int pos = this.ts.tokenBeg;
    
    SwitchStatement pn = new SwitchStatement(pos);
    if (mustMatchToken(87, "msg.no.paren.switch"))
      pn.setLp(this.ts.tokenBeg - pos); 
    pn.setLineno(this.ts.lineno);
    
    AstNode discriminant = expr();
    pn.setExpression(discriminant);
    enterSwitch(pn);
    
    try {
      if (mustMatchToken(88, "msg.no.paren.after.switch")) {
        pn.setRp(this.ts.tokenBeg - pos);
      }
      mustMatchToken(85, "msg.no.brace.switch");
      
      boolean hasDefault = false;
      
      while (true) {
        int tt = nextToken();
        int casePos = this.ts.tokenBeg;
        int caseLineno = this.ts.lineno;
        AstNode caseExpression = null;
        switch (tt) {
          case 86:
            pn.setLength(this.ts.tokenEnd - pos);
            break;
          
          case 115:
            caseExpression = expr();
            mustMatchToken(103, "msg.no.colon.case");
            break;
          
          case 116:
            if (hasDefault) {
              reportError("msg.double.switch.default");
            }
            hasDefault = true;
            caseExpression = null;
            mustMatchToken(103, "msg.no.colon.case");
            break;
          
          default:
            reportError("msg.bad.switch");
            break;
        } 
        
        SwitchCase caseNode = new SwitchCase(casePos);
        caseNode.setExpression(caseExpression);
        caseNode.setLength(this.ts.tokenEnd - pos);
        caseNode.setLineno(caseLineno);



        
        while ((tt = peekToken()) != 86 && tt != 115 && tt != 116 && tt != 0)
        {
          caseNode.addStatement(statement());
        }
        pn.addCase(caseNode);
      } 
    } finally {
      exitSwitch();
    } 
    return pn;
  }


  
  private WhileLoop whileLoop() throws IOException {
    if (this.currentToken != 117) codeBug(); 
    consumeToken();
    int pos = this.ts.tokenBeg;
    WhileLoop pn = new WhileLoop(pos);
    pn.setLineno(this.ts.lineno);
    enterLoop((Loop)pn);
    try {
      ConditionData data = condition();
      pn.setCondition(data.condition);
      pn.setParens(data.lp - pos, data.rp - pos);
      AstNode body = statement();
      pn.setLength(getNodeEnd(body) - pos);
      pn.setBody(body);
    } finally {
      exitLoop();
    } 
    return pn;
  }

  
  private DoLoop doLoop() throws IOException {
    int i;
    if (this.currentToken != 118) codeBug(); 
    consumeToken();
    int pos = this.ts.tokenBeg;
    DoLoop pn = new DoLoop(pos);
    pn.setLineno(this.ts.lineno);
    enterLoop((Loop)pn);
    try {
      AstNode body = statement();
      mustMatchToken(117, "msg.no.while.do");
      pn.setWhilePosition(this.ts.tokenBeg - pos);
      ConditionData data = condition();
      pn.setCondition(data.condition);
      pn.setParens(data.lp - pos, data.rp - pos);
      i = getNodeEnd(body);
      pn.setBody(body);
    } finally {
      exitLoop();
    } 


    
    if (matchToken(82)) {
      i = this.ts.tokenEnd;
    }
    pn.setLength(i - pos);
    return pn;
  }

  
  private Loop forLoop() throws IOException {
    ForLoop forLoop;
    if (this.currentToken != 119) codeBug(); 
    consumeToken();
    int forPos = this.ts.tokenBeg, lineno = this.ts.lineno;
    boolean isForEach = false, isForIn = false;
    int eachPos = -1, inPos = -1, lp = -1, rp = -1;
    AstNode init = null;
    AstNode cond = null;
    AstNode incr = null;
    Loop pn = null;
    
    Scope tempScope = new Scope();
    pushScope(tempScope);
    
    try {
      if (matchToken(39)) {
        if ("each".equals(this.ts.getString())) {
          isForEach = true;
          eachPos = this.ts.tokenBeg - forPos;
        } else {
          reportError("msg.no.paren.for");
        } 
      }
      
      if (mustMatchToken(87, "msg.no.paren.for"))
        lp = this.ts.tokenBeg - forPos; 
      int tt = peekToken();
      
      init = forLoopInit(tt);
      
      if (matchToken(52)) {
        isForIn = true;
        inPos = this.ts.tokenBeg - forPos;
        cond = expr();
      } else {
        mustMatchToken(82, "msg.no.semi.for");
        if (peekToken() == 82) {
          
          EmptyExpression emptyExpression = new EmptyExpression(this.ts.tokenBeg, 1);
          emptyExpression.setLineno(this.ts.lineno);
        } else {
          cond = expr();
        } 
        
        mustMatchToken(82, "msg.no.semi.for.cond");
        int tmpPos = this.ts.tokenEnd;
        if (peekToken() == 88) {
          EmptyExpression emptyExpression = new EmptyExpression(tmpPos, 1);
          emptyExpression.setLineno(this.ts.lineno);
        } else {
          incr = expr();
        } 
      } 
      
      if (mustMatchToken(88, "msg.no.paren.for.ctrl")) {
        rp = this.ts.tokenBeg - forPos;
      }
      if (isForIn) {
        ForInLoop fis = new ForInLoop(forPos);
        if (init instanceof VariableDeclaration)
        {
          if (((VariableDeclaration)init).getVariables().size() > 1) {
            reportError("msg.mult.index");
          }
        }
        fis.setIterator(init);
        fis.setIteratedObject(cond);
        fis.setInPosition(inPos);
        fis.setIsForEach(isForEach);
        fis.setEachPosition(eachPos);
        ForInLoop forInLoop1 = fis;
      } else {
        ForLoop fl = new ForLoop(forPos);
        fl.setInitializer(init);
        fl.setCondition(cond);
        fl.setIncrement(incr);
        forLoop = fl;
      } 

      
      this.currentScope.replaceWith((Scope)forLoop);
      popScope();



      
      enterLoop((Loop)forLoop);
      try {
        AstNode body = statement();
        forLoop.setLength(getNodeEnd(body) - forPos);
        forLoop.setBody(body);
      } finally {
        exitLoop();
      } 
    } finally {
      
      if (this.currentScope == tempScope) {
        popScope();
      }
    } 
    forLoop.setParens(lp, rp);
    forLoop.setLineno(lineno);
    return (Loop)forLoop;
  }
  
  private AstNode forLoopInit(int tt) throws IOException {
    try {
      this.inForInit = true;
      AstNode init = null;
      if (tt == 82) {
        EmptyExpression emptyExpression = new EmptyExpression(this.ts.tokenBeg, 1);
        emptyExpression.setLineno(this.ts.lineno);
      } else if (tt == 122 || tt == 153) {
        consumeToken();
        VariableDeclaration variableDeclaration = variables(tt, this.ts.tokenBeg, false);
      } else {
        init = expr();
        markDestructuring(init);
      } 
      return init;
    } finally {
      this.inForInit = false;
    } 
  }


  
  private TryStatement tryStatement() throws IOException {
    if (this.currentToken != 81) codeBug(); 
    consumeToken();

    
    Comment jsdocNode = getAndResetJsDoc();
    
    int tryPos = this.ts.tokenBeg, lineno = this.ts.lineno, finallyPos = -1;
    if (peekToken() != 85) {
      reportError("msg.no.brace.try");
    }
    AstNode tryBlock = statement();
    int tryEnd = getNodeEnd(tryBlock);
    
    List<CatchClause> clauses = null;
    
    boolean sawDefaultCatch = false;
    int peek = peekToken();
    if (peek == 124) {
      while (matchToken(124)) {
        int catchLineNum = this.ts.lineno;
        if (sawDefaultCatch) {
          reportError("msg.catch.unreachable");
        }
        int catchPos = this.ts.tokenBeg, lp = -1, rp = -1, guardPos = -1;
        if (mustMatchToken(87, "msg.no.paren.catch")) {
          lp = this.ts.tokenBeg;
        }
        mustMatchToken(39, "msg.bad.catchcond");
        Name varName = createNameNode();
        String varNameString = varName.getIdentifier();
        if (this.inUseStrictDirective && (
          "eval".equals(varNameString) || "arguments".equals(varNameString)))
        {
          
          reportError("msg.bad.id.strict", varNameString);
        }

        
        AstNode catchCond = null;
        if (matchToken(112)) {
          guardPos = this.ts.tokenBeg;
          catchCond = expr();
        } else {
          sawDefaultCatch = true;
        } 
        
        if (mustMatchToken(88, "msg.bad.catchcond"))
          rp = this.ts.tokenBeg; 
        mustMatchToken(85, "msg.no.brace.catchblock");
        
        Block catchBlock = (Block)statements();
        tryEnd = getNodeEnd((AstNode)catchBlock);
        CatchClause catchNode = new CatchClause(catchPos);
        catchNode.setVarName(varName);
        catchNode.setCatchCondition(catchCond);
        catchNode.setBody(catchBlock);
        if (guardPos != -1) {
          catchNode.setIfPosition(guardPos - catchPos);
        }
        catchNode.setParens(lp, rp);
        catchNode.setLineno(catchLineNum);
        
        if (mustMatchToken(86, "msg.no.brace.after.body"))
          tryEnd = this.ts.tokenEnd; 
        catchNode.setLength(tryEnd - catchPos);
        if (clauses == null)
          clauses = new ArrayList<CatchClause>(); 
        clauses.add(catchNode);
      } 
    } else if (peek != 125) {
      mustMatchToken(125, "msg.try.no.catchfinally");
    } 
    
    AstNode finallyBlock = null;
    if (matchToken(125)) {
      finallyPos = this.ts.tokenBeg;
      finallyBlock = statement();
      tryEnd = getNodeEnd(finallyBlock);
    } 
    
    TryStatement pn = new TryStatement(tryPos, tryEnd - tryPos);
    pn.setTryBlock(tryBlock);
    pn.setCatchClauses(clauses);
    pn.setFinallyBlock(finallyBlock);
    if (finallyPos != -1) {
      pn.setFinallyPosition(finallyPos - tryPos);
    }
    pn.setLineno(lineno);
    
    if (jsdocNode != null) {
      pn.setJsDocNode(jsdocNode);
    }
    
    return pn;
  }


  
  private ThrowStatement throwStatement() throws IOException {
    if (this.currentToken != 50) codeBug(); 
    consumeToken();
    int pos = this.ts.tokenBeg, lineno = this.ts.lineno;
    if (peekTokenOrEOL() == 1)
    {
      
      reportError("msg.bad.throw.eol");
    }
    AstNode expr = expr();
    ThrowStatement pn = new ThrowStatement(pos, getNodeEnd(expr), expr);
    pn.setLineno(lineno);
    return pn;
  }








  
  private LabeledStatement matchJumpLabelName() throws IOException {
    LabeledStatement label = null;
    
    if (peekTokenOrEOL() == 39) {
      consumeToken();
      if (this.labelSet != null) {
        label = this.labelSet.get(this.ts.getString());
      }
      if (label == null) {
        reportError("msg.undef.label");
      }
    } 
    
    return label;
  }

  
  private BreakStatement breakStatement() throws IOException {
    Jump jump;
    if (this.currentToken != 120) codeBug(); 
    consumeToken();
    int lineno = this.ts.lineno, pos = this.ts.tokenBeg, end = this.ts.tokenEnd;
    Name breakLabel = null;
    if (peekTokenOrEOL() == 39) {
      breakLabel = createNameNode();
      end = getNodeEnd((AstNode)breakLabel);
    } 

    
    LabeledStatement labels = matchJumpLabelName();
    
    Label label = (labels == null) ? null : labels.getFirstLabel();
    
    if (label == null && breakLabel == null) {
      if (this.loopAndSwitchSet == null || this.loopAndSwitchSet.size() == 0) {
        if (breakLabel == null) {
          reportError("msg.bad.break", pos, end - pos);
        }
      } else {
        jump = this.loopAndSwitchSet.get(this.loopAndSwitchSet.size() - 1);
      } 
    }
    
    BreakStatement pn = new BreakStatement(pos, end - pos);
    pn.setBreakLabel(breakLabel);
    
    if (jump != null)
      pn.setBreakTarget(jump); 
    pn.setLineno(lineno);
    return pn;
  }


  
  private ContinueStatement continueStatement() throws IOException {
    if (this.currentToken != 121) codeBug(); 
    consumeToken();
    int lineno = this.ts.lineno, pos = this.ts.tokenBeg, end = this.ts.tokenEnd;
    Name label = null;
    if (peekTokenOrEOL() == 39) {
      label = createNameNode();
      end = getNodeEnd((AstNode)label);
    } 

    
    LabeledStatement labels = matchJumpLabelName();
    Loop target = null;
    if (labels == null && label == null) {
      if (this.loopSet == null || this.loopSet.size() == 0) {
        reportError("msg.continue.outside");
      } else {
        target = this.loopSet.get(this.loopSet.size() - 1);
      } 
    } else {
      if (labels == null || !(labels.getStatement() instanceof Loop)) {
        reportError("msg.continue.nonloop", pos, end - pos);
      }
      target = (labels == null) ? null : (Loop)labels.getStatement();
    } 
    
    ContinueStatement pn = new ContinueStatement(pos, end - pos);
    if (target != null)
      pn.setTarget(target); 
    pn.setLabel(label);
    pn.setLineno(lineno);
    return pn;
  }


  
  private WithStatement withStatement() throws IOException {
    if (this.currentToken != 123) codeBug(); 
    consumeToken();
    
    Comment withComment = getAndResetJsDoc();
    
    int lineno = this.ts.lineno, pos = this.ts.tokenBeg, lp = -1, rp = -1;
    if (mustMatchToken(87, "msg.no.paren.with")) {
      lp = this.ts.tokenBeg;
    }
    AstNode obj = expr();
    
    if (mustMatchToken(88, "msg.no.paren.after.with")) {
      rp = this.ts.tokenBeg;
    }
    AstNode body = statement();
    
    WithStatement pn = new WithStatement(pos, getNodeEnd(body) - pos);
    pn.setJsDocNode(withComment);
    pn.setExpression(obj);
    pn.setStatement(body);
    pn.setParens(lp, rp);
    pn.setLineno(lineno);
    return pn;
  }

  
  private AstNode letStatement() throws IOException {
    VariableDeclaration variableDeclaration;
    if (this.currentToken != 153) codeBug(); 
    consumeToken();
    int lineno = this.ts.lineno, pos = this.ts.tokenBeg;
    
    if (peekToken() == 87) {
      AstNode pn = let(true, pos);
    } else {
      variableDeclaration = variables(153, pos, true);
    } 
    variableDeclaration.setLineno(lineno);
    return (AstNode)variableDeclaration;
  }








  
  private static final boolean nowAllSet(int before, int after, int mask) {
    return ((before & mask) != mask && (after & mask) == mask);
  }

  
  private AstNode returnOrYield(int tt, boolean exprContext) throws IOException {
    ExpressionStatement expressionStatement;
    if (!insideFunction()) {
      reportError((tt == 4) ? "msg.bad.return" : "msg.bad.yield");
    }
    
    consumeToken();
    int lineno = this.ts.lineno, pos = this.ts.tokenBeg, end = this.ts.tokenEnd;
    
    AstNode e = null;
    
    switch (peekTokenOrEOL()) { case -1: case 0: case 1: case 72: case 82: case 84:
      case 86:
      case 88:
        break;
      default:
        e = expr();
        end = getNodeEnd(e);
        break; }
    
    int before = this.endFlags;

    
    if (tt == 4) {
      this.endFlags |= (e == null) ? 2 : 4;
      ReturnStatement returnStatement = new ReturnStatement(pos, end - pos, e);

      
      if (nowAllSet(before, this.endFlags, 6))
      {
        addStrictWarning("msg.return.inconsistent", "", pos, end - pos); } 
    } else {
      if (!insideFunction())
        reportError("msg.bad.yield"); 
      this.endFlags |= 0x8;
      Yield yield = new Yield(pos, end - pos, e);
      setRequiresActivation();
      setIsGenerator();
      if (!exprContext) {
        expressionStatement = new ExpressionStatement((AstNode)yield);
      }
    } 

    
    if (insideFunction() && nowAllSet(before, this.endFlags, 12)) {

      
      Name name = ((FunctionNode)this.currentScriptOrFn).getFunctionName();
      if (name == null || name.length() == 0) {
        addError("msg.anon.generator.returns", "");
      } else {
        addError("msg.generator.returns", name.getIdentifier());
      } 
    } 
    expressionStatement.setLineno(lineno);
    return (AstNode)expressionStatement;
  }


  
  private AstNode block() throws IOException {
    if (this.currentToken != 85) codeBug(); 
    consumeToken();
    int pos = this.ts.tokenBeg;
    Scope block = new Scope(pos);
    block.setLineno(this.ts.lineno);
    pushScope(block);
    try {
      statements((AstNode)block);
      mustMatchToken(86, "msg.no.brace.block");
      block.setLength(this.ts.tokenEnd - pos);
      return (AstNode)block;
    } finally {
      popScope();
    } 
  }


  
  private AstNode defaultXmlNamespace() throws IOException {
    if (this.currentToken != 116) codeBug(); 
    consumeToken();
    mustHaveXML();
    setRequiresActivation();
    int lineno = this.ts.lineno, pos = this.ts.tokenBeg;
    
    if (!matchToken(39) || !"xml".equals(this.ts.getString())) {
      reportError("msg.bad.namespace");
    }
    if (!matchToken(39) || !"namespace".equals(this.ts.getString())) {
      reportError("msg.bad.namespace");
    }
    if (!matchToken(90)) {
      reportError("msg.bad.namespace");
    }
    
    AstNode e = expr();
    UnaryExpression dxmln = new UnaryExpression(pos, getNodeEnd(e) - pos);
    dxmln.setOperator(74);
    dxmln.setOperand(e);
    dxmln.setLineno(lineno);
    
    ExpressionStatement es = new ExpressionStatement((AstNode)dxmln, true);
    return (AstNode)es;
  }



  
  private void recordLabel(Label label, LabeledStatement bundle) throws IOException {
    if (peekToken() != 103) codeBug(); 
    consumeToken();
    String name = label.getName();
    if (this.labelSet == null) {
      this.labelSet = new HashMap<String, LabeledStatement>();
    } else {
      LabeledStatement ls = this.labelSet.get(name);
      if (ls != null) {
        if (this.compilerEnv.isIdeMode()) {
          Label dup = ls.getLabelByName(name);
          reportError("msg.dup.label", dup.getAbsolutePosition(), dup.getLength());
        } 
        
        reportError("msg.dup.label", label.getPosition(), label.getLength());
      } 
    } 
    
    bundle.addLabel(label);
    this.labelSet.put(name, bundle);
  }






  
  private AstNode nameOrLabel() throws IOException {
    ExpressionStatement expressionStatement;
    AstNode astNode1;
    if (this.currentToken != 39) throw codeBug(); 
    int pos = this.ts.tokenBeg;

    
    this.currentFlaggedToken |= 0x20000;
    AstNode expr = expr();
    
    if (expr.getType() != 130) {
      ExpressionStatement expressionStatement1 = new ExpressionStatement(expr, !insideFunction());
      ((AstNode)expressionStatement1).lineno = expr.lineno;
      return (AstNode)expressionStatement1;
    } 
    
    LabeledStatement bundle = new LabeledStatement(pos);
    recordLabel((Label)expr, bundle);
    bundle.setLineno(this.ts.lineno);
    
    AstNode stmt = null;
    while (peekToken() == 39) {
      this.currentFlaggedToken |= 0x20000;
      expr = expr();
      if (expr.getType() != 130) {
        expressionStatement = new ExpressionStatement(expr, !insideFunction());
        autoInsertSemicolon((AstNode)expressionStatement);
        break;
      } 
      recordLabel((Label)expr, bundle);
    } 

    
    try {
      this.currentLabel = bundle;
      if (expressionStatement == null) {
        astNode1 = statementHelper();
      }
    } finally {
      this.currentLabel = null;
      
      for (Label lb : bundle.getLabels()) {
        this.labelSet.remove(lb.getName());
      }
    } 


    
    bundle.setLength((astNode1.getParent() == null) ? (getNodeEnd(astNode1) - pos) : getNodeEnd(astNode1));

    
    bundle.setStatement(astNode1);
    return (AstNode)bundle;
  }












  
  private VariableDeclaration variables(int declType, int pos, boolean isStatement) throws IOException {
    int end;
    VariableDeclaration pn = new VariableDeclaration(pos);
    pn.setType(declType);
    pn.setLineno(this.ts.lineno);
    Comment varjsdocNode = getAndResetJsDoc();
    if (varjsdocNode != null) {
      pn.setJsDocNode(varjsdocNode);
    }


    
    do {
      AstNode destructuring = null;
      Name name = null;
      int tt = peekToken(), kidPos = this.ts.tokenBeg;
      end = this.ts.tokenEnd;
      
      if (tt == 83 || tt == 85) {
        
        destructuring = destructuringPrimaryExpr();
        end = getNodeEnd(destructuring);
        if (!(destructuring instanceof DestructuringForm))
          reportError("msg.bad.assign.left", kidPos, end - kidPos); 
        markDestructuring(destructuring);
      } else {
        
        mustMatchToken(39, "msg.bad.var");
        name = createNameNode();
        name.setLineno(this.ts.getLineno());
        if (this.inUseStrictDirective) {
          String id = this.ts.getString();
          if ("eval".equals(id) || "arguments".equals(this.ts.getString()))
          {
            reportError("msg.bad.id.strict", id);
          }
        } 
        defineSymbol(declType, this.ts.getString(), this.inForInit);
      } 
      
      int lineno = this.ts.lineno;
      
      Comment jsdocNode = getAndResetJsDoc();
      
      AstNode init = null;
      if (matchToken(90)) {
        init = assignExpr();
        end = getNodeEnd(init);
      } 
      
      VariableInitializer vi = new VariableInitializer(kidPos, end - kidPos);
      if (destructuring != null) {
        if (init == null && !this.inForInit) {
          reportError("msg.destruct.assign.no.init");
        }
        vi.setTarget(destructuring);
      } else {
        vi.setTarget((AstNode)name);
      } 
      vi.setInitializer(init);
      vi.setType(declType);
      vi.setJsDocNode(jsdocNode);
      vi.setLineno(lineno);
      pn.addVariable(vi);
    }
    while (matchToken(89));

    
    pn.setLength(end - pos);
    pn.setIsStatement(isStatement);
    return pn;
  }



  
  private AstNode let(boolean isStatement, int pos) throws IOException {
    LetNode pn = new LetNode(pos);
    pn.setLineno(this.ts.lineno);
    if (mustMatchToken(87, "msg.no.paren.after.let"))
      pn.setLp(this.ts.tokenBeg - pos); 
    pushScope((Scope)pn);
    try {
      VariableDeclaration vars = variables(153, this.ts.tokenBeg, isStatement);
      pn.setVariables(vars);
      if (mustMatchToken(88, "msg.no.paren.let")) {
        pn.setRp(this.ts.tokenBeg - pos);
      }
      if (isStatement && peekToken() == 85) {
        
        consumeToken();
        int beg = this.ts.tokenBeg;
        AstNode stmt = statements();
        mustMatchToken(86, "msg.no.curly.let");
        stmt.setLength(this.ts.tokenEnd - beg);
        pn.setLength(this.ts.tokenEnd - pos);
        pn.setBody(stmt);
        pn.setType(153);
      } else {
        
        AstNode expr = expr();
        pn.setLength(getNodeEnd(expr) - pos);
        pn.setBody(expr);
        if (isStatement) {
          
          ExpressionStatement es = new ExpressionStatement((AstNode)pn, !insideFunction());
          
          es.setLineno(pn.getLineno());
          return (AstNode)es;
        } 
      } 
    } finally {
      popScope();
    } 
    return (AstNode)pn;
  }
  
  void defineSymbol(int declType, String name) {
    defineSymbol(declType, name, false);
  }
  
  void defineSymbol(int declType, String name, boolean ignoreNotInBlock) {
    if (name == null) {
      if (this.compilerEnv.isIdeMode()) {
        return;
      }
      codeBug();
    } 
    
    Scope definingScope = this.currentScope.getDefiningScope(name);
    Symbol symbol = (definingScope != null) ? definingScope.getSymbol(name) : null;

    
    int symDeclType = (symbol != null) ? symbol.getDeclType() : -1;
    if (symbol != null && (symDeclType == 154 || declType == 154 || (definingScope == this.currentScope && symDeclType == 153))) {



      
      addError((symDeclType == 154) ? "msg.const.redecl" : ((symDeclType == 153) ? "msg.let.redecl" : ((symDeclType == 122) ? "msg.var.redecl" : ((symDeclType == 109) ? "msg.fn.redecl" : "msg.parm.redecl"))), name);

      
      return;
    } 

    
    switch (declType) {
      case 153:
        if (!ignoreNotInBlock && (this.currentScope.getType() == 112 || this.currentScope instanceof Loop)) {

          
          addError("msg.let.decl.not.in.block");
          return;
        } 
        this.currentScope.putSymbol(new Symbol(declType, name));
        return;
      
      case 109:
      case 122:
      case 154:
        if (symbol != null) {
          if (symDeclType == 122) {
            addStrictWarning("msg.var.redecl", name);
          } else if (symDeclType == 87) {
            addStrictWarning("msg.var.hides.arg", name);
          } 
        } else {
          this.currentScriptOrFn.putSymbol(new Symbol(declType, name));
        } 
        return;
      
      case 87:
        if (symbol != null)
        {
          
          addWarning("msg.dup.parms", name);
        }
        this.currentScriptOrFn.putSymbol(new Symbol(declType, name));
        return;
    } 
    
    throw codeBug();
  }


  
  private AstNode expr() throws IOException {
    InfixExpression infixExpression;
    AstNode pn = assignExpr();
    int pos = pn.getPosition();
    while (matchToken(89)) {
      int opPos = this.ts.tokenBeg;
      if (this.compilerEnv.isStrictMode() && !pn.hasSideEffects()) {
        addStrictWarning("msg.no.side.effects", "", pos, nodeEnd(pn) - pos);
      }
      if (peekToken() == 72)
        reportError("msg.yield.parenthesized"); 
      infixExpression = new InfixExpression(89, pn, assignExpr(), opPos);
    } 
    return (AstNode)infixExpression;
  }

  
  private AstNode assignExpr() throws IOException {
    Assignment assignment;
    int tt = peekToken();
    if (tt == 72) {
      return returnOrYield(tt, true);
    }
    AstNode pn = condExpr();
    tt = peekToken();
    if (90 <= tt && tt <= 101) {
      consumeToken();

      
      Comment jsdocNode = getAndResetJsDoc();
      
      markDestructuring(pn);
      int opPos = this.ts.tokenBeg;
      
      assignment = new Assignment(tt, pn, assignExpr(), opPos);
      
      if (jsdocNode != null) {
        assignment.setJsDocNode(jsdocNode);
      }
    } else if (tt == 82) {

      
      if (this.currentJsDocComment != null) {
        assignment.setJsDocNode(getAndResetJsDoc());
      }
    } 
    return (AstNode)assignment;
  }

  
  private AstNode condExpr() throws IOException {
    ConditionalExpression conditionalExpression;
    AstNode pn = orExpr();
    if (matchToken(102)) {
      AstNode ifTrue; int line = this.ts.lineno;
      int qmarkPos = this.ts.tokenBeg, colonPos = -1;




      
      boolean wasInForInit = this.inForInit;
      this.inForInit = false;
      
      try {
        ifTrue = assignExpr();
      } finally {
        this.inForInit = wasInForInit;
      } 
      if (mustMatchToken(103, "msg.no.colon.cond"))
        colonPos = this.ts.tokenBeg; 
      AstNode ifFalse = assignExpr();
      int beg = pn.getPosition(), len = getNodeEnd(ifFalse) - beg;
      ConditionalExpression ce = new ConditionalExpression(beg, len);
      ce.setLineno(line);
      ce.setTestExpression(pn);
      ce.setTrueExpression(ifTrue);
      ce.setFalseExpression(ifFalse);
      ce.setQuestionMarkPosition(qmarkPos - beg);
      ce.setColonPosition(colonPos - beg);
      conditionalExpression = ce;
    } 
    return (AstNode)conditionalExpression;
  }

  
  private AstNode orExpr() throws IOException {
    InfixExpression infixExpression;
    AstNode pn = andExpr();
    if (matchToken(104)) {
      int opPos = this.ts.tokenBeg;
      infixExpression = new InfixExpression(104, pn, orExpr(), opPos);
    } 
    return (AstNode)infixExpression;
  }

  
  private AstNode andExpr() throws IOException {
    InfixExpression infixExpression;
    AstNode pn = bitOrExpr();
    if (matchToken(105)) {
      int opPos = this.ts.tokenBeg;
      infixExpression = new InfixExpression(105, pn, andExpr(), opPos);
    } 
    return (AstNode)infixExpression;
  }

  
  private AstNode bitOrExpr() throws IOException {
    InfixExpression infixExpression;
    AstNode pn = bitXorExpr();
    while (matchToken(9)) {
      int opPos = this.ts.tokenBeg;
      infixExpression = new InfixExpression(9, pn, bitXorExpr(), opPos);
    } 
    return (AstNode)infixExpression;
  }

  
  private AstNode bitXorExpr() throws IOException {
    InfixExpression infixExpression;
    AstNode pn = bitAndExpr();
    while (matchToken(10)) {
      int opPos = this.ts.tokenBeg;
      infixExpression = new InfixExpression(10, pn, bitAndExpr(), opPos);
    } 
    return (AstNode)infixExpression;
  }

  
  private AstNode bitAndExpr() throws IOException {
    InfixExpression infixExpression;
    AstNode pn = eqExpr();
    while (matchToken(11)) {
      int opPos = this.ts.tokenBeg;
      infixExpression = new InfixExpression(11, pn, eqExpr(), opPos);
    } 
    return (AstNode)infixExpression;
  }

  
  private AstNode eqExpr() throws IOException {
    InfixExpression infixExpression;
    AstNode pn = relExpr();
    while (true) {
      int parseToken, tt = peekToken(), opPos = this.ts.tokenBeg;
      switch (tt) {
        case 12:
        case 13:
        case 46:
        case 47:
          consumeToken();
          parseToken = tt;
          if (this.compilerEnv.getLanguageVersion() == 120)
          {
            if (tt == 12) {
              parseToken = 46;
            } else if (tt == 13) {
              parseToken = 47;
            }  } 
          infixExpression = new InfixExpression(parseToken, pn, relExpr(), opPos);
          continue;
      } 
      break;
    } 
    return (AstNode)infixExpression;
  }

  
  private AstNode relExpr() throws IOException {
    InfixExpression infixExpression;
    AstNode pn = shiftExpr();
    while (true) {
      int tt = peekToken(), opPos = this.ts.tokenBeg;
      switch (tt) {
        case 52:
          if (this.inForInit) {
            break;
          }
        case 14:
        case 15:
        case 16:
        case 17:
        case 53:
          consumeToken();
          infixExpression = new InfixExpression(tt, pn, shiftExpr(), opPos);
          continue;
      } 
      break;
    } 
    return (AstNode)infixExpression;
  }

  
  private AstNode shiftExpr() throws IOException {
    InfixExpression infixExpression;
    AstNode pn = addExpr();
    while (true) {
      int tt = peekToken(), opPos = this.ts.tokenBeg;
      switch (tt) {
        case 18:
        case 19:
        case 20:
          consumeToken();
          infixExpression = new InfixExpression(tt, pn, addExpr(), opPos);
          continue;
      } 
      break;
    } 
    return (AstNode)infixExpression;
  }

  
  private AstNode addExpr() throws IOException {
    InfixExpression infixExpression;
    AstNode pn = mulExpr();
    while (true) {
      int tt = peekToken(), opPos = this.ts.tokenBeg;
      if (tt == 21 || tt == 22) {
        consumeToken();
        infixExpression = new InfixExpression(tt, pn, mulExpr(), opPos);
        continue;
      } 
      break;
    } 
    return (AstNode)infixExpression;
  }

  
  private AstNode mulExpr() throws IOException {
    InfixExpression infixExpression;
    AstNode pn = unaryExpr();
    while (true) {
      int tt = peekToken(), opPos = this.ts.tokenBeg;
      switch (tt) {
        case 23:
        case 24:
        case 25:
          consumeToken();
          infixExpression = new InfixExpression(tt, pn, unaryExpr(), opPos);
          continue;
      } 
      break;
    } 
    return (AstNode)infixExpression;
  }


  
  private AstNode unaryExpr() throws IOException {
    UnaryExpression unaryExpression1, expr;
    int tt = peekToken();
    int line = this.ts.lineno;
    
    switch (tt) {
      case 26:
      case 27:
      case 32:
      case 126:
        consumeToken();
        unaryExpression1 = new UnaryExpression(tt, this.ts.tokenBeg, unaryExpr());
        unaryExpression1.setLineno(line);
        return (AstNode)unaryExpression1;
      
      case 21:
        consumeToken();
        
        unaryExpression1 = new UnaryExpression(28, this.ts.tokenBeg, unaryExpr());
        unaryExpression1.setLineno(line);
        return (AstNode)unaryExpression1;
      
      case 22:
        consumeToken();
        
        unaryExpression1 = new UnaryExpression(29, this.ts.tokenBeg, unaryExpr());
        unaryExpression1.setLineno(line);
        return (AstNode)unaryExpression1;
      
      case 106:
      case 107:
        consumeToken();
        expr = new UnaryExpression(tt, this.ts.tokenBeg, memberExpr(true));
        
        expr.setLineno(line);
        checkBadIncDec(expr);
        return (AstNode)expr;
      
      case 31:
        consumeToken();
        unaryExpression1 = new UnaryExpression(tt, this.ts.tokenBeg, unaryExpr());
        unaryExpression1.setLineno(line);
        return (AstNode)unaryExpression1;
      
      case -1:
        consumeToken();
        return (AstNode)makeErrorNode();

      
      case 14:
        if (this.compilerEnv.isXmlAvailable()) {
          consumeToken();
          return memberExprTail(true, xmlInitializer());
        } 
        break;
    } 
    
    AstNode pn = memberExpr(true);
    
    tt = peekTokenOrEOL();
    if (tt != 106 && tt != 107) {
      return pn;
    }
    consumeToken();
    UnaryExpression uexpr = new UnaryExpression(tt, this.ts.tokenBeg, pn, true);
    
    uexpr.setLineno(line);
    checkBadIncDec(uexpr);
    return (AstNode)uexpr;
  }



  
  private AstNode xmlInitializer() throws IOException {
    if (this.currentToken != 14) codeBug(); 
    int pos = this.ts.tokenBeg, tt = this.ts.getFirstXMLToken();
    if (tt != 145 && tt != 148) {
      reportError("msg.syntax");
      return (AstNode)makeErrorNode();
    } 
    
    XmlLiteral pn = new XmlLiteral(pos);
    pn.setLineno(this.ts.lineno);
    
    for (;; tt = this.ts.getNextXMLToken()) {
      int beg; AstNode expr; XmlExpression xexpr; switch (tt) {
        case 145:
          pn.addFragment((XmlFragment)new XmlString(this.ts.tokenBeg, this.ts.getString()));
          mustMatchToken(85, "msg.syntax");
          beg = this.ts.tokenBeg;
          expr = (peekToken() == 86) ? (AstNode)new EmptyExpression(beg, this.ts.tokenEnd - beg) : expr();

          
          mustMatchToken(86, "msg.syntax");
          xexpr = new XmlExpression(beg, expr);
          xexpr.setIsXmlAttribute(this.ts.isXMLAttribute());
          xexpr.setLength(this.ts.tokenEnd - beg);
          pn.addFragment((XmlFragment)xexpr);
          break;
        
        case 148:
          pn.addFragment((XmlFragment)new XmlString(this.ts.tokenBeg, this.ts.getString()));
          return (AstNode)pn;
        
        default:
          reportError("msg.syntax");
          return (AstNode)makeErrorNode();
      } 
    } 
  }


  
  private List<AstNode> argumentList() throws IOException {
    if (matchToken(88)) {
      return null;
    }
    List<AstNode> result = new ArrayList<AstNode>();
    boolean wasInForInit = this.inForInit;
    this.inForInit = false;
    try {
      do {
        if (peekToken() == 72) {
          reportError("msg.yield.parenthesized");
        }
        AstNode en = assignExpr();
        if (peekToken() == 119) {
          try {
            result.add(generatorExpression(en, 0, true));
          }
          catch (IOException ex) {}
        
        }
        else {
          
          result.add(en);
        } 
      } while (matchToken(89));
    } finally {
      this.inForInit = wasInForInit;
    } 
    
    mustMatchToken(88, "msg.no.paren.arg");
    return result;
  }






  
  private AstNode memberExpr(boolean allowCallSyntax) throws IOException {
    NewExpression newExpression;
    int tt = peekToken(), lineno = this.ts.lineno;

    
    if (tt != 30) {
      AstNode pn = primaryExpr();
    } else {
      consumeToken();
      int pos = this.ts.tokenBeg;
      NewExpression nx = new NewExpression(pos);
      
      AstNode target = memberExpr(false);
      int end = getNodeEnd(target);
      nx.setTarget(target);
      
      int lp = -1;
      if (matchToken(87)) {
        lp = this.ts.tokenBeg;
        List<AstNode> args = argumentList();
        if (args != null && args.size() > 65536)
          reportError("msg.too.many.constructor.args"); 
        int rp = this.ts.tokenBeg;
        end = this.ts.tokenEnd;
        if (args != null)
          nx.setArguments(args); 
        nx.setParens(lp - pos, rp - pos);
      } 




      
      if (matchToken(85)) {
        ObjectLiteral initializer = objectLiteral();
        end = getNodeEnd((AstNode)initializer);
        nx.setInitializer(initializer);
      } 
      nx.setLength(end - pos);
      newExpression = nx;
    } 
    newExpression.setLineno(lineno);
    AstNode tail = memberExprTail(allowCallSyntax, (AstNode)newExpression);
    return tail;
  }









  
  private AstNode memberExprTail(boolean allowCallSyntax, AstNode pn) throws IOException {
    FunctionCall functionCall;
    if (pn == null) codeBug(); 
    int pos = pn.getPosition(); while (true) {
      XmlDotQuery xmlDotQuery1; ElementGet elementGet1; int lineno, opPos, rp; AstNode filter; int end; XmlDotQuery q; int lb, rb; AstNode expr; ElementGet g;
      FunctionCall f;
      List<AstNode> args;
      int tt = peekToken();
      switch (tt) {
        case 108:
        case 143:
          lineno = this.ts.lineno;
          pn = propertyAccess(tt, pn);
          pn.setLineno(lineno);
          continue;
        
        case 146:
          consumeToken();
          opPos = this.ts.tokenBeg; rp = -1;
          lineno = this.ts.lineno;
          mustHaveXML();
          setRequiresActivation();
          filter = expr();
          end = getNodeEnd(filter);
          if (mustMatchToken(88, "msg.no.paren")) {
            rp = this.ts.tokenBeg;
            end = this.ts.tokenEnd;
          } 
          q = new XmlDotQuery(pos, end - pos);
          q.setLeft(pn);
          q.setRight(filter);
          q.setOperatorPosition(opPos);
          q.setRp(rp - pos);
          q.setLineno(lineno);
          xmlDotQuery1 = q;
          continue;
        
        case 83:
          consumeToken();
          lb = this.ts.tokenBeg; rb = -1;
          lineno = this.ts.lineno;
          expr = expr();
          end = getNodeEnd(expr);
          if (mustMatchToken(84, "msg.no.bracket.index")) {
            rb = this.ts.tokenBeg;
            end = this.ts.tokenEnd;
          } 
          g = new ElementGet(pos, end - pos);
          g.setTarget((AstNode)xmlDotQuery1);
          g.setElement(expr);
          g.setParens(lb, rb);
          g.setLineno(lineno);
          elementGet1 = g;
          continue;
        
        case 87:
          if (!allowCallSyntax) {
            break;
          }
          lineno = this.ts.lineno;
          consumeToken();
          checkCallRequiresActivation((AstNode)elementGet1);
          f = new FunctionCall(pos);
          f.setTarget((AstNode)elementGet1);

          
          f.setLineno(lineno);
          f.setLp(this.ts.tokenBeg - pos);
          args = argumentList();
          if (args != null && args.size() > 65536)
            reportError("msg.too.many.function.args"); 
          f.setArguments(args);
          f.setRp(this.ts.tokenBeg - pos);
          f.setLength(this.ts.tokenEnd - pos);
          functionCall = f;
          continue;
      } 

      
      break;
    } 
    return (AstNode)functionCall;
  }







  
  private AstNode propertyAccess(int tt, AstNode pn) throws IOException {
    if (pn == null) codeBug(); 
    int memberTypeFlags = 0, lineno = this.ts.lineno, dotPos = this.ts.tokenBeg;
    consumeToken();
    
    if (tt == 143) {
      mustHaveXML();
      memberTypeFlags = 4;
    } 
    
    if (!this.compilerEnv.isXmlAvailable()) {
      int maybeName = nextToken();
      if (maybeName != 39 && (!this.compilerEnv.isReservedKeywordAsIdentifier() || !TokenStream.isKeyword(this.ts.getString())))
      {
        
        reportError("msg.no.name.after.dot");
      }
      
      Name name = createNameNode(true, 33);
      PropertyGet pg = new PropertyGet(pn, name, dotPos);
      pg.setLineno(lineno);
      return (AstNode)pg;
    } 
    
    AstNode ref = null;
    
    int token = nextToken();
    switch (token) {
      
      case 50:
        saveNameTokenData(this.ts.tokenBeg, "throw", this.ts.lineno);
        ref = propertyName(-1, "throw", memberTypeFlags);
        break;

      
      case 39:
        ref = propertyName(-1, this.ts.getString(), memberTypeFlags);
        break;

      
      case 23:
        saveNameTokenData(this.ts.tokenBeg, "*", this.ts.lineno);
        ref = propertyName(-1, "*", memberTypeFlags);
        break;


      
      case 147:
        ref = attributeAccess();
        break;
      
      default:
        if (this.compilerEnv.isReservedKeywordAsIdentifier()) {
          
          String name = Token.keywordToName(token);
          if (name != null) {
            saveNameTokenData(this.ts.tokenBeg, name, this.ts.lineno);
            ref = propertyName(-1, name, memberTypeFlags);
            break;
          } 
        } 
        reportError("msg.no.name.after.dot");
        return (AstNode)makeErrorNode();
    } 
    
    boolean xml = ref instanceof org.mozilla.javascript.ast.XmlRef;
    InfixExpression result = xml ? (InfixExpression)new XmlMemberGet() : (InfixExpression)new PropertyGet();
    if (xml && tt == 108)
      result.setType(108); 
    int pos = pn.getPosition();
    result.setPosition(pos);
    result.setLength(getNodeEnd(ref) - pos);
    result.setOperatorPosition(dotPos - pos);
    result.setLineno(pn.getLineno());
    result.setLeft(pn);
    result.setRight(ref);
    return (AstNode)result;
  }









  
  private AstNode attributeAccess() throws IOException {
    int tt = nextToken(), atPos = this.ts.tokenBeg;
    
    switch (tt) {
      
      case 39:
        return propertyName(atPos, this.ts.getString(), 0);

      
      case 23:
        saveNameTokenData(this.ts.tokenBeg, "*", this.ts.lineno);
        return propertyName(atPos, "*", 0);

      
      case 83:
        return (AstNode)xmlElemRef(atPos, null, -1);
    } 
    
    reportError("msg.no.name.after.xmlAttr");
    return (AstNode)makeErrorNode();
  }


















  
  private AstNode propertyName(int atPos, String s, int memberTypeFlags) throws IOException {
    int pos = (atPos != -1) ? atPos : this.ts.tokenBeg, lineno = this.ts.lineno;
    int colonPos = -1;
    Name name = createNameNode(true, this.currentToken);
    Name ns = null;
    
    if (matchToken(144)) {
      ns = name;
      colonPos = this.ts.tokenBeg;
      
      switch (nextToken()) {
        
        case 39:
          name = createNameNode();
          break;

        
        case 23:
          saveNameTokenData(this.ts.tokenBeg, "*", this.ts.lineno);
          name = createNameNode(false, -1);
          break;

        
        case 83:
          return (AstNode)xmlElemRef(atPos, ns, colonPos);
        
        default:
          reportError("msg.no.name.after.coloncolon");
          return (AstNode)makeErrorNode();
      } 
    
    } 
    if (ns == null && memberTypeFlags == 0 && atPos == -1) {
      return (AstNode)name;
    }
    
    XmlPropRef ref = new XmlPropRef(pos, getNodeEnd((AstNode)name) - pos);
    ref.setAtPos(atPos);
    ref.setNamespace(ns);
    ref.setColonPos(colonPos);
    ref.setPropName(name);
    ref.setLineno(lineno);
    return (AstNode)ref;
  }






  
  private XmlElemRef xmlElemRef(int atPos, Name namespace, int colonPos) throws IOException {
    int lb = this.ts.tokenBeg, rb = -1, pos = (atPos != -1) ? atPos : lb;
    AstNode expr = expr();
    int end = getNodeEnd(expr);
    if (mustMatchToken(84, "msg.no.bracket.index")) {
      rb = this.ts.tokenBeg;
      end = this.ts.tokenEnd;
    } 
    XmlElemRef ref = new XmlElemRef(pos, end - pos);
    ref.setNamespace(namespace);
    ref.setColonPos(colonPos);
    ref.setAtPos(atPos);
    ref.setExpression(expr);
    ref.setBrackets(lb, rb);
    return ref;
  }


  
  private AstNode destructuringPrimaryExpr() throws IOException, ParserException {
    try {
      this.inDestructuringAssignment = true;
      return primaryExpr();
    } finally {
      this.inDestructuringAssignment = false;
    } 
  }
  
  private AstNode primaryExpr() throws IOException { String s;
    int pos, end;
    RegExpLiteral re;
    int ttFlagged = nextFlaggedToken();
    int tt = ttFlagged & 0xFFFF;
    
    switch (tt) {
      case 109:
        return (AstNode)function(2);
      
      case 83:
        return arrayLiteral();
      
      case 85:
        return (AstNode)objectLiteral();
      
      case 153:
        return let(false, this.ts.tokenBeg);
      
      case 87:
        return parenExpr();
      
      case 147:
        mustHaveXML();
        return attributeAccess();
      
      case 39:
        return name(ttFlagged, tt);
      
      case 40:
        s = this.ts.getString();
        if (this.inUseStrictDirective && this.ts.isNumberOctal()) {
          reportError("msg.no.octal.strict");
        }
        if (this.ts.isNumberOctal()) {
          s = "0" + s;
        }
        if (this.ts.isNumberHex()) {
          s = "0x" + s;
        }
        return (AstNode)new NumberLiteral(this.ts.tokenBeg, s, this.ts.getNumber());



      
      case 41:
        return (AstNode)createStringLiteral();

      
      case 24:
      case 100:
        this.ts.readRegExp(tt);
        pos = this.ts.tokenBeg; end = this.ts.tokenEnd;
        re = new RegExpLiteral(pos, end - pos);
        re.setValue(this.ts.getString());
        re.setFlags(this.ts.readAndClearRegExpFlags());
        return (AstNode)re;
      
      case 42:
      case 43:
      case 44:
      case 45:
        pos = this.ts.tokenBeg; end = this.ts.tokenEnd;
        return (AstNode)new KeywordLiteral(pos, end - pos, tt);
      
      case 127:
        reportError("msg.reserved.id");













      
      case -1:
        return (AstNode)makeErrorNode();
      case 0:
        reportError("msg.unexpected.eof");
    } 
    reportError("msg.syntax"); } private AstNode parenExpr() throws IOException { boolean wasInForInit = this.inForInit;
    this.inForInit = false;
    try {
      Comment jsdocNode = getAndResetJsDoc();
      int lineno = this.ts.lineno;
      int begin = this.ts.tokenBeg;
      AstNode e = expr();
      if (peekToken() == 119) {
        return generatorExpression(e, begin);
      }
      ParenthesizedExpression pn = new ParenthesizedExpression(e);
      if (jsdocNode == null) {
        jsdocNode = getAndResetJsDoc();
      }
      if (jsdocNode != null) {
        pn.setJsDocNode(jsdocNode);
      }
      mustMatchToken(88, "msg.no.paren");
      pn.setLength(this.ts.tokenEnd - pn.getPosition());
      pn.setLineno(lineno);
      return (AstNode)pn;
    } finally {
      this.inForInit = wasInForInit;
    }  }

  
  private AstNode name(int ttFlagged, int tt) throws IOException {
    String nameString = this.ts.getString();
    int namePos = this.ts.tokenBeg, nameLineno = this.ts.lineno;
    if (0 != (ttFlagged & 0x20000) && peekToken() == 103) {

      
      Label label = new Label(namePos, this.ts.tokenEnd - namePos);
      label.setName(nameString);
      label.setLineno(this.ts.lineno);
      return (AstNode)label;
    } 


    
    saveNameTokenData(namePos, nameString, nameLineno);
    
    if (this.compilerEnv.isXmlAvailable()) {
      return propertyName(-1, nameString, 0);
    }
    return (AstNode)createNameNode(true, 39);
  }






  
  private AstNode arrayLiteral() throws IOException {
    if (this.currentToken != 83) codeBug(); 
    int pos = this.ts.tokenBeg, end = this.ts.tokenEnd;
    List<AstNode> elements = new ArrayList<AstNode>();
    ArrayLiteral pn = new ArrayLiteral(pos);
    boolean after_lb_or_comma = true;
    int afterComma = -1;
    int skipCount = 0;
    while (true) {
      int tt = peekToken();
      if (tt == 89) {
        consumeToken();
        afterComma = this.ts.tokenEnd;
        if (!after_lb_or_comma) {
          after_lb_or_comma = true; continue;
        } 
        elements.add(new EmptyExpression(this.ts.tokenBeg, 1));
        skipCount++; continue;
      } 
      if (tt == 84) {
        consumeToken();




        
        end = this.ts.tokenEnd;
        pn.setDestructuringLength(elements.size() + (after_lb_or_comma ? 1 : 0));
        
        pn.setSkipCount(skipCount);
        if (afterComma != -1)
          warnTrailingComma(pos, elements, afterComma);  break;
      } 
      if (tt == 119 && !after_lb_or_comma && elements.size() == 1)
      {
        return arrayComprehension(elements.get(0), pos); } 
      if (tt == 0) {
        reportError("msg.no.bracket.arg");
        break;
      } 
      if (!after_lb_or_comma) {
        reportError("msg.no.bracket.arg");
      }
      elements.add(assignExpr());
      after_lb_or_comma = false;
      afterComma = -1;
    } 
    
    for (AstNode e : elements) {
      pn.addElement(e);
    }
    pn.setLength(end - pos);
    return (AstNode)pn;
  }








  
  private AstNode arrayComprehension(AstNode result, int pos) throws IOException {
    List<ArrayComprehensionLoop> loops = new ArrayList<ArrayComprehensionLoop>();
    
    while (peekToken() == 119) {
      loops.add(arrayComprehensionLoop());
    }
    int ifPos = -1;
    ConditionData data = null;
    if (peekToken() == 112) {
      consumeToken();
      ifPos = this.ts.tokenBeg - pos;
      data = condition();
    } 
    mustMatchToken(84, "msg.no.bracket.arg");
    ArrayComprehension pn = new ArrayComprehension(pos, this.ts.tokenEnd - pos);
    pn.setResult(result);
    pn.setLoops(loops);
    if (data != null) {
      pn.setIfPosition(ifPos);
      pn.setFilter(data.condition);
      pn.setFilterLp(data.lp - pos);
      pn.setFilterRp(data.rp - pos);
    } 
    return (AstNode)pn;
  }


  
  private ArrayComprehensionLoop arrayComprehensionLoop() throws IOException {
    if (nextToken() != 119) codeBug(); 
    int pos = this.ts.tokenBeg;
    int eachPos = -1, lp = -1, rp = -1, inPos = -1;
    ArrayComprehensionLoop pn = new ArrayComprehensionLoop(pos);
    
    pushScope((Scope)pn); try {
      Name name;
      if (matchToken(39)) {
        if (this.ts.getString().equals("each")) {
          eachPos = this.ts.tokenBeg - pos;
        } else {
          reportError("msg.no.paren.for");
        } 
      }
      if (mustMatchToken(87, "msg.no.paren.for")) {
        lp = this.ts.tokenBeg - pos;
      }
      
      AstNode iter = null;
      switch (peekToken()) {
        
        case 83:
        case 85:
          iter = destructuringPrimaryExpr();
          markDestructuring(iter);
          break;
        case 39:
          consumeToken();
          name = createNameNode();
          break;
        default:
          reportError("msg.bad.var");
          break;
      } 

      
      if (name.getType() == 39) {
        defineSymbol(153, this.ts.getString(), true);
      }
      
      if (mustMatchToken(52, "msg.in.after.for.name"))
        inPos = this.ts.tokenBeg - pos; 
      AstNode obj = expr();
      if (mustMatchToken(88, "msg.no.paren.for.ctrl")) {
        rp = this.ts.tokenBeg - pos;
      }
      pn.setLength(this.ts.tokenEnd - pos);
      pn.setIterator((AstNode)name);
      pn.setIteratedObject(obj);
      pn.setInPosition(inPos);
      pn.setEachPosition(eachPos);
      pn.setIsForEach((eachPos != -1));
      pn.setParens(lp, rp);
      return pn;
    } finally {
      popScope();
    } 
  }


  
  private AstNode generatorExpression(AstNode result, int pos) throws IOException {
    return generatorExpression(result, pos, false);
  }



  
  private AstNode generatorExpression(AstNode result, int pos, boolean inFunctionParams) throws IOException {
    List<GeneratorExpressionLoop> loops = new ArrayList<GeneratorExpressionLoop>();
    
    while (peekToken() == 119) {
      loops.add(generatorExpressionLoop());
    }
    int ifPos = -1;
    ConditionData data = null;
    if (peekToken() == 112) {
      consumeToken();
      ifPos = this.ts.tokenBeg - pos;
      data = condition();
    } 
    if (!inFunctionParams) {
      mustMatchToken(88, "msg.no.paren.let");
    }
    GeneratorExpression pn = new GeneratorExpression(pos, this.ts.tokenEnd - pos);
    pn.setResult(result);
    pn.setLoops(loops);
    if (data != null) {
      pn.setIfPosition(ifPos);
      pn.setFilter(data.condition);
      pn.setFilterLp(data.lp - pos);
      pn.setFilterRp(data.rp - pos);
    } 
    return (AstNode)pn;
  }


  
  private GeneratorExpressionLoop generatorExpressionLoop() throws IOException {
    if (nextToken() != 119) codeBug(); 
    int pos = this.ts.tokenBeg;
    int lp = -1, rp = -1, inPos = -1;
    GeneratorExpressionLoop pn = new GeneratorExpressionLoop(pos);
    
    pushScope((Scope)pn); try {
      Name name;
      if (mustMatchToken(87, "msg.no.paren.for")) {
        lp = this.ts.tokenBeg - pos;
      }
      
      AstNode iter = null;
      switch (peekToken()) {
        
        case 83:
        case 85:
          iter = destructuringPrimaryExpr();
          markDestructuring(iter);
          break;
        case 39:
          consumeToken();
          name = createNameNode();
          break;
        default:
          reportError("msg.bad.var");
          break;
      } 

      
      if (name.getType() == 39) {
        defineSymbol(153, this.ts.getString(), true);
      }
      
      if (mustMatchToken(52, "msg.in.after.for.name"))
        inPos = this.ts.tokenBeg - pos; 
      AstNode obj = expr();
      if (mustMatchToken(88, "msg.no.paren.for.ctrl")) {
        rp = this.ts.tokenBeg - pos;
      }
      pn.setLength(this.ts.tokenEnd - pos);
      pn.setIterator((AstNode)name);
      pn.setIteratedObject(obj);
      pn.setInPosition(inPos);
      pn.setParens(lp, rp);
      return pn;
    } finally {
      popScope();
    } 
  }





  
  private ObjectLiteral objectLiteral() throws IOException
  {
    int pos = this.ts.tokenBeg, lineno = this.ts.lineno;
    int afterComma = -1;
    List<ObjectProperty> elems = new ArrayList<ObjectProperty>();
    Set<String> getterNames = null;
    Set<String> setterNames = null;
    if (this.inUseStrictDirective) {
      getterNames = new HashSet<String>();
      setterNames = new HashSet<String>();
    } 
    Comment objJsdocNode = getAndResetJsDoc(); while (true) {
      Name name; int ppos, peeked;
      boolean maybeGetterOrSetter;
      AstNode pname;
      String propertyName = null;
      int entryKind = 1;
      int tt = peekToken();
      Comment jsdocNode = getAndResetJsDoc();
      switch (tt) {
        case 39:
          name = createNameNode();
          propertyName = this.ts.getString();
          ppos = this.ts.tokenBeg;
          consumeToken();








          
          peeked = peekToken();
          maybeGetterOrSetter = ("get".equals(propertyName) || "set".equals(propertyName));

          
          if (maybeGetterOrSetter && peeked != 89 && peeked != 103 && peeked != 86) {



            
            boolean isGet = "get".equals(propertyName);
            entryKind = isGet ? 2 : 4;
            AstNode astNode = objliteralProperty();
            if (astNode == null) {
              propertyName = null; break;
            } 
            propertyName = this.ts.getString();
            ObjectProperty objectProp = getterSetterProperty(ppos, astNode, isGet);
            
            astNode.setJsDocNode(jsdocNode);
            elems.add(objectProp);
            break;
          } 
          name.setJsDocNode(jsdocNode);
          elems.add(plainProperty((AstNode)name, tt));
          break;

        
        case 86:
          if (afterComma != -1) {
            warnTrailingComma(pos, elems, afterComma);
          }
          break;
        default:
          pname = objliteralProperty();
          if (pname == null) {
            propertyName = null; break;
          } 
          propertyName = this.ts.getString();
          pname.setJsDocNode(jsdocNode);
          elems.add(plainProperty(pname, tt));
          break;
      } 

      
      if (this.inUseStrictDirective && propertyName != null) {
        switch (entryKind) {
          case 1:
            if (getterNames.contains(propertyName) || setterNames.contains(propertyName))
            {
              addError("msg.dup.obj.lit.prop.strict", propertyName);
            }
            getterNames.add(propertyName);
            setterNames.add(propertyName);
            break;
          case 2:
            if (getterNames.contains(propertyName)) {
              addError("msg.dup.obj.lit.prop.strict", propertyName);
            }
            getterNames.add(propertyName);
            break;
          case 4:
            if (setterNames.contains(propertyName)) {
              addError("msg.dup.obj.lit.prop.strict", propertyName);
            }
            setterNames.add(propertyName);
            break;
        } 

      
      }
      getAndResetJsDoc();
      
      if (matchToken(89)) {
        afterComma = this.ts.tokenEnd;
        
        continue;
      } 
      break;
    } 
    mustMatchToken(86, "msg.no.brace.prop");
    ObjectLiteral pn = new ObjectLiteral(pos, this.ts.tokenEnd - pos);
    if (objJsdocNode != null) {
      pn.setJsDocNode(objJsdocNode);
    }
    pn.setElements(elems);
    pn.setLineno(lineno);
    return pn; } private AstNode objliteralProperty() throws IOException {
    Name name2;
    StringLiteral stringLiteral;
    NumberLiteral numberLiteral;
    Name name1;
    int tt = peekToken();
    switch (tt)
    { case 39:
        name2 = createNameNode();





















        
        consumeToken();
        return (AstNode)name2;case 41: stringLiteral = createStringLiteral(); consumeToken(); return (AstNode)stringLiteral;case 40: numberLiteral = new NumberLiteral(this.ts.tokenBeg, this.ts.getString(), this.ts.getNumber()); consumeToken(); return (AstNode)numberLiteral; }  if (this.compilerEnv.isReservedKeywordAsIdentifier() && TokenStream.isKeyword(this.ts.getString())) { name1 = createNameNode(); } else { reportError("msg.bad.prop"); return null; }  consumeToken(); return (AstNode)name1;
  }




  
  private ObjectProperty plainProperty(AstNode property, int ptt) throws IOException {
    int tt = peekToken();
    if ((tt == 89 || tt == 86) && ptt == 39 && this.compilerEnv.getLanguageVersion() >= 180) {
      
      if (!this.inDestructuringAssignment) {
        reportError("msg.bad.object.init");
      }
      Name name = new Name(property.getPosition(), property.getString());
      ObjectProperty objectProperty = new ObjectProperty();
      objectProperty.putProp(26, Boolean.TRUE);
      objectProperty.setLeftAndRight(property, (AstNode)name);
      return objectProperty;
    } 
    mustMatchToken(103, "msg.no.colon.prop");
    ObjectProperty pn = new ObjectProperty();
    pn.setOperatorPosition(this.ts.tokenBeg);
    pn.setLeftAndRight(property, assignExpr());
    return pn;
  }



  
  private ObjectProperty getterSetterProperty(int pos, AstNode propName, boolean isGetter) throws IOException {
    FunctionNode fn = function(2);
    
    Name name = fn.getFunctionName();
    if (name != null && name.length() != 0) {
      reportError("msg.bad.prop");
    }
    ObjectProperty pn = new ObjectProperty(pos);
    if (isGetter) {
      pn.setIsGetter();
      fn.setFunctionIsGetter();
    } else {
      pn.setIsSetter();
      fn.setFunctionIsSetter();
    } 
    int end = getNodeEnd((AstNode)fn);
    pn.setLeft(propName);
    pn.setRight((AstNode)fn);
    pn.setLength(end - pos);
    return pn;
  }
  
  private Name createNameNode() {
    return createNameNode(false, 39);
  }







  
  private Name createNameNode(boolean checkActivation, int token) {
    int beg = this.ts.tokenBeg;
    String s = this.ts.getString();
    int lineno = this.ts.lineno;
    if (!"".equals(this.prevNameTokenString)) {
      beg = this.prevNameTokenStart;
      s = this.prevNameTokenString;
      lineno = this.prevNameTokenLineno;
      this.prevNameTokenStart = 0;
      this.prevNameTokenString = "";
      this.prevNameTokenLineno = 0;
    } 
    if (s == null) {
      if (this.compilerEnv.isIdeMode()) {
        s = "";
      } else {
        codeBug();
      } 
    }
    Name name = new Name(beg, s);
    name.setLineno(lineno);
    if (checkActivation) {
      checkActivationName(s, token);
    }
    return name;
  }
  
  private StringLiteral createStringLiteral() {
    int pos = this.ts.tokenBeg, end = this.ts.tokenEnd;
    StringLiteral s = new StringLiteral(pos, end - pos);
    s.setLineno(this.ts.lineno);
    s.setValue(this.ts.getString());
    s.setQuoteCharacter(this.ts.getQuoteChar());
    return s;
  }
  
  protected void checkActivationName(String name, int token) {
    if (!insideFunction()) {
      return;
    }
    boolean activation = false;
    if ("arguments".equals(name) || (this.compilerEnv.getActivationNames() != null && this.compilerEnv.getActivationNames().contains(name))) {


      
      activation = true;
    } else if ("length".equals(name) && 
      token == 33 && this.compilerEnv.getLanguageVersion() == 120) {


      
      activation = true;
    } 
    
    if (activation) {
      setRequiresActivation();
    }
  }
  
  protected void setRequiresActivation() {
    if (insideFunction()) {
      ((FunctionNode)this.currentScriptOrFn).setRequiresActivation();
    }
  }
  
  private void checkCallRequiresActivation(AstNode pn) {
    if ((pn.getType() == 39 && "eval".equals(((Name)pn).getIdentifier())) || (pn.getType() == 33 && "eval".equals(((PropertyGet)pn).getProperty().getIdentifier())))
    {

      
      setRequiresActivation(); } 
  }
  
  protected void setIsGenerator() {
    if (insideFunction()) {
      ((FunctionNode)this.currentScriptOrFn).setIsGenerator();
    }
  }
  
  private void checkBadIncDec(UnaryExpression expr) {
    AstNode op = removeParens(expr.getOperand());
    int tt = op.getType();
    if (tt != 39 && tt != 33 && tt != 36 && tt != 67 && tt != 38)
    {


      
      reportError((expr.getType() == 106) ? "msg.bad.incr" : "msg.bad.decr");
    }
  }

  
  private ErrorNode makeErrorNode() {
    ErrorNode pn = new ErrorNode(this.ts.tokenBeg, this.ts.tokenEnd - this.ts.tokenBeg);
    pn.setLineno(this.ts.lineno);
    return pn;
  }

  
  private int nodeEnd(AstNode node) {
    return node.getPosition() + node.getLength();
  }
  
  private void saveNameTokenData(int pos, String name, int lineno) {
    this.prevNameTokenStart = pos;
    this.prevNameTokenString = name;
    this.prevNameTokenLineno = lineno;
  }













  
  private int lineBeginningFor(int pos) {
    if (this.sourceChars == null) {
      return -1;
    }
    if (pos <= 0) {
      return 0;
    }
    char[] buf = this.sourceChars;
    if (pos >= buf.length) {
      pos = buf.length - 1;
    }
    while (--pos >= 0) {
      char c = buf[pos];
      if (ScriptRuntime.isJSLineTerminator(c)) {
        return pos + 1;
      }
    } 
    return 0;
  }



  
  private void warnMissingSemi(int pos, int end) {
    if (this.compilerEnv.isStrictMode()) {
      int[] linep = new int[2];
      String line = this.ts.getLine(end, linep);


      
      int beg = this.compilerEnv.isIdeMode() ? Math.max(pos, end - linep[1]) : pos;

      
      if (line != null) {
        addStrictWarning("msg.missing.semi", "", beg, end - beg, linep[0], line, linep[1]);
      }
      else {
        
        addStrictWarning("msg.missing.semi", "", beg, end - beg);
      } 
    } 
  }
  
  private void warnTrailingComma(int pos, List<?> elems, int commaPos) {
    if (this.compilerEnv.getWarnTrailingComma()) {
      
      if (!elems.isEmpty()) {
        pos = ((AstNode)elems.get(0)).getPosition();
      }
      pos = Math.max(pos, lineBeginningFor(commaPos));
      addWarning("msg.extra.trailing.comma", pos, commaPos - pos);
    } 
  }

  
  private String readFully(Reader reader) throws IOException {
    BufferedReader in = new BufferedReader(reader);
    try {
      char[] cbuf = new char[1024];
      StringBuilder sb = new StringBuilder(1024);
      int bytes_read;
      while ((bytes_read = in.read(cbuf, 0, 1024)) != -1) {
        sb.append(cbuf, 0, bytes_read);
      }
      return sb.toString();
    } finally {
      in.close();
    } 
  }

  
  protected class PerFunctionVariables
  {
    private ScriptNode savedCurrentScriptOrFn;
    private Scope savedCurrentScope;
    private int savedEndFlags;
    private boolean savedInForInit;
    private Map<String, LabeledStatement> savedLabelSet;
    private List<Loop> savedLoopSet;
    private List<Jump> savedLoopAndSwitchSet;
    
    PerFunctionVariables(FunctionNode fnNode) {
      this.savedCurrentScriptOrFn = Parser.this.currentScriptOrFn;
      Parser.this.currentScriptOrFn = (ScriptNode)fnNode;
      
      this.savedCurrentScope = Parser.this.currentScope;
      Parser.this.currentScope = (Scope)fnNode;
      
      this.savedLabelSet = Parser.this.labelSet;
      Parser.this.labelSet = null;
      
      this.savedLoopSet = Parser.this.loopSet;
      Parser.this.loopSet = null;
      
      this.savedLoopAndSwitchSet = Parser.this.loopAndSwitchSet;
      Parser.this.loopAndSwitchSet = null;
      
      this.savedEndFlags = Parser.this.endFlags;
      Parser.this.endFlags = 0;
      
      this.savedInForInit = Parser.this.inForInit;
      Parser.this.inForInit = false;
    }
    
    void restore() {
      Parser.this.currentScriptOrFn = this.savedCurrentScriptOrFn;
      Parser.this.currentScope = this.savedCurrentScope;
      Parser.this.labelSet = this.savedLabelSet;
      Parser.this.loopSet = this.savedLoopSet;
      Parser.this.loopAndSwitchSet = this.savedLoopAndSwitchSet;
      Parser.this.endFlags = this.savedEndFlags;
      Parser.this.inForInit = this.savedInForInit;
    }
  }













  
  Node createDestructuringAssignment(int type, Node left, Node right) {
    String tempName = this.currentScriptOrFn.getNextTempName();
    Node result = destructuringAssignmentHelper(type, left, right, tempName);
    
    Node comma = result.getLastChild();
    comma.addChildToBack(createName(tempName));
    return result;
  }


  
  Node destructuringAssignmentHelper(int variableType, Node left, Node right, String tempName) {
    Scope result = createScopeNode(158, left.getLineno());
    result.addChildToFront(new Node(153, createName(39, tempName, right)));
    
    try {
      pushScope(result);
      defineSymbol(153, tempName, true);
    } finally {
      popScope();
    } 
    Node comma = new Node(89);
    result.addChildToBack(comma);
    List<String> destructuringNames = new ArrayList<String>();
    boolean empty = true;
    switch (left.getType()) {
      case 65:
        empty = destructuringArray((ArrayLiteral)left, variableType, tempName, comma, destructuringNames);
        break;

      
      case 66:
        empty = destructuringObject((ObjectLiteral)left, variableType, tempName, comma, destructuringNames);
        break;

      
      case 33:
      case 36:
        switch (variableType) {
          case 122:
          case 153:
          case 154:
            reportError("msg.bad.assign.left"); break;
        } 
        comma.addChildToBack(simpleAssignment(left, createName(tempName)));
        break;
      default:
        reportError("msg.bad.assign.left"); break;
    } 
    if (empty)
    {
      comma.addChildToBack(createNumber(0.0D));
    }
    result.putProp(22, destructuringNames);
    return (Node)result;
  }





  
  boolean destructuringArray(ArrayLiteral array, int variableType, String tempName, Node parent, List<String> destructuringNames) {
    boolean empty = true;
    int setOp = (variableType == 154) ? 155 : 8;
    
    int index = 0;
    for (AstNode n : array.getElements()) {
      if (n.getType() == 128) {
        index++;
        continue;
      } 
      Node rightElem = new Node(36, createName(tempName), createNumber(index));

      
      if (n.getType() == 39) {
        String name = n.getString();
        parent.addChildToBack(new Node(setOp, createName(49, name, null), rightElem));


        
        if (variableType != -1) {
          defineSymbol(variableType, name, true);
          destructuringNames.add(name);
        } 
      } else {
        parent.addChildToBack(destructuringAssignmentHelper(variableType, (Node)n, rightElem, this.currentScriptOrFn.getNextTempName()));
      } 



      
      index++;
      empty = false;
    } 
    return empty;
  }





  
  boolean destructuringObject(ObjectLiteral node, int variableType, String tempName, Node parent, List<String> destructuringNames) {
    boolean empty = true;
    int setOp = (variableType == 154) ? 155 : 8;

    
    for (ObjectProperty prop : node.getElements()) {
      int lineno = 0;


      
      if (this.ts != null) {
        lineno = this.ts.lineno;
      }
      AstNode id = prop.getLeft();
      Node rightElem = null;
      if (id instanceof Name) {
        Node s = Node.newString(((Name)id).getIdentifier());
        rightElem = new Node(33, createName(tempName), s);
      } else if (id instanceof StringLiteral) {
        Node s = Node.newString(((StringLiteral)id).getValue());
        rightElem = new Node(33, createName(tempName), s);
      } else if (id instanceof NumberLiteral) {
        Node s = createNumber((int)((NumberLiteral)id).getNumber());
        rightElem = new Node(36, createName(tempName), s);
      } else {
        throw codeBug();
      } 
      rightElem.setLineno(lineno);
      AstNode value = prop.getRight();
      if (value.getType() == 39) {
        String name = ((Name)value).getIdentifier();
        parent.addChildToBack(new Node(setOp, createName(49, name, null), rightElem));


        
        if (variableType != -1) {
          defineSymbol(variableType, name, true);
          destructuringNames.add(name);
        } 
      } else {
        parent.addChildToBack(destructuringAssignmentHelper(variableType, (Node)value, rightElem, this.currentScriptOrFn.getNextTempName()));
      } 


      
      empty = false;
    } 
    return empty;
  }
  
  protected Node createName(String name) {
    checkActivationName(name, 39);
    return Node.newString(39, name);
  }
  
  protected Node createName(int type, String name, Node child) {
    Node result = createName(name);
    result.setType(type);
    if (child != null)
      result.addChildToBack(child); 
    return result;
  }
  
  protected Node createNumber(double number) {
    return Node.newNumber(number);
  }








  
  protected Scope createScopeNode(int token, int lineno) {
    Scope scope = new Scope();
    scope.setType(token);
    scope.setLineno(lineno);
    return scope;
  }





















  
  protected Node simpleAssignment(Node left, Node right) {
    Node obj, ref, id;
    int type, nodeType = left.getType();
    switch (nodeType) {
      case 39:
        if (this.inUseStrictDirective && "eval".equals(((Name)left).getIdentifier()))
        {
          
          reportError("msg.bad.id.strict", ((Name)left).getIdentifier());
        }
        
        left.setType(49);
        return new Node(8, left, right);





      
      case 33:
      case 36:
        if (left instanceof PropertyGet) {
          AstNode astNode = ((PropertyGet)left).getTarget();
          Name name = ((PropertyGet)left).getProperty();
        } else if (left instanceof ElementGet) {
          AstNode astNode1 = ((ElementGet)left).getTarget();
          AstNode astNode2 = ((ElementGet)left).getElement();
        } else {
          
          obj = left.getFirstChild();
          id = left.getLastChild();
        } 
        
        if (nodeType == 33) {
          type = 35;




          
          id.setType(41);
        } else {
          type = 37;
        } 
        return new Node(type, obj, id, right);
      
      case 67:
        ref = left.getFirstChild();
        checkMutableReference(ref);
        return new Node(68, ref, right);
    } 

    
    throw codeBug();
  }
  
  protected void checkMutableReference(Node n) {
    int memberTypeFlags = n.getIntProp(16, 0);
    if ((memberTypeFlags & 0x4) != 0) {
      reportError("msg.bad.assign.left");
    }
  }

  
  protected AstNode removeParens(AstNode node) {
    while (node instanceof ParenthesizedExpression) {
      node = ((ParenthesizedExpression)node).getExpression();
    }
    return node;
  }
  
  void markDestructuring(AstNode node) {
    if (node instanceof DestructuringForm) {
      ((DestructuringForm)node).setIsDestructuring(true);
    } else if (node instanceof ParenthesizedExpression) {
      markDestructuring(((ParenthesizedExpression)node).getExpression());
    } 
  }



  
  private RuntimeException codeBug() throws RuntimeException {
    throw Kit.codeBug("ts.cursor=" + this.ts.cursor + ", ts.tokenBeg=" + this.ts.tokenBeg + ", currentToken=" + this.currentToken);
  }
}
