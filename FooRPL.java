public class FooRPL {
	public static void main(String[] args) throws EmptyStack, FullStack {
		PileRPL pile = new PileRPL(10);
		ObjEmpile oe1 = new ObjEmpile(1);
		ObjEmpile oe2 = new ObjEmpile(2);
		ObjEmpile oe3 = new ObjEmpile(7);
		ObjEmpile oe4 = new ObjEmpile(5);
		ObjEmpile oe5 = new ObjEmpile(-5);
		pile.push(oe1);
		System.out.println(pile);
		pile.push(oe2);
		System.out.println(pile);
		pile.add();
		System.out.println("Add");
		System.out.println(pile);	
		pile.push(oe3);
		System.out.println(pile);
		pile.add();
		System.out.println("Add");
		System.out.println(pile);
		pile.push(oe4);
		System.out.println(pile);
		pile.push(oe5);
		System.out.println(pile);
		System.out.println("Less");
		pile.less();
		System.out.println(pile);
		System.out.println("Less");
		pile.less();
		System.out.println(pile);
	}
}
