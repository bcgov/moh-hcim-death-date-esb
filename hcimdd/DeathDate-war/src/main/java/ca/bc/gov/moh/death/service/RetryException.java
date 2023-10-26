package ca.bc.gov.moh.death.service;

/**
 * Retry sending the message if this exception is thrown.
 *
 * @author David Sharpe (david.a.sharpe@cgi.com)
 */
class RetryException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public RetryException() {
    }

    public RetryException(String string) {
        super(string);
    }

    public RetryException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public RetryException(Throwable thrwbl) {
        super(thrwbl);
    }
    
}
