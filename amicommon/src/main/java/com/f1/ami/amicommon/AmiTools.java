package com.f1.ami.amicommon;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.f1.utils.IOH;
import com.f1.utils.SH;
import com.f1.utils.encrypt.EncrypterTool;

public class AmiTools {
	public static void main(String[] args) {
		if (args.length < 1) {
			printAllUsagesAndExit();
			return;
		}
		// combine EncrypterTool functionality
		if (EncrypterTool.handle(args)) {
			System.exit(0);
			return;
		}
		if (args.length < 2) {
			printErrorAndExit("--migrate option expects file path");
		}
		String cmd = args[0];
		if ("--migrate".equalsIgnoreCase(cmd)) {
			for (int i = 1; i < args.length; i++) {
				// fix each file
				String path = args[i];
				if (SH.is(path)) {
					File f = new File(path);
					if (!f.exists()) {
						System.err.println(path + " does not exists, skipping...");
						continue;
					}
					fixFile(f);
				}
			}
		} else {
			printUsageAndExit();
		}
	}

	public static void printUsageAndExit() {
		System.err.println("This command is used for supporting backwards compatibility with use ds option. E.g. use ds=myDs becomes use ds= \"myDs\"");
		System.err.println();
		System.err.println("Usage: ");
		System.err.println("   --migrate file1 file2...");
		System.exit(1);
	}

	public static void printAllUsagesAndExit() {
		System.err.println("This tool is used for");
		System.err.println("1. supporting backwards compatibility with use ds option. E.g. use ds=myDs becomes use ds= \"myDs\"");
		System.err.println("2. managing AES encrypted tokens using a file based encyrption key");
		System.exit(1);
	}

	private static void printErrorAndExit(String string) {
		System.err.println(string);
		System.exit(1);
	}

	private static void fixFile(File f) {
		if (!validateExtension(f.getName())) {
			return;
		}
		try {
			String txt = IOH.readText(f);
			if (!SH.is(txt))
				return;
			List<String> sqlTxt = SH.splitToList("\r", txt);
			if (AmiUtils.fixUseDs(sqlTxt)) {
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				String fname = f.getName();
				String formatted = df.format(new Date());
				// rename existing file to file_date, as backup
				// e.g. myFile.sql -> myFile_20240916.sql
				String newName = SH.splice(fname, fname.lastIndexOf('.'), 0, "_" + formatted);
				IOH.renameFile(f, newName);
				//				write to original file
				IOH.writeText(new File(f.getAbsolutePath()), SH.j('\r', sqlTxt));
				System.out.println("fixed " + f.getName());
			} else {
				System.out.println("No change needed:  " + f.getName());
			}
		} catch (IOException e) {
			System.err.println("Error while fixing " + f.getName() + ":" + e.getMessage());
		}
	}

	private static boolean validateExtension(String fileName) {
		String extension = SH.afterLast(fileName, '.');
		if (!"amisql".contentEquals(extension) && !"sql".contentEquals(extension)) {
			// ignore non-sql files
			return false;
		}
		return true;
	}
}
