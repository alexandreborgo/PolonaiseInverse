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

	boolean keep = false;
	boolean menu = true;

	boolean record = false;
	boolean replay = false;
	boolean network = false;
	boolean overhttp = false;
	
	String savefile = ""; // name of the current save file to display "Session save in x file" at the end of the session
	String choice = ""; // menu choice, also current state 

	BufferedReader localInput; // System.in
	PrintWriter localOutput; // System.out

	BufferedReader input; // main input
	PrintWriter output; // main output
	PrintWriter outputFile; // output if the user asks to save the session

	Socket socket;
	ServerSocket server;

	private void write(String message)  {
		this.output.println(message);
		this.output.flush();
	}

	private void printhelp() {
		this.output.println("Operations: add, less, time, sub.");
		this.output.println("Show the pile: pile.");
		this.output.println("Other: swap, sort.");
		this.output.println("Other commands: drop.");
		this.output.println("Go back to the menu: quit, exit.");
		this.output.flush();
	}

	private String execCommand(String command) {
		try {
			try {
				double v = Double.parseDouble(command);
				ObjEmpile oe = new ObjEmpile(v);
				this.pile.push(oe);
			} catch(NumberFormatException exception) {
				switch(command) {
					case "quit":
						this.keep = false;	
					case "exit":
						this.keep = false;
					case "add":
						this.pile.add();
						break;
					case "less":
						this.pile.less();
						break;
					case "time":
						this.pile.time();
						break;
					case "sub":
						this.pile.sub();
						break;
					case "drop":
						this.pile.drop();
						break;
					case "sort":
						this.pile.sort();
						break;
					case "swap":
						this.pile.swap();
						break;
					case "help":
						this.printhelp();
						break;
					case "clear":
						return "\033[H\033[2J";
					default:
						return "Unknown command.";
				}
			}

		} catch(EmptyStack exception) { 
			this.write(exception.toString());
		} catch(FullStack exception) {
			this.write(exception.toString());
		}

		return this.pile.toString();
	}

	private void calcloop() throws IOException {
		String line;
		this.write("type help to list available commands");
		while(this.keep) {
			line = this.input.readLine();

			/* line is null if calc over network and we lost the connection */
			if(line == null) {
				this.output = this.localOutput;
				this.write("Connection lost.");
				break;
			}

			if(this.record && this.outputFile != null) {
				this.outputFile.println(line);
			}

			if(this.replay) {
				this.write(line);
			}

			this.write(this.execCommand(line));
		}
	}

	private void calcoverhttploop() throws IOException {		
		String header = "";
		String html = "";
		String line = "";
		String response = "";
		this.server = new ServerSocket(8080);

		while(this.keep) {
			this.socket = this.server.accept();
	
			this.input = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
			this.output = new PrintWriter( socket.getOutputStream() );

			header = "";
			line = "";
			response = "";
			html = "";

			while(true) {
				line = this.input.readLine();
				header += line + "\n";
				if(line.equals(""))
					break;
			}

			try {
				line = header.split("/?cmd=")[1].split(" HTTP/1.1")[0];
			} catch(ArrayIndexOutOfBoundsException exception) {
				line = "FALSE";
			}

			System.out.println("RCV header :\n" + header);

			if(!line.equals("FALSE")) {
				html += "<pre>" + this.execCommand(line) + "</pre><br \\>";
			}
			else {
				html += "<pre>" + this.pile.toString() + "</pre>";
			}			
			
			html += "<form method='get' action='http://10.0.4.12:8080/'><input type='text' name='cmd' placeholder='command' autofocus /> <input type='submit' value='Submit'></form>";
			
			response += "HTTP/1.1 200 OK\n";
			response += "Content-Type: text/html\n";
			response += "Content-Length: " + html.length() + "\n";
			response += "Connection: close\n";
			response += "\n";
			response += html + "\n";
			response += "\r\n";
			
			this.write(response);
		}
	}

	public PrintWriter savesession(BufferedReader input, PrintWriter output) {
		PrintWriter outputFile = null;
		String line = "";
		try {
			this.write("Do you want to save this session in a file ? [y/n] ");

			line = input.readLine();
			if(line.equals("y")) {
				this.write("Name of the file ? ");
				line = input.readLine();
				outputFile = new PrintWriter( new FileOutputStream(line) );
			}
		} catch(IOException exception) {
			this.write(exception.toString());
			this.write("Error while creating/opening " + line + ", this session will not be saved !");
			return null;
		}
		this.savefile = line;
		return outputFile;
	}

	public BufferedReader replaysession(BufferedReader input, PrintWriter output) throws SessionFile {
		String line = "";
		try {
			this.write("In which file is saved the session to replay?");
			line = input.readLine();
			return new BufferedReader( new InputStreamReader( new FileInputStream(line) ) );
			
		} catch(IOException exception) {
			throw new SessionFile("Unable to read the session file.");
		}
	}

	public UIRPL() {
		this.localInput = new BufferedReader( new InputStreamReader( System.in ) ); 
		this.localOutput = new PrintWriter(System.out); 

		this.input = null;
		this.output = null;

		this.socket = null;
		this.server = null;

		this.outputFile = null;
	}

	public void menuloop() {
		while(this.menu) {
			try {
				System.out.println("Menu:");
				System.out.println("1. Session.");
				System.out.println("2. Replay.");
				System.out.println("3. Network.");
				System.out.println("4. Over HTTP.");
				System.out.println("0. Exit");
				System.out.flush();

				this.choice = localInput.readLine();
				
				switch(this.choice) {
					case "0":
						// exit
						this.output = localOutput;
						this.write("bye");
						this.menu = false;
						this.keep = false;
						break;
					case "clear":
						this.write("\033[H\033[2J");
						break;
					case "1":
						// from System.in
						// to System.out
						// and file (if user chose to save the session)
						this.input = localInput;
						this.output = localOutput;
						this.outputFile = this.savesession(this.input, this.output);
						this.keep = true;
						break;
					case "2":
						// from file
						// to System.out
						try {
							this.output = this.localOutput;
							this.input = this.replaysession(this.localInput, this.localOutput);
							this.keep = true;
							this.replay = true;
						} catch(SessionFile exception) {
							this.write(exception.getMessage());
							this.keep = false;
						}
						break;
					case "3":
						// from network
						// to network
						// and file (if user chose to save the session)
						this.keep = true;
						this.server = new ServerSocket(6371);
						this.output = localOutput;
						this.write("Waiting a connection on port 6371...");
						this.socket = server.accept();
						this.write("Someone is connected!");
						this.input = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
						this.output = new PrintWriter( socket.getOutputStream() );
						this.outputFile = this.savesession(input, output);
						break;
					case "4":
						this.overhttp = true;
						this.keep = true;
						this.network = true;
						this.output = localOutput;
						this.write("Waiting for connections on port 8080...");
						break;
					default:
						this.write("Unknown command");
						continue;
				}

				if(this.keep) {
					this.pile = new PileRPL(100);
					
					if(this.overhttp) {
						this.calcoverhttploop();
						this.overhttp = false;
					} 					
					else {
						this.calcloop();
					}
					
					if(this.record) {
						this.outputFile.close();
						this.write("Session saved in the file: " + this.savefile);
						this.outputFile = null;
						this.savefile = "";
						this.record = false;
					}

					if(this.network) {
						socket.close();
						server.close();
						this.network = false;
					}

					this.replay = false;
				}

			} catch(IOException exception) {
				System.out.println("Error");
				exception.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws IOException {
		new UIRPL().menuloop();
	}	
}
