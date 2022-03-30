Change Log
##########


0.7.5
-------

**Miscellaneous**:
    
  Added:
    * RELOADIUMPATH working for files
    * Settings
    * Reloadium menu group
    
  Fixed:
    * Reloading current function with decorators bug
    * Remote interpreters issues
    * Breakpoint not hit when no files are watching
    
**User Experience**:
    
  Added:
    * Warning when editing current function during runtime (not debug)
    * Message that user reload errors can be fixed
    
**Django**:
    
  Fixed:
    * Fixing errors during current function not rolling back session properly
    
**Flask**:
    
  Fixed:
    * Editing template files not reloading page for Flask
    
0.7.3
-------

**Miscellaneous**:
    
  Added:
    * Print warning when no files are watched
    * Print watched paths on start
    
  Fixed:
    * Tuples reloading when not changed bug
    
0.7.2
-------

**Miscellaneous**:
    
  Added:
    * Older mac os systems compatibility
    * Bundle library into the plugin
    * PyGame plugin
    * No reload decorators
    * No reload decorators validation
    
  Changed:
    * Bump library version
    * Move cache to dot directory
    
  Fixed:
    * Context popup actions EDT errors
    * Flask request object update issues
    * Python 3.6 compatibility issues
    
0.7.1
-------

**Miscellaneous**:
    
  Fixed:
    * Older IDE version compatibility
    * Persisting old package versions
    
  Added:
    * Windows 32bit support
    * Handling not supported python versions
    
**Code Quality**:
    
  Added:
    * More tests
    
  Changed:
    * Wheels handling refactor
    
0.7.0
-------

**Miscellaneous**:
    
  Added:
    * Conda compatibility
    * PipEnv compatibility
    * Poetry compatibility
    * Old pip version compatibility
    
  Fixed:
    * EDT errors for context actions
    
**Code quality**:
    
  Added:
    * Unit tests, integration tests
    * General refactor
    
0.6.5
-------

**Miscellaneous**:
    
  Fixed:
    * Null pointer exception when sdk is not set
    * Error when setting run from context menu but not exists in configuration list
    
  Removed:
    * Shortcuts mapping
    
0.6.4
-------

**Miscellaneous**:
    
  Fixed:
    * Update popup
    * Context group action running wrong configuration
    * NotNull parameter exeception when there are no packages
    
0.6.3
-------

**Miscellaneous**:
    
  Fixed:
    * Pip compatibility issues for linux
    * EDT errors
    * Reloadium buttons not starting process ocassionally
    * General stability
    
  Added:
    * Remote interpreters support
    * Speed optimizations
    * Older versions compatibility
    