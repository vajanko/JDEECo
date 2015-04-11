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
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;

/**
 * 
 * @author Ondrej Kov·Ë <info@vajanko.me>
 */
public class AppRunner {
	
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
	
	public static void main(String[] args) throws IOException, InterruptedException {
		Locale.setDefault(Locale.getDefault());		// WTF??
		Runtime runTime = Runtime.getRuntime();
		
		String classPath = System.getProperty("java.class.path");
		String mainClass = "cz.cuni.mff.d3s.deeco.measurement.MatsimOmnetTest";
		String logDir = "logs/tmp/";
		
		// delete content of tmp directory
		clearDir(logDir);
		
		for (int i = 0; i < 1; ++i) {
			
			Double prob = 0.1 * i;
			String logFile = logDir + "request" + i + ".csv";
			String appArgs = String.format("%f %s", prob, logFile);
			
			String cmd = String.format("java -classpath %s %s %s", classPath, mainClass, appArgs);
			Process process = runTime.exec(cmd);
			
			InputStream inputStream = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(inputStream);
			int n1;
			char[] c1 = new char[1024];
			StringBuffer standardOutput = new StringBuffer();
				
			while ((n1 = isr.read(c1)) > 0) {
				standardOutput.append(c1, 0, n1);
				if (standardOutput.length() >= 1024) {
					System.out.println(standardOutput.toString());
					standardOutput.delete(0, standardOutput.length());
				}
			}
			System.out.println(standardOutput.toString());
			
			
			InputStream errorStream = process.getErrorStream();
			InputStreamReader esr = new InputStreamReader(errorStream);
			int n2;
			char[] c2 = new char[1024];
			StringBuffer standardError = new StringBuffer();
			while ((n2 = esr.read(c2)) > 0) {
				standardError.append(c2, 0, n2);
			}
			System.out.println("Standard Error: " + standardError.toString());
			
			process.waitFor();
			System.out.println("Process finised!");
		}
		
		String outFile = logDir + "../request.csv";
		mergeLogFiles(logDir, outFile);
	}
	
}
