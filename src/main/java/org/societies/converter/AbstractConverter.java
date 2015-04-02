package org.societies.converter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.inject.Provider;
import org.societies.api.converter.Converter;
import org.societies.groups.RelationFactory;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupBuilder;
import org.societies.groups.group.GroupPublisher;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberFactory;
import org.societies.groups.member.MemberPublisher;
import org.societies.groups.rank.Rank;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

/**
 * Represents a AbstractConverter
 */
public abstract class AbstractConverter implements Converter {
    private static final JsonFactory factory = new JsonFactory();


    private final MemberFactory memberFactory;
    private final Provider<GroupBuilder> groupBuilder;
    private final RelationFactory relationFactory;
    private final Rank superDefaultRank;

    private final MemberPublisher memberPublisher;
    private final GroupPublisher groupPublisher;

    public AbstractConverter(MemberFactory memberFactory,
                             Provider<GroupBuilder> groupBuilder,
                             RelationFactory relationFactory,
                             Rank superDefaultRank,
                             MemberPublisher memberPublisher,
                             GroupPublisher groupPublisher) {
        this.memberFactory = memberFactory;
        this.groupBuilder = groupBuilder;
        this.relationFactory = relationFactory;
        this.superDefaultRank = superDefaultRank;
        this.memberPublisher = memberPublisher;
        this.groupPublisher = groupPublisher;
    }

    public MemberFactory getMemberFactory() {
        return memberFactory;
    }

    public Provider<GroupBuilder> getGroupBuilder() {
        return groupBuilder;
    }

    public RelationFactory getRelationFactory() {
        return relationFactory;
    }

    public Rank getSuperDefaultRank() {
        return superDefaultRank;
    }

    public MemberPublisher getMemberPublisher() {
        return memberPublisher;
    }

    public GroupPublisher getGroupPublisher() {
        return groupPublisher;
    }


    public UUID findUUID(final String name) throws IOException {
        URL url = new URL("http", "api.mojang.com", "/users/profiles/minecraft/" + name);

        InputStream stream = url.openStream();

        JsonParser parser = factory.createParser(stream);

        String stringUUID = null;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();

            parser.nextToken();

            if (fieldName.equals("id")) {
                stringUUID = parser.getText();
                break;
            }
        }

        if (stringUUID == null) {
            throw new IOException("No uuid found for name " + name + "!");
        }

        parser.close();
        return UUID.fromString(stringUUID.replaceAll(
                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                "$1-$2-$3-$4-$5"));

    }

    protected void publish(Member member) {
        getMemberPublisher().publish(member);
    }

    protected void publish(Group group) {
        getGroupPublisher().publish(group);
    }
}
