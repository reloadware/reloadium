from pathlib import Path
from types import ModuleType
from typing import Any, Optional


# ########## public ###########
class Object:
    py_obj: Any

    module: "Module"
    name: str
    full_name: str
    namespace: str


class Variable(Object):
    pass


class All(Object):
    pass


class Dictionary(Object):
    pass


class Import(Object):
    pass


class Iterable(Object):
    pass


class List(Iterable):
    pass


class Tuple(Iterable):
    pass


class Function(Object):
    pass


class Lambda(Function):
    pass


class Container(Object):
    pass


class Class(Container):
    pass


class DataClass(Class):
    pass


class Method(Function):
    pass


class LambdaMethod(Method, Function):
    pass


class PropertyMethod(Method):
    pass


class PropertyGet(PropertyMethod):
    pass


class PropertyDel(PropertyMethod):
    pass


class PropertySet(PropertyMethod):
    pass


class Property(Method):
    pass


class ClassMethod(Method):
    pass


class StaticMethod(Method):
    pass


class Enum(Class):
    pass


class EnumAttr(Object):
    pass


class Foreigner(Object):
    pass


class Reference(Object):
    pass


class Module(Object):
    path: Path

    module: "Module"


class Action:
    obj: Object

    @property
    def name(self) -> str:
        ...


class Add(Action):
    pass


class Update(Action):
    pass


class Delete(Action):
    pass


class DeepUpdate(Action):
    pass



# ############# Config #############
from typing import List as TList

class BaseConfig:
    watched_paths: TList[str] = []
    ignored_paths: TList[str] = [
        "**/.*",
        "**/*~",
        "**/__pycache__",
        "**/__reloadium__*.py",
        "**/reloadium_config.py",
    ]

    debugger_speedups: bool = True
    verbose: bool = True

    def on_start(self, argv: TList[str]) -> None:
        ...

    def before_full_reload(self, path: Path) -> None:
        ...

    def before_reload(self, path: Path) -> None:
        ...

    def after_reload(self, path: Path, actions: TList[Action]) -> None:
        ...

    def before_rollback(self, path: Path, actions: TList[Action]) -> None:
        ...

    def after_rollback(self, path: Path, actions: TList[Action]) -> None:
        ...

    def plugins(self) -> TList[ModuleType]:
        ...


def start() -> None:
    pass
