package dev.tricked.subnauticraft.features

import dev.tricked.subnauticraft.Utils
import net.minestom.server.entity.Player
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerEatEvent
import net.minestom.server.event.player.PlayerPreEatEvent
import net.minestom.server.item.ItemStack

object Food {
    val events = EventNode.all("food").addListener(
        PlayerPreEatEvent::class.java
    ) {event ->
        event.isCancelled = true

        val nutrition  = event.itemStack.getTag(Utils.nutishmentTag)

        event.player.food = (event.player.food + nutrition).coerceAtMost(20)
        if (event.hand == Player.Hand.OFF) {
            event.player.inventory.itemInOffHand = ItemStack.AIR
        } else {
            event.player.inventory.itemInMainHand = ItemStack.AIR
        }
    }

}