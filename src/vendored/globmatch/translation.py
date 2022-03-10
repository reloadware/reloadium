import os
import re
try:
    from functools import lru_cache
except ImportError:
    from backports.functools_lru_cache import lru_cache

from .pathutils import iexplode_path, SEPARATORS


# TODO: In the future use f-strings for formatting

os_sep_class = '[%s]' % re.escape(SEPARATORS)
double_start_re = r'.*((?<=(%s))|(?<=(\A)))' % (os_sep_class,)


@lru_cache(maxsize=256, typed=True)
def compile_pattern(pat, subentries_match=None):
    """Translate and compile a glob pattern to a regular expression matcher.
    Parameters:
        pat: string
            The pattern to compile
        subentries_match: boolean
            When true, acts as if
    """
    if isinstance(pat, bytes):
        pat_str = pat.decode('ISO-8859-1')
        res_str = translate_glob(os.path.normcase(pat_str), subentries_match=subentries_match)
        res = res_str.encode('ISO-8859-1')
    else:
        res = translate_glob(os.path.normcase(pat), subentries_match=subentries_match)
    return re.compile(res).match

def translate_glob(pat, subentries_match=None):
    """Translate a glob PATTERN to a regular expression."""
    translated_parts = []
    for part in iexplode_path(pat):
        translated_parts.append(translate_glob_part(part))
    res = join_translated(translated_parts, os_sep_class, subentries_match=subentries_match)
    res = r'(?s:{res})\Z'.format(res=res)
    return res


def join_translated(translated_parts, os_sep_class, subentries_match):
    """Join translated glob pattern parts.
    This is different from a simple join, as care need to be taken
    to allow ** to match ZERO or more directories.
    """
    res = ''
    for part in translated_parts[:-1]:
        if part == double_start_re:
            # drop separator, since it is optional
            # (** matches ZERO or more dirs)
            res += part
        else:
            res += part + os_sep_class

    if translated_parts[-1] == double_start_re:
        # Final part is **
        # Should not match directory:
        res += '.+'
        # Follow stdlib/git convention of matching all sub files/directories:
        res += '({os_sep_class}?.*)?'.format(os_sep_class=os_sep_class)
    elif subentries_match:
        # Should match directory, but might also match sub entries:
        res += translated_parts[-1]
        res += '({os_sep_class}?.*)?'.format(os_sep_class=os_sep_class)
    else:
        res += translated_parts[-1]
        # Allow trailing slashes
        # Note: This indicates that the last part whould be a directory, but
        # we explictly say that we don't consult the filesystem, so there is
        # no way for us to know.
        res += '{os_sep_class}?'.format(os_sep_class=os_sep_class)
    return res


def translate_glob_part(pat):
    """Translate a glob PATTERN PART to a regular expression."""
    # Code modified from Python 3 standard lib fnmatch:
    if pat == '**':
        return double_start_re
    i, n = 0, len(pat)
    res = []
    while i < n:
        c = pat[i]
        i = i+1
        if c == '*':
            # Match anything but path separators:
            res.append('[^%s]*' % SEPARATORS)
        elif c == '?':
            res.append('[^%s]?' % SEPARATORS)
        elif c == '[':
            j = i
            if j < n and pat[j] == '!':
                j = j+1
            if j < n and pat[j] == ']':
                j = j+1
            while j < n and pat[j] != ']':
                j = j+1
            if j >= n:
                res.append('\\[')
            else:
                stuff = pat[i:j].replace('\\', '\\\\')
                i = j+1
                if stuff[0] == '!':
                    stuff = '^' + stuff[1:]
                elif stuff[0] == '^':
                    stuff = '\\' + stuff
                res.append('[%s]' % stuff)
        else:
            res.append(re.escape(c))
    return ''.join(res)
