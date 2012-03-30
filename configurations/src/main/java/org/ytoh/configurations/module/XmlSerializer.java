package org.ytoh.configurations.module;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.Validate;
import org.ytoh.configurations.test.Bean4;
import org.ytoh.configurations.ui.SelectionSetDelegate;

import java.beans.PersistenceDelegate;
import java.beans.XMLEncoder;
import java.beans.ExceptionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ytoh
 */
public class XmlSerializer implements ModuleSerializer {

    /** */
	private String baseDir;
    private boolean verboseExceptions = true;

    private class DelegateHolder {
        private Class<?> servedType;
        private PersistenceDelegate delegate;

        private DelegateHolder(Class servedType, PersistenceDelegate delegate) {
            this.servedType = servedType;
            this.delegate = delegate;
        }

        public Class getServedType() {
            return servedType;
        }

        public PersistenceDelegate getDelegate() {
            return delegate;
        }
    }

    private class SerializerExceptionHandler implements ExceptionListener {

        public void exceptionThrown(Exception e) {
            System.out.flush();
            e.printStackTrace();
            System.err.flush();
        }
    }

    private ArrayList<DelegateHolder> delegates;
    private SerializerExceptionHandler excHandler = new SerializerExceptionHandler();


    public XmlSerializer() {
        delegates = new ArrayList<DelegateHolder>(5);
    }

    public void addNewDelegate(Class<?> servedType, PersistenceDelegate delegate) {
        delegates.add(new DelegateHolder(servedType, delegate));
    }

    private XMLEncoder generateEncoder(File path, String fname) throws FileNotFoundException {
        File outputFile = new File(path, fname + ".xml");
        XMLEncoder encoder = new XMLEncoder(new FileOutputStream(outputFile));

        for (int i = 0; i < delegates.size(); i++) {
            DelegateHolder dh = delegates.get(i);
            encoder.setPersistenceDelegate(dh.getServedType(), dh.getDelegate());
        }

        return encoder;
    }

	public void serializeModule(Module module) {
		String moduleFullName = Modules.getModuleFullName(module);

		File outputDirectory = new File(baseDir + moduleFullName);

        if(!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

		Validate.isTrue(outputDirectory.canWrite(), "The module directory must be writable.");

        // serialize all child modules
        for (Module m : module.getChildren()) {
            serializeModule(m);
        }

        // then serialize the components within this module
		for (String name : module.getComponentNames()) {

			try {
                XMLEncoder encoder = generateEncoder(outputDirectory, name);
                if (verboseExceptions) {
                    encoder.setExceptionListener(excHandler);
                }
                System.out.printf("Writing: %s\n",module.getComponent(name).toString());
				encoder.writeObject(module.getComponent(name));
				encoder.flush();
				encoder.close();
			} catch (FileNotFoundException ex) {
				Logger.getLogger(XmlSerializer.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	public void setConfiguration(Configuration configuration) {
		this.baseDir = configuration.getString("modules.baseDir");
	}

    public void setConfigurationDirectory(String path) {
        this.baseDir = path;
    }

    public String getConfigurationDirectory() {
        return baseDir;
    }

    public boolean isVerboseExceptions() {
        return verboseExceptions;
    }

    public void setVerboseExceptions(boolean verboseExceptions) {
        this.verboseExceptions = verboseExceptions;
    }
}
