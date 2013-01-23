package ucab.tesis.sokoban.solver;

public class Position{

	public int x,y;
	public Position(int x,int y) {
		this.x = x;
		this.y = y;
	}

	public Position(Position pos) {
		this.x = pos.x;
		this.y = pos.y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this.x == ((Position)obj).x && this.y == ((Position)obj).y)
			return true;
		
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash = 27*hash + x;
		hash = 31*hash + y;
		return hash;
	}

	@Override
	public String toString() {
		return "("+x+","+y+")";
	}
	
}
