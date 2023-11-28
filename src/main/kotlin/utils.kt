package dev.tricked.subnauticraft

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import java.util.UUID

object Utils {
    private val idTag = Tag.String("id")
    private  val weightTag = Tag.Integer("weight")
    fun createItem(material: Material, name: Component, lore: Component = Component.empty(), weight: Int = 1): ItemStack.Builder {
        val item = ItemStack.builder(material).displayName(name).lore(lore,
            Component.text("Weight $weight", NamedTextColor.GRAY)
        )
        item.setTag(idTag, UUID.randomUUID().toString())
        item.setTag(weightTag, weight)
        return item
    }
}