package org.fife.rsta.ac.js.ast.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.Logger;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.CodeBlock;
import org.fife.rsta.ac.js.ast.JavaScriptFunctionDeclaration;
import org.fife.rsta.ac.js.ast.JavaScriptVariableDeclaration;
import org.fife.rsta.ac.js.ast.TypeDeclarationOptions;
import org.fife.rsta.ac.js.ast.type.ArrayTypeDeclaration;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.completion.JavaScriptInScriptFunctionCompletion;
import org.fife.rsta.ac.js.resolver.JavaScriptResolver;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.CatchClause;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.ForInLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.SwitchCase;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.mozilla.javascript.ast.WhileLoop;




public class JavaScriptAstParser
  extends JavaScriptParser
{
  private ArrayList<ProcessFunctionType> functions;
  
  public JavaScriptAstParser(SourceCompletionProvider provider, int dot, TypeDeclarationOptions options) {
    super(provider, dot, options);
    this.functions = new ArrayList<>();
  }



  
  public CodeBlock convertAstNodeToCodeBlock(AstRoot root, Set<Completion> set, String entered) {
    this.functions.clear();
    CodeBlock block = new CodeBlock(0);
    addCodeBlock((Node)root, set, entered, block, 2147483647);
    setFunctionValues();
    this.provider.getLanguageSupport().getJavaScriptParser()
      .setVariablesAndFunctions(this.provider.getVariableResolver());
    return block;
  }


  
  private void setFunctionValues() {
    for (ProcessFunctionType type : this.functions) {
      type.dec.setTypeDeclaration(type.typeNode);
    }
  }













  
  private void addCodeBlock(Node parent, Set<Completion> set, String entered, CodeBlock codeBlock, int offset) {
    if (parent == null) {
      return;
    }
    Node child = parent.getFirstChild();
    
    while (child != null) {
      CodeBlock childBlock = codeBlock;
      if (child instanceof AstNode) {
        AstNode node = (AstNode)child;
        int start = node.getAbsolutePosition();
        childBlock = codeBlock.addChildCodeBlock(start);
        childBlock.setEndOffset(offset);
      } 
      iterateNode((AstNode)child, set, entered, childBlock, offset);
      
      child = child.getNext();
    } 
  }



  
  protected void iterateNode(AstNode child, Set<Completion> set, String entered, CodeBlock block, int offset) {
    if (child == null) {
      return;
    }
    Logger.log(JavaScriptHelper.convertNodeToSource(child));
    Logger.log(child.shortName());
    
    if (JavaScriptHelper.isInfixOnly(child))
    {
      processInfix((Node)child, block, set, entered, offset);
    }
    
    switch (child.getType()) {
      case 109:
        processFunctionNode((Node)child, block, set, entered, offset);
      
      case 122:
        processVariableNode((Node)child, block, set, entered, offset);
      
      case 119:
        processForNode((Node)child, block, set, entered, offset);

      
      case 117:
        processWhileNode((Node)child, block, set, entered, offset);

      
      case 129:
        addCodeBlock((Node)child, set, entered, block, offset);

      
      case 90:
        reassignVariable(child, block, offset);

      
      case 133:
        processExpressionNode((Node)child, block, set, entered, offset);

      
      case 112:
        processIfThenElse((Node)child, block, set, entered, offset);

      
      case 81:
        processTryCatchNode((Node)child, block, set, entered, offset);

      
      case 118:
        processDoNode((Node)child, block, set, entered, offset);

      
      case 114:
        processSwitchNode((Node)child, block, set, entered, offset);

      
      case 115:
        processCaseNode((Node)child, block, set, entered, offset);

      
      case -1:
      case 4:
      case 30:
      case 33:
      case 38:
      case 39:
      case 74:
      case 120:
      case 121:
      case 124:
      case 128:
      case 147:
        return;

      
      case 134:
        processExpressionStatement((Node)child, block, set, entered, offset);
    } 

    
    Logger.log("Unhandled: " + child.getClass() + " (\"" + child
        .toString() + "\":" + child.getLineno());
  }






  
  private void processExpressionStatement(Node child, CodeBlock block, Set<Completion> set, String entered, int offset) {
    ExpressionStatement exp = (ExpressionStatement)child;
    
    AstNode expNode = exp.getExpression();
    iterateNode(expNode, set, entered, block, offset);
  }


  
  private void reassignVariable(AstNode assign, CodeBlock block, int locationOffSet) {
    Assignment assignNode = (Assignment)assign;
    
    AstNode leftNode = assignNode.getLeft();
    
    AstNode rightNode = assignNode.getRight();

    
    String name = (leftNode.getType() == 39) ? ((Name)leftNode).getIdentifier() : null;
    if (name != null) {
      int start = assignNode.getAbsolutePosition();
      int offset = start + assignNode.getLength();

      
      if (offset <= this.dot) {
        
        JavaScriptVariableDeclaration dec = this.provider.getVariableResolver().findDeclaration(name, this.dot);
        if (dec != null && (dec
          .getCodeBlock() == null || dec.getCodeBlock()
          .contains(this.dot))) {
          
          dec.setTypeDeclaration(rightNode, isPreProcessing());
        
        }
        else {
          
          addVariableToResolver(leftNode, rightNode, block, locationOffSet);
        } 
      } 
    } 
  }



  
  private void addVariableToResolver(AstNode name, AstNode target, CodeBlock block, int offset) {
    JavaScriptVariableDeclaration dec = extractVariableFromNode(name, block, offset, target);
    
    if (dec != null && target != null && 
      JavaScriptHelper.canResolveVariable(name, target)) {
      dec.setTypeDeclaration(target);
    
    }
    else if (dec != null) {
      dec.setTypeDeclaration(this.provider.getTypesFactory().getDefaultTypeDeclaration());
    } 
    
    if (dec != null && 
      canAddVariable(block))
    {
      
      if (isPreProcessing()) {
        block.setStartOffSet(0);
        
        dec.setTypeDeclarationOptions(this.options);
        this.provider.getVariableResolver().addPreProcessingVariable(dec);
      } else {
        
        this.provider.getVariableResolver().addLocalVariable(dec);
      } 
    }
  }



  
  private void processCaseNode(Node child, CodeBlock block, Set<Completion> set, String entered, int offset) {
    SwitchCase switchCase = (SwitchCase)child;
    List<AstNode> statements = switchCase.getStatements();
    int start = switchCase.getAbsolutePosition();
    offset = start + switchCase.getLength();
    if (canProcessNode((AstNode)switchCase)) {
      block = block.addChildCodeBlock(start);
      block.setEndOffset(offset);
      if (statements != null) {
        for (AstNode node : statements) {
          iterateNode(node, set, entered, block, offset);
        }
      }
    } 
  }



  
  private void processSwitchNode(Node child, CodeBlock block, Set<Completion> set, String entered, int offset) {
    SwitchStatement switchStatement = (SwitchStatement)child;
    if (canProcessNode((AstNode)switchStatement)) {
      List<SwitchCase> cases = switchStatement.getCases();
      if (cases != null) {
        for (SwitchCase switchCase : cases) {
          iterateNode((AstNode)switchCase, set, entered, block, offset);
        }
      }
    } 
  }





  
  private void processTryCatchNode(Node child, CodeBlock block, Set<Completion> set, String entered, int offset) {
    TryStatement tryStatement = (TryStatement)child;
    if (canProcessNode((AstNode)tryStatement)) {
      
      offset = tryStatement.getTryBlock().getAbsolutePosition() + tryStatement.getTryBlock().getLength();
      addCodeBlock((Node)tryStatement.getTryBlock(), set, entered, block, offset);

      
      for (int i = 0; i < tryStatement.getCatchClauses().size(); i++) {
        
        CatchClause clause = tryStatement.getCatchClauses().get(i);
        if (canProcessNode((AstNode)clause)) {
          offset = clause.getAbsolutePosition() + clause.getLength();
          CodeBlock catchBlock = block.getParent().addChildCodeBlock(clause
              .getAbsolutePosition());
          catchBlock.setEndOffset(offset);
          Name name = clause.getVarName();
          
          JavaScriptVariableDeclaration dec = extractVariableFromNode((AstNode)name, catchBlock, offset);
          
          if (dec != null) {
            dec.setTypeDeclaration((AstNode)clause);
          }
          
          addCodeBlock((Node)clause.getBody(), set, entered, catchBlock, offset);
        } 
      } 

      
      if (tryStatement.getFinallyBlock() != null) {
        AstNode finallyNode = tryStatement.getFinallyBlock();
        if (canProcessNode(finallyNode)) {
          
          offset = finallyNode.getAbsolutePosition() + finallyNode.getLength();
          
          CodeBlock finallyBlock = block.getParent().addChildCodeBlock(tryStatement
              .getFinallyBlock()
              .getAbsolutePosition());
          addCodeBlock((Node)finallyNode, set, entered, finallyBlock, offset);
          
          finallyBlock.setEndOffset(offset);
        } 
      } 
    } 
  }





  
  private void processIfThenElse(Node child, CodeBlock block, Set<Completion> set, String entered, int offset) {
    IfStatement ifStatement = (IfStatement)child;
    if (canProcessNode((AstNode)ifStatement)) {
      
      offset = ifStatement.getAbsolutePosition() + ifStatement.getLength();
      addCodeBlock((Node)ifStatement.getThenPart(), set, entered, block, offset);
      AstNode elseNode = ifStatement.getElsePart();
      if (elseNode != null) {
        int start = elseNode.getAbsolutePosition();
        CodeBlock childBlock = block.addChildCodeBlock(start);
        offset = start + elseNode.getLength();
        iterateNode(elseNode, set, entered, childBlock, offset);
        childBlock.setEndOffset(offset);
      } 
    } 
  }






  
  private void processExpressionNode(Node child, CodeBlock block, Set<Completion> set, String entered, int offset) {
    if (child instanceof ExpressionStatement) {
      ExpressionStatement expr = (ExpressionStatement)child;
      iterateNode(expr.getExpression(), set, entered, block, offset);
    } 
  }





  
  private void processWhileNode(Node child, CodeBlock block, Set<Completion> set, String entered, int offset) {
    WhileLoop loop = (WhileLoop)child;
    if (canProcessNode((AstNode)loop)) {
      offset = loop.getAbsolutePosition() + loop.getLength();
      addCodeBlock((Node)loop.getBody(), set, entered, block, offset);
    } 
  }





  
  private void processDoNode(Node child, CodeBlock block, Set<Completion> set, String entered, int offset) {
    DoLoop loop = (DoLoop)child;
    if (canProcessNode((AstNode)loop)) {
      offset = loop.getAbsolutePosition() + loop.getLength();
      addCodeBlock((Node)loop.getBody(), set, entered, block, offset);
    } 
  }





  
  private void processInfix(Node child, CodeBlock block, Set<Completion> set, String entered, int offset) {
    InfixExpression epre = (InfixExpression)child;
    AstNode target = epre.getLeft();
    if (canProcessNode(target)) {
      extractVariableFromNode(target, block, offset);
      addCodeBlock((Node)epre, set, entered, block, offset);
    } 
  }







  
  private void processFunctionNode(Node child, CodeBlock block, Set<Completion> set, String entered, int offset) {
    FunctionNode fn = (FunctionNode)child;
    String jsdoc = fn.getJsDoc();
    TypeDeclaration returnType = getFunctionType(fn);
    
    JavaScriptInScriptFunctionCompletion fc = new JavaScriptInScriptFunctionCompletion((CompletionProvider)this.provider, fn.getName(), returnType);
    fc.setShortDescription(jsdoc);
    offset = fn.getAbsolutePosition() + fn.getLength();
    
    if (fn.getParamCount() > 0) {
      List<AstNode> fnParams = fn.getParams();
      List<ParameterizedCompletion.Parameter> params = new ArrayList<>();
      for (int i = 0; i < fn.getParamCount(); i++) {
        String paramName = null;
        AstNode node = fnParams.get(i);
        switch (node.getType()) {
          case 39:
            paramName = ((Name)node).getIdentifier();
            break;
        } 

        
        ParameterizedCompletion.Parameter param = new ParameterizedCompletion.Parameter(null, paramName);
        params.add(param);
        
        if (!isPreProcessing() && canProcessNode((AstNode)fn)) {
          JavaScriptVariableDeclaration dec = extractVariableFromNode(node, block, offset);
          this.provider.getVariableResolver().addLocalVariable(dec);
        } 
      } 
      fc.setParams(params);
    } 
    
    if (isPreProcessing()) {
      block.setStartOffSet(0);
    }
    
    if (isPreProcessing()) {
      JavaScriptFunctionDeclaration function = createJavaScriptFunction(fc.getLookupName(), offset, block, returnType, fn);
      function.setTypeDeclarationOptions(this.options);
      this.provider.getVariableResolver().addPreProcessingFunction(function);
    } else {
      
      this.provider.getVariableResolver().addLocalFunction(createJavaScriptFunction(fc.getLookupName(), offset, block, returnType, fn));
    } 

    
    addCodeBlock((Node)fn.getBody(), set, entered, block, offset);
    
    if (entered.indexOf('.') == -1) {
      set.add(fc);
    }
  }

  
  private JavaScriptFunctionDeclaration createJavaScriptFunction(String lookupName, int offset, CodeBlock block, TypeDeclaration returnType, FunctionNode fn) {
    Name name = fn.getFunctionName();
    JavaScriptFunctionDeclaration function = new JavaScriptFunctionDeclaration(lookupName, offset, block, returnType);
    if (name != null) {
      int start = name.getAbsolutePosition();
      int end = start + name.getLength();
      function.setStartOffset(start);
      function.setEndOffset(end);
      function.setFunctionName(fn.getName());
    } 
    
    return function;
  }

  
  private boolean canProcessNode(AstNode node) {
    int start = node.getAbsolutePosition();
    int offset = start + node.getLength();
    return (this.dot >= start && this.dot < offset);
  }

  
  private TypeDeclaration getFunctionType(FunctionNode fn) {
    FunctionReturnVisitor visitor = new FunctionReturnVisitor();
    fn.visit(visitor);
    return visitor.getCommonReturnType();
  }






  
  private void processVariableNode(Node child, CodeBlock block, Set<Completion> set, String entered, int offset) {
    if (block.contains(this.dot) || isPreProcessing()) {
      
      VariableDeclaration varDec = (VariableDeclaration)child;
      List<VariableInitializer> vars = varDec.getVariables();
      for (VariableInitializer var : vars) {
        extractVariableFromNode(var, block, offset);
      }
    } 
  }






  
  private void processForNode(Node child, CodeBlock block, Set<Completion> set, String entered, int offset) {
    if (child instanceof ForLoop) {
      ForLoop loop = (ForLoop)child;
      offset = loop.getAbsolutePosition() + loop.getLength();
      if (canProcessNode((AstNode)loop)) {
        iterateNode(loop.getInitializer(), set, entered, block, offset);
        addCodeBlock((Node)loop.getBody(), set, entered, block, offset);
      }
    
    } else if (child instanceof ForInLoop) {
      ForInLoop loop = (ForInLoop)child;
      offset = loop.getAbsolutePosition() + loop.getLength();
      if (canProcessNode((AstNode)loop)) {
        AstNode iteratedObject = loop.getIteratedObject();
        AstNode iterator = loop.getIterator();
        if (iterator != null && 
          iterator.getType() == 122) {
          
          VariableDeclaration vd = (VariableDeclaration)iterator;
          List<VariableInitializer> variables = vd.getVariables();
          if (variables.size() == 1) {
            
            VariableInitializer vi = variables.get(0);
            if (loop.isForEach()) {
              extractVariableForForEach(vi, block, offset, iteratedObject);
            }
            else {
              
              extractVariableForForIn(vi, block, offset, iteratedObject);
            } 
          } 
        } 

        
        addCodeBlock((Node)loop.getBody(), set, entered, block, offset);
      } 
    } 
  }









  
  private void extractVariableFromNode(VariableInitializer initializer, CodeBlock block, int offset) {
    AstNode target = initializer.getTarget();
    
    if (target != null) {
      addVariableToResolver(target, initializer.getInitializer(), block, offset);
    }
  }













  
  private void extractVariableForForEach(VariableInitializer initializer, CodeBlock block, int offset, AstNode iteratedObject) {
    AstNode target = initializer.getTarget();
    if (target != null) {
      JavaScriptVariableDeclaration dec = extractVariableFromNode(target, block, offset);
      
      if (dec != null && iteratedObject != null && 
        
        JavaScriptHelper.canResolveVariable(target, iteratedObject)) {



        
        JavaScriptResolver resolver = this.provider.getJavaScriptEngine().getJavaScriptResolver(this.provider);
        if (resolver != null) {
          
          TypeDeclaration iteratorDec = resolver.resolveNode(iteratedObject);
          if (iteratorDec instanceof ArrayTypeDeclaration) {
            
            dec
              .setTypeDeclaration(((ArrayTypeDeclaration)iteratorDec)
                .getArrayType());
          } else {
            
            dec.setTypeDeclaration(iteratorDec);
          } 
        } 

        
        if (canAddVariable(block)) {
          this.provider.getVariableResolver().addLocalVariable(dec);
        }
      } 
    } 
  }











  
  private void extractVariableForForIn(VariableInitializer initializer, CodeBlock block, int offset, AstNode iteratedObject) {
    AstNode target = initializer.getTarget();
    if (target != null) {
      JavaScriptVariableDeclaration dec = extractVariableFromNode(target, block, offset);
      
      if (dec != null && iteratedObject != null && 
        
        JavaScriptHelper.canResolveVariable(target, iteratedObject)) {



        
        JavaScriptResolver resolver = this.provider.getJavaScriptEngine().getJavaScriptResolver(this.provider);
        if (resolver != null) {
          
          TypeDeclaration iteratorDec = resolver.resolveNode(iteratedObject);
          if (iteratorDec instanceof ArrayTypeDeclaration) {
            
            dec.setTypeDeclaration(this.provider.getTypesFactory().getTypeDeclaration("JSNumber"));
          }
          else {
            
            dec.setTypeDeclaration(this.provider.getTypesFactory()
                .getDefaultTypeDeclaration());
          } 
        } 

        
        if (canAddVariable(block)) {
          this.provider.getVariableResolver().addLocalVariable(dec);
        }
      } 
    } 
  }

  
  private boolean canAddVariable(CodeBlock block) {
    if (!isPreProcessing()) {
      return true;
    }
    CodeBlock parent = block.getParent();
    return (parent != null && parent.getStartOffset() == 0);
  }









  
  private JavaScriptVariableDeclaration extractVariableFromNode(AstNode node, CodeBlock block, int offset) {
    return extractVariableFromNode(node, block, offset, (AstNode)null);
  }










  
  private JavaScriptVariableDeclaration extractVariableFromNode(AstNode node, CodeBlock block, int offset, AstNode initializer) {
    JavaScriptVariableDeclaration dec = null;

    
    if (node != null)
    { Name name;
      switch (node.getType())
      { case 39:
          name = (Name)node;
          
          dec = new JavaScriptVariableDeclaration(name.getIdentifier(), offset, this.provider, block);
          dec.setStartOffset(name.getAbsolutePosition());
          dec.setEndOffset(name.getAbsolutePosition() + name.getLength());
          if (initializer != null && initializer
            .getType() == 38) {


            
            ProcessFunctionType func = new ProcessFunctionType();
            func.dec = dec;
            func.typeNode = initializer;
            this.functions.add(func);
          } 
          if (initializer == null || 
            JavaScriptHelper.canResolveVariable((AstNode)name, initializer))
          {
            block.addVariable(dec);
          }







          
          return dec; }  Logger.log("... Unknown var target type: " + node.getClass()); }  return dec;
  }

  
  public SourceCompletionProvider getProvider() {
    return this.provider;
  }

  
  public int getDot() {
    return this.dot;
  }
  
  private class FunctionReturnVisitor
    implements NodeVisitor
  {
    private ArrayList<ReturnStatement> returnStatements = new ArrayList<>();

    
    public boolean visit(AstNode node) {
      switch (node.getType()) {
        case 4:
          this.returnStatements.add((ReturnStatement)node);
          break;
      } 
      return true;
    }







    
    public TypeDeclaration getCommonReturnType() {
      TypeDeclaration commonType = null;
      for (ReturnStatement rs : this.returnStatements) {
        AstNode returnValue = rs.getReturnValue();

        
        TypeDeclaration type = JavaScriptAstParser.this.provider.getJavaScriptEngine().getJavaScriptResolver(JavaScriptAstParser.this.provider).resolveNode(returnValue);
        
        if (commonType == null) {
          commonType = type;
          continue;
        } 
        if (!commonType.equals(type)) {
          
          commonType = JavaScriptAstParser.this.provider.getTypesFactory().getDefaultTypeDeclaration();
          
          break;
        } 
      } 
      
      return commonType;
    }
    
    private FunctionReturnVisitor() {}
  }
  
  static class ProcessFunctionType {
    AstNode typeNode;
    JavaScriptVariableDeclaration dec;
  }
}
