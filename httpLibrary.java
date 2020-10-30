
public class httpLibrary {

 private final static String help_general_text = "httpc is a curl-like application but supports HTTP protocol only."
 + "\n\n" + "Usage:" + "\n\t" + "httpc command [arguments]" + "\n" + "The commands are:" + "\n\t"
 + "get\texecutes a HTTP GET request and prints the response." + "\n\t" + "post" + "\t"
 + "executes a HTTP POST request and prints the response." + "\n\t" + "help" + "\t" + "prints this screen."
 + "\n\n" + "Use  for more information about a command.";

private final static String help_get_usage_text = "usage: httpc get [-v] [-h key:value] URL\n\nGet executes a HTTP GET request for a given URL.\n\n\t-v\t\tPrints the detail of the response such as protocol, status, and headers.\n\t-h key:value\tAssociates headers to HTTP Request with the format 'key:value'.";

private final static String help_post_usage_text = "usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL"
 + "\n\n" + "Post executes a HTTP POST request for a given URL with inline data or from file." + "\n\n\t"
 + "-v" + "\t\t" + "Prints the detail of the response such as protocol, status, and headers." + "\n\t"
 + "-h key:value\tAssociates headers to HTTP Request with the format 'key:value'." + "\n\t" + "-d string"
 + "\t" + "Associates an inline data to the body HTTP POST request." + "\n\t"
 + "-f file\t\tAssociates the content of a file to the body HTTP POST request" + "\n\n"
 + "Either [-d] or [-f] can be used but not both.";

private final static String error_get_format = "Get Error";
private final static String error_post_format = "Post Error";
private final static String error_argument_format = "Argument Error";

 public static String readFile(String file_path) throws FileNotFoundException{
		StringBuilder data = new StringBuilder();
		File file = new File(file_path);

			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				data.append(scanner.nextLine());				
			}
			scanner.close();
		
		return data.toString();
 }

}