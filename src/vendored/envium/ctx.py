from typing import TYPE_CHECKING, Any, Callable, List, Optional

from reloadium.vendored.envium.exceptions import EnviumError

if TYPE_CHECKING:
    pass

from reloadium.vendored.envium.vars import ComputedMixin, Var, VarGroup, VarType

__all__ = ["ctx_var", "Ctx", "computed_ctx_var", "CtxGroup"]


class CtxVar(Var):
    pass


class ComputedCtxVar(ComputedMixin, CtxVar):
    def __init__(
        self, fget: Optional[Callable] = None, fset: Optional[Callable] = None
    ) -> None:
        ComputedMixin.__init__(self, fget=fget, fset=fset)
        CtxVar.__init__(self)

    pass


class CtxGroup(VarGroup[CtxVar]):
    pass


class Ctx(CtxGroup):
    def __init__(self, name: str = ""):
        super().__init__(name=name)
        self._root = self
        self._process()
        a = 1

    def validate(self) -> None:
        self._validate()

    @property
    def errors(self) -> List[EnviumError]:
        return self._errors


def ctx_var(
    default: Optional[Any] = None,
    default_factory: Optional[Callable] = None,
) -> Any:
    return CtxVar(default=default, default_factory=default_factory)


def computed_ctx_var(
    fget: Optional[Callable] = None,
    fset: Optional[Callable] = None,
) -> Any:
    return ComputedCtxVar(fget, fset)


Group = VarGroup
