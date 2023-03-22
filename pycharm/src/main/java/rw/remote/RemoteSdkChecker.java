// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package rw.remote;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.target.TargetEnvironment;
import com.intellij.execution.target.TargetProgressIndicator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.jetbrains.python.run.PythonInterpreterTargetEnvironmentFactory;
import com.jetbrains.python.run.target.HelpersAwareTargetEnvironmentRequest;
import com.jetbrains.python.sdk.PythonSdkType;
import org.jetbrains.annotations.Nullable;
import rw.action.FastDebugWithReloadium;
import rw.audit.RwSentry;
import rw.pkg.PackageManager;
import rw.remote.sftp.SFTPClient;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;


public class RemoteSdkChecker {
    private Map<Sdk, Integer> sdkTries;
    private static final Logger LOGGER = Logger.getInstance(RemoteSdkChecker.class);

    public RemoteSdkChecker() {
        this.sdkTries = new HashMap<>();
        this.checking = false;
    }

    boolean checking;

    public void check() {
        if(this.checking) {
            return;
        }

        try {
            this.checking = true;
            for (Sdk sdk : ProjectJdkTable.getInstance().getSdksOfType(PythonSdkType.getInstance())) {
                if (RemoteUtils.isSdkServerRemote(sdk)) {
                    this.checkRemotePackage(sdk);
                }
            }
        } finally {
            this.checking = false;
        }
    }

    private void checkRemotePackage(Sdk sdk) {
        Project project = this.getProject();

        if (project == null) {
            return;
        }

        if(this.sdkTries.getOrDefault(sdk, 0) >= 3) {
            return;
        }

        LOGGER.info("Checking remote package for " + sdk.getName());

        try {
            HelpersAwareTargetEnvironmentRequest helpersAwareTargetRequest =
                PythonInterpreterTargetEnvironmentFactory.findPythonTargetInterpreter(sdk, project);
            TargetEnvironment targetEnvironment;
            try {
                targetEnvironment = helpersAwareTargetRequest.getTargetEnvironmentRequest().prepareEnvironment(TargetProgressIndicator.EMPTY);
            } catch (ExecutionException ignored) {
                LOGGER.info("Can't connect");
                return;
            }

            Field connectionField = targetEnvironment.getClass().getDeclaredField("connection");
            connectionField.setAccessible(true);

            Object connection = connectionField.get(targetEnvironment);

            Field sshjBackendField = connection.getClass().getDeclaredField("sshjBackend");
            sshjBackendField.setAccessible(true);

            Object ssh = sshjBackendField.get(connection);
            Method newSFTPClient = ssh.getClass().getMethod("newSFTPClient");
            newSFTPClient.setAccessible(true);

            SFTPClient sftp = new SFTPClient(newSFTPClient.invoke(ssh));

            PackageManager packageManager = new PackageManager(new RemoteFileSystem(sftp), new RemoteMachine(targetEnvironment, sftp));
            if (packageManager.shouldInstall()) {
                packageManager.run(new PackageManager.Listener() {
                    @Override
                    public void started() {
                    }

                    @Override
                    public void success() {}

                    @Override
                    public void fail(Exception exception) {
                        sdkTries.put(sdk, sdkTries.getOrDefault(sdk, 0) + 1);
                    }

                    @Override
                    public void cancelled() {
                        sdkTries.put(sdk, sdkTries.getOrDefault(sdk, 0) + 1);
                    }
                }, false);
            }
        }
        catch (Exception ignored) {
        }
    }

    @Nullable
    private Project getProject() {
        Project[] projects = ProjectManager.getInstance().getOpenProjects();

        if (projects.length == 0) {
            return null;
        }

        Project ret = projects[0];
        return ret;
    }
}
