package beam.agentsim.r5Extractor;

import com.conveyal.r5.streets.EdgeStore;
import com.conveyal.r5.transit.TransportNetwork;
import com.vividsolutions.jts.geom.Coordinate;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.utils.geometry.transformations.GeotoolsTransformation;

import java.io.File;
import java.util.EnumSet;
import java.util.HashMap;

/**
 *
 *
 * Created by Andrew A. Campbell on 6/7/17.
 */
public class MNetfromR5 {
	private static final Logger log = Logger.getLogger(MNetfromR5.class);

	private TransportNetwork r5Network = null;  // R5 mNetowrk
	private Network mNetowrk = null;  // MATSim mNetowrk
	private String fromCRS = "EPSG:4326";  // WGS84
	private String toCRS = "EPSG:26910";  // UTM10N

	private HashMap<Coord, Id<Node>> nodeMap = new HashMap<>();  // Maps x,y Coord to node ID
	private int nodeId = 0;  // Current new MATSim network Node ids

	public MNetfromR5(String r5NetPath) {
		File netFile = new File(r5NetPath);
		log.info("Found R5 Transport Network file, loading....");
		try {
			this.r5Network = TransportNetwork.read(netFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.mNetowrk = NetworkUtils.createNetwork();
		buildMNet();
	}

	private void buildMNet() {
		EdgeStore.Edge cursor = r5Network.streetLayer.edgeStore.getCursor();  // Iterator of edges in R5 network
		while (cursor.advance()) {
			// Check if this edge permits autos. Skip if not.
			EnumSet<EdgeStore.EdgeFlag> flags = cursor.getFlags();
			if (!flags.contains(EdgeStore.EdgeFlag.ALLOWS_CAR)){
				log.info("EDGE SKIPPED - no autos allowed");
				continue;
			}
			//Add the edge and its nodes
			Integer idx = cursor.getEdgeIndex();
			double length = cursor.getLengthM();
			double speed = cursor.getSpeedMs();
			// Get start and end coordinates for the edge
			Coordinate tempFromCoord =  cursor.getGeometry().getCoordinate();
			Coord fromCoord = transformCRS(new Coord(tempFromCoord.x, tempFromCoord.y));  // MATSim coord
			Coordinate[] tempCoords = cursor.getGeometry().getCoordinates();
			Coordinate tempToCoord = tempCoords[tempCoords.length - 1];
			Coord toCoord = transformCRS(new Coord(tempToCoord.x, tempToCoord.y));
			// Add R5 start and end nodes to the MATSim network
			// Grab existing nodes from mNetwork if they already exist, else make new ones
			Node fromNode = this.getOrMakeNode(fromCoord);
			Node toNode = this.getOrMakeNode(toCoord);
			// Make the link and add it to mNetwork
			Id<Link> linkId = Id.createLinkId(idx);
			//TODO - why should we ever see the same linkID twice? This would mean we are seeing the same cursor.getEdgeIndex() value, i.e. repeating R5 edges
			if (!this.mNetowrk.getLinks().containsKey(linkId)){
				Link r5Link = NetworkUtils.createLink(Id.createLinkId(idx.toString()), fromNode, toNode, mNetowrk, length, speed, 1.0, 1.0);
				this.mNetowrk.addLink(r5Link);
			} else {
				log.info("Link already exists. ID: " + linkId);
			}
		}
		//TODO - reimplement the NetworkCleaner after initial testing w/out it
//		NetworkCleaner nC = new NetworkCleaner();
//		log.info("Running NetowrkCleaner");
//		nC.run(this.mNetowrk);
	}

	public void setTransformCRS(String to, String from){
		this.toCRS = to;
		this.fromCRS = from;
	}

	public void writeMNet(String mNetPath){
		NetworkWriter nw = new NetworkWriter(this.mNetowrk);
		nw.write(mNetPath);
	}

	/*
	Tranforms from WGS84 to UTM 26910
	 */
	private Coord transformCRS(Coord coord){
		GeotoolsTransformation tranform = new GeotoolsTransformation(this.fromCRS, this.toCRS);
		return tranform.transform(coord);
	}

	/**
	 * Checks whether we already have a MATSim Node at the Coord. If so, returns that Node. If not, makes and adds
	 * a new Node to the network.
	 * @param coord
	 * @return
	 */
	private Node getOrMakeNode(Coord coord){
		Node dummyNode;
		Id<Node> id;
		if (this.nodeMap.containsKey(coord)){  // node already exists.
//			log.info("NODE ALREADY EXISTS");
			id = this.nodeMap.get(coord);
			dummyNode = this.mNetowrk.getNodes().get(id);
		} else { // need to make new fromID and node and increment the nodeId
			id = Id.createNodeId(this.nodeId);
			this.nodeId++;
			dummyNode = NetworkUtils.createAndAddNode(mNetowrk, id, coord);
			this.nodeMap.put(coord, id);
		}
		return dummyNode;
	}

	/**
	 * Input args:
	 * 0 - path to directory with data containers needed to build the TransportNetwork (osm, gtfs ...)
	 * 1 - output path for MATSim network
	 * @param args
	 */
	public static void main(String[] args) {
		String inFolder = args[0];
		String mNetPath = args[1];

		MNetfromR5 mn = new MNetfromR5(inFolder);
		mn.buildMNet();
		log.info("Finished building network.");
		NetworkCleaner nC = new NetworkCleaner();
		log.info("Running NetowrkCleaner");
		nC.run(mn.mNetowrk);
		log.info("Number of links:" + mn.mNetowrk.getLinks().size());
		log.info("Number of nodes: " + mn.mNetowrk.getNodes().size());
		mn.writeMNet(mNetPath);
	}


	// TODO - delete this local class if we don't need to use link to/from nodes as keys in a Map
	/**
	 * Class for using tuples of MATSim Coords as keys for a Map
	 */
	public class CoordKey {

		private final Coord fromCoord;
		private final Coord toCoord;

		public CoordKey(Coord fromCoord, Coord toCoord) {
			this.fromCoord = fromCoord;
			this.toCoord = toCoord;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof CoordKey)) return false;
			CoordKey key = (CoordKey) o;
			return fromCoord == key.fromCoord && toCoord == key.toCoord;
		}

		@Override
		public int hashCode() {
			int result = Integer.valueOf(fromCoord.toString());
			result = 31 * result + Integer.valueOf(toCoord.toString());
			return result;
		}
	}

}

