package rw.pkg.wheel;

import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import rw.consts.Const;
import rw.pkg.FileSystem;
import rw.pkg.Machine;
import rw.util.Architecture;
import rw.util.OsType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class BaseWheel {
    public static String RESOURCE_WHEELS_PATH_ROOT = "META-INF/wheels/";
    protected String input;
    protected OsType osType;
    protected Architecture architecture;
    private String pythonVersion;
    private String filename;
    private String version;

    BaseWheel(String input) {
        this.input = input;
        this.parse();
    }

    protected void parse() {
        // ####### Get python Version
        Matcher pythonVersionMatcher = Pattern.compile("-cp3(.*?)m?-").matcher(input);

        if (!pythonVersionMatcher.find()) {
            throw new RuntimeException("Could not retrieve version from url " + input);
        }
        this.pythonVersion = "3." + pythonVersionMatcher.group(1);

        // ####### Get filename
        Matcher filenameMatcher = Pattern.compile(String.format("%s-.*?\\.whl", Const.get().packageName)).matcher(input);
        if (!filenameMatcher.find()) {
            throw new RuntimeException("Could not retrieve filename from url " + input);
        }
        this.filename = filenameMatcher.group();


        // ####### Get version
        Matcher versionMatcher = Pattern.compile(String.format("%s-(\\d+\\.\\d+\\.\\d+)", Const.get().packageName)).matcher(this.input);
        if (!versionMatcher.find()) {
            throw new RuntimeException("Could not retrieve version from url " + input);
        }
        this.version = versionMatcher.group(1);
    }

    public OsType getOsType() {
        return this.osType;
    }

    public String getInput() {
        return input;
    }

    public Architecture getArchitecture() {
        return this.architecture;
    }

    public String getPythonVersion() {
        return this.pythonVersion;
    }

    public String getFilename() {
        return filename;
    }

    public String getVersion() {
        return this.version;
    }

    public boolean accepts(Machine machine) {
        return this.osType == machine.getOsType() && machine.getArchitecture() == this.architecture;
    }

    public String getDstDirName() {
        return this.pythonVersion;
    }

    public void unpack(FileSystem fs) throws IOException {
        File tmpdir = Files.createTempDirectory(Const.get().packageName).toFile();

        File tmpWheelFile = new File(tmpdir, this.getFilename());

        InputStream wheelFileStream = getClass().getClassLoader().getResourceAsStream(
                RESOURCE_WHEELS_PATH_ROOT + this.getFilename()
        );

        assert wheelFileStream != null;

        FileUtils.copyInputStreamToFile(wheelFileStream, tmpWheelFile);
        wheelFileStream.close();

        File packageVersionDir = new File(fs.getPackagesRootDir().toString(), this.getDstDirName());

        if (packageVersionDir.exists()) {
            try {
                FileUtils.deleteDirectory(packageVersionDir);
            } catch (IOException ignored) {
            }
        }
        packageVersionDir.mkdirs();

        new ZipFile(tmpWheelFile).extractAll(tmpdir.getAbsolutePath());

        fs.putDirectory(tmpdir, packageVersionDir);

        try {
            tmpWheelFile.delete();
        } catch (Exception ignored) {
        }

        try {
            FileUtils.deleteDirectory(tmpdir);
        } catch (Exception ignored) {
        }
    }
}
