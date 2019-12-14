import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ReportGenerator {

	public static void main(String[] args) throws Exception {

		String inputFile = args[0];
		String firstReport = args[1];
		String secondReport = args[2];

		printToFile(printFirstReport(parseDetails(readFile(inputFile))), firstReport, secondReport);
	}

	public static void printToFile(Map<String, List<String>> map, String firstReport, String secondReport)
			throws IOException {
		FileWriter wr = new FileWriter(firstReport);
		FileWriter wr1 = new FileWriter(secondReport);

		wr.write("Hour(CT),Export,Import,Total\n");
		wr1.write("Time(CT),Severity,Action,Duration\n");

		List<String> firstReportList = new ArrayList<String>();
		List<String> secondReportList = new ArrayList<String>();
		int totalImport = 0;
		int totalExport = 0;
		for (Entry<String, List<String>> detail : map.entrySet()) {
			int importCount = 0;
			int exportCount = 0;

			for (String p : detail.getValue()) {

				if (p.contains("Import")) {
					importCount++;
					totalImport++;
				} else if (p.contains("Export")) {
					exportCount++;
					totalExport++;
				}
				String[] list = p.split(",");
				String finalString = list[list.length - 1] + "," + list[2] + "," + list[3] + ","
						+ list[1].replace("ms", "");
				secondReportList.add(finalString);

			}
			String finalString = detail.getKey() + "," + exportCount + "," + importCount + ","
					+ detail.getValue().size();

			firstReportList.add(finalString);
		}
		// This will print the hourly data for First Report
		Collections.sort(firstReportList);
		for (String firstReportData : firstReportList) {
			wr.write(firstReportData);
			wr.write("\n");
		}
		// This will print the Second Report Data
		Collections.sort(secondReportList);
		for (String secondReportData : secondReportList) {
			wr1.write(secondReportData);
			wr1.write("\n");
		}
		// This will print the Last row of First Report
		String printTotalRow = "Total," + totalExport + "," + totalImport + "," + (totalExport + totalImport);
		wr.write(printTotalRow);
		// wr.close();
		wr.flush();
		wr1.close();
	}

	public static List<String> parseDetails(List<String> details) {
		Collections.sort(details);
		List<String> list = new ArrayList<String>();
		for (String detail : details) {
			String process = "";
			String[] data = detail.split(" ");

			if (detail.toLowerCase().contains("importing")) {
				process = "Import";
			} else if (detail.toLowerCase().contains("exporting")) {
				process = "Export";
			}

			list.add(detail.split(" ")[1].split(":")[0] + "," + detail.split(":")[detail.split(":").length - 1] + ","
					+ detail.split(" ")[3] + "," + process + "," + detail.split(" ")[1]);

		}
		Collections.sort(list);
		return list;
	}

	public static Map<String, List<String>> printFirstReport(List<String> input) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();

		for (String v : input) {
			String hour = v.split(",")[0];
			if (map.get(hour) != null) {
				map.get(hour).add(v);
			} else {
				List<String> list = new ArrayList<String>();
				list.add(v);
				map.put(hour, list);
			}
		}
		return map;
	}

	public static List<String> readFile(String inputFile) throws Exception {
		List<String> requiredDetails = new ArrayList<String>();
		FileReader fr = new FileReader(inputFile); // Creation of File Reader object
		BufferedReader br = new BufferedReader(fr); // Creation of BufferedReader object
		String s;
		String input1 = "exporting";
		String input2 = "importing";// Input word to be searched
		int count = 0; // Intialize the word to zero
		while ((s = br.readLine()) != null) // Reading Content from the file
		{
			if (s.contains("exporting") || s.contains("importing")) {
				// System.out.println(s);
				requiredDetails.add(s);
			}
		}

		fr.close();

		return requiredDetails;
	}

}
