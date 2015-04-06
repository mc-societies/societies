package org.societies.request;

import org.societies.groups.request.Participant;
import org.societies.groups.request.Request;
import org.societies.groups.request.RequestMessenger;
import org.societies.groups.request.simple.Choices;

/**
 * Represents a AbstractRequestMessenger
 */
public class ChoiceRequestMessenger implements RequestMessenger<Choices> {

    public void start(Request<Choices> request, Participant participant) {
        request.getSupplier().send("request.started");
        participant.send("request.started");
    }

    @Override
    public void start(Request<Choices> request) {
        for (Participant participant : request.getRecipients()) {
            start(request, participant);
        }
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



    public void end(Participant participant, Request<Choices> request, Choices choice) {
        request.getSupplier().send("request.finished");
        participant.send("request.finished");
    }

    @Override
    public void end(Request<Choices> request, Choices choice) {
        for (Participant participant : request.getRecipients()) {
            end(participant, request, choice);
        }
    }

    @Override
    public void cancelled(Request<Choices> request) {
        end(request, Choices.CANCELLED);
    }
}
