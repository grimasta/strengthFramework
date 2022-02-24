package com.example.inj.model.sampling;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.example.inj.global.ProjectNameContainer;
import com.example.inj.model.storage.DataRepository;
import com.example.inj.model.strength.singlefile.ISingleFileStrength;

/*This class will insert the data into the excel. As we have different requirement for inserting into excel,
it is really helpful if we can have the logic separately
 */
public class InsertExcel {

	private CreateSample createSample;
	private CreateWidth createWidth;
	private ISingleFileStrength singleFileStrength;
	private DataRepository dataRepository;

	public InsertExcel() {
		dataRepository = DataRepository.getInstance();
	}

	/*
	 * We have used ApachePOI to insert into excel as it is quite fast as compared
	 * to normal I/O operation
	 */
	public void insertDataExcel() throws IOException {

		int slope = 0;
		int buggy = 0;
		int i = 1;
		Workbook workbook = null;
		workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet(ProjectNameContainer.PROJECT_NAME);
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("Segment ID");
		header.createCell(1).setCellValue("Segment Width");
		header.createCell(2).setCellValue("File ID");
		header.createCell(3).setCellValue("Start Commit ID");
		header.createCell(4).setCellValue("End Commit ID");
		header.createCell(5).setCellValue("Next Segment Buggy");
		header.createCell(6).setCellValue("Slope");
		header.createCell(7).setCellValue("File Commits in Segment");

		Map<String, List<List<Object>>> sampleVectors = dataRepository.getVectorsForExcel();

//        sampleVectors.entrySet().forEach(e->System.out.println(e));

		Map<String, Integer> segmentWidth = dataRepository.getSegmentWidth();
		for (String key : sampleVectors.keySet()) {
			List<List<Object>> vectorList = sampleVectors.get(key);
			int width = segmentWidth.get(key);
			for (List<Object> objLis : vectorList) {

				// vectorList.forEach(e->System.out.print(" " + e));

				i = i + 1;
				Row row = sheet.createRow(i);
				row.createCell(2).setCellValue(key); // File_Id
				row.createCell(3).setCellValue(objLis.get(1).toString()); // Start_CommitID

				row.createCell(4).setCellValue(objLis.get(7).toString()); // End_CommitId

				if ((Boolean) objLis.get(6)) {
					buggy = 1;
				} else {
					buggy = 0;
				}
				row.createCell(5).setCellValue(buggy);
				row.createCell(0).setCellValue("SID" + i);// SegmentId

				// Hard Coded the Slope value when the commit in the segment is greater then 2
				// || (int)objLis.get(2) > 2
				if (objLis.get(2).toString().equalsIgnoreCase("U")) {
					slope = -1;
				} else {
					slope = 1;
				}
				// row.createCell(7).setCellValue(objLis.get(2).toString()); // number of commit
				// in the segment
				row.createCell(6).setCellValue(slope); // Slope
				row.createCell(1).setCellValue(width); // Width Of Segment

			}
			i++;
		}
		// Slope writer
		FileOutputStream fileOut = new FileOutputStream(
				"results\\" + ProjectNameContainer.PROJECT_NAME + "_slope.xlsx");
		workbook.write(fileOut);
		fileOut.close();
		workbook.close();

	}

	/*
	 * This method will return the data associated with overallStrength into the
	 * excel. Created for Piyush Thesis. Called from Single File Strength
	 */
	public void insertOverallStrength() {
		Map<String, Map<String, Float>> strength = dataRepository.getFinalStrength();
		Map<String, String> dateToCommit = dataRepository.getDictionaryString();
		int sheetCount = 1;
		Workbook workbook = null;
		workbook = new XSSFWorkbook();
		System.out.println("Inside");
		Sheet sheet = workbook.createSheet(ProjectNameContainer.PROJECT_NAME + sheetCount);
		Row header = sheet.createRow(0);
		header.createCell(0).setCellValue("FileId");
		header.createCell(1).setCellValue("Date");
		header.createCell(2).setCellValue("CommitId");
		header.createCell(3).setCellValue("OverallStrengthSoFar");
		int i = 0;
		for (String key : strength.keySet()) {
			Map<String, Float> subStrength = strength.get(key);

			subStrength = subStrength.entrySet().stream().map(entry -> {
				if (entry.getValue() == null)
					entry.setValue(0.0f);
				return entry;
			}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
			for (String dat : subStrength.keySet()) {
				Float overallStrength = subStrength.get(dat);
				String commitID = dateToCommit.get(dat);
				i = i + 1;
				if (i > 1048575) {
					sheetCount++;
					sheet = workbook.createSheet(ProjectNameContainer.PROJECT_NAME + sheetCount);
					header = sheet.createRow(0);
					header.createCell(0).setCellValue("FileId");
					header.createCell(1).setCellValue("Date");
					header.createCell(2).setCellValue("CommitId");
					header.createCell(3).setCellValue("OverallStrengthSoFar");
					i = 0;
				}
				Row row = sheet.createRow(i);
				row.createCell(0).setCellValue(key); // File_ID
				row.createCell(1).setCellValue(dat); // Date
				row.createCell(2).setCellValue(commitID); // CommitID
				row.createCell(3).setCellValue(overallStrength);// OverallStrength

			}
			i++;

		}
		try {
			// Strength writer
			FileOutputStream fileOut = new FileOutputStream(
					"results\\" + ProjectNameContainer.PROJECT_NAME + "_strengths.xlsx");
			workbook.write(fileOut);
			fileOut.close();
			workbook.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Get Message");
		}

	}

	public CreateSample getCreateSample() {
		return createSample;
	}

	public void setCreateSample(CreateSample createSample) {
		this.createSample = createSample;
	}

	public CreateWidth getCreateWidth() {
		return createWidth;
	}

	public void setCreateWidth(CreateWidth createWidth) {
		this.createWidth = createWidth;
	}

	public ISingleFileStrength getSingleFileStrength() {
		return singleFileStrength;
	}

	public void setSingleFileStrength(ISingleFileStrength singleFileStrength) {
		this.singleFileStrength = singleFileStrength;
	}
}
