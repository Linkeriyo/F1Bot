package com.linkeriyo.f1bot.listeners;

import com.linkeriyo.f1bot.F1Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class F1MessageListener extends ListenerAdapter {

    private int calculatePoints(List<Object> row) {
        int puntos = 0;
        for (int j = 1; j < row.size(); j++) {
            try {
                puntos += Integer.parseInt((String) row.get(j));
            } catch (NumberFormatException ignored) {
            }
        }
        return puntos;
    }

    private String getNameByUser(User user) {
        List<List<Object>> discordIDSGrid = F1Bot.getSheetAccess().getDiscordIDsGrid();
        String userId = user.getId();
        final String[] name = {null};

        discordIDSGrid.forEach(row -> {
            if (userId.equals(row.get(0))) {
                name[0] = (String) row.get(1);
            }
        });

        return name[0].toLowerCase();
    }

    private void checkCommands(Message message, String[] args) {
        for (String arg :
                args) {
            System.out.println(arg);
        }
        if ("puntos".equals(args[0])) {
            try {
                String nombre;
                try {
                    nombre = args[1].toLowerCase();
                } catch (IndexOutOfBoundsException e) {
                    nombre = getNameByUser(message.getAuthor());
                    System.out.println(nombre);
                }
                List<List<Object>> pilotGrid = F1Bot.getSheetAccess().getPilotGrid();
                boolean found = false;

                for (List<Object> row : pilotGrid) {
                    if (((String) row.get(0)).toLowerCase().contains(nombre) && !found) {
                        found = true;
                        int puntos = calculatePoints(row);

                        message.reply(row.get(0) + " tiene " + puntos + " puntos.").queue();
                    }
                }

                if (!found) {
                    List<List<Object>> teamGrid = F1Bot.getSheetAccess().getTeamGrid();

                    for (List<Object> row : teamGrid) {
                        if (((String) row.get(0)).toLowerCase().contains(nombre) && !found) {
                            found = true;
                            int puntos = calculatePoints(row);

                            message.reply(row.get(0) + " tiene " + puntos + " puntos.").queue();
                        }
                    }
                }

                if (!found) {
                    message.reply("No he encontrado el piloto o equipo").queue();
                }

            } catch (IndexOutOfBoundsException e) {
                message.reply("WIP").queue();
            }
        } else if ("pilotos".equals(args[0])) {
            List<List<Object>> pilotGrid = F1Bot.getSheetAccess().getPilotGrid();
            StringBuilder description = new StringBuilder();

            for (int i = 0; i < pilotGrid.size(); i++) {
                List<Object> row = pilotGrid.get(i);
                int puntos = calculatePoints(row);
                description.append(i + 1).append(". ").append(row.get(0)).append(" (").append(puntos).append(" puntos)\n");
            }

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Mundial de pilotos de MINECRAFTERS F1");
            embedBuilder.setDescription(description);
            message.replyEmbeds(embedBuilder.build()).queue();

        } else if ("equipos".equals(args[0]) || "constructores".equals(args[0])) {
            List<List<Object>> teamGrid = F1Bot.getSheetAccess().getTeamGrid();
            StringBuilder description = new StringBuilder();

            for (int i = 0; i < teamGrid.size(); i++) {
                List<Object> row = teamGrid.get(i);
                int puntos = calculatePoints(row);
                description.append(i + 1).append(". ").append(row.get(0)).append(" (").append(puntos).append(" puntos)\n");
            }

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Mundial de constructores de MINECRAFTERS F1");
            embedBuilder.setDescription(description);
            message.replyEmbeds(embedBuilder.build()).queue();
        } else if ("resultados".equals(args[0])) {
            String nombre;
            try {
                nombre = args[1].toLowerCase();
            } catch (IndexOutOfBoundsException e) {
                nombre = getNameByUser(message.getAuthor());
            }
            List<List<Object>> pilotGrid = F1Bot.getSheetAccess().getPilotGrid();
            List<List<Object>> gpsGrid = F1Bot.getSheetAccess().getGPsGrid();
            boolean found = false;

            for (List<Object> row : pilotGrid) {
                if (((String) row.get(0)).toLowerCase().contains(nombre) && !found) {
                    found = true;
                    int puntos = calculatePoints(row);

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle("Resultados de " + row.get(0) + " (" + puntos + " puntos)");
                    for (int i = 0; i < gpsGrid.size(); i++) {
                        List<Object> gpRow = gpsGrid.get(i);

                        String puntosGP = (String) row.get(i + 1);
                        if (puntosGP.equals("")) {
                            puntosGP = "0";
                        }

                        embedBuilder.addField(gpRow.get(0) + ". " + gpRow.get(1), puntosGP + " puntos", true);
                    }
                    message.replyEmbeds(embedBuilder.build()).queue();
                }
            }

            if (!found) {
                message.reply("No he encontrado el piloto o equipo").queue();
            }
        } else if ("estadisticas".equals(args[0])) {
            String nombre;
            try {
                nombre = args[1].toLowerCase();
            } catch (IndexOutOfBoundsException e) {
                nombre = getNameByUser(message.getAuthor());
            }

            List<List<Object>> pilotStats = F1Bot.getSheetAccess().getPilotStatsGrid();
            boolean found = false;

            for (List<Object> row : pilotStats) {
                if (((String) row.get(0)).toLowerCase().contains(nombre) && !found) {
                    found = true;

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle("Estadísticas de " + row.get(0) + ".");

                    for (int i = 1; i < pilotStats.get(0).size(); i++) {
                        String columnName = (String) pilotStats.get(0).get(i);
                        String columnValue = (String) row.get(i);

                        embedBuilder.addField(columnName, columnValue, true);
                    }

                    message.replyEmbeds(embedBuilder.build()).queue();
                }
            }

            if (!found) {
                List<List<Object>> teamStats = F1Bot.getSheetAccess().getTeamStatsGrid();

                for (List<Object> row : teamStats) {
                    if (((String) row.get(0)).toLowerCase().contains(nombre) && !found) {
                        found = true;

                        EmbedBuilder embedBuilder = new EmbedBuilder();
                        embedBuilder.setTitle("Estadísticas de " + row.get(0) + ".");

                        for (int i = 1; i < teamStats.get(0).size(); i++) {
                            String columnName = (String) teamStats.get(0).get(i);
                            String columnValue = (String) row.get(i);

                            embedBuilder.addField(columnName, columnValue, true);
                        }

                        message.replyEmbeds(embedBuilder.build()).queue();
                    }
                }
            }
        } else {
            message.reply("Ese comando no existe.").queue();
        }
    }



    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            List<User> mentionedUsers = event.getMessage().getMentionedUsers();

            mentionedUsers.forEach(user -> {
                if (user.getIdLong() == F1Bot.getJda().getSelfUser().getIdLong()) {
                    String[] args = event.getMessage().getContentRaw().substring(23).split(" ");
                    checkCommands(event.getMessage(), args);
                }
            });
        }
    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            String[] args = event.getMessage().getContentRaw().split(" ");
            checkCommands(event.getMessage(), args);
        }
    }
}
