package wormguides;

/**
 * Class which implements quaternions for rotating the scene graph
 * We use quaternions to account for the loss of a degree of freedom which
 * occurs on gimbal lock. In order to minimize the changes needed to our existing
 * rotation paradigm, quaternions as computed and converted to euler for applying to
 * the model. This allows us to maintain the javafx rotation paragidm and avoid
 * writing quaternion matrix classes which would apply their own transformation matrix
 * 
 * @author bradenkatzman
 *
 */
public class Quaternion {

	private double w,x,y,z;
	
	/**
	 * Constructor for initial quaterion
	 * 		= <1, axis.x, axis.y, axis.z>
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public Quaternion(double x, double y, double z) {
		//initialize to <1,0,0,0>
		this.w = 1.0;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Constructor for local quaternion
	 * @param w
	 * @param x
	 * @param y
	 * @param z
	 */
	public Quaternion(double w,
			double x, double y, double z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Performs the quaternion update quaternion *= localQuaternion, 
	 * 		where local_rotation =
	 * 			w = cos(angle/2)
	 * 			x = axis.x * sin(angle/2)
	 * 			y = axis.y * sin(angle/2)
	 * 			z = axis.z * sin(angle/2)
	 *
	 * 
	 * @param angle the angle of rotation around the axis
	 */
	public double[] updateQuaternion(double angle) {
		//create local quaternion
		double w,x,y,z;
		
		w = Math.cos(angle/twoDbl);
		x = this.x + Math.sin(angle/twoDbl);
		y = this.y + Math.sin(angle/twoDbl);
		z = this.z + Math.sin(angle/twoDbl);
		
		Quaternion localQuaternion = new Quaternion(w, x, y, z);
		
		System.out.println("pre multiplcation, quaternion = <" + this.w + ", " + this.x
				+ ", " + this.y + ", " + this.z + ">");
		
		
		this.multiplyQuaternion(localQuaternion);
		
		System.out.println("post multiplcation, quaternion = <" + this.w + ", " + this.x
				+ ", " + this.y + ", " + this.z + ">");
		
		return this.quaternionToEuler();
	}
	
	/**
	 * Mutliplies 'this' quaternion with the parameter quaternion,
	 * following quaternion multiplication rules:
	 * 		(q1*q1).w = ((w1*w2) - (x1*x2) - (y1*y2) - (z1*z2))
	 * 		(q1*q1).x = ((w1*x2) + (x1*w2) + (y1*z2) - (z1*y2))
	 * 		(q1*q1).y = ((w1*y2) - (x1*z2) + (y1*w2) + (z1*x2))
	 * 		(q1*q1).z = ((w1*z2) + (x1*y2) - (y1*x2) + (z1*w2))
	 * 
	 * @param q the quaternion to multiply with 'this' quaternion
	 */
	private void multiplyQuaternion(Quaternion q) {
		this.w = ((this.w*q.getW()) - (this.x*q.getX()) - (this.y*q.getY()) - (this.z*q.getZ()));
		this.x = ((this.w*q.getX()) + (this.x*q.getW()) + (this.y*q.getZ()) - (this.z*q.getY()));
		this.y = ((this.w*q.getY()) - (this.x*q.getZ()) + (this.y*q.getW()) + (this.z*q.getX()));
		this.z = ((this.w*q.getZ()) + (this.x*q.getY()) - (this.y*q.getX()) + (this.z*q.getW()));
	}
	
	/**
	 * Convert the quaternion rotation into euler to apply to model
	 * 
	 * heading = rotation about y-axis
	 * 		   = atan2((2*y*(w-2)*x*z) , (1 - (2*(y^2)) - (2*(z^2))))
	 * 
	 * attitude = rotation about z-axis
	 * 			= asin((2*x*y) + (2*z*w))
	 * 
	 * bank = rotation about x-axis
	 * 		= atan2((2*x*(w-2)*y*z) , (1 - (2*(x^2)) - (2*(z^2)))
	 * 
	 * North and soul pole exceptions:
	 *  if (((x*y) + (z*w)) == 0.5) { //north pole
	 * 		heading = 2*atan2(x, w)
	 * 		bank = 0
	 * 	} else if (((x*y) + (z*w)) == -0.5) { //south pole
	 * 		heading = -2*atan2(x, w)
	 * 		bank = 0
	 * 	}
	 * 
	 * @return the converted quaternion to euler transform
	 */
	private double[] quaternionToEuler() {
		double[] eulerTransform = new double[3];
		
		double heading, attitude, bank;
		heading = 0.0;
		attitude = 0.0;
		bank = 0.0;
		
		if (((x*y) + (z*w)) == NORTH_POLE) {
			heading = (2*Math.atan2(x,  w));
			bank = 0.0;
		} else if (((x*y) + (z*w)) == SOUTH_POLE) {
			heading = -(2*Math.atan2(x,  w));
			bank = 0.0;
		} else {
			heading = Math.atan2((2*y*(w-2)*x*z) , (1 - (2*(Math.pow(y, 2))) - (2*(Math.pow(z,  2)))));
			bank = Math.atan2((2*x*(w-2)*y*z) , (1 - (2*(Math.pow(x, 2))) - (2*(Math.pow(z,  2)))));
		}
		
		attitude = Math.asin((2*x*y) + (2*z*w));
		
		//add in x,y,z order
		eulerTransform[0] = bank;
		eulerTransform[1] = heading;
		eulerTransform[2] = attitude;
		
		System.out.println("quaternion to euler = " + bank + ", " + heading + ", " + attitude);
		
		return eulerTransform;
	}
	
	public double getW() {
		return this.w;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public double getZ() {
		return this.z;
	}
	
	private final static double NORTH_POLE = 0.5;
	private final static double SOUTH_POLE = -0.5;
	private final static double twoDbl = 2.0;
}
