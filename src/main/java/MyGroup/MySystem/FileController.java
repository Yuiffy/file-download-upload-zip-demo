package MyGroup.MySystem;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.management.FileSystem;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by yuiff on 2017/1/13.
 */
@Controller
@RequestMapping("/file")
public class FileController {
    @RequestMapping("")
    String mainPage() {
        return "file/index";
    }

    @RequestMapping(value = "/test1.xls")
    @ResponseBody
    FileSystemResource testDown1(){
        final String excelDirString = "./workfiles/excel2others/excelinput/";
        File excelDir = new File(excelDirString);
        File[] excelFiles = excelDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xls");
            }
        });
        if (excelFiles == null || excelFiles.length == 0)
            System.out.println(excelDir.getAbsolutePath() + " have not .xls files in it!");
        return new FileSystemResource(excelFiles[0]);
    }


    @RequestMapping(value = "/test2.xlsx", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    byte[] testDown() throws IOException, InvalidFormatException {
        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        for (int i = 0; i < 60000; i++) {
            Row newRow = sheet.createRow(i);
            for (int j = 0; j < 100; j++) {
                newRow.createCell(j).setCellValue("test" + Math.random());
            }
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        workbook.write(os);
        byte[] bytes = os.toByteArray();
        return bytes;
    }

    @RequestMapping(value = "/test3.zip")
    @ResponseBody
    byte[] testDownZip() throws IOException {
        File dir = new File("./workfiles/excel2others/excelinput/");
        File[] excelFiles = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                System.out.println(dir+" "+name);
                return name.endsWith(".xls");
            }
        });
        if (excelFiles == null || excelFiles.length == 0)
            System.out.println(dir.getAbsolutePath() + " have not .xls files in it!");
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ZipOutputStream zipOut= new ZipOutputStream(bo);
        for(File xlsFile:excelFiles){
            if(xlsFile.isDirectory())continue;
            ZipEntry zipEntry = new ZipEntry(xlsFile.getName());
            zipOut.putNextEntry(zipEntry);
            zipOut.write(IOUtils.toByteArray(new FileInputStream(xlsFile)));
            zipOut.closeEntry();
        }
        zipOut.close();
        return bo.toByteArray();
    }

    @RequestMapping(value = "/files.zip")
    @ResponseBody
    byte[] filesZip() throws IOException {
        File dir = new File("./");
        File[] filesArray = dir.listFiles();
        if (filesArray == null || filesArray.length == 0)
            System.out.println(dir.getAbsolutePath() + " have no file!");
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ZipOutputStream zipOut= new ZipOutputStream(bo);
        for(File xlsFile:filesArray){
            if(!xlsFile.isFile())continue;
            ZipEntry zipEntry = new ZipEntry(xlsFile.getName());
            zipOut.putNextEntry(zipEntry);
            zipOut.write(IOUtils.toByteArray(new FileInputStream(xlsFile)));
            zipOut.closeEntry();
        }
        zipOut.close();
        return bo.toByteArray();
    }
}
