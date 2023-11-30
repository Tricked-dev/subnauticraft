package dev.tricked.subnauticraft.features

import dev.tricked.subnauticraft.Utils
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerPacketEvent
import net.minestom.server.event.player.PlayerSwapItemEvent

object Pickup {
    val events = EventNode.all("pickup").addListener(PlayerSwapItemEvent::class.java) { event ->

        val player = event.player;
        val playerPos = player.position.add(0.0, 1.6, 0.0)
        val res = Utils.raycastEntity(
            player.instance,
            playerPos,
            player.position.direction(),
            4.0,
            hitFilter = { it.hasTag(Utils.pickupableTag) },
            )
        if (res != null) {
            val entity = res.first;
            if(entity.entityType.name()  == "minecraft:horse") {
                event.player.food = (event.player.food + 1).coerceAtMost(20);
                entity.remove()
            }

            if(entity.entityType.name()  == "minecraft:cat") {
                event.player.food = (event.player.food + 1).coerceAtMost(20);
                entity.remove()
            }

        }
    }

}