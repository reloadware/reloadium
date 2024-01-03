package rw.stack;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;
import rw.handler.RunConfHandler;
import rw.session.events.FrameData;
import rw.session.events.StackUpdate;

import java.io.File;
import java.util.*;

public class Stack {
    private final Map<String, List<Frame>> content;  // thread_id to frames
    private final Map<Long, Frame> frameIdToFrame;
    private final Map<File, List<Frame>> pathToFrames;

    private final Map<String, Thread> threads;
    RunConfHandler handler;

    Project project;

    public Stack(Project project, RunConfHandler handler) {
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

            for (FrameData f : reverseFrames) {
                Frame frame = this.frameIdToFrame.getOrDefault(f.getFrameId(),
                        new Frame(f.getFrameId(),
                                f.getLocalPath(),
                                f.isReloadable()));
                frames.add(0, frame);

                this.pathToFrames.putIfAbsent(f.getLocalPath(), new ArrayList<>());
                this.pathToFrames.get(f.getLocalPath()).add(frame);
                this.frameIdToFrame.putIfAbsent(f.getFrameId(), frame);
            }

            for(int i = 0; i < frames.size(); i++) {
                Frame frame = frames.get(i);
                if (i > 0) {
                    frame.setForwardFrame(frames.get(i - 1));
                }
                if (i < frames.size() - 1) {
                    frame.setBackFrame(frames.get(i + 1));
                }
            }

            this.content.put(threadId, frames);
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

        for (List<Frame> frames : this.content.values()) {
            ret.addAll(frames);
        }

        return ret;
    }
}
