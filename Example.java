import java.util.ArrayList;

public class Example {
	
	private ArrayList<String> values;
	
	public Example() {
		this.values = new ArrayList<String>();
	}
	
	public Example(ArrayList<String> values) {
		this.values = values;
	}
	
	public String getValue(int index) {
		return values.get(index);
	}
	
	public ArrayList<String> getAllValues(){
		return this.values;
	}

}
