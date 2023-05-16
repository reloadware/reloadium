package rw.tests;

import rw.tests.utils.MiscUtils;

import java.io.File;

public class Depot {
    static public String[] run(String cmd) throws Exception {
        return MiscUtils.envoRun(cmd, new File("../../cluster/depot/backend"));
    }
}
