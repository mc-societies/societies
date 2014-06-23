package net.catharos.societies.request;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Set;
import java.util.concurrent.ExecutionException;

@RunWith(JUnit4.class)
public class SimpleRequestTest {

    private final Participant[] elements = new Participant[]{new SimpleParticipant(), new SimpleParticipant(), new SimpleParticipant(), new SimpleParticipant()};

    private final Set<Participant> participants = Sets.newHashSet(elements);


    @Test
    public void testVoting() throws ExecutionException, InterruptedException {
        Involved involved = new Involved() {
            @Override
            public boolean isInvolved(Participant participant) {
                return getInvolved().contains(participant);
            }

            @Override
            public Set<Participant> getInvolved() {
                return participants;
            }
        };


        SimpleRequest request = new SimpleRequest(involved);


        request.vote(elements[0], SimpleRequest.Choices.ACCEPT);
        request.vote(elements[1], SimpleRequest.Choices.ACCEPT);
        request.vote(elements[2], SimpleRequest.Choices.ACCEPT);
        request.vote(elements[3], SimpleRequest.Choices.ACCEPT);

        Assert.assertTrue(!request.isPending());

        System.out.println(request.result().get());
    }
}
