package com.bingo

enum MsgType {
    PRIVATE(1), CHAT(2)

    private final int type

    MsgType(int type) {
        this.type = type
    }

    int getType() {
        return type
    }
}