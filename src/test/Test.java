import org.apache.log4j.Logger;

import com.chat.netty.client.Client;

public class Test {
	private static final Logger logger = Logger.getLogger(Test.class.getName());
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		long start_time = System.currentTimeMillis();
		
		for( int i = 0; i < 10000; ++i) {
			System.out.println ("a"+"b"+"c"+"d"+"e"+"f");
			
		}
		
		long end_time = System.currentTimeMillis();
//		logger.info("system: {}ms", String.valueOf(end_time - start_time));
		
		start_time = System.currentTimeMillis();
		
		for( int i = 0; i < 10000; ++i) {
			System.out.println ("a"+"b"+"c"+"d"+"e"+"f");
			
		}
		
		end_time = System.currentTimeMillis();
		logger.info( end_time - start_time + "ms");

	}

}
