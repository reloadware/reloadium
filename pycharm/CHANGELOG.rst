Change Log
##########


1.5.1
-------

**Miscellaneous**:
    
  Fixed:
    * Multiprocessing Pool issues
    * RuntimeError: super(): empty __class__ cell when frame reloading with super()
    
1.5.0
-------

**Miscellaneous**:
    
  Fixed:
    * Memory leaks on stepping
    * Memory leaks on frame reloading
    * CUDA out of memory issues
    * RuntimeError: Attempted implicid sequence conversion issues in flask apps
    * Django and webpack issues
    
  Added:
    * Hot reloading django templates
    
**PyCharm**:
    
  Fixed:
    * Highlighting errors in editor on hover
    
1.4.1
-------

**Miscellaneous**:
    
  Fixed:
    * Not profiling some functions without breakpoint
    * Dropping debugger on hot reloading errors
    * Profiling data not updating for some multiline function calls
    
  Added:
    * Future annotations support
    * Hot reloading __init__ and __post_init__ methods
    
**PyCharm**:
    
  Fixed:
    * Previewing variables not working for some django projects
    
  Added:
    * 2024.1 support
    
1.4.0
-------

**PyCharm**:
    
  Fixed:
    * Reloadium error when switching tabs in run mode
    * Frame progress errors on some setups
    
**Miscellaneous**:
    
  Added:
    * Line profiler optimisations
    * Processing files optimisations
    * Reloading long files optimisations
    
  Fixed:
    * Crashing on start for inline functions
    
1.3.4
-------

**Miscellaneous**:
    
  Added:
    * More robust frame reloading
    * Updating methods of nested dataclasses
    * Dropping frames of functions returning tuples.
    * Reloading methods of classes inside functions.
    * Celery support
    * Reloading forked processes support
    * Improved line profiler.
    * Profiling open files without breakpoints
    * Profiling on stepping
    * Profiling on resume
    * Real time profiling on slow lines
    
  Fixed:
    * Frame reloading crashing occasionally
    * Orange update indicator showing for whole dataclasses when updating a single object
    * Freezing on reloads
    * -X flag issue on new PyCharm versions
    * Debugger freezing occasionally
    
**PyCharm**:
    
  Fixed:
    * Error message not showing occasionally
    * Droppable frames logic.
    * Not reloading on save when multiple projects are open.
    * Blue frame progress indicators not hiding
    * Averaging line profile values in 'Mean' mode
    
  Added:
    * Line timing rendering optimisations
    
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
    
0.9.6
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
    
0.9.5
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
    
0.9.4
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
    
0.9.3
-------

**PyCharm**:
    
  Fixed:
    * Error handling preferences issues
    * M2 Chip issues
    * Debugger in suspend mode after fixing an error
    * Marking reloadable frames if non reloadable between
    
0.9.2
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
    
0.9.1
-------

**Miscellaneous**:
    
  Changed:
    * Incompatible system message
    
**PyCharm**:
    
  Fixed:
    * M1 installation compatibility issues
    * Non ascii paths issues on save
    * Not cleaning profile information
    
0.9.0
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
    * Remote interpreter issues in 2022.2.3
    
0.8.8
-------

**Miscellaneous**:
    
  Fixed:
    * Empty working directory issues
    
0.8.7
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
    
0.8.6
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
    
0.8.5
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
    
0.8.4
-------

**Miscellaneous**:
    
  Changed:
    * Add mypyc optimisations
    
  Added:
    * Support async methods
    * Nested closures support
    
  Removed:
    * Python 3.6 support
    
0.8.3
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
    * Watching paths with dots
    
**PyCharm**:
    
  Added:
    * Rendering exception messages
    
0.8.2
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
    