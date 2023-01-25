package rw.stack;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;
import rw.handler.runConf.BaseRunConfHandler;
import rw.highlights.ErrorHighlightManager;
import rw.session.events.ClearThreadError;
import rw.session.events.FrameData;
import rw.session.events.StackUpdate;
import rw.session.events.ThreadErrorEvent;

import java.io.File;
import java.util.*;

public class Stack {
    private final Map<String, List<Frame>> content;  // thread_id to frames
    private final Map<Long, Frame> frameIdToFrame;
    private final Map<File, List<Frame>> pathToFrames;

    private final Map<String, Thread> threads;
    BaseRunConfHandler handler;

    Project project;

    public Stack(Project project, BaseRunConfHandler handler) {
        this.project = project;
        this.handler = handler;
        this.content = new HashMap<>();
        this.pathToFrames = new HashMap<>();
        this.frameIdToFrame = new HashMap<>();
        this.threads = new HashMap<>();
    }

    public void onStackUpdateEvent(StackUpdate event) {
        this.content.clear();
        this.pathToFrames.clear();

        for (Map.Entry<String, List<FrameData>> entry : event.getContent().entrySet()) {
            List<Frame> frames = new ArrayList<>();
            String threadId = entry.getKey();

            List<FrameData> reverseFrames = new ArrayList<>(entry.getValue());
            Collections.reverse(reverseFrames);

            this.threads.putIfAbsent(threadId, new Thread(threadId));

            Frame backFrame = null;
            for (FrameData f: reverseFrames) {
                Frame frame = this.frameIdToFrame.getOrDefault(f.getFrameId(),
                        new Frame(f.getFrameId(),
                                f.getLocalPath(),
                                f.getFullname(),
                                f.isReloadable(),
                                backFrame));
                frames.add(0, frame);

                backFrame = frame;

                this.pathToFrames.putIfAbsent(f.getLocalPath(), new ArrayList<>());
                this.pathToFrames.get(f.getLocalPath()).add(frame);
                this.frameIdToFrame.putIfAbsent(f.getFrameId(), frame);
            }

            this.content.put(threadId, frames);
        }
    }

    public void onThreadError(ThreadErrorEvent threadErrorEvent) {
        Thread thread = this.threads.get(threadErrorEvent.getThreadId());
        Frame frame = this.getFrameById(threadErrorEvent.getFramenId());
        thread.makeErrored(frame, threadErrorEvent.getMsg(), threadErrorEvent.getLine());
        this.handler.getErrorHighlightManager().add(threadErrorEvent.getLocalPath(),
                threadErrorEvent.getLine(), threadErrorEvent.getMsg());
    }

    public void onClearThreadError(ClearThreadError clearThreadError) {
        Thread thread = this.threads.get(clearThreadError.getThreadId());

        if(thread.getThreadError() != null) {
            this.handler.getErrorHighlightManager().clearFile(thread.getThreadError().getPath());
            thread.clearErrored();
        }
    }

    @Nullable
    public List<Frame> getForPath(File file) {
        return this.pathToFrames.get(file);
    }

    @Nullable
    public Frame getFrameById(Long id) {
        return this.frameIdToFrame.get(id);
    }

    public List<Frame> getAllFrames() {
        List<Frame> ret = new ArrayList<>();

        for (List<Frame> frames: this.content.values()) {
            ret.addAll(frames);
        }

        return ret;
    }
}
