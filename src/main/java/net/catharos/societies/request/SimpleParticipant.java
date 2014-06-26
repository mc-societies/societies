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


    @Override
    public boolean clearRequest() {
        boolean value = request != null;
        request = null;
        return value;
    }

    @Override
    public void setActiveRequest(Request request) {
        this.request = request;
    }
}
