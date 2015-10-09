package xisbigdata;

import java.io.BufferedReader;
import java.io.FileReader;

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

	public static String JSON_TEST = " { \"codeLieu\":\"CRQU4\", \"libelle\":\"Place du Cirque\" }";
	
	public static void main(String[] args) {
		try {
			String jsonText = "";
			String[] testArgs = { "test.json", "C:/Users/User/Desktop/result.ecore" }; 
			args = testArgs;
			
			if (args.length < 2) {
				System.out.println("Arguments not valid : { Json_path, Result_path }");
			}
			else {
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
			}
			
			JsonSource source = new JsonSource("Discovered");
			source.addJsonDef(jsonText);
			
			JsonDiscoverer discoverer = new JsonDiscoverer();
			EPackage discoveredModel = discoverer.discoverMetamodel(source);
			
			ResourceSet rset = new ResourceSetImpl();
			rset.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
			
			Resource res2 = rset.createResource(URI.createFileURI(args[1]));
			res2.getContents().add(discoveredModel);
			res2.save(null);
			
//			String graphvizPath = "../graphviz-2.38/bin/dot.exe";
//			
//			if (url.toString().contains("rsrc:")) {
//				graphvizPath = "graphviz-2.38/bin/dot.exe";
//			}
//			
//			ModelDrawer drawer = new ModelDrawer(new File("./"), new File(graphvizPath));
//			List<EObject> toDraw = new ArrayList<>();
//			toDraw.add(discoveredModel);
//			drawer.draw(toDraw, new File("./result.jpg"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
