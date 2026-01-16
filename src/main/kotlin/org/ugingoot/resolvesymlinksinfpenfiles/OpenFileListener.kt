package org.ugingoot.resolvesymlinksinfpenfiles

import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Paths


class OpenFileListener : FileEditorManagerListener {
    override fun fileOpened(
        source: FileEditorManager,
        file: VirtualFile
    ) {
        val path = Paths.get(file.path).toRealPath().toString()
        if (path == file.path) {
            return
        }

        val realFile = file.fileSystem.findFileByPath(path) ?: return
        val textEditor = source.selectedTextEditor ?: return

        source.runWhenLoaded(textEditor, {
            val pos = textEditor.caretModel.logicalPosition
            source.closeFile(file)
            source.openFile(realFile, true)
            source.selectedTextEditor!!.caretModel.moveToLogicalPosition(pos)
            source.selectedTextEditor!!.scrollingModel.scrollToCaret(ScrollType.CENTER)
        })
    }
}