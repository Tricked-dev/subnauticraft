package dev.tricked.subnauticraft.features

import dev.tricked.subnauticraft.Utils
import dev.tricked.subnauticraft.particle
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.effects.Effects
import net.minestom.server.entity.Player
import net.minestom.server.event.EventNode
import net.minestom.server.event.instance.InstanceRegisterEvent
import net.minestom.server.event.item.ItemDropEvent
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.network.packet.server.play.EffectPacket
import net.minestom.server.tag.Tag
import net.minestom.server.utils.PacketUtils
import world.cepi.particle.Particle
import world.cepi.particle.ParticleType
import world.cepi.particle.data.OffsetAndSpeed
import world.cepi.particle.extra.BlockState


object AcidMushroom {
    val durabilityLeft = Tag.Integer("durabilityLeft").defaultValue(3)
    val events = EventNode.all("mushroom")
        .addListener(InstanceRegisterEvent::class.java) {event ->

        }
        .addListener(ItemDropEvent::class.java) {event:ItemDropEvent ->
          event.isCancelled = true;
        }
        .addListener(PlayerBlockPlaceEvent::class.java) {event:PlayerBlockPlaceEvent ->
            event.isCancelled=true
        }
        .addListener(PlayerBlockBreakEvent::class.java) {event:PlayerBlockBreakEvent ->
            event.isCancelled=true
        }
        .addListener(PlayerBlockInteractEvent::class.java) {event:PlayerBlockInteractEvent->
            println("Event! ${event.block} ${event.block.name()}")
            if (event.block.name() == "minecraft:tube_coral")  {
                val value = event.block.getTag(durabilityLeft)
                if(value == 1) {
                    event.player.instance.breakBlock( event.player, event.blockPosition, BlockFace.TOP)
                    event.player.instance.setBlock(event.blockPosition, Block.WATER)

                    event.instance.scheduleNextTick {
                        val particle = Particle.particle(
                            type = ParticleType.BLOCK,
                            count = 50,
                            data = OffsetAndSpeed(0f, -0.5f, 0f ,10f),
                            extraData = BlockState(Block.TUBE_CORAL)
                        )

                        event.player.particle(
                            particle,
                            event.blockPosition.add(0.5, 0.5, 0.5)
                        )
                    }

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

            if (event.block.name() == "minecraft:cactus")  {
                val value = event.block.getTag(durabilityLeft)
                if(value == 1) {
                    event.player.instance.breakBlock( event.player, event.blockPosition, BlockFace.TOP)
                    event.player.instance.setBlock(event.blockPosition, Block.AIR)

                    event.instance.scheduleNextTick {
                        val particle = Particle.particle(
                            type = ParticleType.BLOCK,
                            count = 50,
                            data = OffsetAndSpeed(0f, -0.5f, 0f ,10f),
                            extraData = BlockState(Block.CACTUS)
                        )

                        event.player.particle(
                            particle,
                            event.blockPosition.add(0.5, 0.5, 0.5)
                        )
                    }

                } else {
                    val block = event.block.withTag(durabilityLeft, value - 1)
                    event.player.instance.setBlock(event.blockPosition, block)


                }
                event.player.inventory.addItemStack(
                    Utils.createItem(Material.GREEN_DYE, Component.text("Cactus",
                        NamedTextColor.DARK_GREEN
                    ), arrayOf(Component.text("Very kewl", NamedTextColor.RED)), 2).build()
                )
            }

            if (event.block.name() == "minecraft:wheat")  {
                event.player.instance.breakBlock( event.player, event.blockPosition, BlockFace.TOP)
                event.player.instance.setBlock(event.blockPosition, Block.AIR)

                event.player.inventory.addItemStack(
                    Utils.createFoodItem(Material.POTATO, Component.text("Lantern Fruit",
                        NamedTextColor.DARK_GREEN
                    ), arrayOf(Component.text("Very nurishing", NamedTextColor.RED)), 4, 8).build()
                )
            }
        }
}