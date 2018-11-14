package com.bingo.validator

import com.bingo.utils.ConfigFields
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils

final class ConfigValidator {

    @CompileStatic
    final static void validate(final Properties configProps) {
        assert !StringUtils.isEmpty(configProps[ConfigFields.BOT_VK_USER_ID]): "config field ${ConfigFields.BOT_VK_USER_ID} should be not empty"
        assert !StringUtils.isEmpty(configProps[ConfigFields.BOT_VK_USER_ACCESS_TOKEN]): "config field ${ConfigFields.BOT_VK_USER_ACCESS_TOKEN} should be not empty"
        assert !StringUtils.isEmpty(configProps[ConfigFields.BOT_ADMIN_VK_USER_ID]): "config field ${ConfigFields.BOT_ADMIN_VK_USER_ID} should be not empty"
        assert !StringUtils.isEmpty(configProps[ConfigFields.ALLOW_VK_CHAT_ID]): "config field ${ConfigFields.ALLOW_VK_CHAT_ID} should be not empty"
        assert !StringUtils.isEmpty(configProps[ConfigFields.ALLOW_HANDLERS_LIST_PATH]): "config field ${ConfigFields.ALLOW_HANDLERS_LIST_PATH} should be not empty"
        assert !StringUtils.isEmpty(configProps[ConfigFields.PLUGINS_DIR]): "config field ${ConfigFields.PLUGINS_DIR} should be not empty"
    }
}
