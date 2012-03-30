/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.experiment.util;

import cz.cvut.felk.cig.jcool.core.Consumer;
import cz.cvut.felk.cig.jcool.core.Producer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ytoh
 */
public final class Producers {

    private Producers() {
        // no instances please
        throw new AssertionError();
    }

    /**
     * 
     * @param <T>
     * @param <E>
     * @param type
     * @return
     */
    public static final <T, E extends T> Filter<T, E> filtering(final Class<E> type, Class<T> from) {
        return new Filter<T, E>() {

            private final Class<E> filteringType = type;
            private List<Consumer<? super E>> consumers = new ArrayList<Consumer<? super E>>();
            private E value;

            public void notifyOf(Producer<? extends List<? extends T>> producer) {
                for (T o : producer.getValue()) {
                    if (o.getClass().equals(filteringType)) {
                        value = filteringType.cast(o);
                    }
                }

                for (Consumer<? super E> consumer : consumers) {
                    consumer.notifyOf(this);
                }
            }

            public void addConsumer(Consumer<? super E> consumer) {
                consumers.add(consumer);
            }

            public E getValue() {
                return value;
            }
        };
    }

    /**
     * 
     * @param <T>
     * @param <E>
     * @param producer
     * @param t
     * @return
     */
    public static final <T,E> Wrapper<T,E> wrap(final Producer<T> producer, final Transformer<T,E> t) {
        return new Wrapper<T,E>() {
            private Producer<T> delegate = producer;
            private List<Consumer<? super E>> consumers = new ArrayList<Consumer<? super E>>();

            {{ delegate.addConsumer(this); }}

            public void notifyOf(Producer<? extends T> producer) {
                for (Consumer<? super E> consumer : consumers) {
                    consumer.notifyOf(this);
                }
            }

            public void addConsumer(Consumer<? super E> consumer) {
                consumers.add(consumer);
            }

            public E getValue() {
                return t.transform(delegate.getValue());
            }
        };
    }
}
