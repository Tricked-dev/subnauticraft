package dev.tricked.subnauticraft.features

import dev.tricked.subnauticraft.FoodItem
import dev.tricked.subnauticraft.Item
import dev.tricked.subnauticraft.particle
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.event.EventNode
import net.minestom.server.event.instance.InstanceRegisterEvent
import net.minestom.server.event.item.ItemDropEvent
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import world.cepi.particle.Particle
import world.cepi.particle.ParticleType
import world.cepi.particle.data.OffsetAndSpeed
import world.cepi.particle.extra.BlockState


object Cactus : Item() {
    override val material = Material.GREEN_DYE
    override val name = Component.text("Cactus", NamedTextColor.GREEN)
    override val lore = arrayOf(Component.text("Epic Cacus"))
    override val weight = 2
    override val id = "cactus"
}

object AcidMushroom : Item() {
    override val material = Material.GLOW_BERRIES
    override val name = Component.text("Acid Mushroom", NamedTextColor.GREEN)
    override val lore = arrayOf(Component.text("Material used for batteries"))
    override val weight = 1
    override val id = "acidmushroom"
}

object LanternFruit : Item(), FoodItem {
    override val material = Material.POTATO
    override val name = Component.text("Lantern Fruit", NamedTextColor.GREEN)
    override val lore = arrayOf(Component.text("very nurishing"))
    override val weight = 4
    override val id = "lanternfruit"
    override val nurishment = 2
}

object AcidMushroomEvents {
    val durabilityLeft = Tag.Integer("durabilityLeft").defaultValue(3)
    val events = EventNode.all("mushroom")
        .addListener(InstanceRegisterEvent::class.java) { event ->

        }
        .addListener(ItemDropEvent::class.java) { event: ItemDropEvent ->
            event.isCancelled = true;
        }
        .addListener(PlayerBlockPlaceEvent::class.java) { event: PlayerBlockPlaceEvent ->
            event.isCancelled = true
        }
        .addListener(PlayerBlockBreakEvent::class.java) { event: PlayerBlockBreakEvent ->
            event.isCancelled = true
        }
        .addListener(PlayerBlockInteractEvent::class.java) { event: PlayerBlockInteractEvent ->
            if (event.block.name() == "minecraft:tube_coral") {
                val value = event.block.getTag(durabilityLeft)
                if (value == 1) {
                    event.player.instance.breakBlock(event.player, event.blockPosition, BlockFace.TOP)
                    event.player.instance.setBlock(event.blockPosition, Block.WATER)

                    event.instance.scheduleNextTick {
                        val particle = Particle.particle(
                            type = ParticleType.BLOCK,
                            count = 50,
                            data = OffsetAndSpeed(0f, -0.5f, 0f, 10f),
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
                    AcidMushroom.create()
                )
            }

            if (event.block.name() == "minecraft:cactus") {
                val value = event.block.getTag(durabilityLeft)
                if (value == 1) {
                    event.player.instance.breakBlock(event.player, event.blockPosition, BlockFace.TOP)
                    event.player.instance.setBlock(event.blockPosition, Block.AIR)

                    event.instance.scheduleNextTick {
                        val particle = Particle.particle(
                            type = ParticleType.BLOCK,
                            count = 50,
                            data = OffsetAndSpeed(0f, -0.5f, 0f, 10f),
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
                    Cactus.create()
                )
            }

            if (event.block.name() == "minecraft:wheat") {
                event.player.instance.breakBlock(event.player, event.blockPosition, BlockFace.TOP)
                event.player.instance.setBlock(event.blockPosition, Block.AIR)

                event.player.inventory.addItemStack(LanternFruit.create())
            }
        }
}