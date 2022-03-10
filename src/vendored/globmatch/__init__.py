import os

from .translation import compile_pattern
from ._version import __version__


def glob_match(path, globs, subentries_match=None):
    """Matches a path against a sequence of globs."""
    path = os.path.normcase(path)
    for g in globs:
        matcher = compile_pattern(g, subentries_match=subentries_match)
        if matcher(path):
            return True
    return False

__all__ = ['glob_match', '__version__']
