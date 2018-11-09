package com.bingo.service

import com.bingo.MsgEntity
import com.bingo.MsgFeedEntry
import groovy.transform.CompileStatic
import org.apache.commons.lang3.tuple.Pair
import org.springframework.util.StringUtils

@CompileStatic
class MsgFeedProcessorService {

    static Map<String, List<MsgEntity>> tokenize(final List<MsgFeedEntry> msgFeedEntryList) {
        Map<String, List<MsgEntity>> msgEntitiesMap = [:]
        msgFeedEntryList.each {
            def currentText = it.text
            def commandPair = getCommandAndText(currentText)
            if (!StringUtils.isEmpty(commandPair.left)) {
                def msgEntitiesList = msgEntitiesMap.get(commandPair.left)
                it.setText(commandPair.right as String)
                int commandVersion = getCommandVersion(commandPair.left as String)
                if (msgEntitiesList != null) {
                    msgEntitiesList.add(new MsgEntity(it, commandVersion))
                    msgEntitiesMap.put(commandPair.left as String, msgEntitiesList)
                } else {
                    msgEntitiesMap.put(commandPair.left as String, [new MsgEntity(it, commandVersion)])
                }
            }
        }
        msgEntitiesMap
    }

    private static Pair getCommandAndText(String commandText) {
        def command = ""
        def text = ""
        commandText = commandText.trim().toLowerCase()
        if (commandText.startsWith("!")) {
            int endIndex = commandText.indexOf(" ")
            if (endIndex == -1) {
                command = commandText
            } else {
                command = commandText.substring(0, endIndex)
                text = commandText.substring(endIndex + 1, commandText.length())
            }
        }
        Pair.of(command, text)
    }

    private static int getCommandVersion(String command) {
        command = command.trim()
        if (command.startsWith("!")) {
            return 1
        }
        return -1
    }
}
