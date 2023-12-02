package dev.tricked.subnauticraft.features

import dev.tricked.subnauticraft.Utils
import dev.tricked.subnauticraft.particle
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerPacketEvent
import net.minestom.server.event.player.PlayerSwapItemEvent
import net.minestom.server.timer.ExecutionType
import net.minestom.server.timer.TaskSchedule
import world.cepi.particle.Particle
import world.cepi.particle.ParticleType
import world.cepi.particle.data.OffsetAndSpeed
import world.cepi.particle.extra.Item

object Pickup {
    val events = EventNode.all("pickup").addListener(PlayerSwapItemEvent::class.java) { event ->
        event.isCancelled = true;
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

            val particle = Particle.particle(
                type = ParticleType.PORTAL,
                count = 200,
                data = OffsetAndSpeed(0.3f, 0.2f, 0.3f, 10f),
            )

            event.player.particle(
                particle,
                entity.position.add(0.0, entity.boundingBox.height()/2, 0.0)
            )


            event.player.scheduler().scheduleTask(Runnable {
                    if(entity.entityType.name()  == "minecraft:horse") {
                        event.player.food = (event.player.food + 1).coerceAtMost(20);
                        entity.remove()
                    }

                    if(entity.entityType.name()  == "minecraft:cat") {
                        event.player.food = (event.player.food + 1).coerceAtMost(20);
                        entity.remove()
                    }
            }, TaskSchedule.tick(50), TaskSchedule.stop())
        }
    }

}