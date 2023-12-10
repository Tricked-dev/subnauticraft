package dev.tricked.subnauticraft.features

import dev.tricked.subnauticraft.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.entity.Player
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.event.player.PlayerPacketEvent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.network.packet.client.play.ClientPlayerPositionPacket
import net.minestom.server.tag.Tag
import net.minestom.server.timer.TaskSchedule
import net.minestom.server.utils.inventory.PlayerInventoryUtils
import world.cepi.kstom.item.set

val oxygen = Tag.Integer("oxygen")

abstract class Tank : Item() {
    abstract val capacity: Int
    override val weight = 6
    open val slowness = 20

    override fun create(): ItemStack {
        return Utils.createItem(
            material,
            name,
            lore(capacity),
            weight
        ).meta { meta ->
            meta.set(oxygen, capacity*10)
        }.build()
    }

    fun lore(left: Int): List<Component> {
        return listOf(*lore(), Component.text("Capacity: $left/$capacity"))
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
    override val slowness = 5
}

object UltraHighCapacityTank : Tank() {
    override val material = Material.GOLDEN_CHESTPLATE
    override val name = Component.text("Ultra High Capacity O² Tank", NamedTextColor.GOLD)
    override val lore = arrayOf(Component.text("Additional air capacity."))
    override val id = "highcapacitytank"
    override val capacity = 180
}

fun Player.isLiquid(): Boolean {
    return this.instance.getBlock(this.position).isLiquid
}

object Oxygen {
    val swimmingTag = Tag.Boolean("swimming").defaultValue(false)
    val events = EventNode.all("oxygen")
        .addListener(PlayerMoveEvent::class.java) { event ->
            val player = event.player
            val inventory = player.inventory


            val swimming = player.isLiquid()
            val swimTag = player.getTag(swimmingTag)
            if (swimTag && !swimming) {
                player.inventory.itemStacks.forEachIndexed { index, itemStack ->
                    val item = Items.fromMaterial(itemStack.material())
                    if (item is WaterEventsItem) {
                        val res = item.onLeaveWater(event, itemStack)
                        if (res != null) {
                            inventory.setItemStack(index, res)
                        }
                    }

                }


                player.scheduler().submitTask {
                    if(player.isLiquid()) return@submitTask TaskSchedule.stop()
                    player.inventory.itemStacks.forEachIndexed { index, itemStack ->
                        val item = Items.fromMaterial(itemStack.material())
                        if (item is Tank) {
                            val newAirTicks = ((itemStack.getTag(oxygen) ?: 0  )+ 8).coerceAtMost(item.capacity *10)
                            player.inventory.setItemStack(
                                index,
                                itemStack.withLore(item.lore((newAirTicks/10))).withTag(oxygen, newAirTicks)
                            )

                        }
                    }
                    return@submitTask TaskSchedule.millis(200)
                }
            } else if (!swimTag && swimming) {
                player.inventory.itemStacks.forEachIndexed { index, itemStack ->
                    val item = Items.fromMaterial(itemStack.material())
                    if (item is WaterEventsItem) {
                        val res = item.onEnterWater(event, itemStack)
                        if (res != null) {
                            inventory.setItemStack(index, res)
                        }
                    }
                }

                player.scheduler().submitTask {
                    if(!player.isLiquid()) return@submitTask TaskSchedule.stop()
                    player.inventory.itemStacks.forEachIndexed { index, itemStack ->
                        if(index != PlayerInventoryUtils.CHESTPLATE_SLOT) return@forEachIndexed
                        val item = Items.fromMaterial(itemStack.material())
                        if (item is Tank) {
                            val newAirTicks = ((itemStack.getTag(oxygen) ?: 0  ) - 2).coerceAtLeast(0)
                            player.inventory.setItemStack(
                                index,
                                itemStack.withLore(item.lore((newAirTicks/10))).withTag(oxygen, newAirTicks)
                            )
                        }
                    }
                    return@submitTask TaskSchedule.millis(200)
                }
            }
            player.setTag(swimmingTag, swimming)
        }
}