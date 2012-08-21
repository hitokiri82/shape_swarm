import sim.engine.*;
import sim.field.grid.*;
import sim.util.*;

public class WalkersWorld extends SimState
{
	public boolean obstacleCourse = true;
	public boolean loadWorldWithObstacles = false;

	public SparseGrid2D walkers;
	public IntGrid2D obstacles;

	public int gridSize;

	public int gridWidth;
	public int gridHeight;
	public int numWalkers = 450;    

	public int RANGE_RADIUS = 5;

	public int REPULSION_RADIUS = 5;

	public double Repulsion = 7.0;
	public double Cohesion = 8.0;
	public double Evasion = 5.0;
	public double Inertia = 1.0;

	boolean[][] course = null;
	//	String figura;
	//	String[] paths;

	public WalkersWorld(long seed)
	{
		super(seed);
	}

	public void start()
	{
		super.start();

		boolean[][] bitmap = null;

		try {
			bitmap = Bitmap.loadImage("bitmaps/grid.bmp");
			//bitmap = Bitmap.loadImage("bitmaps/barbell.bmp");
			//bitmap = Bitmap.loadImage("bitmaps/square70.bmp");
			//bitmap = Bitmap.loadImage("bitmaps/bent_bottleneck.bmp");
			//bitmap = Bitmap.loadImage("bitmaps/letter_x.bmp");

			if (obstacleCourse) {
				course = Bitmap.loadImage("bitmaps/course.bmp");
			} 
			else if (loadWorldWithObstacles) {
				course = Bitmap.loadImage("bitmaps/world.bmp");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.gridSize = bitmap.length;
		this.gridHeight = bitmap.length;
		this.gridWidth = bitmap.length;

		walkers = new SparseGrid2D(gridWidth, gridHeight);
		obstacles = new IntGrid2D(gridWidth,gridHeight);
		Mapper map = new Mapper(bitmap, gridSize);

		if (obstacleCourse) {

			for (int i = 0; i < gridHeight; i++) {
				for (int j = 0; j < gridWidth; j++) {
					if (!course[i][j]) {
						obstacles.field[i][j] = 1;
					}
				}
			}			
			Steppable mover = new Steppable() {
				int os = 1;
				public void step(SimState state)
				{
					int aux;
					for (int i = 0; i < gridHeight; i++) {
						for (int j = 0; j < gridWidth; j++) {
							aux = (i+os) % course.length;
							if (!course[aux][j]) {
								obstacles.field[i][j] = 1;
							} else {
								obstacles.field[i][j] = 0;
							}
						}
					}
					os++;
				}
				static final long serialVersionUID = 6330208160095250478L;
			};

			schedule.scheduleRepeating(Schedule.EPOCH,0,mover,5);
		}
		else if (loadWorldWithObstacles) {
			for (int i = 0; i < gridHeight; i++) {
				for (int j = 0; j < gridWidth; j++) {
					if (!course[i][j]) {
						obstacles.field[i][j] = 1;
					}
				}
			}	
		}

		Walker w;
		int xo,yo;
		Int2D loc = null;
		for(int i=0 ; i<numWalkers ; i++)
		{
			do {
				xo = random.nextInt(gridWidth);
				yo = random.nextInt(gridHeight);
				loc = new Int2D(xo, yo);
				walkers.getObjectsAtLocation(loc);

			} while ((obstacles.get(xo, yo)==1)||(walkers.getObjectsAtLocation(new Double2D(xo, yo))!=null));
			w = new Walker(loc,map,this);
			schedule.scheduleRepeating(Schedule.EPOCH,2,w,1);
			walkers.setObjectLocation(w,new Int2D(xo,yo));	
		}
	}

	public Bag obtainNeighbors(Walker w){
		Int2D loc = walkers.getObjectLocation(w);
		Bag result = new Bag();
		Bag result2 = new Bag();

		walkers.getNeighborsMaxDistance(loc.x, loc.y, RANGE_RADIUS, true, result, null,null);

		while (result.size() > 0){
			Object o = result.pop();
			result.removeMultiply(o);
			result2.add(o);
		}
		result2.remove(w);
		return result2;
	}

	public static void main(String[] args)
	{
		doLoop(WalkersWorld.class, args);
		System.exit(0);
	}    

	static final long serialVersionUID = 9115981605874680023L;    

	public double getRepulsion() {
		return Repulsion;
	}

	public void setRepulsion(double repulsion) {
		Repulsion = repulsion;
	}

	public double getCohesion() {
		return Cohesion;
	}

	public void setCohesion(double cohesion) {
		Cohesion = cohesion;
	}

	public double getEvasion() {
		return Evasion;
	}

	public void setEvasion(double evasion) {
		Evasion = evasion;
	}

	public double getInercia() {
		return Inertia;
	}

	public void setInertia(double inertia) {
		Inertia = inertia;
	}

	public boolean isObstacleCourse() {
		return obstacleCourse;
	}

	public void setObstacleCourse(boolean obstacleCourse) {
		this.obstacleCourse = obstacleCourse;
	}
	public int getnumWalkers(){
		return this.numWalkers;
	}

	public void setnumWalkers(int n){
		this.numWalkers = n;
	}

	public boolean isLoadWorldWithObstacles() {
		return loadWorldWithObstacles;
	}

	public void setLoadWorldWithObstacles(boolean loadWorldWithObstacles) {
		this.loadWorldWithObstacles = loadWorldWithObstacles;
	}

	public int getRANGE_RADIUS() {
		return RANGE_RADIUS;
	}

	public void setRANGE_RADIUS(int rADIO_ALCANCE) {
		RANGE_RADIUS = rADIO_ALCANCE;
	}

	public int getREPULSION_RADIUS() {
		return REPULSION_RADIUS;
	}

	public void setREPULSION_RADIUS(int rADIO_REPULSION) {
		REPULSION_RADIUS = rADIO_REPULSION;
	}

}