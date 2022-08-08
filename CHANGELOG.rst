Change Log
##########


0.9.1
-------

**Miscellaneous**:
    
  Changed:
    * Add mypyc optimisations
    
  Added:
    * Support async methods
    * Nested closures support
    
  Removed:
    * Python 3.6 support
    
0.9.0
-------

**Miscellaneous**:
    
  Changed:
    * More defensive reloading
    
  Added:
    * Reloading closures
    * Before and after reload hooks
    * Accepting (re-raising) handled exceptions
    * Profiling modules
    
  Fixed:
    * Not resolving templates for Flask
    * Errors not highlighted when reloading module frames
    * Syntax errors not highlighted
    * Pickling issues
    * Watching paths containing dots
    
**PyCharm**:
    
  Added:
    * Rendering exception messages
    
0.8.8
-------

**Miscellaneous**:
    
  Fixed:
    * Frame progress stopping after handled exceptions
    * Startup error when running without utf-8 encoding
    * Fixing errors mode for handled exceptions
    * Mocked functions errors
    * Intercepting flask errors
    * Reference issues for enums
    * Dataclass attributes updating issues
    * Debugger speedups
    
  Added:
    * Handle profiling closures
    * VsCode compatibility
    
**PyCharm**:
    
  Added:
    * More colormaps choices
    
  Changed:
    * Move Timing Details button below Annotate with git blame
    * Make debugger speedups enabled by default
    
  Fixed:
    * Disappearing frame progress for very slow lines
    
0.8.7
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
    
**PyCharm**:
    
  Fixed:
    * Error highlighter not working for closures
    * Multithreaded frame reload issues
    
  Added:
    * Highlighting updated objects
    * Preferences
    * Frame progress rendering
    * Profiling current function
    
0.8.6
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
    
**PyCharm**:
    
  Added:
    * First run dialog
    * First debug dialog
    * Events, commands
    * Error highlighter
    * First user error dialog
    * Fixing frame error dialog
    * Remote interpreters improvements
    * Handling remote path mappings
    
0.8.5
-------

**Miscellaneous**:
    
  Changed:
    * Make debugger speedups disabled by default (does not work in some cases)
    
  Fixed:
    * Optimise import time
    * Modifying decorated class methods bugs
    * Comprehensions bugs
    * Python 3.10 compatibility bugs
    * Reloading nested classes
    * Windows compatibility bugs (django not rolling back db on user error)
    
  Added:
    * Handle user errors feature (let users fix errors that occured durring debugging).
    * Adding and editing enums
    
0.8.4
-------

**Miscellaneous**:
    
  Fixed:
    * Patching methods bugs
    * Adding classes bugs
    * Patching tuples bugs
    
**User Experience**:
    
  Added:
    * Modifying not loaded files msg
    
0.8.3
-------

**Miscellaneous**:
    
  Fixed:
    * Updating methods issues under run (non debug)
    
0.8.2
-------

**Miscellaneous**:
    
  Added:
    * Add settings env variables
    * RELOADIUMPATH working for files
    
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
    
0.8.0
-------

**Miscellaneous**:
    
  Added:
    * Print warning when no files are watched
    * Print watched paths on start
    
  Fixed:
    * PYTHONPATH issues for standalone usage
    * Tuples reloading when not changed bug
    
0.7.18
-------

**Miscellaneous**:
    
  Changed:
    * Move cache to dot directory
    
  Added:
    * No reload decorators
    * PyGame plugin
    * Older mac os systems compatibility
    * No reload decorators validation
    
  Fixed:
    * Flask request object update issues
    * Python 3.6 compatibility issues
    
  Removed:
    * Full reload feature
    
0.7.17
-------

**Miscellaneous**:
    
  Added:
    * Windows 32 bit compatibility
    * Handling unsupported python and os versions
    * Desynchronisation reason messages
    * Python 3.10 support
    * Report to github message
    * Wheel size optimisation
    
0.7.15
-------

**Miscellaneous**:
    
  Added:
    * Property frame reloading
    * Classmethods frame reloading
    
  Fixed:
    * Minor bugs
    
**Code quality**:
    
  Added:
    * Frame reloader integrity tests
    * General refactor
    
0.7.13
-------

**Miscellaneous**:
    
  Fixed:
    * No __main__ feature not working for some cases
    * Unable to update function after frame update
    * Slices for Python 3.8 produce exception
    
  Added:
    * Support for __future__ imports
    
0.7.12
-------

**Miscellaneous**:
    
  Fixed:
    * Support for no __main__ guard when debugging
    
0.7.11
-------

**Miscellaneous**:
    
  Added:
    * Support for no __main__ guard
    
0.7.10
-------

**Miscellaneous**:
    
  Added:
    * Remote interpreters debugging support
    
  Fixed:
    * Nuitka errors on Windows
    
0.7.9
-------

**Miscellaneous**:
    
  Added:
    * Remote interpreters debugging support
    
  Fixed:
    * Nuitka errors on Windows
    
0.7.8
-------

**Miscellaneous**:
    
  Fixed:
    * Process hanging on exit
    * Crashing when django installed but not imported
    
**PyCharm**:
    
  Added:
    * Django and Flask support
    
0.7.6
-------

**Miscellaneous**:
    
  Added:
    * Python < 3.9 versions
    
0.7.0
-------

**Miscellaneous**:
    
  Fixed:
    * Windows compatibility issues
    
  Added:
    * Terminal commands and help 
    
**PyCharm**:
    
  Fixed:
    * Run and Debug Buttons would fail if clicked too fast
    