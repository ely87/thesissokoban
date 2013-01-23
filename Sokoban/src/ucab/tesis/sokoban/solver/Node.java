package ucab.tesis.sokoban.solver;

public class Node implements Comparable<Node>{

		public static final int EMPTY=0,WALL=1,WHOLE=2,BOX=3,PLAYER=4,BOX_ON_WHOLE=5,PLAYER_ON_WHOLE = 6;
		public Integer Type;
		public Position position;
		public boolean unsafe;
	    
		public Node(int type,int x,int y)  {
			Type = type;
			position = new Position(x,y);
			unsafe = false;
		}

		public Node(Node node)  {
			this(node.Type,node.position);
		}
		
		public Node(int type,Position pos)  {
			this(type,pos.x,pos.y);
		}
		
		
		@Override
		public String toString() {
			return "node:("+Type+",X->"+position.x+",Y->"+position.y+")";
		}
	
		@Override
		public int hashCode() {
		int hash = 23;
			switch(Type){
			case PLAYER:
				hash = 31*hash;
				break;
			case PLAYER_ON_WHOLE:
				hash = 31*hash + 2;
				break;
			default:
				hash = 31*hash + Type.hashCode();
				break;
			}
			
			hash = 31*hash + position.hashCode();
			return hash;
		}
		
		@Override
		public int compareTo(Node n) {
				int type = n.Type;
				return type < Type ? -1 : type > Type ? 1 : 0;
		}
		
	}