package org.fife.rsta.ac.java.tree;

import javax.swing.Icon;
import org.fife.rsta.ac.java.DecoratableIcon;
import org.fife.rsta.ac.java.IconFactory;
import org.fife.rsta.ac.java.rjc.ast.ASTNode;
import org.fife.rsta.ac.java.rjc.ast.CodeBlock;
import org.fife.rsta.ac.java.rjc.ast.Field;
import org.fife.rsta.ac.java.rjc.ast.FormalParameter;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.java.rjc.lang.Modifiers;
import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.ui.autocomplete.Util;


















class MemberTreeNode
  extends JavaTreeNode
{
  private String text;
  
  public MemberTreeNode(CodeBlock cb) {
    super((ASTNode)cb);
    this.text = "<html>" + cb.getName();
    IconFactory fact = IconFactory.get();
    Icon base = fact.getIcon("methodPrivateIcon");
    DecoratableIcon di = new DecoratableIcon(base);
    int priority = 3;
    if (cb.isStatic()) {
      di.addDecorationIcon(fact.getIcon("staticIcon"));
      priority -= 16;
    } 
    setIcon((Icon)di);
    setSortPriority(priority);
  }


  
  public MemberTreeNode(Field field) {
    super((ASTNode)field);
    String icon;
    Modifiers mods = field.getModifiers();

    
    if (mods == null) {
      icon = "fieldDefaultIcon";
    }
    else if (mods.isPrivate()) {
      icon = "fieldPrivateIcon";
    }
    else if (mods.isProtected()) {
      icon = "fieldProtectedIcon";
    }
    else if (mods.isPublic()) {
      icon = "fieldPublicIcon";
    } else {
      
      icon = "fieldDefaultIcon";
    } 
    
    StringBuilder sb = new StringBuilder();
    sb.append("<html>");
    sb.append(field.getName());
    sb.append(" : ");
    sb.append("<font color='#888888'>");
    
    appendType(field.getType(), sb);
    this.text = sb.toString();
    int priority = 1;
    
    IconFactory fact = IconFactory.get();
    Icon base = fact.getIcon(icon);
    DecoratableIcon di = new DecoratableIcon(base);
    di.setDeprecated(field.isDeprecated());
    if (mods != null) {
      if (mods.isStatic()) {
        di.addDecorationIcon(fact.getIcon("staticIcon"));
        priority -= 16;
      } 
      if (mods.isFinal()) {
        di.addDecorationIcon(fact.getIcon("finalIcon"));
      }
    } 
    setIcon((Icon)di);
    
    setSortPriority(priority);
  }



  
  public MemberTreeNode(Method method) {
    super((ASTNode)method);
    
    String icon;
    int priority = 3;
    
    Modifiers mods = method.getModifiers();
    if (mods == null) {
      icon = "methodDefaultIcon";
    }
    else if (mods.isPrivate()) {
      icon = "methodPrivateIcon";
    }
    else if (mods.isProtected()) {
      icon = "methodProtectedIcon";
    }
    else if (mods.isPublic()) {
      icon = "methodPublicIcon";
    } else {
      
      icon = "methodDefaultIcon";
    } 
    StringBuilder sb = new StringBuilder();
    sb.append("<html>");
    sb.append(method.getName());
    sb.append('(');
    int paramCount = method.getParameterCount();
    for (int i = 0; i < paramCount; i++) {
      FormalParameter param = method.getParameter(i);
      appendType(param.getType(), sb);
      if (i < paramCount - 1) {
        sb.append(", ");
      }
    } 
    sb.append(')');
    if (method.getType() != null) {
      sb.append(" : ");
      sb.append("<font color='#888888'>");
      appendType(method.getType(), sb);
    } 
    
    this.text = sb.toString();
    
    IconFactory fact = IconFactory.get();
    Icon base = fact.getIcon(icon);
    DecoratableIcon di = new DecoratableIcon(base);
    di.setDeprecated(method.isDeprecated());
    if (mods != null) {
      if (mods.isAbstract()) {
        di.addDecorationIcon(fact.getIcon("abstractIcon"));
      }
      if (method.isConstructor()) {
        di.addDecorationIcon(fact.getIcon("constructorIcon"));
        priority = 2;
      } 
      if (mods.isStatic()) {
        di.addDecorationIcon(fact.getIcon("staticIcon"));
        priority -= 16;
      } 
      if (mods.isFinal()) {
        di.addDecorationIcon(fact.getIcon("finalIcon"));
      }
    } 
    setIcon((Icon)di);
    
    setSortPriority(priority);
  }


  
  static void appendType(Type type, StringBuilder sb) {
    if (type != null) {
      String t = type.toString();
      t = t.replaceAll("<", "&lt;");
      t = t.replaceAll(">", "&gt;");
      sb.append(t);
    } 
  }



  
  public String getText(boolean selected) {
    return selected ? Util.stripHtml(this.text)
      .replaceAll("&lt;", "<").replaceAll("&gt;", ">") : this.text;
  }
}
