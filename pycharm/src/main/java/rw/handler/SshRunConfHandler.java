package rw.handler;

import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.process.BaseProcessHandler;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import rw.audit.RwSentry;

import java.lang.reflect.Method;


public class SshRunConfHandler extends RemoteRunConfHandler {
    public SshRunConfHandler(RunConfiguration runConf) {
        super(runConf);
    }

    public void onProcessStarted(RunContentDescriptor descriptor) {
        super.onProcessStarted(descriptor);
        try {
            BaseProcessHandler<?> processHandler = (BaseProcessHandler<?>) descriptor.getProcessHandler();
            assert processHandler != null;
            Object process = processHandler.getProcess();

            Method addRemoteTunnel = process.getClass().getMethod("addRemoteTunnel", int.class, String.class, int.class);
            Method getSession = process.getClass().getMethod("getSession");

            Object session  = getSession.invoke(process);
            Method getHost = session.getClass().getMethod("getHost");

            String host = (String) getHost.invoke(session);

            addRemoteTunnel.invoke(process, this.session.getPort(), host, this.session.getPort());
        } catch (Exception e) {
            RwSentry.get().captureException(e, true);
        }
    }
}
