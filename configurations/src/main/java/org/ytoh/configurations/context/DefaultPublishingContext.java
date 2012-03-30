/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ytoh.configurations.context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Factory;
import org.apache.commons.collections.map.LazyMap;

/**
 *
 * @author ytoh
 */
public class DefaultPublishingContext implements PublishingContext, MutableContext {

    /** */
    private MutableContext delegate;
    /** */
    private Map<Class<?>, Map<String, Set<Subscriber<?>>>> subscribers;

    /**
     *
     * @param delegate
     */
    public DefaultPublishingContext(MutableContext delegate) {
        this.delegate = delegate;

        subscribers = LazyMap.decorate(new HashMap<Class<?>, Map<String, Set<Subscriber<?>>>>(), new Factory() {

            public Object create() {
                return LazyMap.decorate(new HashMap<String, Set<Subscriber<?>>>(), new Factory() {

                    public Object create() {
                        return new HashSet<Subscriber<?>>();
                    }
                });
            }
        });
    }

    public <T> List<T> getList(Class<T> type, String key) {
        return delegate.getList(type, key);
    }

    public <T> T get(Class<T> type, String key) {
        return delegate.get(type, key);
    }

    public <T> void subscribeTo(Class<T> type, String key, Subscriber<T> subscriber) {
        subscribers.get(type).get(key).add(subscriber);
    }

    public <T> void register(Class<T> type, List<? extends T> options, String key) {
        CollectionUtils.forAllDo(options, new Closure() {

            public void execute(Object input) {
                if (input instanceof ContextAware) {
                    ((ContextAware) input).registerContext(DefaultPublishingContext.this);
                }
            }
        });

        delegate.register(type, options, key);

        for (Subscriber<?> subscriber : subscribers.get(type).get(key)) {
            ((Subscriber<T>)subscriber).notifyOf(this, options, key);
        }
    }

    public <T, E extends T> void register(Class<T> type, E value, String key) {
        if (value instanceof ContextAware) {
            ((ContextAware) value).registerContext(DefaultPublishingContext.this);
        }
        
        delegate.register(type, value, key);
    }
}
