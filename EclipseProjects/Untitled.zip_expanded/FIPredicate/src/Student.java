import java.util.function.Function;

class Student {
	public int age;
	public String gender;
	public Student(int age,String gender){
		this.age = age;
		this.gender = gender;
	}
	public  String customShow(Function<Student,String> fun){
		return fun.apply(this);
	}
}
