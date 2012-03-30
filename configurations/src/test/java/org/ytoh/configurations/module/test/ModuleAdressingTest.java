package org.ytoh.configurations.module.test;

import org.junit.Before;
import org.junit.Test;
import org.ytoh.configurations.module.BasicModule;
import org.ytoh.configurations.module.Module;
import org.ytoh.configurations.module.Modules;
import org.ytoh.configurations.test.Bean2;
import static org.junit.Assert.*;

/**
 *
 * @author ytoh
 */
public class ModuleAdressingTest {
    private Module core;
    private Module moduleA;
    private Module moduleB;
    private Module extension;
    private Module module1;

    @Before
    public void setUp() {
        core = BasicModule.withName("core", Modules.getRootModule())
                .withComponent(new Object())
                .build();

        moduleA = BasicModule.withName("moduleA", core)
                .withComponent(new Object())
                .build();

        moduleB = BasicModule.withName("moduleB", core)
                .withComponent(new Object())
                .build();

        extension = BasicModule.withName("extension", Modules.getRootModule())
                .withComponent(new Object())
                .build();

        module1 = BasicModule.withName("module1", extension)
                .withComponent(new Bean2())
                .build();
    }
    
    @Test
    public void moduleFullNames() {
        assertEquals("/", Modules.getRootModule().getName());
        assertEquals("/core", Modules.getModuleFullName(core));
        assertEquals("/core/moduleA", Modules.getModuleFullName(moduleA));
        assertEquals("/core/moduleB", Modules.getModuleFullName(moduleB));
        assertEquals("/extension", Modules.getModuleFullName(extension));
        assertEquals("/extension/module1", Modules.getModuleFullName(module1));
    }
    
    @Test
    public void moduleFromName() {
        assertEquals(Modules.getRootModule(), Modules.getModule("/"));
        assertEquals(core, Modules.getModule("/core"));
        assertEquals(moduleA, Modules.getModule("/core/moduleA"));
        assertEquals(moduleB, Modules.getModule("/core/moduleB"));
        assertEquals(extension, Modules.getModule("/extension"));
        assertEquals(module1, Modules.getModule("/extension/module1"));
    }
}
