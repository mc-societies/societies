package org.societies.group.rule;

import org.societies.groups.rank.Rank;

/**
 * Represents a RankSelector
 */
interface RankSelector {

    String getName();

    boolean is(Rank rank);
}
