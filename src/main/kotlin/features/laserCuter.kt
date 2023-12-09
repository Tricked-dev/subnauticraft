package dev.tricked.subnauticraft.features

import dev.tricked.subnauticraft.InteractableItem
import dev.tricked.subnauticraft.Item
import dev.tricked.subnauticraft.Utils
import dev.tricked.subnauticraft.particle
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.entity.Player
import net.minestom.server.event.EventNode
import net.minestom.server.event.item.ItemDropEvent
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.event.player.PlayerSwapItemEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import net.minestom.server.timer.TaskSchedule
import world.cepi.particle.Particle
import world.cepi.particle.ParticleType
import world.cepi.particle.data.OffsetAndSpeed
import world.cepi.particle.extra.Dust


object LaserCutter : Item(), InteractableItem {
    override val material = Material.DIAMOND_AXE
    override val name = Component.text("Laser Cutter", NamedTextColor.GREEN)
    override val lore = arrayOf(Component.text("Right click to cut doors"))
    override val weight = 1
    override val id = "lasercutter"

    val cutTag = Tag.Boolean("cut")
    val timeLeft = Tag.Integer("timeLeft").defaultValue(75)

    override fun blockInteract(event: PlayerBlockInteractEvent) {
        if (!event.block.hasTag(cutTag)) return

        val tl = event.block.getTag(timeLeft)

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