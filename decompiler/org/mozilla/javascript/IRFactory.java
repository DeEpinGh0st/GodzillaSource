package org.mozilla.javascript;
import java.util.List;
import org.mozilla.javascript.ast.ArrayComprehension;
import org.mozilla.javascript.ast.ArrayComprehensionLoop;
import org.mozilla.javascript.ast.ArrayLiteral;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.Block;
import org.mozilla.javascript.ast.ElementGet;
import org.mozilla.javascript.ast.ForInLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.GeneratorExpression;
import org.mozilla.javascript.ast.GeneratorExpressionLoop;
import org.mozilla.javascript.ast.Jump;
import org.mozilla.javascript.ast.Label;
import org.mozilla.javascript.ast.LetNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.UnaryExpression;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.mozilla.javascript.ast.XmlRef;

public final class IRFactory extends Parser {
  private static final int LOOP_DO_WHILE = 0;
  private Decompiler decompiler = new Decompiler(); private static final int LOOP_WHILE = 1; private static final int LOOP_FOR = 2;
  private static final int ALWAYS_TRUE_BOOLEAN = 1;
  private static final int ALWAYS_FALSE_BOOLEAN = -1;
  
  public IRFactory() {}
  
  public IRFactory(CompilerEnvirons env) {
    this(env, env.getErrorReporter());
  }
  
  public IRFactory(CompilerEnvirons env, ErrorReporter errorReporter) {
    super(env, errorReporter);
  }




  
  public ScriptNode transformTree(AstRoot root) {
    this.currentScriptOrFn = (ScriptNode)root;
    this.inUseStrictDirective = root.isInStrictMode();
    int sourceStartOffset = this.decompiler.getCurrentOffset();




    
    ScriptNode script = (ScriptNode)transform((AstNode)root);
    
    int sourceEndOffset = this.decompiler.getCurrentOffset();
    script.setEncodedSourceBounds(sourceStartOffset, sourceEndOffset);

    
    if (this.compilerEnv.isGeneratingSource()) {
      script.setEncodedSource(this.decompiler.getEncodedSource());
    }
    
    this.decompiler = null;
    return script;
  }




  
  public Node transform(AstNode node) {
    switch (node.getType()) {
      case 157:
        return transformArrayComp((ArrayComprehension)node);
      case 65:
        return transformArrayLiteral((ArrayLiteral)node);
      case 129:
        return transformBlock(node);
      case 120:
        return transformBreak((BreakStatement)node);
      case 38:
        return transformFunctionCall((FunctionCall)node);
      case 121:
        return transformContinue((ContinueStatement)node);
      case 118:
        return transformDoLoop((DoLoop)node);
      case 128:
        return (Node)node;
      case 119:
        if (node instanceof ForInLoop) {
          return transformForInLoop((ForInLoop)node);
        }
        return transformForLoop((ForLoop)node);
      
      case 109:
        return transformFunction((FunctionNode)node);
      case 162:
        return transformGenExpr((GeneratorExpression)node);
      case 36:
        return transformElementGet((ElementGet)node);
      case 33:
        return transformPropertyGet((PropertyGet)node);
      case 102:
        return transformCondExpr((ConditionalExpression)node);
      case 112:
        return transformIf((IfStatement)node);
      
      case 42:
      case 43:
      case 44:
      case 45:
      case 160:
        return transformLiteral(node);
      
      case 39:
        return transformName((Name)node);
      case 40:
        return transformNumber((NumberLiteral)node);
      case 30:
        return transformNewExpr((NewExpression)node);
      case 66:
        return transformObjectLiteral((ObjectLiteral)node);
      case 48:
        return transformRegExp((RegExpLiteral)node);
      case 4:
        return transformReturn((ReturnStatement)node);
      case 136:
        return transformScript((ScriptNode)node);
      case 41:
        return transformString((StringLiteral)node);
      case 114:
        return transformSwitch((SwitchStatement)node);
      case 50:
        return transformThrow((ThrowStatement)node);
      case 81:
        return transformTry((TryStatement)node);
      case 117:
        return transformWhileLoop((WhileLoop)node);
      case 123:
        return transformWith((WithStatement)node);
      case 72:
        return transformYield((Yield)node);
    } 
    if (node instanceof ExpressionStatement) {
      return transformExprStmt((ExpressionStatement)node);
    }
    if (node instanceof Assignment) {
      return transformAssignment((Assignment)node);
    }
    if (node instanceof UnaryExpression) {
      return transformUnary((UnaryExpression)node);
    }
    if (node instanceof XmlMemberGet) {
      return transformXmlMemberGet((XmlMemberGet)node);
    }
    if (node instanceof InfixExpression) {
      return transformInfix((InfixExpression)node);
    }
    if (node instanceof VariableDeclaration) {
      return transformVariables((VariableDeclaration)node);
    }
    if (node instanceof ParenthesizedExpression) {
      return transformParenExpr((ParenthesizedExpression)node);
    }
    if (node instanceof LabeledStatement) {
      return transformLabeledStatement((LabeledStatement)node);
    }
    if (node instanceof LetNode) {
      return transformLetNode((LetNode)node);
    }
    if (node instanceof XmlRef) {
      return transformXmlRef((XmlRef)node);
    }
    if (node instanceof XmlLiteral) {
      return transformXmlLiteral((XmlLiteral)node);
    }
    throw new IllegalArgumentException("Can't transform: " + node);
  }





















  
  private Node transformArrayComp(ArrayComprehension node) {
    int lineno = node.getLineno();
    Scope scopeNode = createScopeNode(157, lineno);
    String arrayName = this.currentScriptOrFn.getNextTempName();
    pushScope(scopeNode);
    try {
      defineSymbol(153, arrayName, false);
      Node block = new Node(129, lineno);
      Node newArray = createCallOrNew(30, createName("Array"));
      Node init = new Node(133, createAssignment(90, createName(arrayName), newArray), lineno);



      
      block.addChildToBack(init);
      block.addChildToBack(arrayCompTransformHelper(node, arrayName));
      scopeNode.addChildToBack(block);
      scopeNode.addChildToBack(createName(arrayName));
      return (Node)scopeNode;
    } finally {
      popScope();
    } 
  }

  
  private Node arrayCompTransformHelper(ArrayComprehension node, String arrayName) {
    this.decompiler.addToken(83);
    int lineno = node.getLineno();
    Node expr = transform(node.getResult());
    
    List<ArrayComprehensionLoop> loops = node.getLoops();
    int numLoops = loops.size();

    
    Node[] iterators = new Node[numLoops];
    Node[] iteratedObjs = new Node[numLoops];
    
    for (int i = 0; i < numLoops; i++) {
      ArrayComprehensionLoop acl = loops.get(i);
      this.decompiler.addName(" ");
      this.decompiler.addToken(119);
      if (acl.isForEach()) {
        this.decompiler.addName("each ");
      }
      this.decompiler.addToken(87);
      
      AstNode iter = acl.getIterator();
      String name = null;
      if (iter.getType() == 39) {
        name = iter.getString();
        this.decompiler.addName(name);
      } else {
        
        decompile(iter);
        name = this.currentScriptOrFn.getNextTempName();
        defineSymbol(87, name, false);
        expr = createBinary(89, createAssignment(90, (Node)iter, createName(name)), expr);
      } 



      
      Node init = createName(name);

      
      defineSymbol(153, name, false);
      iterators[i] = init;
      
      this.decompiler.addToken(52);
      iteratedObjs[i] = transform(acl.getIteratedObject());
      this.decompiler.addToken(88);
    } 

    
    Node call = createCallOrNew(38, createPropertyGet(createName(arrayName), (String)null, "push", 0));



    
    Node body = new Node(133, call, lineno);
    
    if (node.getFilter() != null) {
      this.decompiler.addName(" ");
      this.decompiler.addToken(112);
      this.decompiler.addToken(87);
      body = createIf(transform(node.getFilter()), body, (Node)null, lineno);
      this.decompiler.addToken(88);
    } 

    
    int pushed = 0; try {
      int j;
      for (j = numLoops - 1; j >= 0; j--) {
        ArrayComprehensionLoop acl = loops.get(j);
        Scope loop = createLoopNode((Node)null, acl.getLineno());
        
        pushScope(loop);
        pushed++;
        body = createForIn(153, (Node)loop, iterators[j], iteratedObjs[j], body, acl.isForEach());
      
      }

    
    }
    finally {
      
      for (int j = 0; j < pushed; j++) {
        popScope();
      }
    } 
    
    this.decompiler.addToken(84);


    
    call.addChildToBack(expr);
    return body;
  }
  
  private Node transformArrayLiteral(ArrayLiteral node) {
    if (node.isDestructuring()) {
      return (Node)node;
    }
    this.decompiler.addToken(83);
    List<AstNode> elems = node.getElements();
    Node array = new Node(65);
    List<Integer> skipIndexes = null;
    for (int i = 0; i < elems.size(); i++) {
      AstNode elem = elems.get(i);
      if (elem.getType() != 128) {
        array.addChildToBack(transform(elem));
      } else {
        if (skipIndexes == null) {
          skipIndexes = new ArrayList<Integer>();
        }
        skipIndexes.add(Integer.valueOf(i));
      } 
      if (i < elems.size() - 1)
        this.decompiler.addToken(89); 
    } 
    this.decompiler.addToken(84);
    array.putIntProp(21, node.getDestructuringLength());
    
    if (skipIndexes != null) {
      int[] skips = new int[skipIndexes.size()];
      for (int j = 0; j < skipIndexes.size(); j++)
        skips[j] = ((Integer)skipIndexes.get(j)).intValue(); 
      array.putProp(11, skips);
    } 
    return array;
  }
  
  private Node transformAssignment(Assignment node) {
    AstNode left = removeParens(node.getLeft());
    Node target = null;
    if (isDestructuring((Node)left)) {
      decompile(left);
      AstNode astNode = left;
    } else {
      target = transform(left);
    } 
    this.decompiler.addToken(node.getType());
    return createAssignment(node.getType(), target, transform(node.getRight()));
  }


  
  private Node transformBlock(AstNode node) {
    if (node instanceof Scope) {
      pushScope((Scope)node);
    }
    try {
      List<Node> kids = new ArrayList<Node>();
      for (Node kid : node) {
        kids.add(transform((AstNode)kid));
      }
      node.removeChildren();
      for (Node kid : kids) {
        node.addChildToBack(kid);
      }
      return (Node)node;
    } finally {
      if (node instanceof Scope) {
        popScope();
      }
    } 
  }
  
  private Node transformBreak(BreakStatement node) {
    this.decompiler.addToken(120);
    if (node.getBreakLabel() != null) {
      this.decompiler.addName(node.getBreakLabel().getIdentifier());
    }
    this.decompiler.addEOL(82);
    return (Node)node;
  }
  
  private Node transformCondExpr(ConditionalExpression node) {
    Node test = transform(node.getTestExpression());
    this.decompiler.addToken(102);
    Node ifTrue = transform(node.getTrueExpression());
    this.decompiler.addToken(103);
    Node ifFalse = transform(node.getFalseExpression());
    return createCondExpr(test, ifTrue, ifFalse);
  }
  
  private Node transformContinue(ContinueStatement node) {
    this.decompiler.addToken(121);
    if (node.getLabel() != null) {
      this.decompiler.addName(node.getLabel().getIdentifier());
    }
    this.decompiler.addEOL(82);
    return (Node)node;
  }
  
  private Node transformDoLoop(DoLoop loop) {
    loop.setType(132);
    pushScope((Scope)loop);
    try {
      this.decompiler.addToken(118);
      this.decompiler.addEOL(85);
      Node body = transform(loop.getBody());
      this.decompiler.addToken(86);
      this.decompiler.addToken(117);
      this.decompiler.addToken(87);
      Node cond = transform(loop.getCondition());
      this.decompiler.addToken(88);
      this.decompiler.addEOL(82);
      return createLoop((Jump)loop, 0, body, cond, (Node)null, (Node)null);
    } finally {
      
      popScope();
    } 
  }


  
  private Node transformElementGet(ElementGet node) {
    Node target = transform(node.getTarget());
    this.decompiler.addToken(83);
    Node element = transform(node.getElement());
    this.decompiler.addToken(84);
    return new Node(36, target, element);
  }
  
  private Node transformExprStmt(ExpressionStatement node) {
    Node expr = transform(node.getExpression());
    this.decompiler.addEOL(82);
    return new Node(node.getType(), expr, node.getLineno());
  }
  
  private Node transformForInLoop(ForInLoop loop) {
    this.decompiler.addToken(119);
    if (loop.isForEach())
      this.decompiler.addName("each "); 
    this.decompiler.addToken(87);
    
    loop.setType(132);
    pushScope((Scope)loop);
    try {
      int declType = -1;
      AstNode iter = loop.getIterator();
      if (iter instanceof VariableDeclaration) {
        declType = ((VariableDeclaration)iter).getType();
      }
      Node lhs = transform(iter);
      this.decompiler.addToken(52);
      Node obj = transform(loop.getIteratedObject());
      this.decompiler.addToken(88);
      this.decompiler.addEOL(85);
      Node body = transform(loop.getBody());
      this.decompiler.addEOL(86);
      return createForIn(declType, (Node)loop, lhs, obj, body, loop.isForEach());
    } finally {
      
      popScope();
    } 
  }
  
  private Node transformForLoop(ForLoop loop) {
    this.decompiler.addToken(119);
    this.decompiler.addToken(87);
    loop.setType(132);

    
    Scope savedScope = this.currentScope;
    this.currentScope = (Scope)loop;
    try {
      Node init = transform(loop.getInitializer());
      this.decompiler.addToken(82);
      Node test = transform(loop.getCondition());
      this.decompiler.addToken(82);
      Node incr = transform(loop.getIncrement());
      this.decompiler.addToken(88);
      this.decompiler.addEOL(85);
      Node body = transform(loop.getBody());
      this.decompiler.addEOL(86);
      return createFor((Scope)loop, init, test, incr, body);
    } finally {
      this.currentScope = savedScope;
    } 
  }
  
  private Node transformFunction(FunctionNode fn) {
    int functionType = fn.getFunctionType();
    int start = this.decompiler.markFunctionStart(functionType);
    Node mexpr = decompileFunctionHeader(fn);
    int index = this.currentScriptOrFn.addFunction(fn);
    
    Parser.PerFunctionVariables savedVars = new Parser.PerFunctionVariables(this, fn);

    
    try {
      Node destructuring = (Node)fn.getProp(23);
      fn.removeProp(23);
      
      int lineno = fn.getBody().getLineno();
      this.nestingOfFunction++;
      Node body = transform(fn.getBody());
      
      if (!fn.isExpressionClosure()) {
        this.decompiler.addToken(86);
      }
      fn.setEncodedSourceBounds(start, this.decompiler.markFunctionEnd(start));
      
      if (functionType != 2 && !fn.isExpressionClosure())
      {
        
        this.decompiler.addToken(1);
      }
      
      if (destructuring != null) {
        body.addChildToFront(new Node(133, destructuring, lineno));
      }

      
      int syntheticType = fn.getFunctionType();
      Node pn = initFunction(fn, index, body, syntheticType);
      if (mexpr != null) {
        pn = createAssignment(90, mexpr, pn);
        if (syntheticType != 2) {
          pn = createExprStatementNoReturn(pn, fn.getLineno());
        }
      } 
      return pn;
    } finally {
      
      this.nestingOfFunction--;
      savedVars.restore();
    } 
  }
  
  private Node transformFunctionCall(FunctionCall node) {
    Node call = createCallOrNew(38, transform(node.getTarget()));
    call.setLineno(node.getLineno());
    this.decompiler.addToken(87);
    List<AstNode> args = node.getArguments();
    for (int i = 0; i < args.size(); i++) {
      AstNode arg = args.get(i);
      call.addChildToBack(transform(arg));
      if (i < args.size() - 1) {
        this.decompiler.addToken(89);
      }
    } 
    this.decompiler.addToken(88);
    return call;
  }

  
  private Node transformGenExpr(GeneratorExpression node) {
    Node pn;
    FunctionNode fn = new FunctionNode();
    fn.setSourceName(this.currentScriptOrFn.getNextTempName());
    fn.setIsGenerator();
    fn.setFunctionType(2);
    fn.setRequiresActivation();
    
    int functionType = fn.getFunctionType();
    int start = this.decompiler.markFunctionStart(functionType);
    Node mexpr = decompileFunctionHeader(fn);
    int index = this.currentScriptOrFn.addFunction(fn);
    
    Parser.PerFunctionVariables savedVars = new Parser.PerFunctionVariables(this, fn);

    
    try {
      Node destructuring = (Node)fn.getProp(23);
      fn.removeProp(23);
      
      int lineno = node.lineno;
      this.nestingOfFunction++;
      Node body = genExprTransformHelper(node);
      
      if (!fn.isExpressionClosure()) {
        this.decompiler.addToken(86);
      }
      fn.setEncodedSourceBounds(start, this.decompiler.markFunctionEnd(start));
      
      if (functionType != 2 && !fn.isExpressionClosure())
      {
        
        this.decompiler.addToken(1);
      }
      
      if (destructuring != null) {
        body.addChildToFront(new Node(133, destructuring, lineno));
      }

      
      int syntheticType = fn.getFunctionType();
      pn = initFunction(fn, index, body, syntheticType);
      if (mexpr != null) {
        pn = createAssignment(90, mexpr, pn);
        if (syntheticType != 2) {
          pn = createExprStatementNoReturn(pn, fn.getLineno());
        }
      } 
    } finally {
      this.nestingOfFunction--;
      savedVars.restore();
    } 
    
    Node call = createCallOrNew(38, pn);
    call.setLineno(node.getLineno());
    this.decompiler.addToken(87);
    this.decompiler.addToken(88);
    return call;
  }
  
  private Node genExprTransformHelper(GeneratorExpression node) {
    this.decompiler.addToken(87);
    int lineno = node.getLineno();
    Node expr = transform(node.getResult());
    
    List<GeneratorExpressionLoop> loops = node.getLoops();
    int numLoops = loops.size();

    
    Node[] iterators = new Node[numLoops];
    Node[] iteratedObjs = new Node[numLoops];
    
    for (int i = 0; i < numLoops; i++) {
      GeneratorExpressionLoop acl = loops.get(i);
      this.decompiler.addName(" ");
      this.decompiler.addToken(119);
      this.decompiler.addToken(87);
      
      AstNode iter = acl.getIterator();
      String name = null;
      if (iter.getType() == 39) {
        name = iter.getString();
        this.decompiler.addName(name);
      } else {
        
        decompile(iter);
        name = this.currentScriptOrFn.getNextTempName();
        defineSymbol(87, name, false);
        expr = createBinary(89, createAssignment(90, (Node)iter, createName(name)), expr);
      } 



      
      Node init = createName(name);

      
      defineSymbol(153, name, false);
      iterators[i] = init;
      
      this.decompiler.addToken(52);
      iteratedObjs[i] = transform(acl.getIteratedObject());
      this.decompiler.addToken(88);
    } 

    
    Node yield = new Node(72, expr, node.getLineno());
    
    Node body = new Node(133, yield, lineno);
    
    if (node.getFilter() != null) {
      this.decompiler.addName(" ");
      this.decompiler.addToken(112);
      this.decompiler.addToken(87);
      body = createIf(transform(node.getFilter()), body, (Node)null, lineno);
      this.decompiler.addToken(88);
    } 

    
    int pushed = 0; try {
      int j;
      for (j = numLoops - 1; j >= 0; j--) {
        GeneratorExpressionLoop acl = loops.get(j);
        Scope loop = createLoopNode((Node)null, acl.getLineno());
        
        pushScope(loop);
        pushed++;
        body = createForIn(153, (Node)loop, iterators[j], iteratedObjs[j], body, acl.isForEach());
      
      }

    
    }
    finally {
      
      for (int j = 0; j < pushed; j++) {
        popScope();
      }
    } 
    
    this.decompiler.addToken(88);
    
    return body;
  }
  
  private Node transformIf(IfStatement n) {
    this.decompiler.addToken(112);
    this.decompiler.addToken(87);
    Node cond = transform(n.getCondition());
    this.decompiler.addToken(88);
    this.decompiler.addEOL(85);
    Node ifTrue = transform(n.getThenPart());
    Node ifFalse = null;
    if (n.getElsePart() != null) {
      this.decompiler.addToken(86);
      this.decompiler.addToken(113);
      this.decompiler.addEOL(85);
      ifFalse = transform(n.getElsePart());
    } 
    this.decompiler.addEOL(86);
    return createIf(cond, ifTrue, ifFalse, n.getLineno());
  }
  
  private Node transformInfix(InfixExpression node) {
    Node left = transform(node.getLeft());
    this.decompiler.addToken(node.getType());
    Node right = transform(node.getRight());
    if (node instanceof org.mozilla.javascript.ast.XmlDotQuery) {
      this.decompiler.addToken(88);
    }
    return createBinary(node.getType(), left, right);
  }
  
  private Node transformLabeledStatement(LabeledStatement ls) {
    Label label = ls.getFirstLabel();
    List<Label> labels = ls.getLabels();
    this.decompiler.addName(label.getName());
    if (labels.size() > 1)
    {
      for (Label lb : labels.subList(1, labels.size())) {
        this.decompiler.addEOL(103);
        this.decompiler.addName(lb.getName());
      } 
    }
    if (ls.getStatement().getType() == 129) {
      
      this.decompiler.addToken(66);
      this.decompiler.addEOL(85);
    } else {
      this.decompiler.addEOL(103);
    } 
    Node statement = transform(ls.getStatement());
    if (ls.getStatement().getType() == 129) {
      this.decompiler.addEOL(86);
    }


    
    Node breakTarget = Node.newTarget();
    Node block = new Node(129, (Node)label, statement, breakTarget);
    label.target = breakTarget;
    
    return block;
  }
  
  private Node transformLetNode(LetNode node) {
    pushScope((Scope)node);
    try {
      this.decompiler.addToken(153);
      this.decompiler.addToken(87);
      Node vars = transformVariableInitializers(node.getVariables());
      this.decompiler.addToken(88);
      node.addChildToBack(vars);
      boolean letExpr = (node.getType() == 158);
      if (node.getBody() != null) {
        if (letExpr) {
          this.decompiler.addName(" ");
        } else {
          this.decompiler.addEOL(85);
        } 
        node.addChildToBack(transform(node.getBody()));
        if (!letExpr) {
          this.decompiler.addEOL(86);
        }
      } 
      return (Node)node;
    } finally {
      popScope();
    } 
  }
  
  private Node transformLiteral(AstNode node) {
    this.decompiler.addToken(node.getType());
    return (Node)node;
  }
  
  private Node transformName(Name node) {
    this.decompiler.addName(node.getIdentifier());
    return (Node)node;
  }
  
  private Node transformNewExpr(NewExpression node) {
    this.decompiler.addToken(30);
    Node nx = createCallOrNew(30, transform(node.getTarget()));
    nx.setLineno(node.getLineno());
    List<AstNode> args = node.getArguments();
    this.decompiler.addToken(87);
    for (int i = 0; i < args.size(); i++) {
      AstNode arg = args.get(i);
      nx.addChildToBack(transform(arg));
      if (i < args.size() - 1) {
        this.decompiler.addToken(89);
      }
    } 
    this.decompiler.addToken(88);
    if (node.getInitializer() != null) {
      nx.addChildToBack(transformObjectLiteral(node.getInitializer()));
    }
    return nx;
  }
  
  private Node transformNumber(NumberLiteral node) {
    this.decompiler.addNumber(node.getNumber());
    return (Node)node;
  }
  private Node transformObjectLiteral(ObjectLiteral node) {
    Object[] properties;
    if (node.isDestructuring()) {
      return (Node)node;
    }


    
    this.decompiler.addToken(85);
    List<ObjectProperty> elems = node.getElements();
    Node object = new Node(66);
    
    if (elems.isEmpty()) {
      properties = ScriptRuntime.emptyArgs;
    } else {
      int size = elems.size(), i = 0;
      properties = new Object[size];
      for (ObjectProperty prop : elems) {
        if (prop.isGetter()) {
          this.decompiler.addToken(151);
        } else if (prop.isSetter()) {
          this.decompiler.addToken(152);
        } 
        
        properties[i++] = getPropKey((Node)prop.getLeft());


        
        if (!prop.isGetter() && !prop.isSetter()) {
          this.decompiler.addToken(66);
        }
        
        Node right = transform(prop.getRight());
        if (prop.isGetter()) {
          right = createUnary(151, right);
        } else if (prop.isSetter()) {
          right = createUnary(152, right);
        } 
        object.addChildToBack(right);
        
        if (i < size) {
          this.decompiler.addToken(89);
        }
      } 
    } 
    this.decompiler.addToken(86);
    object.putProp(12, properties);
    return object;
  }
  
  private Object getPropKey(Node id) {
    Object key;
    if (id instanceof Name) {
      String s = ((Name)id).getIdentifier();
      this.decompiler.addName(s);
      key = ScriptRuntime.getIndexObject(s);
    } else if (id instanceof StringLiteral) {
      String s = ((StringLiteral)id).getValue();
      this.decompiler.addString(s);
      key = ScriptRuntime.getIndexObject(s);
    } else if (id instanceof NumberLiteral) {
      double n = ((NumberLiteral)id).getNumber();
      this.decompiler.addNumber(n);
      key = ScriptRuntime.getIndexObject(n);
    } else {
      throw Kit.codeBug();
    } 
    return key;
  }
  
  private Node transformParenExpr(ParenthesizedExpression node) {
    AstNode expr = node.getExpression();
    this.decompiler.addToken(87);
    int count = 1;
    while (expr instanceof ParenthesizedExpression) {
      this.decompiler.addToken(87);
      count++;
      expr = ((ParenthesizedExpression)expr).getExpression();
    } 
    Node result = transform(expr);
    for (int i = 0; i < count; i++) {
      this.decompiler.addToken(88);
    }
    result.putProp(19, Boolean.TRUE);
    return result;
  }
  
  private Node transformPropertyGet(PropertyGet node) {
    Node target = transform(node.getTarget());
    String name = node.getProperty().getIdentifier();
    this.decompiler.addToken(108);
    this.decompiler.addName(name);
    return createPropertyGet(target, (String)null, name, 0);
  }
  
  private Node transformRegExp(RegExpLiteral node) {
    this.decompiler.addRegexp(node.getValue(), node.getFlags());
    this.currentScriptOrFn.addRegExp(node);
    return (Node)node;
  }
  
  private Node transformReturn(ReturnStatement node) {
    boolean expClosure = Boolean.TRUE.equals(node.getProp(25));
    if (expClosure) {
      this.decompiler.addName(" ");
    } else {
      this.decompiler.addToken(4);
    } 
    AstNode rv = node.getReturnValue();
    Node value = (rv == null) ? null : transform(rv);
    if (!expClosure) this.decompiler.addEOL(82); 
    return (rv == null) ? new Node(4, node.getLineno()) : new Node(4, value, node.getLineno());
  }


  
  private Node transformScript(ScriptNode node) {
    this.decompiler.addToken(136);
    if (this.currentScope != null) Kit.codeBug(); 
    this.currentScope = (Scope)node;
    Node body = new Node(129);
    for (Node kid : node) {
      body.addChildToBack(transform((AstNode)kid));
    }
    node.removeChildren();
    Node children = body.getFirstChild();
    if (children != null) {
      node.addChildrenToBack(children);
    }
    return (Node)node;
  }
  
  private Node transformString(StringLiteral node) {
    this.decompiler.addString(node.getValue());
    return Node.newString(node.getValue());
  }







































  
  private Node transformSwitch(SwitchStatement node) {
    this.decompiler.addToken(114);
    this.decompiler.addToken(87);
    Node switchExpr = transform(node.getExpression());
    this.decompiler.addToken(88);
    node.addChildToBack(switchExpr);
    
    Node block = new Node(129, (Node)node, node.getLineno());
    this.decompiler.addEOL(85);
    
    for (SwitchCase sc : node.getCases()) {
      AstNode expr = sc.getExpression();
      Node caseExpr = null;
      
      if (expr != null) {
        this.decompiler.addToken(115);
        caseExpr = transform(expr);
      } else {
        this.decompiler.addToken(116);
      } 
      this.decompiler.addEOL(103);
      
      List<AstNode> stmts = sc.getStatements();
      Block block1 = new Block();
      if (stmts != null) {
        for (AstNode kid : stmts) {
          block1.addChildToBack(transform(kid));
        }
      }
      addSwitchCase(block, caseExpr, (Node)block1);
    } 
    this.decompiler.addEOL(86);
    closeSwitch(block);
    return block;
  }
  
  private Node transformThrow(ThrowStatement node) {
    this.decompiler.addToken(50);
    Node value = transform(node.getExpression());
    this.decompiler.addEOL(82);
    return new Node(50, value, node.getLineno());
  }
  
  private Node transformTry(TryStatement node) {
    this.decompiler.addToken(81);
    this.decompiler.addEOL(85);
    Node tryBlock = transform(node.getTryBlock());
    this.decompiler.addEOL(86);
    
    Block block = new Block();
    for (CatchClause cc : node.getCatchClauses()) {
      EmptyExpression emptyExpression; this.decompiler.addToken(124);
      this.decompiler.addToken(87);
      
      String varName = cc.getVarName().getIdentifier();
      this.decompiler.addName(varName);
      
      Node catchCond = null;
      AstNode ccc = cc.getCatchCondition();
      if (ccc != null) {
        this.decompiler.addName(" ");
        this.decompiler.addToken(112);
        catchCond = transform(ccc);
      } else {
        emptyExpression = new EmptyExpression();
      } 
      this.decompiler.addToken(88);
      this.decompiler.addEOL(85);
      
      Node body = transform((AstNode)cc.getBody());
      this.decompiler.addEOL(86);
      
      block.addChildToBack(createCatch(varName, (Node)emptyExpression, body, cc.getLineno()));
    } 
    
    Node finallyBlock = null;
    if (node.getFinallyBlock() != null) {
      this.decompiler.addToken(125);
      this.decompiler.addEOL(85);
      finallyBlock = transform(node.getFinallyBlock());
      this.decompiler.addEOL(86);
    } 
    return createTryCatchFinally(tryBlock, (Node)block, finallyBlock, node.getLineno());
  }

  
  private Node transformUnary(UnaryExpression node) {
    int type = node.getType();
    if (type == 74) {
      return transformDefaultXmlNamepace(node);
    }
    if (node.isPrefix()) {
      this.decompiler.addToken(type);
    }
    Node child = transform(node.getOperand());
    if (node.isPostfix()) {
      this.decompiler.addToken(type);
    }
    if (type == 106 || type == 107) {
      return createIncDec(type, node.isPostfix(), child);
    }
    return createUnary(type, child);
  }
  
  private Node transformVariables(VariableDeclaration node) {
    this.decompiler.addToken(node.getType());
    transformVariableInitializers(node);


    
    AstNode parent = node.getParent();
    if (!(parent instanceof org.mozilla.javascript.ast.Loop) && !(parent instanceof LetNode))
    {
      this.decompiler.addEOL(82);
    }
    return (Node)node;
  }
  
  private Node transformVariableInitializers(VariableDeclaration node) {
    List<VariableInitializer> vars = node.getVariables();
    int size = vars.size(), i = 0;
    for (VariableInitializer var : vars) {
      AstNode target = var.getTarget();
      AstNode init = var.getInitializer();
      
      Node left = null;
      if (var.isDestructuring()) {
        decompile(target);
        AstNode astNode = target;
      } else {
        left = transform(target);
      } 
      
      Node right = null;
      if (init != null) {
        this.decompiler.addToken(90);
        right = transform(init);
      } 
      
      if (var.isDestructuring()) {
        if (right == null) {
          node.addChildToBack(left);
        } else {
          Node d = createDestructuringAssignment(node.getType(), left, right);
          
          node.addChildToBack(d);
        } 
      } else {
        if (right != null) {
          left.addChildToBack(right);
        }
        node.addChildToBack(left);
      } 
      if (i++ < size - 1) {
        this.decompiler.addToken(89);
      }
    } 
    return (Node)node;
  }
  
  private Node transformWhileLoop(WhileLoop loop) {
    this.decompiler.addToken(117);
    loop.setType(132);
    pushScope((Scope)loop);
    try {
      this.decompiler.addToken(87);
      Node cond = transform(loop.getCondition());
      this.decompiler.addToken(88);
      this.decompiler.addEOL(85);
      Node body = transform(loop.getBody());
      this.decompiler.addEOL(86);
      return createLoop((Jump)loop, 1, body, cond, (Node)null, (Node)null);
    } finally {
      popScope();
    } 
  }
  
  private Node transformWith(WithStatement node) {
    this.decompiler.addToken(123);
    this.decompiler.addToken(87);
    Node expr = transform(node.getExpression());
    this.decompiler.addToken(88);
    this.decompiler.addEOL(85);
    Node stmt = transform(node.getStatement());
    this.decompiler.addEOL(86);
    return createWith(expr, stmt, node.getLineno());
  }
  
  private Node transformYield(Yield node) {
    this.decompiler.addToken(72);
    Node kid = (node.getValue() == null) ? null : transform(node.getValue());
    if (kid != null) {
      return new Node(72, kid, node.getLineno());
    }
    return new Node(72, node.getLineno());
  }



  
  private Node transformXmlLiteral(XmlLiteral node) {
    Node pnXML = new Node(30, node.getLineno());
    List<XmlFragment> frags = node.getFragments();
    
    XmlString first = (XmlString)frags.get(0);
    boolean anon = first.getXml().trim().startsWith("<>");
    pnXML.addChildToBack(createName(anon ? "XMLList" : "XML"));
    
    Node pn = null;
    for (XmlFragment frag : frags) {
      Node expr; if (frag instanceof XmlString) {
        String xml = ((XmlString)frag).getXml();
        this.decompiler.addName(xml);
        if (pn == null) {
          pn = createString(xml); continue;
        } 
        pn = createBinary(21, pn, createString(xml));
        continue;
      } 
      XmlExpression xexpr = (XmlExpression)frag;
      boolean isXmlAttr = xexpr.isXmlAttribute();
      
      this.decompiler.addToken(85);
      if (xexpr.getExpression() instanceof EmptyExpression) {
        expr = createString("");
      } else {
        expr = transform(xexpr.getExpression());
      } 
      this.decompiler.addToken(86);
      if (isXmlAttr) {
        
        expr = createUnary(75, expr);
        Node prepend = createBinary(21, createString("\""), expr);

        
        expr = createBinary(21, prepend, createString("\""));
      }
      else {
        
        expr = createUnary(76, expr);
      } 
      pn = createBinary(21, pn, expr);
    } 

    
    pnXML.addChildToBack(pn);
    return pnXML;
  }
  
  private Node transformXmlMemberGet(XmlMemberGet node) {
    XmlRef ref = node.getMemberRef();
    Node pn = transform(node.getLeft());
    int flags = ref.isAttributeAccess() ? 2 : 0;
    if (node.getType() == 143) {
      flags |= 0x4;
      this.decompiler.addToken(143);
    } else {
      this.decompiler.addToken(108);
    } 
    return transformXmlRef(pn, ref, flags);
  }

  
  private Node transformXmlRef(XmlRef node) {
    int memberTypeFlags = node.isAttributeAccess() ? 2 : 0;
    
    return transformXmlRef((Node)null, node, memberTypeFlags);
  }
  
  private Node transformXmlRef(Node pn, XmlRef node, int memberTypeFlags) {
    if ((memberTypeFlags & 0x2) != 0)
      this.decompiler.addToken(147); 
    Name namespace = node.getNamespace();
    String ns = (namespace != null) ? namespace.getIdentifier() : null;
    if (ns != null) {
      this.decompiler.addName(ns);
      this.decompiler.addToken(144);
    } 
    if (node instanceof XmlPropRef) {
      String name = ((XmlPropRef)node).getPropName().getIdentifier();
      this.decompiler.addName(name);
      return createPropertyGet(pn, ns, name, memberTypeFlags);
    } 
    this.decompiler.addToken(83);
    Node expr = transform(((XmlElemRef)node).getExpression());
    this.decompiler.addToken(84);
    return createElementGet(pn, ns, expr, memberTypeFlags);
  }

  
  private Node transformDefaultXmlNamepace(UnaryExpression node) {
    this.decompiler.addToken(116);
    this.decompiler.addName(" xml");
    this.decompiler.addName(" namespace");
    this.decompiler.addToken(90);
    Node child = transform(node.getOperand());
    return createUnary(74, child);
  }





  
  private void addSwitchCase(Node switchBlock, Node caseExpression, Node statements) {
    if (switchBlock.getType() != 129) throw Kit.codeBug(); 
    Jump switchNode = (Jump)switchBlock.getFirstChild();
    if (switchNode.getType() != 114) throw Kit.codeBug();
    
    Node gotoTarget = Node.newTarget();
    if (caseExpression != null) {
      Jump caseNode = new Jump(115, caseExpression);
      caseNode.target = gotoTarget;
      switchNode.addChildToBack((Node)caseNode);
    } else {
      switchNode.setDefault(gotoTarget);
    } 
    switchBlock.addChildToBack(gotoTarget);
    switchBlock.addChildToBack(statements);
  }

  
  private void closeSwitch(Node switchBlock) {
    if (switchBlock.getType() != 129) throw Kit.codeBug(); 
    Jump switchNode = (Jump)switchBlock.getFirstChild();
    if (switchNode.getType() != 114) throw Kit.codeBug();
    
    Node switchBreakTarget = Node.newTarget();

    
    switchNode.target = switchBreakTarget;
    
    Node defaultTarget = switchNode.getDefault();
    if (defaultTarget == null) {
      defaultTarget = switchBreakTarget;
    }
    
    switchBlock.addChildAfter((Node)makeJump(5, defaultTarget), (Node)switchNode);
    
    switchBlock.addChildToBack(switchBreakTarget);
  }
  
  private Node createExprStatementNoReturn(Node expr, int lineno) {
    return new Node(133, expr, lineno);
  }
  
  private Node createString(String string) {
    return Node.newString(string);
  }









  
  private Node createCatch(String varName, Node catchCond, Node stmts, int lineno) {
    if (catchCond == null) {
      catchCond = new Node(128);
    }
    return new Node(124, createName(varName), catchCond, stmts, lineno);
  }


  
  private Node initFunction(FunctionNode fnNode, int functionIndex, Node statements, int functionType) {
    fnNode.setFunctionType(functionType);
    fnNode.addChildToBack(statements);
    
    int functionCount = fnNode.getFunctionCount();
    if (functionCount != 0)
    {
      fnNode.setRequiresActivation();
    }
    
    if (functionType == 2) {
      Name name = fnNode.getFunctionName();
      if (name != null && name.length() != 0 && fnNode.getSymbol(name.getIdentifier()) == null) {







        
        fnNode.putSymbol(new Symbol(109, name.getIdentifier()));
        Node setFn = new Node(133, new Node(8, Node.newString(49, name.getIdentifier()), new Node(63)));



        
        statements.addChildrenToFront(setFn);
      } 
    } 

    
    Node lastStmt = statements.getLastChild();
    if (lastStmt == null || lastStmt.getType() != 4) {
      statements.addChildToBack(new Node(4));
    }
    
    Node result = Node.newString(109, fnNode.getName());
    result.putIntProp(1, functionIndex);
    return result;
  }





  
  private Scope createLoopNode(Node loopLabel, int lineno) {
    Scope result = createScopeNode(132, lineno);
    if (loopLabel != null) {
      ((Jump)loopLabel).setLoop((Jump)result);
    }
    return result;
  }

  
  private Node createFor(Scope loop, Node init, Node test, Node incr, Node body) {
    if (init.getType() == 153) {


      
      Scope let = Scope.splitScope(loop);
      let.setType(153);
      let.addChildrenToBack(init);
      let.addChildToBack(createLoop((Jump)loop, 2, body, test, new Node(128), incr));
      
      return (Node)let;
    } 
    return createLoop((Jump)loop, 2, body, test, init, incr);
  }


  
  private Node createLoop(Jump loop, int loopType, Node body, Node cond, Node init, Node incr) {
    Node bodyTarget = Node.newTarget();
    Node condTarget = Node.newTarget();
    if (loopType == 2 && cond.getType() == 128) {
      cond = new Node(45);
    }
    Jump IFEQ = new Jump(6, cond);
    IFEQ.target = bodyTarget;
    Node breakTarget = Node.newTarget();
    
    loop.addChildToBack(bodyTarget);
    loop.addChildrenToBack(body);
    if (loopType == 1 || loopType == 2)
    {
      loop.addChildrenToBack(new Node(128, loop.getLineno()));
    }
    loop.addChildToBack(condTarget);
    loop.addChildToBack((Node)IFEQ);
    loop.addChildToBack(breakTarget);
    
    loop.target = breakTarget;
    Node continueTarget = condTarget;
    
    if (loopType == 1 || loopType == 2) {
      
      loop.addChildToFront((Node)makeJump(5, condTarget));
      
      if (loopType == 2) {
        int initType = init.getType();
        if (initType != 128) {
          if (initType != 122 && initType != 153) {
            init = new Node(133, init);
          }
          loop.addChildToFront(init);
        } 
        Node incrTarget = Node.newTarget();
        loop.addChildAfter(incrTarget, body);
        if (incr.getType() != 128) {
          incr = new Node(133, incr);
          loop.addChildAfter(incr, incrTarget);
        } 
        continueTarget = incrTarget;
      } 
    } 
    
    loop.setContinue(continueTarget);
    return (Node)loop;
  }




  
  private Node createForIn(int declType, Node loop, Node lhs, Node obj, Node body, boolean isForEach) {
    Node lvalue, assign;
    int destructuring = -1;
    int destructuringLen = 0;
    
    int type = lhs.getType();
    if (type == 122 || type == 153) {
      Node kid = lhs.getLastChild();
      int kidType = kid.getType();
      if (kidType == 65 || kidType == 66) {
        
        type = destructuring = kidType;
        lvalue = kid;
        destructuringLen = 0;
        if (kid instanceof ArrayLiteral)
          destructuringLen = ((ArrayLiteral)kid).getDestructuringLength(); 
      } else if (kidType == 39) {
        lvalue = Node.newString(39, kid.getString());
      } else {
        reportError("msg.bad.for.in.lhs");
        return null;
      } 
    } else if (type == 65 || type == 66) {
      destructuring = type;
      lvalue = lhs;
      destructuringLen = 0;
      if (lhs instanceof ArrayLiteral)
        destructuringLen = ((ArrayLiteral)lhs).getDestructuringLength(); 
    } else {
      lvalue = makeReference(lhs);
      if (lvalue == null) {
        reportError("msg.bad.for.in.lhs");
        return null;
      } 
    } 
    
    Node localBlock = new Node(141);
    int initType = isForEach ? 59 : ((destructuring != -1) ? 60 : 58);


    
    Node init = new Node(initType, obj);
    init.putProp(3, localBlock);
    Node cond = new Node(61);
    cond.putProp(3, localBlock);
    Node id = new Node(62);
    id.putProp(3, localBlock);
    
    Node newBody = new Node(129);
    
    if (destructuring != -1) {
      assign = createDestructuringAssignment(declType, lvalue, id);
      if (!isForEach && (destructuring == 66 || destructuringLen != 2))
      {


        
        reportError("msg.bad.for.in.destruct");
      }
    } else {
      assign = simpleAssignment(lvalue, id);
    } 
    newBody.addChildToBack(new Node(133, assign));
    newBody.addChildToBack(body);
    
    loop = createLoop((Jump)loop, 1, newBody, cond, (Node)null, (Node)null);
    loop.addChildToFront(init);
    if (type == 122 || type == 153)
      loop.addChildToFront(lhs); 
    localBlock.addChildToBack(loop);
    
    return localBlock;
  }

















  
  private Node createTryCatchFinally(Node tryBlock, Node catchBlocks, Node finallyBlock, int lineno) {
    boolean hasFinally = (finallyBlock != null && (finallyBlock.getType() != 129 || finallyBlock.hasChildren()));



    
    if (tryBlock.getType() == 129 && !tryBlock.hasChildren() && !hasFinally)
    {
      
      return tryBlock;
    }
    
    boolean hasCatch = catchBlocks.hasChildren();

    
    if (!hasFinally && !hasCatch)
    {
      return tryBlock;
    }
    
    Node handlerBlock = new Node(141);
    Jump pn = new Jump(81, tryBlock, lineno);
    pn.putProp(3, handlerBlock);
    
    if (hasCatch) {
      
      Node endCatch = Node.newTarget();
      pn.addChildToBack((Node)makeJump(5, endCatch));

      
      Node catchTarget = Node.newTarget();
      pn.target = catchTarget;
      
      pn.addChildToBack(catchTarget);
















































      
      Node catchScopeBlock = new Node(141);

      
      Node cb = catchBlocks.getFirstChild();
      boolean hasDefault = false;
      int scopeIndex = 0;
      while (cb != null) {
        Node condStmt; int catchLineNo = cb.getLineno();
        
        Node name = cb.getFirstChild();
        Node cond = name.getNext();
        Node catchStatement = cond.getNext();
        cb.removeChild(name);
        cb.removeChild(cond);
        cb.removeChild(catchStatement);




        
        catchStatement.addChildToBack(new Node(3));
        catchStatement.addChildToBack((Node)makeJump(5, endCatch));


        
        if (cond.getType() == 128) {
          condStmt = catchStatement;
          hasDefault = true;
        } else {
          condStmt = createIf(cond, catchStatement, (Node)null, catchLineNo);
        } 



        
        Node catchScope = new Node(57, name, createUseLocal(handlerBlock));
        
        catchScope.putProp(3, catchScopeBlock);
        catchScope.putIntProp(14, scopeIndex);
        catchScopeBlock.addChildToBack(catchScope);

        
        catchScopeBlock.addChildToBack(createWith(createUseLocal(catchScopeBlock), condStmt, catchLineNo));



        
        cb = cb.getNext();
        scopeIndex++;
      } 
      pn.addChildToBack(catchScopeBlock);
      if (!hasDefault) {
        
        Node rethrow = new Node(51);
        rethrow.putProp(3, handlerBlock);
        pn.addChildToBack(rethrow);
      } 
      
      pn.addChildToBack(endCatch);
    } 
    
    if (hasFinally) {
      Node finallyTarget = Node.newTarget();
      pn.setFinally(finallyTarget);

      
      pn.addChildToBack((Node)makeJump(135, finallyTarget));

      
      Node finallyEnd = Node.newTarget();
      pn.addChildToBack((Node)makeJump(5, finallyEnd));
      
      pn.addChildToBack(finallyTarget);
      Node fBlock = new Node(125, finallyBlock);
      fBlock.putProp(3, handlerBlock);
      pn.addChildToBack(fBlock);
      
      pn.addChildToBack(finallyEnd);
    } 
    handlerBlock.addChildToBack((Node)pn);
    return handlerBlock;
  }
  
  private Node createWith(Node obj, Node body, int lineno) {
    setRequiresActivation();
    Node result = new Node(129, lineno);
    result.addChildToBack(new Node(2, obj));
    Node bodyNode = new Node(123, body, lineno);
    result.addChildrenToBack(bodyNode);
    result.addChildToBack(new Node(3));
    return result;
  }

  
  private Node createIf(Node cond, Node ifTrue, Node ifFalse, int lineno) {
    int condStatus = isAlwaysDefinedBoolean(cond);
    if (condStatus == 1)
      return ifTrue; 
    if (condStatus == -1) {
      if (ifFalse != null) {
        return ifFalse;
      }
      
      return new Node(129, lineno);
    } 
    
    Node result = new Node(129, lineno);
    Node ifNotTarget = Node.newTarget();
    Jump IFNE = new Jump(7, cond);
    IFNE.target = ifNotTarget;
    
    result.addChildToBack((Node)IFNE);
    result.addChildrenToBack(ifTrue);
    
    if (ifFalse != null) {
      Node endTarget = Node.newTarget();
      result.addChildToBack((Node)makeJump(5, endTarget));
      result.addChildToBack(ifNotTarget);
      result.addChildrenToBack(ifFalse);
      result.addChildToBack(endTarget);
    } else {
      result.addChildToBack(ifNotTarget);
    } 
    
    return result;
  }
  
  private Node createCondExpr(Node cond, Node ifTrue, Node ifFalse) {
    int condStatus = isAlwaysDefinedBoolean(cond);
    if (condStatus == 1)
      return ifTrue; 
    if (condStatus == -1) {
      return ifFalse;
    }
    return new Node(102, cond, ifTrue, ifFalse);
  }
  
  private Node createUnary(int nodeType, Node child) {
    Node n;
    int status, childType = child.getType();
    switch (nodeType) {
      
      case 31:
        if (childType == 39) {

          
          child.setType(49);
          Node left = child;
          Node right = Node.newString(child.getString());
          n = new Node(nodeType, left, right);
        } else if (childType == 33 || childType == 36) {

          
          Node left = child.getFirstChild();
          Node right = child.getLastChild();
          child.removeChild(left);
          child.removeChild(right);
          n = new Node(nodeType, left, right);
        } else if (childType == 67) {
          Node ref = child.getFirstChild();
          child.removeChild(ref);
          n = new Node(69, ref);
        } else {
          
          n = new Node(nodeType, new Node(45), child);
        } 
        return n;
      
      case 32:
        if (childType == 39) {
          child.setType(137);
          return child;
        } 
        break;
      case 27:
        if (childType == 40) {
          int value = ScriptRuntime.toInt32(child.getDouble());
          child.setDouble((value ^ 0xFFFFFFFF));
          return child;
        } 
        break;
      case 29:
        if (childType == 40) {
          child.setDouble(-child.getDouble());
          return child;
        } 
        break;
      case 26:
        status = isAlwaysDefinedBoolean(child);
        if (status != 0) {
          int type;
          if (status == 1) {
            type = 44;
          } else {
            type = 45;
          } 
          if (childType == 45 || childType == 44) {
            child.setType(type);
            return child;
          } 
          return new Node(type);
        } 
        break;
    } 
    
    return new Node(nodeType, child);
  }
  
  private Node createCallOrNew(int nodeType, Node child) {
    int type = 0;
    if (child.getType() == 39) {
      String name = child.getString();
      if (name.equals("eval")) {
        type = 1;
      } else if (name.equals("With")) {
        type = 2;
      } 
    } else if (child.getType() == 33) {
      String name = child.getLastChild().getString();
      if (name.equals("eval")) {
        type = 1;
      }
    } 
    Node node = new Node(nodeType, child);
    if (type != 0) {
      
      setRequiresActivation();
      node.putIntProp(10, type);
    } 
    return node;
  }
  private Node createIncDec(int nodeType, boolean post, Node child) {
    Node n;
    int incrDecrMask;
    child = makeReference(child);
    int childType = child.getType();
    
    switch (childType) {
      case 33:
      case 36:
      case 39:
      case 67:
        n = new Node(nodeType, child);
        incrDecrMask = 0;
        if (nodeType == 107) {
          incrDecrMask |= 0x1;
        }
        if (post) {
          incrDecrMask |= 0x2;
        }
        n.putIntProp(13, incrDecrMask);
        return n;
    } 
    
    throw Kit.codeBug();
  }


  
  private Node createPropertyGet(Node target, String namespace, String name, int memberTypeFlags) {
    if (namespace == null && memberTypeFlags == 0) {
      if (target == null) {
        return createName(name);
      }
      checkActivationName(name, 33);
      if (ScriptRuntime.isSpecialProperty(name)) {
        Node ref = new Node(71, target);
        ref.putProp(17, name);
        return new Node(67, ref);
      } 
      return new Node(33, target, Node.newString(name));
    } 
    Node elem = Node.newString(name);
    memberTypeFlags |= 0x1;
    return createMemberRefGet(target, namespace, elem, memberTypeFlags);
  }










  
  private Node createElementGet(Node target, String namespace, Node elem, int memberTypeFlags) {
    if (namespace == null && memberTypeFlags == 0) {

      
      if (target == null) throw Kit.codeBug(); 
      return new Node(36, target, elem);
    } 
    return createMemberRefGet(target, namespace, elem, memberTypeFlags);
  }


  
  private Node createMemberRefGet(Node target, String namespace, Node elem, int memberTypeFlags) {
    Node ref, nsNode = null;
    if (namespace != null)
    {
      if (namespace.equals("*")) {
        nsNode = new Node(42);
      } else {
        nsNode = createName(namespace);
      } 
    }
    
    if (target == null) {
      if (namespace == null) {
        ref = new Node(79, elem);
      } else {
        ref = new Node(80, nsNode, elem);
      }
    
    } else if (namespace == null) {
      ref = new Node(77, target, elem);
    } else {
      ref = new Node(78, target, nsNode, elem);
    } 
    
    if (memberTypeFlags != 0) {
      ref.putIntProp(16, memberTypeFlags);
    }
    return new Node(67, ref);
  }
  private Node createBinary(int nodeType, Node left, Node right) {
    int leftStatus;
    switch (nodeType) {

      
      case 21:
        if (left.type == 41) {
          String s2;
          if (right.type == 41)
          { s2 = right.getString(); }
          else { if (right.type == 40)
            { s2 = ScriptRuntime.numberToString(right.getDouble(), 10);


              
              String s1 = left.getString();
              left.setString(s1.concat(s2));
              return left; }  break; }  String str1 = left.getString(); left.setString(str1.concat(s2)); return left;
        }  if (left.type == 40) {
          if (right.type == 40) {
            left.setDouble(left.getDouble() + right.getDouble());
            return left;
          }  if (right.type == 41) {
            
            String s1 = ScriptRuntime.numberToString(left.getDouble(), 10);
            String s2 = right.getString();
            right.setString(s1.concat(s2));
            return right;
          } 
        } 
        break;




      
      case 22:
        if (left.type == 40) {
          double ld = left.getDouble();
          if (right.type == 40) {
            
            left.setDouble(ld - right.getDouble());
            return left;
          }  if (ld == 0.0D)
          {
            return new Node(29, right); }  break;
        } 
        if (right.type == 40 && 
          right.getDouble() == 0.0D)
        {
          
          return new Node(28, left);
        }
        break;


      
      case 23:
        if (left.type == 40) {
          double ld = left.getDouble();
          if (right.type == 40) {
            
            left.setDouble(ld * right.getDouble());
            return left;
          }  if (ld == 1.0D)
          {
            return new Node(28, right); }  break;
        } 
        if (right.type == 40 && 
          right.getDouble() == 1.0D)
        {
          
          return new Node(28, left);
        }
        break;



      
      case 24:
        if (right.type == 40) {
          double rd = right.getDouble();
          if (left.type == 40) {
            
            left.setDouble(left.getDouble() / rd);
            return left;
          }  if (rd == 1.0D)
          {
            
            return new Node(28, left);
          }
        } 
        break;




      
      case 105:
        leftStatus = isAlwaysDefinedBoolean(left);
        if (leftStatus == -1)
        {
          return left; } 
        if (leftStatus == 1)
        {
          return right;
        }
        break;





      
      case 104:
        leftStatus = isAlwaysDefinedBoolean(left);
        if (leftStatus == 1)
        {
          return left; } 
        if (leftStatus == -1)
        {
          return right;
        }
        break;
    } 

    
    return new Node(nodeType, left, right);
  } private Node createAssignment(int assignType, Node left, Node right) {
    int assignOp;
    Node op, obj, opLeft, lvalueLeft, id, node1;
    int type;
    Node node2, node3, ref = makeReference(left);
    if (ref == null) {
      if (left.getType() == 65 || left.getType() == 66) {

        
        if (assignType != 90) {
          reportError("msg.bad.destruct.op");
          return right;
        } 
        return createDestructuringAssignment(-1, left, right);
      } 
      reportError("msg.bad.assign.left");
      return right;
    } 
    left = ref;

    
    switch (assignType) {
      case 90:
        return simpleAssignment(left, right);
      case 91: assignOp = 9; break;
      case 92: assignOp = 10; break;
      case 93: assignOp = 11; break;
      case 94: assignOp = 18; break;
      case 95: assignOp = 19; break;
      case 96: assignOp = 20; break;
      case 97: assignOp = 21; break;
      case 98: assignOp = 22; break;
      case 99: assignOp = 23; break;
      case 100: assignOp = 24; break;
      case 101: assignOp = 25; break;
      default: throw Kit.codeBug();
    } 
    
    int nodeType = left.getType();
    switch (nodeType) {
      case 39:
        op = new Node(assignOp, left, right);
        lvalueLeft = Node.newString(49, left.getString());
        return new Node(8, lvalueLeft, op);
      
      case 33:
      case 36:
        obj = left.getFirstChild();
        id = left.getLastChild();
        
        type = (nodeType == 33) ? 139 : 140;


        
        node2 = new Node(138);
        node3 = new Node(assignOp, node2, right);
        return new Node(type, obj, id, node3);
      
      case 67:
        ref = left.getFirstChild();
        checkMutableReference(ref);
        opLeft = new Node(138);
        node1 = new Node(assignOp, opLeft, right);
        return new Node(142, ref, node1);
    } 

    
    throw Kit.codeBug();
  }
  
  private Node createUseLocal(Node localBlock) {
    if (141 != localBlock.getType()) throw Kit.codeBug(); 
    Node result = new Node(54);
    result.putProp(3, localBlock);
    return result;
  }
  
  private Jump makeJump(int type, Node target) {
    Jump n = new Jump(type);
    n.target = target;
    return n;
  }
  
  private Node makeReference(Node node) {
    int type = node.getType();
    switch (type) {
      case 33:
      case 36:
      case 39:
      case 67:
        return node;
      case 38:
        node.setType(70);
        return new Node(67, node);
    } 
    
    return null;
  }
  
  private static int isAlwaysDefinedBoolean(Node node) {
    double num;
    switch (node.getType()) {
      case 42:
      case 44:
        return -1;
      case 45:
        return 1;
      case 40:
        num = node.getDouble();
        if (num == num && num != 0.0D) {
          return 1;
        }
        return -1;
    } 

    
    return 0;
  }

  
  boolean isDestructuring(Node n) {
    return (n instanceof DestructuringForm && ((DestructuringForm)n).isDestructuring());
  }

  
  Node decompileFunctionHeader(FunctionNode fn) {
    Node mexpr = null;
    if (fn.getFunctionName() != null) {
      this.decompiler.addName(fn.getName());
    } else if (fn.getMemberExprNode() != null) {
      mexpr = transform(fn.getMemberExprNode());
    } 
    this.decompiler.addToken(87);
    List<AstNode> params = fn.getParams();
    for (int i = 0; i < params.size(); i++) {
      decompile(params.get(i));
      if (i < params.size() - 1) {
        this.decompiler.addToken(89);
      }
    } 
    this.decompiler.addToken(88);
    if (!fn.isExpressionClosure()) {
      this.decompiler.addEOL(85);
    }
    return mexpr;
  }
  
  void decompile(AstNode node) {
    switch (node.getType()) {
      case 65:
        decompileArrayLiteral((ArrayLiteral)node);
      
      case 66:
        decompileObjectLiteral((ObjectLiteral)node);
      
      case 41:
        this.decompiler.addString(((StringLiteral)node).getValue());
      
      case 39:
        this.decompiler.addName(((Name)node).getIdentifier());
      
      case 40:
        this.decompiler.addNumber(((NumberLiteral)node).getNumber());
      
      case 33:
        decompilePropertyGet((PropertyGet)node);
      
      case 128:
        return;
      case 36:
        decompileElementGet((ElementGet)node);
      
      case 43:
        this.decompiler.addToken(node.getType());
    } 
    
    Kit.codeBug("unexpected token: " + Token.typeToName(node.getType()));
  }



  
  void decompileArrayLiteral(ArrayLiteral node) {
    this.decompiler.addToken(83);
    List<AstNode> elems = node.getElements();
    int size = elems.size();
    for (int i = 0; i < size; i++) {
      AstNode elem = elems.get(i);
      decompile(elem);
      if (i < size - 1) {
        this.decompiler.addToken(89);
      }
    } 
    this.decompiler.addToken(84);
  }

  
  void decompileObjectLiteral(ObjectLiteral node) {
    this.decompiler.addToken(85);
    List<ObjectProperty> props = node.getElements();
    int size = props.size();
    for (int i = 0; i < size; i++) {
      ObjectProperty prop = props.get(i);
      boolean destructuringShorthand = Boolean.TRUE.equals(prop.getProp(26));
      
      decompile(prop.getLeft());
      if (!destructuringShorthand) {
        this.decompiler.addToken(103);
        decompile(prop.getRight());
      } 
      if (i < size - 1) {
        this.decompiler.addToken(89);
      }
    } 
    this.decompiler.addToken(86);
  }

  
  void decompilePropertyGet(PropertyGet node) {
    decompile(node.getTarget());
    this.decompiler.addToken(108);
    decompile((AstNode)node.getProperty());
  }

  
  void decompileElementGet(ElementGet node) {
    decompile(node.getTarget());
    this.decompiler.addToken(83);
    decompile(node.getElement());
    this.decompiler.addToken(84);
  }
}
