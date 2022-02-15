package org.fife.rsta.ac.js.ast.jsType;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.ast.parser.RhinoJavaScriptAstParser;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.ast.type.TypeDeclarationFactory;












public class RhinoJavaScriptTypesFactory
  extends JSR223JavaScriptTypesFactory
{
  private LinkedHashSet<String> importClasses = new LinkedHashSet<>();
  private LinkedHashSet<String> importPackages = new LinkedHashSet<>();

  
  public RhinoJavaScriptTypesFactory(TypeDeclarationFactory typesFactory) {
    super(typesFactory);
  }
  
  public void addImportClass(String qualifiedClass) {
    this.importClasses.add(qualifiedClass);
  }

  
  public void addImportPackage(String packageName) {
    this.importPackages.add(packageName);
  }

  
  public void mergeImports(HashSet<String> packages, HashSet<String> classes) {
    mergeImports(packages, this.importPackages, true);
    mergeImports(classes, this.importClasses, false);
  }


  
  private void mergeImports(HashSet<String> newImports, LinkedHashSet<String> oldImports, boolean packages) {
    HashSet<String> remove = new HashSet<>();
    for (String obj : oldImports) {
      if (!newImports.contains(obj)) {
        remove.add(obj);
      }
    } 


    
    if (!remove.isEmpty()) {
      
      HashSet<TypeDeclaration> removeTypes = new HashSet<>();
      for (String name : remove) {
        for (TypeDeclaration dec : this.cachedTypes.keySet()) {
          if ((packages && dec.getQualifiedName().startsWith(name)) || (!packages && dec.getQualifiedName().equals(name))) {
            
            removeAllTypes(this.cachedTypes.get(dec));
            removeTypes.add(dec);
          } 
        } 
      } 
      this.cachedTypes.keySet().removeAll(removeTypes);
    } 
    
    if (canClearCache(newImports, oldImports)) {

      
      oldImports.clear();
      
      clearAllImportTypes();
      
      oldImports.addAll(newImports);
    } 
  }






  
  private boolean canClearCache(HashSet<String> newImports, LinkedHashSet<String> oldImports) {
    if (newImports.size() != oldImports.size()) {
      return true;
    }
    
    for (String im : oldImports) {
      if (!newImports.contains(im)) {
        return true;
      }
    } 
    
    return false;
  }

  
  public void clearImportCache() {
    this.importClasses.clear();
    this.importPackages.clear();
    clearAllImportTypes();
  }

  
  private void clearAllImportTypes() {
    HashSet<TypeDeclaration> removeTypes = new HashSet<>();
    
    for (Iterator<TypeDeclaration> i = this.cachedTypes.keySet().iterator(); i.hasNext(); ) {
      TypeDeclaration dec = i.next();
      if (!this.typesFactory.isJavaScriptType(dec) && !dec.equals(this.typesFactory.getDefaultTypeDeclaration())) {
        removeAllTypes(this.cachedTypes.get(dec));
        removeTypes.add(dec);
      } 
    } 
    this.cachedTypes.keySet().removeAll(removeTypes);
  }





  
  private void removeAllTypes(JavaScriptType type) {
    if (type != null) {
      
      this.typesFactory.removeType(type.getType().getQualifiedName());
      if (type.getExtendedClasses().size() > 0)
      {
        for (Iterator<JavaScriptType> i = type.getExtendedClasses().iterator(); i.hasNext(); ) {
          
          JavaScriptType extendedType = i.next();
          removeAllTypes(extendedType);
        } 
      }
    } 
  }






  
  public ClassFile getClassFile(JarManager manager, TypeDeclaration type) {
    String qName = removePackagesFromType(type.getQualifiedName());
    ClassFile file = super.getClassFile(manager, JavaScriptHelper.createNewTypeDeclaration(qName));
    if (file == null) {
      file = findFromClasses(manager, qName);
      if (file == null) {
        file = findFromImport(manager, qName);
      }
    } 
    return file;
  }

  
  private String removePackagesFromType(String type) {
    if (type.startsWith("Packages."))
    {
      return RhinoJavaScriptAstParser.removePackages(type);
    }
    return type;
  }






  
  private ClassFile findFromClasses(JarManager manager, String name) {
    ClassFile file = null;
    for (String cls : this.importClasses) {
      if (cls.endsWith(name)) {
        file = manager.getClassEntry(cls);
        if (file != null)
          break; 
      } 
    } 
    return file;
  }






  
  private ClassFile findFromImport(JarManager manager, String name) {
    ClassFile file = null;
    for (String packageName : this.importPackages) {
      String cls = name.startsWith(".") ? (packageName + name) : (packageName + "." + name);
      file = manager.getClassEntry(cls);
      if (file != null)
        break; 
    } 
    return file;
  }
}
