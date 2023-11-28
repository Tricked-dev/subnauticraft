package dev.tricked.subnauticraft.features

import dev.tricked.subnauticraft.Utils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.effects.Effects
import net.minestom.server.entity.Player
import net.minestom.server.event.EventNode
import net.minestom.server.event.instance.InstanceRegisterEvent
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.network.packet.server.play.EffectPacket
import net.minestom.server.tag.Tag
import net.minestom.server.utils.PacketUtils


object AcidMushroom {
    val durabilityLeft = Tag.Integer("durabilityLeft").defaultValue(3)
    val events = EventNode.all("mushroom")
        .addListener(InstanceRegisterEvent::class.java) {event ->

        }
        .addListener(PlayerBlockInteractEvent::class.java) {event:PlayerBlockInteractEvent->
            println("Event! ${event.block} ${event.block.name()}")
            if (event.block.name() == "minecraft:tube_coral")  {
                val value = event.block.getTag(durabilityLeft)
                if(value == 1) {
                    event.player.instance.breakBlock( event.player, event.blockPosition, BlockFace.TOP)
                    event.player.instance.setBlock(event.blockPosition, Block.WATER)

                } else {
                    val block = event.block.withTag(durabilityLeft, value - 1)
                    event.player.instance.setBlock(event.blockPosition, block)
                }
                event.player.inventory.addItemStack(
                    Utils.createItem(Material.GLOW_BERRIES, Component.text("Acid Mushroom",
                        NamedTextColor.DARK_PURPLE
                    )).build()
                )
            }
        }
}