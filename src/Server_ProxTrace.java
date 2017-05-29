import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.FutureTask;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
//import java.security.MessageDigest;
import org.ejml.ops.MatrixIO;

import org.AMTL_Matrix.*;
import org.AMTL_Matrix.MatrixOps.MatrixOps;
import org.AMTL_Matrix.Norms.Norms;


import org.ejml.data.DenseMatrix64F;


public class Server_ProxTrace {
	
	public static void main(String[] args){
				
		// Read the addresses of possible clients (change)
		ReadAddress reader = new ReadAddress("/home/decs/Desktop/Javaws/DAMTLDP/address.txt");
		
		ArrayList<String> addressList = reader.readAddress();
		HashMap<String, BigInteger> addressSearch = reader.convertHash(addressList);
		
		// index of the client ip address in the address list
		int index = 0;
		
		/* BlasID 0: ejml
		 * BlasID 1: ujmp
		 * BlasID 2: jama
		 * BlasID 3: jblas
		*/
		
		
        int Blas = 0;
		
		//Initialization shared components of the model matrix (change)
		DenseMatrix64F P_load;
		try{
			P_load = MatrixIO.loadCSV("startP");
			System.out.println("Central server side, initial shared component P is loaded!");
		}catch (IOException e1){
			throw new RuntimeException(e1);
		}

		AMTL_Matrix P = new AMTL_Matrix(P_load,Blas);
		//System.out.println("The rank of P is " + Integer.toString(MatrixOps.getRank(P)));
		
		//Initialization S matrix, the matrix contains the gradient w.r.t. p  (change)
		DenseMatrix64F S_load;
		try{
			S_load = MatrixIO.loadCSV("startS");
			System.out.println("Central server side, initial gradient matrix S is loaded!");
		}catch (IOException e1){
			throw new RuntimeException(e1);
		}

		AMTL_Matrix S = new AMTL_Matrix(S_load,Blas);
		
		// Dimension of the model vector
		int dim = P.NumRows;
		System.out.println("Central server side, dimension of the model vector is " + Integer.toString(dim));
		System.out.println("Central server side, number of column of P is " + Integer.toString(P.NumColumns));
		System.out.println("Central server side, number of column of S is " + Integer.toString(S.NumColumns));
		
		// Parameters (change)
		double alpha2 = 0.01; // Step size in central server side
		System.out.println("Central server side, step size is " +  Double.toString(alpha2));

		double mu = 0.001; // regularization parameter in proximal mapping in central server side (change)
		System.out.println("Central server side, regularization parameter in proximal mapping is " +  Double.toString(mu));

		
		
		
		try {
			
			// Creating a socket by binding the port number. Server is ready to listen 
			// from this port.
			int serverPort = 3457; // (change)
			ServerSocket serverSocket = new ServerSocket(serverPort);
			
			System.out.println("****** Get Ready (Starts listening) ******");
			
			while(true){
				
				// accept(): A blocking method call. When 1 client contacts, the method is unblocked 
				// and returns a Socket object to the server to communicate with the client. 
				Socket clientSocket = serverSocket.accept();
				System.out.println("Starts communicating a client.");
				
				try{
					//
					InetAddress address = clientSocket.getInetAddress();
					System.out.println("Current client IP: " + address.getHostAddress());
					//int port_client = clientSocket.getPort(); // Get the port of client
					//System.out.println("Current client port: " + Integer.toString(port_client));
					
					// This index will specify the column of the model matrix server needs to 
					// return.
					index = reader.searchIndex(address.getHostAddress(), addressSearch);

					// If there is a new Client, permission will be denied and it will be terminated.
					if (index == -1){
						System.out.println("New Client!");
					} else {
						System.out.println("Current index: " + index);
					}
				} catch(Exception ex){
					ex.printStackTrace();
				}
				
				
				ServerThread_Trace t = new ServerThread_Trace(clientSocket, dim, index, P, S, alpha2, mu);
				// FutureTask interface takes a callable object. Object ft is used to call the call() 
				// method overridden in ServerThread.
				FutureTask<TwoReturn> ft = new FutureTask<TwoReturn>(t);
				// This invokes the thread where call() method of ServerThread operates.
				new Thread(ft).start();
				// get() is a method of FutureTask and returns the result of 
				// the computations of call() method of ServerThread.
				TwoReturn next = (TwoReturn) ft.get(); // save for next iteration
				P = next.P;
				S = next.S;
				
			}
			
			
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/*public static double Calculate_obj(int iter, AMTL_Matrix Model) throws IOException{
		
		String s0 = new String("/home/inci/Dropbox/AMTL_Java_Inci/AMTL/0.txt");
		String s1 = new String("/home/inci/Dropbox/AMTL_Java_Inci/AMTL/1.txt");
		String s2 = new String("/home/inci/Dropbox/AMTL_Java_Inci/AMTL/2.txt");
		String s3 = new String("/home/inci/Dropbox/AMTL_Java_Inci/AMTL/3.txt");
		
		File f0 = new File(s0);
		File f1 = new File(s1);
		File f2 = new File(s2);
		File f3 = new File(s3);
		
		double[] obj_val0 = new double[iter];
		double[] obj_val1 = new double[iter];
		double[] obj_val2 = new double[iter];
		double[] obj_val3 = new double[iter];
		
		if(f0.exists()){
			FileReader file = new FileReader(s0);
			BufferedReader reader0 = new BufferedReader(file);
			int lines0 = 0;
			while(reader0.readLine() != null) lines0++;
			reader0.close();
			Scanner inputStream0 = null;
			inputStream0 = new Scanner(f0);
			for(int row = 0; row<lines0; row++){
				obj_val0[row] = Double.parseDouble(inputStream0.nextLine());
			}
			
			inputStream0.close();
		}
		
		if(f1.exists()){
			FileReader file = new FileReader(s1);
			BufferedReader reader1 = new BufferedReader(file);
			int lines1 = 0;
			while(reader1.readLine() != null) lines1++;
			reader1.close();
			Scanner inputStream1 = null;
			inputStream1 = new Scanner(f0);
			for(int row = 0; row<lines1; row++){
				obj_val1[row] = Double.parseDouble(inputStream1.nextLine());
			}
			
			inputStream1.close();
		}
		
		if(f2.exists()){
			FileReader file = new FileReader(s2);
			BufferedReader reader2 = new BufferedReader(file);
			int lines2 = 0;
			while(reader2.readLine() != null) lines2++;
			reader2.close();
			Scanner inputStream2 = null;
			inputStream2 = new Scanner(f2);
			for(int row = 0; row<lines2; row++){
				obj_val2[row] = Double.parseDouble(inputStream2.nextLine());
			}
			
			inputStream2.close();
		}
		
		if(f3.exists()){
			FileReader file = new FileReader(s3);
			BufferedReader reader3 = new BufferedReader(file);
			int lines3 = 0;
			while(reader3.readLine() != null) lines3++;
			reader3.close();
			Scanner inputStream3 = null;
			inputStream3 = new Scanner(f3);
			for(int row = 0; row<lines3; row++){
				obj_val3[row] = Double.parseDouble(inputStream3.nextLine());
			}
			
			inputStream3.close();
		}
		
		for(int i = 0; i<iter; i++){
			
		}
		
		//List<Integer> counts = Arrays.asList(count0,count1,count2,count3);
		//int min = Collections.min(counts);
		
		
		
	}*/
	
	
}