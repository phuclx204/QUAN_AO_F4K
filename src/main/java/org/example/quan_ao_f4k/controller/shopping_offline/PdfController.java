package org.example.quan_ao_f4k.controller.shopping_offline;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
        // Tạo dữ liệu cho template Thymeleaf
        Context context = new Context();
        context.setVariables(model.asMap());
        String htmlContent = templateEngine.process("shopping_offline/orderPDF", context);
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

        String currentDateTime = dateFormatter.format(new Date());
        String output = dateFormatter.format(new Date());

        // Cấu hình header cho response để tải PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"output.pdf\"");

        // Sử dụng Flying Saucer để chuyển HTML thành PDF
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();

        // Ghi PDF vào OutputStream (browser)
        try (OutputStream os = response.getOutputStream()) {
            renderer.createPDF(os);
        }
    }

}