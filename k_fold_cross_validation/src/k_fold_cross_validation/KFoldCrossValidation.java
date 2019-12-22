package k_fold_cross_validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Math;
import java.util.Collections;
import java.util.Random;

public class KFoldCrossValidation {

	public static int[][] ratings = new int[100][65];
	public static int[][] ratings2 = new int[100][65];
	static float correct=0;
	static float zeroes=0;
	static final int k_fold = 10;
	static double [] ans = new double[1000];//new double[k_fold];
	static int m=0;

	static float sum;

		public static void main(String[] args){

			
			//String csvFile = "ratings_of_100_users_withoutHeader.csv";
	        BufferedReader br = null;
	        String line = "";
	        String cvsSplitBy = ",";
			
	        try {
	        	
	        	int i=0,j =0;
	        	String[] rating= null;
	            br = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/src/ratings_of_100_users_withoutHeader.csv"));
	            while ((line = br.readLine()) != null) {

	                // use comma as separator
	                rating = line.split(cvsSplitBy);
	                if (i<100) {
		                if (j<65) {
		                	ratings[i][j] = Integer.parseInt(rating[2]);
		                	j++;
		                }
		                else 
		                {
		                	j=0;
		                	i++;
		                }
	                }
	                //int[][] country2  = Integer.parseInt(line.split(cvsSplitBy)); 
	                //System.out.println("Country [code= " + country[2] + " , name=" + country[3] + "]");

	            }


	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (br != null) {
	                try {
	                    br.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	        
			Random r = new Random();
						
			//1st step: shuffle dataset
			Collections.shuffle(Arrays.asList(ratings));
			
			Scanner scan = new Scanner(System.in);
			System.out.println("Choose k: ");
			int k = scan.nextInt();
			scan.close();
			
			//start counting time when i choose k
			long startTime = System.nanoTime();
			
			int k_fold_index =0;
			
			//if we want 6-fold cross validation then
			//int k_fold = 99;
			
			//how many items X_TEST,Y_TEST will have each time
			int p = ratings.length/k_fold;
			
			
			ArrayList<Integer> user = new ArrayList<Integer>();
			ArrayList<Integer> suser = new ArrayList<Integer>(); //sorted
			ArrayList<Double> sim = new ArrayList<Double>();
			ArrayList<Double> ssim = new ArrayList<Double>(); //sorted 
			
			int [][] y_test = new int[p][ratings[0].length]; //real values of QUERY DATA. test_size, 65
			int [][] X_test = new int[p][ratings[0].length]; //QUERY DATA , test_size, 65
			int i,j;
			
			
			for (i=0;i<ratings.length;i++) {
				for (j=0;j<ratings[0].length;j++) {
					ratings2[i][j] = ratings[i][j];
				}
			}
			

			
			ArrayList<Integer> rndNum = new ArrayList<Integer>();
			
			//generate y_test & X_test k (of k-fold) times
			for (int l=0;l<k_fold;l++) { //k_fold times train_test split 
				//will also have a test dataset
				ratings = ratings2;
				
				//System.out.println(l+1 + "time");
				for (i=0;i<p;i++) { //10 elements (split groupd of data k times)
					for (j=0;j<ratings[0].length;j++) { //for 65 elements in sublists
						if (k_fold_index<ratings.length)
							{
							y_test[i][j] = ratings[k_fold_index][j]; //for any k sublist copy and paste 65 of their elements
							//ratings
							X_test[i][j] = ratings[k_fold_index][j]; //simultaneous copy of y_test
			    		//System.out.println("OLD: " + y_test[i][j]);
							}
					}
					//System.out.println("K FOLD INDEX TILL 99: "+k_fold_index);
					k_fold_index++;
				}
				
				int[] to_remove = new int[p+1];
				int counter =0;
				
				for (i=k_fold_index-p;i<k_fold_index;i++)
				{
					to_remove[counter] = i;
					counter++;
				}
				
				ratings = removeElement(ratings,to_remove,p);
				//System.out.println(X_test.length);
			
			
			int percent50_y_test= 70*y_test[0].length/100;
			
			rndNum.clear();
			for (i=0;i<y_test[0].length;i++)
			{
				rndNum.add(i);
			}
			Collections.shuffle(rndNum);
			
			
			
			for (i=0;i<X_test.length;i++) //for each sublist
			{
				//int times=0;
				for (j=0;j<percent50_y_test;j++) //at 90% of y_test, for each item in a sublist (65)
				{//randomly choose some cells and  them 0
					
					X_test[i][rndNum.get(j)] = 0;///[j]=0;//
					//System.out.println(rndNum.get(j));
				}
			}
			
						
			boolean hamming = false;
			

			//System.out.println(ratings);
			for (int test_i=0;test_i<p ;test_i++) { //for test elements (4)
				for(int train_i=0; train_i<ratings.length; train_i++) {	//for every element of TRAIN DATA

					euclideanDist(ratings,X_test,train_i,test_i, user, sim, ssim);
					//cosineSimilarity(ratings,X_test,train_i,test_i,user,sim,ssim);
					//hamming=true;
					//hammingDist(ratings,X_test,train_i,test_i,user,sim,ssim);
				}
			
			if (!hamming)
				Collections.sort(ssim, Collections.reverseOrder()); 
			else 
				Collections.sort(ssim);
		    
		    int remove = ssim.size() - k;
		    for (i =0; i<remove;i++) {
		    	ssim.remove(ssim.size()-1);
		    }
		    boolean once = true;
		    for (i=0; i<ssim.size(); i++) {
		    	for (j=0; j<sim.size(); j++) {
		    		if(Math.floor(ssim.get(i)*10000) == Math.floor(sim.get(j)*10000) && once && !suser.contains(user.get(j))) { 
		    			once = false;
		    			suser.add(user.get(j)); 
		    		}
		    	}
		    	once = true; 
		    }
		    

	    	ArrayList <Double> recommended = new ArrayList<Double>();
	    	//ArrayList <String> recommended_name = new ArrayList<String>();
	    	double weightedSum = 0; 
		    double similaritySum = 0; 
		    
		    //train set
		    for(i=0; i<ratings[0].length; i++) { 
		    	for(j=0; j<ratings.length; j++) { 
		    		//if(ssim.contains(sim.get(j))) {
		    		if(suser.contains(user.get(j))) { 
		    			if(X_test[test_i][i] == 0) {
		    				weightedSum += ratings[j][i]*sim.get(j); 
		    				similaritySum += sim.get(j);
		    			}
		    		}
		    		
		    	}
		    	if(weightedSum == 0) {
		    		double d = X_test[test_i][i]; 
		    		recommended.add(d);
		    		correct-=1;
		    		
		    		//recommended[i] = d;
		    	}
		    	else { 
			    	weightedSum /= similaritySum;
			    	recommended.add(weightedSum);
			    	//recommended[i] = weightedSum;
		    	}
		    	weightedSum=0;
		    	similaritySum=0;
		    }
		    //System.out.println("correct before accuracy score: "+ correct);
		    correct+= accuracyScore(p,X_test,y_test,recommended);

		    user.clear();
		    sim.clear();
		    suser.clear();
		    ssim.clear();
		    
		 	}
			//System.out.println(correct);
		    //System.out.println(midenika);
			ans[m] = correct/zeroes;
		    System.out.println(String.format("%.2f", ans[m]));
		    m++;
		    //System.out.println(midenika); //prepei na einai 45*10
		    correct=0;
		    zeroes=0;
			}
	        
			sum=0;
			for (i=0;i<k_fold;i++) {
				sum += ans[i];
			}
			System.out.println("Average Accuracy Score: "+ (sum/k_fold)*100+ "%");
			
			long endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			System.out.println("Time in milliseconds: "+timeElapsed/1000000);	
			
		}
		
		public static int accuracyScore(int test_size,int [][] X_test,int [][] y_test,ArrayList <Double> recommended) {
			int i,j;
			
			for (i=0;i<X_test.length;i++)
			{
				//int midenika=0;
				for (j=0;j<X_test[0].length;j++)
				{
					if (X_test[i][j] == 0)
						zeroes++;
				}
				//System.out.println("Gia " +i + " exoume  " + midenika + " midenika");
			}

			//System.out.println(midenika);
			

			int correct=0;
		    for (i=0;i<y_test.length;i++) //sublists 4
		    {
		    	for (j=0;j<y_test[0].length;j++) //content 65
		    	{
		    		if (Math.round(recommended.get(j)) == y_test[i][j] )
		    			correct++;
		    		//System.out.println("Predicted: " + Math.round(recommended.get(j)) + ", True: " + y_test[i][j] ); //provlepsi}
		    		
		    	}
		    	
		    }
		    
		    return correct;
		    //return correct/(X_test[0].length*test_size);
		    
		}

		
		public static void cosineSimilarity(int [][] ratings,int[][] X_test, int train_i, int test_i, ArrayList<Integer> user,ArrayList<Double> sim,ArrayList<Double> ssim)
		{

			double sumProduct = 0;
			double sumASq = 0;
			double sumBSq = 0;
			double similarity;
			
			for(int j=0; j<ratings[0].length; j++) { 
				if(X_test[test_i][j]!=0 && ratings[train_i][j]!=0) { 
					sumProduct += X_test[test_i][j]*ratings[train_i][j];
					sumASq += X_test[test_i][j]*X_test[test_i][j];
					sumBSq += ratings[train_i][j] * ratings[train_i][j];
					}
			}
			if (sumASq == 0 && sumBSq == 0) {
				similarity = 0.0;
			}
			else {
				similarity = sumProduct / (Math.sqrt(sumASq) * Math.sqrt(sumBSq));
				//System.out.println("TEST user: "+test_i+" has similarity to TRAIN user: "+ train_i+" of value "+similarity); //similarity me tous ypoloipous users
			}
			
			
			user.add(train_i);
			sim.add(similarity);
			ssim.add(similarity);
			
		}
		
		public static int[][] removeElement(int[][] ratings, int[] index, int p)
		{
			/*if (ratings == null || index <0 || index >= ratings.length)
				return ratings;*/
			int[][] rat2 = new int[ratings.length-p][ratings[0].length]; //itan ratings.length -1
			
			//System.out.println(ratings[0].length);
			int k=0;
			int counter=0;
			for (int i=0;i<ratings.length;i++) {
				if (i != index[counter])
				{
					for (int j=0;j<ratings[0].length;j++)
						rat2[k][j] = ratings[i][j];
					k++;
				}
				else
					counter++;
			}

			//System.out.println("I: "+rat2.length);
			//System.out.println("J: "+rat2[0].length);
			return rat2;
			
		}
		
		public static void euclideanDist(int [][] ratings,int[][] X_test, int train_i, int test_i, ArrayList<Integer> user,ArrayList<Double> sim,ArrayList<Double> ssim) {
			double sumSquares = 0;
			boolean flag = true;
			for(int j=0; j<ratings[0].length; j++) { //for each location
				if(X_test[test_i][j]!=0 && ratings[train_i][j]!=0) { 
						
						//euclidean
					double diff = 0;
					diff = X_test[test_i][j] - ratings[train_i][j]; 
					sumSquares += diff*diff; //to be eucledian
					flag = true;
				}
			}
			if(flag) { 
				double d = Math.sqrt(sumSquares); //eucledian dist
				double similarity = 1/d;
				if (Double.isInfinite(similarity)) {
					similarity = 1; 
				}
			
				//System.out.println("user: "+test_i+" has similarity to user: "+ train_i+" of value "+similarity);
				user.add(train_i);
				sim.add(similarity);
				ssim.add(similarity);
					
			//return similarity;
			}
		}
		
		public static void hammingDist (int [][] ratings, int[][] X_test, int train_i, int test_i,ArrayList<Integer> user,ArrayList<Double> sim,ArrayList<Double> ssim) {
			
			//num = user to ignore
			double distance = 0.0;
			//boolean flag = true;
			for (int j=0; j <ratings[0].length;j++) 
			{
				//if (train_i != test_i) { 
					//flag = true;
					if (X_test[test_i][j] != ratings[train_i][j])
						distance++;
				}
				/*else
				{
					flag = false;
				}
			}
			if (flag)
			{*/
				//System.out.println("user: "+num+" has similarity to user: "+ i+" of value "+distance); 
				user.add(train_i);
				sim.add(distance);
				ssim.add(distance);
			//}
		}
		
	
}