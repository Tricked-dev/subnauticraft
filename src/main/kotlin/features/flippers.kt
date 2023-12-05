package dev.tricked.subnauticraft.features

import dev.tricked.subnauticraft.Utils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.attribute.Attribute
import net.minestom.server.coordinate.Vec
import net.minestom.server.event.EventNode
import net.minestom.server.event.inventory.InventoryCloseEvent
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.item.Material
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

                    ).build().withTag(flipperSpeedTag, 500)
            )
        }.addListener(PlayerMoveEvent::class.java) { event: PlayerMoveEvent ->
            val player = event.player
            val swimming = player.instance.getBlock(player.position).isLiquid

            if (swimming) {
                if (player.hasTag(flipperSpeedTag)) {
                    val speed = player.getTag(flipperSpeedTag)
                    val newVelocity = player.velocity.mul(
                        Vec(
                            4.0,
//                            (speed.toDouble() / 100),
                            1.0,
//                            (speed.toDouble() / 100)
                            4.0
                        )
                    );
                    if(newVelocity != player.velocity) player.velocity = newVelocity

                    player.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = 0.1f + speed.toFloat() / 1000
                }
            } else {
                player.getAttribute(Attribute.MOVEMENT_SPEED).baseValue = 0.1f
            }

        }
        .addListener(InventoryCloseEvent::class.java) { event ->
            val boots = event.player.inventory.getItemStack(37)
            if (!boots.isAir && boots.hasTag(flipperSpeedTag)) {
                val bootsSpeed = boots.getTag(flipperSpeedTag)
                event.player.setTag(flipperSpeedTag, bootsSpeed)
                event.player.getAttribute(Attribute.FLYING_SPEED).baseValue = 20f
                println("boots speed: $bootsSpeed")
            } else {
                event.player.setTag(flipperSpeedTag, 0)
            }

        }
}