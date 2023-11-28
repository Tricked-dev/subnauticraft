package dev.tricked.subnauticraft.features

import dev.tricked.subnauticraft.Utils
import net.minestom.server.event.EventNode
import net.minestom.server.event.inventory.PlayerInventoryItemChangeEvent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag

object Weight {
    val updatingTag = Tag.Boolean("updating")
    val events = EventNode.all("weight").addListener(PlayerInventoryItemChangeEvent::class.java) { event ->
        if(event.player.getTag(updatingTag) == true) return@addListener
        event.player.setTag(updatingTag, true)
        println("A ${Utils.weightTag}")
        event.player.inventory.itemStacks.withIndex()
            .filter { it.value.material() == Material.BAMBOO }
            .forEach { event.player.inventory.setItemStack(it.index, ItemStack.AIR) }
        val totalWeight = event.player.inventory.itemStacks
            .filter { !it.isAir  && it.material() != Material.BAMBOO}
            .sumOf { it.getTag(Utils.weightTag) }
        val totalCount = event.player.inventory.itemStacks.count { !it.isAir && it.material() != Material.BAMBOO }
        println("Totl count: $totalCount")
        val usableCount = 36;
        val removeFrom = usableCount - totalWeight  + totalCount
        for(i in removeFrom..<usableCount) {
            println("Removing $i")
            event.player.inventory.setItemStack(i, ItemStack.of(Material.BAMBOO))
        }


        println("Total Weight: $totalWeight ${removeFrom}")
        event.player.setTag(updatingTag, false)
    }
}