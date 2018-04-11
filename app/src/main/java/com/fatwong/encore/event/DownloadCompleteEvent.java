package com.fatwong.encore.event;

public class DownloadCompleteEvent {

    private boolean finished;

    public DownloadCompleteEvent(boolean paramBoolean) {
        this.finished = paramBoolean;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public void setFinished(boolean paramBoolean) {
        this.finished = paramBoolean;
    }
}
