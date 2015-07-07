package xisbigdata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import fr.inria.atlanmod.discoverer.JsonDiscoverer;
import fr.inria.atlanmod.discoverer.JsonSource;
import fr.inria.atlanmod.json.discoverer.zoo.ModelDrawer;

public class JsonDiscovererCaller {

	public static String RESULT_TEST_FILE = "./result.ecore";
	public static String JSON_TEST = " { \"codeLieu\":\"CRQU4\", \"libelle\":\"Place du Cirque\" }";
	
	public static void main(String[] args) {
		JsonSource source = new JsonSource("Discovered");
		source.addJsonDef(JSON_TEST);
		
		JsonDiscoverer discoverer = new JsonDiscoverer();
		EPackage discoveredModel = discoverer.discoverMetamodel(source);
		
		ResourceSet rset = new ResourceSetImpl();
		rset.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		
		Resource res2 = rset.createResource(URI.createFileURI(RESULT_TEST_FILE));
		res2.getContents().add(discoveredModel);
		
		try {
			res2.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ModelDrawer drawer = new ModelDrawer(
			new File("../JsonDiscovererCaller/json/"), 
			new File("../graphviz-2.38/bin/dot.exe"));
		List<EObject> toDraw = new ArrayList<>();
		toDraw.add(discoveredModel);
		drawer.draw(toDraw, new File("./json/issue3.jpg"));
	}
}
