package org.example.quan_ao_f4k.controller.shopping_offline;

import com.itextpdf.text.pdf.BaseFont;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class PdfController {

    @Autowired
    TemplateEngine templateEngine;
    @GetMapping("/generate-pdff")
    @ResponseBody
    public void generatePdff(HttpServletResponse response, Model model) throws Exception {
        Context context = new Context();
        context.setVariables(model.asMap());
        String htmlContent = templateEngine.process("shopping_offline/orderPDF", context);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"output.pdf\"");

        ITextRenderer renderer = new ITextRenderer();

        // Thêm font hỗ trợ Unicode
        String fontPath = new ClassPathResource("fonts/Roboto-Regular.ttf").getPath();
        renderer.getFontResolver().addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        renderer.setDocumentFromString(htmlContent);
        renderer.layout();

        try (OutputStream os = response.getOutputStream()) {
            renderer.createPDF(os);
        }
    }


}