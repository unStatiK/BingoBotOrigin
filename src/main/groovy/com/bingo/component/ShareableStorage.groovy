package com.bingo.component

import com.bingo.GeneralPluginHandler
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

@CompileStatic
@Component
class ShareableStorage {
    private final Map allowsHandlers = [:]
    private final Map bingoApiObjects = [:]

    void allowHandler(String handlerId) {
        if (!allowsHandlers.containsKey(handlerId))
            allowsHandlers.put(handlerId, "")
    }

    boolean share(Object apiObject, String apiObjectId) {
        bingoApiObjects.put(apiObjectId, apiObject)
        true
    }

    Closure getBingoApiInteropMap(GeneralPluginHandler plugin) {
        if (allowsHandlers.containsKey(plugin.getName())) {
            return { bingoApiObjects }
        }
        return {}
    }
}
