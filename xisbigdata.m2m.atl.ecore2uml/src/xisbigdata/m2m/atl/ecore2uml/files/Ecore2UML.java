/*******************************************************************************
 * Copyright (c) 2010, 2012 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package xisbigdata.m2m.atl.ecore2uml.files;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.m2m.atl.common.ATLExecutionException;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.core.IExtractor;
import org.eclipse.m2m.atl.core.IInjector;
import org.eclipse.m2m.atl.core.IModel;
import org.eclipse.m2m.atl.core.IReferenceModel;
import org.eclipse.m2m.atl.core.ModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFExtractor;
import org.eclipse.m2m.atl.core.emf.EMFInjector;
import org.eclipse.m2m.atl.core.emf.EMFModelFactory;
import org.eclipse.m2m.atl.core.launch.ILauncher;
import org.eclipse.m2m.atl.engine.emfvm.launch.EMFVMLauncher;
import org.eclipse.ocl.pivot.internal.resource.OCLASResourceFactory;
import org.eclipse.ocl.pivot.model.OCLstdlib;
import org.eclipse.ocl.pivot.resource.ASResource;
import org.eclipse.ocl.pivot.utilities.OCL;
import org.eclipse.ocl.pivot.utilities.PivotStandaloneSetup;
import org.eclipse.ocl.xtext.completeocl.CompleteOCLStandaloneSetup;
import org.eclipse.uml2.uml.resource.UMLResource;

/**
 * Entry point of the 'Ecore2UML' transformation module.
 */
public class Ecore2UML {

	/**
	 * The property file. Stores module list, the metamodel and library locations.
	 * @generated
	 */
	private Properties properties;
	
	/**
	 * The IN model.
	 * @generated
	 */
	protected IModel inModel;	
	
	/**
	 * The IN_Library model.
	 * @generated
	 */
	protected IModel in_libraryModel;	
	
	/**
	 * The IN_XISMobile model.
	 * @generated
	 */
	protected IModel in_xismobileModel;	
	
	/**
	 * The OUT model.
	 * @generated
	 */
	protected IModel outModel;	
		
	/**
	 * The main method.
	 * 
	 * @param args
	 *            are the arguments
	 * @generated NOT
	 */
	public static void main(String[] args) {
		try {
			if (args.length < 4) {
				System.out.println("Arguments not valid : {IN_model_path, IN_Library_model_path, IN_XISMobile_model_path, OUT_model_path}.");
			} else {
				Ecore2UML runner = new Ecore2UML();
				runner.loadModels(args[0], args[1], args[2]);
				runner.doEcore2UML(new NullProgressMonitor());
				runner.saveModels(args[3]);
			}
		} catch (ATLCoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ATLExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor.
	 *
	 * @generated NOT
	 */
	public Ecore2UML() throws IOException {
		properties = new Properties();
		properties.load(getFileURL("Ecore2UML.properties").openStream());
		EPackage.Registry.INSTANCE.put(getMetamodelUri("UML"), org.eclipse.uml2.uml.UMLPackage.eINSTANCE);
		EPackage.Registry.INSTANCE.put(getMetamodelUri("Ecore"), org.eclipse.emf.ecore.EcorePackage.eINSTANCE);
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		
		EPackage.Registry.INSTANCE.put("http://www.eclipse.org/uml2/4.0.0/UML", org.eclipse.uml2.uml.UMLPackage.eINSTANCE);
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(org.eclipse.uml2.uml.resource.UMLResource.FILE_EXTENSION, org.eclipse.uml2.uml.resource.UMLResource.Factory.INSTANCE);
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(org.eclipse.uml2.uml.resource.UMLResource.METAMODEL_FILE_EXTENSION, org.eclipse.uml2.uml.resource.UMLResource.Factory.INSTANCE);
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(org.eclipse.uml2.uml.resource.UMLResource.PROFILE_FILE_EXTENSION, org.eclipse.uml2.uml.resource.UMLResource.Factory.INSTANCE);
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(org.eclipse.uml2.uml.resource.UMLResource.LIBRARY_FILE_EXTENSION, org.eclipse.uml2.uml.resource.UMLResource.Factory.INSTANCE);
		
		URI baseUri = 
			URI.createURI("jar:file:/C:/Users/User/Desktop/eclipse/plugins/org.eclipse.uml2.uml.resources_5.1.0.v20150906-1225.jar!/");
			URIConverter.URI_MAP.put(URI.createURI( UMLResource.LIBRARIES_PATHMAP ), 
			baseUri.appendSegment( "libraries" ).appendSegment( "" ));
			URIConverter.URI_MAP.put(URI.createURI( UMLResource.METAMODELS_PATHMAP 
			), baseUri.appendSegment( "metamodels" ).appendSegment( "" ));
			URIConverter.URI_MAP.put(URI.createURI( UMLResource.PROFILES_PATHMAP ), 
			baseUri.appendSegment( "profiles" ).appendSegment( "" ));
		
		// Register Pivot globally (resourceSet == null)
		// Alternatively register it just for your resource set (see Javadoc).		
		PivotStandaloneSetup.doSetup();
		OCLstdlib.install();
		OCL.newInstance(OCL.CLASS_PATH);
		CompleteOCLStandaloneSetup.doSetup();
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
			ASResource.FILE_EXTENSION, OCLASResourceFactory.getInstance());
	}
	
	/**
	 * Load the input and input/output models, initialize output models.
	 * 
	 * @param inModelPath
	 *            the IN model path
	 * @param in_libraryModelPath
	 *            the IN_Library model path
	 * @param in_xismobileModelPath
	 *            the IN_XISMobile model path
	 * @throws ATLCoreException
	 *             if a problem occurs while loading models
	 *
	 * @generated
	 */
	public void loadModels(String inModelPath, String in_libraryModelPath, String in_xismobileModelPath) throws ATLCoreException {
		ModelFactory factory = new EMFModelFactory();
		IInjector injector = new EMFInjector();
	 	IReferenceModel umlMetamodel = factory.newReferenceModel();
		injector.inject(umlMetamodel, getMetamodelUri("UML"));
	 	IReferenceModel ecoreMetamodel = factory.newReferenceModel();
		injector.inject(ecoreMetamodel, getMetamodelUri("Ecore"));
		this.inModel = factory.newModel(ecoreMetamodel);
		injector.inject(inModel, inModelPath);
		this.in_libraryModel = factory.newModel(umlMetamodel);
		injector.inject(in_libraryModel, in_libraryModelPath);
		this.in_xismobileModel = factory.newModel(umlMetamodel);
		injector.inject(in_xismobileModel, in_xismobileModelPath);
		this.outModel = factory.newModel(umlMetamodel);
	}
	
	/**
	 * Save the output and input/output models.
	 * 
	 * @param outModelPath
	 *            the OUT model path
	 * @throws ATLCoreException
	 *             if a problem occurs while saving models
	 *
	 * @generated
	 */
	public void saveModels(String outModelPath) throws ATLCoreException {
		IExtractor extractor = new EMFExtractor();
		extractor.extract(outModel, outModelPath);
	}

	/**
	 * Transform the models.
	 * 
	 * @param monitor
	 *            the progress monitor
	 * @throws ATLCoreException
	 *             if an error occurs during models handling
	 * @throws IOException
	 *             if a module cannot be read
	 * @throws ATLExecutionException
	 *             if an error occurs during the execution
	 *
	 * @generated
	 */
	public Object doEcore2UML(IProgressMonitor monitor) throws ATLCoreException, IOException, ATLExecutionException {
		ILauncher launcher = new EMFVMLauncher();
		Map<String, Object> launcherOptions = getOptions();
		launcher.initialize(launcherOptions);
		launcher.addInModel(inModel, "IN", "Ecore");
		launcher.addInModel(in_libraryModel, "IN_Library", "UML");
		launcher.addInModel(in_xismobileModel, "IN_XISMobile", "UML");
		launcher.addOutModel(outModel, "OUT", "UML");
		return launcher.launch("run", monitor, launcherOptions, (Object[]) getModulesList());
	}
	
	/**
	 * Returns an Array of the module input streams, parameterized by the
	 * property file.
	 * 
	 * @return an Array of the module input streams
	 * @throws IOException
	 *             if a module cannot be read
	 *
	 * @generated
	 */
	protected InputStream[] getModulesList() throws IOException {
		InputStream[] modules = null;
		String modulesList = properties.getProperty("Ecore2UML.modules");
		if (modulesList != null) {
			String[] moduleNames = modulesList.split(",");
			modules = new InputStream[moduleNames.length];
			for (int i = 0; i < moduleNames.length; i++) {
				String asmModulePath = new Path(moduleNames[i].trim()).removeFileExtension().addFileExtension("asm").toString();
				modules[i] = getFileURL(asmModulePath).openStream();
			}
		}
		return modules;
	}
	
	/**
	 * Returns the URI of the given metamodel, parameterized from the property file.
	 * 
	 * @param metamodelName
	 *            the metamodel name
	 * @return the metamodel URI
	 *
	 * @generated
	 */
	protected String getMetamodelUri(String metamodelName) {
		return properties.getProperty("Ecore2UML.metamodels." + metamodelName);
	}
	
	/**
	 * Returns the file name of the given library, parameterized from the property file.
	 * 
	 * @param libraryName
	 *            the library name
	 * @return the library file name
	 *
	 * @generated
	 */
	protected InputStream getLibraryAsStream(String libraryName) throws IOException {
		return getFileURL(properties.getProperty("Ecore2UML.libraries." + libraryName)).openStream();
	}
	
	/**
	 * Returns the options map, parameterized from the property file.
	 * 
	 * @return the options map
	 *
	 * @generated
	 */
	protected Map<String, Object> getOptions() {
		Map<String, Object> options = new HashMap<String, Object>();
		for (Entry<Object, Object> entry : properties.entrySet()) {
			if (entry.getKey().toString().startsWith("Ecore2UML.options.")) {
				options.put(entry.getKey().toString().replaceFirst("Ecore2UML.options.", ""), 
				entry.getValue().toString());
			}
		}
		return options;
	}
	
	/**
	 * Finds the file in the plug-in. Returns the file URL.
	 * 
	 * @param fileName
	 *            the file name
	 * @return the file URL
	 * @throws IOException
	 *             if the file doesn't exist
	 * 
	 * @generated
	 */
	protected static URL getFileURL(String fileName) throws IOException {
		final URL fileURL;
		if (isEclipseRunning()) {
			URL resourceURL = Ecore2UML.class.getResource(fileName);
			if (resourceURL != null) {
				fileURL = FileLocator.toFileURL(resourceURL);
			} else {
				fileURL = null;
			}
		} else {
			fileURL = Ecore2UML.class.getResource(fileName);
		}
		if (fileURL == null) {
			throw new IOException("'" + fileName + "' not found");
		} else {
			return fileURL;
		}
	}

	/**
	 * Tests if eclipse is running.
	 * 
	 * @return <code>true</code> if eclipse is running
	 *
	 * @generated
	 */
	public static boolean isEclipseRunning() {
		try {
			return Platform.isRunning();
		} catch (Throwable exception) {
			// Assume that we aren't running.
		}
		return false;
	}
}
