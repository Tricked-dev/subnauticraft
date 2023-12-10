package dev.tricked.subnauticraft.features

import dev.tricked.subnauticraft.Item
import dev.tricked.subnauticraft.Items
import dev.tricked.subnauticraft.Utils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerPacketEvent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.network.packet.client.play.ClientPlayerPositionPacket
import net.minestom.server.tag.Tag

val durationLeft = Tag.Integer("durationLeft")
val swimmingSince = Tag.Long("swimmingSince")

abstract class Tank : Item() {
    abstract val capacity: Int;
    override val weight = 4

    override fun create(): ItemStack {
        return Utils.createItem(
            material,
            name,
            arrayOf(*lore, Component.text("Capacity: $capacity")),
            weight
        ).meta { meta ->
            meta.set(durationLeft, capacity)
            meta.set(swimmingSince, -1)
        }.build()
    }
}

object StandardTank : Tank() {
    override val material = Material.LEATHER_CHESTPLATE
    override val name = Component.text("Standard O₂ Tank", NamedTextColor.GOLD)
    override val lore = arrayOf(Component.text("O2 mix. Compressed breathable air."))
    override val id = "tank"
    override val capacity = 30
}

object HighCapacityTank : Tank() {
    override val material = Material.IRON_CHESTPLATE
    override val name = Component.text("High Capacity O₂ Tank", NamedTextColor.GOLD)
    override val lore = arrayOf(Component.text("O2 mix. Highly Compressed breathable air."))
    override val id = "doubletank"
    override val capacity = 90
}

object LightWeightHighCapacityTank : Tank() {
    override val material = Material.CHAINMAIL_CHESTPLATE
    override val name = Component.text("Light Weight O² Tank", NamedTextColor.GOLD)
    override val lore = arrayOf(Component.text("O2 mix. Lightweight breathable air."))
    override val id = "plastictank"
    override val capacity = 90
}

object UltraHighCapacityTank : Tank() {
    override val material = Material.GOLDEN_CHESTPLATE
    override val name = Component.text("Ultra High Capacity O² Tank", NamedTextColor.GOLD)
    override val lore = arrayOf(Component.text("Additional air capacity."))
    override val id = "highcapacitytank"
    override val capacity = 180
}

object Oxygen {
    val events = EventNode.all("oxygen")
        .addListener(PlayerPacketEvent::class.java) { event: PlayerPacketEvent ->
            val packet = event.packet
            val player = event.player
            if (packet is ClientPlayerPositionPacket) {
                val inventory = player.inventory
                if (inventory.chestplate.isAir) return@addListener
                val tank = Items.fromMaterial(inventory.chestplate.material())
                if (tank !is Tank) return@addListener
                val seconds = tank.capacity;
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
                    player.level = 0
                }
            }
        }
}