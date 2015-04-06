package org.societies.bukkit.listener;

import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Invoke;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.societies.groups.event.GroupTagEvent;
import org.societies.groups.event.MemberJoinEvent;
import org.societies.groups.event.MemberLeaveEvent;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;

/**
 * Represents a TeamListener
 */
class TeamListener {

    @Handler(delivery = Invoke.Synchronously)
    public void onJoin(MemberJoinEvent event) {
        setupMember(event.getMember());
    }

    @Handler(delivery = Invoke.Synchronously)
    public void onTag(GroupTagEvent event) {
        Group group = event.getGroup();
        removeTeam(group);

        for (Member member : group.getMembers()) {
            setupMember(member);
        }
    }

    @Handler(delivery = Invoke.Synchronously)
    public void onLeave(MemberLeaveEvent event) {
        removeMember(event.getMember(), event.getPreviousGroup());
    }

    public void setupMember(Member member) {
        Player extension = member.get(Player.class);

        Group group = member.getGroup();

        if (group == null) {
            return;
        }

        Team team = getTeam(group);
        team.addPlayer(extension);
    }

    public void removeMember(Member member, Group group) {
        Player extension = member.get(Player.class);

        Team team = getTeam(group);
        team.removePlayer(extension);
    }

    public Team getTeam(Group group) {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();

        Team team = board.getTeam(ChatColor.stripColor(group.getTag()));

        if (team == null) {
            team = board.registerNewTeam(ChatColor.stripColor(group.getTag()));
        }

        team.setPrefix("§8 [" + group.getTag() + "§8] " + ChatColor.RESET);

        return team;
    }

    public void removeTeam(Group group) {
        Team team = getTeam(group);
        team.unregister();
    }
}
