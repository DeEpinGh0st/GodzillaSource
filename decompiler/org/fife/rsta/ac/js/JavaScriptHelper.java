package org.fife.rsta.ac.js;

import java.io.StringReader;
import org.fife.rsta.ac.js.ast.type.ArrayTypeDeclaration;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.resolver.JavaScriptCompletionResolver;
import org.fife.rsta.ac.js.resolver.JavaScriptResolver;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.ArrayLiteral;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ElementGet;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NewExpression;
import org.mozilla.javascript.ast.NodeVisitor;














public class JavaScriptHelper
{
  private static final String INFIX = InfixExpression.class
    .getName();









  
  public static boolean canResolveVariable(AstNode target, AstNode initialiser) {
    String varName = target.toSource();
    try {
      String init = initialiser.toSource();
      String[] splitInit = init.split("\\.");
      if (splitInit.length > 0) {
        return !varName.equals(splitInit[0]);
      }
    }
    catch (Exception exception) {}

    
    return false;
  }








  
  public static final ParseText parseEnteredText(String text) {
    CompilerEnvirons env = new CompilerEnvirons();
    env.setIdeMode(true);
    env.setErrorReporter(new ErrorReporter()
        {
          public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {}







          
          public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
            return null;
          }





          
          public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {}
        });
    env.setRecoverFromErrors(true);
    Parser parser = new Parser(env);
    StringReader r = new StringReader(text);
    ParseText pt = new ParseText();
    try {
      AstRoot root = parser.parse(r, null, 0);
      ParseTextVisitor visitor = new ParseTextVisitor(text);
      root.visitAll(visitor);
      pt.isNew = visitor.isNew();
      pt.text = visitor.getLastNodeSource();
    }
    catch (Exception e) {
      e.printStackTrace();
    } 
    return pt;
  }






  
  public static String getFunctionNameLookup(AstNode node, SourceCompletionProvider provider) {
    FunctionCall call = findFunctionCallFromNode(node);
    return provider.getJavaScriptEngine().getJavaScriptResolver(provider).getFunctionNameLookup(call, provider);
  }














  
  public static FunctionCall findFunctionCallFromNode(AstNode node) {
    AstNode parent = node;
    for (int i = 0; i < 3; i++) {
      
      if (parent == null || parent instanceof AstRoot)
        break; 
      if (parent instanceof FunctionCall) {
        return (FunctionCall)parent;
      }
      parent = parent.getParent();
    } 
    return null;
  }










  
  public static final TypeDeclaration tokenToNativeTypeDeclaration(AstNode typeNode, SourceCompletionProvider provider) {
    if (typeNode != null) {
      AstNode expr; TypeDeclaration dec; String self; switch (typeNode.getType()) {
        case 134:
          expr = ((ExpressionStatement)typeNode).getExpression();
          if (expr.getType() == 39) {
            return provider.resolveTypeDeclation(((Name)expr)
                .getIdentifier());
          }
          break;
        case 124:
          return getTypeDeclaration("JSError", provider);
        case 39:
          return provider.resolveTypeDeclation(((Name)typeNode)
              .getIdentifier());
        case 30:
          return processNewNode(typeNode, provider);
        case 40:
          return getTypeDeclaration("JSNumber", provider);
        case 66:
          return getTypeDeclaration("JSObject", provider);
        case 41:
          return getTypeDeclaration("JSString", provider);
        case 44:
        case 45:
          return getTypeDeclaration("JSBoolean", provider);
        case 65:
          return createArrayType(typeNode, provider);
        case 36:
          dec = findGetElementType(typeNode, provider);
          if (dec != null) {
            return dec;
          }
          break;

        
        case 43:
          self = provider.getSelf();
          if (self == null) {
            self = "JSGlobal";
          }
          return getTypeDeclaration(self, provider);

        
        case 145:
          if (provider.isXMLSupported())
          {
            return getTypeDeclaration("E4XXML", provider);
          }
          break;
      } 

      
      if (isInfixOnly(typeNode)) {
        TypeDeclaration typeDeclaration = getTypeFromInFixExpression(typeNode, provider);
        if (typeDeclaration != null) {
          return typeDeclaration;
        }
      } 
    } 
    return null;
  }











  
  private static TypeDeclaration findGetElementType(AstNode node, SourceCompletionProvider provider) {
    ElementGet getElement = (ElementGet)node;
    
    AstNode target = getElement.getTarget();
    if (target != null) {
      JavaScriptCompletionResolver resolver = new JavaScriptCompletionResolver(provider);
      TypeDeclaration typeDec = resolver.resolveNode(target);
      if (typeDec != null && 
        typeDec instanceof ArrayTypeDeclaration) {
        return ((ArrayTypeDeclaration)typeDec).getArrayType();
      }
    } 
    
    return null;
  }







  
  private static TypeDeclaration createArrayType(AstNode typeNode, SourceCompletionProvider provider) {
    TypeDeclaration array = getTypeDeclaration("JSArray", provider);
    if (array != null) {
      
      ArrayTypeDeclaration arrayDec = new ArrayTypeDeclaration(array.getPackageName(), array.getAPITypeName(), array.getJSName());
      ArrayLiteral arrayLit = (ArrayLiteral)typeNode;
      
      arrayDec.setArrayType(findArrayType(arrayLit, provider));
      return (TypeDeclaration)arrayDec;
    } 
    
    return null;
  }







  
  private static TypeDeclaration findArrayType(ArrayLiteral arrayLit, SourceCompletionProvider provider) {
    TypeDeclaration dec = null;
    boolean first = true;
    JavaScriptResolver resolver = provider.getJavaScriptEngine().getJavaScriptResolver(provider);
    for (AstNode element : arrayLit.getElements()) {
      TypeDeclaration elementType = resolver.resolveNode(element);
      if (first) {
        dec = elementType;
        first = false;
        continue;
      } 
      if (elementType != null && !elementType.equals(dec)) {
        dec = provider.getTypesFactory().getDefaultTypeDeclaration();
        
        break;
      } 
    } 
    return (dec != null) ? dec : provider.getTypesFactory().getDefaultTypeDeclaration();
  }

  
  private static TypeDeclaration processNewNode(AstNode typeNode, SourceCompletionProvider provider) {
    String newName = findNewExpressionString(typeNode);
    if (newName != null) {
      
      TypeDeclaration newType = createNewTypeDeclaration(newName);
      if (newType.isQualified()) {
        return newType;
      }
      return findOrMakeTypeDeclaration(newName, provider);
    } 
    
    return null;
  }


  
  public static TypeDeclaration findOrMakeTypeDeclaration(String name, SourceCompletionProvider provider) {
    TypeDeclaration newType = getTypeDeclaration(name, provider);
    if (newType == null) {
      newType = createNewTypeDeclaration(name);
    }
    return newType;
  }


  
  public static TypeDeclaration createNewTypeDeclaration(String newName) {
    String pName = (newName.indexOf('.') > 0) ? newName.substring(0, newName
        .lastIndexOf('.')) : "";
    String cName = (newName.indexOf('.') > 0) ? newName.substring(newName
        .lastIndexOf('.') + 1, newName.length()) : newName;
    
    return new TypeDeclaration(pName, cName, newName);
  }

  
  public static boolean isInfixOnly(AstNode typeNode) {
    return (typeNode instanceof InfixExpression && typeNode
      .getClass().getName().equals(INFIX));
  }








  
  private static class InfixVisitor
    implements NodeVisitor
  {
    private String type = null;
    private SourceCompletionProvider provider;
    
    private InfixVisitor(SourceCompletionProvider provider) {
      this.provider = provider;
    }


    
    public boolean visit(AstNode node) {
      if (!(node instanceof InfixExpression)) {
        
        JavaScriptResolver resolver = this.provider.getJavaScriptEngine().getJavaScriptResolver(this.provider);
        TypeDeclaration dec = resolver.resolveNode(node);
        
        boolean isNumber = ("JSNumber".equals(dec.getAPITypeName()) || "JSBoolean".equals(dec.getAPITypeName()));
        if (isNumber && (this.type == null || (isNumber && "JSNumber".equals(this.type)))) {
          this.type = "JSNumber";
        } else {
          
          this.type = "JSString";
        } 
      } 

      
      return true;
    }
  }







  
  private static TypeDeclaration getTypeFromInFixExpression(AstNode node, SourceCompletionProvider provider) {
    InfixVisitor visitor;
    InfixExpression infix = (InfixExpression)node;
    switch (infix.getType()) {

      
      case 21:
      case 22:
      case 23:
      case 24:
      case 25:
        visitor = new InfixVisitor(provider);
        infix.visit(visitor);
        return getTypeDeclaration(visitor.type, provider);
    } 

    
    AstNode rightExp = infix.getRight();
    JavaScriptResolver resolver = provider.getJavaScriptEngine().getJavaScriptResolver(provider);
    TypeDeclaration dec = resolver.resolveNode(rightExp);
    return dec;
  }



  
  public static String convertNodeToSource(AstNode node) {
    try {
      return node.toSource();
    }
    catch (Exception e) {
      
      Logger.log(e.getMessage());
      
      return null;
    } 
  }







  
  public static int findIndexOfFirstOpeningBracket(String text) {
    int index = 0;
    if (text != null && text.length() > 0) {
      char[] chars = text.toCharArray();
      for (int i = chars.length - 1; i >= 0; i--) {
        switch (chars[i]) {
          case '(':
            index--;
            break;
          case ')':
            index++;
            break;
        } 
        if (index == -1) {
          return i + 1;
        }
      } 
    } else {
      return 0;
    } 
    return 0;
  }
  
  public static int findIndexOfFirstOpeningSquareBracket(String text) {
    int index = 0;
    if (text != null && text.length() > 0) {
      char[] chars = text.toCharArray();
      for (int i = chars.length - 1; i >= 0; i--) {
        switch (chars[i]) {
          case '[':
            index--;
            break;
          case ']':
            index++;
            break;
        } 
        if (index == -1) {
          return i + 1;
        }
      } 
    } else {
      return 0;
    } 
    return 0;
  }








  
  private static String findNewExpressionString(AstNode node) {
    NewExpression newEx = (NewExpression)node;
    AstNode target = newEx.getTarget();
    String source = target.toSource();
    int index = source.indexOf('(');
    if (index != -1) {
      source = source.substring(0, index);
    }
    return source;
  }








  
  public static TypeDeclaration getTypeDeclaration(String name, SourceCompletionProvider provider) {
    return provider.getTypesFactory().getTypeDeclaration(name);
  }

  
  public static int findLastIndexOfJavaScriptIdentifier(String input) {
    int index = -1;
    if (input != null) {
      char[] c = input.toCharArray();
      for (int i = 0; i < c.length; i++) {
        if (!Character.isJavaIdentifierPart(c[i])) {
          index = i;
        }
      } 
    } 
    return index;
  }





  
  public static String removeLastDotFromText(String text) {
    int trim = text.length();
    if (text.lastIndexOf('.') != -1) {
      trim = text.lastIndexOf('.');
    }
    
    String parseText = text.substring(0, trim);
    
    return parseText;
  }












  
  public static String trimFromLastParam(String text) {
    int trim = 0;
    if (text.lastIndexOf(',') != -1) {
      int i1 = 0;
      int i2 = 0;
      char[] chars = text.toCharArray();
      for (int i = chars.length - 1; i >= 0; i--) {
        switch (chars[i]) {
          case '(':
            i1--;
            break;
          case '[':
            i2--;
            break;
          case ')':
            i1++;
            break;
          case ']':
            i2++;
            break;
          case ',':
            if (i1 == 0 && i2 == 0) {
              return text.substring(i + 1, text.length()).trim();
            }
            break;
        } 

      
      } 
      trim = text.lastIndexOf(',') + 1;
    } 
    
    String parseText = text.substring(trim, text.length());
    
    return parseText.trim();
  }
  
  private static class ParseTextVisitor
    implements NodeVisitor
  {
    private AstNode lastNode;
    private String text;
    private boolean isNew;
    
    private ParseTextVisitor(String text) {
      this.text = text;
    }


    
    public boolean visit(AstNode node) {
      switch (node.getType()) {
        case 39:
        case 40:
        case 41:
        case 44:
        case 45:
        case 65:
        case 66:
          this.lastNode = node;
          break;
        case 30:
          this.isNew = true;
          break;
      } 
      return true;
    }

    
    public String getLastNodeSource() {
      return (this.lastNode != null) ? this.lastNode.toSource() : (this.isNew ? "" : this.text);
    }

    
    public boolean isNew() {
      return this.isNew;
    }
  }

  
  public static class ParseText
  {
    public String text = "";
    public boolean isNew;
  }
}
