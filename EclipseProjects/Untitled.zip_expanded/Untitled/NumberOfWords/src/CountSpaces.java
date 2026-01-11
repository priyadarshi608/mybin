public class CountSpaces {
	
	static int countSpacesIterative(String line) {
		int countBlank = 0;
		for(int i=0; i< line.length(); i++) {
		    if(Character.isWhitespace(line.charAt(i))) {
		        countBlank++;
		    }
		}
		return countBlank;
	}
	
	/* Returns the count of white spaces in a string*/
	static int countSpacesRecursive(String line){
		if (line.length() == 0)
			return 0;
		else
			return (((line.charAt(0) == ' ') ? 1 : 0) + countSpacesRecursive(line.substring(1)));
	}
	
	public static void main(String[] args) {
		String str = "naote saoteh saotnhsnntaohe stnohe";
		if (str == "" || str == null)
			System.out.println(0);
		else
			//System.out.println(countSpacesRecursive(str) + 1);
			System.out.println(countSpacesIterative(str) + 1);
	}    
}
