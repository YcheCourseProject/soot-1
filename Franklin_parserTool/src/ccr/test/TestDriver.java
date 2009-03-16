package ccr.test;

import ccr.app.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class TestDriver {

	public static final int TEST_POOL_SIZE = 20000;
	public static final int TEST_POOL_START_LABEL = -10000;
	public static final String APPLICATION_FOLDER = "src/ccr/app";
	public static final String APPLICATION_PACKAGE = "ccr.app";
	public static final String VERSION_PACKAGE_NAME = "version";

	//2009-02-26: keep all execution records
	public static HashMap traceTable = new HashMap(); 
	
	
	
	public static final String WORK_FOLDER = System.getProperty("user.dir")
			+ File.separator + "src" + File.separator + "ccr" + File.separator
			+ "app" + File.separator;

	
	
	public static Object run(Application app, String testcase) {

		return app.application(testcase);
	}

	public static Object run(String appClassName, String testcase) {
		Object result = null;
		
		try {
			Application app = (Application) Class.forName(appClassName)
					.newInstance();
			result = run(app, testcase);
			
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return result;
	}

	public static String[] getTrace(String appClassName, String testcase) {
		
		//2009-03-13: it is true that multiple running of a test case generates the same outputs.
		if(traceTable.containsKey(testcase)){
			return (String[])traceTable.get(testcase);
		}else{
			Trace.getInstance().initialize();
			run(APPLICATION_PACKAGE + "." + appClassName, testcase);
			String[] records =Trace.getInstance().getTrace();
			traceTable.put(testcase, records);
			return 	records;
		}
		
//		Trace.getInstance().initialize();
//		run(APPLICATION_PACKAGE + "." + appClassName, testcase);
//		String[] records =Trace.getInstance().getTrace();
//		return 	records;
		
	}

	public static double test(String appClassName, Oracle oracle,
			TestSet testSets[]) {

		int detected = 0;
		for (int i = 0; i < testSets.length; i++) {
			boolean equivalent = true;
			for (int j = 0; j < testSets[i].size(); j++) {
				String testcase = testSets[i].get(j);
				if (!run(appClassName, testcase).equals(
						oracle.getOutcome(testcase))) {
					equivalent = false;
					break;
				}
			}
			if (!equivalent) {
				detected = detected + 1;
			}
		}
		return (double) detected / (double) testSets.length;
	}

	// 2009-1-6:for context intensity
	public static double test(String appClassName, String oracleClassName,
			TestSet testSets[]) {

		int detected = 0;
		for (int i = 0; i < testSets.length; i++) {
			boolean equivalent = true;
			for (int j = 0; j < testSets[i].size(); j++) {
				String testcase = testSets[i].get(j); // we need the pass/fail
														// information for each
														// test case.
				if (!run(appClassName, testcase).equals(
						run(oracleClassName, testcase))) {
					equivalent = false;
					break;
				}
			}
			if (!equivalent) {
				detected = detected + 1;
			}
		}
		double result = (double) detected / (double) testSets.length;
		return result;
	}

	// 2009-1-6:for context intensity
	public static String test(String appClassName, String oracleClassName,
			String criterion, TestSet testSets[]) {
		StringBuilder sb = new StringBuilder();
		// 2009-2-15:reshape the output

		for (int i = 0; i < testSets.length; i++) {
			int validTestCase = 0;
			TestSet testSet = testSets[i];
			int size_TestSet = testSets[i].size();
			for (int j = 0; j < size_TestSet; j++) {
				String testcase = testSets[i].get(j);
				ApplicationResult result = (ApplicationResult) run(
						appClassName, testcase);
				if (!result.equals(run(oracleClassName, testcase)))
					validTestCase++;
			}
			String line = appClassName.substring(appClassName.indexOf("_")
					+ "_".length())
					+ "\t"
					+ testSet.index
					+ "\t"
					+ size_TestSet
					+ "\t"
					+ validTestCase
					+ "\t"
					+ (double) validTestCase
					/ (double) size_TestSet + "\t";

			if (validTestCase > 0)
				line += "1" + "\t";
			else
				line += "0" + "\t";
			line += "1" + "\n";
			System.out.print(line);
			sb.append(line);
		}

		return sb.toString();

	}

	public static void test(String versionPackageName, String oracleClassName,
			TestSet testSets[], String reportFile) {

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(reportFile));
			String versionFolder = APPLICATION_FOLDER + "/"
					+ versionPackageName;
			File versions = new File(versionFolder);
			for (int i = 0; i < versions.list().length; i++) {
				String appClassName = versions.list()[i];
				appClassName = APPLICATION_PACKAGE
						+ "."
						+ versionPackageName
						+ "."
						+ appClassName.substring(0, appClassName
								.indexOf(".java"));
				String line = "Testing "
						+ appClassName
						+ " Fault detection rate\t"
						+ test(appClassName, APPLICATION_PACKAGE + "."
								+ oracleClassName, testSets);
				bw.write(line);
				bw.newLine();
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public static void test(String versionPackageName, String oracleClassName,
			TestSet testSets[][], String reportFile) {

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(reportFile));
			String versionFolder = APPLICATION_FOLDER + "/"
					+ versionPackageName;
			File versions = new File(versionFolder);
			int versionCounter = 0;

			// 2009-2-15
			StringBuilder sb = new StringBuilder();
			sb.append("FaultyVersion" + "\t" + "TestSet" + "\t" + "#TestCase"
					+ "\t" + "#ValidTestCase" + "\t" + "%ValidTestCase" + "\t"
					+ "\t" + "#ValidTestSet" + "\t" + "#TestSet" + "\n");

			for (int i = 0; i <versions.listFiles().length; i++) {
				if (versions.listFiles()[i].isFile()) {
					versionCounter++;
					String appClassName = versions.list()[i];
					appClassName = APPLICATION_PACKAGE
							+ "."
							+ versionPackageName
							+ "."
							+ appClassName.substring(0, appClassName
									.indexOf(".java"));

					// 2009-2-15: re-generate the forms of outputs
					for (int j = 0; j < testSets.length; j++) { // the length is
																// 50
						long startTime = System.currentTimeMillis();
						// 2009-2-15:context intensity
						String criteria = new File(reportFile).getPath();
						criteria = criteria.substring(0, criteria.indexOf("."));
						sb.append(test(appClassName, APPLICATION_PACKAGE + "."
								+ oracleClassName, criteria, testSets[j]));
					}

				}
			}
			bw.write(sb.toString());
			bw.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/**2009-03-05: To speed up the testing process, we did not run the test case on the faulty version 
	 * any more, we just retrieve the P/F information from execHistory which is generated when examining 
	 * failure rates of faults.
	 * @param testSets
	 * @param faultList
	 * @param execHistory: faultVersion->(testcase ->P/F)+
	 * @param reportFile
	 */
	public static void test_load(TestSet ts_Set[][], ArrayList faultList, HashMap execHistory, String reportFile){
		StringBuilder sb = new StringBuilder();
		sb.append("FaultyVersion" + "\t" + "TestSet" + "\t" + "#TestCase"
				+ "\t" + "#ValidTestCase" + "\t" + "%ValidTestCase" + "\t"
				+ "\t" + "#ValidTestSet" + "\t" + "#TestSet" + "\n");
		
		for(int k = 0 ; k < faultList.size(); k ++){
			String fault = (String)faultList.get(k); //for each faulty version
			for(int t = 0; t < ts_Set.length; t ++){ // for each testing criterion
				TestSet[] testSets = ts_Set[t];	
				
				for (int i = 0; i < testSets.length; i++) { //for each test set
					int validTestCase = 0;
					TestSet testSet = testSets[i];
					int size_TestSet = testSets[i].size();
					for (int j = 0; j < size_TestSet; j++) { // for each test case
						String testcase = testSet.get(j);
						
						HashMap tc_PFs = (HashMap)execHistory.get(fault);
						
						String POrF = (String)tc_PFs.get(testcase);
						if(POrF.equals("F")){
							validTestCase ++;
						}
					}
					String line = fault
							+ "\t"
							+ testSet.index
							+ "\t"
							+ size_TestSet
							+ "\t"
							+ validTestCase
							+ "\t"
							+ (double) validTestCase
							/ (double) size_TestSet + "\t";

					if (validTestCase > 0)
						line += "1" + "\t";
					else
						line += "0" + "\t";
					line += "1" + "\n";
					System.out.print(line);
					sb.append(line);
			}
		}
		}
		Logger.getInstance().setPath(reportFile, false);
		Logger.getInstance().write(sb.toString());
		Logger.getInstance().close();
	}
	
	/** 2009-03-05: test faults listed in a fault list
	 * 
	 * @param versionPackageName
	 * @param oracleClassName
	 * @param testSets
	 * @param reportFile
	 * @param faultList
	 */
	public static void test(String versionPackageName, String oracleClassName, 
			TestSet testSets[][], String reportFile, ArrayList faultList){

		try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(reportFile));

				String versionFolder = APPLICATION_FOLDER + "/"
						+ versionPackageName;
				File versions = new File(versionFolder);
				int versionCounter = 0;

				// 2009-2-15
				StringBuilder sb = new StringBuilder();
				sb.append("FaultyVersion" + "\t" + "TestSet" + "\t" + "#TestCase"
						+ "\t" + "#ValidTestCase" + "\t" + "%ValidTestCase" + "\t"
						+ "\t" + "#ValidTestSet" + "\t" + "#TestSet" + "\n");

				for (int i = 0; i <versions.listFiles().length; i++) {				
					if (versions.listFiles()[i].isFile()) {
						versionCounter++;
						String appClassName = versions.list()[i];
						appClassName = APPLICATION_PACKAGE
								+ "."
								+ versionPackageName
								+ "."
								+ appClassName.substring(0, appClassName
										.indexOf(".java"));
						String faultyVersion =appClassName.substring(appClassName.indexOf("_")+"_".length()); 
						if(!faultList.contains(faultyVersion))
							continue;
						

						// 2009-2-15: re-generate the forms of outputs
						for (int j = 0; j < testSets.length; j++) { // the length is
																	// 50
							long startTime = System.currentTimeMillis();
							// 2009-2-15:context intensity
							String criteria = new File(reportFile).getPath();
							criteria = criteria.substring(0, criteria.indexOf("."));
							sb.append(test(appClassName, APPLICATION_PACKAGE + "."
									+ oracleClassName, criteria, testSets[j]));
						}
					}
				}
				bw.write(sb.toString());
				bw.close();
			} catch (IOException e) {
				System.out.println(e);
			}
	}
	
	//2009-1-6: for context-intensity
	//2009-2-22: for concurrent purpose
	public static void test(String versionPackageName, String oracleClassName,
			TestSet testSets[][], String reportFile, int startVersion, int endVersion ) {

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(reportFile));
			String versionFolder = APPLICATION_FOLDER + "/"
					+ versionPackageName;
			File versions = new File(versionFolder);
			int versionCounter = 0;

			// 2009-2-15
			StringBuilder sb = new StringBuilder();
			sb.append("FaultyVersion" + "\t" + "TestSet" + "\t" + "#TestCase"
					+ "\t" + "#ValidTestCase" + "\t" + "%ValidTestCase" + "\t"
					+ "\t" + "#ValidTestSet" + "\t" + "#TestSet" + "\n");

			for (int i = 0; i <versions.listFiles().length; i++) {				
				if (versions.listFiles()[i].isFile()) {
					versionCounter++;
					String appClassName = versions.list()[i];
					appClassName = APPLICATION_PACKAGE
							+ "."
							+ versionPackageName
							+ "."
							+ appClassName.substring(0, appClassName
									.indexOf(".java"));
					int faultyVersion =Integer.parseInt(appClassName.substring(appClassName.indexOf("_")+"_".length())); 
					if(faultyVersion< startVersion || faultyVersion >= endVersion)
						continue;

					// 2009-2-15: re-generate the forms of outputs
					for (int j = 0; j < testSets.length; j++) { // the length is
																// 50
						long startTime = System.currentTimeMillis();
						// 2009-2-15:context intensity
						String criteria = new File(reportFile).getPath();
						criteria = criteria.substring(0, criteria.indexOf("."));
						sb.append(test(appClassName, APPLICATION_PACKAGE + "."
								+ oracleClassName, criteria, testSets[j]));
					}

				}
			}
			bw.write(sb.toString());
			bw.close();

		} catch (IOException e) {
			System.out.println(e);
		}
	}

	//2009-2-16: 
	public static void getFailureRate(String versionPackageName, String oracleClassName, TestSet testpool, String reportDir){
		try {
			Oracle oracle = new Oracle(APPLICATION_PACKAGE + "."
					+ oracleClassName, testpool);
			String versionFolder = APPLICATION_FOLDER + "/"
					+ versionPackageName;
			File versions = new File(versionFolder);
			
			StringBuilder sb = new StringBuilder();
			sb.append("FaultyVersion" + "\t" + "TestCase" +"\t"+ "PorF" + "CI"+"\n");
			
			HashMap failureRate = new HashMap(); //keep all valid test cases for a specified fault
			for(int i = 0 ; i < versions.list().length; i ++){				
				if((versions.listFiles()[i]).isFile()){
					String appClassName = versions.list()[i];
					appClassName = APPLICATION_PACKAGE
							+ "."
							+ versionPackageName
							+ "."
							+ appClassName.substring(0, appClassName
									.indexOf(".java"));
					//2009-2-17: re-form the appClassName to delete any redundant characters
					failureRate.put(appClassName.substring(appClassName.indexOf("_") + "_".length()), new ArrayList());	
				}
			}
			
			HashMap validTestCase = new HashMap(); //keep all exposed faults for a specified test case
			for(int i = 0; i < testpool.size(); i ++){
				validTestCase.put(testpool.get(i), new ArrayList());
			}
			
			long startTime = System.currentTimeMillis();
			for (int i = 0; i < versions.list().length; i++) {
				//for each faulty version
				if (versions.listFiles()[i].isFile()) {
					String appClassName = versions.list()[i];
					appClassName = APPLICATION_PACKAGE
							+ "."
							+ versionPackageName
							+ "."
							+ appClassName.substring(0, appClassName
									.indexOf(".java"));
					int detected = 0;
					System.out.println("Start version:" + appClassName.substring(appClassName.indexOf("_")+"_".length()));
					for (int j = 0; j < testpool.size(); j++) {
						long startTime1 = System.currentTimeMillis();
						ApplicationResult result = (ApplicationResult) run(
								appClassName, testpool.get(j));
						long last = System.currentTimeMillis() - startTime1;
						sb.append(appClassName.substring(appClassName.indexOf("_")+"_".length()) + "\t" + testpool.get(j) + "\t");
						if (!result.equals(oracle.getOutcome(testpool.get(j)))) {
							detected = detected + 1;
							String faultName = appClassName.substring(appClassName.indexOf("_")+"_".length());
							((ArrayList)failureRate.get(faultName)).add(testpool.get(j));
							((ArrayList)validTestCase.get(testpool.get(j))).add(appClassName.substring(appClassName.indexOf("_")+"_".length()));
							sb.append("F"+"\t"+((TestCase)Adequacy.testCases.get(testpool.get(j))).CI +"\n");
						} else
							sb.append("P"+"\t"+((TestCase)Adequacy.testCases.get(testpool.get(j))).CI +"\n");
					}
				}
			}
			
			//detailed result
			BufferedWriter bw = new BufferedWriter(new FileWriter(reportDir + "/detailed.txt"));
			bw.write(sb.toString());
			bw.close();
			
			//failure rate of faulty version
			bw = new BufferedWriter(new FileWriter(reportDir + "/failureRate.txt"));
			Iterator ite = failureRate.keySet().iterator();
			StringBuilder temp = new StringBuilder();
			temp.append("FaultyVersion" + "\t" + "FailureRate" + "\t" + "Avg.CI.ValidTestCase" + "\n");
			while(ite.hasNext()){ //for each faulty version
				String faultyVersion = (String)ite.next();
				ArrayList validTestCases = (ArrayList)failureRate.get(faultyVersion);
				//get faulty version and failure rate
				temp.append(faultyVersion + "\t" + (double)validTestCases.size()/(double)testpool.size() + "\t");
				
				//get Avg.CI of validTestCase
				if(validTestCases.size() > 0){
					//the failure rate of faults is not 0
					TestSet ts = new TestSet(); 
					for(int i = 0; i < validTestCases.size(); i ++){
						ts.add(validTestCases.get(i)+"");
					}
					temp.append(Adequacy.getAverageCI(ts)+"\n");
				}else{
					temp.append("0.0" + "\n");
				}
				
			}
			bw.write(temp.toString());
			bw.flush();
			bw.close();
			
			//valid test cases exposed faults
			bw = new BufferedWriter(new FileWriter(reportDir + "/validTestCases.txt"));
			ite = validTestCase.keySet().iterator();
			StringBuilder tmp = new StringBuilder();
			tmp.append("TestCase" + "\t" + "#ExposedFault" + "\t" + "CI" + "\t" + "Avg.FailureRate" + "\n");
			while(ite.hasNext()){ //for each faulty version
				String testcase = (String)ite.next();
				ArrayList exposedFault = (ArrayList)validTestCase.get(testcase);
				//get test case and exposed faults
				tmp.append(testcase + "\t" + exposedFault.size() + "\t");
				
				//get CI of test case
				tmp.append(((TestCase)Adequacy.testCases.get(testcase)).CI + "\t");
				
				//get Avg.failure rate of exposed faults
				double sum_failureRate = 0.0;
				if(exposedFault.size() > 0){
					for(int i = 0; i < exposedFault.size(); i ++){
						String fault = (String)exposedFault.get(i);
						sum_failureRate += (double)((ArrayList)failureRate.get(fault)).size()/(double)testpool.size();
					}
					tmp.append(sum_failureRate/(double)exposedFault.size()+"\n");	
				}else{
					tmp.append("0.0"+"\n");
				}
			}
			bw.write(tmp.toString());
			bw.flush();
			bw.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	/**2009-03-16: fault similarity is measured by the percentage of shared test cases
	 * 
	 * @param srcDir
	 * @param containHeader
	 */
	public static void getFaultSimilarity(String srcDir, boolean containHeader_faultList, boolean containHeader_executionFile
			, double threshHold){
		StringBuilder sb = new StringBuilder();
		
		HashMap fault_validTestCases = new HashMap();
		HashMap faultPair_equivalent = new HashMap();
		
		ArrayList faults = new ArrayList();
		String faultList = srcDir + "/FaultList.txt";
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(faultList));
			String fault = null;
			
			if(containHeader_faultList)
				br.readLine();
			
			while((fault = br.readLine())!= null){
				faults.add(fault);
				fault_validTestCases.put(fault, new ArrayList());
				faultPair_equivalent.put(fault, new ArrayList());
			}
			
			br.close();
			String executionFile = srcDir +"/detailed.txt";
			br = new BufferedReader(new FileReader(executionFile));
			String line = null;
			if(containHeader_executionFile)
				br.readLine();
			
			while((line=br.readLine())!=null){
				String[] strs = line.split("\t");
				fault = strs[0];
				if(!fault_validTestCases.containsKey(fault))
					continue;
				String testcase = strs[1];
				String PorF = strs[2];
				if(PorF.equals("F")){
					ArrayList validTestCases = (ArrayList)(fault_validTestCases.get(fault));
					validTestCases.add(testcase);
					fault_validTestCases.put(fault, validTestCases);
				}
			}
			br.close();
			
			//1.header: first line: the size of valid test cases; second line: fault number
			sb.append("\t");
			StringBuilder sb_temp = new StringBuilder();
			sb_temp.append("\t");
			for(int i = 0; i < faults.size(); i ++){
				fault = (String)faults.get(i);
				int validTC_size = ((ArrayList)(fault_validTestCases.get(fault))).size();
				sb.append(validTC_size + "\t");
				sb_temp.append(fault + "\t");
			}
			sb.append("\n");
			sb.append(sb_temp.toString() + "\n");
			
			//2.compare the similarity
			StringBuilder sb_sum = new StringBuilder();
			for(int i = 0; i < faults.size(); i ++){
				fault = (String)faults.get(i);
				sb.append(fault + "\t");
				for(int j = 0; j < faults.size(); j ++){
					String fault_temp = (String)faults.get(j);
					ArrayList testSet_src = (ArrayList)fault_validTestCases.get(fault);
					ArrayList testSet_dest = (ArrayList)fault_validTestCases.get(fault_temp);
					
					ArrayList sharedTC = getSharedTestCases(testSet_src, testSet_dest);
					
					sb.append(sharedTC.size() + "\t");
					
					//judge whether they are equivalent faults
					double rate_1 = (double)sharedTC.size()/(double)testSet_src.size();
					double rate_2 = (double)sharedTC.size()/(double)testSet_dest.size();
					
					
					if(rate_1 >= rate_2 && rate_1 >= threshHold && !fault.equals(fault_temp)){
						if(faultPair_equivalent.containsKey(fault)){
							ArrayList equivalent = (ArrayList)faultPair_equivalent.get(fault);
							if(equivalent.contains(fault_temp)){ //this pair has been recorded
								continue;
							}else{
								equivalent.add(fault_temp);
								faultPair_equivalent.put(fault, equivalent);
							}
						}
						if(faultPair_equivalent.containsKey(fault_temp)){
							ArrayList equivalent = (ArrayList)faultPair_equivalent.get(fault_temp);
							if(equivalent.contains(fault)){ //this pair has been recorded
								continue;
							}else{
								equivalent.add(fault);
								faultPair_equivalent.put(fault_temp, equivalent);
							}
						}
						
						sb_sum.append(fault + "\t" + testSet_src.size() + "\t" + fault_temp + "\t"
								+testSet_dest.size() + "\t" + sharedTC.size() + "\t" + rate_1 + "\n");
						
						
						
					}else if(rate_1 < rate_2 && rate_2 >= threshHold && !fault.equals(fault_temp)){
						if(faultPair_equivalent.containsKey(fault)){
							ArrayList equivalent = (ArrayList)faultPair_equivalent.get(fault);
							if(equivalent.contains(fault_temp)){ //this pair has been recorded
								continue;
							}else{
								equivalent.add(fault_temp);
								faultPair_equivalent.put(fault, equivalent);
							}
						}
						if(faultPair_equivalent.containsKey(fault_temp)){
							ArrayList equivalent = (ArrayList)faultPair_equivalent.get(fault_temp);
							if(equivalent.contains(fault)){ //this pair has been recorded
								continue;
							}else{
								equivalent.add(fault);
								faultPair_equivalent.put(fault_temp, equivalent);
							}
						}
						
						sb_sum.append(fault + "\t" + testSet_src.size() + "\t" + fault_temp + "\t"
								+testSet_dest.size() + "\t" + sharedTC.size() + "\t" + rate_2 + "\n");
					}
				}
				sb.append("\n");
			}
			
			sb.append("\nPotential equivalent faults\n");
			sb.append("Fault\t" + "ValidTestCases\t" + "Fault\t" + "ValidTestCases\t" + "SharedTestCases\t" + "Ratio\n");
			sb.append(sb_sum.toString());
			
			Logger.getInstance().setPath(srcDir + "/Fault_Similarity.txt", false);
			Logger.getInstance().write(sb.toString());
			Logger.getInstance().close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static ArrayList getSharedTestCases(ArrayList testSet_src, ArrayList testSet_dest){
		ArrayList sharedTC = new ArrayList();
		
		if(testSet_src.size() < testSet_dest.size()){
			for(int i = 0; i < testSet_src.size(); i ++){
				String testcase = (String)testSet_src.get(i);
				if(testSet_dest.contains(testcase)){
					sharedTC.add(testcase);
				}
			}
		}else{
			for(int i = 0; i < testSet_dest.size(); i ++){
				String testcase = (String)testSet_dest.get(i);
				if(testSet_src.contains(testcase)){
					sharedTC.add(testcase);
				}
			}
		}
		
		return sharedTC;
	}
	
	public static void getFaultDetail(String srcDir,  boolean containHeader){
		try {
			
			
			StringBuilder sb = new StringBuilder();
			sb.append("FaultyVersion\t"  + "FailureRate\t" + "MinCI\t" + "MeanCI\t"+"MaxCI\t" + "SD.CI\t\n");

			
			HashMap fault_validTestCases = new HashMap();
			String faultList = srcDir + "/FaultList.txt";
			BufferedReader br = new BufferedReader(new FileReader(faultList));
			String fault = null;
			while((fault = br.readLine())!= null){
				fault_validTestCases.put(fault, new ArrayList());
			}
			br.close();
			
			String executionFile = srcDir + "/detailed.txt";  
			br = new BufferedReader(new FileReader(executionFile));
			String line = null;
			if(containHeader)
				br.readLine();
			
			while((line = br.readLine())!= null){
				String[] strs = line.split("\t");
				fault = strs[0];
				if(!fault_validTestCases.containsKey(fault)) //non-interested fault
					continue;
				
				String testcase = strs[1];
				String PorF = strs[2];
				if(PorF.equals("F")){
					//failed test case
					ArrayList validTestCases = (ArrayList)(fault_validTestCases.get(fault));
					if(validTestCases == null){
						validTestCases = (ArrayList)(fault_validTestCases.get(""+Integer.parseInt(fault)));
					}
					validTestCases.add(testcase);
					fault_validTestCases.put(fault, validTestCases);
				}
			}
			
			Iterator ite = fault_validTestCases.keySet().iterator();
			while(ite.hasNext()){
				fault = (String)ite.next();
				ArrayList validTCs = (ArrayList)fault_validTestCases.get(fault);
				sb.append(fault + "\t" +ResultAnalyzer.failureRate.get(fault) + "\t" + Adequacy.getStatisticsCI(validTCs) + "\n");
			}
			
			Logger.getInstance().setPath(srcDir + "/Fault_CI_Detail.txt", false);
			Logger.getInstance().write(sb.toString());
			Logger.getInstance().close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	

	public static void main(String argv[]) {
		
		//2009-02-21:
		String instruction = argv[0];
		String date = argv[1];
		
		if(instruction.equals("getFailureRate")){
			String testcaseFile = "src/ccr/experiment/Context-Intensity_backup/TestHarness/20090221/TestPool.txt";
			getFailureRate("testversion", "TestCFG2", Adequacy.getTestPool(testcaseFile, true),
					"src/ccr/experiment/Context-Intensity_backup/TestHarness/20090221/");	
		}else if(instruction.equals("getFaultCI")){
			String srcDir = "src/ccr/experiment/Context-Intensity_backup/TestHarness/"+date+"/";
			String failureRateFile = srcDir + "failureRate.txt";
			boolean containHeader = true;
			ResultAnalyzer.loadFailureRate(failureRateFile, containHeader);
			
			String testcaseFile = "src/ccr/experiment/Context-Intensity_backup/TestHarness/"+date+"/TestPool.txt";
			
			//2009-02-18: load CI of each test case from a file
			containHeader = true;
			Adequacy.getTestPool(testcaseFile, containHeader);
			

			TestDriver.getFaultDetail(srcDir, containHeader);
		}else if(instruction.equals("getFaultSimilarity")){
			String srcDir = "src/ccr/experiment/Context-Intensity_backup/TestHarness/"+date+"/";
//			String failureRateFile = srcDir + "failureRate.txt";
			boolean containHeader_faultList = false;
			boolean containHeader_executionFile = true;
			double threshHold = Double.parseDouble(argv[2]);
			TestDriver.getFaultSimilarity(srcDir, containHeader_faultList, containHeader_executionFile, threshHold);
			
//			ResultAnalyzer.loadFailureRate(failureRateFile, containHeader);
			
//			String testcaseFile = "src/ccr/experiment/Context-Intensity_backup/TestHarness/"+date+"/TestPool.txt";
			
			//2009-02-18: load CI of each test case from a file
//			containHeader = true;
//			Adequacy.getTestPool(testcaseFile, containHeader);
			

//			TestDriver.getFaultDetail(srcDir, containHeader);
		}
		
	}

}
