package io.github.madmas.backupanalyzerfx

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import org.kordamp.ikonli.javafx.FontIcon
import java.io.File
import java.net.URL

class BackupAnalyzerApp : Application() {
  val f: URL? = javaClass.getResource("size-filename-small.log")
  val map: MutableMap<String, String> = mutableMapOf()
  val treeView: TreeView<String> = TreeView<String>()
  val rootItem: TreeItem<String> = TreeItem<String>("/")

  override fun start(stage: Stage) {
    File(f!!.file).forEachLine {
      val size = it.split(" ")[0].toLong()
      val path = it.split(" ")[1]
      map.put(Utils.byteCountOf(size), path)
    }

    treeView.isShowRoot = true
    treeView.root = rootItem

    rootItem.isExpanded = true
    rootItem.graphic = FontIcon()

    map.forEach {
      val entry = it
      val subFolders = it.value.split("/")
      subFolders.forEach { folder ->
        val item = TreeItem("${folder} (${entry.key})")
        item.graphic = FontIcon()
        for (j in 1..4) {
          val subItem = TreeItem("Sub item ${it.value}: $j")
          item.children.add(subItem)
        }
        rootItem.children.add(item)
      }
    }

    val tree = TreeView(rootItem)
    val root = StackPane()
    root.children.add(tree)

    val scene = Scene(root, 600.0, 500.0)
    val style = javaClass.getResource("css/style.css")
    scene.getStylesheets().add(style!!.toString());

    stage.title = "BackupAnalyzerFX"
    stage.scene = scene
    stage.show()
  }
}

fun main() {
  Application.launch(BackupAnalyzerApp::class.java)
}
