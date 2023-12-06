package dev.tricked.subnauticraft.features

import dev.tricked.subnauticraft.Utils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.attribute.Attribute
import net.minestom.server.event.EventNode
import net.minestom.server.event.inventory.InventoryCloseEvent
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.item.Enchantment
import net.minestom.server.item.ItemHideFlag
import net.minestom.server.item.Material
import net.minestom.server.potion.Potion
import net.minestom.server.potion.PotionEffect
import net.minestom.server.tag.Tag

object Flippers {
    val flipperSpeedTag = Tag.Integer("flipperSpeed").defaultValue(0)
    val events = EventNode.all("mushroom")
        .addListener(PlayerSpawnEvent::class.java) { event ->
            event.player.inventory.addItemStack(
                Utils.createItem(
                    Material.DIAMOND_BOOTS,
                    Component.text("Flipper Boots", NamedTextColor.DARK_PURPLE),
                    arrayOf(Component.text("Speed: 200%")),
                    1,

                    ).meta { meta ->
                    meta.enchantment(Enchantment.DEPTH_STRIDER, 1)
                    meta.set(flipperSpeedTag, 100)
                    meta.hideFlag(ItemHideFlag.HIDE_ENCHANTS)
                }.build()

            )
            event.player.inventory.addItemStack(
                Utils.createItem(
                    Material.IRON_BOOTS,
                    Component.text("Flipper Boots", NamedTextColor.RED),
                    arrayOf(Component.text("Speed: 130%")),
                    1,

                    ).meta { meta ->
                    meta.enchantment(Enchantment.DEPTH_STRIDER, 1)
                    meta.set(flipperSpeedTag, 30)
                    meta.hideFlag(ItemHideFlag.HIDE_ENCHANTS)
                }.build()

            )
        }.addListener(PlayerMoveEvent::class.java) { event: PlayerMoveEvent ->
            val player = event.player
            val swimming = player.instance.getBlock(player.position).isLiquid
            println("${player.velocity}")
            if (swimming) {
                if (player.hasTag(flipperSpeedTag)) {
                    val speed = player.getTag(flipperSpeedTag)
                    player.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = 0.1f + speed.toFloat() / 1000
                }
            } else {
                player.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = 0.1f
            }

        }
        .addListener(PlayerUseItemEvent::class.java) { event ->
            val boots = event.player.inventory.getItemStack(44)
            if (!boots.isAir && boots.hasTag(flipperSpeedTag)) {
                val bootsSpeed = boots.getTag(flipperSpeedTag)
                event.player.setTag(flipperSpeedTag, bootsSpeed)
                println("boots speed: $bootsSpeed")
            } else {
                event.player.setTag(flipperSpeedTag, 0)
            }
        }
        .addListener(InventoryCloseEvent::class.java) { event ->
            val boots = event.player.inventory.getItemStack(44)
            if (!boots.isAir && boots.hasTag(flipperSpeedTag)) {
                val bootsSpeed = boots.getTag(flipperSpeedTag)
                event.player.setTag(flipperSpeedTag, bootsSpeed)
                println("boots speed: $bootsSpeed")
            } else {
                event.player.setTag(flipperSpeedTag, 0)
            }
        }
}