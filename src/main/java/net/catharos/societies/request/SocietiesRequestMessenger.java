package net.catharos.societies.request;

import net.catharos.groups.request.*;
import net.catharos.groups.request.simple.Choices;

/**
 * Represents a SocietiesRequestMessenger
 */
public class SocietiesRequestMessenger implements RequestMessenger<Choices> {

    @Override
    public void start(Request<Choices> request, Participant participant) {
        participant.send("request.started", request);
    }

    @Override
    public void voted(Request<Choices> request, Choices choice, Participant participant) {
        participant.send("request.voted", participant.getName(), choice, request);
    }

    @Override
    public void end(Request<Choices> request) {
        for (Participant participant : request.getReceivers()) {
            participant.send("request.finished", request);
        }
    }

    @Override
    public void cancelled(Request<Choices> request) {
        for (Participant participant : request.getReceivers()) {
            participant.send("request.cancelled", request);
        }
    }
}
