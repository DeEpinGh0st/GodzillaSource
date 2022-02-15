package org.springframework.expression.spel.standard;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateAwareExpressionParser;
import org.springframework.expression.spel.InternalParseException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.expression.spel.ast.BeanReference;
import org.springframework.expression.spel.ast.BooleanLiteral;
import org.springframework.expression.spel.ast.CompoundExpression;
import org.springframework.expression.spel.ast.ConstructorReference;
import org.springframework.expression.spel.ast.Elvis;
import org.springframework.expression.spel.ast.FunctionReference;
import org.springframework.expression.spel.ast.Identifier;
import org.springframework.expression.spel.ast.Indexer;
import org.springframework.expression.spel.ast.InlineList;
import org.springframework.expression.spel.ast.InlineMap;
import org.springframework.expression.spel.ast.Literal;
import org.springframework.expression.spel.ast.MethodReference;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.expression.spel.ast.OpDec;
import org.springframework.expression.spel.ast.OpDivide;
import org.springframework.expression.spel.ast.OpEQ;
import org.springframework.expression.spel.ast.OpGE;
import org.springframework.expression.spel.ast.OpGT;
import org.springframework.expression.spel.ast.OpInc;
import org.springframework.expression.spel.ast.OpLE;
import org.springframework.expression.spel.ast.OpLT;
import org.springframework.expression.spel.ast.OpMinus;
import org.springframework.expression.spel.ast.OpModulus;
import org.springframework.expression.spel.ast.OpMultiply;
import org.springframework.expression.spel.ast.OpNE;
import org.springframework.expression.spel.ast.OpOr;
import org.springframework.expression.spel.ast.OpPlus;
import org.springframework.expression.spel.ast.OperatorBetween;
import org.springframework.expression.spel.ast.OperatorInstanceof;
import org.springframework.expression.spel.ast.OperatorMatches;
import org.springframework.expression.spel.ast.OperatorNot;
import org.springframework.expression.spel.ast.OperatorPower;
import org.springframework.expression.spel.ast.Projection;
import org.springframework.expression.spel.ast.PropertyOrFieldReference;
import org.springframework.expression.spel.ast.QualifiedIdentifier;
import org.springframework.expression.spel.ast.Selection;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import org.springframework.expression.spel.ast.StringLiteral;
import org.springframework.expression.spel.ast.Ternary;
import org.springframework.expression.spel.ast.TypeReference;
import org.springframework.expression.spel.ast.VariableReference;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
























class InternalSpelExpressionParser
  extends TemplateAwareExpressionParser
{
  private static final Pattern VALID_QUALIFIED_ID_PATTERN = Pattern.compile("[\\p{L}\\p{N}_$]+");

  
  private final SpelParserConfiguration configuration;

  
  private final Deque<SpelNodeImpl> constructedNodes = new ArrayDeque<>();

  
  private String expressionString = "";

  
  private List<Token> tokenStream = Collections.emptyList();


  
  private int tokenStreamLength;


  
  private int tokenStreamPointer;



  
  public InternalSpelExpressionParser(SpelParserConfiguration configuration) {
    this.configuration = configuration;
  }




  
  protected SpelExpression doParseExpression(String expressionString, @Nullable ParserContext context) throws ParseException {
    try {
      this.expressionString = expressionString;
      Tokenizer tokenizer = new Tokenizer(expressionString);
      this.tokenStream = tokenizer.process();
      this.tokenStreamLength = this.tokenStream.size();
      this.tokenStreamPointer = 0;
      this.constructedNodes.clear();
      SpelNodeImpl ast = eatExpression();
      Assert.state((ast != null), "No node");
      Token t = peekToken();
      if (t != null) {
        throw new SpelParseException(t.startPos, SpelMessage.MORE_INPUT, new Object[] { toString(nextToken()) });
      }
      Assert.isTrue(this.constructedNodes.isEmpty(), "At least one node expected");
      return new SpelExpression(expressionString, ast, this.configuration);
    }
    catch (InternalParseException ex) {
      throw ex.getCause();
    } 
  }





  
  @Nullable
  private SpelNodeImpl eatExpression() {
    NullLiteral nullLiteral;
    SpelNodeImpl expr = eatLogicalOrExpression();
    Token t = peekToken();
    if (t != null) {
      if (t.kind == TokenKind.ASSIGN) {
        if (expr == null) {
          nullLiteral = new NullLiteral(t.startPos - 1, t.endPos - 1);
        }
        nextToken();
        SpelNodeImpl assignedValue = eatLogicalOrExpression();
        return (SpelNodeImpl)new Assign(t.startPos, t.endPos, new SpelNodeImpl[] { (SpelNodeImpl)nullLiteral, assignedValue });
      } 
      if (t.kind == TokenKind.ELVIS) {
        NullLiteral nullLiteral1; if (nullLiteral == null) {
          nullLiteral = new NullLiteral(t.startPos - 1, t.endPos - 2);
        }
        nextToken();
        SpelNodeImpl valueIfNull = eatExpression();
        if (valueIfNull == null) {
          nullLiteral1 = new NullLiteral(t.startPos + 1, t.endPos + 1);
        }
        return (SpelNodeImpl)new Elvis(t.startPos, t.endPos, new SpelNodeImpl[] { (SpelNodeImpl)nullLiteral, (SpelNodeImpl)nullLiteral1 });
      } 
      if (t.kind == TokenKind.QMARK) {
        if (nullLiteral == null) {
          nullLiteral = new NullLiteral(t.startPos - 1, t.endPos - 1);
        }
        nextToken();
        SpelNodeImpl ifTrueExprValue = eatExpression();
        eatToken(TokenKind.COLON);
        SpelNodeImpl ifFalseExprValue = eatExpression();
        return (SpelNodeImpl)new Ternary(t.startPos, t.endPos, new SpelNodeImpl[] { (SpelNodeImpl)nullLiteral, ifTrueExprValue, ifFalseExprValue });
      } 
    } 
    return (SpelNodeImpl)nullLiteral;
  }
  
  @Nullable
  private SpelNodeImpl eatLogicalOrExpression() {
    OpOr opOr;
    SpelNodeImpl expr = eatLogicalAndExpression();
    while (peekIdentifierToken("or") || peekToken(TokenKind.SYMBOLIC_OR)) {
      Token t = takeToken();
      SpelNodeImpl rhExpr = eatLogicalAndExpression();
      checkOperands(t, expr, rhExpr);
      opOr = new OpOr(t.startPos, t.endPos, new SpelNodeImpl[] { expr, rhExpr });
    } 
    return (SpelNodeImpl)opOr;
  }
  
  @Nullable
  private SpelNodeImpl eatLogicalAndExpression() {
    OpAnd opAnd;
    SpelNodeImpl expr = eatRelationalExpression();
    while (peekIdentifierToken("and") || peekToken(TokenKind.SYMBOLIC_AND)) {
      Token t = takeToken();
      SpelNodeImpl rhExpr = eatRelationalExpression();
      checkOperands(t, expr, rhExpr);
      opAnd = new OpAnd(t.startPos, t.endPos, new SpelNodeImpl[] { expr, rhExpr });
    } 
    return (SpelNodeImpl)opAnd;
  }

  
  @Nullable
  private SpelNodeImpl eatRelationalExpression() {
    SpelNodeImpl expr = eatSumExpression();
    Token relationalOperatorToken = maybeEatRelationalOperator();
    if (relationalOperatorToken != null) {
      Token t = takeToken();
      SpelNodeImpl rhExpr = eatSumExpression();
      checkOperands(t, expr, rhExpr);
      TokenKind tk = relationalOperatorToken.kind;
      
      if (relationalOperatorToken.isNumericRelationalOperator()) {
        if (tk == TokenKind.GT) {
          return (SpelNodeImpl)new OpGT(t.startPos, t.endPos, new SpelNodeImpl[] { expr, rhExpr });
        }
        if (tk == TokenKind.LT) {
          return (SpelNodeImpl)new OpLT(t.startPos, t.endPos, new SpelNodeImpl[] { expr, rhExpr });
        }
        if (tk == TokenKind.LE) {
          return (SpelNodeImpl)new OpLE(t.startPos, t.endPos, new SpelNodeImpl[] { expr, rhExpr });
        }
        if (tk == TokenKind.GE) {
          return (SpelNodeImpl)new OpGE(t.startPos, t.endPos, new SpelNodeImpl[] { expr, rhExpr });
        }
        if (tk == TokenKind.EQ) {
          return (SpelNodeImpl)new OpEQ(t.startPos, t.endPos, new SpelNodeImpl[] { expr, rhExpr });
        }
        Assert.isTrue((tk == TokenKind.NE), "Not-equals token expected");
        return (SpelNodeImpl)new OpNE(t.startPos, t.endPos, new SpelNodeImpl[] { expr, rhExpr });
      } 
      
      if (tk == TokenKind.INSTANCEOF) {
        return (SpelNodeImpl)new OperatorInstanceof(t.startPos, t.endPos, new SpelNodeImpl[] { expr, rhExpr });
      }
      
      if (tk == TokenKind.MATCHES) {
        return (SpelNodeImpl)new OperatorMatches(t.startPos, t.endPos, new SpelNodeImpl[] { expr, rhExpr });
      }
      
      Assert.isTrue((tk == TokenKind.BETWEEN), "Between token expected");
      return (SpelNodeImpl)new OperatorBetween(t.startPos, t.endPos, new SpelNodeImpl[] { expr, rhExpr });
    } 
    return expr;
  }
  
  @Nullable
  private SpelNodeImpl eatSumExpression() {
    OpMinus opMinus;
    SpelNodeImpl expr = eatProductExpression();
    while (peekToken(TokenKind.PLUS, TokenKind.MINUS, TokenKind.INC)) {
      OpPlus opPlus; Token t = takeToken();
      SpelNodeImpl rhExpr = eatProductExpression();
      checkRightOperand(t, rhExpr);
      if (t.kind == TokenKind.PLUS) {
        opPlus = new OpPlus(t.startPos, t.endPos, new SpelNodeImpl[] { expr, rhExpr }); continue;
      } 
      if (t.kind == TokenKind.MINUS) {
        opMinus = new OpMinus(t.startPos, t.endPos, new SpelNodeImpl[] { (SpelNodeImpl)opPlus, rhExpr });
      }
    } 
    return (SpelNodeImpl)opMinus;
  }
  
  @Nullable
  private SpelNodeImpl eatProductExpression() {
    OpModulus opModulus;
    SpelNodeImpl expr = eatPowerIncDecExpression();
    while (peekToken(TokenKind.STAR, TokenKind.DIV, TokenKind.MOD)) {
      OpMultiply opMultiply; OpDivide opDivide; Token t = takeToken();
      SpelNodeImpl rhExpr = eatPowerIncDecExpression();
      checkOperands(t, expr, rhExpr);
      if (t.kind == TokenKind.STAR) {
        opMultiply = new OpMultiply(t.startPos, t.endPos, new SpelNodeImpl[] { expr, rhExpr }); continue;
      } 
      if (t.kind == TokenKind.DIV) {
        opDivide = new OpDivide(t.startPos, t.endPos, new SpelNodeImpl[] { (SpelNodeImpl)opMultiply, rhExpr });
        continue;
      } 
      Assert.isTrue((t.kind == TokenKind.MOD), "Mod token expected");
      opModulus = new OpModulus(t.startPos, t.endPos, new SpelNodeImpl[] { (SpelNodeImpl)opDivide, rhExpr });
    } 
    
    return (SpelNodeImpl)opModulus;
  }

  
  @Nullable
  private SpelNodeImpl eatPowerIncDecExpression() {
    SpelNodeImpl expr = eatUnaryExpression();
    if (peekToken(TokenKind.POWER)) {
      Token t = takeToken();
      SpelNodeImpl rhExpr = eatUnaryExpression();
      checkRightOperand(t, rhExpr);
      return (SpelNodeImpl)new OperatorPower(t.startPos, t.endPos, new SpelNodeImpl[] { expr, rhExpr });
    } 
    if (expr != null && peekToken(TokenKind.INC, TokenKind.DEC)) {
      Token t = takeToken();
      if (t.getKind() == TokenKind.INC) {
        return (SpelNodeImpl)new OpInc(t.startPos, t.endPos, true, new SpelNodeImpl[] { expr });
      }
      return (SpelNodeImpl)new OpDec(t.startPos, t.endPos, true, new SpelNodeImpl[] { expr });
    } 
    return expr;
  }

  
  @Nullable
  private SpelNodeImpl eatUnaryExpression() {
    if (peekToken(TokenKind.PLUS, TokenKind.MINUS, TokenKind.NOT)) {
      Token t = takeToken();
      SpelNodeImpl expr = eatUnaryExpression();
      Assert.state((expr != null), "No node");
      if (t.kind == TokenKind.NOT) {
        return (SpelNodeImpl)new OperatorNot(t.startPos, t.endPos, expr);
      }
      if (t.kind == TokenKind.PLUS) {
        return (SpelNodeImpl)new OpPlus(t.startPos, t.endPos, new SpelNodeImpl[] { expr });
      }
      Assert.isTrue((t.kind == TokenKind.MINUS), "Minus token expected");
      return (SpelNodeImpl)new OpMinus(t.startPos, t.endPos, new SpelNodeImpl[] { expr });
    } 
    if (peekToken(TokenKind.INC, TokenKind.DEC)) {
      Token t = takeToken();
      SpelNodeImpl expr = eatUnaryExpression();
      if (t.getKind() == TokenKind.INC) {
        return (SpelNodeImpl)new OpInc(t.startPos, t.endPos, false, new SpelNodeImpl[] { expr });
      }
      return (SpelNodeImpl)new OpDec(t.startPos, t.endPos, false, new SpelNodeImpl[] { expr });
    } 
    return eatPrimaryExpression();
  }

  
  @Nullable
  private SpelNodeImpl eatPrimaryExpression() {
    SpelNodeImpl start = eatStartNode();
    List<SpelNodeImpl> nodes = null;
    SpelNodeImpl node = eatNode();
    while (node != null) {
      if (nodes == null) {
        nodes = new ArrayList<>(4);
        nodes.add(start);
      } 
      nodes.add(node);
      node = eatNode();
    } 
    if (start == null || nodes == null) {
      return start;
    }
    return (SpelNodeImpl)new CompoundExpression(start.getStartPosition(), ((SpelNodeImpl)nodes.get(nodes.size() - 1)).getEndPosition(), nodes
        .<SpelNodeImpl>toArray(new SpelNodeImpl[0]));
  }

  
  @Nullable
  private SpelNodeImpl eatNode() {
    return peekToken(TokenKind.DOT, TokenKind.SAFE_NAVI) ? eatDottedNode() : eatNonDottedNode();
  }

  
  @Nullable
  private SpelNodeImpl eatNonDottedNode() {
    if (peekToken(TokenKind.LSQUARE) && 
      maybeEatIndexer()) {
      return pop();
    }
    
    return null;
  }









  
  private SpelNodeImpl eatDottedNode() {
    Token t = takeToken();
    boolean nullSafeNavigation = (t.kind == TokenKind.SAFE_NAVI);
    if (maybeEatMethodOrProperty(nullSafeNavigation) || maybeEatFunctionOrVar() || 
      maybeEatProjection(nullSafeNavigation) || maybeEatSelection(nullSafeNavigation)) {
      return pop();
    }
    if (peekToken() == null)
    {
      throw internalException(t.startPos, SpelMessage.OOD, new Object[0]);
    }
    
    throw internalException(t.startPos, SpelMessage.UNEXPECTED_DATA_AFTER_DOT, new Object[] { toString(peekToken()) });
  }







  
  private boolean maybeEatFunctionOrVar() {
    if (!peekToken(TokenKind.HASH)) {
      return false;
    }
    Token t = takeToken();
    Token functionOrVariableName = eatToken(TokenKind.IDENTIFIER);
    SpelNodeImpl[] args = maybeEatMethodArgs();
    if (args == null) {
      push((SpelNodeImpl)new VariableReference(functionOrVariableName.stringValue(), t.startPos, functionOrVariableName.endPos));
      
      return true;
    } 
    
    push((SpelNodeImpl)new FunctionReference(functionOrVariableName.stringValue(), t.startPos, functionOrVariableName.endPos, args));
    
    return true;
  }

  
  @Nullable
  private SpelNodeImpl[] maybeEatMethodArgs() {
    if (!peekToken(TokenKind.LPAREN)) {
      return null;
    }
    List<SpelNodeImpl> args = new ArrayList<>();
    consumeArguments(args);
    eatToken(TokenKind.RPAREN);
    return args.<SpelNodeImpl>toArray(new SpelNodeImpl[0]);
  }
  
  private void eatConstructorArgs(List<SpelNodeImpl> accumulatedArguments) {
    if (!peekToken(TokenKind.LPAREN)) {
      throw new InternalParseException(new SpelParseException(this.expressionString, 
            positionOf(peekToken()), SpelMessage.MISSING_CONSTRUCTOR_ARGS, new Object[0]));
    }
    consumeArguments(accumulatedArguments);
    eatToken(TokenKind.RPAREN);
  }



  
  private void consumeArguments(List<SpelNodeImpl> accumulatedArguments) {
    Token next, t = peekToken();
    Assert.state((t != null), "Expected token");
    int pos = t.startPos;
    
    do {
      nextToken();
      t = peekToken();
      if (t == null) {
        throw internalException(pos, SpelMessage.RUN_OUT_OF_ARGUMENTS, new Object[0]);
      }
      if (t.kind != TokenKind.RPAREN) {
        accumulatedArguments.add(eatExpression());
      }
      next = peekToken();
    }
    while (next != null && next.kind == TokenKind.COMMA);
    
    if (next == null) {
      throw internalException(pos, SpelMessage.RUN_OUT_OF_ARGUMENTS, new Object[0]);
    }
  }
  
  private int positionOf(@Nullable Token t) {
    if (t == null)
    {
      
      return this.expressionString.length();
    }
    return t.startPos;
  }











  
  @Nullable
  private SpelNodeImpl eatStartNode() {
    if (maybeEatLiteral()) {
      return pop();
    }
    if (maybeEatParenExpression()) {
      return pop();
    }
    if (maybeEatTypeReference() || maybeEatNullReference() || maybeEatConstructorReference() || 
      maybeEatMethodOrProperty(false) || maybeEatFunctionOrVar()) {
      return pop();
    }
    if (maybeEatBeanReference()) {
      return pop();
    }
    if (maybeEatProjection(false) || maybeEatSelection(false) || maybeEatIndexer()) {
      return pop();
    }
    if (maybeEatInlineListOrMap()) {
      return pop();
    }
    
    return null;
  }



  
  private boolean maybeEatBeanReference() {
    if (peekToken(TokenKind.BEAN_REF) || peekToken(TokenKind.FACTORY_BEAN_REF)) {
      BeanReference beanReference; Token beanRefToken = takeToken();
      Token beanNameToken = null;
      String beanName = null;
      if (peekToken(TokenKind.IDENTIFIER)) {
        beanNameToken = eatToken(TokenKind.IDENTIFIER);
        beanName = beanNameToken.stringValue();
      }
      else if (peekToken(TokenKind.LITERAL_STRING)) {
        beanNameToken = eatToken(TokenKind.LITERAL_STRING);
        beanName = beanNameToken.stringValue();
        beanName = beanName.substring(1, beanName.length() - 1);
      } else {
        
        throw internalException(beanRefToken.startPos, SpelMessage.INVALID_BEAN_REFERENCE, new Object[0]);
      } 
      
      if (beanRefToken.getKind() == TokenKind.FACTORY_BEAN_REF) {
        String beanNameString = String.valueOf(TokenKind.FACTORY_BEAN_REF.tokenChars) + beanName;
        beanReference = new BeanReference(beanRefToken.startPos, beanNameToken.endPos, beanNameString);
      } else {
        
        beanReference = new BeanReference(beanNameToken.startPos, beanNameToken.endPos, beanName);
      } 
      this.constructedNodes.push(beanReference);
      return true;
    } 
    return false;
  }
  
  private boolean maybeEatTypeReference() {
    if (peekToken(TokenKind.IDENTIFIER)) {
      Token typeName = peekToken();
      Assert.state((typeName != null), "Expected token");
      if (!"T".equals(typeName.stringValue())) {
        return false;
      }
      
      Token t = takeToken();
      if (peekToken(TokenKind.RSQUARE)) {
        
        push((SpelNodeImpl)new PropertyOrFieldReference(false, t.stringValue(), t.startPos, t.endPos));
        return true;
      } 
      eatToken(TokenKind.LPAREN);
      SpelNodeImpl node = eatPossiblyQualifiedId();

      
      int dims = 0;
      while (peekToken(TokenKind.LSQUARE, true)) {
        eatToken(TokenKind.RSQUARE);
        dims++;
      } 
      eatToken(TokenKind.RPAREN);
      this.constructedNodes.push(new TypeReference(typeName.startPos, typeName.endPos, node, dims));
      return true;
    } 
    return false;
  }
  
  private boolean maybeEatNullReference() {
    if (peekToken(TokenKind.IDENTIFIER)) {
      Token nullToken = peekToken();
      Assert.state((nullToken != null), "Expected token");
      if (!"null".equalsIgnoreCase(nullToken.stringValue())) {
        return false;
      }
      nextToken();
      this.constructedNodes.push(new NullLiteral(nullToken.startPos, nullToken.endPos));
      return true;
    } 
    return false;
  }

  
  private boolean maybeEatProjection(boolean nullSafeNavigation) {
    Token t = peekToken();
    if (!peekToken(TokenKind.PROJECT, true)) {
      return false;
    }
    Assert.state((t != null), "No token");
    SpelNodeImpl expr = eatExpression();
    Assert.state((expr != null), "No node");
    eatToken(TokenKind.RSQUARE);
    this.constructedNodes.push(new Projection(nullSafeNavigation, t.startPos, t.endPos, expr));
    return true;
  }

  
  private boolean maybeEatInlineListOrMap() {
    InlineMap inlineMap;
    Token t = peekToken();
    if (!peekToken(TokenKind.LCURLY, true)) {
      return false;
    }
    Assert.state((t != null), "No token");
    SpelNodeImpl expr = null;
    Token closingCurly = peekToken();
    if (peekToken(TokenKind.RCURLY, true))
    
    { Assert.state((closingCurly != null), "No token");
      InlineList inlineList = new InlineList(t.startPos, closingCurly.endPos, new SpelNodeImpl[0]); }
    
    else if (peekToken(TokenKind.COLON, true))
    { closingCurly = eatToken(TokenKind.RCURLY);
      
      inlineMap = new InlineMap(t.startPos, closingCurly.endPos, new SpelNodeImpl[0]); }
    else
    
    { SpelNodeImpl firstExpression = eatExpression();



      
      if (peekToken(TokenKind.RCURLY))
      { List<SpelNodeImpl> elements = new ArrayList<>();
        elements.add(firstExpression);
        closingCurly = eatToken(TokenKind.RCURLY);
        InlineList inlineList = new InlineList(t.startPos, closingCurly.endPos, elements.<SpelNodeImpl>toArray(new SpelNodeImpl[0])); }
      else
      { if (peekToken(TokenKind.COMMA, true))
        { List<SpelNodeImpl> elements = new ArrayList<>();
          elements.add(firstExpression);
          while (true)
          { elements.add(eatExpression());
            
            if (!peekToken(TokenKind.COMMA, true))
            { closingCurly = eatToken(TokenKind.RCURLY);
              InlineList inlineList = new InlineList(t.startPos, closingCurly.endPos, elements.<SpelNodeImpl>toArray(new SpelNodeImpl[0]));

















              
              this.constructedNodes.push(inlineList);
              return true; }  }  }  if (peekToken(TokenKind.COLON, true)) { List<SpelNodeImpl> elements = new ArrayList<>(); elements.add(firstExpression); elements.add(eatExpression()); while (peekToken(TokenKind.COMMA, true)) { elements.add(eatExpression()); eatToken(TokenKind.COLON); elements.add(eatExpression()); }  closingCurly = eatToken(TokenKind.RCURLY); inlineMap = new InlineMap(t.startPos, closingCurly.endPos, elements.<SpelNodeImpl>toArray(new SpelNodeImpl[0])); } else { throw internalException(t.startPos, SpelMessage.OOD, new Object[0]); }  }  }  this.constructedNodes.push(inlineMap); return true;
  }
  
  private boolean maybeEatIndexer() {
    Token t = peekToken();
    if (!peekToken(TokenKind.LSQUARE, true)) {
      return false;
    }
    Assert.state((t != null), "No token");
    SpelNodeImpl expr = eatExpression();
    Assert.state((expr != null), "No node");
    eatToken(TokenKind.RSQUARE);
    this.constructedNodes.push(new Indexer(t.startPos, t.endPos, expr));
    return true;
  }
  
  private boolean maybeEatSelection(boolean nullSafeNavigation) {
    Token t = peekToken();
    if (!peekSelectToken()) {
      return false;
    }
    Assert.state((t != null), "No token");
    nextToken();
    SpelNodeImpl expr = eatExpression();
    if (expr == null) {
      throw internalException(t.startPos, SpelMessage.MISSING_SELECTION_EXPRESSION, new Object[0]);
    }
    eatToken(TokenKind.RSQUARE);
    if (t.kind == TokenKind.SELECT_FIRST) {
      this.constructedNodes.push(new Selection(nullSafeNavigation, 1, t.startPos, t.endPos, expr));
    }
    else if (t.kind == TokenKind.SELECT_LAST) {
      this.constructedNodes.push(new Selection(nullSafeNavigation, 2, t.startPos, t.endPos, expr));
    } else {
      
      this.constructedNodes.push(new Selection(nullSafeNavigation, 0, t.startPos, t.endPos, expr));
    } 
    return true;
  }




  
  private SpelNodeImpl eatPossiblyQualifiedId() {
    Deque<SpelNodeImpl> qualifiedIdPieces = new ArrayDeque<>();
    Token node = peekToken();
    while (isValidQualifiedId(node)) {
      nextToken();
      if (node.kind != TokenKind.DOT) {
        qualifiedIdPieces.add(new Identifier(node.stringValue(), node.startPos, node.endPos));
      }
      node = peekToken();
    } 
    if (qualifiedIdPieces.isEmpty()) {
      if (node == null) {
        throw internalException(this.expressionString.length(), SpelMessage.OOD, new Object[0]);
      }
      throw internalException(node.startPos, SpelMessage.NOT_EXPECTED_TOKEN, new Object[] { "qualified ID", node
            .getKind().toString().toLowerCase() });
    } 
    return (SpelNodeImpl)new QualifiedIdentifier(((SpelNodeImpl)qualifiedIdPieces.getFirst()).getStartPosition(), ((SpelNodeImpl)qualifiedIdPieces
        .getLast()).getEndPosition(), (SpelNodeImpl[])qualifiedIdPieces.toArray((Object[])new SpelNodeImpl[0]));
  }
  
  private boolean isValidQualifiedId(@Nullable Token node) {
    if (node == null || node.kind == TokenKind.LITERAL_STRING) {
      return false;
    }
    if (node.kind == TokenKind.DOT || node.kind == TokenKind.IDENTIFIER) {
      return true;
    }
    String value = node.stringValue();
    return (StringUtils.hasLength(value) && VALID_QUALIFIED_ID_PATTERN.matcher(value).matches());
  }



  
  private boolean maybeEatMethodOrProperty(boolean nullSafeNavigation) {
    if (peekToken(TokenKind.IDENTIFIER)) {
      Token methodOrPropertyName = takeToken();
      SpelNodeImpl[] args = maybeEatMethodArgs();
      if (args == null) {
        
        push((SpelNodeImpl)new PropertyOrFieldReference(nullSafeNavigation, methodOrPropertyName.stringValue(), methodOrPropertyName.startPos, methodOrPropertyName.endPos));
        
        return true;
      } 
      
      push((SpelNodeImpl)new MethodReference(nullSafeNavigation, methodOrPropertyName.stringValue(), methodOrPropertyName.startPos, methodOrPropertyName.endPos, args));

      
      return true;
    } 
    return false;
  }


  
  private boolean maybeEatConstructorReference() {
    if (peekIdentifierToken("new")) {
      Token newToken = takeToken();
      
      if (peekToken(TokenKind.RSQUARE)) {
        
        push((SpelNodeImpl)new PropertyOrFieldReference(false, newToken.stringValue(), newToken.startPos, newToken.endPos));
        return true;
      } 
      SpelNodeImpl possiblyQualifiedConstructorName = eatPossiblyQualifiedId();
      List<SpelNodeImpl> nodes = new ArrayList<>();
      nodes.add(possiblyQualifiedConstructorName);
      if (peekToken(TokenKind.LSQUARE)) {
        
        List<SpelNodeImpl> dimensions = new ArrayList<>();
        while (peekToken(TokenKind.LSQUARE, true)) {
          if (!peekToken(TokenKind.RSQUARE)) {
            dimensions.add(eatExpression());
          } else {
            
            dimensions.add(null);
          } 
          eatToken(TokenKind.RSQUARE);
        } 
        if (maybeEatInlineListOrMap()) {
          nodes.add(pop());
        }
        push((SpelNodeImpl)new ConstructorReference(newToken.startPos, newToken.endPos, dimensions
              .<SpelNodeImpl>toArray(new SpelNodeImpl[0]), nodes.<SpelNodeImpl>toArray(new SpelNodeImpl[0])));
      }
      else {
        
        eatConstructorArgs(nodes);
        
        push((SpelNodeImpl)new ConstructorReference(newToken.startPos, newToken.endPos, nodes.<SpelNodeImpl>toArray(new SpelNodeImpl[0])));
      } 
      return true;
    } 
    return false;
  }
  
  private void push(SpelNodeImpl newNode) {
    this.constructedNodes.push(newNode);
  }
  
  private SpelNodeImpl pop() {
    return this.constructedNodes.pop();
  }








  
  private boolean maybeEatLiteral() {
    Token t = peekToken();
    if (t == null) {
      return false;
    }
    if (t.kind == TokenKind.LITERAL_INT) {
      push((SpelNodeImpl)Literal.getIntLiteral(t.stringValue(), t.startPos, t.endPos, 10));
    }
    else if (t.kind == TokenKind.LITERAL_LONG) {
      push((SpelNodeImpl)Literal.getLongLiteral(t.stringValue(), t.startPos, t.endPos, 10));
    }
    else if (t.kind == TokenKind.LITERAL_HEXINT) {
      push((SpelNodeImpl)Literal.getIntLiteral(t.stringValue(), t.startPos, t.endPos, 16));
    }
    else if (t.kind == TokenKind.LITERAL_HEXLONG) {
      push((SpelNodeImpl)Literal.getLongLiteral(t.stringValue(), t.startPos, t.endPos, 16));
    }
    else if (t.kind == TokenKind.LITERAL_REAL) {
      push((SpelNodeImpl)Literal.getRealLiteral(t.stringValue(), t.startPos, t.endPos, false));
    }
    else if (t.kind == TokenKind.LITERAL_REAL_FLOAT) {
      push((SpelNodeImpl)Literal.getRealLiteral(t.stringValue(), t.startPos, t.endPos, true));
    }
    else if (peekIdentifierToken("true")) {
      push((SpelNodeImpl)new BooleanLiteral(t.stringValue(), t.startPos, t.endPos, true));
    }
    else if (peekIdentifierToken("false")) {
      push((SpelNodeImpl)new BooleanLiteral(t.stringValue(), t.startPos, t.endPos, false));
    }
    else if (t.kind == TokenKind.LITERAL_STRING) {
      push((SpelNodeImpl)new StringLiteral(t.stringValue(), t.startPos, t.endPos, t.stringValue()));
    } else {
      
      return false;
    } 
    nextToken();
    return true;
  }

  
  private boolean maybeEatParenExpression() {
    if (peekToken(TokenKind.LPAREN)) {
      nextToken();
      SpelNodeImpl expr = eatExpression();
      Assert.state((expr != null), "No node");
      eatToken(TokenKind.RPAREN);
      push(expr);
      return true;
    } 
    
    return false;
  }




  
  @Nullable
  private Token maybeEatRelationalOperator() {
    Token t = peekToken();
    if (t == null) {
      return null;
    }
    if (t.isNumericRelationalOperator()) {
      return t;
    }
    if (t.isIdentifier()) {
      String idString = t.stringValue();
      if (idString.equalsIgnoreCase("instanceof")) {
        return t.asInstanceOfToken();
      }
      if (idString.equalsIgnoreCase("matches")) {
        return t.asMatchesToken();
      }
      if (idString.equalsIgnoreCase("between")) {
        return t.asBetweenToken();
      }
    } 
    return null;
  }
  
  private Token eatToken(TokenKind expectedKind) {
    Token t = nextToken();
    if (t == null) {
      int pos = this.expressionString.length();
      throw internalException(pos, SpelMessage.OOD, new Object[0]);
    } 
    if (t.kind != expectedKind) {
      throw internalException(t.startPos, SpelMessage.NOT_EXPECTED_TOKEN, new Object[] { expectedKind
            .toString().toLowerCase(), t.getKind().toString().toLowerCase() });
    }
    return t;
  }
  
  private boolean peekToken(TokenKind desiredTokenKind) {
    return peekToken(desiredTokenKind, false);
  }
  
  private boolean peekToken(TokenKind desiredTokenKind, boolean consumeIfMatched) {
    Token t = peekToken();
    if (t == null) {
      return false;
    }
    if (t.kind == desiredTokenKind) {
      if (consumeIfMatched) {
        this.tokenStreamPointer++;
      }
      return true;
    } 
    
    if (desiredTokenKind == TokenKind.IDENTIFIER)
    {

      
      if (t.kind.ordinal() >= TokenKind.DIV.ordinal() && t.kind.ordinal() <= TokenKind.NOT.ordinal() && t.data != null)
      {
        
        return true;
      }
    }
    return false;
  }
  
  private boolean peekToken(TokenKind possible1, TokenKind possible2) {
    Token t = peekToken();
    if (t == null) {
      return false;
    }
    return (t.kind == possible1 || t.kind == possible2);
  }
  
  private boolean peekToken(TokenKind possible1, TokenKind possible2, TokenKind possible3) {
    Token t = peekToken();
    if (t == null) {
      return false;
    }
    return (t.kind == possible1 || t.kind == possible2 || t.kind == possible3);
  }
  
  private boolean peekIdentifierToken(String identifierString) {
    Token t = peekToken();
    if (t == null) {
      return false;
    }
    return (t.kind == TokenKind.IDENTIFIER && identifierString.equalsIgnoreCase(t.stringValue()));
  }
  
  private boolean peekSelectToken() {
    Token t = peekToken();
    if (t == null) {
      return false;
    }
    return (t.kind == TokenKind.SELECT || t.kind == TokenKind.SELECT_FIRST || t.kind == TokenKind.SELECT_LAST);
  }
  
  private Token takeToken() {
    if (this.tokenStreamPointer >= this.tokenStreamLength) {
      throw new IllegalStateException("No token");
    }
    return this.tokenStream.get(this.tokenStreamPointer++);
  }
  
  @Nullable
  private Token nextToken() {
    if (this.tokenStreamPointer >= this.tokenStreamLength) {
      return null;
    }
    return this.tokenStream.get(this.tokenStreamPointer++);
  }
  
  @Nullable
  private Token peekToken() {
    if (this.tokenStreamPointer >= this.tokenStreamLength) {
      return null;
    }
    return this.tokenStream.get(this.tokenStreamPointer);
  }
  
  public String toString(@Nullable Token t) {
    if (t == null) {
      return "";
    }
    if (t.getKind().hasPayload()) {
      return t.stringValue();
    }
    return t.kind.toString().toLowerCase();
  }
  
  private void checkOperands(Token token, @Nullable SpelNodeImpl left, @Nullable SpelNodeImpl right) {
    checkLeftOperand(token, left);
    checkRightOperand(token, right);
  }
  
  private void checkLeftOperand(Token token, @Nullable SpelNodeImpl operandExpression) {
    if (operandExpression == null) {
      throw internalException(token.startPos, SpelMessage.LEFT_OPERAND_PROBLEM, new Object[0]);
    }
  }
  
  private void checkRightOperand(Token token, @Nullable SpelNodeImpl operandExpression) {
    if (operandExpression == null) {
      throw internalException(token.startPos, SpelMessage.RIGHT_OPERAND_PROBLEM, new Object[0]);
    }
  }
  
  private InternalParseException internalException(int startPos, SpelMessage message, Object... inserts) {
    return new InternalParseException(new SpelParseException(this.expressionString, startPos, message, inserts));
  }
}
