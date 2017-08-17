package beam.utils.testNet;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andrew A. Campbell on 8/14/17.
 *
 * Builds the simple Manhattan grid OSM test network
 */
public class OSMTestNet {

	private double tLLon =  -122.259820;  // top-left lon
	private double tLLat = 37.874080;  // top-left lat
	private double inc = 0.001;  // lat lon increments (i.e. block length)
	private double intxLen = 0.00005;
	private int nRows = 5;
	private int nCols = 5;
	private String wayVal = "trunk";  // the value of the OSM way (e.g. trunk, residential, sidewalk etc.

	private String nLanes = "1";
	private String maxSpeed = "35 mph";
	private String oneWay = "yes";

	private Element osm;
	private Document doc;

	private Map<Integer, Integer> nodeIDMap = new HashMap<>();
	private Map<Integer, Integer> wayIDMap = new HashMap<>();
	private Map<Integer, Element> nodeMap = new HashMap<>();

	private Integer nodeId = 1;
	private Integer wayId = 1;

	OSMTestNet(){
		this.osm = new Element("osm");  // root
		osm.setAttribute("version", "0.6");
		osm.setAttribute("generator", "OSMTestNet.java");
		this.doc = new Document(osm);
		this.doc.setRootElement(osm);
	}


	/**
	 * Builds the test network. Use the setters to overwrite defaults
	 */
	public void buildTestNet(){
		// Add all the nodes first
		for (int row=0; row<nRows; row++){
			for(int col=0; col<nCols; col++){
				// Create the new node
				double lon = this.tLLon + this.inc*row;
				double lat = this.tLLat - this.inc*col;
				Element node = this.makeNode(lat, lon, row, col);
				// Add the node to the doc
				this.doc.getRootElement().addContent(node);
			}
		}
		// Then add all the ways so that they are written in separate order like in the raw OSM downloads
		for (int row=0; row<nRows; row++) {
			for (int col = 0; col < nCols; col++) {
//				String thisNodeId = String.valueOf(row) + "_" + String.valueOf(col);
				String thisNodeId = String.valueOf(this.nodeIDMap.get(cantorPairing(row, col)));
				Element thisNode = this.nodeMap.get(cantorPairing(row, col));
				// Add the to/from edges to to top and left nodes (if they exist)
				if (row>0){
//					String topNodeId = String.valueOf(row - 1) + "_" + String.valueOf(col);
//					String topNodeId = String.valueOf(this.nodeIDMap.get(cantorPairing(row-1, col)));
					Element topNode = this.nodeMap.get(cantorPairing(row-1, col));
					String name = "col_" + String.valueOf(col);
					this.doc.getRootElement().addContent(this.makeWay(topNode, thisNode, name));
					this.doc.getRootElement().addContent(this.makeWay(thisNode, topNode, name));
				}
				if (col>0){
//					String leftNodeId = String.valueOf(col - 1) + "_" + String.valueOf(col);
//					String leftNodeId = String.valueOf(this.nodeIDMap.get(cantorPairing(row, col-1)));
					Element leftNode = this.nodeMap.get(cantorPairing(row, col-1));
					String name = "row_" + String.valueOf(row);
					this.doc.getRootElement().addContent(this.makeWay(leftNode, thisNode, name));
					this.doc.getRootElement().addContent(this.makeWay(thisNode, leftNode, name));
				}
			}
		}
	}


	public void writeNetwork(String path) throws IOException {
		XMLOutputter xmlOutPut = new XMLOutputter();
		xmlOutPut.setFormat(Format.getPrettyFormat());
		xmlOutPut.output(this.doc, new FileWriter(path));
	}

	public Element makeWay(Element otherNode, Element thisNode, String name){
		//TODO - add a name to the way based on whether it is vertical or horizontal (row_0, row_1 ...)
		// Initialize the node and increment wayId
		Element newWay = new Element("way");
		String wayID = String.valueOf(this.wayId);
		newWay.setAttribute(new Attribute("id", wayID));
		this.wayId++;

		//Add the default way attributes
		newWay.setAttribute(new Attribute("user", "wintermute"));
		newWay.setAttribute(new Attribute("uid", "1"));
		newWay.setAttribute(new Attribute("timestamp", "2016-12-31T23:59:59.999Z"));
		newWay.setAttribute(new Attribute("visible", "true"));
		newWay.setAttribute(new Attribute("version", "1"));
		newWay.setAttribute(new Attribute("changeset", "1"));

		// Add the ordered node elements
		Element oNT = new Element("nd");
		oNT.setAttribute("ref", otherNode.getAttributeValue("id"));
		newWay.addContent(oNT);
		Element tNT = new Element("nd");
		tNT.setAttribute("ref", thisNode.getAttributeValue("id"));
		newWay.addContent(tNT);

		// Add the other tags
		Element tag0 = makeTag("highway", this.wayVal);
		Element tag1= makeTag("lanes", this.nLanes);
		Element tag2 = makeTag("maxspeed", this.maxSpeed);
		Element tag3 = makeTag("oneway", "true");
		Element tag4 = makeTag("name", name);
		newWay.addContent(tag0);
		newWay.addContent(tag1);
		newWay.addContent(tag2);
		newWay.addContent(tag3);
		newWay.addContent(tag4);
		return newWay;
	}

	/**
	 * Makes a new network node. Increments the nodeId field and puts the node in the lookup map.
	 * @param lat
	 * @param lon
	 * @param row
	 * @param col
	 * @return
	 */
	public Element makeNode(double lat, double lon, int row, int col){
		// Initialize node, increment nodeID, and add to the lookup map
		Element node = new Element("node");
		String thisNodeId = String.valueOf(this.nodeId);
		this.nodeIDMap.put(cantorPairing(row, col), this.nodeId);
		this.nodeMap.put(cantorPairing(row, col), node);
		this.nodeId++;
		// Add all the variable node attributes
		node.setAttribute(new Attribute("id", thisNodeId));
		node.setAttribute(new Attribute("lat", String.valueOf(lat)));
		node.setAttribute(new Attribute("lon", String.valueOf(lon)));
		// Add all the default node attributes
		node.setAttribute(new Attribute("user", "wintermute"));
		node.setAttribute(new Attribute("uid", "1"));
		node.setAttribute(new Attribute("timestamp", "2016-12-31T23:59:59.999Z"));
		node.setAttribute(new Attribute("visible", "true"));
		node.setAttribute(new Attribute("version", "1"));
		node.setAttribute(new Attribute("changeset", "1"));
		// Add the default tags
		Element tag = makeTag("highway", "traffic_signals");
		node.addContent(tag);

		return node;
	}

	/**
	 *
	 * @param lat Centroid of intersection lat
	 * @param lon Centroid of intersection lon
	 * @param row
	 * @param col
	 */
	public void makeAndAddIntersection(double lat, double lon, int row, int col){
		// Make four nodes for the four corners of the intersection
		Element nodeNW = makeNode(lat+this.intxLen, lon-this.intxLen, row, col);
		Element nodeNE = makeNode(lat+this.intxLen, lon+this.intxLen, row, col);
		Element nodeSE = makeNode(lat-this.intxLen, lon+this.intxLen, row, col);
		Element nodeSW = makeNode(lat-this.intxLen, lon-this.intxLen, row, col);
		this.doc.getRootElement().addContent(nodeNW);
		this.doc.getRootElement().addContent(nodeNE);
		this.doc.getRootElement().addContent(nodeSE);
		this.doc.getRootElement().addContent(nodeSW);
		// Make four short ways connecting the nodes

	}

	public Element makeTag(String key, String value){
		Element tag = new Element("tag");
		tag.setAttribute("k", key);
		tag.setAttribute("v", value);
		return tag;
	}

	/**
	 * Maps all unique pairs of non-negative integers to unique integer. Used for generating Map keys.
	 * @param k1
	 * @param k2
	 * @return
	 */
	public int cantorPairing(int k1, int k2){
		return (k1+k2)*(k1+k2+1)/2 + k2;
	}

	/**
	 * Sets the number of rows and columns in the Manhattan grid
	 * @param nRows
	 * @param nCols
	 */
	public void setNRowsCols(int nRows, int nCols){
		this.nRows = nRows;
		this.nCols = nCols;
	}

	/**
	 * Sets the lat / lon for the top-left intersection
	 * @param lat
	 * @param lon
	 */
	public void setTLLatLon(double lat, double lon){
		this.tLLon = lon;
		this.tLLat = lat;
	}

	/**
	 * Sets the lat/lon increments (i.e. block length)
	 * @param inc
	 */
	public void setInc(double inc){
		this.inc = inc;
	}

	public static void main(String[] args) throws IOException {
		String outPath = args[0];
		OSMTestNet testNet = new OSMTestNet();
		testNet.buildTestNet();
		testNet.writeNetwork(outPath);
	}

}
