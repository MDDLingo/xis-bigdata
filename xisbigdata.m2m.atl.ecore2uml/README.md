# Ecore2UML
Ecore2UML is a software project that transforms an Ecore model (produced from the [JsonDiscovererCaller](https://github.com/MDDLingo/xis-bigdata/tree/master/JsonDiscovererCaller)) to its equivalent UML model.
Ecore2UML performs these Model-To-Model (M2M) transformations using the [ATL model transformation language](https://eclipse.org/atl/) and its supporting environment.

## Software Dependencies
To build the source code of this project the following requirements must be met:

  - We reccomend the usage of the [Eclipse Modeling Tools](http://www.eclipse.org/downloads/packages/eclipse-modeling-tools/mars1]) version of Eclipse, which will have by default some of the jars mentioned below;
  - Install the [ATL plugin](https://eclipse.org/atl/);
  - Assure you have the following jars in Eclipse's plugins folder: "org.eclipse.uml2.uml", "org.eclipse.emf.ecore", "org.eclipse.m2m.atl.engine.emfvm", "org.eclipse.ocl.xtext.completeocl" and "org.apache.log4j". If not, you need to install the plugins containg them through the menu option "Help->Install New Software...".
  
## Usage
Ecore2UML can be run in two modes: inside Eclipse or as a standalone jar.

**Inside Eclipse:**
Run the file Ecore2UML.atl as an ATL Transformation and provide:
  1. IN_Library - The path of the UML Primitive Types model. It is the file "UMLPrimitiveTypes.library.uml" contained in the folder "models" of this project;
  2. IN_XISMobile - The path of the XIS-Mobile profile model. It is the file "xis.profile.uml" contained in the folder "models" of this project;
  3. IN - The path of the ecore model (e.g. "C:\test.ecore");
  4. OUT - The path of the generated uml file (e.g. "C:\test.uml"). This file should always have the .uml extension.

**Standalone:**
- Export the Ecore2UML from Eclipse as a runnable jar file (right-click option "Export...");
- Copy the folder "libs" of this project to the path of the exported jar;
- Run the jar (use command: java -jar) and provide 5 arguments:
    1. The path of the ecore model (e.g. "C:\test.ecore");
    2. The path of the UML Primitive Types model (e.g. "C:\UMLPrimitiveTypes.library.uml"). It is the file "UMLPrimitiveTypes.library.uml" contained in the folder "models" of this project;
    3. The path of the XIS-Mobile profile model (e.g. "C:\xis.profile.uml"). It is the file "xis.profile.uml" contained in the folder "models" of this project;
    4. The path of the generated uml file (e.g. "C:\test.uml"). This file should always have the .uml extension;
    5. The path of the exported jar (e.g "C:\").