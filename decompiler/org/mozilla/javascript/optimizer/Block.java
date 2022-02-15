package org.mozilla.javascript.optimizer;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.ObjArray;
import org.mozilla.javascript.ObjToIntMap;
import org.mozilla.javascript.ast.Jump;

class Block {
  private Block[] itsSuccessors;
  private Block[] itsPredecessors;
  private int itsStartNodeIndex;
  private int itsEndNodeIndex;
  private int itsBlockID;
  private BitSet itsLiveOnEntrySet;
  private BitSet itsLiveOnExitSet;
  private BitSet itsUseBeforeDefSet;
  private BitSet itsNotDefSet;
  static final boolean DEBUG = false;
  private static int debug_blockCount;
  
  private static class FatBlock {
    private static Block[] reduceToArray(ObjToIntMap map) {
      Block[] result = null;
      if (!map.isEmpty()) {
        result = new Block[map.size()];
        int i = 0;
        ObjToIntMap.Iterator iter = map.newIterator();
        iter.start(); for (; !iter.done(); iter.next()) {
          FatBlock fb = (FatBlock)iter.getKey();
          result[i++] = fb.realBlock;
        } 
      } 
      return result;
    }
    
    void addSuccessor(FatBlock b) { this.successors.put(b, 0); } void addPredecessor(FatBlock b) {
      this.predecessors.put(b, 0);
    }
    Block[] getSuccessors() { return reduceToArray(this.successors); } Block[] getPredecessors() {
      return reduceToArray(this.predecessors);
    }
    
    private ObjToIntMap successors = new ObjToIntMap();
    
    private ObjToIntMap predecessors = new ObjToIntMap();
    Block realBlock;
    
    private FatBlock() {}
  }
  
  Block(int startNodeIndex, int endNodeIndex) {
    this.itsStartNodeIndex = startNodeIndex;
    this.itsEndNodeIndex = endNodeIndex;
  }

  
  static void runFlowAnalyzes(OptFunctionNode fn, Node[] statementNodes) {
    int paramCount = fn.fnode.getParamCount();
    int varCount = fn.fnode.getParamAndVarCount();
    int[] varTypes = new int[varCount];
    int i;
    for (i = 0; i != paramCount; i++) {
      varTypes[i] = 3;
    }

    
    for (i = paramCount; i != varCount; i++) {
      varTypes[i] = 0;
    }
    
    Block[] theBlocks = buildBlocks(statementNodes);







    
    reachingDefDataFlow(fn, statementNodes, theBlocks, varTypes);
    typeFlow(fn, statementNodes, theBlocks, varTypes);











    
    for (int j = paramCount; j != varCount; j++) {
      if (varTypes[j] == 1) {
        fn.setIsNumberVar(j);
      }
    } 
  }



  
  private static Block[] buildBlocks(Node[] statementNodes) {
    Map<Node, FatBlock> theTargetBlocks = new HashMap<Node, FatBlock>();
    ObjArray theBlocks = new ObjArray();

    
    int beginNodeIndex = 0;
    int i;
    for (i = 0; i < statementNodes.length; i++) {
      FatBlock fb; switch (statementNodes[i].getType()) {
        
        case 131:
          if (i != beginNodeIndex) {
            FatBlock fatBlock = newFatBlock(beginNodeIndex, i - 1);
            if (statementNodes[beginNodeIndex].getType() == 131) {
              theTargetBlocks.put(statementNodes[beginNodeIndex], fatBlock);
            }
            theBlocks.add(fatBlock);
            
            beginNodeIndex = i;
          } 
          break;

        
        case 5:
        case 6:
        case 7:
          fb = newFatBlock(beginNodeIndex, i);
          if (statementNodes[beginNodeIndex].getType() == 131) {
            theTargetBlocks.put(statementNodes[beginNodeIndex], fb);
          }
          theBlocks.add(fb);
          
          beginNodeIndex = i + 1;
          break;
      } 

    
    } 
    if (beginNodeIndex != statementNodes.length) {
      FatBlock fb = newFatBlock(beginNodeIndex, statementNodes.length - 1);
      if (statementNodes[beginNodeIndex].getType() == 131) {
        theTargetBlocks.put(statementNodes[beginNodeIndex], fb);
      }
      theBlocks.add(fb);
    } 


    
    for (i = 0; i < theBlocks.size(); i++) {
      FatBlock fb = (FatBlock)theBlocks.get(i);
      
      Node blockEndNode = statementNodes[fb.realBlock.itsEndNodeIndex];
      int blockEndNodeType = blockEndNode.getType();
      
      if (blockEndNodeType != 5 && i < theBlocks.size() - 1) {
        FatBlock fallThruTarget = (FatBlock)theBlocks.get(i + 1);
        fb.addSuccessor(fallThruTarget);
        fallThruTarget.addPredecessor(fb);
      } 

      
      if (blockEndNodeType == 7 || blockEndNodeType == 6 || blockEndNodeType == 5) {

        
        Node target = ((Jump)blockEndNode).target;
        FatBlock branchTargetBlock = theTargetBlocks.get(target);
        target.putProp(6, branchTargetBlock.realBlock);
        fb.addSuccessor(branchTargetBlock);
        branchTargetBlock.addPredecessor(fb);
      } 
    } 
    
    Block[] result = new Block[theBlocks.size()];
    
    for (int j = 0; j < theBlocks.size(); j++) {
      FatBlock fb = (FatBlock)theBlocks.get(j);
      Block b = fb.realBlock;
      b.itsSuccessors = fb.getSuccessors();
      b.itsPredecessors = fb.getPredecessors();
      b.itsBlockID = j;
      result[j] = b;
    } 
    
    return result;
  }

  
  private static FatBlock newFatBlock(int startNodeIndex, int endNodeIndex) {
    FatBlock fb = new FatBlock();
    fb.realBlock = new Block(startNodeIndex, endNodeIndex);
    return fb;
  }

  
  private static String toString(Block[] blockList, Node[] statementNodes) {
    return null;
  }









































  
  private static void reachingDefDataFlow(OptFunctionNode fn, Node[] statementNodes, Block[] theBlocks, int[] varTypes) {
    for (int i = 0; i < theBlocks.length; i++) {
      theBlocks[i].initLiveOnEntrySets(fn, statementNodes);
    }




    
    boolean[] visit = new boolean[theBlocks.length];
    boolean[] doneOnce = new boolean[theBlocks.length];
    int vIndex = theBlocks.length - 1;
    boolean needRescan = false;
    visit[vIndex] = true; while (true) {
      int j;
      if (visit[vIndex] || !doneOnce[vIndex]) {
        doneOnce[vIndex] = true;
        visit[vIndex] = false;
        if (theBlocks[vIndex].doReachedUseDataFlow()) {
          Block[] pred = (theBlocks[vIndex]).itsPredecessors;
          if (pred != null) {
            for (int k = 0; k < pred.length; k++) {
              int index = (pred[k]).itsBlockID;
              visit[index] = true;
              j = needRescan | ((index > vIndex) ? 1 : 0);
            } 
          }
        } 
      } 
      if (vIndex == 0) {
        if (j != 0) {
          vIndex = theBlocks.length - 1;
          j = 0;
          continue;
        } 
        break;
      } 
      vIndex--;
    } 






    
    theBlocks[0].markAnyTypeVariables(varTypes);
  }


  
  private static void typeFlow(OptFunctionNode fn, Node[] statementNodes, Block[] theBlocks, int[] varTypes) {
    boolean[] visit = new boolean[theBlocks.length];
    boolean[] doneOnce = new boolean[theBlocks.length];
    int vIndex = 0;
    boolean needRescan = false;
    visit[vIndex] = true; while (true) {
      int i;
      if (visit[vIndex] || !doneOnce[vIndex]) {
        doneOnce[vIndex] = true;
        visit[vIndex] = false;
        if (theBlocks[vIndex].doTypeFlow(fn, statementNodes, varTypes)) {
          
          Block[] succ = (theBlocks[vIndex]).itsSuccessors;
          if (succ != null) {
            for (int j = 0; j < succ.length; j++) {
              int index = (succ[j]).itsBlockID;
              visit[index] = true;
              i = needRescan | ((index < vIndex) ? 1 : 0);
            } 
          }
        } 
      } 
      if (vIndex == theBlocks.length - 1) {
        if (i != 0) {
          vIndex = 0;
          i = 0;
          continue;
        } 
        break;
      } 
      vIndex++;
    } 
  }


  
  private static boolean assignType(int[] varTypes, int index, int type) {
    int prev = varTypes[index];
    varTypes[index] = varTypes[index] | type; return (prev != (varTypes[index] | type));
  }

  
  private void markAnyTypeVariables(int[] varTypes) {
    for (int i = 0; i != varTypes.length; i++) {
      if (this.itsLiveOnEntrySet.get(i)) {
        assignType(varTypes, i, 3);
      }
    } 
  }






  
  private void lookForVariableAccess(OptFunctionNode fn, Node n) {
    int i;
    Node node1, lhs;
    int varIndex;
    Node rhs;
    switch (n.getType()) {


      
      case 137:
        i = fn.fnode.getIndexForNameNode(n);
        if (i > -1 && !this.itsNotDefSet.get(i)) {
          this.itsUseBeforeDefSet.set(i);
        }
        return;
      
      case 106:
      case 107:
        node1 = n.getFirstChild();
        if (node1.getType() == 55) {
          int j = fn.getVarIndex(node1);
          if (!this.itsNotDefSet.get(j))
            this.itsUseBeforeDefSet.set(j); 
          this.itsNotDefSet.set(j);
        } else {
          lookForVariableAccess(fn, node1);
        } 
        return;

      
      case 56:
      case 156:
        lhs = n.getFirstChild();
        rhs = lhs.getNext();
        lookForVariableAccess(fn, rhs);
        this.itsNotDefSet.set(fn.getVarIndex(n));
        return;

      
      case 55:
        varIndex = fn.getVarIndex(n);
        if (!this.itsNotDefSet.get(varIndex)) {
          this.itsUseBeforeDefSet.set(varIndex);
        }
        return;
    } 
    Node child = n.getFirstChild();
    while (child != null) {
      lookForVariableAccess(fn, child);
      child = child.getNext();
    } 
  }








  
  private void initLiveOnEntrySets(OptFunctionNode fn, Node[] statementNodes) {
    int listLength = fn.getVarCount();
    this.itsUseBeforeDefSet = new BitSet(listLength);
    this.itsNotDefSet = new BitSet(listLength);
    this.itsLiveOnEntrySet = new BitSet(listLength);
    this.itsLiveOnExitSet = new BitSet(listLength);
    for (int i = this.itsStartNodeIndex; i <= this.itsEndNodeIndex; i++) {
      Node n = statementNodes[i];
      lookForVariableAccess(fn, n);
    } 
    this.itsNotDefSet.flip(0, listLength);
  }







  
  private boolean doReachedUseDataFlow() {
    this.itsLiveOnExitSet.clear();
    if (this.itsSuccessors != null) {
      for (int i = 0; i < this.itsSuccessors.length; i++) {
        this.itsLiveOnExitSet.or((this.itsSuccessors[i]).itsLiveOnEntrySet);
      }
    }
    return updateEntrySet(this.itsLiveOnEntrySet, this.itsLiveOnExitSet, this.itsUseBeforeDefSet, this.itsNotDefSet);
  }


  
  private boolean updateEntrySet(BitSet entrySet, BitSet exitSet, BitSet useBeforeDef, BitSet notDef) {
    int card = entrySet.cardinality();
    entrySet.or(exitSet);
    entrySet.and(notDef);
    entrySet.or(useBeforeDef);
    return (entrySet.cardinality() != card);
  } private static int findExpressionType(OptFunctionNode fn, Node n, int[] varTypes) {
    Node node1;
    Node ifTrue;
    Node child;
    int i;
    Node ifFalse;
    int lType;
    int j;
    int ifTrueType;
    int rType;
    int ifFalseType;
    switch (n.getType()) {
      case 40:
        return 1;
      
      case 30:
      case 38:
      case 70:
        return 3;
      
      case 33:
      case 36:
      case 39:
      case 43:
        return 3;
      
      case 55:
        return varTypes[fn.getVarIndex(n)];
      
      case 9:
      case 10:
      case 11:
      case 18:
      case 19:
      case 20:
      case 22:
      case 23:
      case 24:
      case 25:
      case 27:
      case 28:
      case 29:
      case 106:
      case 107:
        return 1;

      
      case 126:
        return 3;

      
      case 12:
      case 13:
      case 14:
      case 15:
      case 16:
      case 17:
      case 26:
      case 31:
      case 44:
      case 45:
      case 46:
      case 47:
      case 52:
      case 53:
      case 69:
        return 3;

      
      case 32:
      case 41:
      case 137:
        return 3;
      
      case 42:
      case 48:
      case 65:
      case 66:
      case 157:
        return 3;



      
      case 21:
        node1 = n.getFirstChild();
        i = findExpressionType(fn, node1, varTypes);
        j = findExpressionType(fn, node1.getNext(), varTypes);
        return i | j;

      
      case 102:
        ifTrue = n.getFirstChild().getNext();
        ifFalse = ifTrue.getNext();
        ifTrueType = findExpressionType(fn, ifTrue, varTypes);
        ifFalseType = findExpressionType(fn, ifFalse, varTypes);
        return ifTrueType | ifFalseType;

      
      case 8:
      case 35:
      case 37:
      case 56:
      case 89:
      case 156:
        return findExpressionType(fn, n.getLastChild(), varTypes);
      
      case 104:
      case 105:
        child = n.getFirstChild();
        lType = findExpressionType(fn, child, varTypes);
        rType = findExpressionType(fn, child.getNext(), varTypes);
        return lType | rType;
    } 

    
    return 3;
  }
  
  private static boolean findDefPoints(OptFunctionNode fn, Node n, int[] varTypes) {
    Node rValue;
    int theType, i;
    boolean result = false;
    Node first = n.getFirstChild();
    for (Node next = first; next != null; next = next.getNext()) {
      result |= findDefPoints(fn, next, varTypes);
    }
    switch (n.getType()) {
      case 106:
      case 107:
        if (first.getType() == 55) {
          
          int j = fn.getVarIndex(first);
          if (!fn.fnode.getParamAndVarConst()[j]) {
            result |= assignType(varTypes, j, 1);
          }
        } 
        break;
      case 56:
      case 156:
        rValue = first.getNext();
        theType = findExpressionType(fn, rValue, varTypes);
        i = fn.getVarIndex(n);
        if (n.getType() != 56 || !fn.fnode.getParamAndVarConst()[i])
        {
          result |= assignType(varTypes, i, theType);
        }
        break;
    } 
    
    return result;
  }


  
  private boolean doTypeFlow(OptFunctionNode fn, Node[] statementNodes, int[] varTypes) {
    boolean changed = false;
    
    for (int i = this.itsStartNodeIndex; i <= this.itsEndNodeIndex; i++) {
      Node n = statementNodes[i];
      if (n != null) {
        changed |= findDefPoints(fn, n, varTypes);
      }
    } 
    
    return changed;
  }
  
  private void printLiveOnEntrySet(OptFunctionNode fn) {}
}
