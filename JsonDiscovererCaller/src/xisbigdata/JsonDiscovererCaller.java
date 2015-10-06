package xisbigdata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import fr.inria.atlanmod.discoverer.JsonDiscoverer;
import fr.inria.atlanmod.discoverer.JsonSource;

public class JsonDiscovererCaller {

	public static String RESULT_TEST_FILE = "./result.ecore";
	public static String JSON_TEST = " { \"codeLieu\":\"CRQU4\", \"libelle\":\"Place du Cirque\" }";
	
	public static void main(String[] args) {
		URL url = JsonDiscovererCaller.class.getResource("JsonDiscovererCaller.class");
		String jsonText = "";//JSON_TEST;
		String[] cenas = { "test.json" }; 
		args = cenas;
		
		if (args.length == 1) {
			
			try {
				BufferedReader br = new BufferedReader(new FileReader(args[0]));
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();

				while (line != null) {
					sb.append(line);
					sb.append(System.lineSeparator());
					line = br.readLine();
				}

				jsonText = sb.toString();
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
		JsonSource source = new JsonSource("Discovered");
		source.addJsonDef(jsonText);
		
		JsonDiscoverer discoverer = new JsonDiscoverer();
		EPackage discoveredModel = discoverer.discoverMetamodel(source);
		
		ResourceSet rset = new ResourceSetImpl();
		rset.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		
		Resource res2 = rset.createResource(URI.createFileURI("C:/Users/User/Desktop/agora.ecore"));
		res2.getContents().add(discoveredModel);
		
		try {
			res2.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		String graphvizPath = "../graphviz-2.38/bin/dot.exe";
//		
//		if (url.toString().contains("rsrc:")) {
//			graphvizPath = "graphviz-2.38/bin/dot.exe";
//		}
//		
//		ModelDrawer drawer = new ModelDrawer(new File("./"), new File(graphvizPath));
//		List<EObject> toDraw = new ArrayList<>();
//		toDraw.add(discoveredModel);
//		drawer.draw(toDraw, new File("./result.jpg"));
	}
}
