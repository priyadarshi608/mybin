class StringReverse {
	public String reverse(String str) {
		System.out.println(str);
		if ((str==null)||(str.length() <= 1) )
			return str;
		return reverse(str.substring(1)) + str.charAt(0);
	}
	public static void main(String[] args) {
		StringReverse obj=new StringReverse();
		String str = "Quora";
		System.out.println("Reverse of \'"+str+"\' is \'"+obj.reverse(str)+"\'");    
	}    
}