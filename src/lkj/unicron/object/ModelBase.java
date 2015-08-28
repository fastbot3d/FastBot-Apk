package lkj.unicron.object;

public class ModelBase {

	public ModelBase() {
		// TODO Auto-generated constructor stub
	}
	public  float maxX = Float.MIN_VALUE;
	public  float maxY = Float.MIN_VALUE;
	public  float maxZ = Float.MIN_VALUE;
	
	public  float minX = Float.MAX_VALUE;
	public  float minY = Float.MAX_VALUE;
	public  float minZ = Float.MAX_VALUE;
	
	public void GetAABB(float x, float y, float z) {
		if (x > maxX) {
			maxX = x;
		}
		if (y > maxY) {
			maxY = y;
		}
		if (z > maxZ) {
			maxZ = z;
		}
		if (x < minX) {
			minX = x;
		}
		if (y < minY) {
			minY = y;
		}
		if (z < minZ) {
			minZ = z;
		}
	}
}
