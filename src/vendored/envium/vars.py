import inspect
import os

# Python >= 3.8
import typing
from abc import ABC, abstractmethod
from copy import deepcopy
from pathlib import Path
from typing import (
    TYPE_CHECKING,
    Any,
    Callable,
    ClassVar,
    Dict,
    Generic,
    List,
    Optional,
    Tuple,
    Type,
    TypeVar,
    Union,
    cast,
)

from reloadium.vendored.envium.exceptions import (
    ComputedVarError,
    EnviumError,
    NoTypeError,
    NoValueError,
    RedefinedVarError,
    UndefinedVarError,
    ValidationErrors,
    WrongTypeError,
)

try:
    typing.get_args  # type: ignore
    typing.get_origin  # type: ignore
# Compatibility
except AttributeError:
    typing.get_args = lambda t: getattr(t, "__args__", ()) if t is not Generic else Generic  # type: ignore
    typing.get_origin = lambda t: getattr(t, "__origin__", None)  # type: ignore


__all__ = ["VarGroup"]

VarType = TypeVar("VarType", bound="FinalVar")


class BaseVar(ABC, Generic[VarType]):
    # will be injected by parent
    _root: Optional["VarGroup"]
    _parent: Optional["BaseVar"]
    _name: str
    _ready: bool

    def __init__(self) -> None:
        self._root = None
        self._parent = None
        self._name = ""
        self._ready = False

    @property
    def _fullname(self) -> str:
        ret = f"{self._parent._fullname}.{self._name}" if self._parent else self._name
        return ret


class FinalVar(BaseVar, ABC, Generic[VarType]):
    _type_: Optional[Type]
    _optional: bool
    _value: Optional[VarType]

    _final: ClassVar[bool] = True
    _ready: bool

    def __init__(self) -> None:
        super().__init__()

        self._type_ = None
        self._optional = False
        self._value = None

    @abstractmethod
    def _init_value(self) -> None:
        raise NotImplementedError

    @abstractmethod
    def _get_errors(self) -> List[EnviumError]:
        return []

    def __repr__(self) -> str:
        return f"{self._fullname}"

    @abstractmethod
    def _get_value(self) -> Any:
        raise NotImplementedError

    @abstractmethod
    def _set_value(self, new_value) -> None:
        raise NotImplementedError


class Var(FinalVar, Generic[VarType]):
    _default: Optional[VarType]
    _default_factory: Optional[Callable]

    def __init__(
        self,
        default: Optional[VarType] = None,
        default_factory: Optional[Callable] = None,
    ) -> None:
        super().__init__()
        self._default_factory = default_factory
        self._default = default

        if self._default_factory:
            self._default = self._default_factory()

    def _init_value(self):
        if not self._value:
            self._value = self._default

    def _get_value(self) -> Any:
        return self._value

    def _set_value(self, new_value) -> None:
        self._value = new_value

    def _get_errors(self) -> List[EnviumError]:
        ret: List[EnviumError] = []

        # Try evaluating value first. There might be some issues with that
        if not self._type_:
            return [NoTypeError(var_name=self._fullname)]

        if not self._optional and self._get_value() is None:
            ret.append(NoValueError(type_=self._type_, var_name=self._fullname))
        elif self._get_value() is not None:
            try:
                if self._type_ and not isinstance(self._get_value(), self._type_):
                    ret.append(
                        WrongTypeError(
                            type_=self._type_,
                            var_name=self._fullname,
                            got_type=type(self._get_value()),
                        )
                    )
            except TypeError:
                # isinstance will fail for types like Union[] etc
                pass

        return super()._get_errors() + ret

    def _from_str(self, env_value: str) -> VarType:
        if self._type_ is bool:
            ret = env_value == "True"
        else:
            ret = self._type_(env_value)

        ret_casted = cast(VarType, ret)
        return ret_casted


class ComputedMixin(Var):
    _fget: Optional[Callable]
    _fset: Optional[Callable]

    def __init__(
        self,
        fget: Optional[Callable] = None,
        fset: Optional[Callable] = None,
    ) -> None:
        super().__init__()
        self._fget = fget
        self._fset = fset

    def _init_value(self):
        self._value = self._get_value()

    def _get_value(self) -> Any:
        object.__setattr__(self, "_ready", False)
        if self._fget:
            ret = self._fget(self._root)
        else:
            ret = self._value
        object.__setattr__(self, "_ready", True)
        return ret

    def _set_value(self, new_value) -> None:
        object.__setattr__(self, "_ready", False)
        if self._fset:
            self._fset(self._root, new_value)
        else:
            self._value = new_value
        object.__setattr__(self, "_ready", True)

    def _get_errors(self) -> List[EnviumError]:
        try:
            self._get_value()
        except Exception as e:
            return [ComputedVarError(var_name=self._fullname, exception=e)]

        return super()._get_errors()


class VarGroup(BaseVar[VarType]):
    _children: List[VarType]

    def __init__(self, name: str = ""):
        super().__init__()
        self._children = []
        self._name = name

        # Create copy of the var class attributes and assign them to the instance
        for f in dir(self):
            attr = inspect.getattr_static(self, f)

            if isinstance(attr, BaseVar):
                setattr(self, f, deepcopy(attr))

    def _process(self) -> None:
        self._children.clear()

        annotations = [
            c.__annotations__
            for c in self.__class__.__mro__
            if hasattr(c, "__annotations__")
        ]
        flat_annotations = {}

        for a in annotations:
            flat_annotations.update(a)

        for n in dir(self):
            v = inspect.getattr_static(self, n)

            if v is self._root:
                continue

            if v is self._parent:
                continue

            if not isinstance(v, BaseVar):
                continue

            v = cast(TypeVar, v)

            v._name = n
            v._root = self._root
            v._parent = self

            self._children.append(v)

            if isinstance(v, VarGroup):
                v._process()
            elif isinstance(v, FinalVar):
                type_ = flat_annotations.get(n, None)
                optional = typing.get_origin(type_) is Union and type(None) in typing.get_args(type_)  # type: ignore
                v._optional = optional

                if optional:
                    v._type_ = typing.get_args(type_)[0]  # type: ignore
                else:
                    v._type_ = type_
                v._init_value()

            v._ready = True

        self._ready = True

    @property
    def _flat(self) -> List[VarType]:
        ret: List[VarType] = []
        for c in self._children:
            if isinstance(c, VarGroup):
                ret.extend(c._flat)
            elif isinstance(c, FinalVar):
                c = cast(VarType, c)
                ret.append(c)

        ret = sorted(ret, key=lambda x: x._fullname)
        return ret

    def copy_from(self, var_group: "VarGroup") -> None:
        for l, r in zip(self._children, var_group._children):
            if isinstance(l, VarGroup):
                l.copy_from(r)
            l._value = r._value

    def __setattr__(self, key: str, value: Any) -> None:
        if not hasattr(self, key):
            if hasattr(self, "_ready") and self._ready:
                raise UndefinedVarError(parent_fullname=self._fullname, var_name=key)
            object.__setattr__(self, key, value)
            return

        attr = object.__getattribute__(self, key)

        if not isinstance(attr, FinalVar):
            object.__setattr__(self, key, value)
            return

        if not attr._ready:
            object.__setattr__(self, key, value)
            return
        else:
            attr._set_value(value)

    def __getattribute__(self, item: str) -> Any:
        attr = object.__getattribute__(self, item)

        if not isinstance(attr, FinalVar):
            return attr

        if not attr._ready:
            return attr

        return attr._get_value()

    @property
    def _errors(self) -> List[EnviumError]:
        ret: List[EnviumError] = []
        for v in self._flat:
            ret.extend(v._get_errors())

        return ret

    def _validate(self) -> None:
        errors = self._errors

        if errors:
            raise ValidationErrors(errors)

    def _dump(self, path: Union[Path, str]) -> None:
        path = Path(path)

        if not path.parent.exists():
            path.parent.mkdir(parents=True)

        path.touch(exist_ok=True)

        content = "\n".join(
            [f'{key}="{value}"' for key, value in self._root._get_env_vars().items()]
        )
        path.write_text(content, "utf-8")
