package com.bingo

interface GeneralPluginHandler {
    List<MsgResponseEntity> process(final Map<String, List<MsgEntity>> msgEntitiesMap, Closure bingoApiInteropClosure)

    String getName()
}