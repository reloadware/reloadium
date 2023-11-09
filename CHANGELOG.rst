Change Log
##########


1.3.3
-------

**Miscellaneous**:
    
  Fixed:
    * Not reloading on save for some projects
    * No OpenAI API key message not showing up
    
  Removed:
    * Frame reloading non suspended frames
    
1.3.2
-------

**Miscellaneous**:
    
  Added:
    * Numba support
    * Restrict missing path mapping popup message to project files
    * Fast debug mode for module scopes
    
  Fixed:
    * Fix startup issue for Python 3.8
    * Fast debug not breaking in `if` `while` and other code blocks
    * Filtered logs showing up when using Django
    * Profiling preview overlapping VCS indicators
    
1.3.0
-------

**PyCharm**:
    
  Fixed:
    * Stop using /usr/libexec/rosetta/ to detect rosetta
    * Not deleting temporary files in /tmp
    
  Added:
    * Reloadium settings group
    
**Miscellaneous**:
    
  Added:
    * Better exit codes handling
    * Reloadium Pro
    
  Fixed:
    * Logging formatting issues
    
1.2.1
-------

**Miscellaneous**:
    
  Fixed:
    * PyDev errors on exit
    * Reloading frames issues with break statement
    * Magic mock issues on M1
    
**AI**:
    
  Fixed:
    * Psi element offset issues
    * Chat crashing non deterministically
    
1.2.0
-------

**PyCharm**:
    
  Added:
    * Files with breakpoints are reloadable
    * Processing files progress
    * Uploading reloadium package to remote interpreters improvements
    * Current File run configuration support
    * Only scroll to error if not visible
    
  Fixed:
    * Persisting current line indicators
    * Not switching thread errors
    * Not updating debugger after frame reload
    * Function local completion not working in remote development
    * Syntax errors not clearing
    
**Miscellaneous**:
    
  Added:
    * Ignore special functions like __getattr__, __repr__ from handling exceptions (Unless with a breakpoint)
    * Exception handling speed improvements
    * Drop frame improvements
    * Function mementos when stepping into
    * Debug logging when RW_DEBUG=True
    * Frame reloading closures
    * Handling errors in closures
    * Profile child frames when stepping
    * Better asyncio support
    
  Fixed:
    * Breakpoint always hit on functions last lines
    * Not handling errors during stepping when wrapped in try from outside
    * Jittery stepping into
    * Not breaking in closures
    * Closure parent function not restarting
    * Running with reloadium results in normal run when started too quickly.
    * Encoding issues
    * Circular import dependency issues for bigger projects
    
  Removed:
    * Drop frame support for python <= 3.9
    
1.1.1
-------

**AI**:
    
  Added:
    * Add change context button
    * Resizable code viewer
    * High contrast mode improvements
    * Sources to replies in square bracket notation
    
  Fixed:
    * Hyperlinks not working in markdown
    * Code viewer not persisting extensions
    * Wrong lines for method context
    
**PyCharm**:
    
  Added:
    * Set caret position to error location
    * Dictionary runtime completion improvements
    
  Fixed:
    * Jitters when typing
    * Multiple runtime completion markers in the gutter issues
    * Runtime completion indicator not working for remote interpreters
    
**Miscellaneous**:
    
  Removed:
    * 3.7 support
    
  Added:
    * Reloading non suspended frames (in debug mode)
    * Pytest speedups
    
  Fixed:
    * Mementos crashing for non async functions in async context
    * Not profiling when using freezegun
    * Stripping docstrings in Python 3.11
    * Infinite recursion issue for big projects
    
1.1.0
-------

**PyCharm**:
    
  Added:
    * ChatGPT integration
    * UI Improvements
    
  Fixed:
    * Fix ComparableVersion issues
    
1.0.1
-------

**Miscellaneous**:
    
  Added:
    * Python 3.11 support
    
**PyCharm**:
    
  Fixed:
    * Remote interpreters saving issues
    * Missing () when completing functions
    * Profiler concurrency issues
    * Too many whitespaces in completion tail
    * Apple silicon rosetta support
    * Completion not working for selected frame in evaluate
    * Docker compose interpreter not working
    
  Added:
    * Multiline error rendering
    
1.0.0
-------

**Miscellaneous**:
    
  Added:
    * Dropping module frames for M1
    * Add __doc__ to function calls completion
    * Runtime completion for evaluate mode
    * Numpy __doc__ style completion support
    * FastApi support
    * Hot reloading docstrings
    
  Fixed:
    * Assertion error in fast debug mode when no breakpoints present
    * Wrong error lines in fast debug mode
    * Reloading issues when using snoop library
    * Celery noreload flag issue
    * Cannot retrieve frame symbol issues
    * Cannot drop module frame on M1
    
  Removed:
    * Telemetry, sentry opt out
    
**PyCharm**:
    
  Fixed:
    * Completion issues when not suspended
    * Slow action on EDT issues
    
  Added:
    * Completion in run mode
    
0.9.11
-------

**Miscellaneous**:
    
  Fixed:
    * No reload decorator in function and module frames issues
    
  Added:
    * Fast debug
    * Async mementos support
    
**PyCharm**:
    
  Added:
    * Always collect memory info option
    * New UI support
    * Multithreaded frame errors support 
    * Runtime completion
    * Remote development automatic package upload
    * Centering editor on errors
    
  Fixed:
    * Frame progress not showing on first slow line
    * Null pointer exception when dropping frames
    
0.9.10
-------

**Miscellaneous**:
    
  Added:
    * Restarting non top frames
    
  Fixed:
    * Not restarting frame on related files changes
    
**PyCharm**:
    
  Fixed:
    * Crashing on None profiler
    * Error message and highlighter not disappearing
    * Execution highlighter not disappearing
    
  Added:
    * Tooltip for profiler preview renderer
    
0.9.9
-------

**Miscellaneous**:
    
  Fixed:
    * Dropping multiple frames after frame restarting issues
    * Reloading flask views
    
  Added:
    * Async generators support
    
**PyCharm**:
    
  Fixed:
    * Jittery frame dropping visualisation
    
0.9.8
-------

**PyCharm**:
    
  Fixed:
    * Error handling preferences issues
    * M2 Chip issues
    * Debugger in suspend mode after fixing an error
    * Marking reloadable frames if non reloadable between
    
0.9.7
-------

**Miscellaneous**:
    
  Added:
    * Handle adding and modifying dataclass class variables
    * Make RW_DEBUG work in prod
    
  Fixed:
    * Flask-sqlalchemy issues
    
**PyCharm**:
    
  Added:
    * Error message on missing path mappings
    * Profiling formatting improvements
    * Collecting both memory and time information at the same time
    * Profiling values folding
    * Profiling color map frame scope
    * Set as default buttons to quick config page
    * Profiling cumulate type
    * Highlighting reloadable frames in the call stack
    * Add drop frame action (pop, reset frame)
    * Hot reloading unhandled exceptions without breakpoint
    
  Fixed:
    * Profiling sampling issue (blank values for 100ms lines)
    * Detecting M1 issues
    
0.9.6
-------

**Miscellaneous**:
    
  Changed:
    * Incompatible system message
    
**PyCharm**:
    
  Fixed:
    * M1 installation compatibility issues
    * Non ascii paths issues on save
    * Not cleaning profile information
    
0.9.5
-------

**PyCharm**:
    
  Added:
    * Quick config
    * Memory line profiler
    
  Fixed:
    * Detecting rosetta
    * System PYTHONPATH not persisting issue
    
**Miscellaneous**:
    
  Fixed:
    * Future imports and docstring issue
    * Missing docstrings
    * Non ascii paths issues
    
0.9.4
-------

**Miscellaneous**:
    
  Fixed:
    * Fix adding from import items issue
    * Fix windows multiprocessing bugs
    
**PyCharm**:
    
  Added:
    * Easier downgrading
    
  Removed:
    * Package autoupdater
    
  Fixed:
    * Confusing RELOADIUMPATH message when no files are watched
    * Remote interpreter issues for new PyCharm versions
    * View pane null pointer exception
    
0.9.3
-------

**Miscellaneous**:
    
  Fixed:
    * Encoding issues
    * Import threading issues
    * Multiprocessing issues
    * Double popup issue on FrameError
    
  Added:
    * Multiprocessing extension
    * Manual reload file command
    * Symlinks and mounted directories support
    
**PyCharm**:
    
  Added:
    * Reloadable files highlighting
    * Manual reload action
    
  Fixed:
    * Too many open files issue
    
0.9.2
-------

**Miscellaneous**:
    
  Added:
    * Support for no_reload decorator for frame reloads
    * Profiling optimisations
    * PyTest extension
    * Disabling telemetry
    * Disabling error reporting
    * RELOADIUMIGNORE env variable
    * M1 support
    * Profiling optimisations
    
  Fixed:
    * cached_property issues
    * Moving function closures
    * Moving non instantiated closures
    
  Removed:
    * Win32 support
    
**PyCharm**:
    
  Fixed:
    * Freeze on update
    
  Added:
    * Docker compose support
    * Docker support
    
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
    