package rw.pkg;

import rw.util.Architecture;
import rw.util.OsType;

abstract public class Machine {
    public abstract OsType getOsType();

    public abstract Architecture getArchitecture();
}
