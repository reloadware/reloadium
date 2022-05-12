from getpass import getpass
from typing import TYPE_CHECKING, Any, Callable, List, Optional

from reloadium.vendored.envium.exceptions import EnviumError

if TYPE_CHECKING:
    pass

from reloadium.vendored.envium.vars import ComputedMixin, Var, VarGroup, VarType

__all__ = ["secret", "Secrets", "computed_secret", "SecretsGroup"]


class SecretVar(Var):
    def __init__(
        self,
        default: Optional[Any] = None,
        default_factory: Optional[Callable] = None,
        value_from_input: bool = True,
    ) -> None:
        super().__init__(default=default, default_factory=default_factory)
        self._value_from_input = value_from_input


class ComputedSecretVar(ComputedMixin, SecretVar):
    def __init__(
        self,
        fget: Optional[Callable] = None,
        fset: Optional[Callable] = None,
        value_from_input: bool = True,
    ) -> None:
        ComputedMixin.__init__(self, fget=fget, fset=fset)
        SecretVar.__init__(self, value_from_input=value_from_input)

    pass


class SecretsGroup(VarGroup[SecretVar]):
    pass


class Secrets(SecretsGroup):
    def __init__(self, name: str = ""):
        super().__init__(name=name)
        self._root = self
        self._process()
        self._get_secrets_from_input()

    def validate(self) -> None:
        self._validate()

    def _get_secrets_from_input(self) -> None:
        for s in self._flat:
            if s._value_from_input:
                value = s._from_str(getpass(f"{s._fullname}: "))
                setattr(s._parent, s._name, value)

    @property
    def errors(self) -> List[EnviumError]:
        return self._errors


def secret(
    default: Optional[Any] = None,
    default_factory: Optional[Callable] = None,
    value_from_input: bool = True,
) -> Any:
    if default:
        value_from_input = False
    return SecretVar(
        default=default,
        default_factory=default_factory,
        value_from_input=value_from_input,
    )


def computed_secret(
    fget: Optional[Callable] = None,
    fset: Optional[Callable] = None,
    value_from_input: bool = True,
) -> Any:
    return ComputedSecretVar(fget=fget, fset=fset, value_from_input=value_from_input)


Group = VarGroup
