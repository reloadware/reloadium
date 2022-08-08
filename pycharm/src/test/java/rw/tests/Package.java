package rw.tests;

import rw.tests.utils.MiscUtils;

import java.io.File;

public class Package {
    static public String[] run(String cmd) throws Exception {
        return MiscUtils.envoRun(cmd, new File("../../package"));
    }
}