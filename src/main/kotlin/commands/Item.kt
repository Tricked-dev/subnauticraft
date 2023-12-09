package dev.tricked.subnauticraft.commands

import dev.tricked.subnauticraft.Items
import net.minestom.server.command.builder.arguments.ArgumentWord
import world.cepi.kstom.command.arguments.suggest
import world.cepi.kstom.command.kommand.Kommand

object ItemCommand : Kommand({
    syntax {
        sender.sendMessage("Usage: item <item>")
        sender.sendMessage("Available items: ${Items.entries.map { it.item.id }}")
    }
    val item = ArgumentWord("item").suggest {
        println("${  Items.entries.map { it.item.id }}")
       Items.entries.map { it.item.id }
    }

    syntax(item) {
        val data = Items.entries.first { it.item.id == !item }.item
        player.inventory.addItemStack(data.create())
    }

}, "item")