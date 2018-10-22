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
			ObjEmpile oe3 = oe1.add(oe2);
			this.push(oe3);
		}

		public void less() throws EmptyStack, FullStack {
			if(this.currentPos < 2) {
				throw new EmptyStack("Not enougth value in the stack to substract.");
			}
			ObjEmpile oe1 = this.pop();
			ObjEmpile oe2 = this.pop();
			ObjEmpile oe3 = oe1.less(oe2);
			this.push(oe3);
		}

		public void time() throws EmptyStack, FullStack {
			if(this.currentPos < 2) {
				throw new EmptyStack("Not enougth value in the stack multiply.");
			}
			ObjEmpile oe1 = this.pop();
			ObjEmpile oe2 = this.pop();
			ObjEmpile oe3 = oe1.time(oe2);
			this.push(oe3);
		}

		public void sub() throws EmptyStack, FullStack {
			if(this.currentPos < 2) {
				throw new EmptyStack("Not enougth value in the stack to divide.");
			}
			ObjEmpile oe1 = this.pop();
			ObjEmpile oe2 = this.pop();
			ObjEmpile oe3 = oe1.sub(oe2);
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
			//Arrays.sort(this.pile);
		}

		public String toString() {
				String result = "";
				result += "  +-------------+\n";
				for(int i=0; i<this.currentPos; i++) { 
						result += String.format("%d !%12s !\n", i, this.pile[i]);
						result += "  +-------------+\n";
				}
				return result;
		}
}
