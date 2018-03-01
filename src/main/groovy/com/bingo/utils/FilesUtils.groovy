package com.bingo.utils

import groovy.transform.CompileStatic

final class FilesUtils {

    @CompileStatic
    final public static void CreateFolderIfNeeded(final String path) {
        def folderPath = new File(path)
        if (!folderPath.exists()) {
            folderPath.mkdir()
        }
    }

    @CompileStatic
    final public static void write(final String path, final String content) {
        FileWriter fw = new FileWriter(path)
        BufferedWriter bw = new BufferedWriter(fw)
        bw.write(content)

        if (bw != null) {
            bw.flush()
            bw.close()
        }
    }
}
