package cz.cvut.felk.cig.jcool.ui.util;

import java.awt.EventQueue;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JTextArea;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * An {@link org.apache.log4j.Appender} implementation that appends to
 * a {@link JTextArea} instance.
 *
 * <p>The JTextArea has to be supplied via a static setter method.
 * Access to the <code>textarea</code> has to be synchronized because it will
 * be accessed from multiple threads (atleast the thread that sets the textarea
 * will be different from AWT Event thread that repaints it). The access
 * is synchrinized using {@link ReentrantLock} from the java concurrent framework.
 * </p>
 *
 * @author ytoh
 */
public class JTextAreaAppender extends WriterAppender {
    /** a reference to the textarea to append to */
    private static JTextArea textarea;
    /** lock garing the access to the textarea */
    private static ReentrantLock lock = new ReentrantLock();

    /**
     * Set the reference to the textarea to append to.
     *
     * <p>The textarea reference is garded by an instance of {@link ReentrantLock}</p>
     *
     * @see ReentrantLock
     *
     * @param textarea
     */
    public static void setTextarea(JTextArea textarea) {
        // lock access to textarea
        lock.lock();
        // set is to a new instance
        JTextAreaAppender.textarea = textarea;
        // release the lock
        lock.unlock();
    }

    /**
     * Append messages to the associated textarea.
     *
     * <p>Access to the textarea is guarded by an instance of {@link ReentrantLock}</p>
     *
     * @see ReentrantLock
     * @see WriterAppender#append(org.apache.log4j.spi.LoggingEvent)
     * @param event
     */
    @Override
    public void append(final LoggingEvent event) {
        // try to acquire the lock
        if(lock.tryLock()) {
            // if the textarea is set, schedule and append the message
            if(textarea != null) {
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        textarea.append(layout.format(event));
                    }
                });
            }
            // unlock the textareas
            lock.unlock();
        }
    }
}
