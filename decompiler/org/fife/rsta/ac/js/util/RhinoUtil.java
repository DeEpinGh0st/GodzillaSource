package org.fife.rsta.ac.js.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.StringLiteral;




































public class RhinoUtil
{
  public static String getFunctionArgsString(FunctionNode fn) {
    StringBuilder sb = new StringBuilder("(");
    int paramCount = fn.getParamCount();
    if (paramCount > 0) {
      List<AstNode> fnParams = fn.getParams();
      for (int i = 0; i < paramCount; i++) {
        String paramName;
        AstNode paramNode = fnParams.get(i);
        switch (paramNode.getType()) {
          case 39:
            paramName = ((Name)paramNode).getIdentifier();
            break;
          default:
            System.out.println("Unhandled class for param: " + paramNode
                .getClass());
            paramName = "?";
            break;
        } 
        sb.append(paramName);
        if (i < paramCount - 1) {
          sb.append(", ");
        }
      } 
    } 
    sb.append(')');
    return sb.toString();
  }











  
  public static String getPropertyName(AstNode propKeyNode) {
    return (propKeyNode instanceof Name) ? ((Name)propKeyNode)
      .getIdentifier() : ((StringLiteral)propKeyNode)
      .getValue();
  }

  
  public static String getPrototypeClazz(List<AstNode> nodes) {
    return getPrototypeClazz(nodes, -1);
  }


  
  public static String getPrototypeClazz(List<AstNode> nodes, int depth) {
    if (depth < 0) {
      depth = nodes.size();
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < depth; i++) {

      
      sb.append(((AstNode)nodes.get(i)).toSource());
      if (i < depth - 1) {
        sb.append('.');
      }
    } 
    return sb.toString();
  }










  
  private static boolean isName(AstNode node, String value) {
    return (node instanceof Name && value.equals(((Name)node).getIdentifier()));
  }

  
  public static boolean isPrototypeNameNode(AstNode node) {
    return (node instanceof Name && "prototype"
      .equals(((Name)node).getIdentifier()));
  }

  
  public static boolean isPrototypePropertyGet(PropertyGet pg) {
    return (pg != null && pg.getLeft() instanceof Name && 
      isPrototypeNameNode(pg.getRight()));
  }











  
  public static boolean isSimplePropertyGet(PropertyGet pg, String expectedObj, String expectedField) {
    return (pg != null && isName(pg.getLeft(), expectedObj) && 
      isName(pg.getRight(), expectedField));
  }

  
  public static List<AstNode> toList(AstNode... nodes) {
    List<AstNode> list = new ArrayList<>();
    Collections.addAll(list, nodes);
    return list;
  }
}
