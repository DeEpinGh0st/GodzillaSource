package org.fife.rsta.ac.java.rjc.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.fife.rsta.ac.java.rjc.ast.CodeBlock;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.EnumBody;
import org.fife.rsta.ac.java.rjc.ast.EnumDeclaration;
import org.fife.rsta.ac.java.rjc.ast.Field;
import org.fife.rsta.ac.java.rjc.ast.FormalParameter;
import org.fife.rsta.ac.java.rjc.ast.ImportDeclaration;
import org.fife.rsta.ac.java.rjc.ast.LocalVariable;
import org.fife.rsta.ac.java.rjc.ast.Member;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.java.rjc.ast.NormalClassDeclaration;
import org.fife.rsta.ac.java.rjc.ast.NormalInterfaceDeclaration;
import org.fife.rsta.ac.java.rjc.ast.Package;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclarationContainer;
import org.fife.rsta.ac.java.rjc.lang.Annotation;
import org.fife.rsta.ac.java.rjc.lang.Modifiers;
import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.rsta.ac.java.rjc.lang.TypeArgument;
import org.fife.rsta.ac.java.rjc.lang.TypeParameter;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.lexer.Token;
import org.fife.rsta.ac.java.rjc.lexer.TokenTypes;
import org.fife.rsta.ac.java.rjc.notices.ParserNotice;




























public class ASTFactory
  implements TokenTypes
{
  private static final boolean DEBUG = false;
  private boolean nextMemberDeprecated;
  
  private boolean checkDeprecated() {
    boolean deprecated = this.nextMemberDeprecated;
    this.nextMemberDeprecated = false;
    return deprecated;
  }















  
  private void checkForDuplicateLocalVarNames(CompilationUnit cu, Token lVar, CodeBlock block, Method m) {
    String name = lVar.getLexeme();
    boolean found = false;
    
    int i;
    
    for (i = 0; i < block.getLocalVarCount(); i++) {
      LocalVariable otherLocal = block.getLocalVar(i);
      if (name.equals(otherLocal.getName())) {
        cu.addParserNotice(lVar, "Duplicate local variable: " + name);
        found = true;
        
        break;
      } 
    } 
    
    if (!found)
    {

      
      if (block.getParent() != null) {
        checkForDuplicateLocalVarNames(cu, lVar, block.getParent(), m);


      
      }
      else if (m != null) {
        for (i = 0; i < m.getParameterCount(); i++) {
          FormalParameter param = m.getParameter(i);
          if (name.equals(param.getName())) {
            cu.addParserNotice(lVar, "Duplicate local variable: " + name);
            break;
          } 
        } 
      } 
    }
  }













  
  private Annotation _getAnnotation(CompilationUnit cu, Scanner s) throws IOException {
    s.yylexNonNull(67108864, "Annotation expected");
    Type type = _getType(cu, s);
    
    if ("Deprecated".equals(type.toString())) {
      this.nextMemberDeprecated = true;
    }
    
    if (s.yyPeekCheckType() == 8388609) {
      s.yylex();
      
      s.eatThroughNextSkippingBlocks(8388610);
    } 
    
    Annotation a = new Annotation(type);
    return a;
  }



  
  private CodeBlock _getBlock(CompilationUnit cu, CodeBlock parent, Method m, Scanner s, boolean isStatic) throws IOException {
    return _getBlock(cu, parent, m, s, isStatic, 1);
  }















  
  private CodeBlock _getBlock(CompilationUnit cu, CodeBlock parent, Method m, Scanner s, boolean isStatic, int depth) throws IOException {
    log("Entering _getBlock() (" + depth + ")");


    
    Token t = s.yylexNonNull(8388611, "'{' expected");
    CodeBlock block = new CodeBlock(isStatic, s.createOffset(t.getOffset()));
    block.setParent(parent);
    boolean atStatementStart = true;

    
    while (true) {
      CodeBlock child, tryBlock;
      
      int nextType;
      
      if ((t = s.yylex()) == null) {
        log("Exiting _getBlock() - eos (" + depth + ")");
        block.setDeclarationEndOffset(s.createOffset(s.getOffset()));
        return block;
      } 
      
      int type = t.getType();
      boolean isFinal = false;
      
      switch (type) {
        
        case 8388611:
          s.yyPushback(t);
          child = _getBlock(cu, block, m, s, isStatic, depth + 1);
          block.add(child);
          atStatementStart = true;
          continue;
        
        case 8388612:
          block.setDeclarationEndOffset(s.createOffset(t.getOffset()));
          break;
        
        case 65583:
          t = s.yyPeekNonNull(8388611, 8388609, "'{' or '(' expected");
          if (t.getType() == 8388609)
          {
            s.eatParenPairs();
          }
          s.yyPeekNonNull(8388611, "'{' expected");
          tryBlock = _getBlock(cu, block, m, s, isStatic, depth + 1);
          block.add(tryBlock);
          while (s.yyPeekCheckType() == 65543 && s
            .yyPeekCheckType(2) == 8388609) {
            Type exType; Token var; CodeBlock catchBlock; int offs; s.yylex();
            s.yylex();

            
            boolean multiCatch = false;
            while (true) {
              isFinal = false;
              Token temp = s.yyPeekNonNull(262144, 65554, "Throwable type expected");
              if (temp.isType(65554)) {
                isFinal = true;
                s.yylex();
              } 
              s.yyPeekNonNull(262144, "Variable declarator expected");
              exType = _getType(cu, s);
              var = s.yylexNonNull(262144, 16777237, "Variable declarator expected");
              multiCatch |= var.isType(16777237);
              if (!var.isType(16777237)) {
                s.yylexNonNull(8388610, "')' expected");
                s.yyPeekNonNull(8388611, "'{' expected");
                catchBlock = _getBlock(cu, block, m, s, false, depth);
                offs = var.getOffset();
                if (multiCatch)
                
                { 
                  
                  exType = new Type("java");
                  exType.addIdentifier("lang", null);
                  exType.addIdentifier("Throwable", null); }  break;
              } 
            }  LocalVariable localVar = new LocalVariable(s, isFinal, exType, offs, var.getLexeme());
            checkForDuplicateLocalVarNames(cu, var, block, m);
            catchBlock.addLocalVariable(localVar);
            block.add(catchBlock);
          } 
          continue;


        
        case 65557:
        case 65586:
          nextType = s.yyPeekCheckType();
          while (nextType != -1 && nextType != 8388609) {
            t = s.yylex();
            if (t != null) {
              ParserNotice pn = new ParserNotice(t, "Unexpected token");
              cu.addParserNotice(pn);
            } 
            nextType = s.yyPeekCheckType();
          } 
          if (nextType == 8388609) {
            s.eatParenPairs();
          }
          nextType = s.yyPeekCheckType();
          if (nextType == 8388611) {
            child = _getBlock(cu, block, m, s, isStatic, depth + 1);
            block.add(child);
            atStatementStart = true;
          } 
          continue;











        
        case 65554:
          isFinal = true;
          t = s.yylexNonNull("Unexpected end of file");
          break;
      } 
      
      if (t.isType(8388615)) {
        atStatementStart = true;
        continue;
      } 
      if (atStatementStart && (t.isBasicType() || t.isIdentifier())) {
        Type varType; s.yyPushback(t);

        
        try {
          varType = _getType(cu, s, true);
        } catch (IOException ioe) {
          s.eatUntilNext(8388615, 8388611, 8388612);
          
          atStatementStart = true;
          continue;
        } 
        if (s.yyPeekCheckType() == 262144) {
          while ((t = s.yylexNonNull(262144, "Variable name expected (type==" + varType.toString() + ")")) != null) {
            int arrayDepth = s.skipBracketPairs();
            varType.incrementBracketPairCount(arrayDepth);
            String varDec = varType.toString() + " " + t.getLexeme();
            log(">>> Variable -- " + varDec + " (line " + t.getLine() + ")");
            int offs = t.getOffset();
            String name = t.getLexeme();
            LocalVariable lVar = new LocalVariable(s, isFinal, varType, offs, name);
            checkForDuplicateLocalVarNames(cu, t, block, m);
            block.addLocalVariable(lVar);
            nextType = s.yyPeekCheckType();

            
            if (nextType == 33554433) {
              Token temp = s.eatThroughNextSkippingBlocksAndStuffInParens(8388616, 8388615);
              if (temp != null) {
                s.yyPushback(temp);
              }
              nextType = s.yyPeekCheckType();
            } 


            
            if (nextType != 8388616) {
              s.eatThroughNextSkippingBlocks(8388615);
              break;
            } 
            s.yylex();
          } 
        }
        continue;
      } 
      atStatementStart = false;
    } 





    
    log("Exiting _getBlock() (" + depth + ")");
    return block;
  }




  
  private void _getClassBody(CompilationUnit cu, Scanner s, NormalClassDeclaration classDec) throws IOException {
    log("Entering _getClassBody");
    
    Token t = s.yylexNonNull(8388611, "'{' expected");
    classDec.setBodyStartOffset(s.createOffset(t.getOffset()));
    
    t = s.yylexNonNull("ClassBody expected");
    
    while (t.getType() != 8388612) {
      Token t2; Modifiers modList; CodeBlock block; Modifiers modifiers1;
      switch (t.getType()) {
        case 8388615:
          break;

        
        case 65574:
          t2 = s.yyPeekNonNull("'{' or modifier expected");
          if (t2.isType(8388611)) {
            CodeBlock codeBlock = _getBlock(cu, null, null, s, true);
            classDec.addMember((Member)codeBlock);
            
            break;
          } 
          s.yyPushback(t);
          modList = _getModifierList(cu, s);
          _getMemberDecl(cu, s, classDec, modList);
          break;

        
        case 8388611:
          s.yyPushback(t);
          block = _getBlock(cu, null, null, s, false);
          classDec.addMember((Member)block);
          break;
        
        default:
          s.yyPushback(t);
          modifiers1 = _getModifierList(cu, s);
          _getMemberDecl(cu, s, classDec, modifiers1);
          break;
      } 

      
      try {
        t = s.yylexNonNull("'}' expected (one)");
        classDec.setBodyEndOffset(s.createOffset(t.getOffset()));
      } catch (IOException ioe) {
        classDec.setBodyEndOffset(s.createOffset(s.getOffset()));
        int line = s.getLine();
        int col = s.getColumn();
        ParserNotice pn = new ParserNotice(line, col, 1, "'}' expected (two)");
        cu.addParserNotice(pn);
        
        break;
      } 
    } 
    
    log("Exiting _getClassBody");
  }


  
  private TypeDeclaration _getClassOrInterfaceDeclaration(CompilationUnit cu, Scanner s, TypeDeclarationContainer addTo, Modifiers modList) throws IOException {
    NormalClassDeclaration normalClassDeclaration2;
    EnumDeclaration enumDeclaration;
    NormalInterfaceDeclaration normalInterfaceDeclaration;
    log("Entering _getClassOrInterfaceDeclaration");
    Token t = s.yyPeekNonNull("class, enum, interface or @interface expected");

    
    if (modList == null) {
      modList = _getModifierList(cu, s);
    }
    t = s.yylexNonNull("class, enum, interface or @interface expected");


    
    switch (t.getType())
    
    { case 65545:
        normalClassDeclaration2 = _getNormalClassDeclaration(cu, s, addTo);

























        
        normalClassDeclaration2.setModifiers(modList);
        normalClassDeclaration2.setDeprecated(checkDeprecated());
        
        log("Exiting _getClassOrInterfaceDeclaration");
        return (TypeDeclaration)normalClassDeclaration2;case 65552: enumDeclaration = _getEnumDeclaration(cu, s, addTo); enumDeclaration.setModifiers(modList); enumDeclaration.setDeprecated(checkDeprecated()); log("Exiting _getClassOrInterfaceDeclaration"); return (TypeDeclaration)enumDeclaration;case 65564: normalInterfaceDeclaration = _getNormalInterfaceDeclaration(cu, s, addTo); normalInterfaceDeclaration.setModifiers(modList); normalInterfaceDeclaration.setDeprecated(checkDeprecated()); log("Exiting _getClassOrInterfaceDeclaration"); return (TypeDeclaration)normalInterfaceDeclaration;case 67108864: throw new IOException("AnnotationTypeDeclaration not implemented"); }  ParserNotice notice = new ParserNotice(t, "class, interface or enum expected"); cu.addParserNotice(notice); NormalClassDeclaration normalClassDeclaration1 = _getNormalClassDeclaration(cu, s, addTo); normalClassDeclaration1.setModifiers(modList); normalClassDeclaration1.setDeprecated(checkDeprecated()); log("Exiting _getClassOrInterfaceDeclaration"); return (TypeDeclaration)normalClassDeclaration1;
  }










  
  public CompilationUnit getCompilationUnit(String name, Scanner scanner) {
    CompilationUnit cu = new CompilationUnit(name);


    
    try {
      List<Annotation> initialAnnotations = null;
      while (scanner.yyPeekCheckType() == 67108864) {
        if (initialAnnotations == null) {
          initialAnnotations = new ArrayList<>(1);
        }
        initialAnnotations.add(_getAnnotation(cu, scanner));
      } 

      
      Token t = scanner.yylex();
      if (t == null) {
        return cu;
      }
      if (t.isType(65568)) {
        t = scanner.yyPeekNonNull("Identifier expected");
        int offs = t.getOffset();
        String qualifiedID = getQualifiedIdentifier(scanner);
        Package pkg = new Package(scanner, offs, qualifiedID);
        if (initialAnnotations != null)
        {
          initialAnnotations = null;
        }
        cu.setPackage(pkg);
        scanner.yylexNonNull(8388615, "Semicolon expected");
        t = scanner.yylex();
      } 


      
      while (t != null && t.isType(65561)) {
        
        boolean isStatic = false;
        StringBuilder buf = new StringBuilder();
        t = scanner.yylexNonNull("Incomplete import statement");
        Token temp = null;
        int offs = 0;
        
        if (t.isType(65574)) {
          isStatic = true;
          t = scanner.yylexNonNull("Incomplete import statement");
        } 
        
        if (!t.isIdentifier()) {
          cu.addParserNotice(t, "Expected identifier, found: \"" + t
              .getLexeme() + "\"");
          scanner.eatThroughNextSkippingBlocks(8388615);
          
          t = scanner.getMostRecentToken();
        } else {
          
          offs = t.getOffset();
          buf.append(t.getLexeme());
          temp = scanner.yylexNonNull(8388617, 8388615, "'.' or ';' expected");
          
          while (temp.isType(8388617)) {
            temp = scanner.yylexNonNull(262144, 16777234, "Identifier or '*' expected");
            
            if (temp.isIdentifier()) {
              buf.append('.').append(temp.getLexeme());
            } else {
              
              buf.append(".*");
              temp = scanner.yylex();
              break;
            } 
            temp = scanner.yylexNonNull(65561, 8388617, 8388615, "'.' or ';' expected");
            
            if (temp.isType(65561)) {
              cu.addParserNotice(temp, "';' expected");
              t = temp;
            } 
          } 
          
          t = temp;
        } 
        
        if (temp == null || !t.isType(8388615)) {
          throw new IOException("Semicolon expected, found " + t);
        }

        
        ImportDeclaration id = new ImportDeclaration(scanner, offs, buf.toString(), isStatic);
        cu.addImportDeclaration(id);
        t = scanner.yylex();
      } 


      
      if (t == null) {
        return cu;
      }
      
      scanner.yyPushback(t);
      
      while (_getTypeDeclaration(cu, scanner) != null) {
        if (initialAnnotations != null)
        {
          initialAnnotations = null;
        
        }
      }
    
    }
    catch (IOException ioe) {
      ParserNotice notice; if (isDebug() && !(ioe instanceof java.io.EOFException)) {
        ioe.printStackTrace();
      }
      
      Token lastTokenLexed = scanner.getMostRecentToken();
      if (lastTokenLexed == null) {
        notice = new ParserNotice(0, 0, 5, ioe.getMessage());
      } else {
        
        notice = new ParserNotice(lastTokenLexed, ioe.getMessage());
      } 
      cu.addParserNotice(notice);
    } 

    
    return cu;
  }




  
  private EnumBody _getEnumBody(CompilationUnit cu, Scanner s, EnumDeclaration enumDec) throws IOException {
    CodeBlock block = _getBlock(cu, null, null, s, false);
    enumDec.setBodyEndOffset(s.createOffset(block.getNameEndOffset()));
    return null;
  }



  
  private EnumDeclaration _getEnumDeclaration(CompilationUnit cu, Scanner s, TypeDeclarationContainer addTo) throws IOException {
    Token t = s.yylexNonNull(262144, "Identifier expected");
    String enumName = t.getLexeme();
    EnumDeclaration enumDec = new EnumDeclaration(s, t.getOffset(), enumName);
    enumDec.setPackage(cu.getPackage());
    addTo.addTypeDeclaration((TypeDeclaration)enumDec);
    
    t = s.yylexNonNull("implements or '{' expected");
    
    if (t.isType(65560)) {
      List<Type> implemented = new ArrayList<>(1);
      do {
        implemented.add(_getType(cu, s));
        t = s.yylex();
      } while (t != null && t.isType(8388616));
      
      if (t != null) {
        s.yyPushback(t);
      }
    }
    else if (t.isType(8388611)) {
      s.yyPushback(t);
    } 
    
    _getEnumBody(cu, s, enumDec);


    
    return enumDec;
  }




  
  private List<FormalParameter> _getFormalParameters(CompilationUnit cu, List<Token> tokenList) throws IOException {
    List<FormalParameter> list = new ArrayList<>(0);
    
    Scanner s = new Scanner(tokenList);
    Token t = s.yylex();
    if (t == null) {
      return list;
    }

    
    while (true) {
      boolean isFinal = false;
      if (t.isType(65554)) {
        isFinal = true;
        t = s.yylexNonNull("Type expected");
      } 
      
      List<Annotation> annotations = null;
      while (t.getType() == 67108864) {
        s.yyPushback(t);
        if (annotations == null) {
          annotations = new ArrayList<>(1);
        }
        annotations.add(_getAnnotation(cu, s));
        t = s.yylexNonNull("Type expected");
      } 
      
      s.yyPushback(t);
      Type type = _getType(cu, s);
      Token temp = s.yylexNonNull("Argument name expected");
      boolean elipsis = false;
      if (temp.isType(134217728)) {
        elipsis = true;
        temp = s.yylexNonNull(262144, "Argument name expected");
      } 
      type.incrementBracketPairCount(s.skipBracketPairs());
      int offs = temp.getOffset();
      String name = temp.getLexeme();
      FormalParameter param = new FormalParameter(s, isFinal, type, offs, name, annotations);
      
      list.add(param);
      if (elipsis) {
        break;
      }
      t = s.yylex();
      if (t == null) {
        break;
      }
      if (t.getType() != 8388616) {
        throw new IOException("Comma expected");
      }
      t = s.yylexNonNull("Parameter or ')' expected");
    } 
    
    return list;
  }




  
  private void _getInterfaceBody(CompilationUnit cu, Scanner s, NormalInterfaceDeclaration iDec) throws IOException {
    log("Entering _getInterfaceBody");
    
    Token t = s.yylexNonNull(8388611, "'{' expected");
    iDec.setBodyStartOffset(s.createOffset(t.getOffset()));
    
    t = s.yylexNonNull("InterfaceBody expected");
    
    while (t.getType() != 8388612) {
      Modifiers modList;
      switch (t.getType()) {
        case 8388615:
          break;

        
        case 8388611:
          s.yyPushback(t);
          
          _getBlock(cu, null, null, s, false);
          break;
        
        default:
          s.yyPushback(t);
          modList = _getModifierList(cu, s);
          _getInterfaceMemberDecl(cu, s, iDec, modList);
          break;
      } 

      
      try {
        t = s.yylexNonNull("'}' expected (one)");
        iDec.setBodyEndOffset(s.createOffset(t.getOffset()));
      } catch (IOException ioe) {
        iDec.setBodyEndOffset(s.createOffset(s.getOffset()));
        int line = s.getLine();
        int col = s.getColumn();
        ParserNotice pn = new ParserNotice(line, col, 1, "'}' expected (two)");
        cu.addParserNotice(pn);
      } 
    } 

    
    log("Exiting _getInterfaceBody");
  }













  
  private void _getInterfaceMemberDecl(CompilationUnit cu, Scanner s, NormalInterfaceDeclaration iDec, Modifiers modList) throws IOException {
    log("Entering _getInterfaceMemberDecl");
    
    List<Token> tokenList = new ArrayList<>(1);
    List<Token> methodNameAndTypeTokenList = null;
    List<Token> methodParamsList = null;
    
    boolean methodDecl = false;
    boolean blockDecl = false;
    boolean varDecl = false;



    
    while (true) {
      Token t = s.yylexNonNull("Unexpected end of input");
      
      switch (t.getType()) {
        case 8388609:
          methodNameAndTypeTokenList = tokenList;
          methodParamsList = new ArrayList<>(1);
          methodDecl = true;
          break;
        case 8388611:
          blockDecl = true;
          break;
        case 33554433:
          varDecl = true;
          
          s.eatThroughNextSkippingBlocks(8388615);
          break;
        case 8388615:
          varDecl = true;
          break;
      } 
      tokenList.add(t);
    } 



    
    if (varDecl) {
      log("*** Variable declaration:");
      Scanner tempScanner = new Scanner(tokenList);
      Type type = _getType(cu, tempScanner);
      Token fieldNameToken = tempScanner.yylexNonNull(262144, "Identifier (field name) expected");
      int bracketPairCount = tempScanner.skipBracketPairs();
      type.incrementBracketPairCount(bracketPairCount);
      Field field = new Field(s, modList, type, fieldNameToken);
      field.setDeprecated(checkDeprecated());
      field.setDocComment(s.getLastDocComment());
      log(field.toString());
      iDec.addMember((Member)field);
    } else {
      Token token;
      if (methodDecl) {
        log("*** Method declaration:");
        Scanner tempScanner = new Scanner(methodNameAndTypeTokenList);
        Type type = null;
        if (methodNameAndTypeTokenList.size() > 1) {
          if (tempScanner.yyPeekCheckType() == 16777219) {
            _getTypeParameters(cu, tempScanner);
            type = _getType(cu, tempScanner);
          } else {
            
            type = _getType(cu, tempScanner);
          } 
        }
        Token methodNameToken = tempScanner.yylexNonNull(262144, "Identifier (method name) expected");
        while (true) {
          Token token1 = s.yylexNonNull("Unexpected end of input");
          if (token1.isType(8388610)) {
            break;
          }
          methodParamsList.add(token1);
        } 
        List<FormalParameter> formalParams = _getFormalParameters(cu, methodParamsList);
        if (s.yyPeekCheckType() == 8388613) {
          if (type == null) {
            throw new IOException("Constructors cannot return array types");
          }
          type.incrementBracketPairCount(s.skipBracketPairs());
        } 
        List<String> thrownTypeNames = getThrownTypeNames(cu, s);
        token = s.yylexNonNull("'{' or ';' expected");
        if (token.getType() != 8388615) {
          throw new IOException("';' expected");
        }
        Method m = new Method(s, modList, type, methodNameToken, formalParams, thrownTypeNames);
        
        m.setDeprecated(checkDeprecated());
        m.setDocComment(s.getLastDocComment());
        iDec.addMember((Member)m);
      }
      else if (blockDecl) {




        
        if (tokenList.size() < 2) {
          for (int i = tokenList.size() - 1; i >= 0; i--) {
            s.yyPushback(tokenList.get(i));
          }
          CodeBlock block = _getBlock(cu, null, null, s, false);
          iDec.addMember((Member)block);
        } else {
          
          s.yyPushback(token);
          for (int i = tokenList.size() - 1; i >= 0; i--) {
            s.yyPushback(tokenList.get(i));
          }
          _getClassOrInterfaceDeclaration(cu, s, (TypeDeclarationContainer)iDec, modList);
        } 
      } 
    } 
    log("Exiting _getInterfaceMemberDecl");
  }














  
  private void _getMemberDecl(CompilationUnit cu, Scanner s, NormalClassDeclaration classDec, Modifiers modList) throws IOException {
    log("Entering _getMemberDecl");
    
    List<Token> tokenList = new ArrayList<>(1);
    List<Token> methodNameAndTypeTokenList = null;
    List<Token> methodParamsList = null;
    
    boolean methodDecl = false;
    boolean blockDecl = false;
    boolean varDecl = false;



    
    while (true) {
      Token t = s.yylexNonNull("Unexpected end of input");
      
      switch (t.getType()) {
        case 8388609:
          methodNameAndTypeTokenList = tokenList;
          methodParamsList = new ArrayList<>(1);
          methodDecl = true;
          break;
        case 8388611:
          blockDecl = true;
          break;
        case 33554433:
          varDecl = true;
          
          s.eatThroughNextSkippingBlocks(8388615);
          break;
        case 8388615:
          varDecl = true;
          break;
      } 
      tokenList.add(t);
    } 



    
    if (varDecl) {
      log("*** Variable declaration:");
      Scanner tempScanner = new Scanner(tokenList);
      Type type = _getType(cu, tempScanner);
      Token fieldNameToken = tempScanner.yylexNonNull(262144, "Identifier (field name) expected");
      int bracketPairCount = tempScanner.skipBracketPairs();
      type.incrementBracketPairCount(bracketPairCount);
      Field field = new Field(s, modList, type, fieldNameToken);
      field.setDeprecated(checkDeprecated());
      field.setDocComment(s.getLastDocComment());
      log(field.toString());
      classDec.addMember((Member)field);
    } else {
      Token token; if (methodDecl) {
        log("*** Method declaration:");
        CodeBlock block = null;
        Scanner tempScanner = new Scanner(methodNameAndTypeTokenList);
        Type type = null;
        if (methodNameAndTypeTokenList.size() > 1) {
          if (tempScanner.yyPeekCheckType() == 16777219) {
            _getTypeParameters(cu, tempScanner);
            if (tempScanner.yyPeekCheckType(2) != -1)
            {

              
              type = _getType(cu, tempScanner);
            }
          } else {
            
            type = _getType(cu, tempScanner);
          } 
        }
        Token methodNameToken = tempScanner.yylexNonNull(262144, "Identifier (method name) expected");
        
        label82: while (true) {
          Token token1 = s.yylexNonNull("Unexpected end of input");
          if (token1.isType(67108864)) {

            
            methodParamsList.add(token1);
            
            token1 = s.yylexNonNull("Unexpected end of input");
            methodParamsList.add(token1);
            token1 = s.yylexNonNull("Unexpected end of input");
            if (token1.isType(8388609)) {
              
              methodParamsList.add(token1);
              
              while (true) {
                token1 = s.yylexNonNull("Unexpected end of input");
                methodParamsList.add(token1);
                if (token1.isType(8388610)) {
                  continue label82;
                }
              } 
            } 

            
            if (token1.isType(67108864)) {
              s.yyPushback(token1); continue;
            } 
            if (!token1.isType(8388610)) {
              
              methodParamsList.add(token1);
              
              continue;
            } 
            
            break;
          } 
          
          if (token1.isType(8388610)) {
            break;
          }
          
          methodParamsList.add(token1);
        } 
        
        List<FormalParameter> formalParams = _getFormalParameters(cu, methodParamsList);
        if (s.yyPeekCheckType() == 8388613) {
          if (type == null) {
            throw new IOException("Constructors cannot return array types");
          }
          type.incrementBracketPairCount(s.skipBracketPairs());
        } 
        List<String> thrownTypeNames = getThrownTypeNames(cu, s);
        Method m = new Method(s, modList, type, methodNameToken, formalParams, thrownTypeNames);
        
        m.setDeprecated(checkDeprecated());
        m.setDocComment(s.getLastDocComment());
        classDec.addMember((Member)m);
        token = s.yylexNonNull("'{' or ';' expected");
        if (!token.isType(8388615))
        {
          
          if (token.isType(8388611)) {
            s.yyPushback(token);
            block = _getBlock(cu, null, m, s, false);
          } else {
            
            throw new IOException("'{' or ';' expected");
          }  } 
        m.setBody(block);
      }
      else if (blockDecl) {

        
        this.nextMemberDeprecated = false;
        if (tokenList.size() < 2) {
          for (int i = tokenList.size() - 1; i >= 0; i--) {
            s.yyPushback(tokenList.get(i));
          }
          CodeBlock block = _getBlock(cu, null, null, s, false);
          classDec.addMember((Member)block);
        } else {
          
          s.yyPushback(token);
          for (int i = tokenList.size() - 1; i >= 0; i--) {
            s.yyPushback(tokenList.get(i));
          }
          _getClassOrInterfaceDeclaration(cu, s, (TypeDeclarationContainer)classDec, modList);
        } 
      } 
    } 
    log("Exiting _getMemberDecl (next== " + s.yyPeek() + ")");
  }




  
  private Modifiers _getModifierList(CompilationUnit cu, Scanner s) throws IOException {
    Modifiers modList = null;
    Token t = s.yylexNonNull("Unexpected end of input");

    
    while (true) {
      int modifier = isModifier(t);
      if (modifier != -1) {
        if (modList == null) {
          modList = new Modifiers();
        }
        if (!modList.addModifier(modifier)) {
          cu.addParserNotice(t, "Duplicate modifier");
        }
      }
      else if (t.isType(67108864)) {
        Token next = s.yyPeekNonNull("Annotation expected");
        s.yyPushback(t);
        
        if (next.isType(65564)) {
          return modList;
        }
        if (modList == null) {
          modList = new Modifiers();
        }
        modList.addAnnotation(_getAnnotation(cu, s));
      } else {
        
        s.yyPushback(t);
        return modList;
      } 
      
      t = s.yylexNonNull("Unexpected end of input");
    } 
  }





  
  private NormalClassDeclaration _getNormalClassDeclaration(CompilationUnit cu, Scanner s, TypeDeclarationContainer addTo) throws IOException {
    String className;
    log("Entering _getNormalClassDeclaration");

    
    Token t = s.yylexNonNull("Identifier expected");
    if (t.isType(262144)) {
      className = t.getLexeme();
    } else {
      
      className = "Unknown";
      cu.addParserNotice(new ParserNotice(t, "Class name expected"));
      s.eatUntilNext(65553, 65560, 8388611);
    } 

    
    NormalClassDeclaration classDec = new NormalClassDeclaration(s, t.getOffset(), className);
    classDec.setPackage(cu.getPackage());
    addTo.addTypeDeclaration((TypeDeclaration)classDec);
    
    t = s.yylexNonNull("TypeParameters, extends, implements or '{' expected");
    if (t.isType(16777219)) {
      s.yyPushback(t);
      List<TypeParameter> typeParams = _getTypeParameters(cu, s);
      classDec.setTypeParameters(typeParams);
      t = s.yylexNonNull("extends, implements or '{' expected");
    } 
    
    if (t.isType(65553)) {
      classDec.setExtendedType(_getType(cu, s));
      t = s.yylexNonNull("implements or '{' expected");
    } 
    
    if (t.isType(65560)) {
      do {
        classDec.addImplemented(_getType(cu, s));
        t = s.yylex();
      } while (t != null && t.isType(8388616));
      if (t != null) {
        s.yyPushback(t);
      }
    }
    else if (t.isType(8388611)) {
      s.yyPushback(t);
    } 
    
    _getClassBody(cu, s, classDec);
    
    log("Exiting _getNormalClassDeclaration");
    return classDec;
  }






  
  private NormalInterfaceDeclaration _getNormalInterfaceDeclaration(CompilationUnit cu, Scanner s, TypeDeclarationContainer addTo) throws IOException {
    String iName;
    Token t = s.yylexNonNull("Identifier expected");
    if (t.isType(262144)) {
      iName = t.getLexeme();
    } else {
      
      iName = "Unknown";
      cu.addParserNotice(new ParserNotice(t, "Interface name expected"));
      s.eatUntilNext(65553, 8388611);
    } 

    
    NormalInterfaceDeclaration iDec = new NormalInterfaceDeclaration(s, t.getOffset(), iName);
    iDec.setPackage(cu.getPackage());
    addTo.addTypeDeclaration((TypeDeclaration)iDec);
    
    t = s.yylexNonNull("TypeParameters, extends or '{' expected");
    if (t.isType(16777219)) {
      s.yyPushback(t);
      _getTypeParameters(cu, s);
      t = s.yylexNonNull("Interface body expected");
    } 
    
    if (t.isType(65553)) {
      do {
        iDec.addExtended(_getType(cu, s));
        t = s.yylex();
      } while (t != null && t.isType(8388616));
      if (t != null) {
        s.yyPushback(t);
      }
    }
    else if (t.isType(8388611)) {
      s.yyPushback(t);
    } 
    
    _getInterfaceBody(cu, s, iDec);
    
    return iDec;
  }





  
  private String getQualifiedIdentifier(Scanner scanner) throws IOException {
    StringBuilder sb = new StringBuilder();
    Token t;
    while ((t = scanner.yylex()).isIdentifier()) {
      sb.append(t.getLexeme());
      t = scanner.yylex();
      if (t.isType(8388617)) {
        sb.append('.');
      }
    } 




    
    scanner.yyPushback(t);
    
    return sb.toString();
  }




  
  private List<String> getThrownTypeNames(CompilationUnit cu, Scanner s) throws IOException {
    if (s.yyPeekCheckType() != 65581) {
      return null;
    }
    s.yylex();
    
    List<String> list = new ArrayList<>(1);
    
    list.add(getQualifiedIdentifier(s));
    while (s.yyPeekCheckType() == 8388616) {
      s.yylex();
      list.add(getQualifiedIdentifier(s));
    } 
    
    return list;
  }




  
  private Type _getType(CompilationUnit cu, Scanner s) throws IOException {
    return _getType(cu, s, false);
  }



  
  private Type _getType(CompilationUnit cu, Scanner s, boolean pushbackOnUnexpected) throws IOException {
    log("Entering _getType()");
    Type type = new Type();
    
    Token t = s.yylexNonNull("Type expected");

    
    if (t.isType(65584)) {
      type.addIdentifier(t.getLexeme(), null);
      log("Exiting _getType(): " + type.toString());
      return type;
    } 
    if (t.isBasicType()) {
      int arrayDepth = s.skipBracketPairs();
      type.addIdentifier(t.getLexeme(), null);
      type.setBracketPairCount(arrayDepth);
      log("Exiting _getType(): " + type.toString());
      return type;
    } 
    
    while (true) {
      List<TypeArgument> typeArgs;
      switch (t.getType()) {
        case 262144:
          typeArgs = null;
          if (s.yyPeekCheckType() == 16777219) {
            typeArgs = _getTypeArguments(cu, s);
          }
          type.addIdentifier(t.getLexeme(), typeArgs);
          t = s.yylexNonNull("Unexpected end of input");
          if (t.isType(8388617)) {
            t = s.yylexNonNull("Unexpected end of input");
            continue;
          } 
          if (t.isType(8388613)) {
            s.yyPushback(t);
            type.setBracketPairCount(s.skipBracketPairs());
          }
          else {
            
            s.yyPushback(t);
          } 








          
          log("Exiting _getType(): " + type.toString());
          return type;
      } 
      break;
    } 
    if (pushbackOnUnexpected)
      s.yyPushback(t); 
    throw new IOException("Expected identifier, found: " + t); } private TypeArgument _getTypeArgument(CompilationUnit cu, Scanner s) throws IOException {
    TypeArgument typeArg;
    log("Entering _getTypeArgument()");


    
    Token t = s.yyPeekNonNull("Type or '?' expected");
    
    if (t.isType(16777222))
    { s.yylex();
      t = s.yyPeek();
      if (t.getType() != 16777218)
      { t = s.yylexNonNull(8388616, 65553, 65576, "',', super or extends expected");

        
        switch (t.getType())
        { case 8388616:
            typeArg = new TypeArgument(null, 0, null);
            s.yyPushback(t);



















            
            log("Exiting _getTypeArgument() : " + typeArg);
            return typeArg;case 65553: otherType = _getType(cu, s); typeArg = new TypeArgument(null, 1, otherType); log("Exiting _getTypeArgument() : " + typeArg); return typeArg; }  Type otherType = _getType(cu, s); typeArg = new TypeArgument(null, 2, otherType); } else { typeArg = new TypeArgument(null, 0, null); }  } else { Type type = _getType(cu, s); typeArg = new TypeArgument(type); }  log("Exiting _getTypeArgument() : " + typeArg); return typeArg;
  }



  
  private List<TypeArgument> _getTypeArguments(CompilationUnit cu, Scanner s) throws IOException {
    Token t;
    s.increaseTypeArgumentsLevel();
    log("Entering _getTypeArguments() (" + s.getTypeArgumentsLevel() + ")");
    
    s.markResetPosition();
    s.yylexNonNull(16777219, "'<' expected");
    
    List<TypeArgument> typeArgs = new ArrayList<>(1);

    
    do {
      typeArgs.add(_getTypeArgument(cu, s));
      t = s.yylexNonNull("',' or '>' expected");
      if (t.getType() != 8388616 && t.getType() != 16777218) {

        
        s.resetToLastMarkedPosition();
        log("Exiting _getTypeArguments() (" + s.getTypeArgumentsLevel() + ") - NOT TYPE ARGUMENTS (" + t.getLexeme() + ")");
        s.decreaseTypeArgumentsLevel();
        return null;
      } 
    } while (t.isType(8388616));
    
    log("Exiting _getTypeArguments() (" + s.getTypeArgumentsLevel() + ")");
    s.decreaseTypeArgumentsLevel();
    
    s.clearResetPosition();
    return typeArgs;
  }










  
  private TypeDeclaration _getTypeDeclaration(CompilationUnit cu, Scanner s) throws IOException {
    Token t = s.yylex();
    if (t == null) {
      return null;
    }

    
    while (t.isType(8388615)) {
      t = s.yylex();
      if (t == null) {
        return null;
      }
    } 
    
    s.yyPushback(t);
    
    String docComment = s.getLastDocComment();
    TypeDeclaration td = _getClassOrInterfaceDeclaration(cu, s, (TypeDeclarationContainer)cu, null);
    td.setDocComment(docComment);
    return td;
  }




  
  private TypeParameter _getTypeParameter(CompilationUnit cu, Scanner s) throws IOException {
    log("Entering _getTypeParameter()");
    
    Token identifier = s.yylexNonNull(262144, "Identifier expected");
    TypeParameter typeParam = new TypeParameter(identifier);
    
    if (s.yyPeekCheckType() == 65553) {
      do {
        s.yylex();
        typeParam.addBound(_getType(cu, s));
      } while (s.yyPeekCheckType() == 16777236);
    }
    
    log("Exiting _getTypeParameter(): " + typeParam.getName());
    return typeParam;
  }




  
  private List<TypeParameter> _getTypeParameters(CompilationUnit cu, Scanner s) throws IOException {
    s.increaseTypeArgumentsLevel();
    log("Entering _getTypeParameters() (" + s.getTypeArgumentsLevel() + ")");
    
    s.markResetPosition();
    Token t = s.yylexNonNull(16777219, "TypeParameters expected");
    
    List<TypeParameter> typeParams = new ArrayList<>(1);
    
    do {
      TypeParameter typeParam = _getTypeParameter(cu, s);
      typeParams.add(typeParam);
      t = s.yylexNonNull(8388616, 16777218, "',' or '>' expected");
    } while (t.isType(8388616));
    
    log("Exiting _getTypeParameters() (" + s.getTypeArgumentsLevel() + ")");
    s.decreaseTypeArgumentsLevel();
    
    return typeParams;
  }


  
  private static boolean isDebug() {
    return false;
  }

  
  private int isModifier(Token t) {
    switch (t.getType()) {
      case 65537:
      case 65554:
      case 65566:
      case 65569:
      case 65570:
      case 65571:
      case 65574:
      case 65575:
      case 65578:
      case 65582:
      case 65585:
        return t.getType();
    } 
    return -1;
  }
  
  private static void log(String msg) {}
}
