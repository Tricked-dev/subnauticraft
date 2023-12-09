package dev.tricked.subnauticraft.features

import net.minestom.server.coordinate.Pos
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerBlockInteractEvent

object Trapdoors {
    val events = EventNode.all("trapdoors").addListener(PlayerBlockInteractEvent::class.java) { event ->
        if (event.block.name().contains("trapdoor")) {
            if (event.player.position.y > event.blockPosition.y()) {
                event.player.teleport(
                    Pos(
                        event.blockPosition.add(0.5, -2.0, 0.5),
                        event.player.position.yaw,
                        event.player.position.pitch
                    )
                )
            } else {
                event.player.teleport(
                    Pos(
                        event.blockPosition.add(0.5, 2.0, 0.5),
                        event.player.position.yaw,
                        event.player.position.pitch
                    )
                )
            }
        }
    }

}