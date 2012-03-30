/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.felk.cig.jcool.ui.model;

import cz.cvut.felk.cig.jcool.experiment.util.Aggregator;
import cz.cvut.felk.cig.jcool.core.Consumer;
import cz.cvut.felk.cig.jcool.experiment.util.Filter;
import cz.cvut.felk.cig.jcool.core.Point;
import cz.cvut.felk.cig.jcool.core.Producer;
import cz.cvut.felk.cig.jcool.core.Telemetry;
import cz.cvut.felk.cig.jcool.experiment.util.Transformer;
import cz.cvut.felk.cig.jcool.core.ValuePoint;
import cz.cvut.felk.cig.jcool.core.ValuePointTelemetry;
import cz.cvut.felk.cig.jcool.core.ValueTelemetry;
import cz.cvut.felk.cig.jcool.experiment.util.Wrapper;
import cz.cvut.felk.cig.jcool.experiment.util.Producers;
import cz.cvut.felk.cig.jcool.experiment.util.Consumers;
import cz.cvut.felk.cig.jcool.solver.Synchronization;
import cz.cvut.felk.cig.jcool.experiment.Iteration;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author ytoh
 */
public class App {
    static int i = 0;

    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // single value producer
        Producer<ValueTelemetry> p1 = new Producer<ValueTelemetry>() {

            private Consumer<? super ValueTelemetry> consumer;
            private ValueTelemetry value = new ValueTelemetry(0.0);

            public void addConsumer(Consumer<? super ValueTelemetry> consumer) {
                this.consumer = consumer;
            }

            public void work() {
                value = new ValueTelemetry(value.getValue() + 1.0);
                consumer.notifyOf(this);
            }

            public ValueTelemetry getValue() {
                return value;
            }
        };

        // single value producer
        Producer<ValuePointTelemetry> p2 = new Producer<ValuePointTelemetry>() {

            private Consumer<? super ValuePointTelemetry> consumer;
            private ValuePointTelemetry value = new ValuePointTelemetry(ValuePoint.at(Point.at(0, 1), 0));

            public void addConsumer(Consumer<? super ValuePointTelemetry> consumer) {
                this.consumer = consumer;
            }

            public void work() {
                value = new ValuePointTelemetry(ValuePoint.at(value.getValue().getPoint(), value.getValue().getValue() + 1.0));
                consumer.notifyOf(this);
            }

            public ValuePointTelemetry getValue() {
                return value;
            }
        };

        // synchronizer
        Synchronizer s = new Synchronizer();
        // aggregator
        Aggregator<Telemetry> p = Consumers.synchronizingAggregatorOf(Telemetry.class, Synchronizer.class);

        p1.addConsumer(p);
        p2.addConsumer(p);
        s.addConsumer(p);

        // filter
        Filter<Telemetry, ValueTelemetry> f1 = Producers.filtering(ValueTelemetry.class, Telemetry.class);

        // filter
        Filter<Telemetry, ValuePointTelemetry> f2 = Producers.filtering(ValuePointTelemetry.class, Telemetry.class);

        p.addConsumer(f1);
        p.addConsumer(f2);

        // single value consumer
        Consumer<ValueTelemetry> end1 = new Consumer<ValueTelemetry>() {

            public void notifyOf(Producer<? extends ValueTelemetry> producer) {
                System.out.println("producer.pullValue().getValue() = " + producer.getValue().getValue());
            }
        };

        // single value consumer
        Consumer<ValuePointTelemetry> end2 = new Consumer<ValuePointTelemetry>() {

            public void notifyOf(Producer<? extends ValuePointTelemetry> producer) {
                System.out.println("producer.pullValue().getValue() = " + producer.getValue().getValue());
            }
        };

        Consumer<Iteration<ValueTelemetry>> end3 = new Consumer<Iteration<ValueTelemetry>>() {

            public void notifyOf(Producer<? extends Iteration<ValueTelemetry>> producer) {
                Iteration<ValueTelemetry> value = producer.getValue();
                System.out.println(value.getValue().getValue() + " in iteration: " + value.getIteration());
            }
        };

        Wrapper<ValueTelemetry, Iteration<ValueTelemetry>> wrap = Producers.wrap(f1, new Transformer<ValueTelemetry, Iteration<ValueTelemetry>>() {

            public Iteration<ValueTelemetry> transform(ValueTelemetry input) {
                return new Iteration<ValueTelemetry>(input, i++);
            }
        });

        f1.addConsumer(end1);
        f2.addConsumer(end2);
        wrap.addConsumer(end3);

        p1.getClass().getMethod("work", new Class[0]).invoke(p1, new Object[0]);
        p1.getClass().getMethod("work", new Class[0]).invoke(p1, new Object[0]);
        p1.getClass().getMethod("work", new Class[0]).invoke(p1, new Object[0]);
        p2.getClass().getMethod("work", new Class[0]).invoke(p2, new Object[0]);
        p2.getClass().getMethod("work", new Class[0]).invoke(p2, new Object[0]);
        s.work(); // 3, 2
        p1.getClass().getMethod("work", new Class[0]).invoke(p1, new Object[0]);
        p1.getClass().getMethod("work", new Class[0]).invoke(p1, new Object[0]);
        p1.getClass().getMethod("work", new Class[0]).invoke(p1, new Object[0]);
        p2.getClass().getMethod("work", new Class[0]).invoke(p2, new Object[0]);
        p2.getClass().getMethod("work", new Class[0]).invoke(p2, new Object[0]);
        p2.getClass().getMethod("work", new Class[0]).invoke(p2, new Object[0]);
        p2.getClass().getMethod("work", new Class[0]).invoke(p2, new Object[0]);
        p2.getClass().getMethod("work", new Class[0]).invoke(p2, new Object[0]);
        s.work(); // 6, 7
        p2.getClass().getMethod("work", new Class[0]).invoke(p2, new Object[0]);
        s.work(); // 6, 8
    }

    public static class Synchronizer implements Producer<Synchronization> {

        private Consumer<? super Synchronization> consumer;
        private Synchronization synchronization = new Synchronization();

        public void addConsumer(Consumer<? super Synchronization> consumer) {
            this.consumer = consumer;
        }

        public void work() {
            synchronization = new Synchronization(synchronization.getValue() + 1);
            consumer.notifyOf(this);
        }

        public Synchronization getValue() {
            return synchronization;
        }
    }
}
