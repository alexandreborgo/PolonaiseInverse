import java.util.Arrays;
import java.util.EmptyStackException;

public class PileRPL {

		private ObjEmpile[] pile;
		private int p_size;
		private int currentPos;

		public PileRPL(int size) {
				this.pile = new ObjEmpile[size];
				this.p_size = size;
				this.currentPos = 0;
		}

		public void add()  throws EmptyStack, FullStack {
			if(this.currentPos < 2) {
					throw new EmptyStack("Not enougth value in the stack to add.");
			}
			ObjEmpile oe1 = this.pop();
			ObjEmpile oe2 = this.pop();
			ObjEmpile oe3 = oe2.add(oe1);
			this.push(oe3);
		}

		public void less() throws EmptyStack, FullStack {
			if(this.currentPos < 2) {
				throw new EmptyStack("Not enougth value in the stack to divstract.");
			}
			ObjEmpile oe1 = this.pop();
			ObjEmpile oe2 = this.pop();
			ObjEmpile oe3 = oe2.less(oe1);
			this.push(oe3);
		}

		public void time() throws EmptyStack, FullStack {
			if(this.currentPos < 2) {
				throw new EmptyStack("Not enougth value in the stack multiply.");
			}
			ObjEmpile oe1 = this.pop();
			ObjEmpile oe2 = this.pop();
			ObjEmpile oe3 = oe2.time(oe1);
			this.push(oe3);
		}

		public void div() throws EmptyStack, FullStack {
			if(this.currentPos < 2) {
				throw new EmptyStack("Not enougth value in the stack to divide.");
			}
			ObjEmpile oe1 = this.pop();
			ObjEmpile oe2 = this.pop();
			ObjEmpile oe3 = oe2.div(oe1);
			this.push(oe3);
		}

		public void push(ObjEmpile obj) throws FullStack {
			if(this.currentPos >= this.p_size) {
				throw new FullStack("Stack is full (" + this.p_size + ").");
			}
			this.pile[this.currentPos++] = obj;
		}

		public ObjEmpile pop() throws EmptyStack {
			if(this.currentPos <= 0) {
				throw new EmptyStack("Stack is empty.");
			}
			return this.pile[--this.currentPos];
		}

		public void drop() throws EmptyStack {
			if(this.currentPos <= 0) {
				throw new EmptyStack("Stack is empty.");
			}
			this.currentPos--;
		}

		public void sort() {
			boolean sorting = true;
			while(sorting) {
				sorting = false;
				for(int i=0; i<this.currentPos-1; i++) {
					if(this.pile[i].getValue() > this.pile[i+1].getValue()) {
						ObjEmpile tmp = this.pile[i];
						this.pile[i] = this.pile[i+1];
						this.pile[i+1] = tmp;
						sorting = true;
					}
				}
			}
		}

		public void swap() throws EmptyStack, FullStack {
			if(this.currentPos < 2) {
				throw new EmptyStack("Not enougth value in the stack to swap.");
			}
			ObjEmpile oe1 = this.pop();
			ObjEmpile oe2 = this.pop();
			this.push(oe1);
			this.push(oe2);
		}

		public String toString() {
				String result = "";
				for(int i=0; i<this.currentPos; i++) { 
						result += "     +-------------+\n";
						result += String.format("%4d !%12s !\n", i, this.pile[i]);
				}				
				result += "     +-------------+\n";
				result += "  ->\n";
				return result;
		}
}
