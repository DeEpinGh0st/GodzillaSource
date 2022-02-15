package org.fife.rsta.ac.java.tree;

import javax.swing.Icon;
import org.fife.rsta.ac.java.DecoratableIcon;
import org.fife.rsta.ac.java.IconFactory;
import org.fife.rsta.ac.java.rjc.ast.ASTNode;
import org.fife.rsta.ac.java.rjc.ast.EnumDeclaration;
import org.fife.rsta.ac.java.rjc.ast.NormalClassDeclaration;
import org.fife.rsta.ac.java.rjc.ast.NormalInterfaceDeclaration;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.rsta.ac.java.rjc.lang.Modifiers;



















class TypeDeclarationTreeNode
  extends JavaTreeNode
{
  public TypeDeclarationTreeNode(TypeDeclaration typeDec) {
    super((ASTNode)typeDec);
    
    String iconName = null;
    int priority = 0;
    
    if (typeDec instanceof NormalClassDeclaration) {
      NormalClassDeclaration ncd = (NormalClassDeclaration)typeDec;
      if (ncd.getModifiers() != null) {
        if (ncd.getModifiers().isPublic()) {
          iconName = "classIcon";
        }
        else if (ncd.getModifiers().isProtected()) {
          iconName = "innerClassProtectedIcon";
        }
        else if (ncd.getModifiers().isPrivate()) {
          iconName = "innerClassPrivateIcon";
        } else {
          
          iconName = "innerClassDefaultIcon";
        }
      
      } else {
        
        iconName = "defaultClassIcon";
      }
    
    } else if (typeDec instanceof NormalInterfaceDeclaration) {
      NormalInterfaceDeclaration nid = (NormalInterfaceDeclaration)typeDec;
      if (nid.getModifiers() != null && nid.getModifiers().isPublic()) {
        iconName = "interfaceIcon";
      } else {
        
        iconName = "defaultInterfaceIcon";
      }
    
    } else if (typeDec instanceof EnumDeclaration) {
      EnumDeclaration ed = (EnumDeclaration)typeDec;
      if (ed.getModifiers() != null) {
        if (ed.getModifiers().isPublic()) {
          iconName = "enumIcon";
        }
        else if (ed.getModifiers().isProtected()) {
          iconName = "enumProtectedIcon";
        }
        else if (ed.getModifiers().isPrivate()) {
          iconName = "enumPrivateIcon";
        } else {
          
          iconName = "enumDefaultIcon";
        }
      
      } else {
        
        iconName = "enumDefaultIcon";
      } 
    } 
    
    IconFactory fact = IconFactory.get();
    Icon mainIcon = fact.getIcon(iconName);
    
    if (mainIcon == null) {
      System.out.println("*** " + typeDec);
    } else {
      
      DecoratableIcon di = new DecoratableIcon(mainIcon);
      di.setDeprecated(typeDec.isDeprecated());
      Modifiers mods = typeDec.getModifiers();
      if (mods != null) {
        if (mods.isAbstract()) {
          di.addDecorationIcon(fact.getIcon("abstractIcon"));
        }
        else if (mods.isFinal()) {
          di.addDecorationIcon(fact.getIcon("finalIcon"));
        } 
        if (mods.isStatic()) {
          di.addDecorationIcon(fact.getIcon("staticIcon"));
          priority = -16;
        } 
      } 
      setIcon((Icon)di);
    } 
    
    setSortPriority(priority);
  }



  
  public String getText(boolean selected) {
    TypeDeclaration typeDec = (TypeDeclaration)getUserObject();
    
    return (typeDec != null) ? typeDec.getName() : null;
  }
}
