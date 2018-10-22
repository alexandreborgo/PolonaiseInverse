
public class ObjEmpile implements Comparable<ObjEmpile> {
	private double n;

	public ObjEmpile(double n) {
		this.n = n;
	}

	public double getValue() {
		return this.n;
	}

	public ObjEmpile add(ObjEmpile oe) {
		return new ObjEmpile(this.n + oe.getValue());
	}
	
	public ObjEmpile less(ObjEmpile oe) {
		return new ObjEmpile(this.n - oe.getValue());
	}
	
	public ObjEmpile time(ObjEmpile oe) {
		return new ObjEmpile(this.n * oe.getValue());
	}
	
	public ObjEmpile sub(ObjEmpile oe) {
		return new ObjEmpile(this.n / oe.getValue());
	}

	@Override
    public int compareTo(ObjEmpile obj) {
		if(this.getValue() > obj.getValue())
			return -1;
		else if(this.getValue() < obj.getValue())
			return 1;
		else
			return 0;
	}

	public String toString() {
		return String.valueOf(this.getValue());
	}
}
