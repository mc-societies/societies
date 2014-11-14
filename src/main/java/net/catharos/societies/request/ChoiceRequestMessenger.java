package net.catharos.societies.request;

import net.catharos.groups.request.Participant;
import net.catharos.groups.request.Request;
import net.catharos.groups.request.RequestMessenger;
import net.catharos.groups.request.simple.Choices;

/**
 * Represents a AbstractRequestMessenger
 */
public class ChoiceRequestMessenger implements RequestMessenger<Choices> {

    @Override
    public void start(Request<Choices> request, Participant participant) {
        request.getSupplier().send("request.started");
        participant.send("request.started");
    }

    @Override
    public void voted(Request<Choices> request, Choices choice, Participant participant) {
        String msg;

        switch (choice) {
            case ACCEPT:
                msg = "request.participant-voted.accept";
                break;
            case DENY:
                msg = "request.participant-voted.deny";
                break;
            case ABSTAIN:
                msg = "request.participant-voted.abstain";
                break;
            case CANCELLED:
                msg = "request.participant-voted.cancelled";
                break;
            default:
                msg = "request.participant-voted";
        }

        request.getSupplier().send(msg, participant.getName(), choice, request);
        participant.send(msg, participant.getName(), choice, request);
    }

    public void end(Participant participant, Request<Choices> request) {
        request.getSupplier().send("request.finished");
        participant.send("request.finished");
    }

    public void cancelled(Participant participant, Request<Choices> request) {
        request.getSupplier().send("request.cancelled");
        participant.send("request.cancelled");
    }


    @Override
    public final void end(Request<Choices> request) {
        for (Participant participant : request.getRecipients()) {
            end(participant, request);
        }
    }

    @Override
    public final void cancelled(Request<Choices> request) {
        for (Participant participant : request.getRecipients()) {
            cancelled(participant, request);
        }
    }
}
