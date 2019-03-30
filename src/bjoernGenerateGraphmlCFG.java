import java.io.BufferedReader;
import java.io.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.script.ScriptException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class bjoernGenerateGraphmlCFG {

	public static final String configPath = "config/binary_project.conf";
	public static String testFolder;  
        public static String bjoernJar;  
        public static String bjoern_radareFolder; 
	public static String orient_db;
	public static String localBin;     
	public static void readConfig() throws IOException {
                //ClassLoader classLoader = FeatureCalculators.class.getClass().getClassLoader();
                File file = new File(configPath);
                System.out.println(file.getAbsolutePath());
                //System.out.println(classLoader.getResource(configPath));
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                //BufferedReader reader = new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream(configPath)));
                String line = reader.readLine();
                String parts[];
                while(line != null) {
                        parts = line.split(" = ", 2);
                        switch(parts[0]) {
                                case "testFolder":
                                        testFolder = parts[1];
                                        break;
                                case "bjoernJar":
                                        bjoernJar = parts[1];
                                        break;
                                case "bjoern-radare":
                                        bjoern_radareFolder = parts[1];
                                        break;
				case "orient_db":
                                        orient_db = parts[1];
                                        break;
				case "localBin":
                                        localBin = parts[1];
                                        break;
                                default:
                                        //System.err.println("Invalid option: " + parts[0]);
                                        break;
                        }
                        line = reader.readLine();
                }
                reader.close();
            }
	
	public static void main(String[] args) throws IOException, InterruptedException, ScriptException{
		readConfig();
		String folderToProcess = testFolder;
		//Start server for once
		//./Users/Aylin/git/bjoern-radare/bjoern-server.sh
		// orientdb-community-2.1.5/bin/server.sh
		

				 		 
		//make sure to disable the local cache in orientdb-community-2.1.5/bin/server.sh
		//ORIENTDB_SETTINGS=-Dcache.level1.enabled=false
		//-Dcache.local.enabled=false
		//the server keeps running in the background
		
		List binary_paths = Util.listBinaryFiles(folderToProcess);
		int noFiles = binary_paths.size();
		int modVal = 9;   //might be number of authors
		int loopNo = noFiles % modVal;
		int iterations= noFiles/modVal;
		if (loopNo > 0){
			iterations=iterations+1;
		}
		for(int i=0; i<iterations; i++){
			  try
			  {
				  Process runScript;
					Runtime run = Runtime.getRuntime();
					String cmd =// "cd /Users/Aylin/git/bjoern-radare/ "+";"+
							//"echo startingOrientDB ;"+
						//	"export PATH=$PATH:/usr/local/bin/ ;"+
					//		"rm -rf /Users/Aylin/git/bjoern-radare/orientdb-community-2.1.5/databases/ ;"+
							"/bin/bash " + orient_db.trim() + "/bin/server.sh "	 
							;
					 System.out.println(cmd);
					 runScript = run.exec(new String[]{"/bin/bash","-c",cmd});
					 runScript.waitFor(2, TimeUnit.SECONDS);
/*					 BufferedReader br = new BufferedReader(new InputStreamReader(runScript.getInputStream()));
					 while(br.ready())
					 {	System.out.println("Start server input str"+br.readLine());}
					 br.close();		
					 br = new BufferedReader(new InputStreamReader(runScript.getErrorStream()));
					 while(br.ready())
					{  System.out.println("Start server error stream:"+br.readLine());}
					 br.close();*/
				//	 runScript.getInputStream().close();
				//	 runScript.getErrorStream().close();

					 


					 
			for(int i1=0; i1< modVal-1; i1++){
				
		File bin= new File(binary_paths.get(i1+(i*modVal)).toString());
		 File cfgFolder= new File(binary_paths.get(i1+(i*modVal)).toString()+"_bjoernDisassembly" + File.separator +bin.getName()+"CFG"+File.separator);
		 if((cfgFolder.exists() == false) || (FileUtils.sizeOfDirectory(cfgFolder) < 1)){
	System.out.println(cfgFolder.getPath());
	bjoernGenerateCFG(binary_paths.get(i1+(i*modVal)).toString());

}}

		
			Thread.sleep(1000);
			
			for(int i1=0; i1< modVal-1; i1++){
				
				File bin= new File(binary_paths.get(i1+(i*modVal)).toString());
				 File cfgFolder= new File(binary_paths.get(i1+(i*modVal)).toString() + "_bjoernDisassembly" + File.separator +bin.getName()+"CFG"+File.separator);
				 if((cfgFolder.exists() == false) || (FileUtils.sizeOfDirectory(cfgFolder) < 1)){
			dumpCFG(binary_paths.get(i1+(i*modVal)).toString());
					 }
			}
			
			

			Runtime run1=	Runtime.getRuntime();
		Process stopScript = run1.exec(new String[]{"/bin/bash", "-c",
				    "/bin/bash "+orient_db+"/bin/shutdown.sh"	 					 });
		 stopScript.waitFor();
		 BufferedReader br2 = new BufferedReader(new InputStreamReader(stopScript.getInputStream()));
			 while(br2.ready())
			 {	System.out.println("Server shutdown input str:"+br2.readLine());}
			 br2.close();
/*			  br2 = new BufferedReader(new InputStreamReader(stopScript.getErrorStream()));
			 while(br2.ready())
			 {	System.out.println("Server shutdown error str:"+br2.readLine());
			 }
			 
			 br2.close();*/
			 int exitCode = stopScript.exitValue();
			 System.out.println("Process shutdown exit code: " + exitCode);
				
			  
			  }
			  catch(IOException e)
			  {		      
			      e.printStackTrace();
			      System.out.print("RUN EXP");
			  } 

		     
	
		}
		
		

/*		//kill server
		Runtime run = Runtime.getRuntime();
		Process runScript = run.exec(new String[]{"/bin/bash", "-c",
				    "/bin/bash /Users/Aylin/git/bjoern-radare/orientdb-community-2.1.5/bin/shutdown.sh"	 
				 });
		 runScript.waitFor();
		  BufferedReader br = new BufferedReader(new InputStreamReader(runScript.getInputStream()));
		 while(br.ready())
		 {	System.out.println(br.readLine());}
		 br.close();
		 //t.stop();
		 int exitCode = runScript.exitValue();
		 System.out.println("Process shutdown exit code: " + exitCode);
		 t.destroy();*/
	}
	
	public static void bjoernGenerateCFG(String filePath) throws IOException, InterruptedException, ScriptException{
		//disassembles binary in filePath with bjoern-radare to outdir 
		//generates the cfg databases
		File processing = new File(filePath);
		String path = FilenameUtils.getPath(filePath);
		String filename = processing.getName();
		String outdirTMP = File.separator + path + filename +"_bjoernDisassembly" + File.separator +"bjoernCFG"+File.separator;
		String dbName = filename +"CFG" ;
		File outputTMP = new File(outdirTMP);
		outputTMP.mkdir();
		String cfgDBFolder = orient_db + "/databases/"
				+ dbName;
		File cfgDB = new File (cfgDBFolder);
		if(cfgDB.exists()){
		FileUtils.deleteDirectory(cfgDB); //delete if previous version of DB exists
		}
		 
         System.out.println("outdir: "+outdirTMP);
         System.out.println("filepath: "+filePath);
         System.out.println("path: "+path);


			 String bjoern_radare_tmp=  "#!/bin/sh" +"\n"+
			 "export PATH=$PATH:"  + localBin + " \n"+
					 "java -cp "
		    	   		+ bjoern_radareFolder.trim()+ "/bin/bjoern.jar:"+ localBin + " "
		    	   		+ "exporters.radare.RadareExporterMain "
		    	   		+ filePath+" "+ " -outdir "+outdirTMP +"\n";

			
			 PrintWriter writer = new PrintWriter(bjoern_radareFolder.trim() + "/bjoern-radare2.sh");
			 writer.println(bjoern_radare_tmp);
			 writer.close();


			 Runtime run = Runtime.getRuntime();
			 Process fileProcess = run.exec(new String[]{"/bin/sh", "-c",
			      	//	"curl --user root:admin -X DELETE http://localhost:2480/database/"+dbName+" ;"+
					  "chmod 777 " + bjoern_radareFolder.trim() + "/bjoern-radare2.sh"

					 });
				       fileProcess.waitFor();
				        BufferedReader br = new BufferedReader(new InputStreamReader(fileProcess.getInputStream()));
				       while(br.ready())
				           System.out.println("Delete if db there input:"+br.readLine());
				       br.close();
				        br = new BufferedReader(new InputStreamReader(fileProcess.getErrorStream()));
				       while(br.ready())
				           System.out.println("Delete if db there error:"+br.readLine());
				       br.close();
		
			Runtime runBjoern = Runtime.getRuntime();
			Process runScript = runBjoern.exec(new String[]{"/bin/bash", "-c",
				"cd " +bjoern_radareFolder.trim() + " ;"
			    +"/bin/bash " + bjoern_radareFolder.trim() + "/bjoern-radare2.sh ;"+
			    "tail -n+2 "+outdirTMP+"nodes.csv | sort -r | uniq > "+ outdirTMP +"nodes.csv_ ;"+
			    "tail -n+2 "+outdirTMP+"edges.csv | sort -r | uniq > "+outdirTMP+"edges.csv_ ;"+

			    "head -n 1 "+outdirTMP+"nodes.csv > "+outdirTMP+"nodeHead.csv ;"+
			    "head -n 1 "+outdirTMP+"edges.csv > "+outdirTMP+"edgeHead.csv ;"+

			    "cat "+outdirTMP+"nodeHead.csv >  nodes.csv ;"+
			    "cat "+outdirTMP+"nodes.csv_ >> nodes.csv ;"+

			    "cat "+outdirTMP+"edgeHead.csv > edges.csv ;"+
			    "cat "+outdirTMP+"edges.csv_ >> edges.csv ;"+
			    
			    "/bin/bash "+ bjoern_radareFolder.trim() + "/bjoern-csvimport.sh -dbname "+ dbName+" ;" 
/*		    "export PATH=$PATH:/usr/local/bin/ ;"+   
			"java -cp /Users/Aylin/git/bjoern-radare/bin/bjoern.jar:/usr/local/bin/ "
			+ "clients.bjoernImport.BjoernImport -dbname "+dbName + " ;"*/

			 });

			 runScript.waitFor(); 
			 System.out.println(filename +": importing binary");
			 br = new BufferedReader(new InputStreamReader(runScript.getInputStream()));
		     while(br.ready()) {System.out.println("import input stream:"+br.readLine());}
		     br.close();
		     br = new BufferedReader(new InputStreamReader(runScript.getErrorStream()));
		     while(br.ready()) {System.out.println("import error stream:"+br.readLine());}
		     br.close();
			int exitCode = runScript.exitValue();
			System.out.println("Process import exit code: " + exitCode); 

			    
	}
	
	
	public static void dumpCFG(String filePath) throws IOException, InterruptedException, ScriptException{
		//generates the cfgs in outdir
		File processing = new File(filePath);
		String path = FilenameUtils.getPath(filePath);
		String filename = processing.getName();
		String dbName = filename +"CFG" ;
		String outdir = File.separator + path + filename +"_bjoernDisassembly" + File.separator + dbName;
		String outdirTMP = File.separator + path + filename +"_bjoernDisassembly" + File.separator +"bjoernCFG"+File.separator;
	//	 File outputTMP = new File(outdirTMP);
	//	 outputTMP.mkdir();
		
		File outdirDB = new File (outdir);
		if(outdirDB.exists()){
			FileUtils.deleteDirectory(outdirDB); //delete if previous version of DB exists
		}

		String cfgDBFolder = orient_db.trim() + "/databases/"
					+ dbName;
		
		Runtime dumpTime = Runtime.getRuntime();
	    Process dumpCFG = dumpTime.exec(new String[]{"/bin/bash", "-c",
	    //	"curl -v http://localhost:2480/disconnect ;"+  
	    		 "curl http://localhost:2480/dumpcfg/"+ dbName + " ;"
				 });
				 
		 System.out.println(filename +": dumping cfg");
		 dumpCFG.waitFor();

	     BufferedReader br = new BufferedReader(new InputStreamReader(dumpCFG.getInputStream()));
	    while(br.ready())     {System.out.println("cfgDump input stream:"+br.readLine());} 
	    br.close();
	    br = new BufferedReader(new InputStreamReader(dumpCFG.getErrorStream()));
	    while(br.ready())     {System.out.println("cfgDump error stream:"+br.readLine());} 
	    br.close();
		int exitCode = dumpCFG.exitValue();
		System.out.println("Process dumpCFG exit code: " + exitCode); 
	    
		 Runtime cpTime = Runtime.getRuntime();
	     Process cpCFG = cpTime.exec(new String[]{"/bin/bash", "-c",
	    			"cp -r "+ bjoern_radareFolder.trim() +"/dump/cfg/"+dbName +File.separator+  " " + outdir + " ;"
				   + "rm -r "+ bjoern_radareFolder.trim() +"/dump/cfg/"+ dbName+File.separator+" ;"
		      		+"curl --user root:admin -X DELETE http://localhost:2480/database/"+dbName+" ;"//delete db from orientDB
		      	//	+"rm -r "+ cfgDBFolder + File.separator
	     });
		 
		 System.out.println(filename +": copying cfg");
		 cpCFG.waitFor();
	//	 int exitCode = runScript.exitValue();
	//	 System.out.println("Process exit code: " + exitCode); 
		 BufferedReader br1 = new BufferedReader(new InputStreamReader(cpCFG.getInputStream()));
		 while(br1.ready())     {System.out.println("cfgCopy input stream:"+br1.readLine());} 
		 br1.close();
		 br1 = new BufferedReader(new InputStreamReader(cpCFG.getErrorStream()));
		 while(br1.ready())     {System.out.println("cfgCopy error stream:"+br1.readLine());} 
		 br1.close();	   
		 exitCode = cpCFG.exitValue();
		System.out.println("Process cfgCopy exit code: " + exitCode); 

		File cfgDB = new File (cfgDBFolder);
		if(cfgDB.exists()){
			FileUtils.deleteDirectory(cfgDB); //delete if previous version exists
		}
		
		File tmpFolder = new File (outdirTMP);
		if(tmpFolder.exists()){
			FileUtils.deleteDirectory(tmpFolder); //delete if previous version exists
			}
				 
	}
}
