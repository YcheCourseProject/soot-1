package ccr.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class ResultAnalyzer {
	public static final int TESTPOOL_SIZE = 20000;
	public static HashMap failureRate = new HashMap();
	
	/**2009-10-20: get the Replacement distribution for each testing criterion and rename it. 
	 * criterion + minReplacement + meanReplacement + mediumReplacement + maxReplacement + stdReplacement
	 * 
	 * @param srcDir
	 * @param containHeader
	 * @param criterion
	 * @param rename_criterion
	 * @return
	 */
	public static String getReplacement(String srcDir, boolean containHeader, String criterion, String rename_criterion){
		File[] files = new File(srcDir).listFiles();
		StringBuilder sb = new StringBuilder();
		
		for (File file : files) {
			String fileName = file.getName();
			if (fileName.matches(criterion + "_CI.txt")) {
				String str = null;
				ArrayList Replacement = new ArrayList();
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					if (containHeader)
						br.readLine();

					while ((str = br.readLine()) != null) {
						String[] strs = str.split("\t");
						Replacement.add(Double.parseDouble(strs[5]));
					}
					
					double[] Replacement_array = new double[Replacement.size()];
					for(int i = 0; i < Replacement_array.length; i ++){
						Replacement_array[i] = (Double)Replacement.get(i);
					}
					Arrays.sort(Replacement_array);
					
					double minReplacement = Replacement_array[0];
					double maxReplacement = Replacement_array[Replacement_array.length - 1];

					double sumReplacement = 0.0;
					double meanReplacement = 0.0;
					double stdReplacement = 0.0;
					
					for (int i = 0; i < Replacement_array.length; i++) {
						sumReplacement += Replacement_array[i];
					}
					meanReplacement = sumReplacement / (double) Replacement_array.length;
					
					double tempReplacement = 0.0;
					for (int i = 0; i < Replacement_array.length; i++) {
						double replacement = Replacement_array[i];
						tempReplacement += (replacement - meanReplacement) * (replacement - meanReplacement);
					}
					stdReplacement = Math.sqrt(tempReplacement / (double) Replacement_array.length);

					double mediumReplacement = 0.0;
					if(Replacement_array.length % 2 == 1){//odd number
						mediumReplacement = Replacement_array[(Replacement_array.length + 1)/2 - 1];
					}else{//even number
						mediumReplacement = (Replacement_array[Replacement_array.length/2 - 1] + Replacement_array[Replacement_array.length/2])/(double)2.0;
					}
					
					sb.append(rename_criterion + "\t");
					sb.append(minReplacement + "\t");					
					sb.append(meanReplacement + "\t");
					sb.append(mediumReplacement + "\t");
					sb.append(maxReplacement + "\t");
					sb.append(stdReplacement + "\t" + "\n");

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
	}
	

	/**2009-10-20: get the Activation distribution for each testing criterion and rename it. 
	 * criterion + minActivation + meanActivation + mediumActivation + maxActivation + stdActivation
	 * 
	 * @param srcDir
	 * @param containHeader
	 * @param criterion
	 * @param rename_criterion
	 * @return
	 */
	public static String getActivation(String srcDir, boolean containHeader, String criterion, String rename_criterion){
		File[] files = new File(srcDir).listFiles();
		StringBuilder sb = new StringBuilder();
		
		for (File file : files) {
			String fileName = file.getName();
			if (fileName.matches(criterion + "_CI.txt")) {
				String str = null;
				ArrayList Activations = new ArrayList();
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					if (containHeader)
						br.readLine();

					while ((str = br.readLine()) != null) {
						String[] strs = str.split("\t");
						Activations.add(Double.parseDouble(strs[4]));
					}
					
					double[] Activation_array = new double[Activations.size()];
					for(int i = 0; i < Activation_array.length; i ++){
						Activation_array[i] = (Double)Activations.get(i);
					}
					Arrays.sort(Activation_array);
					
					double minActivation = Activation_array[0];
					double maxActivation = Activation_array[Activation_array.length - 1];

					double sumActivation = 0.0;
					double meanActivation = 0.0;
					double stdActivation = 0.0;
					
					for (int i = 0; i < Activation_array.length; i++) {
						sumActivation += Activation_array[i];
					}
					meanActivation = sumActivation / (double) Activation_array.length;
					
					double tempActivation = 0.0;
					for (int i = 0; i < Activation_array.length; i++) {
						double activation = Activation_array[i];
						tempActivation += (activation - meanActivation) * (activation - meanActivation);
					}
					stdActivation = Math.sqrt(tempActivation / (double) Activation_array.length);

					double mediumActivation = 0.0;
					if(Activation_array.length % 2 == 1){//odd number
						mediumActivation = Activation_array[(Activation_array.length + 1)/2 - 1];
					}else{//even number
						mediumActivation = (Activation_array[Activation_array.length/2 - 1] + Activation_array[Activation_array.length/2])/(double)2.0;
					}
					
					sb.append(rename_criterion + "\t");
					sb.append(minActivation + "\t");					
					sb.append(meanActivation + "\t");
					sb.append(mediumActivation + "\t");
					sb.append(maxActivation + "\t");
					sb.append(stdActivation + "\t" + "\n");

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
	}
	
	/**
	 * 2009-10-20: add medium information of the test set and rename 
	 * the testing criterion
	 * criterion + minCI + meanCI + mediumCI + maxCI + stdCI
	 * 
	 * @param srcDir
	 * @param containHeader
	 * @param criterion
	 * @return
	 */
	public static String getCriteriaCI(String srcDir, boolean containHeader,
			String criterion, String rename_criterion) {
		File[] files = new File(srcDir).listFiles();
		StringBuilder sb = new StringBuilder();
		
		for (File file : files) {
			String fileName = file.getName();
			if (fileName.matches(criterion + "_CI.txt")) {
				String str = null;
				ArrayList CIs = new ArrayList();
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					if (containHeader)
						br.readLine();

					while ((str = br.readLine()) != null) {
						String[] strs = str.split("\t");
						CIs.add(Double.parseDouble(strs[3]));
					}
					
					double[] CI_array = new double[CIs.size()];
					for(int i = 0; i < CI_array.length; i ++){
						CI_array[i] = (Double)CIs.get(i);
					}
					Arrays.sort(CI_array);
					
					double minCI = CI_array[0];
					double maxCI = CI_array[CI_array.length - 1];

					double sumCI = 0.0;
					double meanCI = 0.0;
					double stdCI = 0.0;
					
					for (int i = 0; i < CI_array.length; i++) {
						sumCI += CI_array[i];
					}
					meanCI = sumCI / (double) CI_array.length;
					
					double tempCI = 0.0;
					for (int i = 0; i < CI_array.length; i++) {
						double CI = CI_array[i];
						tempCI += (CI - meanCI) * (CI - meanCI);
					}
					stdCI = Math.sqrt(tempCI / (double) CI_array.length);

					double mediumCI = 0.0;
					if(CI_array.length % 2 == 1){//odd number
						mediumCI = CI_array[(CI_array.length + 1)/2 - 1];
					}else{//even number
						mediumCI = (CI_array[CI_array.length/2 - 1] + CI_array[CI_array.length/2])/(double)2.0;
					}
					
					sb.append(rename_criterion + "\t");
					sb.append(minCI + "\t");					
					sb.append(meanCI + "\t");
					sb.append(mediumCI + "\t");
					sb.append(maxCI + "\t");
					sb.append(stdCI + "\t" + "\n");

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
	}
	/**2009-02-25: get the CI summary information for a given criterion
	 * criterion + minCI + meanCI + maxCI + stdCI
	 * 
	 * @param srcDir
	 * @param containHeader
	 * @param criterion
	 * @param rename_criterion
	 * @return
	 */
	public static String getCriteriaCI(String srcDir, boolean containHeader,
			String criterion) {
		File[] files = new File(srcDir).listFiles();
		StringBuilder sb = new StringBuilder();

		for (File file : files) {
			String fileName = file.getName();
			if (fileName.matches(criterion + "_CI.txt")) {
				double minCI = Double.MAX_VALUE;
				double maxCI = Double.MIN_VALUE;
				double meanCI = 0.0;
				double stdCI = 0.0;

				String str = null;
				double sumCI = 0.0;
				ArrayList CIs = new ArrayList();
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					if (containHeader)
						br.readLine();

					while ((str = br.readLine()) != null) {
						String[] strs = str.split("\t");
						CIs.add(Double.parseDouble(strs[3]));
					}
					for (int i = 0; i < CIs.size(); i++) {
						double CI = (Double) CIs.get(i);

						sumCI += CI;
						if (CI > maxCI)
							maxCI = CI;
						if (CI < minCI)
							minCI = CI;

					}

					meanCI = sumCI / (double) CIs.size();
					double tempCI = 0.0;
					for (int i = 0; i < CIs.size(); i++) {
						double CI = (Double) CIs.get(i);
						tempCI += (CI - meanCI) * (CI - meanCI);
					}
					stdCI = Math.sqrt(tempCI / (double) CIs.size());

					sb.append(criterion + "\t");
					sb.append(minCI + "\t");
					sb.append(meanCI + "\t");
					sb.append(maxCI + "\t");
					sb.append(stdCI + "\t" + "\n");

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
	}
	
	

	/**
	 * 2009-03-31: get the correlation between CI and coveredElements
	 * 
	 * @param srcDir
	 * @param containHeader
	 * @param criterion
	 * @param pattern
	 * @return
	 */
	public static HashMap getCICoveredElements(String srcDir,
			boolean containHeader, String pattern) {

		HashMap CI_CoveredElements = new HashMap();

		File[] files = new File(srcDir).listFiles();

		for (File file : files) {
			String fileName = file.getName();
			if (fileName.matches(pattern)) {
				String[] strs = fileName.split("_");
				String CI = strs[1];

				String str = null;
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					if (containHeader)
						br.readLine();

					str = br.readLine();
					strs = str.split("\t");
					String mean_CoveredElements = strs[4];
					CI_CoveredElements.put(CI, mean_CoveredElements);

					br.close();

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return CI_CoveredElements;
	}

	public static HashMap getCriterialCI(String srcDir, boolean containHeader,
			String criterion, String pattern) {

		HashMap CIRange_CIStatistic = new HashMap();

		File[] files = new File(srcDir).listFiles();

		for (File file : files) {
			String fileName = file.getName();
			if (fileName.matches(pattern)) {
				String[] strs = fileName.split("_");
				String min = strs[1];
				String max = strs[2];
				String CIRange = min + "_" + max;

				double minCI = Double.MAX_VALUE;
				double maxCI = Double.MIN_VALUE;
				double meanCI = 0.0;
				double stdCI = 0.0;

				String str = null;
				double sumCI = 0.0;
				ArrayList CIs = new ArrayList();
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					if (containHeader)
						br.readLine();

					while ((str = br.readLine()) != null) {
						strs = str.split("\t");
						CIs.add(Double.parseDouble(strs[3]));
					}

					for (int i = 0; i < CIs.size(); i++) {
						double CI = (Double) CIs.get(i);

						sumCI += CI;
						if (CI > maxCI)
							maxCI = CI;
						if (CI < minCI)
							minCI = CI;

					}

					meanCI = sumCI / (double) CIs.size();
					double tempCI = 0.0;
					for (int i = 0; i < CIs.size(); i++) {
						double CI = (Double) CIs.get(i);
						tempCI += (CI - meanCI) * (CI - meanCI);
					}
					stdCI = Math.sqrt(tempCI / (double) CIs.size());

					ArrayList CIStatistics = new ArrayList();
					CIStatistics.add(minCI);
					CIStatistics.add(meanCI);
					CIStatistics.add(maxCI);
					CIStatistics.add(stdCI);

					CIRange_CIStatistic.put(CIRange, CIStatistics);

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return CIRange_CIStatistic;
	}

	/**
	 * 2009-03-17: filter faults whose failure rate is below threshHold
	 * 
	 * @param srcFaultFile
	 * @param threshHold
	 * @param saveFile
	 */
	public static ArrayList filterFaults(String srcFaultFile,
			boolean containHeader, double threshHold) {
		ArrayList faults = new ArrayList();
		try {

			BufferedReader br = new BufferedReader(new FileReader(srcFaultFile));
			String line = null;

			if (containHeader)
				br.readLine();

			while ((line = br.readLine()) != null) {
				String[] strs = line.split("\t");
				String fault = strs[0];
				double failureRate = Double.parseDouble(strs[1]);
				if (failureRate <= threshHold) {
					faults.add(fault);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return faults;
	}

	/**
	 * 2009-03-17: filter faults whose failure rate is below threshHold, save
	 * results into a file
	 * 
	 * @param srcFaultFile
	 * @param threshHold
	 * @param saveFile
	 */
	public static ArrayList filterFaults(String srcFaultFile,
			boolean containHeader, double threshHold, String saveFile) {
		ArrayList faults = new ArrayList();
		try {

			BufferedReader br = new BufferedReader(new FileReader(srcFaultFile));
			String line = null;
			StringBuilder sb = new StringBuilder();
			if (containHeader)
				br.readLine();

			while ((line = br.readLine()) != null) {
				String[] strs = line.split("\t");
				String fault = strs[0];
				double failureRate = Double.parseDouble(strs[1]);
				if (failureRate <= threshHold) {
					sb.append(fault + "\t" + failureRate + "\n");
					faults.add(fault);
				}
			}

			Logger.getInstance().setPath(saveFile, false);
			Logger.getInstance().write(sb.toString());
			Logger.getInstance().close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return faults;
	}

	/**
	 * 2009-03-17: a powerful method to merge all files generated distributedly
	 * 
	 * @param srcDir
	 * @param containHeader
	 * @param pattern
	 * @param saveFile
	 */
	public static void mergeFiles(String srcDir, boolean containHeader,
			String pattern, String saveFile) {
		File[] files = new File(srcDir).listFiles();
		StringBuilder sb = new StringBuilder();
		boolean writeHeader = false;
		for (File file : files) {

			String fileName = file.getName();
			if (fileName.matches(pattern)) {
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					String str = null;

					if (containHeader && writeHeader) // write the header only
														// once
						br.readLine();
					else {
						sb.append(br.readLine() + "\n");
						writeHeader = true;
					}

					while ((str = br.readLine()) != null) {
						sb.append(str + "\n");
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		Logger.getInstance().setPath(saveFile, false);
		Logger.getInstance().write(sb.toString());
		Logger.getInstance().close();
	}

	/**
	 * 2009-02-24: due to the concurrent execution, we need to merge those
	 * partial results into a complete one
	 * 
	 * @param srcDir
	 * @param criteria
	 * @param saveFile
	 */
	public static void mergeTestResultFiles(String srcDir, String criteria,
			boolean containHeader, String saveFile) {
		File[] files = new File(srcDir).listFiles();
		StringBuilder sb = new StringBuilder();

		for (File file : files) {

			String fileName = file.getName();
			if (fileName.matches(criteria + "\\_[0-9]+\\_[0-9]+\\.txt")) {
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					String str = null;
					if (containHeader)
						br.readLine();

					while ((str = br.readLine()) != null) {
						sb.append(str + "\n");
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		Logger.getInstance().setPath(saveFile, false);
		Logger.getInstance().write(sb.toString());
		Logger.getInstance().close();
	}

	/**
	 * 2009-02-22: load the failure rate of each fault from the file
	 * 
	 * @param failureRateFile
	 * @param containHeader
	 */
	public static void loadFailureRate(String failureRateFile,
			boolean containHeader) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					failureRateFile));
			if (containHeader)
				br.readLine();

			String str = null;
			while ((str = br.readLine()) != null) {
				String[] strs = str.split("\t");
				String fault = strs[0];
				String failureRate = strs[1];
				ResultAnalyzer.failureRate.put(fault, failureRate);
			}
			br.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 2009-2-18: the result is the a table with faulty version as columns, and
	 * CI of each test case as rows
	 * 
	 * @param detailFile:records
	 *            of executions of each test case in a test pool
	 * @param saveFile:
	 * @param containHeader:whether
	 *            the first row of detailFile is a header or not
	 */
	public static void translateFormat(String detailFile, String saveFile,
			boolean containHeader) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(detailFile));
			String str = null;
			HashMap faults = new HashMap();
			if (containHeader)
				br.readLine();

			while ((str = br.readLine()) != null) {
				String[] strs = str.split("\t");
				if (strs[2].equals("F")) {
					double CI = ((TestCase) Adequacy.testCases.get(strs[1])).CI;
					if (faults.containsKey(strs[0]))
						((ArrayList) faults.get(strs[0])).add(CI);
					else {
						ArrayList temp = new ArrayList();
						temp.add(CI);
						faults.put(strs[0], temp);
					}
				}
			}
			br.close();

			Iterator ite = faults.keySet().iterator();
			StringBuilder sb = new StringBuilder();
			while (ite.hasNext()) {
				String fault = (String) ite.next();
				sb.append(fault.substring(fault.indexOf("_") + "_".length())
						+ "\t");
				ArrayList CIs = (ArrayList) faults.get(fault);
				for (int i = 0; i < CIs.size(); i++) {
					sb.append(CIs.get(i) + "\t");
				}
				sb.append("\n");
			}

			Logger.getInstance().setPath(saveFile, false);
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

	public static ArrayList getFaultWithTestCase(String testDetailFile, boolean containHeader){
		HashMap faults = getFaultsWithTestCase(testDetailFile, containHeader);
		if(faults.size() == 1){
			System.out.println("ResultAnalyzer(getFaultsWithTestCase()):more than one fault");
		}
		return (ArrayList)faults.values().iterator().next();
	}
	
	/**
	 * 2009-02-19: the result is a table with faults(String) as rows and valid test
	 * cases(String) to expose such a fault as columns
	 * 
	 * @param testDetailFile
	 * @param containHeader
	 * @return
	 */
	public static HashMap getFaultsWithTestCase(String testDetailFile,
			boolean containHeader) {
		HashMap faults = new HashMap();

		File tmp = new File(testDetailFile); 
		if(tmp.exists()){//2009-12-31: check the file exists or not
			try {		
				BufferedReader br = new BufferedReader(new FileReader(
						testDetailFile));
				String str = null;
				if (containHeader)
					br.readLine();

				while ((str = br.readLine()) != null) {
					String[] strs = str.split("\t");
					String fault = strs[0].substring(strs[0].indexOf("_")
							+ "_".length());

					if (strs[2].equals("F")) {// if a test case exposes a fault
						ArrayList testCases;
						if (faults.containsKey(fault))
							testCases = (ArrayList) faults.get(fault);
						else
							testCases = new ArrayList();

						String testCase = strs[1];
						if (!testCases.contains(strs[1])) {
							testCases.add(testCase); // add this valid test case
						}
						faults.put(fault, testCases);
					} else { //
						if (!faults.containsKey(fault)) {
							faults.put(fault, new ArrayList());
						}
					}
				}
				br.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return faults;
	}
	
	/**2010-01-01:give a set of mutants in a fault list, get the killed mutant numbers 
	 * of each test case in the test pool
	 * 
	 * @param date_detailed
	 * @param containHeader_detailed
	 * @param faultList
	 * @param containHeader_fault
	 * @param date_save
	 */
	public static void saveToFile_TestPool_faultList(String date_detailed, boolean containHeader_detailed, String faultList, boolean containHeader_fault, String date_save){
		StringBuilder sb = new StringBuilder();
		sb.append("TestCase").append("\t").append("KilledNumber").append("\t").
		append("MutantKillArray").append("\n");
		
		int fault_counter = 0;
		ArrayList faultArray = new ArrayList();
		File tmp = new File(faultList);
		if(tmp.exists()){
			//1. load the fault list
			try {
				BufferedReader br = new BufferedReader(new FileReader(faultList));
				if(containHeader_fault){
					br.readLine();
				}
				
				String str = null;
				while((str = br.readLine())!= null){
					faultArray.add(str);
					fault_counter ++;
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//2. initialize the testcase_faultKilled array
			HashMap fault_effectTS = null;			
			HashMap testcase_faultKilled = new HashMap(); 
			int TEST_POOL_END_LABEL = TestManager.TEST_POOL_SIZE + TestManager.TEST_POOL_START_LABEL;
			for(int i = TestManager.TEST_POOL_START_LABEL; i < TEST_POOL_END_LABEL; i ++){
				testcase_faultKilled.put(""+i, new ArrayList());
			}
			
			//3. for each fault in the fault array
			for(int i = 0 ; i < faultArray.size(); i ++){
				int fault_int = Integer.parseInt((String)faultArray.get(i));
				String fault = "" + fault_int;
				if(fault_int >= 0 && fault_int < 10){
					fault = "0" + fault_int;
				}
				
				String testDetailFile = 
					 "src/ccr/experiment/Context-Intensity_backup/TestHarness/"
						+ date_detailed + "/" + "detailed_" + fault_int + "_" + (fault_int + 1) + ".txt";
				
				fault_effectTS = ResultAnalyzer.getFaultsWithTestCase(testDetailFile, containHeader_detailed);
				if(fault_effectTS.size() == 0){ // 2. This file does not exist means 
					//This fault throws some exceptions or runs out of time,
					//then each test case can kill this fault
					Iterator it_tc = testcase_faultKilled.keySet().iterator();
					String testcase;
					ArrayList killed = null;
					while(it_tc.hasNext()){
						testcase = (String)it_tc.next();
						killed = (ArrayList)testcase_faultKilled.get(testcase);
						killed.add(fault);
						testcase_faultKilled.put(testcase, killed);
					}				
				}else if(fault_effectTS.size() > 0){ 
					ArrayList effectTS = (ArrayList)fault_effectTS.get(fault);
					if(effectTS.size() > 0){//some test cases kill this fault
						String testcase;
						ArrayList killed = null;
						for(int j = 0; j < effectTS.size(); j ++){
							testcase = (String)effectTS.get(j);
							killed = (ArrayList)testcase_faultKilled.get(testcase);
							killed.add(fault);
							testcase_faultKilled.put(testcase, killed);
						}					
					}
				}	
			}
			
			//3. record the information
			for(int i = TestManager.TEST_POOL_START_LABEL; i < TEST_POOL_END_LABEL; i ++){
				String testcase = "" + i;
				ArrayList killed = (ArrayList)testcase_faultKilled.get(testcase);
				sb.append(testcase).append("\t");
				sb.append(killed.size()).append("\t");
//				for(int j = 0; j < killed.size(); j ++){
//					String faultKilled = (String)killed.get(j);
//					sb.append(faultKilled).append(",");
//				}
				sb.append("\n");
			}
			
			String saveFile = "src/ccr/experiment"
				+ "/Context-Intensity_backup/TestHarness/" + date_save + "/TestCaseDetails_" 
				+ fault_counter + ".txt";
			Logger.getInstance().setPath(saveFile, false);
			Logger.getInstance().write(sb.toString());
			Logger.getInstance().close();
			
		}else{
			System.out.println("The fault list:" + faultList + " does not exist");
		}
	}
	
	/**2010-01-01: save both equivalent and non-equivalent faults
	 * 
	 * @param date_faultList
	 * @param containHeader_faultList
	 * @param date_detailed
	 * @param containHeader_detailed
	 * @param date_save
	 */
	public static void saveNonEquivalentFault(String date_faultList, boolean containHeader_faultList, 
			String date_detailed, boolean containHeader_detailed, String date_save){
		String faultList = "src/ccr/experiment/Context-Intensity_backup/TestHarness/" 
			+ date_faultList + "/PotentialEquivalentFaults.txt";
		File faultListFile = new File(faultList);
		
		ArrayList allFaults = new ArrayList();
		
		
		if(faultListFile.exists()){
			
			HashMap failureRate_faultArray = new HashMap();//group faults which share the same failure rate
			HashMap faultPair_equivalent = new HashMap();
			
			try {
				BufferedReader br = new BufferedReader(new FileReader(faultListFile));
				if(containHeader_faultList){
					br.readLine();
				}
				
				//1. classify all faults based on the failure rate
				String str = null;
				while((str = br.readLine())!= null){
					String[] strs = str.split(",");
					String failureRate = strs[1];
					int fault_int = Integer.parseInt(strs[0].substring(
							strs[0].indexOf("_")+"_".length(), strs[0].indexOf(".")));
					
					ArrayList faults = new ArrayList();
					if(failureRate_faultArray.containsKey(failureRate)){
						faults = (ArrayList)failureRate_faultArray.get(failureRate);						
					}
					if(!faults.contains(""+fault_int)){
						faults.add(""+fault_int);	
					}
					
					failureRate_faultArray.put(failureRate, faults);
					allFaults.add(""+fault_int);
				}
				
				//2. derive all equivalent faults
				Iterator it_failureRate = failureRate_faultArray.keySet().iterator();
				while(it_failureRate.hasNext()){
					String failureRate = (String)it_failureRate.next();
					ArrayList faults = (ArrayList)failureRate_faultArray.get(failureRate);
					if(faults.size() > 1){
						//3. dervie all equivalent faults which share the same failure rate
						
						ArrayList fault_effecientTS = new ArrayList();
						String testDetailFile = null;
						//3.1 load all efficent testcases firstly
						for(int i = 0; i < faults.size(); i++){
							String fault = (String)faults.get(i);
							int fault_int = Integer.parseInt(fault);
							testDetailFile = 
								 "src/ccr/experiment/Context-Intensity_backup/TestHarness/"
									+ date_detailed + "/" + "detailed_" + fault_int 
									+ "_" + (fault_int + 1) + ".txt";
							
							fault_effecientTS.add(ResultAnalyzer.getFaultsWithTestCase(
									testDetailFile, containHeader_detailed));							
						}
						//3.2 check all equivalent faults sharing this failure rate
						for(int i = 0; i < fault_effecientTS.size(); i ++){
							//3.2.1 extract the fault and its efficient test cases
							HashMap fault_efficientTCs_src = (HashMap)fault_effecientTS.get(i);
							String fault_src = (String)fault_efficientTCs_src.keySet().iterator().next();
							ArrayList testSet_src = (ArrayList)fault_efficientTCs_src.get(fault_src);
							for(int j = i + 1; j < fault_effecientTS.size(); j ++){
								
								//3.2.2 extract the fault and its efficient test cases
								HashMap fault_efficientTCs_dest = (HashMap)fault_effecientTS.get(j);
								String fault_dest = (String)fault_efficientTCs_dest.keySet().iterator().next();
								ArrayList testSet_dest = (ArrayList)fault_efficientTCs_dest.get(fault_dest);
								
								//3.2.3 check the equivalent relationship
								ArrayList sharedTCs = TestDriver.getSharedTestCases(testSet_src, testSet_dest);
								if(sharedTCs.size() == testSet_src.size() && sharedTCs.size() == testSet_dest.size()){
									
									//3.2.4 save the equivalent faulty versions
									if(faultPair_equivalent.containsKey(fault_src)){
										ArrayList equivalent = (ArrayList)faultPair_equivalent.get(fault_src);
										if(!equivalent.contains(fault_dest)){ 
											equivalent.add(fault_dest);
											faultPair_equivalent.put(fault_src, equivalent);
										}
									}else{
										ArrayList equivalent  = new ArrayList();
										equivalent.add(fault_dest);
										faultPair_equivalent.put(fault_src, equivalent);
									}
									
									if(faultPair_equivalent.containsKey(fault_dest)){
										ArrayList equivalent = (ArrayList)faultPair_equivalent.get(fault_dest);
										if(!equivalent.contains(fault_src)){ //this pair has been recorded
											equivalent.add(fault_src);
											faultPair_equivalent.put(fault_dest, equivalent);
										}
									}else{
										ArrayList equivalent  = new ArrayList();
										equivalent.add(fault_src);
										faultPair_equivalent.put(fault_dest, equivalent);
									}
								}
							}
						}
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			int equivalentCounter = 0;
			StringBuilder sb = new StringBuilder();
			Iterator it_fault = faultPair_equivalent.keySet().iterator();//fault->(equivalentFaults)
			ArrayList reportedFaults = new ArrayList();
			ArrayList equivalentFaults = new ArrayList();
			while(it_fault.hasNext()){
				String fault = (String)it_fault.next();
				
				if(!reportedFaults.contains(fault)){//has never been reported
					reportedFaults.add(fault);
					sb.append(fault).append("\t");
					
					//2010-01-01:for equivalent faults, only receive the first one
					
					
					ArrayList equivalents = (ArrayList)faultPair_equivalent.get(fault);
					for(int i = 0; i < equivalents.size(); i ++){
						String equivalentFault =(String)equivalents.get(i); 
						if(!reportedFaults.contains(equivalentFault)){
							sb.append(equivalentFault).append("\t");
							reportedFaults.add(equivalentFault);
							equivalentCounter ++;
						}
						
						if(!equivalentFaults.contains(equivalentFault)){
							equivalentFaults.add(equivalentFault);
						}
					}
					sb.append("\n");	
				}				
			}
			
			
			String saveFile = "src/ccr/experiment/Context-Intensity_backup/TestHarness/" 
				+ date_save + "/EquivalentFaults.txt";
			Logger.getInstance().setPath(saveFile, false);
			Logger.getInstance().write(sb.toString());
			Logger.getInstance().close();		
			System.out.println("Equivalent mutants:" + equivalentCounter);
			
			sb.setLength(0);
			
			int nonEquivalent_counter = 0;
			ArrayList nonEquivalentFault = new ArrayList();
			for(int i = 0 ; i < allFaults.size(); i ++){
				String fault = (String)allFaults.get(i);
				if(!equivalentFaults.contains(fault)){
					nonEquivalentFault.add(fault);
					nonEquivalent_counter ++;
					sb.append(fault).append("\n");
				}
			}					
			
			saveFile = "src/ccr/experiment/Context-Intensity_backup/TestHarness/" 
				+ date_save + "/NonEquivalentFaults.txt";
			Logger.getInstance().setPath(saveFile, false);
			Logger.getInstance().write(sb.toString());
			Logger.getInstance().close();
			System.out.println("Non-equivalent mutants:" + nonEquivalent_counter);
			
		}else{
			System.out.println("fault list file:" + faultList + " does not exist at all!");
		}
	}
	
	/**2010-01-01: get the equivalent faults who are specified in a fault list
	 * 
	 * @param date_faultList: the directory to load fault list
	 * @param containHeader_faultList:
	 * @param date_detailed: the directory to load the test case execution detailed files
	 * @param containHeader_detailed
	 * @param saveFile
	 */
	public static void saveFaultSimilarity_Strict(String date_faultList, boolean containHeader_faultList, 
			String date_detailed, boolean containHeader_detailed, String date_save){
		String faultList = "src/ccr/experiment/Context-Intensity_backup/TestHarness/" 
			+ date_faultList + "/PotentialEquivalentFaults.txt";
		File faultListFile = new File(faultList);
		
		if(faultListFile.exists()){
			
			HashMap failureRate_faultArray = new HashMap();//group faults which share the same failure rate
			HashMap faultPair_equivalent = new HashMap();
			
			try {
				BufferedReader br = new BufferedReader(new FileReader(faultListFile));
				if(containHeader_faultList){
					br.readLine();
				}
				
				//1. classify all faults based on the failure rate
				String str = null;
				while((str = br.readLine())!= null){
					String[] strs = str.split(",");
					String failureRate = strs[1];
					int fault_int = Integer.parseInt(strs[0].substring(
							strs[0].indexOf("_")+"_".length(), strs[0].indexOf(".")));
					
					ArrayList faults = new ArrayList();
					if(failureRate_faultArray.containsKey(failureRate)){
						faults = (ArrayList)failureRate_faultArray.get(failureRate);						
					}
					if(!faults.contains(""+fault_int)){
						faults.add(""+fault_int);	
					}
					failureRate_faultArray.put(failureRate, faults);					
				}
				
				//2. derive all equivalent faults
				Iterator it_failureRate = failureRate_faultArray.keySet().iterator();
				while(it_failureRate.hasNext()){
					String failureRate = (String)it_failureRate.next();
					ArrayList faults = (ArrayList)failureRate_faultArray.get(failureRate);
					if(faults.size() > 1){
						//3. dervie all equivalent faults which share the same failure rate
						
						ArrayList fault_effecientTS = new ArrayList();
						String testDetailFile = null;
						//3.1 load all efficent testcases firstly
						for(int i = 0; i < faults.size(); i++){
							String fault = (String)faults.get(i);
							int fault_int = Integer.parseInt(fault);
							testDetailFile = 
								 "src/ccr/experiment/Context-Intensity_backup/TestHarness/"
									+ date_detailed + "/" + "detailed_" + fault_int 
									+ "_" + (fault_int + 1) + ".txt";
							
							fault_effecientTS.add(ResultAnalyzer.getFaultsWithTestCase(
									testDetailFile, containHeader_detailed));							
						}
						//3.2 check all equivalent faults sharing this failure rate
						for(int i = 0; i < fault_effecientTS.size(); i ++){
							//3.2.1 extract the fault and its efficient test cases
							HashMap fault_efficientTCs_src = (HashMap)fault_effecientTS.get(i);
							String fault_src = (String)fault_efficientTCs_src.keySet().iterator().next();
							ArrayList testSet_src = (ArrayList)fault_efficientTCs_src.get(fault_src);
							for(int j = i + 1; j < fault_effecientTS.size(); j ++){
								
								//3.2.2 extract the fault and its efficient test cases
								HashMap fault_efficientTCs_dest = (HashMap)fault_effecientTS.get(j);
								String fault_dest = (String)fault_efficientTCs_dest.keySet().iterator().next();
								ArrayList testSet_dest = (ArrayList)fault_efficientTCs_dest.get(fault_dest);
								
								//3.2.3 check the equivalent relationship
								ArrayList sharedTCs = TestDriver.getSharedTestCases(testSet_src, testSet_dest);
								if(sharedTCs.size() == testSet_src.size() && sharedTCs.size() == testSet_dest.size()){
									
									//3.2.4 save the equivalent faulty versions
									if(faultPair_equivalent.containsKey(fault_src)){
										ArrayList equivalent = (ArrayList)faultPair_equivalent.get(fault_src);
										if(!equivalent.contains(fault_dest)){ 
											equivalent.add(fault_dest);
											faultPair_equivalent.put(fault_src, equivalent);
										}
									}else{
										ArrayList equivalent  = new ArrayList();
										equivalent.add(fault_dest);
										faultPair_equivalent.put(fault_src, equivalent);
									}
									
									if(faultPair_equivalent.containsKey(fault_dest)){
										ArrayList equivalent = (ArrayList)faultPair_equivalent.get(fault_dest);
										if(!equivalent.contains(fault_src)){ //this pair has been recorded
											equivalent.add(fault_src);
											faultPair_equivalent.put(fault_dest, equivalent);
										}
									}else{
										ArrayList equivalent  = new ArrayList();
										equivalent.add(fault_src);
										faultPair_equivalent.put(fault_dest, equivalent);
									}
								}
							}
						}
						
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			int equivalentCounter = 0;
			StringBuilder sb = new StringBuilder();
			Iterator it_fault = faultPair_equivalent.keySet().iterator();
			ArrayList reportedFaults = new ArrayList();
			while(it_fault.hasNext()){
				String fault = (String)it_fault.next();
				if(!reportedFaults.contains(fault)){//has never been reported
					reportedFaults.add(fault);
					sb.append(fault).append("\t");
					equivalentCounter ++;
					ArrayList equivalents = (ArrayList)faultPair_equivalent.get(fault);
					for(int i = 0; i < equivalents.size(); i ++){
						String equivalentFault =(String)equivalents.get(i); 
						if(!reportedFaults.contains(equivalentFault)){
							sb.append(equivalentFault).append("\t");
							reportedFaults.add(equivalentFault);
							equivalentCounter ++;
						}
					}
					sb.append("\n");	
				}
				
			}
			
			String saveFile = "src/ccr/experiment/Context-Intensity_backup/TestHarness/" 
				+ date_save + "/EquivalentFaults.txt";
			Logger.getInstance().setPath(saveFile, false);
			Logger.getInstance().write(sb.toString());
			Logger.getInstance().close();		
			System.out.println("Equivalent mutants:" + equivalentCounter);
		}else{
			System.out.println("fault list file:" + faultList + " does not exist at all!");
		}
	}
	
	/**2009-12-31:get the killed mutant numbers of each test case in the test pool
	 * 
	 * @param date_detailed: the directory of detailed files
	 * @param containHeader: whether the associated test detail file contains the Header
	 * @param startVersion: inclusive
	 * @param endVersion: exclusive
	 * @param date_save: directory to save the results
	 */
	public static void saveToFile_TestPool(String date_detailed, boolean containHeader, int startVersion, int endVersion, String date_save){
		StringBuilder sb = new StringBuilder();
		sb.append("TestCase").append("\t").append("KilledNumber").append("\t").
		append("MutantKillArray").append("\n");
		
		HashMap fault_effectTS = null;
		int fault_counter = 0;
		HashMap testcase_faultKilled = new HashMap(); 
		int TEST_POOL_END_LABEL = TestManager.TEST_POOL_SIZE + TestManager.TEST_POOL_START_LABEL;
		for(int i = TestManager.TEST_POOL_START_LABEL; i < TEST_POOL_END_LABEL; i ++){
			testcase_faultKilled.put(""+i, new ArrayList());
		}
		
		for(int i = startVersion; i < endVersion; i ++){
			fault_counter ++;
			String testDetailFile = 
				 "src/ccr/experiment/Context-Intensity_backup/TestHarness/"
					+ date_detailed + "/" + "detailed_" + i + "_" + (i+1) + ".txt";
			
			//1. check each test case to see whether it can kill this fault
			String fault  = ""+ i ; 
			if(i >= 0 && i < 10){
				fault = "0" + i;
			}
			
			fault_effectTS = ResultAnalyzer.getFaultsWithTestCase(testDetailFile, containHeader);
			if(fault_effectTS.size() == 0){ // 2. This file does not exist means 
				//This fault throws some exceptions or runs out of time,
				//then each test case can kill this fault
				Iterator it_tc = testcase_faultKilled.keySet().iterator();
				String testcase;
				ArrayList killed = null;
				while(it_tc.hasNext()){
					testcase = (String)it_tc.next();
					killed = (ArrayList)testcase_faultKilled.get(testcase);
					killed.add(fault);
					testcase_faultKilled.put(testcase, killed);
				}				
			}else if(fault_effectTS.size() > 0){ 
				ArrayList effectTS = (ArrayList)fault_effectTS.get(fault);
				if(effectTS.size() > 0){//some test cases kill this fault
					String testcase;
					ArrayList killed = null;
					for(int j = 0; j < effectTS.size(); j ++){
						testcase = (String)effectTS.get(j);
						killed = (ArrayList)testcase_faultKilled.get(testcase);
						killed.add(fault);
						testcase_faultKilled.put(testcase, killed);
					}					
				}
				//no test case can kill it				
			}									
		}
		
		//3. record the information
		for(int i = TestManager.TEST_POOL_START_LABEL; i < TEST_POOL_END_LABEL; i ++){
			String testcase = "" + i;
			ArrayList killed = (ArrayList)testcase_faultKilled.get(testcase);
			sb.append(testcase).append("\t");
			sb.append(killed.size()).append("\t");
//			for(int j = 0; j < killed.size(); j ++){
//				String faultKilled = (String)killed.get(j);
//				sb.append(faultKilled).append(",");
//			}
			sb.append("\n");
		}
		
		String saveFile = "src/ccr/experiment"
			+ "/Context-Intensity_backup/TestHarness/" + date_save + "/TestCaseDetails_" 
			+ fault_counter + ".txt";
		Logger.getInstance().setPath(saveFile, false);
		Logger.getInstance().write(sb.toString());
		Logger.getInstance().close();
	}

	/**2010-01-03: load the CI of all test cases
	 * 
	 * 
	 * @param testcaseFile
	 * @param containHeader
	 * @return
	 */
	private static HashMap getCIs(String testcaseFile, boolean containHeader){
		HashMap input_CI = new HashMap();
		try {
			BufferedReader br = new BufferedReader(new FileReader(testcaseFile));
			String str = null;
			int rowNo = 0;
			if(containHeader){
				rowNo = br.readLine().split("\t").length - 6;
			}
			
			StringBuilder infoRecorder = new StringBuilder();
			while((str=br.readLine())!= null){
				String[] strs = str.split("\t");
				input_CI.put(strs[0], Double.parseDouble(strs[1]));							
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return input_CI;
	}
	
	/**2009-12-31: get the failure rate and killed test case set of each fault
	 * 
	 * @param date_detailed: the directory of detailed files
	 * @param containHeader: whether the associated test detail file contains the Header
	 * @param startVersion:inclusive
	 * @param endVersion:exclusive
	 * @param date_save: directory to save the results
	 */
	public static void saveToFile_failureRates(String date_detailed, boolean containHeader, int startVersion, 
			int endVersion, String date_save){
		
		//1. 2010-01-03: load the CIs
		String testcaseFile =  "src/ccr/experiment/Context-Intensity_backup/TestHarness/20091230/TestCaseStatistics.txt";
		HashMap tc_CI = ResultAnalyzer.getCIs(testcaseFile, true);
		
		
		StringBuilder sb = new StringBuilder();
		sb.append("Mutant").append("\t").append("FailureRate").append("\t");
//		append("TestCaseKillArray").append("\n");
//		append("Average CD").append("\n"); //2010-01-03:get the correlation between CD and failure rates
		
		HashMap fault_effectTS = null;
		int fault_counter = 0;
		int TEST_POOL_END_LABEL = TestManager.TEST_POOL_SIZE + TestManager.TEST_POOL_START_LABEL;
		
		for(int i = startVersion; i < endVersion; i ++){
			System.out.println("Processing mutant version:" + i);
			fault_counter ++;
			String testDetailFile = 
				 "src/ccr/experiment/Context-Intensity_backup/TestHarness/"
					+ date_detailed + "/" + "detailed_" + i + "_" + (i+1) + ".txt";
			
			fault_effectTS = ResultAnalyzer.getFaultsWithTestCase(testDetailFile, containHeader);
			if(fault_effectTS.size() == 0 ){
				//this fault must throw out some exceptions or run out of time
				String fault  = "TestCFG2_" + i +".java"; 
				if(i >= 0 && i < 10){
					fault = "0" + i;
				}
				
				//1. get the fault ID
				sb.append(fault).append("\t");
				//2. get the failure rate
				sb.append("1.0").append("\t");			
//				//3. get the kill matrix of the test pool (set 1(kill the mutant) or 0(pass it) for each ordered test case)
//				for(int j = TestManager.TEST_POOL_START_LABEL; j < TEST_POOL_END_LABEL; j ++){
//					sb.append("1,");					
//				}
				
				
				sb.append("\n");
				
			}else{				
				String fault  = ""+ i ;
				if(i >= 0 && i < 10){
					fault = "0" + i;
				}
				
				ArrayList effectTS =(ArrayList)fault_effectTS.get(fault); 								 
				double failureRate = effectTS.size()/(double)TestManager.TEST_POOL_SIZE;  
				
				//1. get the fault ID
				sb.append(fault).append("\t");
				//2. get the failure rate
				sb.append(failureRate).append("\t");	
				
//				//3. get the kill matrix of the test pool (set 1(kill the mutant) or 0(pass it) for each ordered test case)
//				for(int j = TestManager.TEST_POOL_START_LABEL; j < TEST_POOL_END_LABEL; j ++){
//					if(effectTS.contains(""+j)){
//						sb.append("1,");
//					}else{
//						sb.append("0,");
//					}
//				}
				
				//2010-01-03: failure rate <-> CD				
				double[] CDArray = new double[effectTS.size()];
				for(int j = 0; j < effectTS.size(); j ++){
					String tc = (String)effectTS.get(j);
					CDArray[j] = (Double)tc_CI.get(tc);
					sb.append(CDArray[j]).append("\t");
				}
//				sb.append("\n");
//				Arrays.sort(CDArray);
//				
//				double max_CD = CDArray[CDArray.length - 1];
//				double min_CD = CDArray[0];
//				double sum_CD = 0;
//				for(int j = 0; j < CDArray.length; j ++){
//					sum_CD += CDArray[j];
//				}
//				double average_CD = sum_CD/(double)CDArray.length;
				
//				double median = 0.0;
//				double top_quarter = 0.0;
//				double bottom_quarter = 0.0;
//				if(CDArray.length/2 == 0){
//					median = ;
//					top_quarter = 0.0;
//					bottom_quarter = 0.0;
//				}else{
//					median = CDArray[(CDArray.length-1)/2];
//					top_quarter = CDArray[];
//					bottom_quarter = 0.0;
//				}
				
//				sb.append(average_CD);	
				sb.append("\n");
			}
		}
		String saveFile = "src/ccr/experiment"
			+ "/Context-Intensity_backup/TestHarness/" + date_save + "/FailureRate_CI_"
			+ fault_counter + ".txt";
		Logger.getInstance().setPath(saveFile, false);
		Logger.getInstance().write(sb.toString());
		Logger.getInstance().close();
	}
	
	/**
	 * 2009-02-19: get failure rate of faults according to the execution records
	 * of test pool
	 * 
	 * @param testDetailFile:
	 *            the execution records of test cases in a test pool
	 * @param saveFile:
	 */
	public static void getFailureRate(String testDetailFile,
			boolean containHeader, String saveFile) {
		HashMap faults = ResultAnalyzer.getFaultsWithTestCase(testDetailFile,
				containHeader);
		Iterator ite = faults.keySet().iterator();
		StringBuilder sb = new StringBuilder();
		sb.append("Fault" + "\t" + "FailureRate" + "\n");
		while (ite.hasNext()) {
			String fault = (String) ite.next();
			double detected = ((ArrayList) faults.get(fault)).size();
			sb.append(fault + "\t" + detected
					/ (double) ResultAnalyzer.TESTPOOL_SIZE + "\n");
		}

		Logger.getInstance().setPath(saveFile, false);
		Logger.getInstance().write(sb.toString());
		Logger.getInstance().close();

	}

	/**
	 * 2009-02-19: the result is a table with test cases as rows and its exposed
	 * faults as columns
	 * 
	 * @param detailFile:
	 *            the file recording the execution of test pool
	 * @return
	 */
	public static HashMap getTestCaseWithFaults(String testDetailFile,
			boolean containHeader) {
		HashMap testCases = new HashMap();

		try {
			BufferedReader br = new BufferedReader(new FileReader(
					testDetailFile));
			String str = null;
			if (containHeader)
				br.readLine();

			while ((str = br.readLine()) != null) {
				String[] strs = str.split("\t");

				if (strs[2].equals("F")) {// if a test case exposes a fault
					String fault = strs[0].substring(strs[0].indexOf("_")
							+ "_".length());
					String testCase = strs[1];

					ArrayList faults;
					if (testCases.containsKey(testCase))
						faults = (ArrayList) testCases.get(testCase);
					else
						faults = new ArrayList();

					if (!faults.contains(fault))
						faults.add(fault);
					testCases.put(testCase, faults);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return testCases;
	}

	/**
	 * 2009-02-19: the result is a table with test set as rows and all its test
	 * cases as columns
	 * 
	 * @param testSetFile
	 * @param containHeader
	 * @return
	 */
	public static HashMap getTestCaseFromTestSet(String testSetFile,
			boolean containHeader) {
		HashMap testSets = new HashMap();// testSet-TestCases
		try {
			BufferedReader br = new BufferedReader(new FileReader(testSetFile));
			String str = null;
			if (containHeader)
				br.readLine();

			ArrayList lines = new ArrayList();
			while ((str = br.readLine()) != null) {
				lines.add(str);
			}

			for (int i = 0; i < lines.size() - 3; i++) {
				str = (String) lines.get(i);
				String[] strs = str.split("\t");
				String testCases = strs[6];
				testCases = testCases.substring(testCases.indexOf("[")
						+ "[".length());
				testCases = testCases.substring(0, testCases.indexOf("]"));
				String[] testCaseList = testCases.split(",");
				ArrayList tcList = new ArrayList();
				for (String tc : testCaseList) {
					tcList.add(tc);
				}
				testSets.put(strs[0], tcList);
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return testSets;
	}

	/**
	 * analysis valid test cases within a test set, percentage of validTestCase
	 * can be one dimension to compare two testing criteria the result is a
	 * table with fault as rows and averaged-%validTestCases in all testSet as
	 * columns
	 * 
	 * @param testSetExecutionFile
	 * @param containHeader
	 * @param saveFile
	 */
	public static HashMap perValidTestCaseWithinTestSet(
			String testSetExecutionFile, boolean containHeader, String saveFile) {
		HashMap fault_perValidTC = new HashMap();
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					testSetExecutionFile));
			String str = null;
			if (containHeader)
				br.readLine();

			HashMap fault_PerValidTestCases = new HashMap();
			while ((str = br.readLine()) != null) {
				String[] strs = str.split("\t");
				String fault = strs[0];
				double perValidTestCase = Double.parseDouble(strs[4]);

				ArrayList perValidTestCases;
				if (fault_PerValidTestCases.containsKey(fault))
					perValidTestCases = (ArrayList) fault_PerValidTestCases
							.get(fault);
				else
					perValidTestCases = new ArrayList();

				perValidTestCases.add(perValidTestCase);
				fault_PerValidTestCases.put(fault, perValidTestCases);
			}

			sb.append("Fault" + "\t" + "Avg.PerValidTestCases" + "\t"
					+ "FailureRate" + "\t" + "Std.PerValidTestCases" + "\n");
			Iterator ite = fault_PerValidTestCases.keySet().iterator();
			while (ite.hasNext()) {
				String fault = (String) ite.next();
				ArrayList perValidTestCases = (ArrayList) fault_PerValidTestCases
						.get(fault);
				double sum = 0.0;
				for (int i = 0; i < perValidTestCases.size(); i++) {
					sum += (Double) perValidTestCases.get(i);
				}
				double avg = sum / (double) perValidTestCases.size();

				sum = 0.0;
				for (int i = 0; i < perValidTestCases.size(); i++) {
					double temp = (Double) perValidTestCases.get(i);
					// sum += Math.pow(temp-avg, 2.0);
					sum += (temp - avg) * (temp - avg);
				}
				double std = Math.sqrt(sum / (double) perValidTestCases.size());
				sb.append(fault + "\t" + avg + "\t"
						+ ResultAnalyzer.failureRate.get(fault) + "\t" + std
						+ "\n");
				fault_perValidTC.put(fault, avg);
			}

			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Logger.getInstance().setPath(saveFile, false);
		Logger.getInstance().write(sb.toString());
		Logger.getInstance().close();

		return fault_perValidTC;

	}

	public static HashMap getCIExposedFaults(String srcDir,
			boolean containHeader, String pattern) {
		HashMap CI_ExposedFaults = new HashMap();
		StringBuilder sb = new StringBuilder();

		ArrayList faults = new ArrayList();
		// 1.get all interested faults
		try {
			BufferedReader br_temp = new BufferedReader(new FileReader(srcDir
					+ "/FaultList.txt"));
			String line = null;

			while ((line = br_temp.readLine()) != null) {
				faults.add(line);
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		File[] files = new File(srcDir).listFiles();

		for (File file : files) {
			String fileName = file.getName();
			if (fileName.matches(pattern)) {
				String[] strs = fileName.split("_");
				String CI = strs[1];

				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					String str = null;
					if (containHeader)
						br.readLine();

					while ((str = br.readLine()) != null) {
						strs = str.split("\t");
						String fault = strs[0];
						String validTestSet = strs[5];

						if (!faults.contains(fault))
							continue;

						if (validTestSet.equals("1")) { // this test set can
														// expose this fault
							ArrayList faultTypes;
							if (CI_ExposedFaults.containsKey(CI))
								faultTypes = (ArrayList) CI_ExposedFaults
										.get(CI);
							else
								faultTypes = new ArrayList();

							faultTypes.add(fault);
							CI_ExposedFaults.put(CI, faultTypes);
						} else {
							if (!CI_ExposedFaults.containsKey(CI))
								CI_ExposedFaults.put(CI, new ArrayList());
						}
					}
					br.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		HashMap CI_FaultsNum = new HashMap();
		Iterator ite = CI_ExposedFaults.keySet().iterator();
		while (ite.hasNext()) {
			String CI = (String) ite.next();
			int faultNum = ((ArrayList) CI_ExposedFaults.get(CI)).size();
			CI_FaultsNum.put(CI, faultNum);
		}

		return CI_FaultsNum;
	}

	/**
	 * 2009-02-19: analysis faults number and faults type detected by an
	 * adequate test set the result is a table with test set as rows and faults
	 * number and types it exposed as columns
	 * 
	 * @param testSetExecutionFile
	 * @param containHeader
	 * @param saveFile
	 */
	public static HashMap faultsExposedByTestSet(String testSetExecutionFile,
			boolean containHeader, String saveFile) {
		HashMap testSet_fault = new HashMap();
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					testSetExecutionFile));
			String str = null;
			if (containHeader)
				br.readLine();

			HashMap testSet_FaultType = new HashMap();
			while ((str = br.readLine()) != null) {
				String[] strs = str.split("\t");
				String fault = strs[0];
				String testSet = strs[1];
				String validTestSet = strs[5];
				if (validTestSet.equals("1")) { // this test set can expose this
												// fault
					ArrayList faultTypes;
					if (testSet_FaultType.containsKey(testSet))
						faultTypes = (ArrayList) testSet_FaultType.get(testSet);
					else
						faultTypes = new ArrayList();
					if (!faultTypes.contains(fault))
						faultTypes.add(fault);
					testSet_FaultType.put(testSet, faultTypes);
				} else {
					if (!testSet_FaultType.containsKey(testSet))
						testSet_FaultType.put(testSet, new ArrayList());
				}
			}
			br.close();

			sb.append("TestSet" + "\t" + "FaultTypes" + "\n");
			Iterator ite = testSet_FaultType.keySet().iterator();
			while (ite.hasNext()) {
				String testSet = (String) ite.next();
				ArrayList faultTpes = (ArrayList) testSet_FaultType
						.get(testSet);
				sb.append(testSet + "\t" + faultTpes.size() + "\n");
				testSet_fault.put(testSet, faultTpes.size());
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Logger.getInstance().setPath(saveFile, false);
		Logger.getInstance().write(sb.toString());
		Logger.getInstance().close();
		return testSet_fault;
	}

	/**
	 * 2009-03-31: get the correlation between CI and testing performance
	 * measured by fault detection rates
	 * 
	 * @param srcDir
	 * @param containHeader
	 * @param pattern
	 * @return
	 */
	public static HashMap getCIPerValidTestSet(String srcDir,
			boolean containHeader, String pattern) {
		HashMap CI_perValidTestSet = new HashMap();

		File[] files = new File(srcDir).listFiles();

		ArrayList faults = new ArrayList();
		// 1.get all interested faults
		try {
			BufferedReader br_temp = new BufferedReader(new FileReader(srcDir
					+ "/FaultList.txt"));
			String line = null;

			while ((line = br_temp.readLine()) != null) {
				faults.add(line);
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (File file : files) {
			String fileName = file.getName();

			if (fileName.matches(pattern)) {
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					String str = null;
					if (containHeader)
						br.readLine();

					// get fault-test sets pair
					HashMap fault_TestSets = new HashMap();
					while ((str = br.readLine()) != null) {
						String[] strs = str.split("\t");
						String fault = strs[0];

						if (!faults.contains(fault))
							continue;

						String validTestSet = strs[5];

						ArrayList testSets;
						if (fault_TestSets.containsKey(fault))
							testSets = (ArrayList) fault_TestSets.get(fault);
						else
							testSets = new ArrayList();

						testSets.add(validTestSet);
						fault_TestSets.put(fault, testSets);
					}
					br.close();

					// count the valid test sets for each fault
					double sum_perValidTestSet = 0.0;
					Iterator ite = fault_TestSets.keySet().iterator();
					while (ite.hasNext()) { // for each fault
						String fault = (String) ite.next();
						ArrayList testSets = (ArrayList) fault_TestSets
								.get(fault);
						int validCounter = 0;
						for (int i = 0; i < testSets.size(); i++) {
							String testSet = (String) testSets.get(i);
							if (testSet.equals("1"))
								validCounter++;
						}
						sum_perValidTestSet += (double) validCounter
								/ (double) testSets.size();
					}
					double mean_perValidTestSet = sum_perValidTestSet
							/ (double) fault_TestSets.size();
					String[] strs = fileName.split("_");
					String CI = strs[1];
					CI_perValidTestSet.put(CI, mean_perValidTestSet);

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return CI_perValidTestSet;
	}

	/**
	 * return the size_averagePerValidTestSet for a given testing criteria
	 * 
	 * @param testSetExecutionFile
	 * @param containHeader
	 * @param saveFile
	 * @return
	 */
	public static HashMap getTSPerValidTestSet(String srcDir, String criterion,
			boolean containHeader) {
		HashMap size_perValidTestSet = new HashMap();

		File[] files = new File(srcDir).listFiles();

		ArrayList faults = new ArrayList();
		// 1.get all interested faults
		try {
			BufferedReader br_temp = new BufferedReader(new FileReader(srcDir
					+ "FaultList.txt"));
			String line = null;

			while ((line = br_temp.readLine()) != null) {
				faults.add(line);
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (File file : files) {
			String fileName = file.getName();
			if (fileName.matches(criterion + "\\_[0-9]+\\_limited_load.txt")) {
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					String str = null;
					if (containHeader)
						br.readLine();

					// get fault-test sets pair
					HashMap fault_TestSets = new HashMap();
					while ((str = br.readLine()) != null) {
						String[] strs = str.split("\t");
						String fault = strs[0];

						if (!faults.contains(fault))
							continue;

						String validTestSet = strs[5];

						ArrayList testSets;
						if (fault_TestSets.containsKey(fault))
							testSets = (ArrayList) fault_TestSets.get(fault);
						else
							testSets = new ArrayList();

						testSets.add(validTestSet);
						fault_TestSets.put(fault, testSets);
					}
					br.close();

					// count the valid test sets for all faults
					double sum_perValidTestSet = 0.0;
					Iterator ite = fault_TestSets.keySet().iterator();
					while (ite.hasNext()) { // for each fault
						String fault = (String) ite.next();
						ArrayList testSets = (ArrayList) fault_TestSets
								.get(fault);
						int validCounter = 0;
						for (int i = 0; i < testSets.size(); i++) {
							String testSet = (String) testSets.get(i);
							if (testSet.equals("1"))
								validCounter++;
						}
						sum_perValidTestSet += (double) validCounter
								/ (double) testSets.size();
					}
					double mean_perValidTestSet = sum_perValidTestSet
							/ (double) fault_TestSets.size();
					String size = fileName.substring(
							((String) (criterion + "_")).length(), fileName
									.indexOf("_limited_load.txt"));
					size_perValidTestSet.put(size, mean_perValidTestSet);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return size_perValidTestSet;
	}

	/**
	 * 2009-03-10: get the relationship between CI and testing performance of a
	 * specified criterion
	 * 
	 * @param srcDir
	 * @param criterion
	 * @param containHeader
	 * @return
	 */
	public static HashMap getCIPerValidTestSet(String srcDir, String criterion,
			boolean containHeader) {
		HashMap CIRange_perValidTestSet = new HashMap();

		File[] files = new File(srcDir).listFiles();

		ArrayList faults = new ArrayList();
		// 1.get all interested faults
		try {
			BufferedReader br_temp = new BufferedReader(new FileReader(srcDir
					+ "/FaultList.txt"));
			String line = null;

			while ((line = br_temp.readLine()) != null) {
				faults.add(line);
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (File file : files) {
			String fileName = file.getName();
			if (fileName.matches(criterion
					+ "\\_[0-9]+\\.0\\_[0-9]+\\.0\\_limited_load.txt")) {
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					String str = null;
					if (containHeader)
						br.readLine();

					// get fault-test sets pair
					HashMap fault_TestSets = new HashMap();
					while ((str = br.readLine()) != null) {
						String[] strs = str.split("\t");
						String fault = strs[0];

						if (!faults.contains(fault))
							continue;

						String validTestSet = strs[5];

						ArrayList testSets;
						if (fault_TestSets.containsKey(fault))
							testSets = (ArrayList) fault_TestSets.get(fault);
						else
							testSets = new ArrayList();

						testSets.add(validTestSet);
						fault_TestSets.put(fault, testSets);
					}
					br.close();

					// count the valid test sets for each fault
					double sum_perValidTestSet = 0.0;
					Iterator ite = fault_TestSets.keySet().iterator();
					while (ite.hasNext()) { // for each fault
						String fault = (String) ite.next();
						ArrayList testSets = (ArrayList) fault_TestSets
								.get(fault);
						int validCounter = 0;
						for (int i = 0; i < testSets.size(); i++) {
							String testSet = (String) testSets.get(i);
							if (testSet.equals("1"))
								validCounter++;
						}
						sum_perValidTestSet += (double) validCounter
								/ (double) testSets.size();
					}
					double mean_perValidTestSet = sum_perValidTestSet
							/ (double) fault_TestSets.size();
					String[] strs = fileName.split("_");

					String CIRange = strs[1] + "_" + strs[2];

					CIRange_perValidTestSet.put(CIRange, mean_perValidTestSet);

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return CIRange_perValidTestSet;
	}


	/**2009-10-14: get performance of valid test sets with respect to
	 * specified faults
	 *  
	 * 
	 * @param faultList
	 * @param testSetExecutionFile
	 * @param containHeader
	 * @param saveFile
	 * @return: fault-> testingPerformance(percentage of valid test sets)
	 */
	public static HashMap perValidTestSet(ArrayList faultList, 
			String testSetExecutionFile, boolean containHeader, String saveFile){
		
		HashMap fault_perValidTestSet = new HashMap();
		StringBuilder sb = new StringBuilder();

		try {
			BufferedReader br = new BufferedReader(new FileReader(
					testSetExecutionFile));
			String str = null;
			if (containHeader)
				br.readLine();

			HashMap fault_TestSets = new HashMap();
			while ((str = br.readLine()) != null) {
				String[] strs = str.split("\t");
				String fault = strs[0];
				
				if(faultList.contains(Integer.parseInt(fault))){ //2009-10-14: we only interest in specified faults
					
					String validTestSet = strs[5];

					ArrayList testSets;
					if (fault_TestSets.containsKey(fault))
						testSets = (ArrayList) fault_TestSets.get(fault);
					else
						testSets = new ArrayList();

					testSets.add(validTestSet);
					fault_TestSets.put(fault, testSets);
	
				}
			}
			br.close();

			// sb.append("Fault" + "\t" + "%ValidTestSet" + "\t" + "FailureRate"
			// + "\n");
			sb.append("FailureRate" + "\t" + "%ValidTestSet" + "\t" + "Fault"
					+ "\n");
			Iterator ite = fault_TestSets.keySet().iterator();
			while (ite.hasNext()) {
				String fault = (String) ite.next();
				ArrayList testSets = (ArrayList) fault_TestSets.get(fault);
				int validCounter = 0;
				for (int i = 0; i < testSets.size(); i++) {
					String testSet = (String) testSets.get(i);
					if (testSet.equals("1"))
						validCounter++;
				}
				double perValidTestSet = (double) validCounter
						/ (double) testSets.size();
				sb.append(ResultAnalyzer.failureRate.get(fault) + "\t"
						+ perValidTestSet + "\t" + fault + "\n");
				fault_perValidTestSet.put(fault, perValidTestSet);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 2009-03-08: no need to save the file
		// Logger.getInstance().setPath(saveFile, false);
		// Logger.getInstance().write(sb.toString());
		// Logger.getInstance().close();
		return fault_perValidTestSet;
	}

	/**
	 * return the fault-perValidTestSet(String->double) for a given testing
	 * criterion
	 * 
	 * @param testSetExecutionFile
	 * @param containHeader
	 * @param saveFile
	 * @return
	 */
	public static HashMap perValidTestSet(String testSetExecutionFile,
			boolean containHeader, String saveFile) {
		HashMap fault_perValidTestSet = new HashMap();
		StringBuilder sb = new StringBuilder();

		try {
			BufferedReader br = new BufferedReader(new FileReader(
					testSetExecutionFile));
			String str = null;
			if (containHeader)
				br.readLine();

			HashMap fault_TestSets = new HashMap();
			while ((str = br.readLine()) != null) {
				String[] strs = str.split("\t");
				String fault = strs[0];
				String validTestSet = strs[5];

				ArrayList testSets;
				if (fault_TestSets.containsKey(fault))
					testSets = (ArrayList) fault_TestSets.get(fault);
				else
					testSets = new ArrayList();

				testSets.add(validTestSet);
				fault_TestSets.put(fault, testSets);
			}
			br.close();

			// sb.append("Fault" + "\t" + "%ValidTestSet" + "\t" + "FailureRate"
			// + "\n");
			sb.append("FailureRate" + "\t" + "%ValidTestSet" + "\t" + "Fault"
					+ "\n");
			Iterator ite = fault_TestSets.keySet().iterator();
			while (ite.hasNext()) {
				String fault = (String) ite.next();
				ArrayList testSets = (ArrayList) fault_TestSets.get(fault);
				int validCounter = 0;
				for (int i = 0; i < testSets.size(); i++) {
					String testSet = (String) testSets.get(i);
					if (testSet.equals("1"))
						validCounter++;
				}
				double perValidTestSet = (double) validCounter
						/ (double) testSets.size();
				sb.append(ResultAnalyzer.failureRate.get(fault) + "\t"
						+ perValidTestSet + "\t" + fault + "\n");
				fault_perValidTestSet.put(fault, perValidTestSet);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 2009-03-08: no need to save the file
		// Logger.getInstance().setPath(saveFile, false);
		// Logger.getInstance().write(sb.toString());
		// Logger.getInstance().close();
		return fault_perValidTestSet;
	}
	
	public static void mergeHashMap_medium_classify(String[] criteria,
			String[] rename_criteria, HashMap criterion_Metric, String faultFile,
			String saveFile) {
		StringBuilder sb = new StringBuilder();

		// Header
		sb.append("Fault" + "\t" + "FailureRate" + "\t");

		for (int i = 0; i < criteria.length; i++) {
			String criterion = rename_criteria[i];
			sb.append(criterion + "\t");
		}
		sb.append("\n");

		// 2009-03-08: we may only interest in faults in fault list
		ArrayList faultList = new ArrayList();

		try {
			BufferedReader br = new BufferedReader(new FileReader(faultFile));
			String str = null;
			while ((str = br.readLine()) != null) {
				faultList.add(str);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String criterion = null;
		for (int j = 0; j < faultList.size(); j++) {
			String fault = (String) faultList.get(j);
			String failureRate = null;
			if(!ResultAnalyzer.failureRate.containsKey(fault)){
				fault = "0" + fault;
			}
			
			sb.append(fault + "\t" + ResultAnalyzer.failureRate.get(fault)
					+ "\t");
			for (int i = 0; i < criteria.length; i++) {
				criterion = criteria[i];
				HashMap metric = (HashMap) criterion_Metric.get(criterion);//metric:fault- testing effectiveness

				Object effectiveness = metric.get(fault);
				if (effectiveness == null) {
					effectiveness = metric.get(Integer.parseInt(fault) + "");
				}
				sb.append(effectiveness + "\t");
			}
			

			sb.append("\n");
			
		}

		// 2009-03-07: we need more abstract information: min,
		// max, medium, mean,
		// and SD for each criterion

		// another header
		sb.append("\nCriterion\tMin\tMedium\tMean\tMax\tSD\n");
		for (int i = 0; i < criteria.length; i++) {
			sb.append(rename_criteria[i] + "\t");
			
			criterion = criteria[i];
			HashMap metric = (HashMap) criterion_Metric.get(criterion);

			double[] performances = new double[faultList.size()];
			for (int j = 0; j < faultList.size(); j++) {
				String fault = (String) faultList.get(j);
				if(!metric.containsKey(fault)){
					fault = Integer.parseInt(fault) + "";
				}
				
				Double performance = (Double) metric.get(fault);
				performances[j] = performance;	
			}
			Arrays.sort(performances);//ascending order
			
			double min = performances[0];
			double max = performances[performances.length-1];
			double medium = 0.0;
			if(performances.length % 2 == 1){//odd number
				medium = performances[(performances.length + 1)/2 - 1];
			}else{//even number
				medium = (performances[performances.length/2 - 1] + performances[performances.length/2])/(double)2.0;
			}
			
			double sum = 0;
			for(int j = 0; j < performances.length; j ++){
				sum += performances[j];
			}
			double mean = sum/(double)performances.length;
			
			sum = 0.0;
			for (int j = 0; j < performances.length; j++) {
				sum += (performances[j] - mean) * (performances[j] - mean);
			}
			double STD = Math.sqrt(sum / performances.length);
			sb.append(min + "\t" + medium + "\t" + mean + "\t" + max + "\t" + STD + "\n");
		}

		Logger.getInstance().setPath(saveFile, false);
		Logger.getInstance().write(sb.toString());
		Logger.getInstance().close();
	}
	
	/**2009-10-20: this merge can get the medium information 
	 * 
	 * @param criteria
	 * @param rename_criteria
	 * @param criterion_Metric
	 * @param date
	 * @param saveFile
	 */
	public static void mergeHashMap_medium(String[] criteria,
			String[] rename_criteria, HashMap criterion_Metric, String date,
			String saveFile) {
		StringBuilder sb = new StringBuilder();

		// Header
		sb.append("Fault" + "\t" + "FailureRate" + "\t");

		for (int i = 0; i < criteria.length; i++) {
			String criterion = rename_criteria[i];
			sb.append(criterion + "\t");
		}
		sb.append("\n");

		// 2009-03-08: we may only interest in faults in fault list
		ArrayList faultList = new ArrayList();

		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"src/ccr/experiment"
							+ "/Context-Intensity_backup/TestHarness/" + date
							+ "/FaultList.txt"));
			String str = null;
			while ((str = br.readLine()) != null) {
				faultList.add(str);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String criterion = null;
		for (int j = 0; j < faultList.size(); j++) {
			String fault = (String) faultList.get(j);
			String failureRate = null;
			if(!ResultAnalyzer.failureRate.containsKey(fault)){
				fault = "0" + fault;
			}
			
			sb.append(fault + "\t" + ResultAnalyzer.failureRate.get(fault)
					+ "\t");
			for (int i = 0; i < criteria.length; i++) {
				criterion = criteria[i];
				HashMap metric = (HashMap) criterion_Metric.get(criterion);//metric:fault- testing effectiveness

				Object effectiveness = metric.get(fault);
				if (effectiveness == null) {
					effectiveness = metric.get("0" + Integer.parseInt(fault));
				}
				sb.append(effectiveness + "\t");
			}
			

			sb.append("\n");
			
		}

		// 2009-03-07: we need more abstract information: min,
		// max, medium, mean,
		// and SD for each criterion

		// another header
		sb.append("\nCriterion\tMin\tMedium\tMean\tMax\tSD\n");
		for (int i = 0; i < criteria.length; i++) {
			sb.append(rename_criteria[i] + "\t");
			
			criterion = criteria[i];
			HashMap metric = (HashMap) criterion_Metric.get(criterion);

			double[] performances = new double[faultList.size()];
			for (int j = 0; j < faultList.size(); j++) {
				String fault = (String) faultList.get(j);
				if(!metric.containsKey(fault)){
					fault = Integer.parseInt(fault) + "";
				}
				if(!metric.containsKey(fault)){
					fault = "0" + fault;
				}
				
				Double performance = (Double) metric.get(fault);
				performances[j] = performance;	
			}
			Arrays.sort(performances);//ascending order
			
			double min = performances[0];
			double max = performances[performances.length-1];
			double medium = 0.0;
			if(performances.length % 2 == 1){//odd number
				medium = performances[(performances.length + 1)/2 - 1];
			}else{//even number
				medium = (performances[performances.length/2 - 1] + performances[performances.length/2])/(double)2.0;
			}
			
			double sum = 0;
			for(int j = 0; j < performances.length; j ++){
				sum += performances[j];
			}
			double mean = sum/(double)performances.length;
			
			sum = 0.0;
			for (int j = 0; j < performances.length; j++) {
				sum += (performances[j] - mean) * (performances[j] - mean);
			}
			double STD = Math.sqrt(sum / performances.length);
			sb.append(min + "\t" + medium + "\t" + mean + "\t" + max + "\t" + STD + "\n");
		}

		Logger.getInstance().setPath(saveFile, false);
		Logger.getInstance().write(sb.toString());
		Logger.getInstance().close();
	}
	/**
	 * 2009-03-27: we wish to rename the original criteria
	 * 
	 * @param criteria:
	 *            the used criteria
	 * @param rename_criteria:
	 *            the header of used criteria,which should correspond to the
	 *            original criteria
	 * @param criterion_Metric
	 * @param date
	 * @param saveFile
	 */
	public static void mergeHashMap(String[] criteria,
			String[] rename_criteria, HashMap criterion_Metric, String date,
			String saveFile) {
		StringBuilder sb = new StringBuilder();

		// Header
		sb.append("Fault" + "\t" + "FailureRate" + "\t");

		for (int i = 0; i < criteria.length; i++) {
			// 2009-03-27: we wish to shorten the name of criterion
			String criterion = rename_criteria[i];
			sb.append(criterion + "\t");

			// sb.append(criteria[i] + "\t");
		}
		sb.append("\n");

		// 2009-03-08: we may not interest in all faults
		// Iterator ite = ResultAnalyzer.failureRate.keySet().iterator();

		// 2009-03-08: we may only interest in faults in fault list
		ArrayList faultList = new ArrayList();

		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"src/ccr/experiment"
							+ "/Context-Intensity_backup/TestHarness/" + date
							+ "/FaultList.txt"));
			String str = null;
			while ((str = br.readLine()) != null) {
				faultList.add(str);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String criterion = null;
		for (int j = 0; j < faultList.size(); j++) {
			String fault = (String) faultList.get(j);
			String failureRate = null;
			if(!ResultAnalyzer.failureRate.containsKey(fault)){
				fault = "0" + fault;
			}
			
			sb.append(fault + "\t" + ResultAnalyzer.failureRate.get(fault)
					+ "\t");
			for (int i = 0; i < criteria.length; i++) {
				criterion = criteria[i];
				HashMap metric = (HashMap) criterion_Metric.get(criterion);

//				if (!metric.containsKey(fault))
//					System.out.println(fault + " not exist");

				Object item = metric.get(fault);
				if (item == null) {
					item = metric.get(Integer.parseInt(fault) + "");
				}
				sb.append(item + "\t");
			}
			sb.append("\n");
		}

		// 2009-03-07: we need a more abstract information, for example: min,
		// max, mean
		// and SD for each criterion
		// another header
		sb.append("\nCriterion\tMin\tMean\tMax\tSD\n");
		for (int i = 0; i < criteria.length; i++) {
			criterion = criteria[i];

			// 2009-03-27: we wish to rename the original criteria
			sb.append(rename_criteria[i] + "\t");
			// sb.append(criterion + "\t");

			HashMap metric = (HashMap) criterion_Metric.get(criterion);

			double[] performances = new double[faultList.size()];
			for (int j = 0; j < faultList.size(); j++) {
				String fault = (String) faultList.get(j);
				if(!metric.containsKey(fault)){
//					System.out.println(fault + " does not exist");
					fault = Integer.parseInt(fault) + "";
				}
				
				Double performance = (Double) metric.get(fault);
				performances[j] = performance;	
								
			}

			//			
			// int index = 0;
			// Iterator ite1 = metric.keySet().iterator();
			// while(ite1.hasNext()){
			// String fault = (String)ite1.next();
			// if(!faultList.contains(fault))
			// continue;
			//					
			// Double performance= (Double)metric.get(fault);
			// if(performance == null){
			// performance = (Double)metric.get(Integer.parseInt(fault) + "");
			// }
			//				
			// performances[index] = performance;
			// index++;
			// }
			//			
			double min = Double.MAX_VALUE;
			double max = Double.MIN_VALUE;
			double sum = 0;

			for (int j = 0; j < performances.length; j++) {
				sum += performances[j];

				if (performances[j] > max)
					max = performances[j];
				if (performances[j] < min)
					min = performances[j];
			}
			double mean = sum / (double) performances.length;

			sum = 0.0;
			for (int j = 0; j < performances.length; j++) {
				sum += (performances[j] - mean) * (performances[j] - mean);
			}
			double SD = Math.sqrt(sum / performances.length);
			sb.append(min + "\t" + mean + "\t" + max + "\t" + SD + "\n");
		}

		Logger.getInstance().setPath(saveFile, false);
		Logger.getInstance().write(sb.toString());
		Logger.getInstance().close();
	}

	public static void mergeHashMap(String[] criteria,
			HashMap criterion_Metric, String date, String saveFile) {
		StringBuilder sb = new StringBuilder();

		// Header
		sb.append("Fault" + "\t" + "FailureRate" + "\t");

		for (int i = 0; i < criteria.length; i++) {
			// 2009-03-27: we wish to shorten the name of criterion
			String criterion = criteria[i].replaceAll("TestSets", "");
			sb.append(criterion + "\t");

			// sb.append(criteria[i] + "\t");
		}
		sb.append("\n");

		// 2009-03-08: we may not interest in all faults
		// Iterator ite = ResultAnalyzer.failureRate.keySet().iterator();

		// 2009-03-08: we may only interest in faults in fault list
		ArrayList faultList = new ArrayList();

		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"src/ccr/experiment"
							+ "/Context-Intensity_backup/TestHarness/" + date
							+ "/FaultList.txt"));
			String str = null;
			while ((str = br.readLine()) != null) {
				faultList.add(str);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String criterion = null;
		for (int j = 0; j < faultList.size(); j++) {
			String fault = (String) faultList.get(j);
			sb.append(fault + "\t" + ResultAnalyzer.failureRate.get(fault)
					+ "\t");
			for (int i = 0; i < criteria.length; i++) {
				criterion = criteria[i];
				HashMap metric = (HashMap) criterion_Metric.get(criterion);

				if (!metric.containsKey(fault))
					System.out.println("No existing");

				Object item = metric.get(fault);
				if (item == null) {
					item = metric.get(Integer.parseInt(fault) + "");
				}
				sb.append(item + "\t");
			}
			sb.append("\n");
		}

		// 2009-03-07: we need a more abstract information, for example: min,
		// max, mean
		// and SD for each criterion
		// another header
		sb.append("\nCriterion\tMin\tMean\tMax\tSD\n");
		for (int i = 0; i < criteria.length; i++) {
			criterion = criteria[i];
			sb.append(criterion + "\t");
			HashMap metric = (HashMap) criterion_Metric.get(criterion);

			double[] performances = new double[faultList.size()];
			for (int j = 0; j < faultList.size(); j++) {
				String fault = (String) faultList.get(j);
				Double performance = (Double) metric.get(fault);
				performances[j] = performance;
			}

			//			
			// int index = 0;
			// Iterator ite1 = metric.keySet().iterator();
			// while(ite1.hasNext()){
			// String fault = (String)ite1.next();
			// if(!faultList.contains(fault))
			// continue;
			//					
			// Double performance= (Double)metric.get(fault);
			// if(performance == null){
			// performance = (Double)metric.get(Integer.parseInt(fault) + "");
			// }
			//				
			// performances[index] = performance;
			// index++;
			// }
			//			
			double min = Double.MAX_VALUE;
			double max = Double.MIN_VALUE;
			double sum = 0;

			for (int j = 0; j < performances.length; j++) {
				sum += performances[j];

				if (performances[j] > max)
					max = performances[j];
				if (performances[j] < min)
					min = performances[j];
			}
			double mean = sum / (double) performances.length;

			sum = 0.0;
			for (int j = 0; j < performances.length; j++) {
				sum += (performances[j] - mean) * (performances[j] - mean);
			}
			double SD = Math.sqrt(sum / performances.length);
			sb.append(min + "\t" + mean + "\t" + max + "\t" + SD + "\n");
		}

		Logger.getInstance().setPath(saveFile, false);
		Logger.getInstance().write(sb.toString());
		Logger.getInstance().close();
	}

	public static void getCorrelationTSPeformance(String[] criteria,
			HashMap criterion_size_performance, String saveFile) {

		// 2. Header, keeping the order
		StringBuilder sb = new StringBuilder();
		sb.append("Size" + "\t");
		for (int i = 0; i < criteria.length; i++) {
			sb.append(criteria[i] + "\t");
		}
		sb.append("\n");

		String criterion = (String) criteria[0]; // standard criterion
		HashMap size_performance = (HashMap) criterion_size_performance
				.get(criterion);

		// 3.summary all results
		Iterator it1 = size_performance.keySet().iterator();
		while (it1.hasNext()) {
			String size = (String) it1.next();
			sb.append(size + "\t");

			ArrayList criterion_performance = new ArrayList();
			for (int i = 0; i < criteria.length; i++) {
				double performance = (Double) ((HashMap) criterion_size_performance
						.get(criteria[i])).get(size);
				sb.append(performance + "\t");
			}
			sb.append("\n");
		}

		Logger.getInstance().setPath(saveFile, false);
		Logger.getInstance().write(sb.toString());
		Logger.getInstance().close();

	}

	/**
	 * 2009-03-31: get the correlations among CI, coveredElements, and testing
	 * performances
	 * 
	 * @param CI_CoveredElements
	 * @param CI_Testing
	 * @param saveFile
	 */
	public static void getCorrelation_CI_CoverEle_Testing(
			HashMap CI_CoveredElements, HashMap CI_Testing, String saveFile) {
		StringBuilder sb = new StringBuilder();
		sb.append("CI" + "\t" + "CoveredElements" + "\t" + "FaultDetectionRate"
				+ "\n");

		Iterator ite = CI_CoveredElements.keySet().iterator();
		while (ite.hasNext()) {
			String CI = (String) ite.next();
			String coveredElements = (String) CI_CoveredElements.get(CI);
			// double faultDetectionRate = (Double)CI_Testing.get(CI);
			// sb.append(CI + "\t" + coveredElements + "\t" + faultDetectionRate
			// +"\n");
			int faultNum = (Integer) CI_Testing.get(CI);
			sb.append(CI + "\t" + coveredElements + "\t" + faultNum + "\n");
		}
		Logger.getInstance().setPath(saveFile, false);
		Logger.getInstance().write(sb.toString());
		Logger.getInstance().close();
	}

	public static void getCorrelationCIPeformance(String[] criteria,
			HashMap criterion_CIs, HashMap criterion_CIRange_performance,
			String saveFile) {

		// 2. Header, keeping the order
		StringBuilder sb = new StringBuilder();
		sb.append("CIRange\t");
		for (int i = 0; i < criteria.length; i++) {
			sb.append("CI\t" + criteria[i] + "\t");
		}
		sb.append("\n");

		String criterion = (String) criteria[0]; // standard criterion
		HashMap CIRange_performance = (HashMap) criterion_CIRange_performance
				.get(criterion);

		// 3.summary all results
		Iterator it1 = CIRange_performance.keySet().iterator();
		while (it1.hasNext()) {
			String CIRange = (String) it1.next();
			sb.append(CIRange + "\t");

			ArrayList criterion_performance = new ArrayList();
			for (int i = 0; i < criteria.length; i++) {
				criterion = criteria[i];
				double meanCI = (Double) ((ArrayList) ((HashMap) criterion_CIs
						.get(criterion)).get(CIRange)).get(1);
				CIRange_performance = (HashMap) criterion_CIRange_performance
						.get(criterion);
				double performance = (Double) CIRange_performance.get(CIRange);
				sb.append(meanCI + "\t" + performance + "\t");
			}
			sb.append("\n");
		}

		Logger.getInstance().setPath(saveFile, false);
		Logger.getInstance().write(sb.toString());
		Logger.getInstance().close();

	}

	/**
	 * 2009-03-28: we wish to find out all tests failed to executed when
	 * deriving the correlations between test set sizes and testing performances
	 * Add another method getLostTest(): we wish to find out all tests failed to
	 * executed when deriving the correlations between test set sizes and
	 * testing performances
	 * 
	 * @param criteria
	 * @param srcDir
	 * @param start
	 * @param end
	 * @return
	 */
	public static void getLostTest(String[] criteria, String srcDir, int start,
			int end) {

		HashMap criterion_tests = new HashMap(); // record the size of test
													// sets which have been
													// tested
		HashMap criterion_lostTest = new HashMap(); // record the size of test
													// sets which have been
													// missed

		for (int i = 0; i < criteria.length; i++) {
			criterion_tests.put(criteria[i], new ArrayList());
			criterion_lostTest.put(criteria[i], new ArrayList());
		}

		// 1.record all test set sizes which have been tested
		String pattern = null;
		File dir = new File(srcDir);
		for (int i = 0; i < criteria.length; i++) {
			String criterion = criteria[i];
			ArrayList tests = (ArrayList) criterion_tests.get(criterion);

			pattern = criterion + "\\_[0-9]+\\_limited_load.txt";
			for (File file : dir.listFiles()) {
				// for each file matchining the specified pattern
				if (file.getName().matches(pattern)) {
					String[] strs = file.getName().split("_");
					int index = Integer.parseInt(strs[strs.length - 3]);
					tests.add(index);
				}
			}

			criterion_tests.put(criterion, tests);
		}

		// 2.get test set sizes which have been lost
		for (int i = 0; i < criteria.length; i++) {
			String criterion = criteria[i];

			ArrayList tests = (ArrayList) criterion_tests.get(criterion);
			ArrayList lostTests = (ArrayList) criterion_lostTest.get(criterion);

			for (int j = start; j < end; j++) {
				if (!tests.contains(j)) {
					lostTests.add(j);
				}
			}
			criterion_lostTest.put(criterion, lostTests);
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < criteria.length; i++) {
			String criterion = criteria[i];
			ArrayList loadTests = (ArrayList) criterion_lostTest.get(criterion);
			sb.append(criterion + "\t");
			for (int j = 0; j < loadTests.size(); j++) {
				sb.append(loadTests.get(j) + "\t");
			}
			sb.append("\n");
		}

		Logger.getInstance().setPath(srcDir + "/LostTest.txt", false);
		Logger.getInstance().write(sb.toString());
		Logger.getInstance().close();
	}
	
	/**
	 * 2009-10-14: load faults from the fault list
	 * 
	 * @param faultFile
	 * @param containHeader
	 * @return
	 */
	public static ArrayList loadFaultList(String faultFile,
			boolean containHeader) {
		ArrayList faultList = new ArrayList();
		String line = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(faultFile));
			if (containHeader)
				br.readLine();

			while ((line = br.readLine()) != null) {
				faultList.add(line);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return faultList;
	}

	/**2009-12-31: save information (CI, activation, replacement) of CI-enhanced
	 * random test suite constructed with only Context Diversity information into files
	 * 
	 * 
	 * @param date:save directory
	 * @param size_ART
	 */
	public static void saveContextDiveristyInfo(String date, String size_ART ){
		String srcDir = "src/ccr/experiment/Context-Intensity_backup/TestHarness/"
			+ date + "/";
		
		String[] criteria = new String[]{
				"RA-H", "RA-L", "RA-R" 
		};
		
		HashMap criterion_perValidTS = new HashMap();
		srcDir += "/" + size_ART + "/";
		String saveFile = null;
		for(int i = 0; i < criteria.length; i ++ ){
			saveFile = srcDir + criteria[i] + "_" + size_ART + "_limited_load.txt";
			String testSetExecutionFile = saveFile;
			boolean containHeader1 = true;
			
			String perValidTSFile = saveFile.substring(0, saveFile
					.indexOf("_limited_load.txt"))
					+ "_PerValidTestSet.txt";
			
			HashMap perValidTS = ResultAnalyzer.perValidTestSet(
					testSetExecutionFile, containHeader1, perValidTSFile);
		}
		
		saveFile = srcDir + "/PerValidTS.txt";
		String[] rename_criteria = criteria;
		ResultAnalyzer.mergeHashMap_medium(criteria, rename_criteria,
				criterion_perValidTS, date, saveFile);
		
		StringBuilder sb = new StringBuilder();			
		sb.append("criterion").append("\t").append("minCI").append("\t").append("meanCI").
			append("\t").append("mediumCI").append("\t").
			append("maxCI").append("\t").append("stdCI").append("\n");
		
		boolean containHeader = true;
		for (int i = 0; i < criteria.length; i++) {
			if(!criteria[i].contains("RandomTestSets") || criteria[i].contains("RandomTestSets_R")){
				criteria[i] = criteria[i] + "_" + size_ART;
			}
			
			sb.append(ResultAnalyzer.getCriteriaCI(srcDir, containHeader, 
					criteria[i], rename_criteria[i]));
		}
		
		sb.append("\n").append("criterion").append("\t").append("minAct.").append("\t").append("meanAct.").
		append("\t").append("mediumAct.").append("\t").
		append("maxAct.").append("\t").append("stdAct.").append("\n");
		for (int i = 0; i < criteria.length; i++) {
			sb.append(ResultAnalyzer.getActivation(srcDir, containHeader, 
					criteria[i], rename_criteria[i]));
		}

		sb.append("\n").append("criterion").append("\t").append("minReplace.").append("\t").append("meanReplace.").
		append("\t").append("mediumReplace.").append("\t").
		append("maxReplace.").append("\t").append("stdReplace.").append("\n");
		for (int i = 0; i < criteria.length; i++) {
			sb.append(ResultAnalyzer.getReplacement(srcDir, containHeader, 
					criteria[i], rename_criteria[i]));
		}

		saveFile = srcDir + "/CI_Activation.txt";
		Logger.getInstance().setPath(saveFile, false);
		Logger.getInstance().write(sb.toString());
		Logger.getInstance().close();
	}
	
	/**2009-12-31: get the testing effectiveness and other info
	 * (CI, activation, replacement)of adequate test sets
	 * 
	 * @param date
	 * @param size_ART
	 */
	public static void saveTestingPerfomanceOfAdequateTestSet(String date, String size_ART){
		HashMap criterion_perValidTS = new HashMap();
		String srcDir = "src/ccr/experiment/Context-Intensity_backup/TestHarness/"
			+ date + "/" + size_ART + "/";
		
		String[] criteria = new String[] {"RandomTestSets_27", "AllPolicies_CA",
				"AllPolicies_RA-H", "AllPolicies_RA-L", "AllPolicies_RA-R",

				"RandomTestSets_42","All1ResolvedDU_CA", "All1ResolvedDU_RA-H",
				"All1ResolvedDU_RA-L", "All1ResolvedDU_RA-R",

				"RandomTestSets_50","All2ResolvedDU_CA", "All2ResolvedDU_RA-H",
				"All2ResolvedDU_RA-L", "All2ResolvedDU_RA-R", 
				
				"RandomTestSets_RA-H_27","RandomTestSets_RA-L_27","RandomTestSets_RA-R_27",
				"RandomTestSets_RA-H_42","RandomTestSets_RA-L_42","RandomTestSets_RA-R_42",
				"RandomTestSets_RA-H_50","RandomTestSets_RA-L_50","RandomTestSets_RA-R_50",
		};
		
		String saveFile = null;
		for (int i = 0; i < criteria.length; i++) {
			if(criteria[i].contains("RandomTestSets") && !criteria[i].contains("RandomTestSets_R")){
				saveFile = srcDir + criteria[i] + "_limited_load.txt";
			}else{
				saveFile = srcDir + criteria[i] + "_" + size_ART
				+ "_limited_load.txt";	
			}
			

			String testSetExecutionFile = saveFile;
			boolean containHeader1 = true;

			String perValidTSFile = saveFile.substring(0, saveFile
					.indexOf("_limited_load.txt"))
					+ "_PerValidTestSet.txt";
			
			//2009-2-23:
			HashMap perValidTS = ResultAnalyzer.perValidTestSet(
					testSetExecutionFile, containHeader1, perValidTSFile);
			
			//2009-10-14:
//			HashMap perValidTS = ResultAnalyzer.perValidTestSet(faultList, 
//					testSetExecutionFile, containHeader, saveFile);
			
			criterion_perValidTS.put(criteria[i], perValidTS);
		}

		saveFile = srcDir + "/PerValidTS.txt";


		// 2009-09-19: rename the default criterion
		String[] rename_criteria = new String[] {
		// rename the criteria of Group 1
				 "R-27",
				"AS_CA", 
				"AS_RA-H", "AS_RA-L", "AS_RA-R",
				 "R-42",
				"ASU-CA", 
				"ASU_RA-H", "ASU_RA-L", "ASU_RA-R",
				 "R-50",
				"A2SU_CA", 
				"A2SU_RA-H", "A2SU_RA-L", "A2SU_RA-R",
				
				"R-RA-H-27","R-RA-L-27", "R-RA-R-27",
				"R-RA-H-42","R-RA-L-42", "R-RA-R-42",
				"R-RA-H-50","R-RA-L-50", "R-RA-R-50"
		};
		//2009-02-25:
//		ResultAnalyzer.mergeHashMap(criteria, rename_criteria,
//				criterion_perValidTS, date, saveFile);
		
		//2009-10-20: get the mediume testing effectiveness
		ResultAnalyzer.mergeHashMap_medium(criteria, rename_criteria,
				criterion_perValidTS, date, saveFile);
		
		// 2009-02-25: to explore the CI distributions of different testing
		// criteria
		StringBuilder sb = new StringBuilder();			
//		sb.append("criteria" + "\t" + "minCI" + "\t" + "meanCI" + "\t"
//				+ "maxCI" + "\t" + "stdCI" + "\n");
		sb.append("criterion").append("\t").append("minCI").append("\t").append("meanCI").
			append("\t").append("mediumCI").append("\t").
			append("maxCI").append("\t").append("stdCI").append("\n");
		
		//2009-10-20:get CI distribution
		boolean containHeader = true;
		for (int i = 0; i < criteria.length; i++) {
			if(!criteria[i].contains("RandomTestSets") || criteria[i].contains("RandomTestSets_R")){
				criteria[i] = criteria[i] + "_" + size_ART;
			}
			
			//2009-02-25:
//			sb.append(ResultAnalyzer.getCriteriaCI(srcDir, containHeader,
//					criteria[i]));
			sb.append(ResultAnalyzer.getCriteriaCI(srcDir, containHeader, 
					criteria[i], rename_criteria[i]));
		}
		
		//2009-10-20:get Activation distribution
		sb.append("\n").append("criterion").append("\t").append("minAct.").append("\t").append("meanAct.").
		append("\t").append("mediumAct.").append("\t").
		append("maxAct.").append("\t").append("stdAct.").append("\n");
		for (int i = 0; i < criteria.length; i++) {
			sb.append(ResultAnalyzer.getActivation(srcDir, containHeader, 
					criteria[i], rename_criteria[i]));
		}

		//2009-10-20:get Replacement distribution
		sb.append("\n").append("criterion").append("\t").append("minReplace.").append("\t").append("meanReplace.").
		append("\t").append("mediumReplace.").append("\t").
		append("maxReplace.").append("\t").append("stdReplace.").append("\n");
		for (int i = 0; i < criteria.length; i++) {
			sb.append(ResultAnalyzer.getReplacement(srcDir, containHeader, 
					criteria[i], rename_criteria[i]));
		}

		saveFile = srcDir + "/CI_Activation.txt";
		Logger.getInstance().setPath(saveFile, false);
		Logger.getInstance().write(sb.toString());
		Logger.getInstance().close();
	}
	
	/**2009-10-26: a new analyzer to get the testing 
	 * effectiveness on different categories of fault like hard, medium and easy.
	 * 
	 * @param date
	 * @param size_ART
	 * @param Hard_Medium_Easy
	 */
	public static void saveTestingPerformanceOnFaultCategory(String date, String size_ART, String Hard_Medium_Easy ){
		HashMap criterion_perValidTS = new HashMap();
		
		 String[] criteria = new String[]{
		 //Group 1
		 "RandomTestSets_27",
		 "AllPoliciesTestSets_old",
		 "AllPoliciesTestSets_new",
		 "RandomTestSets_42",
		 "All1ResolvedDUTestSets_old",
		 "All1ResolvedDUTestSets_new",
		 "RandomTestSets_50",
		 "All2ResolvedDUTestSets_old",
		 "All2ResolvedDUTestSets_new",
						
		 //Group 2
		 "RandomTestSets_62",
		 "AllPoliciesTestSets_old_random_62",
		 "AllPoliciesTestSets_new_random_62",
		 "All1ResolvedDUTestSets_old_random_62",
		 "All1ResolvedDUTestSets_new_random_62",
		 "All2ResolvedDUTestSets_old_random_62",
		 "All2ResolvedDUTestSets_new_random_62",
		
		 //Group 3
		 // "RandomTestSets_62",
		 "AllPoliciesTestSets_old_criteria_62",
		 "AllPoliciesTestSets_new_criteria_62",
		 "All1ResolvedDUTestSets_old_criteria_62",
		 "All1ResolvedDUTestSets_new_criteria_62",
		 "All2ResolvedDUTestSets_old_criteria_62",
		 "All2ResolvedDUTestSets_new_criteria_62",
		 };
		String srcDir = "src/ccr/experiment/Context-Intensity_backup/TestHarness/" + date ;
		String faultFile = srcDir + "/FaultList_" + Hard_Medium_Easy + ".txt";
		//2009-10-26: change the srcDir
		srcDir += size_ART + "/";
		
		String saveFile = null;
		for (int i = 0; i < criteria.length; i++) {
			if(criteria[i].contains("RandomTestSets") && !criteria[i].contains("RandomTestSets_R")){
				saveFile = srcDir + criteria[i] + "_limited_load.txt";
			}else{
				saveFile = srcDir + criteria[i] + "_" + size_ART
				+ "_limited_load.txt";	
			}
			

			String testSetExecutionFile = saveFile;
			boolean containHeader1 = true;

			//2009-10-26: change the save file
			String perValidTSFile = saveFile.substring(0, saveFile
					.indexOf("_limited_load.txt"))
					+ "_PerValidTestSet_" + Hard_Medium_Easy + ".txt";
			
			HashMap perValidTS = ResultAnalyzer.perValidTestSet(
					testSetExecutionFile, containHeader1, perValidTSFile);
			
			
			criterion_perValidTS.put(criteria[i], perValidTS);
		}

		saveFile = srcDir + "/PerValidTS_" + Hard_Medium_Easy+ ".txt";


		String[] rename_criteria = new String[] {
				 "R-27",
				"AS_CA", 
				"AS_RA-H", "AS_RA-L", "AS_RA-R",
				 "R-42",
				"ASU-CA", 
				"ASU_RA-H", "ASU_RA-L", "ASU_RA-R",
				 "R-50",
				"A2SU_CA", 
				"A2SU_RA-H", "A2SU_RA-L", "A2SU_RA-R",
				
				"R-RA-H-27","R-RA-L-27", "R-RA-R-27",
				"R-RA-H-42","R-RA-L-42", "R-RA-R-42",
				"R-RA-H-50","R-RA-L-50", "R-RA-R-50"
		};
		//2009-10-26: get the testing effectiveness based on classified fault
		
		ResultAnalyzer.mergeHashMap_medium_classify(criteria, 
				rename_criteria, criterion_perValidTS, faultFile, saveFile);
		
		// 2009-02-25: to explore the CI distributions of different testing
		// criteria
		StringBuilder sb = new StringBuilder();			
		sb.append("criterion").append("\t").append("minCI").append("\t").append("meanCI").
			append("\t").append("mediumCI").append("\t").
			append("maxCI").append("\t").append("stdCI").append("\n");
		
		//2009-10-20:get CI distribution
		boolean containHeader = true;
		for (int i = 0; i < criteria.length; i++) {
			if(!criteria[i].contains("RandomTestSets") || criteria[i].contains("RandomTestSets_R")){
				criteria[i] = criteria[i] + "_" + size_ART;
			}
			
			sb.append(ResultAnalyzer.getCriteriaCI(srcDir, containHeader, 
					criteria[i], rename_criteria[i]));
		}
		
		//2009-10-20:get Activation distribution
		sb.append("\n").append("criterion").append("\t").append("minAct.").append("\t").append("meanAct.").
		append("\t").append("mediumAct.").append("\t").
		append("maxAct.").append("\t").append("stdAct.").append("\n");
		for (int i = 0; i < criteria.length; i++) {
			sb.append(ResultAnalyzer.getActivation(srcDir, containHeader, 
					criteria[i], rename_criteria[i]));
		}

		//2009-10-20:get Replacement distribution
		sb.append("\n").append("criterion").append("\t").append("minReplace.").append("\t").append("meanReplace.").
		append("\t").append("mediumReplace.").append("\t").
		append("maxReplace.").append("\t").append("stdReplace.").append("\n");
		for (int i = 0; i < criteria.length; i++) {
			sb.append(ResultAnalyzer.getReplacement(srcDir, containHeader, 
					criteria[i], rename_criteria[i]));
		}

		saveFile = srcDir + "/CI_Activation_" + Hard_Medium_Easy+ ".txt";
		Logger.getInstance().setPath(saveFile, false);
		Logger.getInstance().write(sb.toString());
		Logger.getInstance().close();	
	}
	
	/**2009-12-31: get the correlation between test suite size 
	 * and testing performance
	 * 
	 * @param date
	 */
	public static void saveCorrelationSizePerformance(String date){
		String[] criteria = new String[] {
				// //Group 1
				// "AllPoliciesTestSets_old",
				// "AllPoliciesTestSets_new",
				// "All1ResolvedDUTestSets_old",
				// "All1ResolvedDUTestSets_new",
				// "All2ResolvedDUTestSets_old",
				// "All2ResolvedDUTestSets_new",

				// Group 2
				"RandomTestSets",
				"AllPoliciesTestSets_old_random",
				"AllPoliciesTestSets_new_random",
				"All1ResolvedDUTestSets_old_random",
				"All1ResolvedDUTestSets_new_random",
				"All2ResolvedDUTestSets_old_random",
				"All2ResolvedDUTestSets_new_random",

				// Group 3
				// "RandomTestSets",
				"AllPoliciesTestSets_old_criteria",
				"AllPoliciesTestSets_new_criteria",
				"All1ResolvedDUTestSets_old_criteria",
				"All1ResolvedDUTestSets_new_criteria",
				"All2ResolvedDUTestSets_old_criteria",
				"All2ResolvedDUTestSets_new_criteria", };

		HashMap criterion_sizePerformance = new HashMap();
		String srcDir = "src/ccr/experiment/Context-Intensity_backup/TestHarness/" + date ;		
		boolean containHeader = true;
		for (int i = 0; i < criteria.length; i++) {
			String criterion = criteria[i];
			
			criterion_sizePerformance
					.put(criteria[i], ResultAnalyzer.getTSPerValidTestSet(
							srcDir, criterion, containHeader));
		}
		
		String saveFile = srcDir + "/Size_Performance.txt";
		ResultAnalyzer.getCorrelationTSPeformance(criteria,
				criterion_sizePerformance, saveFile);
	}
	
	/**2009-12-31: get the correlation between CI of adequate test sets
	 * and testing performance
	 * 
	 * @param date
	 */
	public static void saveCorrelationCIPerformance(String date){
		String[] criteria = new String[] { "AllPolicies", "All1ResolvedDU",
				"All2ResolvedDU", "TestCases", };

		String srcDir = "src/ccr/experiment/Context-Intensity_backup/TestHarness/" + date ;	
		boolean containHeader = true;
		// 2009-02-25: to explore the CI distributions of different testing
		// criteria
		HashMap criterion_CIs = new HashMap();
		for (int i = 0; i < criteria.length; i++) {
			String pattern = criteria[i]
					+ "\\_[0-9]+\\.0\\_[0-9]+\\.0\\_CI.txt";
			criterion_CIs.put(criteria[i], ResultAnalyzer.getCriterialCI(
					srcDir, containHeader, criteria[i], pattern));
		}

		HashMap criterion_CIPerformance = new HashMap();
		String saveFile = srcDir + "/CI_Performance.txt";
		for (int i = 0; i < criteria.length; i++) {
			String criterion = criteria[i];
			criterion_CIPerformance
					.put(criterion, ResultAnalyzer.getCIPerValidTestSet(
							srcDir, criterion, containHeader));
		}
		ResultAnalyzer.getCorrelationCIPeformance(criteria, criterion_CIs,
				criterion_CIPerformance, saveFile);
	}

	/**2009-12-31:get the correlation between coveredElements of adequate test sets
	 * and testing performance
	 * 
	 * @param date
	 */
	public static void saveCorrelationCoveredElemsPerformance(String date){
		// 2009-03-31: get the correlation between covered elements and
		// testing performance, this correlation
		// can explain why CI does matter with testing effectiveness

		// 1. get the correlations between CI and covered elements
		String pattern = "TestCases"
				+ "\\_[0-9]+\\.0\\_[0-9]+\\.0\\_coveredElements.txt";
		
		String srcDir = "src/ccr/experiment/Context-Intensity_backup/TestHarness/" + date ;	
		boolean containHeader = true;
		HashMap CI_coveredElements = ResultAnalyzer.getCICoveredElements(
				srcDir, containHeader, pattern);

		// 2. get the correlations between CI and testing
		// performances(measured by fault detection rate)
		pattern = "TestCases"
				+ "\\_[0-9]+\\.0\\_[0-9]+\\.0\\_limited_load.txt";
		// HashMap CI_Performance =
		// ResultAnalyzer.getCIPerValidTestSet(srcDir, containHeader,
		// pattern);
		HashMap CI_Performance = ResultAnalyzer.getCIExposedFaults(srcDir,
				containHeader, pattern);
		String saveFile = srcDir + "/CI_CoveredElements_Performance.txt";

		// 3. merge the results
		saveFile = srcDir + "/CI_CoveredElements_Performance.txt";
		ResultAnalyzer.getCorrelation_CI_CoverEle_Testing(
				CI_coveredElements, CI_Performance, saveFile);
	}
	
		
	/**2009-12-31: merge files based on some patterns
	 * 
	 * @param date
	 * @param prefix
	 */
	public static void saveMergedFile(String date, String prefix){
		boolean containHeader = true;		
		String pattern = prefix + "\\_[0-9]+\\_[0-9]+\\.txt";
		String srcDir = "src/ccr/experiment/Context-Intensity_backup/TestHarness/" + date ;	
		String saveFile = srcDir + prefix + ".txt";
		ResultAnalyzer.mergeFiles(srcDir, containHeader, pattern, saveFile);
	}
	
	/**2009-12-31: get lost tests with respect to an adequate testing criterion
	 * 
	 * @param startVersion
	 * @param endVersion
	 * @param date
	 */
	public static void saveLostTests(String startVersion, String endVersion, String date){
		String[] criteria = new String[] {
				// Group 2
				"RandomTestSets",
				"AllPoliciesTestSets_old_random",
				"AllPoliciesTestSets_new_random",
				"All1ResolvedDUTestSets_old_random",
				"All1ResolvedDUTestSets_new_random",
				"All2ResolvedDUTestSets_old_random",
				"All2ResolvedDUTestSets_new_random",

				//Group 3					
				"AllPoliciesTestSets_old_criteria",
				"AllPoliciesTestSets_new_criteria",
				"All1ResolvedDUTestSets_old_criteria",
				"All1ResolvedDUTestSets_new_criteria",
				"All2ResolvedDUTestSets_old_criteria",
				"All2ResolvedDUTestSets_new_criteria", };
		int start = Integer.parseInt(startVersion);
		int end = Integer.parseInt(endVersion);
		String srcDir = "src/ccr/experiment/Context-Intensity_backup/TestHarness/" + date ;
		ResultAnalyzer.getLostTest(criteria, srcDir, start, end);
	}
	
	public static void main(String[] args) {

//		// 2009-02-18: load CI of each test case from a file
//		String testcaseFile = "src/ccr/experiment/Context-Intensity_backup/TestHarness/"
//			+ "/20091019/TestPool.txt";
//		boolean containHeader = true;		
//		Adequacy.getTestPool(testcaseFile, containHeader);
//
//		// 2009-02-22: load failure rates from a file
//		containHeader = false;
//		String failureRateFile = "src/ccr/experiment/Context-Intensity_backup/TestHarness/"
//				+ "/20091019/failureRate.txt";
//		ResultAnalyzer.loadFailureRate(failureRateFile, containHeader);

		//2009-12-31: reformat all the methods
		String instruction = args[0];
		
		if(instruction.equals("ContextDiversityOnly")){
			String date = args[1];
			String size_ART = args[2];
			ResultAnalyzer.saveContextDiveristyInfo(date, size_ART);			
		}else if(instruction.equals("Context_Intensity")
				|| instruction.equals("Limited") || instruction.equals("Load")){
			String date = args[1];
			String size_ART = args[2];
			ResultAnalyzer.saveTestingPerfomanceOfAdequateTestSet(date, size_ART);			
		}else if (instruction.equals("getSizePerformance")) {
			String date = args[1];
			ResultAnalyzer.saveCorrelationSizePerformance(date);
		}else if (instruction.equals("getCIPerformance")) {
			String date = args[1];
			ResultAnalyzer.saveCorrelationCIPerformance(date);
		}else if (instruction.equals("getCoveredElementsPerformance")) {
			String date = args[1];
			ResultAnalyzer.saveCorrelationCoveredElemsPerformance(date);
		}else if (instruction.equals("mergeFiles")) {
			String date = args[1];
			String prefix = args[2];
			ResultAnalyzer.saveMergedFile(date, prefix);
		} else if (instruction.equals("getLostTests")) {
			String date = args[1];
			String startVersion = args[2];
			String endVersion = args[3];
			ResultAnalyzer.saveLostTests(startVersion, endVersion, date);
		}else if(instruction.equals("Load_Classified")){
			String date = args[1];
			String size_ART = args[2];
			String Hard_Medium_Easy = args[3]; //can only be "Hard", "Medium" or "Easy"
			ResultAnalyzer.saveTestingPerformanceOnFaultCategory(date, size_ART, Hard_Medium_Easy);
		}else if(instruction.equals("saveFailureRateDetails")){
			
			String date_detailed = args[1]; //the directory to load the detailed file
			boolean containHeader = true;
			int startVersion = 0;
			int endVersion = 5024;
			String date_save = args[2]; // the directory to save the results			
//			ResultAnalyzer.saveToFile_failureRates(date_detailed, containHeader, startVersion, endVersion, date_save);
//			ResultAnalyzer.saveToFile_TestPool(date_detailed, containHeader, startVersion, endVersion, date_save);
			
			//2010-01-01:
			boolean containHeader_detailed = true;
			String faultList = "src/ccr/experiment/Context-Intensity_backup/TestHarness/" 
				+ date_save + "/NonEquivalentFaults.txt";			
			boolean containHeader_fault = false;
			ResultAnalyzer.saveToFile_TestPool_faultList(date_detailed, containHeader_detailed, faultList, containHeader_fault, date_save);
			
		}else if(instruction.equals("getEquivalentMutants")){
			String date_detailed = args[1];
			boolean containHeader_detailed = true;
			String date_faultList = args[2];
			boolean containHeader_faultList = true;
			String date_save = args[3];						
			ResultAnalyzer.saveNonEquivalentFault(date_faultList, containHeader_faultList, date_detailed, containHeader_detailed, date_save);
		}
	} 
}
