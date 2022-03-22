package rw.pkg.wheel;

import org.apache.maven.artifact.versioning.ComparableVersion;
import rw.config.Config;
import rw.pkg.Architecture;
import rw.util.OsType;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class BaseWheel {
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
        Matcher filenameMatcher = Pattern.compile(String.format("%s-.*?\\.whl", Config.get().packageName)).matcher(input);
        if (!filenameMatcher.find()) {
            throw new RuntimeException("Could not retrieve filename from url " + input);
        }
        this.filename = filenameMatcher.group();


        // ####### Get version
        Matcher versionMatcher = Pattern.compile(String.format("%s-(\\d+\\.\\d+\\.\\d+)", Config.get().packageName)).matcher(this.input);
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

    abstract public Path getPackageDir();
}
