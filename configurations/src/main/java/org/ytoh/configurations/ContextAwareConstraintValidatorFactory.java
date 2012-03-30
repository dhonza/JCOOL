package org.ytoh.configurations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import org.ytoh.configurations.context.ContextAware;
import org.ytoh.configurations.context.MutableContext;

/**
 *
 * @author ytoh
 */
public class ContextAwareConstraintValidatorFactory implements ConstraintValidatorFactory, ContextAware {
    
    private final ConstraintValidatorFactory constraintValidatorFactory;
    private MutableContext context;

    public ContextAwareConstraintValidatorFactory(ConstraintValidatorFactory constraintValidatorFactory) {
        this.constraintValidatorFactory = constraintValidatorFactory;
    }

    public void registerContext(MutableContext context) {
        this.context = context;
    }

    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        T instance = constraintValidatorFactory.getInstance(key);
        
        if(ContextAware.class.isAssignableFrom(instance.getClass())) {
            ((ContextAware) instance).registerContext(context);
        }

        return instance;
    }
}
