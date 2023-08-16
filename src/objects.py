from pathlib import Path
from typing import TYPE_CHECKING, Any, Optional
from dataclasses import dataclass, field

dataclass = dataclass(repr=False)  # type: ignore

__RELOADIUM__ = True

__all__ = [
    "Object",
    "Variable",
    "All",
    "Dictionary",
    "Import",
    "Imported",
    "Iterable",
    "List",
    "Tuple",
    "Function",
    "Lambda",
    "Container",
    "Class",
    "DataClass",
    "Method",
    "LambdaMethod",
    "PropertyMethod",
    "PropertyGet",
    "PropertyDel",
    "PropertySet",
    "Property",
    "ClassMethod",
    "StaticMethod",
    "Enum",
    "EnumAttr",
    "Dynamic",
    "Reference",
    "Module",
    "Action",
    "Add",
    "Update",
    "Delete",
    "DeepUpdate",
    "UpdateFrame",
]


@dataclass
class Object:
    py_obj: Any

    module: Optional["Module"]
    name: str
    fullname: str
    namespace: str

    def __repr__(self) -> str:
        namespace = self.namespace + "." if self.namespace else ""
        return f"{self.fullname}: {namespace}{self.__class__.__name__}"


@dataclass
class Variable(Object):
    pass


@dataclass
class All(Object):
    pass


@dataclass
class Dictionary(Object):
    pass


@dataclass
class Import(Object):
    pass


@dataclass
class Iterable(Object):
    pass


@dataclass
class IterableItem(Object):
    pass


@dataclass
class List(Iterable):
    pass


@dataclass
class Tuple(Iterable):
    pass


@dataclass
class Function(Object):
    pass


@dataclass
class DecoratedFunctionObject(Function):
    pass


@dataclass
class CachedFunction(Function):
    pass


@dataclass
class MockedFunction(Object):
    pass


@dataclass
class DecoratedFunction(Function):
    pass


@dataclass
class Lambda(Function):
    pass


@dataclass
class Container(Object):
    pass


@dataclass
class Class(Container):
    pass


@dataclass
class DataClass(Class):
    pass


@dataclass
class Method(Function):
    pass


@dataclass
class LambdaMethod(Method, Function):
    pass


@dataclass
class PropertyMethod(Method):
    pass


@dataclass
class PropertyGet(PropertyMethod):
    pass


@dataclass
class PropertyDel(PropertyMethod):
    pass


@dataclass
class PropertySet(PropertyMethod):
    pass


@dataclass
class Property(Method):
    pass


@dataclass
class ClassMethod(Method):
    pass


@dataclass
class StaticMethod(Method):
    pass


@dataclass
class CachedStaticMethod(Method):
    pass


@dataclass
class CachedClassMethod(Method):
    pass


@dataclass
class DecoratedStaticMethod(Method):
    pass


@dataclass
class DecoratedMethodObject(Method):
    pass


@dataclass
class Enum(Class):
    pass


@dataclass
class EnumAttr(Object):
    pass


@dataclass
class Dynamic(Object):
    pass


@dataclass
class Reference(Object):
    pass


@dataclass
class Module(Object):
    path: Path

    module: Optional["Module"] = field(init=False, default=None)


@dataclass
class Imported(Object):
    pass


@dataclass
class Action:
    obj: Object

    @property
    def name(self) -> str:
        return self.__class__.__name__

    def __repr__(self) -> str:
        return f"{self.name} {self.obj.namespace}{self.obj.__class__.__name__}: {self.obj.fullname}"


@dataclass
class Add(Action):
    pass


@dataclass
class Update(Action):
    pass


@dataclass
class Delete(Action):
    pass


@dataclass
class DeepUpdate(Action):
    pass


@dataclass
class UpdateFrame(Action):
    pass
