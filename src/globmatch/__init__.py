import os

from .translation import compile_pattern
from ._version import __version__


def _fix_path_for_globmatch(path) -> str:
    ret = "start" + os.sep + str(path).lstrip("/")
    return ret


def glob_match(path, globs, subentries_match=None):
    """Matches a path against a sequence of globs."""
    path = _fix_path_for_globmatch(path)
    path = os.path.normcase(path)

    globs = [_fix_path_for_globmatch(p) for p in globs]

    for g in globs:
        matcher = compile_pattern(g, subentries_match=subentries_match)
        if matcher(path):
            return True
    return False

__all__ = ['glob_match', '__version__']
