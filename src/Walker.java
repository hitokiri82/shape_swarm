import sim.engine.*;
import sim.util.*;
import java.util.ArrayList;

/** A robot */

public class Walker implements Steppable
{
	public Int2D loc;
	public int vec_x,vec_y;
	public boolean lost = false;
	public boolean inShape = false;
	public WalkersWorld world;
	public Mapper map;

	public Walker(Int2D loc, Mapper map,WalkersWorld world)
	{
		//Constructor
		this.world = world;
		this.map = map;
		this.loc = loc;
		this.vec_x = world.random.nextInt(3)-1;
		this.vec_y = world.random.nextInt(3)-1;
	}

	public Double2D vectorsLost(){
		double x=0, y=0;
		if (world.random.nextBoolean(0.2)){
			x = world.random.nextDouble() * 2 - 1;
			y = world.random.nextDouble() * 2 - 1;
			return new Double2D(x,y);
		} else {
			return new Double2D(this.vec_x,this.vec_y);
		}
	}

	private Double2D vectorsGas(){
		Bag vecinos = world.obtainNeighbors(this);
		Walker v;

		double dist = 0;
		double xo = 0, yo = 0;
		for (int i=0; i<vecinos.size(); i++) {
			v = (Walker)vecinos.get(i);
			dist = this.estimateDistance(v);
			if (dist>0) {
				double magnitude = 0;
				if (dist < world.REPULSION_RADIUS) {
					magnitude = dist - world.REPULSION_RADIUS; 
				} else {
					magnitude = 0;
				}
				xo += ((v.loc.x - this.loc.x)/dist*magnitude);
				yo += ((v.loc.y - this.loc.y)/dist*magnitude);
			} else{
				xo += -1/2;
				yo += -1/2;
			}
		}
		if (this.inShape) {
			return new Double2D(xo*world.Repulsion,yo*world.Repulsion);
		} else {
			return new Double2D(xo*14,yo*14);
		}

	}
	
	private Double2D vectorsObstacles(){

		double dist = 0;
		double xo = 0, yo = 0;
		double magnitude;
		IntBag xPos = new IntBag();
		IntBag yPos = new IntBag();
		IntBag result = new IntBag();
		boolean choque = false;

		world.obstacles.getNeighborsMaxDistance(this.loc.x, this.loc.y, 2, false, result, xPos, yPos);

		for (int i = 0; i < result.size(); i++) {
			if (result.get(i) == 1) {
				choque = true;
				dist = Math.sqrt(Math.pow(this.loc.x-xPos.get(i), 2)+Math.pow(this.loc.y-yPos.get(i), 2));
				if (dist>0) {
					magnitude = (dist - 3);
					xo += ((xPos.get(i) - this.loc.x)/dist*magnitude);
					yo += ((yPos.get(i) - this.loc.y)/dist*magnitude);
				} 
			} 
		}

		if (choque) {
			if ((this.vec_x*xo + this.vec_y*yo) == 0){
				xo = vec_x;
				yo = vec_y;
			} else {
				if (world.random.nextBoolean(0.5)) {
					double orth_x = yo;
					double orth_y = xo;
					if(world.random.nextBoolean()){
						orth_y *= -1;
					} else {
						orth_x *= -1;
					}
					xo = orth_x;
					yo = orth_y;
				}
			}
		}
		return new Double2D(xo*world.Evasion,yo*world.Evasion);
	}

	public Double2D vectorsShape(){

		Int2D closest = this.map.closestPoint(this.loc);

		int xv = closest.x - this.loc.x ;
		int yv = closest.y - this.loc.y;

		return new Double2D(xv*world.Cohesion,yv*world.Cohesion);

	}

	public void getNewLocation(){

		ArrayList<Double2D> vectors = new ArrayList<Double2D>();

		vectors.add(vectorsObstacles());
		if (this.lost){
			vectors.add(vectorsLost());
		} else if(this.inShape){
			vectors.add(vectorsGas());
		} else {
			vectors.add(new Double2D(this.vec_x*world.Inertia, this.vec_y*world.Inertia));
			vectors.add(vectorsGas());
			vectors.add(vectorsShape());
		}

		double xo=0,yo=0;
		for(Double2D t: vectors){
			xo += t.x;
			yo += t.y;
		}
		double norm = Math.sqrt(xo*xo + yo*yo);
		if (norm > 0) {
			xo /= norm;
			yo /= norm;
		}

		vec_x = (int)Math.round(xo);
		vec_y = (int)Math.round(yo);

		int new_x = this.loc.x + vec_x;
		int new_y = this.loc.y + vec_y;

		if (new_x < 0) { new_x++; vec_x = -vec_x; }
		else if (new_x >= world.gridWidth) {new_x--; vec_x = -vec_x; }
		if (new_y < 0) { new_y++ ; vec_y = -vec_y; }
		else if (new_y >= world.gridHeight) {new_y--; vec_y = -vec_y; }

		if (!pointIsEmpty(new Int2D(new_x, new_y))) {
			int i = 0;
			do { 
				vec_x = world.random.nextInt(3)-1;
				vec_y = world.random.nextInt(3)-1;

				new_x = this.loc.x + vec_x;
				new_y = this.loc.y + vec_y;

				if (new_x < 0) { new_x++; vec_x = -vec_x; }
				else if (new_x >= world.gridWidth) {new_x--; vec_x = -vec_x; }
				if (new_y < 0) { new_y++ ; vec_y = -vec_y; }
				else if (new_y >= world.gridHeight) {new_y--; vec_y = -vec_y; }

				i++;
				if(i>100) break;
			} while (!pointIsEmpty(new Int2D(new_x, new_y)));
		}
		this.loc = new Int2D(new_x, new_y);
	}
	
	public double estimateDistance(Walker neighbor){
		double dist = Math.sqrt(Math.pow(this.loc.x-neighbor.loc.x, 2)+Math.pow(this.loc.y-neighbor.loc.y, 2));
		return dist;
	}

	private boolean pointIsEmpty(Int2D p){

		if (world.obstacles.get(p.x, p.y)==0){
			return true;
		} else{
			return false;
		}
	}

	public void step(SimState state)
	{

		if (!this.lost){
			this.inShape = this.map.isInMap(this.loc.x,this.loc.y);
		}
		this.getNewLocation();


		world.walkers.setObjectLocation(this,this.loc);
	}
}