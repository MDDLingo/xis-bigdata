# JsonDiscovererCaller
Helper project that calls the [JsonDiscoverer tool](http://som-research.uoc.edu/tools/jsonDiscoverer/) in the "Simple Discovery" mode.
It will discover the implicit schema of a single json document and represent that schema in an Ecore model.

## Software Dependencies
To build the source code of this project the following requirements must be met:

  - Import the referenced projects "com.google.gson", "fr.inria.atlanmod.json.discoverer" and " fr.inria.atlanmod.json.discoverer.zoo" to your Eclipse's workspace;
  - Assure you have the Ecore plugin in your Eclipse version (specifically assure you have the "org.eclipse.emf.ecore" and "org.eclipse.emf.ecore.xmi" jars in Eclipse's plugins folder);
  - We reccomend the usage of the [Eclipse Modeling Tools](http://www.eclipse.org/downloads/packages/eclipse-modeling-tools/mars1]) version of Eclipse, which has by default the Ecore plugin mentioned before.
  
## Usage
To run the JsonDiscovererCaller you need to run the JsonDiscovererCaller.java class and provide 2 arguments:
  1. The path of the json file (e.g. "C:\test.json");
  2. The path of the generated ecore file (e.g. "C:\test.ecore"). **This file must always have the .ecore extension**.