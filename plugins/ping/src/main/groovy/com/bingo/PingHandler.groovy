package com.bingo

import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.objects.messages.responses.GetByIdResponse

public class PingHandler implements GeneralPluginHandler {

    private static final String handlerName = 'simple_ping'
    private static final String handlerCommandKey = '!ping'

    @Override
    String getName() {
        return handlerName
    }

    @Override
    List<MsgResponseEntity> process(Map<String, List<MsgEntity>> commands, Closure bingoApiInteropClosure) {
        List<MsgResponseEntity> handlerReponses = []
        Map bingoApiInteropMap = bingoApiInteropClosure()

        if (commands[handlerCommandKey] != null) {
            def commandEntity = commands[handlerCommandKey]
            commandEntity.each {
                def additionalResponses = ""
                (it.msgEntry.text != null && it.msgEntry.text.trim() != "") ? additionalResponses = "what mean ${it.msgEntry.text}?" : null

                //check msg type from peerId
                int msgTypeValue = 2000000000 - it.msgEntry.peerId > 0 ? MsgType.PRIVATE.type : MsgType.CHAT.type
                Set userIds = getUserIdsFromMsgId(it.msgEntry.msgId,
                        bingoApiInteropMap[BingoApiConstants.APP_VK_CLIENT_OBJECT_ID],
                        bingoApiInteropMap[BingoApiConstants.APP_ACTOR_OBJECT_ID])

                userIds.each {
                    userId ->
                        def responseEntity = new MsgResponseEntity(it.msgEntry.peerId, "@id${userId} pong!<br />${additionalResponses}", msgTypeValue)
                        handlerReponses << responseEntity
                }
            }
        }
        handlerReponses
    }

    final private Set<Integer> getUserIdsFromMsgId(final int msgId, final VkApiClient vk, final UserActor actor) {
        Set userIds = new HashSet()
        if (vk != null && actor != null) {
            GetByIdResponse response = vk.messages().getById(actor, msgId)
                    .previewLength(0)
                    .execute()
            userIds = response.items.collect {
                it.userId
            }.toSet()
        }
        userIds
    }
}
