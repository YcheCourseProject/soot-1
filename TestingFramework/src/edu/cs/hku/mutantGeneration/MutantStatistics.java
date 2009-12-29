package edu.cs.hku.mutantGeneration;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;



import edu.cs.hku.util.DatabaseManager;
import edu.cs.hku.util.Logger;

public class MutantStatistics {
		
	public HashMap<String, HashMap<String, Integer>> getStatistics_ClassLevel(String mutantDir){
		HashMap<String, HashMap<String, Integer>> class_Mutants = 
			new HashMap<String, HashMap<String, Integer>>();
		//class-level mutants: class-> (modification ->) mutant operators-> mutant numbers
		
		File resultsDir = new File(mutantDir);
		System.out.println(resultsDir.getName());
		
		if(resultsDir.exists()){
			File[] classesDir = resultsDir.listFiles();
			for(File classDir: classesDir){	
				if(classDir.isDirectory()){
					System.out.println(classDir.getName());
					
					//get statistics of class-level mutants
					File classMutantsDir = new File(classDir.getAbsolutePath() +
							File.separator + "class_mutants");
					
					File[] mutantOperators = classMutantsDir.listFiles();
					for(File mutantOperator: mutantOperators){
						System.out.println(mutantOperator.getName());
						if(mutantOperator.isDirectory()){
							String[] tmp = mutantOperator.getName().split("_");
							String mutantoperator = tmp[0];
							String clazz = classDir.getName();
							
							if(class_Mutants.containsKey(clazz)){ //className exists
								HashMap<String, Integer> tmpHash = class_Mutants.get(clazz);
								if(tmpHash.containsKey(mutantoperator)){ //mutant operator exists
									Integer mutantNum = tmpHash.get(mutantoperator);
									mutantNum ++;
									tmpHash.put(mutantoperator, mutantNum);
									class_Mutants.put(clazz, tmpHash);
								}else{ //mutant operator does not exist 
									tmpHash.put(mutantoperator, 1);
									class_Mutants.put(clazz, tmpHash);
									
								}
							}else{ //className does not exist
								HashMap<String, Integer> tmpHash = new HashMap<String, Integer>();
								tmpHash.put(mutantoperator, 1);
								class_Mutants.put(clazz, tmpHash);
							}
						}
					}
				}
			}
		}else{
			System.out.println("The mutant directory does not exist at all!");
		}
		
		return class_Mutants;
	}
	
	public void saveToFile_ClassLevel(HashMap<String, HashMap<String, Integer>> class_Mutants, String saveFile){
		StringBuilder sb = new StringBuilder();
		sb.append("Class").append("\t").append("Function").append("\t").
		append("MutantOperators").append("\t").append("MutantNumber").append("\n");
		
		//record all class-level mutants
		for(String clazz: class_Mutants.keySet()){
			HashMap<String, Integer> operators_Mutants = class_Mutants.get(clazz);
			for(String operator: operators_Mutants.keySet()){
				sb.append(clazz).append("\t").append("global").append("\t").
				append(operator).append("\t").append(operators_Mutants.get(operator)).append("\n");
			}
		}
		
		Logger.getInstance().setPath(saveFile, false);
		Logger.getInstance().write(sb.toString());
		Logger.getInstance().close();
	}

	public void saveToDB_ClassLevel(HashMap<String, HashMap<String, Integer>> class_Mutants){
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO mutant (class, function, operator, mutantNumber) VALUES ");
		
		for(String clazz: class_Mutants.keySet()){
			HashMap<String, Integer> operators_Mutants = class_Mutants.get(clazz);
			for(String operator: operators_Mutants.keySet()){				
				sb.append("(\'").append(clazz).append("\'").append(",");
				sb.append("\'").append("global").append("\'").append(",");
				sb.append("\'").append(operator).append("\'").append(",");
				sb.append("\'").append(operators_Mutants.get(operator)).append("\'").append("),");				
			}
		}
		String sql = sb.substring(0, sb.lastIndexOf(","));
		DatabaseManager.getInstance().update(sql);
	}
	
	
	
		
	public HashMap<String, HashMap<String, HashMap<String, Integer>>> getStatistic_MethodLevel(String mutantDir){
		HashMap<String, HashMap<String, HashMap<String, Integer>>> method_Mutants = 
			new HashMap<String, HashMap<String, HashMap<String, Integer>>>();
		
		File resultsDir = new File(mutantDir);
		System.out.println(resultsDir.getName());
		
		if(resultsDir.exists()){
			File[] classesDir = resultsDir.listFiles();
			for(File classDir: classesDir){	
				if(classDir.isDirectory()){
					System.out.println(classDir.getName());
					
					//get statistics of method-level mutants;
					File classMutantsDir_T = new File(classDir.getAbsolutePath() +
							File.separator + "traditional_mutants");
					File[] methodMutants_T = classMutantsDir_T.listFiles(); 
					for(File methodMutant: methodMutants_T){
						System.out.println(methodMutant.getName());
						if(methodMutant.isDirectory()){ //This directory represents one method instead of a mutantion log file
							File[] mutantOperatorDir = methodMutant.listFiles();						
							for(File mutantOperator: mutantOperatorDir){
								System.out.println(mutantOperator.getName());
								String[] temp = mutantOperator.getName().split("_");
								String mutantoperator = temp[0];
								String method = methodMutant.getName();
								String clazz = classDir.getName();

								if(method_Mutants.containsKey(clazz)){
									HashMap<String, HashMap<String, Integer>> tmp2 = method_Mutants.get(clazz);
									if(tmp2.containsKey(method)){
										HashMap<String, Integer> operator_Mutants = tmp2.get(method);
										if(operator_Mutants.containsKey(mutantoperator)){
											Integer mutantNum = operator_Mutants.get(mutantoperator);
											mutantNum ++;
											operator_Mutants.put(mutantoperator, mutantNum);
											tmp2.put(method, operator_Mutants);
											method_Mutants.put(clazz, tmp2);
										}else{ //mutant operator does not exist
											operator_Mutants.put(mutantoperator, 1);
											tmp2.put(method, operator_Mutants);
											method_Mutants.put(clazz, tmp2);
										}
									}else{ //method does not exist
										HashMap<String, Integer> tmp1 = new HashMap<String, Integer>();										
										tmp1.put(mutantoperator, 1);
										tmp2.put(method, tmp1);
										method_Mutants.put(clazz, tmp2);
									}
								}else{//class does not exist
									HashMap<String, Integer> tmp1= new HashMap<String, Integer>();
									tmp1.put(mutantoperator, 1);
									HashMap<String, HashMap<String, Integer>> tmp2 = new HashMap<String, HashMap<String,Integer>>();
									tmp2.put(method, tmp1);
									method_Mutants.put(clazz, tmp2);
								}
							}
						}
					}
				}
			}
		}else{
			System.out.println("The mutant directory does not exist at all!");
		}
		
		return method_Mutants;
	}
	
	public void saveToFile_MethodLevel(HashMap<String, HashMap<String, HashMap<String, Integer>>> method_Mutants, String saveFile){
		StringBuilder sb = new StringBuilder();
		sb.append("Class").append("\t").append("Function").append("\t").
		append("MutantOperators").append("\t").append("MutantNumber").append("\n");
		
		//record all method-level mutants
		for(String clazz: method_Mutants.keySet()){
			HashMap<String, HashMap<String, Integer>> function_Mutants = method_Mutants.get(clazz);
			for(String function: function_Mutants.keySet()){
				HashMap<String, Integer> operator_Mutants = function_Mutants.get(function);
				for(String operator: operator_Mutants.keySet()){
					sb.append(clazz).append("\t").append(function).append("\t").
					append(operator).append("\t").append(operator_Mutants.get(operator)).append("\n");
				}
			}
		}
		
		Logger.getInstance().setPath(saveFile, false);
		Logger.getInstance().write(sb.toString());
		Logger.getInstance().close();
	}

	public void saveToDB_MethodLevel(HashMap<String, HashMap<String, HashMap<String, Integer>>> method_Mutants){
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO mutant (class, function, operator, mutantNumber) VALUES ");
		
		for(String clazz: method_Mutants.keySet()){
			HashMap<String, HashMap<String, Integer>> function_Mutants = method_Mutants.get(clazz);
			for(String function: function_Mutants.keySet()){
				HashMap<String, Integer> operator_Mutants = function_Mutants.get(function);
				for(String operator: operator_Mutants.keySet()){
					sb.append("(\'").append(clazz).append("\'").append(",");
					sb.append("\'").append(function).append("\'").append(",");
					sb.append("\'").append(operator).append("\'").append(",");
					sb.append("\'").append(operator_Mutants.get(operator)).append("\'").append("),");										
				}
			}
		}
		
		String sql = sb.substring(0, sb.lastIndexOf(","));
		DatabaseManager.getInstance().update(sql);
	}
	
	/**2009-12-29:classify mutants for a class&a method into three categories:
	 * missing construct, wrong constructs and extraneous constructs 
	 * @param className
	 * @param methodName
	 */
	public void mutantSum_IncludeMethod(String className, String methodName){
		String[] missingConstructs = {"AOD", "COD", "LOD", "IHD",
				"IOD", "ISD", "IPC", "PCD", "OMD", "JTD", "JSD", "JID"};
		
		String[] wrongConstructs = {"AOR", "ROR", "COR", "SOR", 
				"LOR", "ASR", "IOP", "IOR", "PMD", "PPD", "PCC",
				"PRV", "OMR", "OAC", "EOA", "EOC", "EAM", "EMM"};
		
		String[] extraneousConstructs = {"AOI", "COI", "LOI", "IHI", 
				"ISI", "PNC", "PCI", "JTI", "JSI", "JDC"};
		
		StringBuilder report = new StringBuilder();
		report.append("MissingConstructs").append("\t").append("WrongConstructs").
			append("\t").append("ExtraneousConstructs").append("\t").append("Total").append("\n");
		
		StringBuilder sql = new  StringBuilder();
		
		
		try {
			//1.get missing constructs
			sql.append("SELECT SUM( mutantNumber ) FROM mutant WHERE class LIKE \'%").append(className).append("%\'")
			.append(" AND function ").append("LIKE \'%").append(methodName).append("%\' ").append("And (");		
			for(int i = 0; i < missingConstructs.length -1; i ++){
				sql.append("operator LIKE \'%").append(missingConstructs[i]).append("%\' or ");
			}
			sql.append("operator LIKE \'%").append(missingConstructs[missingConstructs.length -1]).append("%\')");		
			ResultSet rs = DatabaseManager.getInstance().query(sql.toString());
			while(rs.next()){
				report.append(rs.getInt(1)).append("\t");
			}		
			sql.setLength(0);
			
			//2.get wrong constructs
			sql.append("SELECT SUM( mutantNumber ) FROM mutant WHERE class LIKE \'%").append(className).append("%\'")
			.append(" AND function ").append("LIKE \'%").append(methodName).append("%\' ").append("And (");	
			for(int i = 0; i < wrongConstructs.length -1; i ++){
				sql.append("operator LIKE \'%").append(wrongConstructs[i]).append("%\' or ");
			}
			sql.append("operator LIKE \'%").append(wrongConstructs[wrongConstructs.length -1]).append("%\')");		
			rs = DatabaseManager.getInstance().query(sql.toString());
			while(rs.next()){
				report.append(rs.getInt(1)).append("\t");
			}		
			sql.setLength(0);
			
			//3.get extraneous constructs
			sql.append("SELECT SUM( mutantNumber ) FROM mutant WHERE class LIKE \'%").append(className).append("%\'")
			.append(" AND function ").append("LIKE \'%").append(methodName).append("%\' ").append("And (");	
			for(int i = 0; i < extraneousConstructs.length -1; i ++){
				sql.append("operator LIKE \'%").append(extraneousConstructs[i]).append("%\' or ");
			}
			sql.append("operator LIKE \'%").append(extraneousConstructs[extraneousConstructs.length -1]).append("%\')");		
			rs = DatabaseManager.getInstance().query(sql.toString());
			while(rs.next()){
				report.append(rs.getInt(1)).append("\t");
			}		
			sql.setLength(0);
			
			//4.get mutant sums
			sql.append("SELECT SUM( mutantNumber ) FROM mutant WHERE class LIKE \'%").append(className).append("%\'")
			.append(" AND function ").append("LIKE \'%").append(methodName).append("%\' ");
			rs = DatabaseManager.getInstance().query(sql.toString());
			while(rs.next()){
				report.append(rs.getInt(1)).append("\t");
			}		
			sql.setLength(0);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(report.toString());
	}
	
	/**2009-12-29:classify mutants for a class& a method(exclude) into three categories:
	 * missing construct, wrong constructs and extraneous constructs 
	 * 
	 * @param className
	 * @param methodName
	 */
	public void mutantSum_ExcludeMethod(String className, String methodName){
		String[] missingConstructs = {"AOD", "COD", "LOD", "IHD",
				"IOD", "ISD", "IPC", "PCD", "OMD", "JTD", "JSD", "JID"};
		
		String[] wrongConstructs = {"AOR", "ROR", "COR", "SOR", 
				"LOR", "ASR", "IOP", "IOR", "PMD", "PPD", "PCC",
				"PRV", "OMR", "OAC", "EOA", "EOC", "EAM", "EMM"};
		
		String[] extraneousConstructs = {"AOI", "COI", "LOI", "IHI", 
				"ISI", "PNC", "PCI", "JTI", "JSI", "JDC"};
		
		StringBuilder report = new StringBuilder();
		report.append("MissingConstructs").append("\t").append("WrongConstructs").
			append("\t").append("ExtraneousConstructs").append("\t").append("Total").append("\n");
		
		StringBuilder sql = new  StringBuilder();
		
		
		try {
			//1.get missing constructs
			sql.append("SELECT SUM( mutantNumber ) FROM mutant WHERE class LIKE \'%").append(className).append("%\'")
			.append(" AND function ").append("NOT LIKE \'%").append(methodName).append("%\' ").append("And (");		
			for(int i = 0; i < missingConstructs.length -1; i ++){
				sql.append("operator LIKE \'%").append(missingConstructs[i]).append("%\' or ");
			}
			sql.append("operator LIKE \'%").append(missingConstructs[missingConstructs.length -1]).append("%\')");		
			ResultSet rs = DatabaseManager.getInstance().query(sql.toString());
			while(rs.next()){
				report.append(rs.getInt(1)).append("\t");
			}		
			sql.setLength(0);
			
			//2.get wrong constructs
			sql.append("SELECT SUM( mutantNumber ) FROM mutant WHERE class LIKE \'%").append(className).append("%\'")
			.append(" AND function ").append("NOT LIKE \'%").append(methodName).append("%\' ").append("And (");	
			for(int i = 0; i < wrongConstructs.length -1; i ++){
				sql.append("operator LIKE \'%").append(wrongConstructs[i]).append("%\' or ");
			}
			sql.append("operator LIKE \'%").append(wrongConstructs[wrongConstructs.length -1]).append("%\')");		
			rs = DatabaseManager.getInstance().query(sql.toString());
			while(rs.next()){
				report.append(rs.getInt(1)).append("\t");
			}		
			sql.setLength(0);
			
			//3.get extraneous constructs
			sql.append("SELECT SUM( mutantNumber ) FROM mutant WHERE class LIKE \'%").append(className).append("%\'")
			.append(" AND function ").append("NOT LIKE \'%").append(methodName).append("%\' ").append("And (");	
			for(int i = 0; i < extraneousConstructs.length -1; i ++){
				sql.append("operator LIKE \'%").append(extraneousConstructs[i]).append("%\' or ");
			}
			sql.append("operator LIKE \'%").append(extraneousConstructs[extraneousConstructs.length -1]).append("%\')");		
			rs = DatabaseManager.getInstance().query(sql.toString());
			while(rs.next()){
				report.append(rs.getInt(1)).append("\t");
			}		
			sql.setLength(0);
			
			//4.get sum of these mutants;
			sql.append("SELECT SUM( mutantNumber ) FROM mutant WHERE class LIKE \'%").append(className).append("%\'")
			.append(" AND function ").append("NOT LIKE \'%").append(methodName).append("%\' ");
			rs = DatabaseManager.getInstance().query(sql.toString());
			while(rs.next()){
				report.append(rs.getInt(1)).append("\t");
			}		
			sql.setLength(0);			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(report.toString());
	}
	
	
	public static void main(String[] args) {
//		String mutantDir = "F:\\MyProgram\\eclipse3.3.1.1\\workspace\\TestingFramework\\result\\";
		String mutantDir = "F:\\MyProgram\\eclipse3.3.1.1\\workspace\\ContextDiversity\\result\\";		

		String saveFile = mutantDir + "statistics_ClassLevel.txt";
		MutantStatistics ins = new MutantStatistics();
		HashMap<String, HashMap<String, Integer>> classMutants = ins.getStatistics_ClassLevel(mutantDir);
		ins.saveToFile_ClassLevel(classMutants, saveFile);
//		ins.saveToDB_ClassLevel(classMutants);
		
		saveFile = mutantDir + "statistics_MethodLevel.txt";
		HashMap<String, HashMap<String, HashMap<String, Integer>>> methodMutants =ins.getStatistic_MethodLevel(mutantDir); 
		ins.saveToFile_MethodLevel(methodMutants, saveFile);
//		ins.saveToDB_MethodLevel(methodMutants);
		
		String className = "TestCFG2";
		String methodName = "application"; 
		ins.mutantSum_IncludeMethod(className, methodName);
		ins.mutantSum_ExcludeMethod(className, methodName);
	}

}
