package org.fife.rsta.ac.js.ast.jsType;

import java.util.HashSet;
import java.util.Map;
import org.fife.rsta.ac.js.Logger;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.completion.JSCompletion;



public class JSR223Type
  extends JavaScriptType
{
  public JSR223Type(TypeDeclaration type) {
    super(type);
  }








  
  protected JSCompletion _getCompletion(String completionLookup, SourceCompletionProvider provider) {
    JSCompletion completion = this.methodFieldCompletions.get(completionLookup);
    if (completion != null) {
      return completion;
    }
    
    if (completionLookup.indexOf('(') != -1) {
      boolean isJavaScriptType = provider.getTypesFactory().isJavaScriptType(getType());

      
      Logger.log("Completion Lookup : " + completionLookup);
      
      JavaScriptFunctionType javaScriptFunctionType = JavaScriptFunctionType.parseFunction(completionLookup, provider);
      
      JSCompletion[] matches = getPotentialLookupList(javaScriptFunctionType
          .getName());

      
      int bestFitIndex = -1;
      int bestFitWeight = -1;
      Logger.log("Potential matches : " + matches.length);
      for (int i = 0; i < matches.length; i++) {
        Logger.log("Potential match : " + matches[i].getLookupName());
        
        JavaScriptFunctionType matchFunctionType = JavaScriptFunctionType.parseFunction(matches[i].getLookupName(), provider);
        Logger.log("Matching against completion: " + completionLookup);
        int weight = matchFunctionType.compare(javaScriptFunctionType, provider, isJavaScriptType);
        
        Logger.log("Weight: " + weight);
        if (weight < JavaScriptFunctionType.CONVERSION_NONE && (weight < bestFitWeight || bestFitIndex == -1)) {
          
          bestFitIndex = i;
          bestFitWeight = weight;
        } 
      } 
      if (bestFitIndex > -1) {
        
        Logger.log("BEST FIT: " + matches[bestFitIndex]
            .getLookupName());
        return matches[bestFitIndex];
      } 
    } 
    
    return null;
  }


  
  private JSCompletion[] getPotentialLookupList(String name) {
    HashSet<JSCompletion> completionMatches = new HashSet<>();
    getPotentialLookupList(name, completionMatches, this);
    return (JSCompletion[])completionMatches.toArray((Object[])new JSCompletion[0]);
  }



  
  private void getPotentialLookupList(String name, HashSet<JSCompletion> completionMatches, JavaScriptType type) {
    Map<String, JSCompletion> typeCompletions = type.methodFieldCompletions;
    
    for (String key : typeCompletions.keySet()) {
      if (key.startsWith(name)) {
        JSCompletion completion = typeCompletions.get(key);
        if (completion instanceof org.fife.ui.autocomplete.FunctionCompletion) {
          completionMatches.add(completion);
        }
      } 
    } 

    
    for (JavaScriptType extendedType : type.getExtendedClasses())
      getPotentialLookupList(name, completionMatches, extendedType); 
  }
}
