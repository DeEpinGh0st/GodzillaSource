package org.mozilla.javascript.optimizer;

import org.mozilla.javascript.Node;
import org.mozilla.javascript.ObjArray;
import org.mozilla.javascript.ast.ScriptNode;








class Optimizer
{
  static final int NoType = 0;
  static final int NumberType = 1;
  static final int AnyType = 3;
  private boolean inDirectCallFunction;
  OptFunctionNode theFunction;
  private boolean parameterUsedInNumberContext;
  
  void optimize(ScriptNode scriptOrFn) {
    int functionCount = scriptOrFn.getFunctionCount();
    for (int i = 0; i != functionCount; i++) {
      OptFunctionNode f = OptFunctionNode.get(scriptOrFn, i);
      optimizeFunction(f);
    } 
  }

  
  private void optimizeFunction(OptFunctionNode theFunction) {
    if (theFunction.fnode.requiresActivation())
      return; 
    this.inDirectCallFunction = theFunction.isTargetOfDirectCall();
    this.theFunction = theFunction;
    
    ObjArray statementsArray = new ObjArray();
    buildStatementList_r((Node)theFunction.fnode, statementsArray);
    Node[] theStatementNodes = new Node[statementsArray.size()];
    statementsArray.toArray((Object[])theStatementNodes);
    
    Block.runFlowAnalyzes(theFunction, theStatementNodes);
    
    if (!theFunction.fnode.requiresActivation()) {






      
      this.parameterUsedInNumberContext = false;
      for (Node theStatementNode : theStatementNodes) {
        rewriteForNumberVariables(theStatementNode, 1);
      }
      theFunction.setParameterNumberContext(this.parameterUsedInNumberContext);
    } 
  }


































  
  private void markDCPNumberContext(Node n) {
    if (this.inDirectCallFunction && n.getType() == 55) {
      int varIndex = this.theFunction.getVarIndex(n);
      if (this.theFunction.isParameter(varIndex)) {
        this.parameterUsedInNumberContext = true;
      }
    } 
  }

  
  private boolean convertParameter(Node n) {
    if (this.inDirectCallFunction && n.getType() == 55) {
      int varIndex = this.theFunction.getVarIndex(n);
      if (this.theFunction.isParameter(varIndex)) {
        n.removeProp(8);
        return true;
      } 
    } 
    return false; } private int rewriteForNumberVariables(Node n, int desired) { Node node2; int varIndex; Node node1, lChild, arrayBase, child; int type; Node rChild, arrayIndex;
    OptFunctionNode target;
    int rType, lType;
    Node rValue;
    int baseType, k, j, i, indexType, m, rValueType;
    switch (n.getType()) {
      case 133:
        node2 = n.getFirstChild();
        type = rewriteForNumberVariables(node2, 1);
        if (type == 1)
          n.putIntProp(8, 0); 
        return 0;
      
      case 40:
        n.putIntProp(8, 0);
        return 1;

      
      case 55:
        varIndex = this.theFunction.getVarIndex(n);
        if (this.inDirectCallFunction && this.theFunction.isParameter(varIndex) && desired == 1) {


          
          n.putIntProp(8, 0);
          return 1;
        } 
        if (this.theFunction.isNumberVar(varIndex)) {
          n.putIntProp(8, 0);
          return 1;
        } 
        return 0;

      
      case 106:
      case 107:
        node1 = n.getFirstChild();
        type = rewriteForNumberVariables(node1, 1);
        if (node1.getType() == 55) {
          if (type == 1 && !convertParameter(node1)) {
            
            n.putIntProp(8, 0);
            markDCPNumberContext(node1);
            return 1;
          } 
          return 0;
        } 
        if (node1.getType() == 36 || node1.getType() == 33)
        {
          return type;
        }
        return 0;
      
      case 56:
      case 156:
        lChild = n.getFirstChild();
        rChild = lChild.getNext();
        rType = rewriteForNumberVariables(rChild, 1);
        k = this.theFunction.getVarIndex(n);
        if (this.inDirectCallFunction && this.theFunction.isParameter(k)) {

          
          if (rType == 1) {
            if (!convertParameter(rChild)) {
              n.putIntProp(8, 0);
              return 1;
            } 
            markDCPNumberContext(rChild);
            return 0;
          } 
          
          return rType;
        } 
        if (this.theFunction.isNumberVar(k)) {
          if (rType != 1) {
            n.removeChild(rChild);
            n.addChildToBack(new Node(150, rChild));
          } 
          
          n.putIntProp(8, 0);
          markDCPNumberContext(rChild);
          return 1;
        } 
        
        if (rType == 1 && 
          !convertParameter(rChild)) {
          n.removeChild(rChild);
          n.addChildToBack(new Node(149, rChild));
        } 

        
        return 0;

      
      case 14:
      case 15:
      case 16:
      case 17:
        lChild = n.getFirstChild();
        rChild = lChild.getNext();
        lType = rewriteForNumberVariables(lChild, 1);
        j = rewriteForNumberVariables(rChild, 1);
        markDCPNumberContext(lChild);
        markDCPNumberContext(rChild);
        
        if (convertParameter(lChild)) {
          if (convertParameter(rChild))
            return 0; 
          if (j == 1) {
            n.putIntProp(8, 2);
          }
        }
        else if (convertParameter(rChild)) {
          if (lType == 1) {
            n.putIntProp(8, 1);
          
          }
        }
        else if (lType == 1) {
          if (j == 1) {
            n.putIntProp(8, 0);
          } else {
            
            n.putIntProp(8, 1);
          }
        
        }
        else if (j == 1) {
          n.putIntProp(8, 2);
        } 


        
        return 0;

      
      case 21:
        lChild = n.getFirstChild();
        rChild = lChild.getNext();
        lType = rewriteForNumberVariables(lChild, 1);
        j = rewriteForNumberVariables(rChild, 1);

        
        if (convertParameter(lChild)) {
          if (convertParameter(rChild)) {
            return 0;
          }
          
          if (j == 1) {
            n.putIntProp(8, 2);
          
          }
        
        }
        else if (convertParameter(rChild)) {
          if (lType == 1) {
            n.putIntProp(8, 1);
          
          }
        }
        else if (lType == 1) {
          if (j == 1) {
            n.putIntProp(8, 0);
            return 1;
          } 
          
          n.putIntProp(8, 1);

        
        }
        else if (j == 1) {
          n.putIntProp(8, 2);
        } 



        
        return 0;

      
      case 9:
      case 10:
      case 11:
      case 18:
      case 19:
      case 22:
      case 23:
      case 24:
      case 25:
        lChild = n.getFirstChild();
        rChild = lChild.getNext();
        lType = rewriteForNumberVariables(lChild, 1);
        j = rewriteForNumberVariables(rChild, 1);
        markDCPNumberContext(lChild);
        markDCPNumberContext(rChild);
        if (lType == 1) {
          if (j == 1) {
            n.putIntProp(8, 0);
            return 1;
          } 
          
          if (!convertParameter(rChild)) {
            n.removeChild(rChild);
            n.addChildToBack(new Node(150, rChild));
            
            n.putIntProp(8, 0);
          } 
          return 1;
        } 

        
        if (j == 1) {
          if (!convertParameter(lChild)) {
            n.removeChild(lChild);
            n.addChildToFront(new Node(150, lChild));
            
            n.putIntProp(8, 0);
          } 
          return 1;
        } 
        
        if (!convertParameter(lChild)) {
          n.removeChild(lChild);
          n.addChildToFront(new Node(150, lChild));
        } 
        
        if (!convertParameter(rChild)) {
          n.removeChild(rChild);
          n.addChildToBack(new Node(150, rChild));
        } 
        
        n.putIntProp(8, 0);
        return 1;


      
      case 37:
      case 140:
        arrayBase = n.getFirstChild();
        arrayIndex = arrayBase.getNext();
        rValue = arrayIndex.getNext();
        i = rewriteForNumberVariables(arrayBase, 1);
        if (i == 1 && 
          !convertParameter(arrayBase)) {
          n.removeChild(arrayBase);
          n.addChildToFront(new Node(149, arrayBase));
        } 

        
        m = rewriteForNumberVariables(arrayIndex, 1);
        if (m == 1 && 
          !convertParameter(arrayIndex))
        {

          
          n.putIntProp(8, 1);
        }
        
        rValueType = rewriteForNumberVariables(rValue, 1);
        if (rValueType == 1 && 
          !convertParameter(rValue)) {
          n.removeChild(rValue);
          n.addChildToBack(new Node(149, rValue));
        } 

        
        return 0;
      
      case 36:
        arrayBase = n.getFirstChild();
        arrayIndex = arrayBase.getNext();
        baseType = rewriteForNumberVariables(arrayBase, 1);
        if (baseType == 1 && 
          !convertParameter(arrayBase)) {
          n.removeChild(arrayBase);
          n.addChildToFront(new Node(149, arrayBase));
        } 

        
        indexType = rewriteForNumberVariables(arrayIndex, 1);
        if (indexType == 1 && 
          !convertParameter(arrayIndex))
        {

          
          n.putIntProp(8, 2);
        }
        
        return 0;

      
      case 38:
        child = n.getFirstChild();
        
        rewriteAsObjectChildren(child, child.getFirstChild());
        child = child.getNext();
        
        target = (OptFunctionNode)n.getProp(9);
        
        if (target != null) {



          
          while (child != null) {
            int i1 = rewriteForNumberVariables(child, 1);
            if (i1 == 1) {
              markDCPNumberContext(child);
            }
            child = child.getNext();
          } 
        } else {
          rewriteAsObjectChildren(n, child);
        } 
        return 0;
    } 
    
    rewriteAsObjectChildren(n, n.getFirstChild());
    return 0; }





  
  private void rewriteAsObjectChildren(Node n, Node child) {
    while (child != null) {
      Node nextChild = child.getNext();
      int type = rewriteForNumberVariables(child, 0);
      if (type == 1 && 
        !convertParameter(child)) {
        n.removeChild(child);
        Node nuChild = new Node(149, child);
        if (nextChild == null) {
          n.addChildToBack(nuChild);
        } else {
          n.addChildBefore(nuChild, nextChild);
        } 
      } 
      child = nextChild;
    } 
  }

  
  private static void buildStatementList_r(Node node, ObjArray statements) {
    int type = node.getType();
    if (type == 129 || type == 141 || type == 132 || type == 109) {



      
      Node child = node.getFirstChild();
      while (child != null) {
        buildStatementList_r(child, statements);
        child = child.getNext();
      } 
    } else {
      statements.add(node);
    } 
  }
}
