/**
 * 
 */
package cz.cuni.mff.d3s.deeco.measurement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class AppRunner {
	
	private static final AtomicInteger seq = new AtomicInteger(1);
	private static int createId() {
		return seq.getAndIncrement();
	}
	
	// global properties
	private String classPath;
	private String workingDir;
	private String mainClass;
	private Runtime runTime;
	
	// run properties
	private int hdPeriod;
	private int plPeriod;
	private int knPeriod;
	private double probability;
	private long localTimeout;
	private long globalTimeout;
	private String logFile;
	private String features;
	private String variant;
	
	public AppRunner(String mainClass, String workingDir) {
		this.mainClass = mainClass;
		this.workingDir = workingDir;
		this.classPath = System.getProperty("java.class.path");
		this.runTime = Runtime.getRuntime();
	}
	
	
	private static void writeLines(BufferedWriter out, List<String> lines, int offset) throws IOException {
		for (int i = offset; i < lines.size(); ++i) {
			out.write(lines.get(i));
			out.newLine();
		}
	}
	private static void mergeLogFiles(String dir, String outFile) {
		File d = new File(dir);
		File[] files = d.listFiles();
		
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
		
			for (int i = 0; i < files.length; ++i) {
				List<String> lines = Files.readAllLines(files[i].toPath());
				writeLines(out, lines, i == 0 ? 0 : 1);
			}
			
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void clearDir(String dir) {
		File d = new File(dir);
		for (File f : d.listFiles()) {
			f.delete();
		}
	}
	
	private void createPropertiesFile(String outputFile) throws IOException {
		
		Path path = Paths.get("config/template.properties");
		Charset charset = StandardCharsets.UTF_8;
		
		String content = new String(Files.readAllBytes(path), charset);
		content = content.replaceFirst("\\$\\{hdPeriod\\}", String.valueOf(hdPeriod));
		content = content.replaceFirst("\\$\\{plPeriod\\}", String.valueOf(plPeriod));
		content = content.replaceFirst("\\$\\{knPeriod\\}", String.valueOf(knPeriod));
		content = content.replaceFirst("\\$\\{probability\\}", String.valueOf(probability));
		content = content.replaceFirst("\\$\\{localTimeout\\}", String.valueOf(localTimeout));
		content = content.replaceFirst("\\$\\{globalTimeout\\}", String.valueOf(globalTimeout));
		content = content.replaceFirst("\\$\\{logFile\\}", logFile);
		content = content.replaceFirst("\\$\\{features\\}", features);
		content = content.replaceFirst("\\$\\{variant\\}", variant);
		//content = content.replaceFirst("\\$\\{variant\\}", variant);
		
		Files.write(Paths.get(outputFile), content.getBytes(charset));
	}
	private void runConfiguration(int id) throws IOException, InterruptedException {
		
		this.logFile = this.workingDir + "log" + id + ".csv";
		
		String configFile = this.workingDir + "config" + id + ".properties";
		createPropertiesFile(configFile);
		
		String cmd = String.format("java -classpath %s %s %s", classPath, mainClass, configFile);
		Process process = runTime.exec(cmd);
		process.waitFor();
		
		System.out.println("Process " + id + "finished!");
	}
	public static void main(String[] args) throws IOException, InterruptedException {
		Locale.setDefault(Locale.getDefault());		// WTF??
		
		// delete content of tmp directory
		String logDir = "logs/tmp/";
		clearDir(logDir);
		
		AppRunner runner = new AppRunner("cz.cuni.mff.d3s.deeco.measurement.MatsimOmnetTest", logDir);
		
	
		for (int i = 0; i < 1; ++i) {
			
			int id = createId();
			runner.hdPeriod = 2000;
			runner.plPeriod = 2000;
			runner.knPeriod = 1000;			// this can be constant
			runner.probability = 0.1 * i;
			runner.localTimeout = 3000;
			runner.globalTimeout = 20000;
			//runner.logFile = logDir + "test" + id + ".csv";
			runner.variant = "runner-test";
			runner.features = "logger;push;pull";
			
			runner.runConfiguration(id);
			
//			InputStream inputStream = process.getInputStream();
//			InputStreamReader isr = new InputStreamReader(inputStream);
//			int n1;
//			char[] c1 = new char[1024];
//			StringBuffer standardOutput = new StringBuffer();
//				
//			while ((n1 = isr.read(c1)) > 0) {
//				standardOutput.append(c1, 0, n1);
//				if (standardOutput.length() >= 1024) {
//					System.out.println(standardOutput.toString());
//					standardOutput.delete(0, standardOutput.length());
//				}
//			}
//			System.out.println(standardOutput.toString());
//			
//			InputStream errorStream = process.getErrorStream();
//			InputStreamReader esr = new InputStreamReader(errorStream);
//			int n2;
//			char[] c2 = new char[1024];
//			StringBuffer standardError = new StringBuffer();
//			while ((n2 = esr.read(c2)) > 0) {
//				standardError.append(c2, 0, n2);
//			}
//			System.out.println("Standard Error: " + standardError.toString());
		}
		
		// finally at the end merge all files
		String outFile = logDir + "../demo.csv";
		//mergeLogFiles(logDir, outFile);
	}
	
}
