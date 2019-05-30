package com.activiti.oaf.adapter;


import com.activiti.oaf.adapter.model.UIAttachment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestClass {

    public static void main(String[] args) throws IOException {
        String attachment1 = "[{\"docName\":\"A1----------WO0000000004821- Alfresco Integration-V3.docx\",\"category\":\"A\",\"guid\":\"005056B3023B1EE991C540CFB6FF505C\",\"date\":\"20190314\",\"downloadURL\":\" \"},{\"docName\":\"A2----------Functions CJ88.xls\",\"category\":\"A\",\"guid\":\"005056B3023B1EE991C9D56CCD2E58E7\",\"date\":\"20190314\",\"downloadURL\":\" \"},{\"docName\":\"B1----------FS-WO0000000004871-Trade Spend Additional Configuration.docx\",\"category\":\"B\",\"guid\":\"005056B3023B1EE991C9DAA81C721902\",\"date\":\"20190314\",\"downloadURL\":\" \"},{\"docName\":\"B2----------FS-WO0000000004871-Trade Spend Additional Configuration.docx\",\"category\":\"B\",\"guid\":\"005056B3023B1EE991CAC1EAF03D5AAB\",\"date\":\"20190314\",\"downloadURL\":\" \"},{\"docName\":\"C1----------FS-WO0000000004871-Trade Spend Additional Configuration.docx\",\"category\":\"C\",\"guid\":\"005056B3023B1EE991CAD3C5458D5AD2\",\"date\":\"20190314\",\"downloadURL\":\" \"},{\"docName\":\"C2----------WO0000000004821- Alfresco Integration-V3.docx\",\"category\":\"C\",\"guid\":\"005056B3023B1EE991CAD405CB4BDAD2\",\"date\":\"20190314\",\"downloadURL\":\" \"}]";


        String attachment2 = "[{\"docName\":\"B1----------FS-WO0000000004871-Trade Spend Additional Configuration.docx\",\"category\":\"B\",\"guid\":\"005056B3023B1EE991C9DAA81C721902\",\"date\":\"20190314\",\"downloadURL\":\"CDE\"},{\"docName\":\"B2----------FS-WO0000000004871-Trade Spend Additional Configuration.docx\",\"category\":\"B\",\"guid\":\"005056B3023B1EE991CAC1EAF03D5AAB\",\"date\":\"20190314\",\"downloadURL\":\"DEF\"},{\"docName\":\"C1----------FS-WO0000000004871-Trade Spend Additional Configuration.docx\",\"category\":\"C\",\"guid\":\"005056B3023B1EE991CAD3C5458D5AD2\",\"date\":\"20190314\",\"downloadURL\":\"EFG\"},{\"docName\":\"C2----------WO0000000004821- Alfresco Integration-V3.docx\",\"category\":\"C\",\"guid\":\"005056B3023B1EE991CAD405CB4BDAD2\",\"date\":\"20190314\",\"downloadURL\":\"FGH\"},{\"docName\":\"A1----------WO0000000004821- Alfresco Integration-V3.docx\",\"category\":\"A\",\"guid\":\"005056B3023B1EE991C540CFB6FF505C\",\"date\":\"20190314\",\"downloadURL\":\"ABC\"},{\"docName\":\"A2----------Functions CJ88.xls\",\"category\":\"A\",\"guid\":\"005056B3023B1EE991C9D56CCD2E58E7\",\"date\":\"20190314\",\"downloadURL\":\"BCD\"}]";


        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<List<UIAttachment>> typeReference = new TypeReference<List<UIAttachment>>() {
        };
        List<UIAttachment> att1 = objectMapper.readValue(attachment1, typeReference);
        List<UIAttachment> att2 = objectMapper.readValue(attachment2, typeReference);


        System.out.println("attachment1.equals(attachment2) = " + attachment1.equals(attachment2));
        System.out.println("att1.equals(att2) = " + att1.equals(att2));

//        boolean match = att1.stream().allMatch(num -> att2.contains(num));
        boolean match = att2.containsAll(att1);
        System.out.println("match = " + match);



//        System.out.println("inq1.equals(inq2) = " + inq1.equals(inq2));
    }
}