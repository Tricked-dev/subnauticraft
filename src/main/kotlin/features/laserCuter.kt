package dev.tricked.subnauticraft.features

import dev.tricked.subnauticraft.Utils
import dev.tricked.subnauticraft.particle
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import net.minestom.server.timer.TaskSchedule
import world.cepi.particle.Particle
import world.cepi.particle.ParticleType
import world.cepi.particle.data.OffsetAndSpeed
import world.cepi.particle.extra.Dust

object LaserCuter {
    val laserCutterTag = Tag.Boolean("laserCutter")
    val cutTag = Tag.Boolean("cut")
    val timeLeft = Tag.Integer("timeLeft").defaultValue(75)
    val events = EventNode.all("trapdoors")
        .addListener(PlayerLoginEvent::class.java) { event ->
            event.player.inventory.addItemStack(
                with(
                    Utils.createItem(
                        Material.DIAMOND_AXE,
                        Component.text("Laser Cutter", NamedTextColor.GREEN),
                        arrayOf(Component.text("Right click to cut doors")),
                        1
                    )
                ) {
                    setTag(laserCutterTag, true)
                    build()
                }
            )


        }
        .addListener(PlayerBlockInteractEvent::class.java) { event ->
            println("123!")
            event.player.scheduleNextTick {
                event.player.swingMainHand()
                event.player.swingOffHand()
                println("Swinging!")
            }
            if (!event.player.inventory.itemInMainHand.hasTag(laserCutterTag) || !event.block.hasTag(cutTag)) return@addListener

            val tl = event.block.getTag(timeLeft)
            println("tl $tl")

            event.player.swingMainHand()

            if (tl == 0) {
                event.instance.setBlock(
                    event.blockPosition,
                    Block.AIR
                )
            } else {
                val particle = Particle.particle(
                    type = ParticleType.DUST,
                    count = tl / 4,
                    data = OffsetAndSpeed(0.5f, 0.5f, 0.5f, 2f),
                    extraData = Dust(255f, 0f, 0f, 0.8f),
                    longDistance = true
                )
                event.instance.scheduler().scheduleTask(
                    {
                        event.player.particle(
                            particle,
                            event.blockPosition.add(0.5, 0.5, 0.5)
                        )
                    },
                    TaskSchedule.tick((Math.random() * 10 + 1).toInt()),
                    TaskSchedule.stop()
                )
                event.instance.setBlock(
                    event.blockPosition,
                    event.block.withTag(timeLeft, tl - 1)
                )
            }
        }

}