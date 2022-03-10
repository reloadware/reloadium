from typing import List, Type


class EnviumError(Exception):
    pass


class RedefinedVarError(EnviumError):
    def __init__(self, var_name: str) -> None:
        self.var_name = var_name
        msg = f'Variable "{var_name}" is redefined'
        super().__init__(msg)


class WrongTypeError(EnviumError):
    def __init__(self, var_name: str, type_: Type, got_type: Type) -> None:
        self.var_name = var_name
        self.type_ = type_
        self.got_type = got_type

        msg = f'Expected type "{type_.__name__}" for var "{var_name}" got "{got_type.__name__}"'
        super().__init__(msg)


class NoTypeError(EnviumError):
    def __init__(self, var_name: str) -> None:
        self.var_name = var_name
        msg = f'Type annotation for var "{var_name}" is missing'
        super().__init__(msg)


class NoValueError(EnviumError):
    def __init__(self, var_name: str, type_: Type) -> None:
        self.var_name = var_name
        self.type_ = type_
        msg = f'Expected value of type "{type_.__name__}" for var "{var_name}" not None'
        super().__init__(msg)


class ComputedVarError(EnviumError):
    def __init__(self, var_name: str, exception: Exception) -> None:
        self.var_name = var_name
        msg = f'During computing "{var_name}" following error occured: \n{repr(exception)}'
        super().__init__(msg)


class UndefinedVarError(EnviumError):
    def __init__(self, parent_fullname: str, var_name: str) -> None:
        self.var_name = var_name
        msg = f'Var grup "{parent_fullname}" does not have var "{var_name}"'
        super().__init__(msg)


class ValidationErrors(EnviumError):
    errors: List[EnviumError]

    def __init__(self, errors: List[EnviumError]) -> None:
        self.errors = errors
        msg = "\n".join(repr(e) for e in self.errors)
        super().__init__(msg)
