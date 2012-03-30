/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.experiment;

/**
 *
 * @author ytoh
 */
public class ExperimentException extends RuntimeException {

    /**
     * Creates a new instance of <code>ExperimentException</code> without detail message.
     */
    public ExperimentException() {}


    /**
     * Constructs an instance of <code>ExperimentException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ExperimentException(String msg) {
        super(msg);
    }

    /**
     * Creates a new instance of <code>ExperimentException</code> with the specified cause.
     *
     * @param cause the cause of this exception
     */
    public ExperimentException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of <code>ExperimentException</code> with a cause and a message.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public ExperimentException(String message, Throwable cause) {
        super(message, cause);
    }
}
