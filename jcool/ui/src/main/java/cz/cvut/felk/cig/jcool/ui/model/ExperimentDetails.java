/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.felk.cig.jcool.ui.model;

import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.DistanceFunction;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.EvolutionaryOptimizationMethod;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.Representation;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.ReproductionOperator;
import cz.cvut.felk.cig.jcool.benchmark.method.evolutionary.operators.reproduction.HillClimbingMutationOperator;
import cz.cvut.felk.cig.jcool.core.*;
import cz.cvut.felk.cig.jcool.ui.util.ViewUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.ytoh.configurations.context.MutableContext;
import org.ytoh.configurations.util.PropertyExtractor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

/**
 * Model for ExperimentDetails view. Cares only about method and methodComponents tabs. The rest is controlled directly by events from ExperimentSetup.
 * Method and methodComponents are taken care of due to erasure of improper properties which are no longer in context and dynamic filtration of context due to current representation used in EvolutionaryMethod.
 * @author miklamar
 */
public class ExperimentDetails implements PropertyChangeListener {

    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * Currently showed optimization method which components are to be displayed in MethodComponents tab.
     */
    protected OptimizationMethod method;
    /**
     * Hack for SequentialNiching and other optimization methods that have EvolutionaryOptimizationMethod as their component.
     * Will be processed in filtrateContext method.
     */
    protected EvolutionaryOptimizationMethod innerEvolutionaryMethod;
    /**
     * previous representation instance to prevent repetitive context filtration when there was no change in representation
     */
    protected Class<? extends Representation> oldRepresentationClass;

    private MutableContext context;
    private PropertyExtractor extractor;
    private ExperimentSetup setup;

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getNewValue() == evt.getOldValue()) {
            return;
        }
        // new optimization method
        if (evt.getNewValue() != null && evt.getPropertyName().equals("method")) {
            this.method = (OptimizationMethod) evt.getNewValue();
        }

        this.filtrateContext(evt);

        // process event itself
        if ((evt.getPropertyName().equals("method") || evt.getPropertyName().equals("value")) && this.method != null) {

            List<Object> components = ViewUtils.getComponents(this.method, this.extractor, this.context, true);
            // draw PropertyTable for extracted components
            this.support.firePropertyChange(new PropertyChangeEvent(evt.getSource(), "method", null, this.method));
            this.support.firePropertyChange(new PropertyChangeEvent(evt.getSource(), "methodComponents", null, components));
        }
    }

    /**
     * Makes conditional filtration of given context depending on inner logic.
     * In this case filtrates ReproductionOperators and DistanceFunctions if method is instance of EvolutionaryOptimizationMethod
     * or if inner component is instance of EvolutionaryOptimizationMethod.
     * Outer evolutionary method prevails innerEvolutionaryMethod.
     *
     * @param evt - event that triggered this action.
     */
    @SuppressWarnings("unchecked")
    protected void filtrateContext(PropertyChangeEvent evt) {
        // method is changed, then underlying method mus be dropped as well
        if (evt.getPropertyName().equals("method")) {
            this.innerEvolutionaryMethod = null;
        }

        // process event if key/value matches and we have either EvolutionaryMethod, innerEvolutionaryMethod or newValue is EvolutionaryMethod.
        if ((evt.getPropertyName().equals("method") || evt.getPropertyName().equals("value"))
                && (this.method instanceof EvolutionaryOptimizationMethod
                || ((evt.getNewValue() != null && evt.getNewValue() instanceof EvolutionaryOptimizationMethod) || (this.innerEvolutionaryMethod != null)))) {
            final Class<? extends Representation> representationClass;
            if (this.method instanceof EvolutionaryOptimizationMethod) {
                representationClass = ((EvolutionaryOptimizationMethod) this.method).getRepresentationType();
            } else {
                // initialize innerEvolutionaryMethod, because previous was dropped
                if (evt.getNewValue() instanceof EvolutionaryOptimizationMethod) {
                    this.innerEvolutionaryMethod = (EvolutionaryOptimizationMethod) evt.getNewValue();
                }
                representationClass = this.innerEvolutionaryMethod.getRepresentationType();
            }
            // if the representation is the same, then stop processing
            if (this.oldRepresentationClass == representationClass) {
                return;
            }
            // register "reproductionOperators"
            List<? extends ReproductionOperator> reproductionOperators = (List<? extends ReproductionOperator>) CollectionUtils.select(context.getList(ReproductionOperator.class, "allReproductionOperators"), new Predicate() {

                public boolean evaluate(Object o) {
                    return o instanceof ReproductionOperator && ((ReproductionOperator) o).getAcceptableType().isAssignableFrom(representationClass);
                }
            });
            this.context.register(ReproductionOperator.class, reproductionOperators, "reproductionOperators");
            // register "distanceFunctions"
            List<? extends DistanceFunction> distanceFunctions = (List<? extends DistanceFunction>) CollectionUtils.select(context.getList(DistanceFunction.class, "allDistanceFunctions"), new Predicate() {

                public boolean evaluate(Object o) {
                    return o instanceof DistanceFunction && ((DistanceFunction) o).getAcceptableType().isAssignableFrom(representationClass);
                }
            });
            this.context.register(DistanceFunction.class, distanceFunctions, "distanceFunctions");
            // register "hillClimbingMutationOperators"
            List<? extends HillClimbingMutationOperator> hillClimbingMutationOperators = (List<? extends HillClimbingMutationOperator>) CollectionUtils.select(context.getList(HillClimbingMutationOperator.class, "allHillClimbingMutationOperators"), new Predicate() {

                public boolean evaluate(Object o) {
                    return o instanceof HillClimbingMutationOperator && ((HillClimbingMutationOperator) o).getAcceptableType().isAssignableFrom(representationClass);
                }
            });
            this.context.register(HillClimbingMutationOperator.class, hillClimbingMutationOperators, "hillClimbingMutationOperators");
            // register "mutationOperators"
            List<? extends ReproductionOperator> mutationOperators = (List<? extends ReproductionOperator>) CollectionUtils.select(context.getList(ReproductionOperator.class, "allMutationReproductionOperators"), new Predicate() {

                public boolean evaluate(Object o) {
                    return o instanceof ReproductionOperator && ((ReproductionOperator) o).getAcceptableType().isAssignableFrom(representationClass);
                }
            });
            this.context.register(ReproductionOperator.class, mutationOperators, "mutationReproductionOperators");

            // finally, store the new representation class
            this.oldRepresentationClass = representationClass;
        }
    }

    /**
     * Sets current context for extraction of all filtrated lists.
     * @param context - Context that might have registered "visualizations" key.
     */
    public void setContext(MutableContext context) {
        this.context = context;
    }

    /**
     * Registers this object as PropertyChangeListener of given ExperimentSetup.
     * @param setup - ExperimentSetup that is currently instantiated.
     */
    public void setExperimentSetup(ExperimentSetup setup) {
        // if any, then remove from previous ExperimentSetup from PropertyChangeListeners
        if (this.setup != null) {
            this.setup.removePropertyChangeListener(this);
        }
        this.setup = setup;
        this.setup.addPropertyChangeListener(this);
    }

    /**
     * Sets extractor for extracting properties from method with the same context and settings as in entire application.
     * @param extractor - propertyExtractor for extracting properties from current method.
     */
    public void setExtractor(PropertyExtractor extractor) {
        this.extractor = extractor;
    }

    //////////////////////////////
    // Property change support
    //////////////////////////////
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }
}
