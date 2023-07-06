package rw.remote;

import com.intellij.execution.Platform;
import com.intellij.execution.target.TargetEnvironment;
import rw.pkg.Machine;
import rw.remote.sftp.FileMode;
import rw.remote.sftp.SFTPClient;
import rw.util.Architecture;
import rw.util.OsType;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


public class RemoteMachine extends Machine {
    TargetEnvironment targetEnvironment;
    SFTPClient sftp;

    public RemoteMachine(TargetEnvironment targetEnvironment, SFTPClient sftp) {
        this.targetEnvironment = targetEnvironment;
        this.sftp = sftp;
    }

    public OsType getOsType() {
        if (this.targetEnvironment.getTargetPlatform().getPlatform() == Platform.WINDOWS) {
            return OsType.Windows;
        } else if (this.targetEnvironment.getTargetPlatform().getPlatform() == Platform.UNIX) {

            boolean isMac = false;
            try {
                isMac = this.sftp.type("/Applications") == FileMode.Type.DIRECTORY;
            } catch (IOException ignored) {
            }

            if (isMac) {
                return OsType.MacOS;
            } else {
                return OsType.Linux;
            }
        }
        return OsType.Linux;
    }

    @Override
    public Architecture getArchitecture() {
        return Architecture.X64;
    }
}
