package net.catharos.societies.request;

/**
 *
 */
public interface RequestResult {

    Request getRequest() throws RequestFailedException;

    Choice getChoice() throws RequestFailedException;
}
