package dev.tricked.subnauticraft.features

import dev.tricked.subnauticraft.Utils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.event.player.PlayerPacketEvent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.network.packet.client.play.ClientPlayerPositionPacket
import net.minestom.server.tag.Tag

object Oxygen {
    private val durationTag = Tag.Integer("duration")
    private val durationLeft = Tag.Integer("durationLeft")
    private val swimmingSince = Tag.Long("swimmingSince")

    private fun createBreather(duration: Int): ItemStack {
        val breather= Utils.createItem(Material.GOLDEN_CHESTPLATE, Component.text("Breather", NamedTextColor.GOLD), Component.text("Total Duration: $duration"),4)
        breather.setTag(durationLeft, duration);
        breather.setTag(durationTag, duration)
        breather.setTag(swimmingSince, -1)
        return breather.build()
    }

    val events = EventNode.all("oxygen")
        .addListener(PlayerLoginEvent::class.java) { event: PlayerLoginEvent ->
            event.player.inventory.addItemStack(createBreather(60))
            event.player.inventory.addItemStack(createBreather(5))
            event.player.inventory.addItemStack(createBreather(600))
        }
        .addListener(PlayerPacketEvent::class.java) { event: PlayerPacketEvent ->
            val packet = event.packet
            val player = event.player
            if (packet is ClientPlayerPositionPacket) {
                val inventory = player.inventory
                val seconds = inventory.chestplate.getTag(durationTag)
                val since = inventory.chestplate.getTag(swimmingSince)
                val swimming = player.instance.getBlock(player.position).isLiquid

                if (swimming) {
                    if (since == -1L) inventory.chestplate =
                        inventory.chestplate.withTag(swimmingSince, System.currentTimeMillis())

                    if (seconds == null || System.currentTimeMillis() - since > (seconds * 1000)) {
                        player.entityMeta.airTicks = 0
                    } else {
                        val remainingAirTicks = (seconds * 20 - (System.currentTimeMillis() - since) / 50).toInt()
                        player.entityMeta.airTicks = remainingAirTicks.coerceAtLeast(0)
                        player.level = (remainingAirTicks / 20).coerceAtLeast(0)
                    }
                } else {
                    inventory.chestplate = inventory.chestplate.withTag(swimmingSince, -1)
                }
            }
        }
}