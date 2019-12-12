package test;

class Planet {
	int x;
	int y;
	int z;
	int vx;
	int vy;
	int vz;
	
	public Planet(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int energy() {
		return (Math.abs(x) + Math.abs(y) + Math.abs(z)) 
				* (Math.abs(vx) + Math.abs(vy) + Math.abs(vz));
	}
	
	@Override
	public String toString() {
		return "pos=<" + x + "," + y + "," + z +"> vel=<" + vx + "," + vy + "," + vz + ">";
	}
}

public class Planets {
	
	public static boolean isSame(Planet[] p, Planet[] o) {
		for (int i = 0; i < p.length; i++) {
			if (p[i].x != o[i].x || p[i].y != o[i].y || p[i].z != o[i].z) {
				return false;
			}
		}
		return true;
	}
	
	public static int getV(int p1, int p2) {
		return p1 < p2? 1 : p1 > p2? -1: 0;
	}
	
	public static int ts(Planet[] planets) {
		for (Planet p1 : planets) {
			for (Planet p2 : planets) {
				if (p1 != p2) {
					p1.vx += getV(p1.x, p2.x);
					p1.vy += getV(p1.y, p2.y);
					p1.vz += getV(p1.z, p2.z);
				}
			}
		}
		
		int energy = 0;
		for (Planet p : planets) {
			p.x += p.vx;
			p.y += p.vy;
			p.z += p.vz;
			energy += p.energy();
		}
		return energy;
	}

	public static void main(String[] args) {
		Planet planets[] = new Planet[] {
			new Planet(17, -12, 13),
			new Planet(2, 1, 1),
			new Planet(-1, -17, 7),
			new Planet(12, -14, 18)
		};
		
//		Planet planets[] = new Planet[] {
//				new Planet(-8, -10, 0),
//				new Planet(5, 5, 10),
//				new Planet(2, -7, 3),
//				new Planet(9, -8, -3)
//			};
//		Planet planets[] = new Planet[] {
//				new Planet(-1, 0, 2),
//				new Planet(2, -10, -7),
//				new Planet(4, -8, 8),
//				new Planet(3, 5, -1)
//			};
		
//		int e = 0;
//		for (int i = 0; i < 1000; i++) {
//			//if (i % 10 == 0) {
////				for (Planet p : planets) {
////					System.err.println(p);
////				}
//			//}
//			e = ts(planets);
//		}
		//System.err.println(e);
		Planet orig[] = new Planet[] {
				new Planet(17, -12, 13),
				new Planet(2, 1, 1),
				new Planet(-1, -17, 7),
				new Planet(12, -14, 18)
			};
		
		long c = 0;
		ts(planets);
		while (!isSame(planets, orig)) {
			c++;
			ts(planets);
		}
		System.err.println(c);
	}

}
