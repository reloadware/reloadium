package rw.tests;

import rw.tests.utils.MiscUtils;

import java.io.File;
import java.io.IOException;

public class Product {
    static public String[] run(String cmd) throws Exception {
        return MiscUtils.envoRun(cmd, new File("../../product"));
    }
}