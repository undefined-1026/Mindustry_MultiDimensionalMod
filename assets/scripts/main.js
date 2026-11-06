let mod = Vars.mods.locateMod("mdimension"); //Get module by name
if(mod != null && !Vars.headless) { //Check if the mod exists and if the game is not running in headless mode (without a graphical interface)
  mod.meta.displayName = Core.bundle.get("mdimension.name"); //Core.bundle.get retrieves the translation, then modifies the module's meta tag.
  mod.meta.description = Core.bundle.get("mdimension.description");
};
