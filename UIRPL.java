import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.lang.NumberFormatException;

public class UIRPL {
	public static void main(String[] args) throws IOException, EmptyStack, FullStack {
		PileRPL pile = null;
		BufferedReader input = null;
		BufferedReader menuinput = new BufferedReader( new InputStreamReader( System.in ) );
		PrintWriter output = new PrintWriter( new FileOutputStream("log.txt") );
		String choice;
		String line;
		boolean menu = true;
		boolean keep = false;
		while(menu) {
			System.out.println("Menu:");
			System.out.println("1. Session.");
			System.out.println("2. Replay.");
			System.out.println("3. Network.");
			System.out.println("0. Exit");
			choice = menuinput.readLine();

			if(choice.equals("0")) {
				System.out.println("bye");
				menu = false;
				keep = false;
			}
			else if(choice.equals("clear")) {
				System.out.print("\033[H\033[2J");  
    			System.out.flush();
			}
			else if(choice.equals("1")) {
				// from System.in
				input = menuinput;
				keep = true;
			}
			else if(choice.equals("2")) {
				// from file
				input = new BufferedReader( new InputStreamReader( new FileInputStream("out2.txt") ) );
				keep = true;
			}
			else if(choice.equals("3")) {
				// from network
				keep = true;
			}
			else {
				System.out.println("Unknown command");
				continue;
			}

			if(keep) {
				System.out.println("type help to list available commands");
				pile = new PileRPL(10);
			}

			while(keep) {
				line = input.readLine();
				output.println(line);

				if(choice.equals("2")) {
					System.out.println(line);
				}
				try {
					try {
						double v = Double.parseDouble(line);
						ObjEmpile oe = new ObjEmpile(v);
						pile.push(oe);
						continue;
					} catch(NumberFormatException exception) { }

					switch(line) {
						case "quit":
							keep = false;
							continue;	
						case "exit":
							keep = false;
							continue;	
						case "add":
							pile.add();
							continue;
						case "less":
							pile.less();
							continue;
						case "time":
							pile.time();
							continue;
						case "sub":
							pile.sub();
							continue;
						case "pile":
							System.out.println(pile);
							continue;
						case "drop":
							pile.drop();
							continue;
						case "sort":
							pile.sort();
							continue;
						case "help":
							System.out.println("Operations:\tadd, less, time, sub.");
							System.out.println("Show the pile:\tpile.");
							System.out.println(":\tswap, sort.");
							System.out.println("Other commands:\tdrop.");
							System.out.println("Go back to the menu:\t\tquit, exit.");
							continue;
						default:
							System.out.println("Unknown command.");
							continue;
					}
				} catch(EmptyStack exception) { 
					System.out.println(exception);
				} catch(FullStack exception) {
					System.out.println(exception);
				}
			}
		}

		output.close();
	}	
}
