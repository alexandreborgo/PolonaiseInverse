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
	int p_size;

	boolean keep = false;

	boolean replay = false;

	BufferedReader localInput; // System.in
	PrintWriter localOutput; // System.out

	BufferedReader input; // main input
	PrintWriter output; // main output
	PrintWriter outputFile; // output if the user asks to save the session

	Socket socket;
	ServerSocket server;

	public UIRPL(int p_size) {
		this.p_size = p_size;
		
		this.localInput = new BufferedReader( new InputStreamReader( System.in ) ); 
		this.localOutput = new PrintWriter( System.out ); 

		this.input = null;
		this.output = null;
		this.outputFile = null;

		this.socket = null;
		this.server = null;
	}

	public UIRPL() {
		this(20);
	}

	private void write(PrintWriter output, String message)  {
		output.println(message);
		output.flush();
	}

	private String execCommand(String command) {
		String result = "";
		try {
			try {
				double v = Double.parseDouble(command);
				ObjEmpile oe = new ObjEmpile(v);
				this.pile.push(oe);
			} catch(NumberFormatException exception) {
				switch(command) {
					case "quit":
						this.keep = false;
						return "";
					case "exit":
						this.keep = false;
						return "";
					case "add":
						this.pile.add();
						break;
					case "less":
						this.pile.less();
						break;
					case "time":
						this.pile.time();
						break;
					case "div":
						this.pile.div();
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
						result = "Operations: add, less, time, div.\nOther: swap, sort, drop.\nGo back to the menu: quit, exit.\n\n";
						break;
					case "clear":
						result = "\033[H\033[2J\n\n";
						break;
					default:
						result = "Unknown command, type help to list all available commands.\n\n";
						break;
				}
			}

		} catch(EmptyStack exception) {
			this.write(this.output, exception.toString());
		} catch(FullStack exception) {
			this.write(this.output, exception.toString());
		}

		return result + this.pile.toString();
	}

	private void calcloop() throws IOException {
		
		String line = "";

		this.write(this.output, "type help to list available commands");
		this.pile = new PileRPL(this.p_size);

		this.keep = true;
		while(this.keep) {
			line = this.input.readLine();

			/* line is null if calc over network and we lost the connection */
			if(line == null) {
				this.write(this.localOutput, "Connection lost.");
				break;
			}

			/* if !null then we have a file to save in */
			if(this.outputFile != null) {
				this.write(this.outputFile, line);
			}

			/* print the command if this is a replay of a session */
			if(this.replay) {
				this.write(this.output, line);
			}

			this.write(this.output, this.execCommand(line));
		}
	}

	private void calcoverhttploop() throws IOException {
		String header = "";
		String html = "";
		String line = "";
		String response = "";
		this.server = new ServerSocket(8080);
		this.pile = new PileRPL(this.p_size);

		this.keep = true;
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

			if(!line.equals("FALSE")) {
				html += "<pre>" + this.execCommand(line) + "</pre><br \\>";
			}
			else {
				html += "<pre>" + this.pile.toString() + "</pre>";
			}			
			
			html += "<form method='get' action='http://localhost:8080/'><input type='text' name='cmd' placeholder='command' autofocus /> <input type='submit' value='Submit'></form>";
			
			response += "HTTP/1.1 200 OK\n";
			response += "Content-Type: text/html\n";
			response += "Content-Length: " + html.length() + "\n";
			response += "Connection: close\n";
			response += "\n";
			response += html + "\n";
			response += "\r\n";
			
			this.write(this.output, response);
		}
	}

	public BufferedReader replaysession() throws SessionFile {
		String line = "";
		try {
			this.write(this.localOutput, "In which file is saved the session to replay?");
			line = this.localInput.readLine();
			return new BufferedReader( new InputStreamReader( new FileInputStream(line) ) );
			
		} catch(IOException exception) {
			throw new SessionFile("Unable to read the session file.");
		}
	}

	public void savesession() {
		String line = "";
		try {
			this.write(this.output, "Do you want to save this session in a file ? [y/n] ");

			line = this.input.readLine();
			if(line.equals("y")) {
				this.write(this.output, "Name of the file ? ");
				line = input.readLine();
				this.outputFile = new PrintWriter( new FileOutputStream(line) );
			}
		} catch(IOException exception) {
			this.write(this.output, "Error while creating/opening " + line + ", this session will not be saved !");
			this.outputFile = null;
		}
	}

	public void menuloop() {
		String choice = "";
		boolean menuKeep = true;
		while(menuKeep) {
			try {
				this.write(this.localOutput, "Menu:");
				this.write(this.localOutput, "1. Session.");
				this.write(this.localOutput, "2. Replay.");
				this.write(this.localOutput, "3. Network.");
				this.write(this.localOutput, "4. Over HTTP.");
				this.write(this.localOutput, "0. Exit");

				choice = localInput.readLine();
				
				switch(choice) {
					case "0":
						// exit
						this.write(this.localOutput, "bye");
						menuKeep = false;
						break;
					case "clear":
						this.write(this.localOutput, "\033[H\033[2J");
						break;
					case "1":
						// from System.in
						// to System.out
						// and file (if user chose to save the session)
						this.input = localInput;
						this.output = localOutput;
						this.savesession();
						this.calcloop();
						break;
					case "2":
						// from file
						// to System.out
						try  {
							this.output = this.localOutput;
							this.input = this.replaysession();
							this.replay = true;
							this.calcloop();
						} catch(SessionFile exception) {
							this.write(this.localOutput, exception.getMessage());
						}
						break;
					case "3":
						// from network
						// to network
						// and file (if user chose to save the session)
						this.server = new ServerSocket(6371);
						this.write(this.localOutput, "Waiting a connection on port 6371...");
						this.socket = server.accept();
						this.write(this.localOutput, "Someone is connected!");
						this.output = new PrintWriter( socket.getOutputStream() );
						this.input = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
						this.savesession();
						this.calcloop();
						this.socket.close();
						this.server.close();
						break;
					case "4":
						// from network
						// to network
						// in HTTP
						this.write(this.localOutput, "Waiting for connection on port 8080...");
						this.calcoverhttploop();
						break;
					default:
						this.write(this.localOutput, "Unknown command");
						continue;
				}

				this.output = localOutput;
				this.input = localInput;
				this.outputFile = null;
				this.replay = false;

			} catch(IOException exception) {
				System.out.println("Error");
				exception.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		try {
			if(args.length > 0)
				new UIRPL(Integer.parseInt(args[0])).menuloop();
			else
				new UIRPL().menuloop();
		} catch(NumberFormatException exception) {
			System.out.println("Argument isn't a number.");
			System.exit(-1);
		}		
	}	
}
