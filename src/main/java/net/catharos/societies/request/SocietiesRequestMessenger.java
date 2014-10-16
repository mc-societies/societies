package net.catharos.societies.request;

import net.catharos.groups.request.Participant;
import net.catharos.groups.request.Request;
import net.catharos.groups.request.RequestMessenger;
import net.catharos.groups.request.simple.Choices;

/**
 * Represents a SocietiesRequestMessenger
 */
public class SocietiesRequestMessenger implements RequestMessenger<Choices> {

    @Override
    public void start(Request<Choices> request, Participant participant) {
        participant.send(request.getName());
    }

    @Override
    public void voted(Request<Choices> request, Choices choice, Participant participant) {
        participant.send("request.voted", participant.getName(), choice, request);
    }

    @Override
    public void end(Request<Choices> request) {
        for (Participant participant : request.getRecipients()) {
            participant.send("request.finished", request.getName());
        }
    }

    @Override
    public void cancelled(Request<Choices> request) {
        for (Participant participant : request.getRecipients()) {
            participant.send("request.cancelled", request.getName());
        }
    }
}
