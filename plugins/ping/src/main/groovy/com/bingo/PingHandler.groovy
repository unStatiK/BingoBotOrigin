package com.bingo

public class PingHandler implements GeneralPluginHandler {

    private static final String handlerKey = '!ping'

    @Override
    List<MsgResponseEntity> process(Map<String, List<MsgEntity>> commands) {
        List<MsgResponseEntity> handlerReponses = []
        if (commands[handlerKey] != null) {
            def commandEntity = commands[handlerKey]
            commandEntity.each {
                def additionalResponses = ""
                it.msgEntry.text != null || it.msgEntry.text.trim() != "" ? additionalResponses = "what mean ${it.msgEntry.text}?" : null

                //check msg type from peerId
                int msgTypeValue = 2000000000 - it.msgEntry.peerId > 0 ? MsgType.PRIVATE.type : MsgType.CHAT.type

                def responseEntity = new MsgResponseEntity(it.msgEntry.peerId, "@${it.msgEntry.peerId} pong!<br />${additionalResponses}", msgTypeValue)
                handlerReponses << responseEntity
            }
        }
        handlerReponses
    }
}
