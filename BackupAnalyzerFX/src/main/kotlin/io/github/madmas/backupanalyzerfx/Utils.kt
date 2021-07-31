package io.github.madmas.backupanalyzerfx

import javafx.beans.property.ReadOnlyProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.css.PseudoClass
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView

object Utils {
  fun byteCountOf(bytes: Long) = when {
    bytes == Long.MIN_VALUE || bytes < 0 -> "N/A"
    bytes < 1024L -> "$bytes B"
    bytes <= 0xfffccccccccccccL shr 40 -> "%.1f KB".format(bytes.toDouble() / (0x1 shl 10))
    bytes <= 0xfffccccccccccccL shr 30 -> "%.1f MB".format(bytes.toDouble() / (0x1 shl 20))
    bytes <= 0xfffccccccccccccL shr 20 -> "%.1f GB".format(bytes.toDouble() / (0x1 shl 30))
    bytes <= 0xfffccccccccccccL shr 10 -> "%.1f TB".format(bytes.toDouble() / (0x1 shl 40))
    bytes <= 0xfffccccccccccccL -> "%.1f PB".format((bytes shr 10).toDouble() / (0x1 shl 40))
    else -> "%.1f EiB".format((bytes shr 20).toDouble() / (0x1 shl 40))
  }

  fun expandedChangeListener(treeView: TreeView<String>) = ChangeListener {
      obs: ObservableValue<out Boolean>, wasExpanded: Boolean?, isExpanded: Boolean ->
      if (isExpanded) {
        val expandedProperty: ReadOnlyProperty<*> = obs as ReadOnlyProperty<*>
        val itemExpanded: Any = expandedProperty.getBean()
        for (item in treeView.getRoot().getChildren()) {
          if (item !== itemExpanded) {
            item.isExpanded = false
          }
        }
      }
    }

  fun cellFactoryCallback() = { tv: TreeView<String?>? ->
    val subElementPseudoClass = PseudoClass.getPseudoClass("sub-tree-item")
    val cell: TreeCell<String> = object : TreeCell<String>() {
      override fun updateItem(item: String, empty: Boolean) {
        super.updateItem(item, empty)
        disclosureNode = null
        if (empty) {
          text = ""
          graphic = null
        } else {
          text = item
        }
      }
    }
    cell.treeItemProperty().addListener { obs: ObservableValue<out TreeItem<String>?>?,
                                          oldTreeItem: TreeItem<String>?,
                                          newTreeItem: TreeItem<String>? ->
      cell.pseudoClassStateChanged(
        subElementPseudoClass,
        newTreeItem != null && newTreeItem.parent !== cell.treeView.root
      )
    }
    cell
  }
}
