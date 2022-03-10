import os

SEPARATORS = os.sep if os.altsep is None else os.sep + os.altsep


# FIXME: For our purposes, these should avoid splitting in the middle of [] parts

def iexplode_path(path):
    """Iterate over all the parts of a path.
    Splits path recursively with os.path.split().
    """
    (head, tail) = os.path.split(path)
    if not head or (not tail and head == path):
        if head:
            yield head
        if tail or not head:
            yield tail
        return
    for p in iexplode_path(head):
        yield p
    yield tail


def explode_path(path):
    """Splits path recursively into pieces with os.path.split()"""
    return tuple(iexplode_path(path))
