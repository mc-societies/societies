package net.catharos.societies.member;

import java.util.UUID;

/**
 * Represents a MemberFactory
 */
public interface MemberFactory {

    SocietyMember create(UUID uuid);
}
