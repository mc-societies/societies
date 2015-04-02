package org.societies.member;

import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import net.catharos.lib.core.command.sender.SenderProvider;
import org.shank.AbstractModule;
import org.societies.groups.ExtensionRoller;
import org.societies.groups.member.Member;

import static com.google.inject.multibindings.Multibinder.newSetBinder;

/**
 * Represents a MemberModule
 */
public class MemberModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SenderProvider.class).to(SenderAdapter.class);

        Multibinder<ExtensionRoller<Member>> extensions = newSetBinder(
                binder(),
                new TypeLiteral<ExtensionRoller<Member>>() {}
        );

        extensions.addBinding().to(SocietyMemberRoller.class);
    }

}
