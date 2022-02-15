package org.fife.rsta.ac.css;

import java.util.List;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;

public interface CompletionGenerator {
  List<Completion> generate(CompletionProvider paramCompletionProvider, String paramString);
}
