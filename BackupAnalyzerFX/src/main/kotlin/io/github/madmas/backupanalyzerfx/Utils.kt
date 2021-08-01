package io.github.madmas.backupanalyzerfx

import javafx.beans.property.ReadOnlyProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.css.PseudoClass
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView

object Utils {
  fun byteCountOf(bytes: ULong) = when {
    bytes < 1024UL -> Pair("$bytes B", bytes)
    bytes <= 0xfffccccccccccccUL shr 40 -> Pair("%.2f KB".format(bytes.toDouble() / (0x1 shl 10)), bytes)
    bytes <= 0xfffccccccccccccUL shr 30 -> Pair("%.2f MB".format(bytes.toDouble() / (0x1 shl 20)), bytes)
    bytes <= 0xfffccccccccccccUL shr 20 -> Pair("%.2f GB".format(bytes.toDouble() / (0x1 shl 30)), bytes)
    bytes <= 0xfffccccccccccccUL shr 10 -> Pair("%.2f TB".format(bytes.toDouble() / (0x1 shl 40)), bytes)
    bytes <= 0xfffccccccccccccUL -> Pair("%.2f PB".format((bytes shr 10).toDouble() / (0x1 shl 40)), bytes)
    else -> Pair("%.2f EiB".format((bytes shr 20).toDouble() / (0x1 shl 40)), bytes)
  }

  fun humanReadableToBytes(bytes: String) = when {
    bytes.contains(" B") -> "%.0f".format(bytes.split(" ")[0].toDouble()).toULong()
    bytes.contains("KB") -> "%.0f".format(bytes.split(" ")[0].toDouble() * (0x1 shl 10)).toULong()
    bytes.contains("MB") -> "%.0f".format(bytes.split(" ")[0].toDouble() * (0x1 shl 20)).toULong()
    bytes.contains("GB") -> "%.0f".format(bytes.split(" ")[0].toDouble() * (0x1 shl 30)).toULong()
    bytes.contains("TB") -> "%.0f".format(bytes.split(" ")[0].toDouble() * (0x1 shl 40)).toULong()
    // TODO ADD PB AND EIB CONVERSION
    else -> ULong.MAX_VALUE
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
