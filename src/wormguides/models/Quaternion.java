package wormguides.models;

import java.util.ArrayList;

public class Quaternion {
	private double w, x, y, z;

	/**
	 * Initial quaternion will be <1,0,0,0> i.e. no rotation
	 */
	public Quaternion() {
		this.w = 1.;
		this.x = 0.;
		this.y = 0.;
		this.z = 0.;
	}

	/**
	 * Used only locally to compute the 'local_rotation'
	 * 
	 * @param w
	 * @param x
	 * @param y
	 * @param z
	 */
	private Quaternion(double w, double x, double y, double z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * * Based on the angle of rotation, this method computes the local_rotation
	 * quaternion and then updates 'this' quaternion
	 * 
	 * Local rotation quaternion values: w = cos(angOfRotation/2) x = axis.x *
	 * sin(angOfRotation/2) y = axis.y * sin(angOfRotation/2) z = axis.z *
	 * sin(angOfRotation/2)
	 * 
	 * @param angOfRotation
	 *            - the angle of rotation
	 * @param axisX
	 *            - the x axis of the rotation direction
	 * @param axisY
	 *            - the y axis of the rotation direction
	 * @param axisZ
	 *            - the z axis of the rotation direction
	 */
	public void updateOnRotate(double angOfRotation, double axisX, double axisY, double axisZ) {
		// compute the local rotation quaternion
		double w, x, y, z;

		w = Math.cos(angOfRotation / 2.);
        x = axisX * Math.sin(angOfRotation / 2.);
        y = axisY * Math.sin(angOfRotation / 2.);
        z = axisZ * Math.sin(angOfRotation / 2.);

		Quaternion local_rotation = new Quaternion(w, x, y, z);

		multiplyQuaternions(local_rotation);
	}

	/**
	 * Performs the quaternion update: total = local_rotation * total
	 * 
	 * Quaternion multiplication rules, given Q1 and Q2:
	 * 
	 * (Q1 * Q2).w = (w1*w2 - x1*x2 - y1*y2 - z1*z2) (Q1 * Q2).x = (w1*x2 +
	 * x1*w2 + y1*z2 - z1*y2) (Q1 * Q2).y = (w1*y2 - x1*z2 + y1*w2 + z1*x2) (Q1
	 * * Q2).z = (w1*z2 + x1*y2 - y1*x2 + z1*w2)
	 * 
	 * @param local_rotation
	 */
	private void multiplyQuaternions(Quaternion local_rotation) {

		this.w = ((getW() * local_rotation.getW()) - (getX() * local_rotation.getX()) - (getY() * local_rotation.getY())
				- (getZ() * local_rotation.getZ()));

		this.x = ((getW() * local_rotation.getX()) + (getX() * local_rotation.getW()) + (getY() * local_rotation.getZ())
				- (getZ() * local_rotation.getY()));

		this.y = ((getW() * local_rotation.getY()) - (getX() * local_rotation.getZ()) + (getY() * local_rotation.getW())
				+ (getZ() * local_rotation.getX()));

		this.z = ((getW() * local_rotation.getZ()) + (getX() * local_rotation.getY()) - (getY() * local_rotation.getW())
				+ (getZ() * local_rotation.getW()));

		// normalize quaternion
		double magnitude = Math
				.sqrt(Math.pow(getW(), 2) + Math.pow(getX(), 2) + Math.pow(getY(), 2) + Math.pow(getZ(), 2));

		this.w = getW() / magnitude;
		this.x = getX() / magnitude;
		this.y = getY() / magnitude;
		this.z = getZ() / magnitude;
	}

	public double getW() {
		return w;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		// return y;
		return z;
	}

	/**
	 * Conversion from Quaternion to Euler
	 * 
	 * heading = y-axis attitude = z-axis bank = x-axis
	 * 
	 * heading = atan2(2*qy*(qw-2)*qx*qz , 1 - (2*(qy^2)) - 2*(qz^2)) attitude =
	 * asin(2*qx*qy + 2*qz*qw) bank = atan2(2*qx*(qw-2)*qy*qz , 1 - (2*(qx^2)) -
	 * 2*(qz^2))
	 * 
	 * Source:
	 * http://www.euclideanspace.com/maths/geometry/rotations/conversions/
	 * quaternionToEuler/
	 * 
	 * http://www.cprogramming.com/tutorial/3d/quaternions.html
	 * 
	 * @return the converted quaternion
	 */
	public ArrayList<Double> toEulerRotation() {
		ArrayList<Double> eulerRotation = new ArrayList<Double>();

		double heading, attitude, bank;
		heading = 0.;
		attitude = 0.;
		bank = 0.;

		/*
		 * check for cases north and south poles
		 * 
		 * North pole: x*y + z*w = 0.5 --> which gives heading = 2 * atan2(x, w)
		 * bank = 0
		 * 
		 * South pole: x*y + z*w = -0.5 --> which gives heading = -2 * atan2(x,
		 * w) bank = 0
		 */
		// TODO check if it should be greater or less than NORTH_POLE or
		// SOUTH_POLE
		double f = (x * y) + (z * w);
		if (f > NORTH_POLE) {
            heading = 2 * Math.atan2(x, w);
            attitude = Math.PI / 2.;
			bank = 0.f;
		} else if (f < SOUTH_POLE) {
            heading = -2 * Math.atan2(x, w);
            attitude = -Math.PI / 2.;
			bank = 0;
		} else {
			double sqx = this.getX() * this.getX();
			double sqy = this.getY() * this.getY();
			double sqz = this.getZ() * this.getZ();

            heading = Math.atan2((2 * y * (w - 2.) * x * z), (1 - (2 * sqy) - (2 * sqz)));
            attitude = Math.asin(2 * f);
            bank = Math.atan2((2 * x * (w - 2) * y * z), (1 - 2 * sqx - 2 * sqz));
        }

		eulerRotation.add(Math.toDegrees(heading));
		eulerRotation.add(Math.toDegrees(attitude));
		eulerRotation.add(Math.toDegrees(bank));

		return eulerRotation;
	}

	// public ArrayList<Double> toEulerRotation() {
	// ArrayList<Double> eulerRotation = new ArrayList<Double>();
	//
	// double heading, attitude, bank;
	// heading = 0.;
	// attitude = 0.;
	// bank = 0.;
	//
	// double sqw = this.getW()*this.getW();
	// double sqx = this.getX()*this.getX();
	// double sqy = this.getY()*this.getY();
	// double sqz = this.getZ()*this.getZ();
	//
	// double unit = sqx + sqy + sqz + sqw;
	// double test = this.getX()*this.getY() + this.getZ()*this.getW();
	//
	// if (test > NORTH_POLE*unit) {
	// heading = 2*Math.atan2(this.getX(), this.getW());
	// attitude = Math.PI/2;
	// bank = 0;
	// } else if (test < SOUTH_POLE*unit) {
	// heading = -2*Math.atan2(this.getX(), this.getW());
	// attitude = -Math.PI/2;
	// bank = 0;
	// } else {
	// heading = Math.atan2(2*this.getY()*this.getW() -
	// 2*this.getX()*this.getZ(), sqx-sqy-sqz+sqw);
	// attitude = Math.asin(2*(test/unit));
	// bank = Math.atan2(2*this.getX()*this.getW() - 2*this.getY()*this.getZ(),
	// -sqx+sqy-sqz+sqw);
	// }
	//
	// eulerRotation.add(heading);
	// eulerRotation.add(attitude);
	// eulerRotation.add(bank);
	//
	// return eulerRotation;
	// }

	private final static double NORTH_POLE = 0.4999;

    /**
     * Conversion from Quaternion to Euler
     * <p>
     * heading = y-axis attitude = z-axis bank = x-axis
     * <p>
     * heading = atan2(2*qy*(qw-2)*qx*qz , 1 - (2*(qy^2)) - 2*(qz^2)) attitude =
     * asin(2*qx*qy + 2*qz*qw) bank = atan2(2*qx*(qw-2)*qy*qz , 1 - (2*(qx^2)) -
     * 2*(qz^2))
     * <p>
     * Source:
     * http://www.euclideanspace.com/maths/geometry/rotations/conversions/
     * quaternionToEuler/
     * <p>
     * http://www.cprogramming.com/tutorial/3d/quaternions.html
     *
     * @return the converted quaternion
     */
    public ArrayList<Double> toEulerRotation() {
        ArrayList<Double> eulerRotation = new ArrayList<Double>();

        double heading, attitude, bank;
        heading = 0.;
        attitude = 0.;
        bank = 0.;

		/*
         * check for cases north and south poles
		 *
		 * North pole: x*y + z*w = 0.5 --> which gives heading = 2 * atan2(x, w)
		 * bank = 0
		 *
		 * South pole: x*y + z*w = -0.5 --> which gives heading = -2 * atan2(x,
		 * w) bank = 0
		 */
        // TODO check if it should be greater or less than NORTH_POLE or
        // SOUTH_POLE
        double f = (x * y) + (z * w);
        if (f > NORTH_POLE) {
            heading = 2 * Math.atan2(x, w);
            attitude = Math.PI / 2.;
            bank = 0.f;
        } else if (f < SOUTH_POLE) {
            heading = -2 * Math.atan2(x, w);
            attitude = -Math.PI / 2.;
            bank = 0;
        } else {
            double sqx = this.getX() * this.getX();
            double sqy = this.getY() * this.getY();
            double sqz = this.getZ() * this.getZ();

            heading = Math.atan2((2 * y * (w - 2.) * x * z), (1 - (2 * sqy) - (2 * sqz)));
            attitude = Math.asin(2 * f);
            bank = Math.atan2((2 * x * (w - 2) * y * z), (1 - 2 * sqx - 2 * sqz));
        }

        eulerRotation.add(Math.toDegrees(heading));
        eulerRotation.add(Math.toDegrees(attitude));
        eulerRotation.add(Math.toDegrees(bank));

        return eulerRotation;
    }

    private final static double SOUTH_POLE = -0.499;
}