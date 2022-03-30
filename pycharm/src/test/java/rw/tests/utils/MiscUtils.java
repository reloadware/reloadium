package rw.tests.utils;

import rw.config.Config;
import rw.pkg.Architecture;
import rw.pkg.WebPackageManager;
import rw.util.OsType;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class MiscUtils {
    public static void sleep(Float seconds) {
        try {
            Thread.sleep(Math.round(seconds * 1e3));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String[] envoRun(String cmd, File dir) throws Exception {
        String stage = System.getenv("ENVO_STAGE");
        if (stage == null) {
            stage = "local";
        }

        System.out.printf("##### %s #####%n", cmd);

        Process p = Runtime.getRuntime().exec(String.format("envo %s run %s", stage, cmd), null, dir);
        String[] ret = MiscUtils.handleProcess(p);
        return ret;
    }

    public static String[] handleProcess(Process process) throws Exception {
        process.waitFor(20, TimeUnit.SECONDS);
        String[] ret;

        try {
            assert process.exitValue() == 0;
        } catch (AssertionError e) {
            throw new RuntimeException("Command Failed");
        }
        finally {
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            System.out.println("stdout:\n");
            String s = null;
            List<String> output = new ArrayList<String>();

            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
                output.add(s);
            }

            if (!output.isEmpty())
                ret = output.toArray(new String[0]);
            else
                ret = new String[]{};

            System.out.println("stderr:\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        }
        return ret;
    }

    public static void assertInstalled(String version) throws Exception {
        assertThat(Config.get().getPackagesRootDir().exists()).isTrue();

        for (String pythonVersion: Config.get().supportedVersions) {
            File pythonVersionDir = Config.get().getPackagePythonVersionDir(pythonVersion);
            File packageDir = new File(String.valueOf(pythonVersionDir), Config.get().packageName);

            assertThat(packageDir.exists()).isTrue();
            assertThat(pythonVersionDir.exists()).isTrue();

            if(OsType.DETECTED == OsType.Windows) {
                File pythonVersion32Dir = Config.get().getPackagePythonVersionDir(pythonVersion, Architecture.x86);
                assertThat(pythonVersion32Dir.exists()).isTrue();
            }
        }
        Path currentVersionFile = Paths.get(String.valueOf(Config.get().getPackagesRootDir()), "version.txt");
        Files.readString(currentVersionFile);
    }

    public static void createVenv(File dir) throws Exception {
        Process p = Runtime.getRuntime().exec(String.format("python -m venv %s", dir.getName()), null,
                new File(dir.getParent()));
        String[] ret = MiscUtils.handleProcess(p);
    }
}
