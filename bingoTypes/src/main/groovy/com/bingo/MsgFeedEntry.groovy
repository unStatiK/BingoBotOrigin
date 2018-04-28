package com.bingo

class MsgFeedEntry {

    private final int msgId
    private final int peerId
    private final int date
    private String text

    void setText(String text) {
        this.text = text
    }

    int getMsgId() {
        return msgId
    }

    int getPeerId() {
        return peerId
    }

    int getDate() {
        return date
    }

    String getText() {
        return text
    }

    public MsgFeedEntry(int msgId, int peerId, int date, String text) {
        this.msgId = msgId
        this.peerId = peerId
        this.date = date
        this.text = text
    }

}
