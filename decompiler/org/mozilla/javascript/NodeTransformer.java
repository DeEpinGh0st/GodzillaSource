package org.mozilla.javascript;

import java.util.ArrayList;
import java.util.List;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Jump;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.ScriptNode;

















public class NodeTransformer
{
  private ObjArray loops;
  private ObjArray loopEnds;
  private boolean hasFinally;
  
  public final void transform(ScriptNode tree) {
    transformCompilationUnit(tree);
    for (int i = 0; i != tree.getFunctionCount(); i++) {
      FunctionNode fn = tree.getFunctionNode(i);
      transform((ScriptNode)fn);
    } 
  }

  
  private void transformCompilationUnit(ScriptNode tree) {
    this.loops = new ObjArray();
    this.loopEnds = new ObjArray();

    
    this.hasFinally = false;

    
    boolean createScopeObjects = (tree.getType() != 109 || ((FunctionNode)tree).requiresActivation());
    
    tree.flattenSymbolTable(!createScopeObjects);


    
    boolean inStrictMode = (tree instanceof AstRoot && ((AstRoot)tree).isInStrictMode());
    
    transformCompilationUnit_r(tree, (Node)tree, (Scope)tree, createScopeObjects, inStrictMode);
  }






  
  private void transformCompilationUnit_r(ScriptNode tree, Node parent, Scope scope, boolean createScopeObjects, boolean inStrictMode) {
    Node node = null; while (true) {
      Node leave; Jump jump1; boolean isGenerator; Jump jump; Node node1, result; Scope defining; Node child, nameSource, finallytarget, unwindBlock; Jump jumpStatement; Node cursor; String name; int i;
      Scope scope1;
      Node previous = null;
      if (node == null) {
        node = parent.getFirstChild();
      } else {
        previous = node;
        node = node.getNext();
      } 
      if (node == null) {
        break;
      }
      
      int type = node.getType();
      if (createScopeObjects && (type == 129 || type == 132 || type == 157) && node instanceof Scope) {



        
        Scope newScope = (Scope)node;
        if (newScope.getSymbolTable() != null) {

          
          Node let = new Node((type == 157) ? 158 : 153);
          
          Node innerLet = new Node(153);
          let.addChildToBack(innerLet);
          for (String str : newScope.getSymbolTable().keySet()) {
            innerLet.addChildToBack(Node.newString(39, str));
          }
          newScope.setSymbolTable(null);
          Node oldNode = node;
          node = replaceCurrent(parent, previous, node, let);
          type = node.getType();
          let.addChildToBack(oldNode);
        } 
      } 
      
      switch (type) {
        
        case 114:
        case 130:
        case 132:
          this.loops.push(node);
          this.loopEnds.push(((Jump)node).target);
          break;

        
        case 123:
          this.loops.push(node);
          leave = node.getNext();
          if (leave.getType() != 3) {
            Kit.codeBug();
          }
          this.loopEnds.push(leave);
          break;


        
        case 81:
          jump1 = (Jump)node;
          finallytarget = jump1.getFinally();
          if (finallytarget != null) {
            this.hasFinally = true;
            this.loops.push(node);
            this.loopEnds.push(finallytarget);
          } 
          break;

        
        case 3:
        case 131:
          if (!this.loopEnds.isEmpty() && this.loopEnds.peek() == node) {
            this.loopEnds.pop();
            this.loops.pop();
          } 
          break;
        
        case 72:
          ((FunctionNode)tree).addResumptionPoint(node);
          break;

        
        case 4:
          isGenerator = (tree.getType() == 109 && ((FunctionNode)tree).isGenerator());
          
          if (isGenerator) {
            node.putIntProp(20, 1);
          }






          
          if (!this.hasFinally)
            break; 
          unwindBlock = null;
          for (i = this.loops.size() - 1; i >= 0; i--) {
            Node n = (Node)this.loops.get(i);
            int elemtype = n.getType();
            if (elemtype == 81 || elemtype == 123) {
              Node unwind;
              if (elemtype == 81) {
                Jump jsrnode = new Jump(135);
                Node jsrtarget = ((Jump)n).getFinally();
                jsrnode.target = jsrtarget;
                Jump jump2 = jsrnode;
              } else {
                unwind = new Node(3);
              } 
              if (unwindBlock == null) {
                unwindBlock = new Node(129, node.getLineno());
              }
              
              unwindBlock.addChildToBack(unwind);
            } 
          } 
          if (unwindBlock != null) {
            Node returnNode = node;
            Node returnExpr = returnNode.getFirstChild();
            node = replaceCurrent(parent, previous, node, unwindBlock);
            if (returnExpr == null || isGenerator) {
              unwindBlock.addChildToBack(returnNode); continue;
            } 
            Node store = new Node(134, returnExpr);
            unwindBlock.addChildToFront(store);
            returnNode = new Node(64);
            unwindBlock.addChildToBack(returnNode);
            
            transformCompilationUnit_r(tree, store, scope, createScopeObjects, inStrictMode);
            continue;
          } 
          break;






        
        case 120:
        case 121:
          jump = (Jump)node;
          jumpStatement = jump.getJumpStatement();
          if (jumpStatement == null) Kit.codeBug();
          
          i = this.loops.size(); while (true) {
            if (i == 0)
            {

              
              throw Kit.codeBug();
            }
            i--;
            Node n = (Node)this.loops.get(i);
            if (n == jumpStatement) {
              break;
            }
            
            int elemtype = n.getType();
            if (elemtype == 123) {
              Node node2 = new Node(3);
              previous = addBeforeCurrent(parent, previous, node, node2); continue;
            } 
            if (elemtype == 81) {
              Jump tryNode = (Jump)n;
              Jump jsrFinally = new Jump(135);
              jsrFinally.target = tryNode.getFinally();
              previous = addBeforeCurrent(parent, previous, node, (Node)jsrFinally);
            } 
          } 

          
          if (type == 120) {
            jump.target = jumpStatement.target;
          } else {
            jump.target = jumpStatement.getContinue();
          } 
          jump.setType(5);
          break;


        
        case 38:
          visitCall(node, tree);
          break;
        
        case 30:
          visitNew(node, tree);
          break;
        
        case 153:
        case 158:
          node1 = node.getFirstChild();
          if (node1.getType() == 153) {

            
            boolean createWith = (tree.getType() != 109 || ((FunctionNode)tree).requiresActivation());
            
            node = visitLet(createWith, parent, previous, node);
            break;
          } 




        
        case 122:
        case 154:
          result = new Node(129);
          for (cursor = node.getFirstChild(); cursor != null; ) {

            
            Node n = cursor;
            cursor = cursor.getNext();
            if (n.getType() == 39) {
              if (!n.hasChildren())
                continue; 
              Node init = n.getFirstChild();
              n.removeChild(init);
              n.setType(49);
              n = new Node((type == 154) ? 155 : 8, n, init);




            
            }
            else if (n.getType() != 158) {
              throw Kit.codeBug();
            } 
            Node pop = new Node(133, n, node.getLineno());
            result.addChildToBack(pop);
          } 
          node = replaceCurrent(parent, previous, node, result);
          break;

        
        case 137:
          defining = scope.getDefiningScope(node.getString());
          if (defining != null) {
            node.setScope(defining);
          }
          break;





        
        case 7:
        case 32:
          child = node.getFirstChild();
          if (type == 7) {
            while (child.getType() == 26) {
              child = child.getFirstChild();
            }
            if (child.getType() == 12 || child.getType() == 13) {

              
              Node first = child.getFirstChild();
              Node last = child.getLastChild();
              if (first.getType() == 39 && first.getString().equals("undefined")) {
                
                child = last;
              } else if (last.getType() == 39 && last.getString().equals("undefined")) {
                
                child = first;
              } 
            } 
          }  if (child.getType() == 33) {
            child.setType(34);
          }
          break;
        
        case 8:
          if (inStrictMode) {
            node.setType(73);
          }


        
        case 31:
        case 39:
        case 155:
          if (createScopeObjects) {
            break;
          }
          
          if (type == 39) {
            nameSource = node;
          } else {
            nameSource = node.getFirstChild();
            if (nameSource.getType() != 49) {
              if (type == 31) {
                break;
              }
              throw Kit.codeBug();
            } 
          } 
          if (nameSource.getScope() != null) {
            break;
          }
          name = nameSource.getString();
          scope1 = scope.getDefiningScope(name);
          if (scope1 != null) {
            nameSource.setScope(scope1);
            if (type == 39) {
              node.setType(55); break;
            }  if (type == 8 || type == 73) {
              
              node.setType(56);
              nameSource.setType(41); break;
            }  if (type == 155) {
              node.setType(156);
              nameSource.setType(41); break;
            }  if (type == 31) {
              
              Node n = new Node(44);
              node = replaceCurrent(parent, previous, node, n); break;
            } 
            throw Kit.codeBug();
          } 
          break;
      } 


      
      transformCompilationUnit_r(tree, node, (node instanceof Scope) ? (Scope)node : scope, createScopeObjects, inStrictMode);
    } 
  }


  
  protected void visitNew(Node node, ScriptNode tree) {}


  
  protected void visitCall(Node node, ScriptNode tree) {}


  
  protected Node visitLet(boolean createWith, Node parent, Node previous, Node scopeNode) {
    Node result, vars = scopeNode.getFirstChild();
    Node body = vars.getNext();
    scopeNode.removeChild(vars);
    scopeNode.removeChild(body);
    boolean isExpression = (scopeNode.getType() == 158);

    
    if (createWith) {
      result = new Node(isExpression ? 159 : 129);
      result = replaceCurrent(parent, previous, scopeNode, result);
      ArrayList<Object> list = new ArrayList();
      Node objectLiteral = new Node(66);
      for (Node v = vars.getFirstChild(); v != null; v = v.getNext()) {
        Node current = v;
        if (current.getType() == 158) {
          
          List<?> destructuringNames = (List)current.getProp(22);
          
          Node c = current.getFirstChild();
          if (c.getType() != 153) throw Kit.codeBug();
          
          if (isExpression) {
            body = new Node(89, c.getNext(), body);
          } else {
            body = new Node(129, new Node(133, c.getNext()), body);
          } 



          
          if (destructuringNames != null) {
            list.addAll(destructuringNames);
            for (int i = 0; i < destructuringNames.size(); i++) {
              objectLiteral.addChildToBack(new Node(126, Node.newNumber(0.0D)));
            }
          } 
          
          current = c.getFirstChild();
        } 
        if (current.getType() != 39) throw Kit.codeBug(); 
        list.add(ScriptRuntime.getIndexObject(current.getString()));
        Node init = current.getFirstChild();
        if (init == null) {
          init = new Node(126, Node.newNumber(0.0D));
        }
        objectLiteral.addChildToBack(init);
      } 
      objectLiteral.putProp(12, list.toArray());
      Node newVars = new Node(2, objectLiteral);
      result.addChildToBack(newVars);
      result.addChildToBack(new Node(123, body));
      result.addChildToBack(new Node(3));
    } else {
      result = new Node(isExpression ? 89 : 129);
      result = replaceCurrent(parent, previous, scopeNode, result);
      Node newVars = new Node(89);
      for (Node v = vars.getFirstChild(); v != null; v = v.getNext()) {
        Node current = v;
        if (current.getType() == 158) {
          
          Node c = current.getFirstChild();
          if (c.getType() != 153) throw Kit.codeBug();
          
          if (isExpression) {
            body = new Node(89, c.getNext(), body);
          } else {
            body = new Node(129, new Node(133, c.getNext()), body);
          } 


          
          Scope.joinScopes((Scope)current, (Scope)scopeNode);
          
          current = c.getFirstChild();
        } 
        if (current.getType() != 39) throw Kit.codeBug(); 
        Node stringNode = Node.newString(current.getString());
        stringNode.setScope((Scope)scopeNode);
        Node init = current.getFirstChild();
        if (init == null) {
          init = new Node(126, Node.newNumber(0.0D));
        }
        newVars.addChildToBack(new Node(56, stringNode, init));
      } 
      if (isExpression) {
        result.addChildToBack(newVars);
        scopeNode.setType(89);
        result.addChildToBack(scopeNode);
        scopeNode.addChildToBack(body);
        if (body instanceof Scope) {
          Scope scopeParent = ((Scope)body).getParentScope();
          ((Scope)body).setParentScope((Scope)scopeNode);
          ((Scope)scopeNode).setParentScope(scopeParent);
        } 
      } else {
        result.addChildToBack(new Node(133, newVars));
        scopeNode.setType(129);
        result.addChildToBack(scopeNode);
        scopeNode.addChildrenToBack(body);
        if (body instanceof Scope) {
          Scope scopeParent = ((Scope)body).getParentScope();
          ((Scope)body).setParentScope((Scope)scopeNode);
          ((Scope)scopeNode).setParentScope(scopeParent);
        } 
      } 
    } 
    return result;
  }


  
  private static Node addBeforeCurrent(Node parent, Node previous, Node current, Node toAdd) {
    if (previous == null) {
      if (current != parent.getFirstChild()) Kit.codeBug(); 
      parent.addChildToFront(toAdd);
    } else {
      if (current != previous.getNext()) Kit.codeBug(); 
      parent.addChildAfter(toAdd, previous);
    } 
    return toAdd;
  }


  
  private static Node replaceCurrent(Node parent, Node previous, Node current, Node replacement) {
    if (previous == null) {
      if (current != parent.getFirstChild()) Kit.codeBug(); 
      parent.replaceChild(current, replacement);
    } else if (previous.next == current) {

      
      parent.replaceChildAfter(previous, replacement);
    } else {
      parent.replaceChild(current, replacement);
    } 
    return replacement;
  }
}
