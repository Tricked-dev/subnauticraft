package dev.tricked.subnauticraft.features

import net.minestom.server.coordinate.Pos
import net.minestom.server.event.EventNode
import net.minestom.server.event.instance.InstanceRegisterEvent
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.instance.block.Block

object AcidMushroom {
    val events = EventNode.all("mushroom")
        .addListener(InstanceRegisterEvent::class.java) {event ->
                event.instance.setBlock(Pos(2.0,30.0,2.0), Block.TUBE_CORAL)

        }
        .addListener(PlayerBlockInteractEvent::class.java) {event:PlayerBlockInteractEvent->
            if (event.block== Block.TUBE_CORAL)  {

            }
        }
}