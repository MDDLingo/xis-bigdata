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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

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
			
				if (model.getLength() == 2) {
					xmi = (Element) model.item(1);
					NodeList childNodes = xmi.getChildNodes();
					
			        for (int i = 0; i < childNodes.getLength(); i++) {
			            xmi.getParentNode().insertBefore(childNodes.item(i).cloneNode(true), null);
			        }
					xmi.getParentNode().removeChild(xmi);
					
					//Relocate Associations
					XPath path = XPathFactory.newInstance().newXPath();
					XPathExpression expr = path.compile("/*/*/packagedElement[@*[name()='xmi:type']='uml:Package']");
					Element umlPackage = (Element)((NodeList) expr.evaluate(docEA, XPathConstants.NODESET)).item(0);
					expr = path.compile("/*/*/packagedElement[@*[name()='xmi:type']='uml:Association']");
					NodeList associations = (NodeList) expr.evaluate(docEA, XPathConstants.NODESET);
					java.util.ArrayList<Element> assocs = new java.util.ArrayList<Element>();
					
					for (int i = 0; i < associations.getLength(); i++) {
						Element assoc = (Element) associations.item(i);
						assocs.add(assoc);
					}
					
					for (Element ass : assocs) {
						//Add Missing Attributes
						ass.setAttribute("visibility", "public");
						ass.removeAttribute("memberEnd");
						umlPackage.insertBefore(ass, null);
						
						NodeList ownedEnds = ass.getElementsByTagName("ownedEnd");
						//MemberEnd #1
						Element memberEnd = docEA.createElement("memberEnd");
						String value = ((Element)ownedEnds.item(0)).getAttribute("xmi:id"); 
						memberEnd.setAttribute("xmi:idref", value);
						ass.insertBefore(memberEnd, null);
						
						String type = ((Element)ownedEnds.item(0)).getAttribute("type");
						Element typeTag = docEA.createElement("type");
						typeTag.setAttribute("xmi:idref", type);
						((Element)ownedEnds.item(0)).appendChild(typeTag);
						
						//MemberEnd #2
						memberEnd = docEA.createElement("memberEnd");
						value = ((Element)ownedEnds.item(1)).getAttribute("xmi:id"); 
						memberEnd.setAttribute("xmi:idref", value);
						ass.insertBefore(memberEnd, null);
						
						type = ((Element)ownedEnds.item(1)).getAttribute("type");
						typeTag = docEA.createElement("type");
						typeTag.setAttribute("xmi:idref", type);
						((Element)ownedEnds.item(1)).appendChild(typeTag);
					}
					
					//Relocate XIS-Mobile Stereotypes
					Element umlModel = (Element) docEA.getElementsByTagName("uml:Model").item(0);
					expr = path.compile("/*/*[starts-with(name(), 'XIS')]");
					NodeList stereotypes = (NodeList) expr.evaluate(docEA, XPathConstants.NODESET);
					
					for (int i = 0; i < stereotypes.getLength(); i++) {
						Element s = (Element) stereotypes.item(i);
						s = (Element) docEA.renameNode(s, "", s.getTagName().replace("XIS", "XIS-"));
						umlModel.insertBefore(s, null);
					}
					
					Element profileApplication = (Element) docEA.getElementsByTagName("profileApplication").item(0);
					profileApplication.getParentNode().removeChild(profileApplication);
				}
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
		InputStream f = Emf2EaXMIAdapter.class.getResourceAsStream("result.uml");
//		InputStream f = Emf2EaXMIAdapter.class.getResourceAsStream("refugees.uml");
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
