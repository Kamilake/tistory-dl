package mainmain;

public class Log {

	public Log() {
		// TODO Auto-generated constructor stub
	}
	
	public int print(String string){
        try{
        	System.out.print(string);
        }catch(Exception e){
            System.out.println("로그를 저장할 수 없음: "+e);
        }
        return 0;
    }
	
	public int println(String string){
        try{
        	System.out.println(string);
        }catch(Exception e){
            System.out.println("로그를 저장할 수 없음: "+e);
        }
        return 0;
    }
	
	public int println(Exception e2){
        try{
        	System.out.println(e2);
        }catch(Exception e){
            System.out.println("로그를 저장할 수 없음: "+e);
        }
        return 0;
    }

}
