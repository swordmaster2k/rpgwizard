# Engine Startup

1. Look for project icon in root of game directory, if present use it.
2. Load project file read viewport and debug settings.
3. Look for start.js in root of scripts directory.
  * If not present fail with error notification.
4. Run user provided start logic.
