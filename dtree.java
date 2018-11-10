import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class dtree {
	
	private int test = 0;
	
	static int []classification = new int[3];
	public Node DTL(Example examples, ArrayList<Attribute> attributes, Node def, String option, Node head, int pruning_thr) {
		
		ArrayList<String> examples_left = new ArrayList<String>();
		ArrayList<String> examples_right = new ArrayList<String>();
		
		if(!examples.getAllValues().isEmpty()) {
			ArrayList<Double> labels = SelectColumn(examples.getAllValues(), attributes.size()-1);
			//System.out.println(labels); // check
			double l = labels.get(0);
			boolean sameClass = true;
			for(int i=1; i<labels.size() && sameClass; i++) {
				if(labels.get(i) != l) {
					sameClass = false;
				}
			}
			if(examples.getAllValues().size() < pruning_thr) {
				def = new Node(0);
				return def;
			}
			else if(sameClass == true) {
				def = new Node((int)l);
				return def;
			}
			else {
			
				double []attributeThreshold = new double[2];
				if(option.equals("optimized")) {
					attributeThreshold = Choose_Attribute(attributes, examples);
				}
				else if(option.equals("randomized")){
					attributeThreshold = Choose_AttributeR(attributes, examples);
				}
				else {
					def = new Node(0);
					return def;
				}
				
			    head = new Node((int)attributeThreshold[0]);
	            head.setThreshold(attributeThreshold[1]);;
	            head.setGain(attributeThreshold[2]);
	            
	        	    examples_left = getLeft(examples.getAllValues(), (int)attributeThreshold[0], attributeThreshold[1]);
				examples_right = getRight(examples.getAllValues(), (int)attributeThreshold[0], attributeThreshold[1]);
				     
				head.left = DTL(new Example(examples_left), attributes, def, option, head.left, pruning_thr);
		     	head.right = DTL(new Example(examples_right), attributes, def, option, head.right, pruning_thr);

		}
					//System.out.println(examples_left);

			
		}
		return head;
	}
	
	public double[] Choose_Attribute(ArrayList<Attribute> attributes, Example examples){
		
		double []athres = new double[3];
		double max_gain = -1;
		double best_attribute = -1;
		double best_threshold = -1;
		double gain = 0;
		ArrayList<Double> attribute_values = new ArrayList<Double>();
		
		for(int i=0; i<attributes.size()-1; i++) {
			attribute_values = SelectColumn(examples.getAllValues(), i);
			//System.out.println(attribute_values); //check
			double l = min(attribute_values);
			//System.out.println(l);// check
			double m = max(attribute_values);
			//System.out.println(m);//check
			double threshold;
			for(int k=1; k<=50; k++) {
				threshold = l + (k*(m-l))/51;
				//System.out.println(threshold);
				gain = InformationGain(examples, i, threshold, attributes.size()-1);
				//System.out.println(gain);
				if(gain > max_gain) {
					max_gain = gain;
					best_attribute = i;
					best_threshold = threshold;
				}
				   
			}
		}
		athres[0] = best_attribute;
		athres[1] = best_threshold;
		athres[2] = gain;
		
		return athres; 
	}
	
	public void display(Node head){
		if(head!=null){
			display(head.left);
			if(head.getThreshold() == 0.0) {
				System.out.println(" (tree="+0+", "+"node="+head.getID()+", "+"feature="+head.getAttributeNum()+", "+"thr="+(-1)+", "+"gain="+head.getGain()+")");
			}
			else {
				System.out.println(" (tree="+0+", "+"node="+head.getID()+", "+"feature="+head.getAttributeNum()+", "+"thr="+head.getThreshold()+", "+"gain="+head.getGain()+")");
			}
			display(head.right);
		}
	}
	
	public void setNodeIDs(Node head, int rootID) {
		head.setID(rootID);
		if(head.left != null) {
			head.left.setID(2*rootID);
			setNodeIDs(head.left, head.left.getID());
		}
		if(head.right != null) {
			head.right.setID(2*rootID + 1);
			setNodeIDs(head.right, head.right.getID());
		}
	}
	
	public int Accuracy(Node head, ArrayList<String> examples) {

		int correct = 0;
		for(int i=0; i<classification.length; i++) {
			classification[i] = 0;
		}
		for(int i=0; i<examples.size(); i++) {
			
			String []tokens = examples.get(i).split("\\s+");
			int c = traverse(head, tokens);
			System.out.println(" ID="+i+", predicted="+classification[0]+", true="+classification[1]+", accuracy="+classification[2]);
			if(c==1)
				correct++;
			test = 0;
		}
		return correct;
	}
	
	public int traverse(Node head, String []tokens) {

			if((head.getThreshold() != 0.0) && (Double.parseDouble(tokens[head.getAttributeNum()]) < head.getThreshold())) {
				if(head.left != null) {
					if(head.left.getThreshold() == 0.0) {
						//x = Integer.parseInt(tokens[tokens.length-1]);
						if(Integer.parseInt(tokens[tokens.length-1]) == head.left.getAttributeNum()) {
							test = 1;
							classification[0] = head.left.getAttributeNum();
							classification[1] = head.left.getAttributeNum();
							classification[2] = test;
						}
						//System.out.println("correct leaf");
						else {
							classification[0] = head.left.getAttributeNum();
							classification[1] = Integer.parseInt(tokens[tokens.length-1]);
							classification[2] = 0;
						}
					}
					else {
						//System.out.println("left");
						traverse(head.left, tokens);
					}
				}
			}
			else if((head.getThreshold() != 0.0) && (Double.parseDouble(tokens[head.getAttributeNum()]) >= head.getThreshold())) {
				if(head.right != null) {
					if(head.right.getThreshold() == 0.0) {
						//x = Integer.parseInt(tokens[tokens.length-1]);
						if(Integer.parseInt(tokens[tokens.length-1]) == head.right.getAttributeNum()) {
							test = 1;
							classification[0] = head.right.getAttributeNum();
							classification[1] = head.right.getAttributeNum();
							classification[2] = test;
						}
						else {
							classification[0] = head.right.getAttributeNum();
							classification[1] = Integer.parseInt(tokens[tokens.length-1]);
							classification[2] = 0;
						}
						//System.out.println("correct leaf");
					}
					else {
						//System.out.println("right");
						traverse(head.right, tokens);
					}
				}
			}
		
		return test;
	}
	
    public double[] Choose_AttributeR(ArrayList<Attribute> attributes, Example examples){
		
		double []athres = new double[3];
		double max_gain = -1;
		double best_threshold = -1;
		ArrayList<Double> attribute_values = new ArrayList<Double>();
		Random randomAttribute = new Random();
		int a = randomAttribute.nextInt(8);
		//System.out.println(a);
		attribute_values = SelectColumn(examples.getAllValues(), a);
		
		double l = min(attribute_values);
		double m = max(attribute_values);
		double threshold;
		double gain = 0;;
		for(int k=1; k<=50; k++) {
			if(l == m) {
				break;
			}
			threshold = l + (k*(m-l))/51;
			gain = InformationGain(examples, a, threshold, attributes.size()-1);
			//System.out.println(gain);
			//System.out.println(gain);
			if(gain > max_gain) {
				max_gain = gain;
				best_threshold = threshold;
			}
			   
		}
		athres[0] = a;
		athres[1] = best_threshold;
		athres[2] = gain;
		return athres; 
	}
	
	public ArrayList<Double> SelectColumn(ArrayList<String> examples, int a){
		
		ArrayList<Double> columnValues = new ArrayList<Double>();
		for(int i=0; i<examples.size(); i++) {
			String []tokens = examples.get(i).split("\\s+");
			columnValues.add(Double.parseDouble(tokens[a]));
		}
		return columnValues;
	}
	
	public double max(ArrayList<Double> array) {
		double maxValue = array.get(0);
		for(int i=1; i<array.size(); i++) {
			if(array.get(i) > maxValue) {
				maxValue = array.get(i);
			}
		}
		return maxValue;
	}
	
	public double min(ArrayList<Double> array) {
		double minValue = array.get(0);
		for(int i=1; i<array.size(); i++) {
			if(array.get(i) < minValue) {
				minValue = array.get(i);
			}
		}
		return minValue;
	}
	
	public ArrayList<String> getRight(ArrayList<String> examples, int attribute, double threshold){
		
		ArrayList<String> right = new ArrayList<String>();
		for(int i=0; i<examples.size(); i++) {
			String []tokens = examples.get(i).split("\\s+");
			if(Double.parseDouble(tokens[attribute]) >= threshold) {
				right.add(examples.get(i));
			}
		}
		return right;
	}
	
    public ArrayList<String> getLeft(ArrayList<String> examples, int attribute, double threshold){
    	
    	    ArrayList<String> left = new ArrayList<String>();
		for(int i=0; i<examples.size(); i++) {
			String []tokens = examples.get(i).split("\\s+");
			if(Double.parseDouble(tokens[attribute]) < threshold) {
				left.add(examples.get(i));
			}
		}
		return left;
	}
	
	public double InformationGain(Example examples, int a, double threshold, int attribute) {
		double entropyRoot = 0;
		double entropyLeft = 0;
		double entropyRight = 0;
		ArrayList<Double> labels = SelectColumn(examples.getAllValues(), attribute);
		//System.out.println(labels);//check
		ArrayList<Double> uniqueLabels = new ArrayList<Double>();
		
		ArrayList<String> left_examples = new ArrayList<String>();
		ArrayList<String> right_examples = new ArrayList<String>();
		
		left_examples = getLeft(examples.getAllValues(), a, threshold);
		//System.out.println(threshold);//check
		right_examples = getRight(examples.getAllValues(), a, threshold);
		//System.out.println(right_examples);//check
		ArrayList<Double>labels_left = new ArrayList<Double>();
		ArrayList<Double>labels_right = new ArrayList<Double>();
		
		labels_left = SelectColumn(left_examples, attribute);
		labels_right = SelectColumn(right_examples, attribute);
		for(int i=0; i<labels.size(); i++) {           //for root
			if(!uniqueLabels.contains(labels.get(i))) {
				uniqueLabels.add(labels.get(i));
			}
		}
		int []classes = new int[uniqueLabels.size()];
		for(int i=0; i<classes.length; i++) {
			classes[i] = 0;
		}
		for(int i=0; i<uniqueLabels.size(); i++) {
			for(int j=0; j<labels.size(); j++) {
				if(labels.get(j).equals(uniqueLabels.get(i))) {
					classes[i] = classes[i] + 1;
				}
			}
		}
		for(int i=0; i<classes.length; i++) {
			//System.out.println((double)classes[i]/labels.size());
			entropyRoot = entropyRoot - ((double)classes[i]/labels.size())*(Math.log10((double)classes[i]/labels.size())/Math.log10(2));
		}
		
		//System.out.println(entropyRoot);
		
		ArrayList<Double> uniqueLabels_left = new ArrayList<Double>();
		for(int i=0; i<labels_left.size(); i++) {           //for left
			if(!uniqueLabels_left.contains(labels_left.get(i))) {
				uniqueLabels_left.add(labels_left.get(i));
			}
		}
		int []classes_left = new int[uniqueLabels_left.size()];
		for(int i=0; i<classes_left.length; i++) {
			classes_left[i] = 0;
		}
		for(int i=0; i<uniqueLabels_left.size(); i++) {
			for(int j=0; j<labels_left.size(); j++) {
				if(labels_left.get(j).equals(uniqueLabels_left.get(i))) {
					classes_left[i] = classes_left[i] + 1;
				}
			}
		}
		
		for(int i=0; i<classes_left.length; i++) {
			entropyLeft = entropyLeft - ((double)labels_left.size()/labels.size())*((double)classes_left[i]/labels_left.size())*(Math.log10((double)classes_left[i]/labels_left.size())/Math.log10(2));
		}
		
		
		ArrayList<Double> uniqueLabels_right = new ArrayList<Double>();
		for(int i=0; i<labels_right.size(); i++) {           //for right
			if(!uniqueLabels_right.contains(labels_right.get(i))) {
				uniqueLabels_right.add(labels_right.get(i));
			}
		}
		int []classes_right = new int[uniqueLabels_right.size()];
		for(int i=0; i<classes_right.length; i++) {
			classes_right[i] = 0;
		}
		for(int i=0; i<uniqueLabels_right.size(); i++) {
			for(int j=0; j<labels_right.size(); j++) {
				if(labels_right.get(j).equals(uniqueLabels_right.get(i))) {
					classes_right[i] = classes_right[i] + 1;
				}
			}
		}
		
		for(int i=0; i<classes_right.length; i++) {
			entropyRight = entropyRight - ((double)labels_right.size()/labels.size())*((double)classes_right[i]/labels_right.size())*(Math.log10((double)classes_right[i]/labels_right.size())/Math.log10(2));
		}
		
		return (entropyRoot - entropyLeft - entropyRight);
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ArrayList<String> putToExamples = new ArrayList<String>();
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			String str = "";
			while((str = br.readLine()) != null) {
				putToExamples.add(str.trim());
			}
			br.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
		String []tokens = putToExamples.get(0).split("\\s+");
		//System.out.println(tokens.length);
		
		for(int i=0; i<tokens.length; i++) {
			attributes.add(new Attribute(i));
		}
		
		Example examples = new Example(putToExamples);
		//System.out.println(examples.getAllValues());
		
		ArrayList<String> testFiles = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(args[1]));
			String str = "";
			while((str = br.readLine()) != null) {
				testFiles.add(str.trim());
			}
			br.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		Node node = new Node();
		Node def = new Node();
		dtree dtl = new dtree();
		//System.out.println(examples.getAllValues());
		node = dtl.DTL(examples, attributes, def, args[2], node, Integer.parseInt(args[3]));
		
		dtl.setNodeIDs(node, 1);
		dtl.display(node);
		System.out.println("correct="+dtl.Accuracy(node, testFiles)+" out of "+testFiles.size()+" objects");
		//System.out.println(test.size());
		//System.out.println(node.getID());
		
		
	}

}
