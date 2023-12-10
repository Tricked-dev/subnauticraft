package dev.tricked.subnauticraft.features

import dev.tricked.subnauticraft.InteractableItem
import dev.tricked.subnauticraft.Item
import dev.tricked.subnauticraft.particle
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import net.minestom.server.timer.TaskSchedule
import world.cepi.particle.Particle
import world.cepi.particle.ParticleType
import world.cepi.particle.data.OffsetAndSpeed

val repairToolTag = Tag.Boolean("repairTool")
val repairedTag = Tag.Boolean("repaired")
val timeLeft = Tag.Integer("timeLeft").defaultValue(75)

object RepairTool : Item(), InteractableItem {
    override val material = Material.IRON_AXE
    override val name = Component.text("Repair Tool", NamedTextColor.DARK_PURPLE)
    override val lore = arrayOf(Component.text("Right click to repair doors"))
    override val weight = 1
    override val id = "welder"

    override fun blockInteract(event: PlayerBlockInteractEvent) {
        if (event.block.name() == "minecraft:tripwire_hook") {
            val offset = 1.0
            var blockPosition = event.blockPosition


            val face = BlockFace.valueOf(event.block.getProperty("facing").uppercase())

            blockPosition = when (face) {
                BlockFace.NORTH -> blockPosition.add(0.0, 0.0, offset)
                BlockFace.SOUTH -> blockPosition.add(0.0, 0.0, -offset)
                BlockFace.EAST -> blockPosition.add(offset, 0.0, 0.0)
                BlockFace.WEST -> blockPosition.add(-offset, 0.0, 0.0)
                else -> return
            }

            var possibleLeft = blockPosition
            var possibleRight = blockPosition

            when (face) {
                BlockFace.NORTH, BlockFace.SOUTH -> {
                    possibleLeft = possibleLeft.add(-offset, 0.0, 0.0)
                    possibleRight = possibleRight.add(offset, 0.0, 0.0)
                }

                BlockFace.EAST, BlockFace.WEST -> {
                    possibleLeft = possibleLeft.add(0.0, 0.0, -offset)
                    possibleRight = possibleRight.add(0.0, 0.0, offset)
                }

                else -> return
            }

            val blockLeft = event.instance.getBlock(possibleLeft)
            val blockRight = event.instance.getBlock(possibleRight)
            val matches = blockLeft.name().endsWith("_door") || blockRight.name().endsWith("_door")

            if (matches) {
                val tl = event.block.getTag(timeLeft);
                event.player.swingMainHand()

                if (tl == 0) {
                    if (blockLeft.name().endsWith("_door")) {
                        event.instance.setBlock(possibleLeft, blockLeft.withTag(repairedTag, true))
                        event.instance.setBlock(
                            possibleLeft.add(0.0, -1.0, 0.0),
                            blockLeft.withTag(repairedTag, true).withProperty("half", "lower")
                        )
                    } else if (blockRight.name().endsWith("_door")) {
                        event.instance.setBlock(possibleRight, blockRight.withTag(repairedTag, true))
                        event.instance.setBlock(
                            possibleRight.add(0.0, -1.0, 0.0),
                            blockRight.withTag(repairedTag, true).withProperty("half", "lower")
                        )
                    }
                    event.instance.setBlock(
                        event.blockPosition,
                        event.block.withTag(timeLeft, 0).withProperty("attached", "true")
                    )
                } else {
                    val particle = Particle.particle(
                        type = ParticleType.BUBBLE,
                        count = tl / 4,
                        data = OffsetAndSpeed(0.2f, 0.2f, 0.2f, 5f),
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
    }
}

object RepairToolEvents {
    val events = EventNode.all("trapdoors")
        .addListener(PlayerBlockInteractEvent::class.java) { event ->
            if (event.block.name().endsWith("_door") && event.block.hasTag(repairedTag)) {
                val otherPieceUp = event.instance.getBlock(event.blockPosition.add(0.0, 1.0, 0.0));
                val otherPieceDown = event.instance.getBlock(event.blockPosition.add(0.0, -1.0, 0.0));
                if (otherPieceUp.name().endsWith("_door")) {
                    event.instance.setBlock(
                        event.blockPosition.add(0.0, 1.0, 0.0),
                        otherPieceUp.withProperty("open", "true").withProperty("half", "upper")
                    )
                    event.instance.setBlock(
                        event.blockPosition,
                        event.block.withProperty("open", "true").withProperty("half", "lower")
                    )
                } else if (otherPieceDown.name().endsWith("_door")) {
                    event.instance.setBlock(
                        event.blockPosition.add(0.0, -1.0, 0.0),
                        otherPieceDown.withProperty("open", "true").withProperty("half", "lower")
                    )
                    event.instance.setBlock(
                        event.blockPosition,
                        event.block.withProperty("open", "true").withProperty("half", "upper")
                    )
                }
            }
        }

}