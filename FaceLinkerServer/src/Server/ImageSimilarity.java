package Server;

public class ImageSimilarity {
	private String name;
	private float similarity;
	
	public ImageSimilarity(){
		
	}
	
	public ImageSimilarity(String name, float similarity){
		this.name = name;
		this.similarity = similarity;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getSimilarity() {
		return similarity;
	}
	public void setSimilarity(float similarity) {
		this.similarity = similarity;
	}
}
