package com.bingo.service

import com.bingo.MsgFeedEntry
import com.bingo.utils.LongPollServerMapFields
import com.bingo.utils.MsgFeedResponseFields
import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.objects.messages.LongpollParams
import com.vk.api.sdk.objects.messages.responses.GetByIdResponse
import com.vk.api.sdk.objects.photos.Photo
import com.vk.api.sdk.objects.photos.PhotoUpload
import com.vk.api.sdk.objects.users.UserXtrCounters
import com.vk.api.sdk.queries.users.UserField
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import org.apache.commons.lang3.RandomStringUtils
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.ContentBody
import org.apache.http.entity.mime.content.FileBody
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.springframework.stereotype.Service

import java.nio.charset.StandardCharsets

@Service
@CompileStatic
final class VkHelperService {

    private static final JsonSlurper slurper = new JsonSlurper()
    private static final CloseableHttpClient httpClient = HttpClients.createDefault()

    @CompileStatic
    final Map getPollServerMap(final VkApiClient vk, final UserActor actor) {

        LongpollParams response = vk.messages().getLongPollServer(actor)
                .lpVersion(2)
                .needPts(true)
                .execute()

        // when key changed , pls update LongPollServerMapFields values!!!
        return [key   : response.key,
                server: response.server,
                ts    : response.ts,
                pts   : response.pts]
    }

    @CompileStatic
    final Map getUpdateMsgFeed(final Map longPollServerMap) {
        final HttpGet someHttpGet = new HttpGet("https://" + longPollServerMap[LongPollServerMapFields.SERVER])

        final URI uri = new URIBuilder(someHttpGet.getURI())
                .addParameter("act", "a_check")
                .addParameter("key", longPollServerMap[LongPollServerMapFields.KEY] as String)
                .addParameter("wait", "1")
                .addParameter("mode", "8")
                .addParameter("version", "2")
                .addParameter("ts", longPollServerMap[LongPollServerMapFields.TS] as String)
                .build()

        ((HttpRequestBase) someHttpGet).setURI(uri)

        CloseableHttpResponse response = httpClient.execute(someHttpGet)
        String responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.name())

        return slurper.parseText(responseString) as Map
    }

    // example
    // [4,2105994,561,123456,1496404246,"hello",{"attach1_type":"photo","attach1":"123456_417336473","attach2_type":"audio","attach2":"123456_456239018","title":" ... "}]
    @CompileStatic
    final List<MsgFeedEntry> filterMsgFeed(
            final List<Integer> allowPeerIds,
            final int botUserId,
            final int botAdminUserId,
            final Map msgFeedResponse, final VkApiClient vk, final UserActor actor) {

        def filteredMsgs = []
        msgFeedResponse[MsgFeedResponseFields.UPDATES].each {
            List item ->
                if (item != null && item.size() > 0) {
                    //todo remove hardcodes indexes to enum
                    if (item.size() >= 6 && item[0] == 4) {
                        int msgId = item[1]
                        int peerId = item[3]
                        int date = item[4]
                        String text = item[5]
                        text = text.trim()

                        if (peerId == botAdminUserId || allowPeerIds.contains(peerId)) {
                            filteredMsgs.add(new MsgFeedEntry(msgId, peerId, date, text))
                        }
                    }
                }
        }

        filteredMsgs
    }

    @CompileStatic
    final void sendTextMsg(
            final VkApiClient vk, final UserActor actor, final int targetId, final int peerId, final String text) {
        vk.messages().send(actor)
                .userId(targetId)
                .randomId(Integer.valueOf(RandomStringUtils.randomNumeric(5)))
                .peerId(peerId)
                .message(text)
                .execute()
    }

    @CompileStatic
    final void sendChatTextMsg(
            final VkApiClient vk, final UserActor actor, final int peerId, final String text) {
        vk.messages().send(actor)
                .randomId(Integer.valueOf(RandomStringUtils.randomNumeric(5)))
                .peerId(peerId)
                .chatId(peerId - 2000000000)
                .message(text)
                .execute()
    }

    @CompileStatic
    final void sendTextMsgWithForwardedMsgs(
            final VkApiClient vk,
            final UserActor actor,
            final int targetId, final int peerId, final String text, final List<String> forwardedMsgIds) {
        vk.messages().send(actor)
                .userId(targetId)
                .randomId(Integer.valueOf(RandomStringUtils.randomNumeric(5)))
                .peerId(peerId)
                .message(text)
                .forwardMessages(forwardedMsgIds)
                .execute()
    }

    @CompileStatic
    final void sendChatTextMsgWithForwardedMsgs(
            final VkApiClient vk,
            final UserActor actor, final int peerId, final String text, final List<String> forwardedMsgIds) {
        vk.messages().send(actor)
                .randomId(Integer.valueOf(RandomStringUtils.randomNumeric(5)))
                .peerId(peerId)
                .chatId(peerId - 2000000000)
                .message(text)
                .forwardMessages(forwardedMsgIds)
                .execute()
    }

    @CompileStatic
    final void sendTextMsgWithForwardedMsgsAndPhoto(
            final VkApiClient vk,
            final UserActor actor,
            final int targetId,
            final int peerId, final String text, final List<String> forwardedMsgIds, final List<Integer> photoIds) {
        vk.messages().send(actor)
                .userId(targetId)
                .randomId(Integer.valueOf(RandomStringUtils.randomNumeric(5)))
                .peerId(peerId)
                .message(text)
                .forwardMessages(forwardedMsgIds)
                .attachment(convertToPhotosStrings(peerId, photoIds))
                .execute()
    }

    @CompileStatic
    final void sendChatTextMsgWithForwardedMsgsAndPhoto(
            final VkApiClient vk,
            final UserActor actor,
            final int peerId, final String text, final List<String> forwardedMsgIds, final List<Integer> photoIds) {
        vk.messages().send(actor)
                .randomId(Integer.valueOf(RandomStringUtils.randomNumeric(5)))
                .peerId(peerId)
                .chatId(peerId - 2000000000)
                .message(text)
                .forwardMessages(forwardedMsgIds)
                .attachment(convertToPhotosStrings(actor.id, photoIds))
                .execute()
    }

    @CompileStatic
    final void sendTextMsgWithPhoto(
            final VkApiClient vk,
            final UserActor actor,
            final int peerId, final String text, final List<Integer> photoIds) {
        vk.messages().send(actor)
                .randomId(Integer.valueOf(RandomStringUtils.randomNumeric(5)))
                .peerId(peerId)
                .message(text)
                .attachment(convertToPhotosStrings(actor.id, photoIds))
                .execute()
    }

    @CompileStatic
    final void sendChatTextMsgWithPhoto(
            final VkApiClient vk,
            final UserActor actor,
            final int peerId, final String text, final List<Integer> photoIds) {
        vk.messages().send(actor)
                .randomId(Integer.valueOf(RandomStringUtils.randomNumeric(5)))
                .peerId(peerId)
                .chatId(peerId - 2000000000)
                .message(text)
                .attachment(convertToPhotosStrings(actor.id, photoIds))
                .execute()
    }

    @CompileStatic
    final Map getMsgInfo(final int msgId, final VkApiClient vk, final UserActor actor) {
        Map msgInfo = [:]
        GetByIdResponse response = vk.messages().getById(actor, msgId)
                .previewLength(0)
                .execute()
        response.items.each {
            msgInfo.put('user_id', it.userId)
            msgInfo.put('text', it.body)
            msgInfo.put('out', it.out)
        }

        return msgInfo
    }

    @CompileStatic
    final Photo uploadPhoto(final VkApiClient vk, final UserActor actor, final String photoPath) {
        String serverUrl = getMessagesUploadServerUrl(vk, actor)
        File file = new File(photoPath)
        MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create()
        ContentBody cbFile = new FileBody(file, "image/png")
        mpEntity.addPart("photo", cbFile)

        HttpPost httpPost = new HttpPost(serverUrl)
        httpPost.setEntity(mpEntity.build())
        CloseableHttpResponse response = httpClient.execute(httpPost)
        String responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.name())
        def photoInfo = slurper.parseText(responseString)
        List<Photo> photos = saveMessagePhoto(vk, actor, photoInfo['photo'] as String, photoInfo['server'] as int, photoInfo['hash'] as String)
        return photos[0]
    }

    @CompileStatic
    final private List<Photo> saveMessagePhoto(
            final VkApiClient vk, final UserActor actor, final String photo, final int server, final String hash) {
        return vk.photos().saveMessagesPhoto(actor, photo)
                .server(server)
                .hash(hash)
                .execute() as List<Photo>
    }

    @CompileStatic
    final private String getMessagesUploadServerUrl(final VkApiClient vk, final UserActor actor) {
        PhotoUpload photoUpload = vk.photos().getMessagesUploadServer(actor)
                .execute()
        return photoUpload.uploadUrl
    }

    @CompileStatic
    final List<UserXtrCounters> getUserInfo(final VkApiClient vk, final UserActor actor, final String userIds) {
        return vk.users().get(actor)
                .userIds(userIds)
                .fields(UserField.PHOTO_MAX_ORIG)
                .execute() as List<UserXtrCounters>
    }

    @CompileStatic
    final private List<String> convertToPhotosStrings(final int peerId, final List<Integer> photoIds) {
        final List<String> photos = []
        photoIds.each {
            photos.add(String.format("photo%s_%s", peerId.toString(), it.toString()))
        }
        return photos
    }
}
