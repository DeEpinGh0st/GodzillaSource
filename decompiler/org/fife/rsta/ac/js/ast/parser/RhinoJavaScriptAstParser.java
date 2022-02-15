package org.fife.rsta.ac.js.ast.parser;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.CodeBlock;
import org.fife.rsta.ac.js.ast.TypeDeclarationOptions;
import org.fife.rsta.ac.js.ast.jsType.JavaScriptTypesFactory;
import org.fife.rsta.ac.js.ast.jsType.RhinoJavaScriptTypesFactory;
import org.fife.ui.autocomplete.Completion;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;









public class RhinoJavaScriptAstParser
  extends JavaScriptAstParser
{
  public static final String PACKAGES = "Packages.";
  private LinkedHashSet<String> importClasses = new LinkedHashSet<>();
  private LinkedHashSet<String> importPackages = new LinkedHashSet<>();

  
  public RhinoJavaScriptAstParser(SourceCompletionProvider provider, int dot, TypeDeclarationOptions options) {
    super(provider, dot, options);
  }




  
  public void clearImportCache(SourceCompletionProvider provider) {
    JavaScriptTypesFactory typesFactory = provider.getJavaScriptTypesFactory();
    if (typesFactory instanceof RhinoJavaScriptTypesFactory) {
      ((RhinoJavaScriptTypesFactory)typesFactory).clearImportCache();
    }
  }



  
  public CodeBlock convertAstNodeToCodeBlock(AstRoot root, Set<Completion> set, String entered) {
    try {
      return super.convertAstNodeToCodeBlock(root, set, entered);
    } finally {
      
      mergeImportCache(this.importPackages, this.importClasses);
      
      this.importClasses.clear();
      this.importPackages.clear();
    } 
  }
  
  private void mergeImportCache(HashSet<String> packages, HashSet<String> classes) {
    JavaScriptTypesFactory typesFactory = this.provider.getJavaScriptTypesFactory();
    if (typesFactory instanceof RhinoJavaScriptTypesFactory) {
      ((RhinoJavaScriptTypesFactory)typesFactory).mergeImports(packages, classes);
    }
  }








  
  protected void iterateNode(AstNode child, Set<Completion> set, String entered, CodeBlock block, int offset) {
    boolean importFound;
    switch (child.getType()) {
      case 134:
        importFound = processImportNode(child, set, entered, block, offset);
        if (importFound) {
          return;
        }
        break;
    } 
    super.iterateNode(child, set, entered, block, offset);
  }












  
  private boolean processImportNode(AstNode child, Set<Completion> set, String entered, CodeBlock block, int offset) {
    String src = JavaScriptHelper.convertNodeToSource(child);
    if (src != null) {
      if (src.startsWith("importPackage")) {
        processImportPackage(src);
        return true;
      } 
      if (src.startsWith("importClass")) {
        processImportClass(src);
        return true;
      } 
    } 

    
    return false;
  }

  
  public static String removePackages(String src) {
    if (src.startsWith("Packages.")) {
      
      String pkg = src.substring("Packages.".length());
      if (pkg != null) {
        StringBuilder sb = new StringBuilder();
        
        char[] chars = pkg.toCharArray();
        for (int i = 0; i < chars.length; i++) {
          char ch = chars[i];
          if (Character.isJavaIdentifierPart(ch) || ch == '.') {
            sb.append(ch);
          }
        } 
        if (sb.length() > 0) {
          return sb.toString();
        }
      } 
    } 
    return src;
  }









  
  private String extractNameFromSrc(String src) {
    int startIndex = src.indexOf("(");
    int endIndex = src.indexOf(")");
    if (startIndex != -1 && endIndex != -1) {
      return removePackages(src.substring(startIndex + 1, endIndex));
    }
    return removePackages(src);
  }




  
  private void processImportPackage(String src) {
    String pkg = extractNameFromSrc(src);
    this.importPackages.add(pkg);
  }




  
  private void processImportClass(String src) {
    String cls = extractNameFromSrc(src);
    this.importClasses.add(cls);
  }
}
