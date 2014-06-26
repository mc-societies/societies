package net.catharos.societies.request;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Represents a RequestResult
 */
public class SimpleRequestResult implements RequestResult {

    private final Choice choice;
    private final Request request;

    public SimpleRequestResult() {this(null, null);}

    public SimpleRequestResult(Choice choice, Request request) {
        this.choice = choice;
        this.request = request;
    }

    @Override
    public Request getRequest() throws RequestFailedException {
        if (request == null) {
            throw new RequestFailedException();
        }
        return request;
    }

    @Override
    public Choice getChoice() throws RequestFailedException {
        if (choice == null) {
            throw new RequestFailedException();
        }
        return choice;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("choice", choice)
                .append("request", request)
                .toString();
    }
}
