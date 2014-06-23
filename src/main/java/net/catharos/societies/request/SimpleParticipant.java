package net.catharos.societies.request;

/**
 * Represents a SimpleParticipant
 */
public class SimpleParticipant implements Participant {

    private Request request;

    public SimpleParticipant() {
    }

    public SimpleParticipant(Request request) {this.request = request;}

    @Override
    public Request getActiveRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
}
