
public class ObjEmpile {
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
	
	public ObjEmpile div(ObjEmpile oe) {
		return new ObjEmpile(this.n / oe.getValue());
	}

	public String toString() {
		return String.format("%.4f", this.getValue());
	}
}
