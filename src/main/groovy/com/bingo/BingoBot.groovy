package com.bingo

import com.bingo.service.ConfigValidator
import com.bingo.service.VkHelperService
import com.bingo.utils.*
import com.vk.api.sdk.client.TransportClient
import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.httpclient.HttpTransportClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

import java.util.concurrent.atomic.AtomicReference

@Component
class BingoBot {

    private static final Logger log = LoggerFactory.getLogger(BingoBot.class)

    @Autowired
    private VkHelperService vkHelperService

    private static final TransportClient transportClient = HttpTransportClient.getInstance()
    private static final VkApiClient vk = new VkApiClient(transportClient)

    private static UserActor actor

    private AtomicReference<Date> currentStartDate = new AtomicReference<Date>()
    private AtomicReference<Integer> currentMsgFeedTS = new AtomicReference<Integer>()

    private Map longPollServerMap
    private int botAdminVkUserId
    private int botVkUserId
    private int allowVkChatId

    // every 3sec
    @Scheduled(fixedRate = 3000L)
    public void getMsgFeedUpdate() {
        try {
            if (longPollServerMap != null) {
                def msgFeedResponse = vkHelperService.getUpdateMsgFeed(longPollServerMap)

                //force update LONG POLL SERVER MAP
                if (msgFeedResponse['failed'] != null) {
                    longPollServerMap = vkHelperService.getPollServerMap(vk, actor)
                    currentMsgFeedTS.compareAndSet(currentMsgFeedTS.get(), Integer.valueOf(longPollServerMap[LongPollServerMapFields.TS]))
                }

                currentMsgFeedTS.compareAndSet(currentMsgFeedTS.get(), msgFeedResponse[MsgFeedResponseFields.TS])
                longPollServerMap[LongPollServerMapFields.TS] = msgFeedResponse[MsgFeedResponseFields.TS]

                vkHelperService.filterMsgFeed(Arrays.asList(2000000000 + allowVkChatId), botVkUserId, botAdminVkUserId, msgFeedResponse, vk, actor)
            }
        } catch (Exception ex) {
            String date = DateUtils.getDateString("dd-MM-yyyy_HH_mm_ss")
            String path = Constants.TMP_BINGO_FILES_FOLDER + String.format("%s_log_ex.log", date)
            FileWriter fw = new FileWriter(path, false)
            PrintWriter pw = new PrintWriter(fw)
            ex.printStackTrace(pw)
            if (pw != null) {
                pw.flush()
                pw.close()
            }
        }
    }

    public void init(final String configPath) {

        File propertiesFile = new File(configPath)

        if (propertiesFile.exists() && propertiesFile.isFile()) {

            Properties botConfigProps = new Properties()
            propertiesFile.withInputStream {
                botConfigProps.load(it)
            }

            ConfigValidator.validate(botConfigProps)

            botAdminVkUserId = Integer.valueOf(botConfigProps[ConfigFields.BOT_ADMIN_VK_USER_ID]).intValue()
            botVkUserId = Integer.valueOf(botConfigProps[ConfigFields.BOT_VK_USER_ID]).intValue()
            allowVkChatId = Integer.valueOf(botConfigProps[ConfigFields.ALLOW_VK_CHAT_ID]).intValue()

            actor = new UserActor(botVkUserId, botConfigProps[ConfigFields.BOT_VK_USER_ACCESS_TOKEN])
            currentStartDate.set(DateUtils.getCurrentStartDate())
            longPollServerMap = vkHelperService.getPollServerMap(vk, actor)
            currentMsgFeedTS.set(Integer.valueOf(longPollServerMap[LongPollServerMapFields.TS]))

        } else {
            log.error("config file not found!")
        }

        FilesUtils.CreateFolderIfNeeded(Constants.TMP_BINGO_FILES_FOLDER)
    }
}
