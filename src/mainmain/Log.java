package mainmain;

public class Log {

	public Log() {
		// TODO Auto-generated constructor stub
	}
	/** sysout 출력하기 전에 이 매소드를 거쳐가면 텍스트 파일로 로그를 찍게 된다. 아직 저장은 미구현이라 sysout과 동일한 기능만을 제공. */
	public int print(Object string){
        try{
        	System.out.print(string);
        }catch(Exception e){
            System.out.println("로그를 저장할 수 없음: "+e);
        }
        return 0;
    }
	/** sysout 출력하기 전에 이 매소드를 거쳐가면 텍스트 파일로 로그를 찍게 된다. 아직 저장은 미구현이라 sysout과 동일한 기능만을 제공. */
	public int println(String string){
        try{
        	System.out.println(string);
        }catch(Exception e){
            System.out.println("로그를 저장할 수 없음: "+e);
        }
        return 0;
    }
	/** sysout 출력하기 전에 이 매소드를 거쳐가면 텍스트 파일로 로그를 찍게 된다. 아직 저장은 미구현이라 sysout과 동일한 기능만을 제공. */
	public int println(Exception e2){
        try{
        	System.out.println(e2);
        }catch(Exception e){
            System.out.println("로그를 저장할 수 없음: "+e);
        }
        return 0;
    }

}
