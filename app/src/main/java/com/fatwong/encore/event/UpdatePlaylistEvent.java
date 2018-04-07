package com.fatwong.encore.event;

public class UpdatePlaylistEvent {

    private boolean update;

    public UpdatePlaylistEvent(boolean update) {
        this.update = update;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

}
