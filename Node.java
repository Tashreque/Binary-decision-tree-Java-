
public class Node {
	
	int data;
	int nodeID;
	double threshold;
	double gain;
	Node left;
	Node right;
	public Node() {
		
	}
	public Node(int data){
		this.data = data;
		left = null;
		right = null;
	} 
	
	public int getAttributeNum() {
		return this.data;
	}
	public void setID(int id) {
		this.nodeID = id;
	}
	
	public int getID() {
		return this.nodeID;
	}
	
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
	public double getThreshold() {
		return this.threshold;
	}
	
	public void setGain(double gain) {
		this.gain = gain;
	}
	
	public double getGain() {
		return this.gain;
	}
}
