import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.lang.NumberFormatException;
import java.net.ServerSocket;
import java.net.Socket;

public class UIRPL {
	PileRPL pile = null;
	boolean menu = true;
	boolean keep = false;
	String savefile = "";
	String choice = "";


	private void write(PrintWriter output, String message)  {
		output.println(message);
		output.flush();
	}

	private void printmenu() {
		System.out.println("Menu:");
		System.out.println("1. Session.");
		System.out.println("2. Replay.");
		System.out.println("3. Network.");
		System.out.println("0. Exit");
		System.out.flush();
	}

	private void printhelp(PrintWriter output) {
		output.println("Operations:\tadd, less, time, sub.");
		output.println("Show the pile:\tpile.");
		output.println(":\tswap, sort.");
		output.println("Other commands:\tdrop.");
		output.println("Go back to the menu:\t\tquit, exit.");
		output.flush();
	}

	private void calculate(BufferedReader input, PrintWriter output, PrintWriter output2) throws IOException {
		String line;
		while(this.keep) {
			line = input.readLine();

			if(output2 != null) {
				output2.println(line);
			}

			if(this.choice.equals("2")) {
				output.println(line);
			}

			try {

				try {
					double v = Double.parseDouble(line);
					ObjEmpile oe = new ObjEmpile(v);
					this.pile.push(oe);
					continue;
				} catch(NumberFormatException exception) { }

				switch(line) {
					case "quit":
						this.keep = false;
						continue;	
					case "exit":
						this.keep = false;
						continue;	
					case "add":
						this.pile.add();
						continue;
					case "less":
						this.pile.less();
						continue;
					case "time":
						this.pile.time();
						continue;
					case "sub":
						this.pile.sub();
						continue;
					case "pile":
						this.write(output, pile.toString());
						continue;
					case "drop":
						this.pile.drop();
						continue;
					case "sort":
						this.pile.sort();
						continue;
					case "help":
						this.printhelp(output);
						continue;
					default:
						this.write(output, "Unknown command.");
						continue;
				}
			} catch(EmptyStack exception) { 
				this.write(output, exception.toString());
			} catch(FullStack exception) {
				this.write(output, exception.toString());
			}
		}
	}

	public PrintWriter savesession(BufferedReader input, PrintWriter output) {
		PrintWriter outputFile = null;
		String line = "";
		try {
			this.write(output, "Do you want to save this session in a file ? [y/n] ");

			line = input.readLine();
			if(line.equals("y")) {
				this.write(output, "Name of the file ? ");
				line = input.readLine();
				outputFile = new PrintWriter( new FileOutputStream(line) );
			}
		} catch(IOException exception) {
			this.write(output, exception.toString());
			this.write(output, "Error while creating/opening " + line + ", this session will not be saved !");
			return null;
		}
		this.savefile = line;
		return outputFile;
	}

	public UIRPL() throws IOException {
		Socket socket;
		ServerSocket server;
		BufferedReader localInput = new BufferedReader( new InputStreamReader( System.in ) );
		BufferedReader input = null;
		PrintWriter output = null;
		PrintWriter outputFile = null;
		PrintWriter localOutput = new PrintWriter(System.out);
		
		while(this.menu) {
			this.printmenu();
			this.choice = localInput.readLine();
			
			switch(this.choice) {
				case "0":
					this.write(localOutput, "bye");
					menu = false;
					keep = false;
					break;
				case "clear":
					System.out.print("\033[H\033[2J");  
					System.out.flush();
					break;
				case "1":
					// from System.in
					// to System.out
					// and File
					input = localInput;
					output = localOutput;					
					outputFile = this.savesession(input, output);
					keep = true;
					break;
				case "2":
					// from File
					// to System.out
					input = new BufferedReader( new InputStreamReader( new FileInputStream("input.log") ) );
					output = localOutput;
					keep = true;
					break;
				case "3":
					// from network
					// to network
					keep = true;
					server = new ServerSocket(6371);
					socket = server.accept();
					input = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );;
					output = new PrintWriter( socket.getOutputStream() );
					break;
				default:
					System.out.println("Unknown command");
					continue;
			}

			if(this.keep) {
				this.write(output, "type help to list available commands");
				this.pile = new PileRPL(100);
				this.calculate(input, output, outputFile);
				if(outputFile != null) {
					outputFile.close();
					this.write(output, "Session saved in the file: " + this.savefile);
				}
			}		
		}
	}

	public static void main(String[] args) throws IOException {
		new UIRPL();
	}	
}
