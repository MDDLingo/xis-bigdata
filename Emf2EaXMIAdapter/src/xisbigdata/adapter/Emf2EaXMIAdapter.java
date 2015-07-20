package xisbigdata.adapter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Emf2EaXMIAdapter {
	
	public static void main(String[] args) {
		File f = new File("src/xisbigdata/adapter/Template.xml");
		
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document docEA = builder.parse(f);
			Element xmiElement = docEA.getDocumentElement();
			xmiElement.normalize();

			NodeList model = docEA.getElementsByTagName("xmi:XMI");
			
			if (model.getLength() == 1) {
				Element xmi = (Element) model.item(0);
				addUMLContent(builder, docEA, xmi);
			}
			
			NodeList xmiPackages = docEA.getElementsByTagName("uml:Package");
			
			if (xmiPackages != null) {
				Element p = (Element) xmiPackages.item(0);
				p.removeAttribute("xmi:version");
				p.removeAttribute("xmlns:xmi");
				p.removeAttribute("xmlns:uml");
				p.setAttribute("xmi:type", "uml:Model");
				docEA.renameNode(p, "", "uml:Model");
			}

			NodeList typesNodes = docEA.getElementsByTagName("type");
			
			for (int i = 0; i < typesNodes.getLength(); i++) {
				Element el = (Element) typesNodes.item(i);
				setTypeIdRef(el);
			}
			
			File nFile = new File("aaa.xmi");

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			DOMSource source = new DOMSource(docEA);
			StreamResult result = new StreamResult(nFile);

			// StreamResult result2 = new StreamResult(System.out);

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(source, result);
			// transformer.transform(source, result2);
			System.out.println("Document successfully parsed!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void addUMLContent(DocumentBuilder builder,
			Document document, Element element) throws SAXException,
			IOException {
//		InputStream f = Emf2EaXMIAdapter.class.getResourceAsStream("result.uml");
		InputStream f = Emf2EaXMIAdapter.class.getResourceAsStream("refugees.uml");
		Document doc = builder.parse(f);
		Element profile = doc.getDocumentElement();
		profile.normalize();
		element.appendChild(document.importNode(profile, true));
	}
	
	private static void setTypeIdRef(Element el) {
		String type = el.getAttribute("href");
		
		if (type.contains("#Boolean")) {
			el.removeAttribute("href");
			el.removeAttribute("xmi:type");
			el.setAttribute("xmi:idref", "EAJava_bool");
		} else if (type.contains("#Integer")) {
			el.removeAttribute("href");
			el.removeAttribute("xmi:type");
			el.setAttribute("xmi:idref", "EAJava_int");
		} else if (type.contains("#Real") || type.contains("#UnlimitedNatural")) {
			el.removeAttribute("href");
			el.removeAttribute("xmi:type");
			el.setAttribute("xmi:idref", "EAJava_double");
		} else if (type.contains("#String")) {
			el.removeAttribute("href");
			el.removeAttribute("xmi:type");
			el.setAttribute("xmi:idref", "EAJava_String"); 
		}
	}

}
