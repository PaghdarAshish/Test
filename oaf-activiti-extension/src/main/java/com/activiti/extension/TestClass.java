package com.activiti.extension;


import com.aspose.pdf.*;
import com.aspose.words.BuiltInDocumentProperties;
import com.aspose.words.ConvertUtil;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.NodeType;
import com.aspose.words.PageSetup;
import com.aspose.words.PaperSize;
import com.aspose.words.PreferredWidth;
import com.aspose.words.SaveFormat;
import org.activiti.engine.impl.util.json.JSONArray;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class TestClass {


    public static void main(String[] args) {
        generatingPDF();
    }

    public static void generatingPDF() {
        InputStream templateByteArrayInputStream = null;
        Document pdfDocument = null;
       

       // pdfDocument.di
        // converting ftlString into input stream
        try {
        	//PageSize ps=new com.aspose.pdf.generator.legacyxmlmodel.PageSize();
        	//Document pdfDocument = new Document(PageSize.getA4());
            byte[] bytes = FileUtils.readFileToByteArray(new File("C:\\Users\\user\\Documents\\pdfGeneratorOutput_23042019_0407.html"));


            templateByteArrayInputStream = new ByteArrayInputStream(bytes);
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream documentOutputStream = new ByteArrayOutputStream();	
        // generating PDF document form InputStream of FTL.
        try {
            pdfDocument = new Document(templateByteArrayInputStream);
           // pdfDocument.getse
           
         // Set page margin
            DocumentBuilder documentBuilder = new DocumentBuilder(pdfDocument);
            PageSetup pageSetup = documentBuilder.getPageSetup();
            pageSetup.setPaperSize(PaperSize.A4);
            pageSetup.setLeftMargin(ConvertUtil.inchToPoint(0.3));
            pageSetup.setRightMargin(ConvertUtil.inchToPoint(0.3));
            pageSetup.setTopMargin(ConvertUtil.inchToPoint(0.3));
            pageSetup.setBottomMargin(ConvertUtil.inchToPoint(0.3));

            for (com.aspose.words.Table table : (Iterable<com.aspose.words.Table>)pdfDocument.getChildNodes(NodeType.TABLE,true))
            {
            	table.setAllowAutoFit(false);
            	table.setPreferredWidth(PreferredWidth.fromPercent(100));
            	//table.autoFit(AutoFitBehavior.AUTO_FIT_TO_WINDOW);	
            }
            pdfDocument.updateTableLayout();
            
            pdfDocument.setAutomaticallyUpdateSyles(true);
            BuiltInDocumentProperties builtInDocumentProperties = pdfDocument.getBuiltInDocumentProperties();
            System.out.println("builtInDocumentProperties = " + builtInDocumentProperties);
        } catch (Exception e) {
        }

        // store document data into outputstream
        try {
            pdfDocument.save("E:\\Alfresco\\fromMain\\abc_" + System.currentTimeMillis() + ".pdf", SaveFormat.PDF);
//            pdfDocument.save(documentOutputStream, SaveFormat.DOCX);
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }

    }
}