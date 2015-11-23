package xisbigdata.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

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
		try {
			if (args.length < 2) {
				System.out.println("Arguments not valid : { EMF_model_path, EA_Result_model_path}.");
			} else {
				InputStream f = Emf2EaXMIAdapter.class.getResourceAsStream("Template.xml");
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document docEA = builder.parse(f);
				Element xmiElement = docEA.getDocumentElement();
				xmiElement.normalize();

				NodeList model = docEA.getElementsByTagName("xmi:XMI");

				if (model.getLength() == 1) {
					Element xmi = (Element) model.item(0);
					addUMLContent(builder, docEA, xmi, args[0]);

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

						Element connectorTemplate = (Element) docEA.getElementsByTagName("connector").item(0);

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
							Element memberEnd2 = docEA.createElement("memberEnd");
							value = ((Element)ownedEnds.item(1)).getAttribute("xmi:id"); 
							memberEnd2.setAttribute("xmi:idref", value);
							ass.insertBefore(memberEnd2, null);

							type = ((Element)ownedEnds.item(1)).getAttribute("type");
							typeTag = docEA.createElement("type");
							typeTag.setAttribute("xmi:idref", type);
							((Element)ownedEnds.item(1)).appendChild(typeTag);

							//Extensions/Connectors/Connector
							Element connectorExt = (Element) connectorTemplate.cloneNode(true);
							connectorExt.setAttribute("xmi:idref", ass.getAttribute("xmi:id"));
							//Connector/Source
							Element sourceOwnedEnd = ((Element)ownedEnds.item(1));
							Element targetOwnedEnd = ((Element)ownedEnds.item(0));
							Element source = (Element) connectorExt.getElementsByTagName("source").item(0);
							source.setAttribute("xmi:idref", memberEnd.getAttribute("xmi:idref"));
							Element sourceModel = (Element) source.getElementsByTagName("model").item(0);
							String sourceName = sourceOwnedEnd.getAttribute("name");
							sourceModel.setAttribute("name", sourceName.substring(0, 1).toUpperCase() + sourceName.substring(1));
							Element sourceRole = (Element) source.getElementsByTagName("role").item(0);
							sourceRole.setAttribute("name", sourceName);
							Element sourceType = (Element) source.getElementsByTagName("type").item(0);
							String sourceLMult = ((Element)sourceOwnedEnd.getElementsByTagName("lowerValue").item(0)).getAttribute("value");
							String sourceUMult = ((Element)sourceOwnedEnd.getElementsByTagName("upperValue").item(0)).getAttribute("value");
							sourceType.setAttribute("multiplicity", sourceLMult + ".." + sourceUMult);
							String sourceAggregation = targetOwnedEnd.getAttribute("aggregation");

							if (sourceAggregation.isEmpty()) {
								sourceType.removeAttribute("aggregation");
							} else {
								sourceType.setAttribute("aggregation", sourceAggregation);
							}
							//Connector/Target
							Element target = (Element) connectorExt.getElementsByTagName("target").item(0);
							target.setAttribute("xmi:idref", memberEnd2.getAttribute("xmi:idref"));
							Element targetModel = (Element) target.getElementsByTagName("model").item(0);
							String targetName = targetOwnedEnd.getAttribute("name");
							targetModel.setAttribute("name", targetName.substring(0, 1).toUpperCase() + targetName.substring(1));
							Element targetRole = (Element) target.getElementsByTagName("role").item(0);
							targetRole.setAttribute("name", targetName);
							Element targetType = (Element) target.getElementsByTagName("type").item(0);
							String targetLMult = ((Element)targetOwnedEnd.getElementsByTagName("lowerValue").item(0)).getAttribute("value");
							String targetUMult = ((Element)targetOwnedEnd.getElementsByTagName("upperValue").item(0)).getAttribute("value");
							targetType.setAttribute("multiplicity", targetLMult + ".." + targetUMult);
							String targetAggregation = sourceOwnedEnd.getAttribute("aggregation");

							if (targetAggregation.isEmpty()) {
								targetType.removeAttribute("aggregation");
							} else {
								targetType.setAttribute("aggregation", targetAggregation);
							}
							//Connector/xrefs
							Element xrefs = (Element) connectorExt.getElementsByTagName("xrefs").item(2);
							String xrefsValue = "$XREFPROP=$XID={"+ UUID.randomUUID() + "}$XID;$NAM=Stereotypes$NAM;$TYP=connector property$TYP;$VIS=Public$VIS;$PAR=0$PAR;$DES=@STEREO;Name=XisEntityAssociation;FQName=XIS-Mobile::XisEntityAssociation;@ENDSTEREO;$DES;$CLT={" + ass.getAttribute("xmi:id")  + "}$CLT;$SUP=&lt;none&gt;$SUP;$ENDXREF;";
							xrefs.setAttribute("value", xrefsValue);
							//Connector/tags
							Element tags = (Element) connectorExt.getElementsByTagName("tags").item(2);
							Element tag = (Element) tags.getElementsByTagName("tag").item(0);
							tag.setAttribute("xmi:id", UUID.randomUUID().toString().replace("-", "_"));
							tag.setAttribute("value", ass.getAttribute("name"));
							connectorTemplate.getParentNode().appendChild(connectorExt);
						}

						//Remove Connector Template
						connectorTemplate.getParentNode().removeChild(connectorTemplate);

						//Set Diagram Properties
						Element diagram = (Element) docEA.getElementsByTagName("diagram").item(0);
						diagram.setAttribute("xmi:id", UUID.randomUUID().toString().replace("-", "_"));
						//diagram/model
						Element diagModel = (Element) diagram.getElementsByTagName("model").item(0);
						diagModel.setAttribute("owner", umlPackage.getAttribute("xmi:id"));
						diagModel.setAttribute("package", umlPackage.getAttribute("xmi:id"));
						//diagram/project
						Element diagProject = (Element) diagram.getElementsByTagName("project").item(0);
						String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
						diagProject.setAttribute("created", dateTime);
						diagProject.setAttribute("modified", dateTime);

						//Set Elements
						Element elementTemplate = (Element) docEA.getElementsByTagName("element").item(0);
						Element elementExt = (Element) elementTemplate.cloneNode(true);
						elementExt.setAttribute("xmi:idref", umlPackage.getAttribute("xmi:id"));
						elementExt.setAttribute("xmi:type", umlPackage.getAttribute("xmi:type"));
						elementExt.setAttribute("name", umlPackage.getAttribute("name"));
						//Element/Model
						Element modelEl = (Element) elementExt.getElementsByTagName("model").item(0);
						modelEl.setAttribute("package", UUID.randomUUID().toString().replace("-", "_"));
						modelEl.setAttribute("package2", UUID.randomUUID().toString().replace("-", "_"));
						modelEl.setAttribute("ea_eleType", "package");
						//Element/properties
						Element propertiesEl = (Element) elementExt.getElementsByTagName("properties").item(0);
						propertiesEl.setAttribute("sType", "Package");
						propertiesEl.setAttribute("stereotype", "Domain View");
						propertiesEl.removeAttribute("isAbstract");
						propertiesEl.removeAttribute("isActive");
						propertiesEl.removeAttribute("isLeaf");
						propertiesEl.removeAttribute("isRoot");
						//Element/Tags
						Element tagsEl = (Element) elementExt.getElementsByTagName("tags").item(0);
						while (tagsEl.hasChildNodes()) {
							tagsEl.removeChild(tagsEl.getFirstChild());
						}
						//Element/xrefs
						Element xrefs = (Element) elementExt.getElementsByTagName("xrefs").item(0);
						String guid = UUID.randomUUID().toString();
						String xrefsValue = "$XREFPROP=$XID={" + UUID.randomUUID() + "}$XID;$NAM=CustomProperties$NAM;$TYP=element property$TYP;$VIS=Public$VIS;$PAR=0$PAR;$DES=@PROP=@NAME=_defaultDiagramType@ENDNAME;@TYPE=string@ENDTYPE;@VALU=XIS-Mobile_Diagrams::DomainViewModel@ENDVALU;@PRMT=@ENDPRMT;@ENDPROP;@PROP=@NAME=_makeComposite@ENDNAME;@TYPE=string@ENDTYPE;@VALU=true@ENDVALU;@PRMT=@ENDPRMT;@ENDPROP;$DES;$CLT={" + guid + "}$CLT;$ENDXREF;$XREFPROP=$XID={" + UUID.randomUUID() + "}$XID;$NAM=Stereotypes$NAM;$TYP=element property$TYP;$VIS=Public$VIS;$PAR=0$PAR;$DES=@STEREO;Name=Domain View;FQName=XIS-Mobile::Domain View;@ENDSTEREO;$DES;$CLT={" + guid + "}$CLT;$ENDXREF;";
						xrefs.setAttribute("value", xrefsValue);
						//Element/extendedProperties
						Element extendedProperties = (Element) elementExt.getElementsByTagName("extendedProperties").item(0);
						extendedProperties.setAttribute("package_name", umlPackage.getAttribute("name"));
						//Element/packageProperties
						Element packageProperties = (Element) docEA.createElement("packageProperties");
						packageProperties.setAttribute("version", "1.0");
						elementExt.insertBefore(packageProperties, null);
						//Element/paths
						Element pathsEl = (Element) docEA.createElement("paths");
						elementExt.insertBefore(pathsEl, null);
						//Element/times
						Element timesEl = (Element) docEA.createElement("times");
						timesEl.setAttribute("created", "1.0");
						timesEl.setAttribute("modified", "1.0");
						timesEl.setAttribute("lastloaddate", "1.0");
						timesEl.setAttribute("lastsavedate", "1.0");
						elementExt.insertBefore(timesEl, null);
						//Element/flags
						Element flagsEl = (Element) docEA.createElement("flags");
						flagsEl.setAttribute("iscontrolled", "FALSE");
						flagsEl.setAttribute("isprotected", "FALSE");
						flagsEl.setAttribute("batchsave", "0");
						flagsEl.setAttribute("batchload", "0");
						flagsEl.setAttribute("usedtd", "FALSE");
						flagsEl.setAttribute("logxml", "FALSE");
						elementExt.insertBefore(flagsEl, null);
						//Remove Element/attributes
						Element attributesEl = (Element) elementExt.getElementsByTagName("attributes").item(0);
						elementExt.removeChild(attributesEl);
						//Remove Element/links
						Element linksEl = (Element) elementExt.getElementsByTagName("links").item(0);
						elementExt.removeChild(linksEl);
						elementTemplate.getParentNode().insertBefore(elementExt, null);

						NodeList packageChildren = umlPackage.getChildNodes();

						for (int i = 0; i < packageChildren.getLength(); i++) {
							if (packageChildren.item(i) instanceof Element) {
								Element el = (Element) packageChildren.item(i);

								if (el.getAttribute("xmi:type").equals("uml:Class")) {
									elementExt = (Element) elementTemplate.cloneNode(true);
									elementExt.setAttribute("xmi:idref", el.getAttribute("xmi:id"));
									elementExt.setAttribute("xmi:type", el.getAttribute("xmi:type"));
									elementExt.setAttribute("name", el.getAttribute("name"));
									//Element/Model
									modelEl = (Element) elementExt.getElementsByTagName("model").item(0);
									modelEl.setAttribute("package", umlPackage.getAttribute("xmi:id"));
									//Element/Tags
									tagsEl = (Element) elementExt.getElementsByTagName("tags").item(0);
									Element tag = (Element) tagsEl.getElementsByTagName("tag").item(0);
									tag.setAttribute("xmi:id", UUID.randomUUID().toString().replace("-", "_"));
									tag.setAttribute("value", el.getAttribute("name"));
									tag.setAttribute("modelElement", el.getAttribute("xmi:id"));
									tag = (Element) tagsEl.getElementsByTagName("tag").item(1);
									tag.setAttribute("xmi:id", UUID.randomUUID().toString().replace("-", "_"));
									tag.setAttribute("modelElement", el.getAttribute("xmi:id"));
									//Element/xrefs
									xrefs = (Element) elementExt.getElementsByTagName("xrefs").item(0);
									xrefsValue = "$XREFPROP=$XID={" + UUID.randomUUID() + "}$XID;$NAM=Stereotypes$NAM;$TYP=element property$TYP;$VIS=Public$VIS;$PAR=0$PAR;$DES=@STEREO;Name=XisEntity;FQName=XIS-Mobile::XisEntity;@ENDSTEREO;$DES;$CLT={" + el.getAttribute("xmi:id") + "}$CLT;$SUP=&lt;none&gt;$SUP;$ENDXREF;";
									xrefs.setAttribute("value", xrefsValue);
									//Element/extendedProperties
									extendedProperties = (Element) elementExt.getElementsByTagName("extendedProperties").item(0);
									extendedProperties.setAttribute("package_name", umlPackage.getAttribute("name"));
									//Element/attributes
									NodeList attrList = el.getElementsByTagName("ownedAttribute");
									Element attributes = (Element) elementExt.getElementsByTagName("attributes").item(0);
									Element attributeTemplate = (Element) attributes.getElementsByTagName("attribute").item(0);

									for (int j = 0; j < attrList.getLength(); j++) {
										Element attr = (Element) attrList.item(j);
										Element attributeExt = (Element) attributeTemplate.cloneNode(true);
										attributeExt.setAttribute("xmi:idref", attr.getAttribute("xmi:id"));
										attributeExt.setAttribute("name", attr.getAttribute("name"));
										//attribute/model
										Element attrModel = (Element) attributeExt.getElementsByTagName("model").item(0);
										attrModel.setAttribute("ea_guid", "{" + attr.getAttribute("xmi:id") + "}");
										//attribute/properties
										Element attrProperties = (Element) attributeExt.getElementsByTagName("properties").item(0);
										attrProperties.setAttribute("type", getAttributeType(attr));
										//attribute/tags
										Element attrTags = (Element) attributeExt.getElementsByTagName("tags").item(0);
										NodeList tagsList = attrTags.getElementsByTagName("tag");
										Element attrTag = (Element) tagsList.item(0);
										//name
										attrTag.setAttribute("xmi:id", UUID.randomUUID().toString());
										attrTag.setAttribute("value", attr.getAttribute("name"));
										//value
										attrTag = (Element) tagsList.item(1);
										attrTag.setAttribute("xmi:id", UUID.randomUUID().toString());
										//type
										attrTag = (Element) tagsList.item(2);
										attrTag.setAttribute("xmi:id", UUID.randomUUID().toString());
										attrTag.setAttribute("value", getXisEntityAttributeType(attrProperties.getAttribute("type")));
										//nullable
										attrTag = (Element) tagsList.item(3);
										attrTag.setAttribute("xmi:id", UUID.randomUUID().toString());
										//isKey
										attrTag = (Element) tagsList.item(4);
										attrTag.setAttribute("xmi:id", UUID.randomUUID().toString());
										//attribute/xrefs
										Element attrXrefs = (Element) attributeExt.getElementsByTagName("xrefs").item(0);
										String attrXrefsValue = "$XREFPROP=$XID={" + UUID.randomUUID() + "}$XID;$NAM=Stereotypes$NAM;$TYP=attribute property$TYP;$VIS=Public$VIS;$PAR=0$PAR;$DES=@STEREO;Name=XisEntityAttribute;FQName=XIS-Mobile::XisEntityAttribute;@ENDSTEREO;$DES;$CLT={"+ attr.getAttribute("xmi:id") + "}$CLT;$SUP=&lt;none&gt;$SUP;$ENDXREF;";
										attrXrefs.setAttribute("xrefs", attrXrefsValue);
										attributeTemplate.getParentNode().appendChild(attributeExt);
									}

									//Delete Attribute Template
									attributeTemplate.getParentNode().removeChild(attributeTemplate);

									//Element/links
									linksEl = (Element) elementExt.getElementsByTagName("links").item(0);
									//TODO Properly set the links attributes
									elementTemplate.getParentNode().insertBefore(elementExt, null);
								}
							}
						}

						//Delete Element Template
						elementTemplate.getParentNode().removeChild(elementTemplate);

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

				File nFile = new File(args[1]);

				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				DOMSource source = new DOMSource(docEA);
				StreamResult result = new StreamResult(nFile);

				// StreamResult result2 = new StreamResult(System.out);

				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				transformer.transform(source, result);
				// transformer.transform(source, result2);
				System.out.println("Document successfully parsed!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void addUMLContent(DocumentBuilder builder,
			Document document, Element element, String modelPath) throws SAXException,
	IOException {
		InputStream f = new FileInputStream(new File(modelPath));
		//InputStream f = Emf2EaXMIAdapter.class.getResourceAsStream("result.uml");
		//InputStream f = Emf2EaXMIAdapter.class.getResourceAsStream("refugees.uml");
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

	private static String getAttributeType(Element el) {
		String attrType = "int";
		Element typeEl = ((Element)el.getElementsByTagName("type").item(0));
		String type = typeEl.getAttribute("href");

		if (type.contains("double")) {
			attrType = "double";
		} else if (type.contains("String")) {
			attrType = "String";
		} else if (type.contains("bool")) {
			attrType = "bool";
		}
		return attrType;
	}

	private static String getXisEntityAttributeType(String type) {
		String attrType = "Integer";

		if (type.contains("double")) {
			attrType = "Double";
		} else if (type.contains("String")) {
			attrType = "String";
		} else if (type.contains("bool")) {
			attrType = "Boolean";
		}
		return attrType;
	}
}
