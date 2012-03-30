package org.ytoh.configurations.module.test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ytoh.configurations.module.BasicModule;
import org.ytoh.configurations.module.Module;
import org.ytoh.configurations.module.ModuleSerializer;
import org.ytoh.configurations.module.Modules;
import org.ytoh.configurations.module.XmlSerializer;
import static org.junit.Assert.*;

/**
 *
 * @author ytoh
 */
public class XmlSerializerTest {
    private static File configurationDirectory;
    private ModuleSerializer moduleSerializer;
    private static Configuration configuration;
    private static final String MODULE_NAME = "SERIALIZATION_TEST";
    private static final String COMPONENT_1_NAME = "COMPONENT_1";
    private static final String COMPONENT_2_NAME = "COMPONENT_2";
    private Module module;

    @BeforeClass
    public static void prepare() {
        configurationDirectory = new File("test-module-configuration");
        configurationDirectory.mkdir();
        configuration = new PropertiesConfiguration();
        configuration.addProperty("modules.baseDir", "test-module-configuration");
    }

    @Before
    public void setUp() {
        moduleSerializer = new XmlSerializer();
        moduleSerializer.setConfiguration(configuration);
        module = BasicModule.withName(MODULE_NAME, Modules.getRootModule())
                .withComponent(new Object(), COMPONENT_1_NAME)
                .withComponent(new Object(), COMPONENT_2_NAME)
                .build();
    }

    @Test
    public void serialize() {
        moduleSerializer.serializeModule(module);
        List<File> files = Arrays.asList(configurationDirectory.listFiles());
        assertEquals(1, files.size());
        List<File> found = (List<File>) CollectionUtils.select(files, new Predicate() {

            public boolean evaluate(Object object) {
                return MODULE_NAME.equals(((File) object).getName());
            }
        });
        assertEquals(1, found.size());
        assertEquals(2,found.get(0).listFiles().length);
    }

    @AfterClass
    public static void cleanup() throws IOException {
        FileUtils.deleteDirectory(configurationDirectory);
    }
}
