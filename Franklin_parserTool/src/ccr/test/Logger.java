package ccr.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Logger {

	private static Logger m_Logger;

	private String path;

	private static BufferedWriter writer = null;

	public static Logger getInstance() {
		if (m_Logger == null) {
			m_Logger = new Logger();
		}
		return m_Logger;
	}

	/**2009-10-30: check the directory and file before
	 * operating on them
	 * @param filename
	 * @param append:
	 *            false means override, true means append
	 */
	public void setPath(String filename, boolean append) {
		this.path = filename;
		
		try {
			File file = new File(filename);
			if(!file.exists()){
				File parent = file.getParentFile();
				if(!parent.exists()){
					parent.mkdirs();
				}
				file.createNewFile();
			}
			
			writer = new BufferedWriter(new FileWriter(path, append));
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void write(String log) {
		try {
			if (writer != null) {
				writer.write(log);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	
	
	

	public void deleteFile(String srcFile){
		File file = new File(srcFile);
		if(file.exists()){
			if(file.isDirectory()){				
				File[] files = file.listFiles();
				for(File temp: files)
					this.deleteFile(temp.getPath());
			}else{
				file.delete();
			}
		}
	}
	
	//2009/1/18:delete all files and directory in srcDir
	public void delete(String srcDir){
		
		File file = new File(srcDir);
		while(file.exists()){
			if(file.isDirectory()){				
				File[] files = file.listFiles();
				if(files.length == 0){//no directory and files in this directory
					file.delete();
				}else{
					for(File temp: files)
						this.delete(temp.getPath());	
				}
			}else{
				file.delete();
			}
			
		}
	}
	
	public static int duplicateFlag = 140;

	public void moveFiles(String srcDir, String destDir, String type){
		try {
			
			File src = new File(srcDir);
			if(src.exists()){
				File dest = new File(destDir);
				if(!dest.exists()){
					dest.mkdirs();
				}
				if(src.isFile()){
					BufferedReader br = new BufferedReader(new FileReader(src));
					String srcFile = src.getName();
					String srcType = srcFile.substring(srcFile.indexOf(".") + ".".length());
					
					if(srcType.equals(type)){
						//destFile: TourApp.java -> TourApp_1.java
						String destFile = srcFile.substring(0, srcFile.indexOf(".")) + "_"+duplicateFlag + srcFile.substring(srcFile.indexOf("."));
						duplicateFlag ++;
						BufferedWriter bw  = new BufferedWriter(new FileWriter(destDir + "\\" +  destFile));
						
						String line = null;
						StringBuilder sb = new StringBuilder();
						while((line = br.readLine())!= null){
							sb.append(line+"\n");					
						}
						bw.write(sb.toString());
						bw.close();
						br.close();
						
						String[] temp = src.getAbsolutePath().split("\\\\");
						String src_File = temp[temp.length-2];
						
						Logger.getInstance().setPath(destDir + "/MappingList.txt", true);
						Logger.getInstance().write(src_File + "\t" + destFile + "\n");
						Logger.getInstance().close();
					}
					
					
				}else if(!src.getName().equals("original")){//2009-12-30: exclude "original" directory
					File[] files = src.listFiles();
					for(File file: files){
						String path = file.getAbsolutePath();
						this.moveFiles(file.getPath(), destDir, type);
					}
				}
					
			}else{
				System.out.println("Folder " + srcDir + " does not exist");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	
	/**2009-03-16: change the package name
	 * 
	 * @param srcDir
	 * @param packageName: the desirable package name
	 */
	public void changePackage(String srcDir, String destDir, String packageName){
		try {
			File src = new File(srcDir);			
			if(src.exists()){
				if(src.isFile() && src.getName().contains(".java")){
					BufferedReader br = new BufferedReader(new FileReader(srcDir));
					String className = src.getName().substring(0, src.getName().indexOf("."));
					String line = null;
					StringBuilder sb = new StringBuilder();
					while((line = br.readLine())!= null){
						if(line.indexOf("package")!=-1){ //change the package						
							sb.append("package " + packageName + ";\n");
							sb.append("import ccr.app.*;");
//							sb.append("import context.test.contextIntensity.*;\n");
//							sb.append("import context.apps.Tour.*;\n");
						}else if(line.indexOf("public class")!= -1){//change the class name 
							int i = line.indexOf("public class") + "public class".length();
							i = line.indexOf(" ", i);
							int j = line.indexOf(" ", i + 1);
							
							sb.append(line.substring(0, i + 1) + className + line.substring(j) +  "\n");
						/*}else if(line.indexOf("TourApp")!= -1 && line.indexOf("TourAppFrame")==-1 && line.indexOf("context.apps.Tour.TourApp")==-1){							
							sb.append(line.replaceAll("TourApp", className) + "\n");
						}else if(line.indexOf("context.apps.Tour.TourApp")!=-1 ){
							if( line.indexOf("TourAppFrame")==-1){
								sb.append(line.replaceAll("context.apps.Tour.TourApp", packageName + "." + className)+ "\n");				
							}*/
						}else{
							sb.append(line + "\n");
						}
					}
					
//					String content = sb.toString();
//					content.replaceAll("TourApp", className);
					
					br.close();
					BufferedWriter bw =new BufferedWriter(new FileWriter(destDir));
					bw.write(sb.toString());
					bw.close();
				}else{
					File[] files = src.listFiles();
					for(File temp: files){
						if(temp.getName().contains(".java")){
//							destDir += "/" + temp.getName();
							String dest = destDir + "/" + temp.getName();
							this.changePackage(temp.getPath(), dest, packageName);	
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
	}
	
	/**2009-03-30: get the diff between two fault lists
	 * 
	 * @param srcDir
	 */
	public static void diffFaults(String srcDir){
		try {
			BufferedReader br = new BufferedReader(new FileReader(srcDir + "/FaultList.txt"));
			String line = null;
			ArrayList faultList = new ArrayList();
			while((line= br.readLine())!= null){
				faultList.add(line);
			}
			br.close();
			
			ArrayList clearList = new ArrayList();
			br = new BufferedReader(new FileReader(srcDir + "/ClearList.txt"));			
			while((line = br.readLine())!=null){
				clearList.add(line);
			}
			br.close();
			
//			ArrayList temp = new ArrayList();
//			for(int i = 0; i < clearList.size(); i ++){
//				String fault = (String)clearList.get(i);
//				if(!faultList.contains(fault)){
//					temp.add(fault);
//				}
//			}
			
			
			ArrayList temp = new ArrayList();
			for(int i = 0; i < faultList.size(); i ++){
				String fault = (String)faultList.get(i);
				if(!clearList.contains(fault))
					temp.add(fault);
			}
			
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < temp.size(); i ++){
				sb.append((String)temp.get(i)+"\n");
			}
			
			Logger.getInstance().setPath(srcDir + "/FaultList_noDuplicate.txt", false);
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

	public static void main(String[] args) {
		String srcDir = System.getProperty("user.dir") +"/result";
		String destDir = System.getProperty("user.dir") + "/MuJava";
//		String packageName = "a";
		String type = "java";
//		Logger.getInstance().delete(destDir);
//		Logger.getInstance().moveFiles(srcDir, destDir, type);
		
//		Logger.getInstance().changePackage(destDir, System.getProperty("user.dir") + "/temp/","ccr.app.testversion");
		
		srcDir =  "src/ccr/experiment/Context-Intensity_backup/TestHarness/20090313/";
		Logger.getInstance().diffFaults(srcDir);
	}
}
