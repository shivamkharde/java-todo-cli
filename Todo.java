import java.io.*;
import java.util.*;
import java.lang.*;
import java.text.*;

public class Todo {
	public static void main(String args[]){
		
		if(args.length!=0){

			// if arg is help then show menu items
			if(args[0].equalsIgnoreCase("help")){
				showMenuItems();
				System.exit(0);//exiting the sysytem if user only puts the help command it'll help the program to speed up
			}

			try {
				// initialize and get file instance
				File todoFile = new File("todo.txt");
				File doneFile = new File("done.txt");

				// if files are not available then create them
				if(!todoFile.exists() && !doneFile.exists()){
					todoFile.createNewFile();
					doneFile.createNewFile();
				}

				// pass file instance in Scanner to scan the file
				Scanner todoFileScanner = new Scanner(todoFile);
				Scanner doneFileScanner= new Scanner(doneFile);

				// pass file instance in BufferedWriter to write to file
				BufferedWriter todoFileWriter = new BufferedWriter(new FileWriter(todoFile,true));
				BufferedWriter doneFileWriter = new BufferedWriter(new FileWriter(doneFile,true));

				// for adding the new todo 
				if(args[0].equals("add")){
					if(args.length==2 && args[0].equals("add") && args[1]!=null && !args[1].isEmpty()){
						addTodo(args[1], todoFileWriter);
					}else{
						System.out.println("Error: Missing todo string. Nothing added!");
					}
				}else if(args[0].equals("ls")){//for listing the todo items
					listTodoItems(todoFileScanner);
				}else if(args[0].equals("del")){

					int del_number = 0;
					if(args.length==2){
						del_number = Integer.parseInt(args[1]);
					}

					if(args.length==2 && del_number<=0){
						System.out.println("Error: todo #"+del_number+" does not exist. Nothing deleted.");
					}else if(args.length == 2){
						deleteTodoItems(del_number,todoFileScanner,todoFileWriter,todoFile);
					}else{
						System.out.println("Error: Missing NUMBER for deleting todo.");
					}
				}else if(args[0].equals("done")){
					int done_number = 0;
					if(args.length==2){
						done_number = Integer.parseInt(args[1]);
					}

					if(args.length==2 && done_number<=0){
						System.out.println("Error: todo #"+done_number+" does not exist.");
					}else if(args.length == 2){
						markTodoAsDone(done_number,todoFileScanner,todoFileWriter,doneFileWriter,todoFile);
					}else{
						System.out.println("Error: Missing NUMBER for marking todo as done.");
					}

				}else if(args[0].equals("report")){
					printReport(todoFileScanner, doneFileScanner);
				}else{
					System.out.println("Please provide the correct parameters");
showMenuItems();
				}

				// 
			} catch (FileNotFoundException e) {
				System.out.println("Write/Read file not found");
			} catch(NoSuchElementException e){
				System.out.println("No data found");
			} catch(IOException e){
				System.out.println("IO Exception");
			}
		}else{
			showMenuItems();
		}
		
	}

	// for printing the menu items
	private static void showMenuItems(){
		System.out.println(
		"Usage :-"+
		"\n$ ./todo add \"todo item\"  # Add a new todo"+
		"\n$ ./todo ls               # Show remaining todos"+
		"\n$ ./todo del NUMBER       # Delete a todo"+
		"\n$ ./todo done NUMBER      # Complete a todo"+
		"\n$ ./todo help             # Show usage"+
		"\n$ ./todo report           # Statistics"
		);
	}

	// for adding a new todo item
	private static void addTodo(String todo,BufferedWriter todoFileWriter)throws IOException{
		todoFileWriter.write(todo);
		todoFileWriter.newLine();
		System.out.println("Added todo: \""+todo+"\"");
		todoFileWriter.close();
	}

	// for listing the todo items
	private static void listTodoItems(Scanner todoFileScanner){
		List<String> todoList = new ArrayList<>();
		while(todoFileScanner.hasNext()){
			String item = todoFileScanner.nextLine();
			todoList.add(item.trim());
		}
		if(todoList.isEmpty()){
			System.out.println("There are no pending todos!");
		}else{
			for(int index = todoList.size()-1;index>=0;index--){
				System.out.print("["+(index+1)+"] "+todoList.get(index)+"\n");
			}
		}
	} 

	//for deleteing the todo item
	private static void deleteTodoItems(int del_number,Scanner todoFileScanner, BufferedWriter todoFileWriter, File todoFile)throws IOException{
		List<String> todoList = new ArrayList<>();
		while(todoFileScanner.hasNext()){
			String item = todoFileScanner.nextLine();
			todoList.add(item.trim());
		}

		if(del_number>todoList.size()){
			System.out.println("Error: todo #"+del_number+" does not exist. Nothing deleted.");
		}else{
			todoList.remove(del_number-1);
			PrintWriter forDeleting = new PrintWriter(todoFile);
			forDeleting.print("");
			forDeleting.close();
			// store remainant todos in file again
			for(int i=0;i<todoList.size();i++){
				todoFileWriter.write(todoList.get(i));
				todoFileWriter.newLine();
			}
			todoFileWriter.close();
			System.out.println("Deleted todo #"+del_number);
		}
	}

	//for marking todo as done
	private static void markTodoAsDone(int done_number,Scanner todoFileScanner,BufferedWriter todoFileWriter,BufferedWriter doneFileWriter,File todoFile) throws IOException{
		List<String> todoList = new ArrayList<>();
		while(todoFileScanner.hasNext()){
			String item = todoFileScanner.nextLine();
			todoList.add(item.trim());
		}
		
		if(done_number>todoList.size()){
			System.out.println("Error: todo #"+done_number+" does not exist.");
		}else{
			String todoItemToMarkDone = todoList.get(done_number-1);
			todoList.remove(done_number-1);
			// empty the file and write the todos once again
			PrintWriter forDeleting = new PrintWriter(todoFile);
			forDeleting.print("");
			forDeleting.close();
			// store remainant todos in file again
			for(int i=0;i<todoList.size();i++){
				todoFileWriter.write(todoList.get(i));
				todoFileWriter.newLine();
			}
			todoFileWriter.close();

			// mark todo as done
			DateFormat todoDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date doneDate = new Date();

			String doneString = "X "+todoDateFormat.format(doneDate)+" "+todoItemToMarkDone;
			doneFileWriter.write(doneString);
			doneFileWriter.newLine();
			doneFileWriter.close();

			System.out.println("Marked todo #"+done_number+" as done.");

		}

	}

	// for printing the report
	private static void printReport(Scanner todoFileScanner,Scanner doneFileScanner){

		// for pending todos
		int pendingTodoListCount = 0;
		while(todoFileScanner.hasNext()){
			todoFileScanner.nextLine();
			pendingTodoListCount++;
		}

		// for done todos
		int doneTodoListCount = 0;
		while(doneFileScanner.hasNext()){
			doneFileScanner.nextLine();
			doneTodoListCount++;
		}

		// showing report
		DateFormat todoDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date reportDate = new Date();

		String reportString = ""+todoDateFormat.format(reportDate)+" Pending : "+pendingTodoListCount+" Completed : "+doneTodoListCount;

		System.out.println(reportString);
		
	}
}
