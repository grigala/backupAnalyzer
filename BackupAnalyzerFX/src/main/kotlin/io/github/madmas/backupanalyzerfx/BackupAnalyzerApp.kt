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
  private val f: URL? = javaClass.getResource("size-filename-small.log")
  private val map: MutableMap<ULong, String> = mutableMapOf()
  private val treeView: TreeView<String> = TreeView<String>()
  private val rootItem: TreeItem<String> = TreeItem<String>("/")

  override fun start(stage: Stage) {

    File(f!!.file).forEachLine {
      val size = it.split(" ")[0].toULong()
      val path = it.split(" ")[1]
      map.put(size, path)
    }

    treeView.isShowRoot = true
    treeView.root = rootItem

    rootItem.isExpanded = true
    rootItem.graphic = FontIcon()

    map.forEach {
      addToTheTree(it.value, it.key)
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

  private fun addToTheTree(path: String, size: ULong) {
    var n = rootItem
    for (element: String in path.split("/")) {
      n = addNode(n, element, size)
      // TODO sort children
    }

  }

  private fun addNode(node: TreeItem<String>, folder: String, size: ULong): TreeItem<String> {
    return if (node.children.any { n -> n.value.split(" ")[0] == folder }) {
      val parent = node.children.filter { n -> n.value.split(" ")[0] == folder }[0]
      val old = parent.value.substringAfter("(").substringBefore(")")
      val oldSizeInBytes = Utils.humanReadableToBytes(old)
      val newSize  = oldSizeInBytes + size
      val replacement = Utils.byteCountOf(newSize).first
      parent.value = parent.value.replace("\\(.+?\\)".toRegex(), "($replacement)")
      parent
    } else {
      val newNode = TreeItem("$folder (${Utils.byteCountOf(size).first})")
      newNode.expandedProperty().addListener { _ ->
        run {
          newNode.children.forEach { c ->
            if (c.children.size == 0) {
              c.graphic.styleClass.add("file")
            }
          }
        }
      }
      newNode.graphic = FontIcon()
      node.children.add(newNode)
      newNode
    }
  }
}

fun main() {
  Application.launch(BackupAnalyzerApp::class.java)
}
