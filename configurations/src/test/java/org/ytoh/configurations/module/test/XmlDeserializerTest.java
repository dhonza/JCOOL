package org.ytoh.configurations.module.test;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.apache.commons.collections.Predicate;
import org.ytoh.configurations.module.BasicModule;
import org.ytoh.configurations.module.Module;
import org.ytoh.configurations.module.ModuleDeserializer;
import org.ytoh.configurations.module.Modules;
import org.ytoh.configurations.module.XmlDeserializer;

/**
 *
 * @author ytoh
 */
public class XmlDeserializerTest {
    private ModuleDeserializer moduleDeserializer;
    private static Configuration configuration;
    private Module deserializationRoot = BasicModule.withName("deserialization-test", Modules.getRootModule()).build();

    @BeforeClass
    public static void prepare() {
        configuration = new PropertiesConfiguration();
        configuration.addProperty("modules.baseDir", "test-configuration");
    }

    @Before
    public void setUp() {
        moduleDeserializer = new XmlDeserializer();
        moduleDeserializer.setConfiguration(configuration);
    }

    @Test
    public void deserialize() {
        moduleDeserializer.deserializeModule(deserializationRoot);
        assertEquals(2, deserializationRoot.getChildren().size());

        Module core = (Module) CollectionUtils.find(deserializationRoot.getChildren(), new Predicate() {

            public boolean evaluate(Object object) {
                return "core".equals(((Module)object).getName());
            }
        });

        Module extension = (Module) CollectionUtils.find(deserializationRoot.getChildren(), new Predicate() {

            public boolean evaluate(Object object) {
                return "extension".equals(((Module)object).getName());
            }
        });

        assertNotNull(core);
        assertNotNull(extension);

        Module moduleA = (Module) CollectionUtils.find(core.getChildren(), new Predicate() {

            public boolean evaluate(Object object) {
                return "moduleA".equals(((Module)object).getName());
            }
        });

        Module moduleB = (Module) CollectionUtils.find(core.getChildren(), new Predicate() {

            public boolean evaluate(Object object) {
                return "moduleB".equals(((Module)object).getName());
            }
        });

        Module module1 = (Module) CollectionUtils.find(extension.getChildren(), new Predicate() {

            public boolean evaluate(Object object) {
                return "module1".equals(((Module)object).getName());
            }
        });

        assertNotNull(moduleA);
        assertNotNull(moduleB);
        assertNotNull(module1);

        assertEquals(1, moduleA.getComponents().size());
        assertEquals(1, moduleB.getComponents().size());
        assertEquals(1, module1.getComponents().size());
    }
}
