package org.fife.rsta.ac.js.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import javax.swing.tree.MutableTreeNode;
import org.fife.rsta.ac.js.IconFactory;
import org.fife.rsta.ac.js.util.RhinoUtil;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;





















class JavaScriptOutlineTreeGenerator
  implements NodeVisitor
{
  private JavaScriptTreeNode root;
  private RSyntaxTextArea textArea;
  private JavaScriptTreeNode curScopeTreeNode;
  private Map<String, List<JavaScriptTreeNode>> prototypeAdditions = null;


  
  JavaScriptOutlineTreeGenerator(RSyntaxTextArea textArea, AstRoot ast) {
    this.textArea = textArea;
    this.root = new JavaScriptTreeNode((AstNode)null);
    if (ast != null) {
      ast.visit(this);
    }
  }







  
  private void addPrototypeAdditionsToRoot() {
    if (this.prototypeAdditions != null) {
      
      this.root.refresh();
      
      for (Map.Entry<String, List<JavaScriptTreeNode>> entry : this.prototypeAdditions.entrySet()) {
        String clazz = entry.getKey();
        for (int i = 0; i < this.root.getChildCount(); i++) {
          JavaScriptTreeNode childNode = (JavaScriptTreeNode)this.root.getChildAt(i);
          String text = childNode.getText(true);
          if (text != null && text.startsWith(clazz + "(")) {
            for (JavaScriptTreeNode memberNode : entry.getValue()) {
              childNode.add((MutableTreeNode)memberNode);
            }
            childNode.setIcon(IconFactory.getIcon("default_class"));
            break;
          } 
        } 
      } 
    } 
  }


























  
  private JavaScriptTreeNode createTreeNode(AstNode node) {
    JavaScriptTreeNode tn = new JavaScriptTreeNode(node);
    try {
      int offs = node.getAbsolutePosition();
      tn.setOffset(this.textArea.getDocument().createPosition(offs));
    } catch (BadLocationException ble) {
      ble.printStackTrace();
    } 
    return tn;
  }








  
  private JavaScriptTreeNode createTreeNode(List<AstNode> nodes) {
    JavaScriptTreeNode tn = new JavaScriptTreeNode(nodes);
    try {
      int offs = ((AstNode)nodes.get(0)).getAbsolutePosition();
      tn.setOffset(this.textArea.getDocument().createPosition(offs));
    } catch (BadLocationException ble) {
      ble.printStackTrace();
    } 
    return tn;
  }

  
  private List<AstNode> getChainedPropertyGetNodes(PropertyGet pg) {
    List<AstNode> nodes = new ArrayList<>();
    getChainedPropertyGetNodesImpl(pg, nodes);
    return nodes;
  }

  
  private void getChainedPropertyGetNodesImpl(PropertyGet pg, List<AstNode> nodes) {
    if (pg.getLeft() instanceof PropertyGet) {
      getChainedPropertyGetNodesImpl((PropertyGet)pg.getLeft(), nodes);
    } else {
      
      nodes.add(pg.getLeft());
    } 
    nodes.add(pg.getRight());
  }

  
  public JavaScriptTreeNode getTreeRoot() {
    addPrototypeAdditionsToRoot();
    return this.root;
  }
  
  public boolean visit(AstNode node) {
    FunctionNode fn;
    VariableDeclaration varDec;
    ExpressionStatement exprStmt;
    if (node == null) {
      return false;
    }
    
    int nodeType = node.getType();
    switch (nodeType) {
      
      case 136:
        this.curScopeTreeNode = this.root;
        return true;
      
      case 109:
        fn = (FunctionNode)node;
        return visitFunction(fn);
      
      case 122:
        varDec = (VariableDeclaration)node;
        return visitVariableDeclaration(varDec);
      
      case 129:
        return true;
      
      case 134:
        exprStmt = (ExpressionStatement)node;
        return visitExpressionStatement(exprStmt);
    } 

    
    return false;
  }








  
  private boolean visitExpressionStatement(ExpressionStatement exprStmt) {
    AstNode expr = exprStmt.getExpression();

    
    if (expr instanceof Assignment) {
      
      Assignment assignment = (Assignment)expr;
      AstNode left = assignment.getLeft();

      
      if (left instanceof PropertyGet) {
        
        PropertyGet pg = (PropertyGet)left;
        
        List<AstNode> chainedPropertyGetNodes = getChainedPropertyGetNodes(pg);
        int count = chainedPropertyGetNodes.size();

        
        if (count >= 3 && 
          RhinoUtil.isPrototypeNameNode(chainedPropertyGetNodes.get(count - 2))) {
          
          String clazz = RhinoUtil.getPrototypeClazz(chainedPropertyGetNodes, count - 2);
          AstNode propNode = chainedPropertyGetNodes.get(count - 1);
          String member = ((Name)propNode).getIdentifier();
          
          JavaScriptTreeNode tn = createTreeNode(propNode);
          AstNode propertyValue = assignment.getRight();
          visitPrototypeMember(tn, clazz, member, propertyValue);


        
        }
        else if (RhinoUtil.isPrototypeNameNode(chainedPropertyGetNodes.get(count - 1))) {
          
          JavaScriptTreeNode tn = createTreeNode(chainedPropertyGetNodes);
          tn.setIcon(IconFactory.getIcon("local_variable"));
          tn.setSortPriority(2);
          this.curScopeTreeNode.add((MutableTreeNode)tn);
          
          String clazz = RhinoUtil.getPrototypeClazz(chainedPropertyGetNodes, count - 1);
          AstNode rhs = assignment.getRight();

          
          if (rhs instanceof ObjectLiteral) {
            tn.setText(clazz + "()");
            ObjectLiteral value = (ObjectLiteral)rhs;
            visitPrototypeMembers(value, clazz);

          
          }
          else if (rhs instanceof FunctionCall) {
            
            FunctionCall rhsFunc = (FunctionCall)rhs;
            AstNode target = rhsFunc.getTarget();
            if (target instanceof PropertyGet)
            {
              pg = (PropertyGet)target;
              if (RhinoUtil.isSimplePropertyGet(pg, "Object", "create")) {
                tn.setText(clazz + "()");
                List<AstNode> args = rhsFunc.getArguments();

                
                if (args.size() >= 2) {
                  AstNode arg2 = args.get(1);
                  if (arg2 instanceof ObjectLiteral) {
                    ObjectLiteral descriptorObjLit = (ObjectLiteral)arg2;
                    visitPropertyDescriptors(descriptorObjLit, clazz);
                  }
                
                } 
              } else {
                
                tn.setText(clazz + "(???)");
              }
            
            }
          
          }
          else {
            
            tn.setText(clazz + "(???)");
          
          }
        
        }
        else {
          
          JavaScriptTreeNode tn = createTreeNode(chainedPropertyGetNodes);
          tn.setIcon(IconFactory.getIcon("default_class"));
          tn.setSortPriority(1);

          
          String clazz = RhinoUtil.getPrototypeClazz(chainedPropertyGetNodes, count);
          AstNode rhs = assignment.getRight();

          
          if (rhs instanceof ObjectLiteral) {
            
            this.curScopeTreeNode.add((MutableTreeNode)tn);
            tn.setText(clazz + "()");
            
            ObjectLiteral value = (ObjectLiteral)rhs;
            List<ObjectProperty> properties = value.getElements();
            for (ObjectProperty property : properties)
            {
              AstNode propertyKey = property.getLeft();
              tn = createTreeNode(propertyKey);
              
              String memberName = RhinoUtil.getPropertyName(propertyKey);
              AstNode propertyValue = property.getRight();
              visitPrototypeMember(tn, clazz, memberName, propertyValue);

            
            }


          
          }
          else if (rhs instanceof FunctionCall) {
            
            FunctionCall rhsFunc = (FunctionCall)rhs;
            AstNode target = rhsFunc.getTarget();
            if (target instanceof PropertyGet) {
              
              pg = (PropertyGet)target;
              if (RhinoUtil.isSimplePropertyGet(pg, "Object", "create")) {
                this.curScopeTreeNode.add((MutableTreeNode)tn);
                tn.setText(clazz + "()");
                List<AstNode> args = rhsFunc.getArguments();

                
                if (args.size() >= 2) {
                  AstNode arg2 = args.get(1);
                  if (arg2 instanceof ObjectLiteral) {
                    ObjectLiteral descriptorObjLit = (ObjectLiteral)arg2;
                    visitPropertyDescriptors(descriptorObjLit, clazz);
                  }
                
                }
              
              } else if (RhinoUtil.isSimplePropertyGet(pg, "Object", "freeze")) {
                this.curScopeTreeNode.add((MutableTreeNode)tn);
                tn.setText(clazz + "()");
                List<AstNode> args = rhsFunc.getArguments();
                if (args.size() == 1) {
                  AstNode arg = args.get(0);
                  if (arg instanceof ObjectLiteral) {
                    tn.setText(clazz + "()");
                    ObjectLiteral value = (ObjectLiteral)arg;
                    visitPrototypeMembers(value, clazz);
                  }
                
                } 
              } 
            } else {
              
              tn.setText(clazz + "(???)");
            }
          
          }
          else if (rhs instanceof FunctionNode) {
            String text = clazz;
            this.curScopeTreeNode.add((MutableTreeNode)tn);
            tn.setText(text);
            
            this.curScopeTreeNode = tn;
            ((FunctionNode)rhs).getBody().visit(this);
            this.curScopeTreeNode = (JavaScriptTreeNode)this.curScopeTreeNode.getParent();
          }
          else {
            
            this.curScopeTreeNode.add((MutableTreeNode)tn);
            tn.setText(clazz + "(???)");
          } 
        } 
      } 
    } 



    
    return false;
  }















  
  private void visitPropertyDescriptors(ObjectLiteral descriptorObjLit, String clazz) {
    List<ObjectProperty> descriptors = descriptorObjLit.getElements();
    for (ObjectProperty prop : descriptors) {
      
      AstNode propertyKey = prop.getLeft();
      AstNode propertyValue = prop.getRight();

      
      if (propertyValue instanceof ObjectLiteral) {
        
        JavaScriptTreeNode tn = createTreeNode(propertyKey);
        
        String memberName = RhinoUtil.getPropertyName(propertyKey);
        visitPropertyDescriptor(tn, clazz, memberName, (ObjectLiteral)propertyValue);
      } 
    } 
  }




















  
  private void visitPropertyDescriptor(JavaScriptTreeNode tn, String clazz, String memberName, ObjectLiteral propDesc) {
    List<ObjectProperty> propDescProperties = propDesc.getElements();
    for (ObjectProperty propDescProperty : propDescProperties) {
      
      AstNode propertyKey = propDescProperty.getLeft();
      String propName = RhinoUtil.getPropertyName(propertyKey);
      if ("value".equals(propName)) {
        
        AstNode propertyValue = propDescProperty.getRight();
        boolean isFunction = propertyValue instanceof FunctionNode;
        String text = memberName;
        if (isFunction) {
          FunctionNode func = (FunctionNode)propertyValue;
          text = text + RhinoUtil.getFunctionArgsString(func);
          tn.setIcon(IconFactory.getIcon("methpub_obj"));
          tn.setSortPriority(1);
        } else {
          
          tn.setIcon(IconFactory.getIcon("field_public_obj"));
          tn.setSortPriority(2);
        } 
        
        tn.setText(text);
        if (this.prototypeAdditions == null) {
          this.prototypeAdditions = new HashMap<>();
        }
        List<JavaScriptTreeNode> list = this.prototypeAdditions.get(clazz);
        if (list == null) {
          list = new ArrayList<>();
          this.prototypeAdditions.put(clazz, list);
        } 
        
        list.add(tn);
        
        if (isFunction) {
          JavaScriptTreeNode prevScopeTreeNode = this.curScopeTreeNode;
          this.curScopeTreeNode = tn;
          FunctionNode func = (FunctionNode)propertyValue;
          func.getBody().visit(this);
          this.curScopeTreeNode = prevScopeTreeNode;
        } 
      } 
    } 
  }






  
  private void visitPrototypeMembers(ObjectLiteral objLiteral, String clazz) {
    List<ObjectProperty> properties = objLiteral.getElements();
    for (ObjectProperty property : properties) {
      
      AstNode propertyKey = property.getLeft();
      JavaScriptTreeNode tn = createTreeNode(propertyKey);
      
      String memberName = RhinoUtil.getPropertyName(propertyKey);
      AstNode propertyValue = property.getRight();
      visitPrototypeMember(tn, clazz, memberName, propertyValue);
    } 
  }














  
  private void visitPrototypeMember(JavaScriptTreeNode tn, String clazz, String memberName, AstNode memberValue) {
    boolean isFunction = memberValue instanceof FunctionNode;
    String text = memberName;
    if (isFunction) {
      FunctionNode func = (FunctionNode)memberValue;
      text = text + RhinoUtil.getFunctionArgsString(func);
      tn.setIcon(IconFactory.getIcon("methpub_obj"));
      tn.setSortPriority(1);
    } else {
      
      tn.setIcon(IconFactory.getIcon("field_public_obj"));
      tn.setSortPriority(2);
    } 
    
    tn.setText(text);
    if (this.prototypeAdditions == null) {
      this.prototypeAdditions = new HashMap<>();
    }
    List<JavaScriptTreeNode> list = this.prototypeAdditions.get(clazz);
    if (list == null) {
      list = new ArrayList<>();
      this.prototypeAdditions.put(clazz, list);
    } 
    
    list.add(tn);
    
    if (isFunction) {
      JavaScriptTreeNode prevScopeTreeNode = this.curScopeTreeNode;
      this.curScopeTreeNode = tn;
      FunctionNode func = (FunctionNode)memberValue;
      func.getBody().visit(this);
      this.curScopeTreeNode = prevScopeTreeNode;
    } 
  }












  
  private boolean visitFunction(FunctionNode fn) {
    Name funcName = fn.getFunctionName();


    
    if (funcName != null) {
      
      String text = fn.getName() + RhinoUtil.getFunctionArgsString(fn);
      
      JavaScriptTreeNode tn = createTreeNode((AstNode)funcName);
      tn.setText(text);
      tn.setIcon(IconFactory.getIcon("default_function"));
      tn.setSortPriority(1);
      
      this.curScopeTreeNode.add((MutableTreeNode)tn);
      
      this.curScopeTreeNode = tn;
      fn.getBody().visit(this);
      this.curScopeTreeNode = (JavaScriptTreeNode)this.curScopeTreeNode.getParent();
    } 


    
    return false;
  }











  
  private boolean visitVariableDeclaration(VariableDeclaration varDec) {
    List<VariableInitializer> vars = varDec.getVariables();
    for (VariableInitializer var : vars) {
      String varName;
      Name varNameNode = null;
      
      AstNode target = var.getTarget();
      switch (target.getType()) {
        case 39:
          varNameNode = (Name)target;
          
          varName = varNameNode.getIdentifier();
          break;
        default:
          System.out.println("... Unknown var target type: " + target.getClass());
          varName = "?";
          break;
      } 
      
      boolean isFunction = var.getInitializer() instanceof FunctionNode;
      JavaScriptTreeNode tn = createTreeNode((AstNode)varNameNode);
      if (isFunction) {
        
        FunctionNode func = (FunctionNode)var.getInitializer();
        tn.setText(varName + RhinoUtil.getFunctionArgsString(func));
        tn.setIcon(IconFactory.getIcon("default_class"));
        tn.setSortPriority(1);
        this.curScopeTreeNode.add((MutableTreeNode)tn);
        
        this.curScopeTreeNode = tn;
        func.getBody().visit(this);
        this.curScopeTreeNode = (JavaScriptTreeNode)this.curScopeTreeNode.getParent();
        
        continue;
      } 
      tn.setText(varName);
      tn.setIcon(IconFactory.getIcon("local_variable"));
      tn.setSortPriority(2);
      this.curScopeTreeNode.add((MutableTreeNode)tn);
    } 


    
    return false;
  }
}
