package com.bingo

class MsgResponseEntity {

    private final int peerId
    private final String text
    private final int type
    private final String metadata

    String getMetadata() {
        return metadata
    }

    int getPeerId() {
        return peerId
    }

    String getText() {
        return text
    }

    int getType() {
        return type
    }

    MsgResponseEntity(int peerId, String text, int type, String metadata = null) {
        this.peerId = peerId
        this.text = text
        this.type = type
        this.metadata = metadata
    }
}
