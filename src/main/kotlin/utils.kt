package dev.tricked.subnauticraft

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.instance.Instance
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import java.util.UUID
import dev.emortal.rayfast.area.area3d.Area3d
import dev.emortal.rayfast.area.area3d.Area3d.Area3dCombined
import dev.emortal.rayfast.area.area3d.Area3dLike
import dev.emortal.rayfast.area.area3d.Area3dRectangularPrism
import dev.emortal.rayfast.casting.grid.GridCast
import dev.emortal.rayfast.vector.Vector3d

object Utils {
    private val idTag = Tag.String("id")
    val weightTag = Tag.Integer("weight")
    val pickupableTag = Tag.Boolean("pickupable")

    fun createItem(material: Material, name: Component, lore: Array<out Component> = emptyArray(), weight: Int = 1): ItemStack.Builder {
        val item = ItemStack.builder(material).displayName(name).lore(
            *lore,
            Component.text("Weight $weight", NamedTextColor.GRAY)
        )
        item.setTag(idTag, UUID.randomUUID().toString())
        item.setTag(weightTag, weight)
        return item
    }

    private val boundingBoxToArea3dMap = HashMap<LinkedBoundingBox, Area3d>()

    init {
        Area3d.CONVERTER.register(LinkedBoundingBox::class.java) { box ->

            boundingBoxToArea3dMap.computeIfAbsent(box) { it ->
                Area3dRectangularPrism.wrapper(
                    it,
                    { it.minX }, { it.minY }, { it.minZ },
                    { it.maxX }, { it.maxY }, { it.maxZ }
                )
            }

            boundingBoxToArea3dMap[box]
        }
    }

    val Entity.area3d: Area3d
        get() = Area3d.CONVERTER.from(boundingBox.toLinked(this))

    fun raycastEntity(
        instance: Instance,
        startPoint: Point,
        direction: Vec,
        maxDistance: Double,
        hitFilter: (Entity) -> Boolean = { true }
    ): Pair<Entity, Pos>? {

        instance.entities
            .filter { hitFilter.invoke(it) }
            .filter { it.position.distanceSquared(startPoint) <= maxDistance * maxDistance }
            .forEach {
                val area = it.area3d

                val intersection = area.lineIntersection(
                    Vector3d.of(startPoint.x(), startPoint.y(), startPoint.z()),
                    Vector3d.of(direction.x(), direction.y(), direction.z())
                )
                if (intersection != null) {
                    return Pair(it, Pos(intersection[0], intersection[1], intersection[2]))
                }
            }

        return null
    }
}