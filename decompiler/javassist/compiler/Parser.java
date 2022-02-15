package javassist.compiler;

import javassist.compiler.ast.ASTList;
import javassist.compiler.ast.ASTree;
import javassist.compiler.ast.ArrayInit;
import javassist.compiler.ast.AssignExpr;
import javassist.compiler.ast.BinExpr;
import javassist.compiler.ast.CallExpr;
import javassist.compiler.ast.CastExpr;
import javassist.compiler.ast.CondExpr;
import javassist.compiler.ast.Declarator;
import javassist.compiler.ast.DoubleConst;
import javassist.compiler.ast.Expr;
import javassist.compiler.ast.FieldDecl;
import javassist.compiler.ast.InstanceOfExpr;
import javassist.compiler.ast.IntConst;
import javassist.compiler.ast.Keyword;
import javassist.compiler.ast.Member;
import javassist.compiler.ast.MethodDecl;
import javassist.compiler.ast.NewExpr;
import javassist.compiler.ast.Pair;
import javassist.compiler.ast.Stmnt;
import javassist.compiler.ast.StringL;
import javassist.compiler.ast.Symbol;
import javassist.compiler.ast.Variable;















public final class Parser
  implements TokenId
{
  private Lex lex;
  
  public Parser(Lex lex) {
    this.lex = lex;
  }
  public boolean hasMore() {
    return (this.lex.lookAhead() >= 0);
  }


  
  public ASTList parseMember(SymbolTable tbl) throws CompileError {
    ASTList mem = parseMember1(tbl);
    if (mem instanceof MethodDecl)
      return (ASTList)parseMethod2(tbl, (MethodDecl)mem); 
    return mem;
  }
  
  public ASTList parseMember1(SymbolTable tbl) throws CompileError {
    Declarator d;
    String name;
    ASTList mods = parseMemberMods();
    
    boolean isConstructor = false;
    if (this.lex.lookAhead() == 400 && this.lex.lookAhead(1) == 40) {
      d = new Declarator(344, 0);
      isConstructor = true;
    } else {
      
      d = parseFormalType(tbl);
    } 
    if (this.lex.get() != 400) {
      throw new SyntaxError(this.lex);
    }
    
    if (isConstructor) {
      name = "<init>";
    } else {
      name = this.lex.getString();
    } 
    d.setVariable(new Symbol(name));
    if (isConstructor || this.lex.lookAhead() == 40)
      return (ASTList)parseMethod1(tbl, isConstructor, mods, d); 
    return (ASTList)parseField(tbl, mods, d);
  }







  
  private FieldDecl parseField(SymbolTable tbl, ASTList mods, Declarator d) throws CompileError {
    ASTree expr = null;
    if (this.lex.lookAhead() == 61) {
      this.lex.get();
      expr = parseExpression(tbl);
    } 
    
    int c = this.lex.get();
    if (c == 59)
      return new FieldDecl((ASTree)mods, new ASTList((ASTree)d, new ASTList(expr))); 
    if (c == 44) {
      throw new CompileError("only one field can be declared in one declaration", this.lex);
    }
    
    throw new SyntaxError(this.lex);
  }













  
  private MethodDecl parseMethod1(SymbolTable tbl, boolean isConstructor, ASTList mods, Declarator d) throws CompileError {
    if (this.lex.get() != 40) {
      throw new SyntaxError(this.lex);
    }
    ASTList parms = null;
    if (this.lex.lookAhead() != 41)
      while (true) {
        parms = ASTList.append(parms, (ASTree)parseFormalParam(tbl));
        int t = this.lex.lookAhead();
        if (t == 44) {
          this.lex.get(); continue;
        }  if (t == 41) {
          break;
        }
      }  
    this.lex.get();
    d.addArrayDim(parseArrayDimension());
    if (isConstructor && d.getArrayDim() > 0) {
      throw new SyntaxError(this.lex);
    }
    ASTList throwsList = null;
    if (this.lex.lookAhead() == 341) {
      this.lex.get();
      while (true) {
        throwsList = ASTList.append(throwsList, (ASTree)parseClassType(tbl));
        if (this.lex.lookAhead() == 44) {
          this.lex.get();
          continue;
        } 
        break;
      } 
    } 
    return new MethodDecl((ASTree)mods, new ASTList((ASTree)d, 
          ASTList.make((ASTree)parms, (ASTree)throwsList, null)));
  }




  
  public MethodDecl parseMethod2(SymbolTable tbl, MethodDecl md) throws CompileError {
    Stmnt body = null;
    if (this.lex.lookAhead() == 59) {
      this.lex.get();
    } else {
      body = parseBlock(tbl);
      if (body == null) {
        body = new Stmnt(66);
      }
    } 
    md.sublist(4).setHead((ASTree)body);
    return md;
  }






  
  private ASTList parseMemberMods() {
    ASTList list = null;
    while (true) {
      int t = this.lex.lookAhead();
      if (t == 300 || t == 315 || t == 332 || t == 331 || t == 330 || t == 338 || t == 335 || t == 345 || t == 342 || t == 347) {

        
        list = new ASTList((ASTree)new Keyword(this.lex.get()), list);
        continue;
      } 
      break;
    } 
    return list;
  }


  
  private Declarator parseFormalType(SymbolTable tbl) throws CompileError {
    int t = this.lex.lookAhead();
    if (isBuiltinType(t) || t == 344) {
      this.lex.get();
      int i = parseArrayDimension();
      return new Declarator(t, i);
    } 
    ASTList name = parseClassType(tbl);
    int dim = parseArrayDimension();
    return new Declarator(name, dim);
  }
  
  private static boolean isBuiltinType(int t) {
    return (t == 301 || t == 303 || t == 306 || t == 334 || t == 324 || t == 326 || t == 317 || t == 312);
  }





  
  private Declarator parseFormalParam(SymbolTable tbl) throws CompileError {
    Declarator d = parseFormalType(tbl);
    if (this.lex.get() != 400) {
      throw new SyntaxError(this.lex);
    }
    String name = this.lex.getString();
    d.setVariable(new Symbol(name));
    d.addArrayDim(parseArrayDimension());
    tbl.append(name, d);
    return d;
  }





















  
  public Stmnt parseStatement(SymbolTable tbl) throws CompileError {
    int t = this.lex.lookAhead();
    if (t == 123)
      return parseBlock(tbl); 
    if (t == 59) {
      this.lex.get();
      return new Stmnt(66);
    } 
    if (t == 400 && this.lex.lookAhead(1) == 58) {
      this.lex.get();
      String label = this.lex.getString();
      this.lex.get();
      return Stmnt.make(76, (ASTree)new Symbol(label), (ASTree)parseStatement(tbl));
    } 
    if (t == 320)
      return parseIf(tbl); 
    if (t == 346)
      return parseWhile(tbl); 
    if (t == 311)
      return parseDo(tbl); 
    if (t == 318)
      return parseFor(tbl); 
    if (t == 343)
      return parseTry(tbl); 
    if (t == 337)
      return parseSwitch(tbl); 
    if (t == 338)
      return parseSynchronized(tbl); 
    if (t == 333)
      return parseReturn(tbl); 
    if (t == 340)
      return parseThrow(tbl); 
    if (t == 302)
      return parseBreak(tbl); 
    if (t == 309) {
      return parseContinue(tbl);
    }
    return parseDeclarationOrExpression(tbl, false);
  }


  
  private Stmnt parseBlock(SymbolTable tbl) throws CompileError {
    if (this.lex.get() != 123) {
      throw new SyntaxError(this.lex);
    }
    Stmnt body = null;
    SymbolTable tbl2 = new SymbolTable(tbl);
    while (this.lex.lookAhead() != 125) {
      Stmnt s = parseStatement(tbl2);
      if (s != null) {
        body = (Stmnt)ASTList.concat((ASTList)body, (ASTList)new Stmnt(66, (ASTree)s));
      }
    } 
    this.lex.get();
    if (body == null)
      return new Stmnt(66); 
    return body;
  }


  
  private Stmnt parseIf(SymbolTable tbl) throws CompileError {
    Stmnt elsep;
    int t = this.lex.get();
    ASTree expr = parseParExpression(tbl);
    Stmnt thenp = parseStatement(tbl);
    
    if (this.lex.lookAhead() == 313) {
      this.lex.get();
      elsep = parseStatement(tbl);
    } else {
      
      elsep = null;
    } 
    return new Stmnt(t, expr, new ASTList((ASTree)thenp, new ASTList((ASTree)elsep)));
  }




  
  private Stmnt parseWhile(SymbolTable tbl) throws CompileError {
    int t = this.lex.get();
    ASTree expr = parseParExpression(tbl);
    Stmnt body = parseStatement(tbl);
    return new Stmnt(t, expr, (ASTList)body);
  }


  
  private Stmnt parseDo(SymbolTable tbl) throws CompileError {
    int t = this.lex.get();
    Stmnt body = parseStatement(tbl);
    if (this.lex.get() != 346 || this.lex.get() != 40) {
      throw new SyntaxError(this.lex);
    }
    ASTree expr = parseExpression(tbl);
    if (this.lex.get() != 41 || this.lex.get() != 59) {
      throw new SyntaxError(this.lex);
    }
    return new Stmnt(t, expr, (ASTList)body);
  }



  
  private Stmnt parseFor(SymbolTable tbl) throws CompileError {
    Stmnt expr1, expr3;
    ASTree expr2;
    int t = this.lex.get();
    
    SymbolTable tbl2 = new SymbolTable(tbl);
    
    if (this.lex.get() != 40) {
      throw new SyntaxError(this.lex);
    }
    if (this.lex.lookAhead() == 59) {
      this.lex.get();
      expr1 = null;
    } else {
      
      expr1 = parseDeclarationOrExpression(tbl2, true);
    } 
    if (this.lex.lookAhead() == 59) {
      expr2 = null;
    } else {
      expr2 = parseExpression(tbl2);
    } 
    if (this.lex.get() != 59) {
      throw new CompileError("; is missing", this.lex);
    }
    if (this.lex.lookAhead() == 41) {
      expr3 = null;
    } else {
      expr3 = parseExprList(tbl2);
    } 
    if (this.lex.get() != 41) {
      throw new CompileError(") is missing", this.lex);
    }
    Stmnt body = parseStatement(tbl2);
    return new Stmnt(t, (ASTree)expr1, new ASTList(expr2, new ASTList((ASTree)expr3, (ASTList)body)));
  }








  
  private Stmnt parseSwitch(SymbolTable tbl) throws CompileError {
    int t = this.lex.get();
    ASTree expr = parseParExpression(tbl);
    Stmnt body = parseSwitchBlock(tbl);
    return new Stmnt(t, expr, (ASTList)body);
  }
  
  private Stmnt parseSwitchBlock(SymbolTable tbl) throws CompileError {
    if (this.lex.get() != 123) {
      throw new SyntaxError(this.lex);
    }
    SymbolTable tbl2 = new SymbolTable(tbl);
    Stmnt s = parseStmntOrCase(tbl2);
    if (s == null) {
      throw new CompileError("empty switch block", this.lex);
    }
    int op = s.getOperator();
    if (op != 304 && op != 310) {
      throw new CompileError("no case or default in a switch block", this.lex);
    }
    
    Stmnt body = new Stmnt(66, (ASTree)s);
    while (this.lex.lookAhead() != 125) {
      Stmnt s2 = parseStmntOrCase(tbl2);
      if (s2 != null) {
        int op2 = s2.getOperator();
        if (op2 == 304 || op2 == 310) {
          body = (Stmnt)ASTList.concat((ASTList)body, (ASTList)new Stmnt(66, (ASTree)s2));
          s = s2;
          continue;
        } 
        s = (Stmnt)ASTList.concat((ASTList)s, (ASTList)new Stmnt(66, (ASTree)s2));
      } 
    } 
    
    this.lex.get();
    return body;
  }
  private Stmnt parseStmntOrCase(SymbolTable tbl) throws CompileError {
    Stmnt s;
    int t = this.lex.lookAhead();
    if (t != 304 && t != 310) {
      return parseStatement(tbl);
    }
    this.lex.get();
    
    if (t == 304) {
      s = new Stmnt(t, parseExpression(tbl));
    } else {
      s = new Stmnt(310);
    } 
    if (this.lex.get() != 58) {
      throw new CompileError(": is missing", this.lex);
    }
    return s;
  }



  
  private Stmnt parseSynchronized(SymbolTable tbl) throws CompileError {
    int t = this.lex.get();
    if (this.lex.get() != 40) {
      throw new SyntaxError(this.lex);
    }
    ASTree expr = parseExpression(tbl);
    if (this.lex.get() != 41) {
      throw new SyntaxError(this.lex);
    }
    Stmnt body = parseBlock(tbl);
    return new Stmnt(t, expr, (ASTList)body);
  }





  
  private Stmnt parseTry(SymbolTable tbl) throws CompileError {
    this.lex.get();
    Stmnt block = parseBlock(tbl);
    ASTList catchList = null;
    while (this.lex.lookAhead() == 305) {
      this.lex.get();
      if (this.lex.get() != 40) {
        throw new SyntaxError(this.lex);
      }
      SymbolTable tbl2 = new SymbolTable(tbl);
      Declarator d = parseFormalParam(tbl2);
      if (d.getArrayDim() > 0 || d.getType() != 307) {
        throw new SyntaxError(this.lex);
      }
      if (this.lex.get() != 41) {
        throw new SyntaxError(this.lex);
      }
      Stmnt b = parseBlock(tbl2);
      catchList = ASTList.append(catchList, (ASTree)new Pair((ASTree)d, (ASTree)b));
    } 
    
    Stmnt finallyBlock = null;
    if (this.lex.lookAhead() == 316) {
      this.lex.get();
      finallyBlock = parseBlock(tbl);
    } 
    
    return Stmnt.make(343, (ASTree)block, (ASTree)catchList, (ASTree)finallyBlock);
  }


  
  private Stmnt parseReturn(SymbolTable tbl) throws CompileError {
    int t = this.lex.get();
    Stmnt s = new Stmnt(t);
    if (this.lex.lookAhead() != 59) {
      s.setLeft(parseExpression(tbl));
    }
    if (this.lex.get() != 59) {
      throw new CompileError("; is missing", this.lex);
    }
    return s;
  }


  
  private Stmnt parseThrow(SymbolTable tbl) throws CompileError {
    int t = this.lex.get();
    ASTree expr = parseExpression(tbl);
    if (this.lex.get() != 59) {
      throw new CompileError("; is missing", this.lex);
    }
    return new Stmnt(t, expr);
  }




  
  private Stmnt parseBreak(SymbolTable tbl) throws CompileError {
    return parseContinue(tbl);
  }




  
  private Stmnt parseContinue(SymbolTable tbl) throws CompileError {
    int t = this.lex.get();
    Stmnt s = new Stmnt(t);
    int t2 = this.lex.get();
    if (t2 == 400) {
      s.setLeft((ASTree)new Symbol(this.lex.getString()));
      t2 = this.lex.get();
    } 
    
    if (t2 != 59) {
      throw new CompileError("; is missing", this.lex);
    }
    return s;
  }











  
  private Stmnt parseDeclarationOrExpression(SymbolTable tbl, boolean exprList) throws CompileError {
    Stmnt expr;
    int t = this.lex.lookAhead();
    while (t == 315) {
      this.lex.get();
      t = this.lex.lookAhead();
    } 
    
    if (isBuiltinType(t)) {
      t = this.lex.get();
      int dim = parseArrayDimension();
      return parseDeclarators(tbl, new Declarator(t, dim));
    } 
    if (t == 400) {
      int i = nextIsClassType(0);
      if (i >= 0 && 
        this.lex.lookAhead(i) == 400) {
        ASTList name = parseClassType(tbl);
        int dim = parseArrayDimension();
        return parseDeclarators(tbl, new Declarator(name, dim));
      } 
    } 

    
    if (exprList) {
      expr = parseExprList(tbl);
    } else {
      expr = new Stmnt(69, parseExpression(tbl));
    } 
    if (this.lex.get() != 59) {
      throw new CompileError("; is missing", this.lex);
    }
    return expr;
  }


  
  private Stmnt parseExprList(SymbolTable tbl) throws CompileError {
    Stmnt expr = null;
    while (true) {
      Stmnt e = new Stmnt(69, parseExpression(tbl));
      expr = (Stmnt)ASTList.concat((ASTList)expr, (ASTList)new Stmnt(66, (ASTree)e));
      if (this.lex.lookAhead() == 44) {
        this.lex.get(); continue;
      }  break;
    }  return expr;
  }





  
  private Stmnt parseDeclarators(SymbolTable tbl, Declarator d) throws CompileError {
    Stmnt decl = null;
    while (true) {
      decl = (Stmnt)ASTList.concat((ASTList)decl, (ASTList)new Stmnt(68, (ASTree)
            parseDeclarator(tbl, d)));
      int t = this.lex.get();
      if (t == 59)
        return decl; 
      if (t != 44) {
        throw new CompileError("; is missing", this.lex);
      }
    } 
  }



  
  private Declarator parseDeclarator(SymbolTable tbl, Declarator d) throws CompileError {
    if (this.lex.get() != 400 || d.getType() == 344) {
      throw new SyntaxError(this.lex);
    }
    String name = this.lex.getString();
    Symbol symbol = new Symbol(name);
    int dim = parseArrayDimension();
    ASTree init = null;
    if (this.lex.lookAhead() == 61) {
      this.lex.get();
      init = parseInitializer(tbl);
    } 
    
    Declarator decl = d.make(symbol, dim, init);
    tbl.append(name, decl);
    return decl;
  }


  
  private ASTree parseInitializer(SymbolTable tbl) throws CompileError {
    if (this.lex.lookAhead() == 123)
      return (ASTree)parseArrayInitializer(tbl); 
    return parseExpression(tbl);
  }





  
  private ArrayInit parseArrayInitializer(SymbolTable tbl) throws CompileError {
    this.lex.get();
    if (this.lex.lookAhead() == 125) {
      this.lex.get();
      return new ArrayInit(null);
    } 
    ASTree expr = parseExpression(tbl);
    ArrayInit init = new ArrayInit(expr);
    while (this.lex.lookAhead() == 44) {
      this.lex.get();
      expr = parseExpression(tbl);
      ASTList.append((ASTList)init, expr);
    } 
    
    if (this.lex.get() != 125) {
      throw new SyntaxError(this.lex);
    }
    return init;
  }


  
  private ASTree parseParExpression(SymbolTable tbl) throws CompileError {
    if (this.lex.get() != 40) {
      throw new SyntaxError(this.lex);
    }
    ASTree expr = parseExpression(tbl);
    if (this.lex.get() != 41) {
      throw new SyntaxError(this.lex);
    }
    return expr;
  }



  
  public ASTree parseExpression(SymbolTable tbl) throws CompileError {
    ASTree left = parseConditionalExpr(tbl);
    if (!isAssignOp(this.lex.lookAhead())) {
      return left;
    }
    int t = this.lex.get();
    ASTree right = parseExpression(tbl);
    return (ASTree)AssignExpr.makeAssign(t, left, right);
  }
  
  private static boolean isAssignOp(int t) {
    return (t == 61 || t == 351 || t == 352 || t == 353 || t == 354 || t == 355 || t == 356 || t == 360 || t == 361 || t == 365 || t == 367 || t == 371);
  }






  
  private ASTree parseConditionalExpr(SymbolTable tbl) throws CompileError {
    ASTree cond = parseBinaryExpr(tbl);
    if (this.lex.lookAhead() == 63) {
      this.lex.get();
      ASTree thenExpr = parseExpression(tbl);
      if (this.lex.get() != 58) {
        throw new CompileError(": is missing", this.lex);
      }
      ASTree elseExpr = parseExpression(tbl);
      return (ASTree)new CondExpr(cond, thenExpr, elseExpr);
    } 
    return cond;
  }









































  
  private ASTree parseBinaryExpr(SymbolTable tbl) throws CompileError {
    ASTree expr = parseUnaryExpr(tbl);
    while (true) {
      int t = this.lex.lookAhead();
      int p = getOpPrecedence(t);
      if (p == 0)
        return expr; 
      expr = binaryExpr2(tbl, expr, p);
    } 
  }


  
  private ASTree parseInstanceOf(SymbolTable tbl, ASTree expr) throws CompileError {
    int t = this.lex.lookAhead();
    if (isBuiltinType(t)) {
      this.lex.get();
      int i = parseArrayDimension();
      return (ASTree)new InstanceOfExpr(t, i, expr);
    } 
    ASTList name = parseClassType(tbl);
    int dim = parseArrayDimension();
    return (ASTree)new InstanceOfExpr(name, dim, expr);
  }


  
  private ASTree binaryExpr2(SymbolTable tbl, ASTree expr, int prec) throws CompileError {
    int t = this.lex.get();
    if (t == 323) {
      return parseInstanceOf(tbl, expr);
    }
    ASTree expr2 = parseUnaryExpr(tbl);
    while (true) {
      int t2 = this.lex.lookAhead();
      int p2 = getOpPrecedence(t2);
      if (p2 != 0 && prec > p2) {
        expr2 = binaryExpr2(tbl, expr2, p2); continue;
      }  break;
    }  return (ASTree)BinExpr.makeBin(t, expr, expr2);
  }


  
  private static final int[] binaryOpPrecedence = new int[] { 0, 0, 0, 0, 1, 6, 0, 0, 0, 1, 2, 0, 2, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 4, 0 };




  
  private int getOpPrecedence(int c) {
    if (33 <= c && c <= 63)
      return binaryOpPrecedence[c - 33]; 
    if (c == 94)
      return 7; 
    if (c == 124)
      return 8; 
    if (c == 369)
      return 9; 
    if (c == 368)
      return 10; 
    if (c == 358 || c == 350)
      return 5; 
    if (c == 357 || c == 359 || c == 323)
      return 4; 
    if (c == 364 || c == 366 || c == 370) {
      return 3;
    }
    return 0;
  }









  
  private ASTree parseUnaryExpr(SymbolTable tbl) throws CompileError {
    int t;
    switch (this.lex.lookAhead()) {
      case 33:
      case 43:
      case 45:
      case 126:
      case 362:
      case 363:
        t = this.lex.get();
        if (t == 45) {
          int t2 = this.lex.lookAhead();
          switch (t2) {
            case 401:
            case 402:
            case 403:
              this.lex.get();
              return (ASTree)new IntConst(-this.lex.getLong(), t2);
            case 404:
            case 405:
              this.lex.get();
              return (ASTree)new DoubleConst(-this.lex.getDouble(), t2);
          } 


        
        } 
        return (ASTree)Expr.make(t, parseUnaryExpr(tbl));
      case 40:
        return parseCast(tbl);
    } 
    return parsePostfix(tbl);
  }









  
  private ASTree parseCast(SymbolTable tbl) throws CompileError {
    int t = this.lex.lookAhead(1);
    if (isBuiltinType(t) && nextIsBuiltinCast()) {
      this.lex.get();
      this.lex.get();
      int dim = parseArrayDimension();
      if (this.lex.get() != 41) {
        throw new CompileError(") is missing", this.lex);
      }
      return (ASTree)new CastExpr(t, dim, parseUnaryExpr(tbl));
    } 
    if (t == 400 && nextIsClassCast()) {
      this.lex.get();
      ASTList name = parseClassType(tbl);
      int dim = parseArrayDimension();
      if (this.lex.get() != 41) {
        throw new CompileError(") is missing", this.lex);
      }
      return (ASTree)new CastExpr(name, dim, parseUnaryExpr(tbl));
    } 
    
    return parsePostfix(tbl);
  }


  
  private boolean nextIsBuiltinCast() {
    int i = 2; int t;
    while ((t = this.lex.lookAhead(i++)) == 91) {
      if (this.lex.lookAhead(i++) != 93)
        return false; 
    } 
    return (this.lex.lookAhead(i - 1) == 41);
  }
  
  private boolean nextIsClassCast() {
    int i = nextIsClassType(1);
    if (i < 0) {
      return false;
    }
    int t = this.lex.lookAhead(i);
    if (t != 41) {
      return false;
    }
    t = this.lex.lookAhead(i + 1);
    return (t == 40 || t == 412 || t == 406 || t == 400 || t == 339 || t == 336 || t == 328 || t == 410 || t == 411 || t == 403 || t == 402 || t == 401 || t == 405 || t == 404);
  }






  
  private int nextIsClassType(int i) {
    while (this.lex.lookAhead(++i) == 46) {
      if (this.lex.lookAhead(++i) != 400)
        return -1; 
    }  int t;
    while ((t = this.lex.lookAhead(i++)) == 91) {
      if (this.lex.lookAhead(i++) != 93)
        return -1; 
    } 
    return i - 1;
  }


  
  private int parseArrayDimension() throws CompileError {
    int arrayDim = 0;
    while (this.lex.lookAhead() == 91) {
      arrayDim++;
      this.lex.get();
      if (this.lex.get() != 93) {
        throw new CompileError("] is missing", this.lex);
      }
    } 
    return arrayDim;
  }


  
  private ASTList parseClassType(SymbolTable tbl) throws CompileError {
    ASTList list = null;
    while (true) {
      if (this.lex.get() != 400) {
        throw new SyntaxError(this.lex);
      }
      list = ASTList.append(list, (ASTree)new Symbol(this.lex.getString()));
      if (this.lex.lookAhead() == 46) {
        this.lex.get();
        continue;
      } 
      break;
    } 
    return list;
  }
















  
  private ASTree parsePostfix(SymbolTable tbl) throws CompileError {
    Expr expr1;
    int token = this.lex.lookAhead();
    switch (token) {
      case 401:
      case 402:
      case 403:
        this.lex.get();
        return (ASTree)new IntConst(this.lex.getLong(), token);
      case 404:
      case 405:
        this.lex.get();
        return (ASTree)new DoubleConst(this.lex.getDouble(), token);
    } 




    
    ASTree expr = parsePrimaryExpr(tbl); while (true) {
      String str; ASTree index; Expr expr2; ASTree aSTree1;
      int t;
      switch (this.lex.lookAhead()) {
        case 40:
          expr = parseMethodCall(tbl, expr);
          continue;
        case 91:
          if (this.lex.lookAhead(1) == 93) {
            int dim = parseArrayDimension();
            if (this.lex.get() != 46 || this.lex.get() != 307) {
              throw new SyntaxError(this.lex);
            }
            expr = parseDotClass(expr, dim);
            continue;
          } 
          index = parseArrayIndex(tbl);
          if (index == null) {
            throw new SyntaxError(this.lex);
          }
          expr2 = Expr.make(65, expr, index);
          continue;
        
        case 362:
        case 363:
          t = this.lex.get();
          expr2 = Expr.make(t, null, (ASTree)expr2);
          continue;
        case 46:
          this.lex.get();
          t = this.lex.get();
          if (t == 307) {
            aSTree1 = parseDotClass((ASTree)expr2, 0); continue;
          }  if (t == 336) {
            expr1 = Expr.make(46, (ASTree)new Symbol(toClassName(aSTree1)), (ASTree)new Keyword(t)); continue;
          }  if (t == 400) {
            String str1 = this.lex.getString();
            expr1 = Expr.make(46, (ASTree)expr1, (ASTree)new Member(str1));
            continue;
          } 
          throw new CompileError("missing member name", this.lex);
        
        case 35:
          this.lex.get();
          t = this.lex.get();
          if (t != 400) {
            throw new CompileError("missing static member name", this.lex);
          }
          str = this.lex.getString();
          expr1 = Expr.make(35, (ASTree)new Symbol(toClassName((ASTree)expr1)), (ASTree)new Member(str)); continue;
      } 
      break;
    } 
    return (ASTree)expr1;
  }








  
  private ASTree parseDotClass(ASTree className, int dim) throws CompileError {
    String cname = toClassName(className);
    if (dim > 0) {
      StringBuffer sbuf = new StringBuffer();
      while (dim-- > 0) {
        sbuf.append('[');
      }
      sbuf.append('L').append(cname.replace('.', '/')).append(';');
      cname = sbuf.toString();
    } 
    
    return (ASTree)Expr.make(46, (ASTree)new Symbol(cname), (ASTree)new Member("class"));
  }





  
  private ASTree parseDotClass(int builtinType, int dim) throws CompileError {
    String cname;
    if (dim > 0) {
      String str = CodeGen.toJvmTypeName(builtinType, dim);
      return (ASTree)Expr.make(46, (ASTree)new Symbol(str), (ASTree)new Member("class"));
    } 
    
    switch (builtinType) {
      case 301:
        cname = "java.lang.Boolean";





























        
        return (ASTree)Expr.make(35, (ASTree)new Symbol(cname), (ASTree)new Member("TYPE"));case 303: cname = "java.lang.Byte"; return (ASTree)Expr.make(35, (ASTree)new Symbol(cname), (ASTree)new Member("TYPE"));case 306: cname = "java.lang.Character"; return (ASTree)Expr.make(35, (ASTree)new Symbol(cname), (ASTree)new Member("TYPE"));case 334: cname = "java.lang.Short"; return (ASTree)Expr.make(35, (ASTree)new Symbol(cname), (ASTree)new Member("TYPE"));case 324: cname = "java.lang.Integer"; return (ASTree)Expr.make(35, (ASTree)new Symbol(cname), (ASTree)new Member("TYPE"));case 326: cname = "java.lang.Long"; return (ASTree)Expr.make(35, (ASTree)new Symbol(cname), (ASTree)new Member("TYPE"));case 317: cname = "java.lang.Float"; return (ASTree)Expr.make(35, (ASTree)new Symbol(cname), (ASTree)new Member("TYPE"));case 312: cname = "java.lang.Double"; return (ASTree)Expr.make(35, (ASTree)new Symbol(cname), (ASTree)new Member("TYPE"));case 344: cname = "java.lang.Void"; return (ASTree)Expr.make(35, (ASTree)new Symbol(cname), (ASTree)new Member("TYPE"));
    } 
    throw new CompileError("invalid builtin type: " + builtinType);
  }





  
  private ASTree parseMethodCall(SymbolTable tbl, ASTree expr) throws CompileError {
    if (expr instanceof Keyword) {
      int token = ((Keyword)expr).get();
      if (token != 339 && token != 336) {
        throw new SyntaxError(this.lex);
      }
    } else if (!(expr instanceof Symbol)) {
      
      if (expr instanceof Expr) {
        int op = ((Expr)expr).getOperator();
        if (op != 46 && op != 35)
          throw new SyntaxError(this.lex); 
      } 
    } 
    return (ASTree)CallExpr.makeCall(expr, (ASTree)parseArgumentList(tbl));
  }


  
  private String toClassName(ASTree name) throws CompileError {
    StringBuffer sbuf = new StringBuffer();
    toClassName(name, sbuf);
    return sbuf.toString();
  }


  
  private void toClassName(ASTree name, StringBuffer sbuf) throws CompileError {
    if (name instanceof Symbol) {
      sbuf.append(((Symbol)name).get());
      return;
    } 
    if (name instanceof Expr) {
      Expr expr = (Expr)name;
      if (expr.getOperator() == 46) {
        toClassName(expr.oprand1(), sbuf);
        sbuf.append('.');
        toClassName(expr.oprand2(), sbuf);
        
        return;
      } 
    } 
    throw new CompileError("bad static member access", this.lex);
  }











  
  private ASTree parsePrimaryExpr(SymbolTable tbl) throws CompileError {
    String name;
    Declarator decl;
    ASTree expr;
    int t;
    switch (t = this.lex.get()) {
      case 336:
      case 339:
      case 410:
      case 411:
      case 412:
        return (ASTree)new Keyword(t);
      case 400:
        name = this.lex.getString();
        decl = tbl.lookup(name);
        if (decl == null)
          return (ASTree)new Member(name); 
        return (ASTree)new Variable(name, decl);
      case 406:
        return (ASTree)new StringL(this.lex.getString());
      case 328:
        return (ASTree)parseNew(tbl);
      case 40:
        expr = parseExpression(tbl);
        if (this.lex.get() == 41)
          return expr; 
        throw new CompileError(") is missing", this.lex);
    } 
    if (isBuiltinType(t) || t == 344) {
      int dim = parseArrayDimension();
      if (this.lex.get() == 46 && this.lex.get() == 307) {
        return parseDotClass(t, dim);
      }
    } 
    throw new SyntaxError(this.lex);
  }





  
  private NewExpr parseNew(SymbolTable tbl) throws CompileError {
    ArrayInit init = null;
    int t = this.lex.lookAhead();
    if (isBuiltinType(t)) {
      this.lex.get();
      ASTList size = parseArraySize(tbl);
      if (this.lex.lookAhead() == 123) {
        init = parseArrayInitializer(tbl);
      }
      return new NewExpr(t, size, init);
    } 
    if (t == 400) {
      ASTList name = parseClassType(tbl);
      t = this.lex.lookAhead();
      if (t == 40) {
        ASTList args = parseArgumentList(tbl);
        return new NewExpr(name, args);
      } 
      if (t == 91) {
        ASTList size = parseArraySize(tbl);
        if (this.lex.lookAhead() == 123) {
          init = parseArrayInitializer(tbl);
        }
        return NewExpr.makeObjectArray(name, size, init);
      } 
    } 
    
    throw new SyntaxError(this.lex);
  }


  
  private ASTList parseArraySize(SymbolTable tbl) throws CompileError {
    ASTList list = null;
    while (this.lex.lookAhead() == 91) {
      list = ASTList.append(list, parseArrayIndex(tbl));
    }
    return list;
  }


  
  private ASTree parseArrayIndex(SymbolTable tbl) throws CompileError {
    this.lex.get();
    if (this.lex.lookAhead() == 93) {
      this.lex.get();
      return null;
    } 
    ASTree index = parseExpression(tbl);
    if (this.lex.get() != 93) {
      throw new CompileError("] is missing", this.lex);
    }
    return index;
  }


  
  private ASTList parseArgumentList(SymbolTable tbl) throws CompileError {
    if (this.lex.get() != 40) {
      throw new CompileError("( is missing", this.lex);
    }
    ASTList list = null;
    if (this.lex.lookAhead() != 41)
      while (true) {
        list = ASTList.append(list, parseExpression(tbl));
        if (this.lex.lookAhead() == 44) {
          this.lex.get();
          continue;
        } 
        break;
      }  
    if (this.lex.get() != 41) {
      throw new CompileError(") is missing", this.lex);
    }
    return list;
  }
}
