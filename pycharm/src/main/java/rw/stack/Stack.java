package rw.stack;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;
import rw.session.events.FrameData;
import rw.session.events.StackUpdate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Stack {
    private final Map<Long, List<Frame>> content;  // thread_id to frames
    private final Map<Long, Frame> frameIdToFrame;
    private final Map<File, List<Frame>> pathToFrames;

    Project project;

    public Stack(Project project) {
        this.project = project;
        this.content = new HashMap<>();
        this.pathToFrames = new HashMap<>();
        this.frameIdToFrame = new HashMap<>();
    }

    public void onStackUpdateEvent(StackUpdate event) {
        this.content.clear();
        this.pathToFrames.clear();

        for (Map.Entry<Long, List<FrameData>> entry : event.getContent().entrySet()) {
            List<Frame> frames = new ArrayList<>();
            Long threadId = entry.getKey();

            for (FrameData f : entry.getValue()) {
                Frame frame = this.frameIdToFrame.getOrDefault(f.getFrameId(),
                        new Frame(f.getFrameId(),
                                threadId,
                                f.getLocalPath(),
                                f.getBodyLineno(),
                                f.getEndLineno(),
                                f.getHandlerLineno(),
                                f.getFullname()));
                frames.add(frame);

                this.pathToFrames.putIfAbsent(f.getLocalPath(), new ArrayList<>());
                this.pathToFrames.get(f.getLocalPath()).add(frame);
                this.frameIdToFrame.putIfAbsent(f.getFrameId(), frame);
            }

            this.content.put(threadId, frames);
        }
    }

    @Nullable
    public List<Frame> getForPath(File file) {
        return this.pathToFrames.get(file);
    }

    @Nullable
    public Frame getById(Long id) {
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
