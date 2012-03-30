/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.experiment.util;

import cz.cvut.felk.cig.jcool.core.Consumer;
import cz.cvut.felk.cig.jcool.core.Producer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

/**
 *
 * @author ytoh
 */
public final class Consumers {

    public Consumers() {
        // no instances please
        throw new AssertionError();
    }
    
    public static final <T> Broadcaster<T> broadcast(Producer<T> source) {
        return new Broadcaster<T>(source);
    }

    public static final <T> Aggregator<T> lazyAggregatorOf(Class<T> type) {
        return new Aggregator<T>() {

            private Set<Producer<? extends T>> producers = new HashSet<Producer<? extends T>>();
            private List<Consumer<? super List<? extends T>>> consumers = new ArrayList<Consumer<? super List<? extends T>>>();;

            public void notifyOf(Producer<? extends T> producer) {
                producers.add(producer);
            }

            public void addConsumer(Consumer<? super List<? extends T>> consumer) {
                consumers.add(consumer);
            }

            public List<? extends T> getValue() {
                return new ArrayList<T>(CollectionUtils.collect(producers, new Transformer() {

                    public Object transform(Object input) {
                        return ((Producer<? extends T>) input).getValue();
                    }
                }));
            }
        };
    }

    /**
     *
     * @param <T>
     * @param type
     * @return
     */
    public static final <T> Aggregator<T> aggregatorOf(Class<T> type) {
        return new Aggregator<T>() {

            private Map<Producer<? extends T>, T> values = new HashMap<Producer<? extends T>, T>();
            private List<Consumer<? super List<? extends T>>> consumers = new ArrayList<Consumer<? super List<? extends T>>>();

            public void notifyOf(Producer<? extends T> producer) {
                values.put(producer, producer.getValue());
            }

            public void addConsumer(Consumer<? super List<? extends T>> consumer) {
                consumers.add(consumer);
            }

            public List<? extends T> getValue() {
                return new ArrayList<T>(values.values());
            }
        };
    }

    /**
     *
     * @param <T>
     * @param type
     * @param synchronizer
     * @return
     */
    public static final <T> Aggregator<T> synchronizingAggregatorOf(Class<T> type, final Class<? extends Producer<? extends T>> synchronizer) {

        return new Aggregator<T>() {

            private Class<? extends Producer<? extends T>> type = synchronizer;
            private Set<Producer<? extends T>> producers = new HashSet<Producer<? extends T>>();
            private List<? extends T>           values;
            private List<Consumer<? super List<? extends T>>> consumers = new ArrayList<Consumer<? super List<? extends T>>>();

            public void notifyOf(Producer<? extends T> producer) {
                if (producer.getClass().equals(type)) {
                    values = (List<? extends T>) CollectionUtils.collect(producers, new Transformer() {

                        public Object transform(Object input) {
                            return ((Producer<? extends T>) input).getValue();
                        }
                    });

                    this.producers.clear();
                    for (Consumer<? super List<? extends T>> consumer : consumers) {
                        consumer.notifyOf(this);
                    }
                } else {
                    this.producers.add(producer);
                }
            }

            public void addConsumer(Consumer<? super List<? extends T>> consumer) {
                this.consumers.add(consumer);
            }

            public List<? extends T> getValue() {
                return values;
            }
        };
    }
}
