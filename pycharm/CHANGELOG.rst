Change Log
##########


0.8.1
-------

**Miscellaneous**:
    
  Fixed:
    * Pydash icompatibility
    * --help not working
    * Morphing object types
    * Hanging on reload issues
    
  Added:
    * Handle django model fields
    * Graphene extension
    
**PyCharmPlugin**:
    
  Fixed:
    * Error highlighter not working for closures
    * Multithreaded frame reload issues
    
  Added:
    * Highlighting updated objects
    * Preferences
    * Frame progress rendering
    * Profiling current function
    
0.8.0
-------

**Miscellaneous**:
    
  Fixed:
    * Reloading decorated methods by objects
    * Adding methods bugs
    * Fixing module errors while in function frame bugs
    * Hangs on startup error in debug mode
    * Python <= 3.8 compatiblity issues
    * Python 3.10 compatibility issues
    * Frame restart pointer recovering bugs
    
  Added:
    * Handle reloading main module without guard, while loop as entrypoint
    
  Changed:
    * Optimise threads
    
**PyCharmPlugin**:
    
  Added:
    * First run dialog
    * First debug dialog
    * Events, commands
    * Error highlighter
    * First user error dialog
    * Fixing frame error dialog
    * Remote interpreters improvements
    * Handling remote path mappings
    
0.7.8
-------

**Miscellaneous**:
    
  Fixed:
    * Index not ready errors
    * Optimise import time
    * Modifying decorated class methods bugs
    * Comprehensions bugs
    * Python 3.10 compatibility bugs
    * Reloading nested classes
    * Windows compatibility bugs (django not rolling back db on user error)
    
  Changed:
    * Make debugger speedups disabled by default (does not work in some cases)
    
  Added:
    * Handle user errors feature (let users fix errors that occured durring debugging).
    * Adding and editing enums
    
0.7.7
-------

**Miscellaneous**:
    
  Added:
    * About Reloadium button
    
  Fixed:
    * Older IDE versions compatiblity
    * Patching methods bugs
    * Adding classes bugs
    * Patching tuples bugs
    
**User Experience**:
    
  Added:
    * Modifing not loaded files msg
    
0.7.6
-------

**Miscellaneous**:
    
  Fixed:
    * Updating methods issues under run (non debug)
    
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
    * NotNull parameter exception when there are no packages
    
0.6.3
-------

**Miscellaneous**:
    
  Fixed:
    * Pip compatibility issues for linux
    * EDT errors
    * Reloadium buttons not starting process occasionally
    * General stability
    
  Added:
    * Remote interpreters support
    * Speed optimizations
    * Older versions compatibility
    