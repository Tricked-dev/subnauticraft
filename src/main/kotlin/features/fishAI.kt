package dev.tricked.subnauticraft.features

import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.ai.goal.MeleeAttackGoal
import net.minestom.server.entity.ai.goal.RandomStrollGoal
import net.minestom.server.entity.ai.target.ClosestEntityTarget
import net.minestom.server.entity.ai.target.LastEntityDamagerTarget
import net.minestom.server.entity.pathfinding.Navigator
import net.minestom.server.utils.time.TimeUnit


class Fish : EntityCreature(EntityType.TROPICAL_FISH) {

    override fun getNavigator(): Navigator {
        val nav = super.getNavigator()
        nav.pathingEntity.isAquatic = true;
        nav.pathingEntity.isAvian = true;
        return nav;
    }

    init {
        addAIGroup(
            listOf(
                MeleeAttackGoal(this, 1.6, 20, TimeUnit.SERVER_TICK), // Attack the target
                RandomStrollGoal(this, 20) // Walk around
            ),
            listOf(
                LastEntityDamagerTarget(this, 32f),
                ClosestEntityTarget(this, 32.0) { entity ->
                    return@ClosestEntityTarget entity is Player
                }
            ))

    }

}