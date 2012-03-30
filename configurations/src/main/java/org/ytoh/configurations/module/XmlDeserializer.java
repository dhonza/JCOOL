package org.ytoh.configurations.module;

import java.beans.XMLDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.Validate;
import org.ytoh.configurations.module.BasicModule.BasicModuleBuilder;

/**
 *
 * @author ytoh
 */
public class XmlDeserializer implements ModuleDeserializer {

    /** */
    private String baseDir;

    public void deserializeModule(Module parent) {
		File inputDirectory = new File(baseDir + Modules.getModuleFullName(parent));

		Validate.isTrue(inputDirectory.exists(), "The module directory must exist. " + inputDirectory.getAbsolutePath());
		Validate.isTrue(inputDirectory.isDirectory(), "There needs to be a directory for the module to be stored in.");
		Validate.isTrue(inputDirectory.canRead(), "The module directory must be readable.");

        // then deserialize its child modules
        for (File file : inputDirectory.listFiles()) {
            if(file.isDirectory()) { // handle all directories as child modules
                deserializeModule(file, parent);
            }
        }
    }

    private void deserializeModule(File directory, Module parent) {
        // deserialize components bolonging to this module
        BasicModuleBuilder builder = BasicModule.withName(directory.getName(), parent);
        for (File file : directory.listFiles()) {
            if(file.isFile() && file.getName().endsWith(".xml")) { // all .xml files
                try {
                    XMLDecoder decoder = new XMLDecoder(new FileInputStream(file));
                    Object o = decoder.readObject();
                    builder.withComponent(o, file.getName().substring(0, file.getName().length() - 4));
                    decoder.close();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(XmlDeserializer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        deserializeModule(builder.build());
    }

    public void setConfiguration(Configuration configuration) {
        this.baseDir = configuration.getString("modules.baseDir");
    }

    public void setConfigurationDirectory(String path) {
        baseDir = path;
    }

    public String getConfigurationDirectory() {
        return baseDir;
    }
}
