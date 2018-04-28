package com.bingo

class MsgEntity {
    private final MsgFeedEntry msgEntry
    private int version

    MsgEntity(MsgFeedEntry msgEntry, int version) {
        this.msgEntry = msgEntry
        this.version = version
    }
}
