Change Log
##########


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
    
**PyCharmPlugin**:
    
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
    
**PyCharmPlugin**:
    
  Fixed:
    * Run and Debug Buttons would fail if clicked too fast
    