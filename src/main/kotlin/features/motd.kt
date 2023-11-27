package dev.tricked.subnauticraft.features

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.utils.identity.NamedAndIdentified


object ServerListPingHandler {

    fun handlePingEvent(event: ServerListPingEvent) {
        val responseData = event.responseData
        responseData.addEntry(NamedAndIdentified.named("The first line is separated from the others"))
        responseData.addEntry(NamedAndIdentified.named("Could be a name, or a message"))

        handleConnectionInfo(event)

        responseData.addEntry(
            NamedAndIdentified.named(
                Component.text("Time", NamedTextColor.YELLOW)
                    .append(Component.text(": ", NamedTextColor.GRAY))
                    .append(
                        Component.text(
                            System.currentTimeMillis(),
                            Style.style(TextDecoration.ITALIC)
                        )
                    )
            )
        )
        responseData.addEntry(
            NamedAndIdentified.named(
                Component.text("You can use ").append(
                    Component.text(
                        "styling too!",
                        NamedTextColor.RED,
                        TextDecoration.BOLD
                    )
                )
            )
        )
        responseData.description = Component.text(
            "This is a Minestom Server",
            TextColor.color(0x66b3ff)
        )
    }

    private fun handleConnectionInfo(event: ServerListPingEvent) {
        if (event.connection != null) {
            val ip = event.connection!!.serverAddress
            event.responseData.addEntry(createEntry("IP", ip ?: "???", NamedTextColor.YELLOW))
            event.responseData.addEntry(createEntry("PORT", event.connection!!.serverPort.toString()))
            event.responseData.addEntry(createEntry("VERSION", event.connection!!.protocolVersion.toString()))
        }
    }

    private fun createEntry(label: String, value: String, textColor: NamedTextColor = NamedTextColor.GRAY): NamedAndIdentified {
        return NamedAndIdentified.named(
            Component.text('-', NamedTextColor.DARK_GRAY)
                .append(Component.text(" $label: ", NamedTextColor.GRAY))
                .append(
                    Component.text(
                        value,
                        textColor
                    )
                )
        )
    }
}