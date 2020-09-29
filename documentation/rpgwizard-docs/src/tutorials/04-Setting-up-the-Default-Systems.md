## Summary
Before you can use the default systems in your project, you must first copy the required assets from the RPGWizard's default project. In this section we will highlight exactly which files you should include in your own project.

> NOTE: You can also simply use "The Wizard's Tower" as your template project to get these default systems with a new project.

## Steps
Bundled with the RPGWizard you will find the default game project, "The Wizard's Tower". If you have played this game you will have seen a number of default systems in use, namely the title screen, dialog window, and battle system. All of these are freely available and can be reused within your own project if you so wish.

To start using them in another project you simply need to copy the "default" folder and all its contents, which is located within the The Wizard's Tower's "Programs" folder, to your own projects "Programs" folder.

![](images/default_systems/01_setup/images/1.png)

### JavaScript Files

#### gui.js
Provides many common graphical functions used by the default systems in one central file. Specifies what fonts and colours will be used on the UI which helps ensure that they share a consistent look-and-feel.

#### titleScreen.js
Simple example of a title screen system, that can be used on game start up.

#### dialog.js
A fully functioning "typewriter" like dialog window, with animated text, and optional sound effects.

#### battle.js
Basic turn based battle system which supports multi-hero vs multi-enemy battles. Similar to those found in classic turn-based games such as the Final Fantasy series.

#### hud.js
Simple HUD which displays the current character's health as hearts in realtime.

#### inventory.js
Really basic inventory system used to display item counts.

#### weather.js
Contains some basic weather effects including rain and snow.
