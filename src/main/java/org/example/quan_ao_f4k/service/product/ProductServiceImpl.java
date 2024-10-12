package org.example.quan_ao_f4k.service.product;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.example.quan_ao_f4k.dto.request.product.ProductRequest;
import org.example.quan_ao_f4k.dto.response.product.ProductResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.product.ProductMapper;
import org.example.quan_ao_f4k.model.product.Product;
import org.example.quan_ao_f4k.repository.product.ProductRepository;
import org.example.quan_ao_f4k.util.SearchFields;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService{
    private ProductMapper productMapper;
    private ProductRepository productRepository;

    @Override
    public ListResponse<ProductResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
        return defaultFindAll(page, size, sort, filter, search, all, SearchFields.PRODUCT, productRepository, productMapper);

    }

    @Override
    public ProductResponse findById(Long aLong) {
        return defaultFindById(aLong,productRepository, productMapper,"");
    }

    @Override
    public ProductResponse save(ProductRequest request) {
        return defaultSave(request, productRepository, productMapper);
    }

    @Override
    public ProductResponse save(Long aLong, ProductRequest request) {
        return defaultSave(aLong,request,productRepository,productMapper,"");
    }

    @Override
    public void delete(Long aLong) {
        productRepository.deleteById(aLong);

    }

    @Override
    public void delete(List<Long> longs) {
        productRepository.deleteAllById(longs);

    }

    @Override
    public ProductResponse findByName(String name) {
        return findByName(name);
    }

    @Override
    public void updateStatus(Long id, int status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tồn tại đối tượng"));
        product.setStatus(status);
        productRepository.save(product);
    }

    @Override
    public void exportExcel(HttpServletResponse response) throws Exception {
        List<Product> products = productRepository.findAll();

        // Đặt loại nội dung và tiêu đề cho response
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=DanhSachSanPham.xlsx");

        // Tạo workbook và sheet
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Danh sách sản phẩm");

        // Tạo font hỗ trợ tiếng Việt (sử dụng font Arial Unicode MS)
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 12);

        // Tạo style cho tiêu đề bảng
        XSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(font);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        // Tạo style cho dữ liệu trong bảng
        XSSFCellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setFont(font);
        dataStyle.setAlignment(HorizontalAlignment.LEFT);

        // Tạo hàng tiêu đề
        XSSFRow headerRow = sheet.createRow(3);
        XSSFCell cell;

        cell = headerRow.createCell(0);
        cell.setCellValue("STT");
        cell.setCellStyle(headerStyle);

        cell = headerRow.createCell(1);
        cell.setCellValue("Tên");
        cell.setCellStyle(headerStyle);

        cell = headerRow.createCell(2);
        cell.setCellValue("Danh mục");
        cell.setCellStyle(headerStyle);

        cell = headerRow.createCell(3);
        cell.setCellValue("Thương hiệu");
        cell.setCellStyle(headerStyle);

        cell = headerRow.createCell(4);
        cell.setCellValue("Hình ảnh");
        cell.setCellStyle(headerStyle);

        cell = headerRow.createCell(5);
        cell.setCellValue("Mô tả");
        cell.setCellStyle(headerStyle);


        // Bắt đầu thêm dữ liệu từ dòng thứ 3
        int dataRowIndex = 4;
        int stt = 1;

        for (Product product : products) {
            XSSFRow dataRow = sheet.createRow(dataRowIndex++);

            // Ô STT
            cell = dataRow.createCell(0);
            cell.setCellValue(stt++);
            cell.setCellStyle(dataStyle);

            // Ô Tên sản phẩm
            cell = dataRow.createCell(1);
            cell.setCellValue(product.getName());
            cell.setCellStyle(dataStyle);

            // Ô tên danh mục
            cell = dataRow.createCell(2);
            cell.setCellValue(product.getCategory().getName());
            cell.setCellStyle(dataStyle);

            // Ô tên thương hiệu
            cell = dataRow.createCell(3);
            cell.setCellValue(product.getBrand().getName());
            cell.setCellStyle(dataStyle);

            // Ô hình ảnh
            cell = dataRow.createCell(4);
            cell.setCellValue(product.getThumbnail());
            cell.setCellStyle(dataStyle);

            // Ô mô tả
            cell = dataRow.createCell(5);
            cell.setCellValue(product.getDescription());
            cell.setCellStyle(dataStyle);
        }

        // Tự động điều chỉnh độ rộng cột
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        // Ghi workbook vào response
        ServletOutputStream ops = response.getOutputStream();
        workbook.write(ops);
        workbook.close();
        ops.close();
    }


    @Override
    public void exportPdf(HttpServletResponse response) throws Exception {
        List<Product> products = productRepository.findAll();

        // Thiết lập loại nội dung cho response là PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=DanhSach.pdf");

        // Tạo tài liệu PDF
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        // Mở tài liệu để ghi dữ liệu
        document.open();

        // Đường dẫn đến font Roboto-Regular trên máy của cá nhân
        String fontPath = "C:/Users/phuc/Máy tính/datn/QUAN_AO_F4K/QUAN_AO_F4K/src/main/resources/static/admin/fonts/RobotoSlab-Bold.ttf";

        // Thêm font Roboto vào tài liệu PDF
        BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font fontTitle = new Font(baseFont, 16, Font.BOLD);
        Font fontHeader = new Font(baseFont, 12, Font.BOLD);
        Font fontCell = new Font(baseFont, 12);
        // Thêm tiêu đề
        Paragraph title = new Paragraph("Danh sách", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Thêm khoảng cách sau tiêu đề
        document.add(new Paragraph(" "));

        // Tạo bảng với 6 cột
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);

        // Đặt chiều rộng các cột
        float[] columnWidths = {1f,4f,4f,4f,6f,8f};
        table.setWidths(columnWidths);

        // Thêm tiêu đề cho các cột
        PdfPCell cell;

        cell = new PdfPCell(new Phrase("STT", fontHeader));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Tên", fontHeader));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Danh mục", fontHeader));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Thương hiệu", fontHeader));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Hình ảnh", fontHeader));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Mô tả", fontHeader));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        // Thêm dữ liệu cho các dòng
        int stt = 1;
        for (Product product : products) {
            // Ô STT
            cell = new PdfPCell(new Phrase(String.valueOf(stt++), fontCell));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            // Ô Tên sp
            cell = new PdfPCell(new Phrase(product.getName(), fontCell));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(product.getCategory().getName(), fontCell));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(product.getBrand().getName(), fontCell));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(product.getThumbnail(), fontCell));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(product.getDescription(), fontCell));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
        }

        // Thêm bảng vào tài liệu PDF
        document.add(table);

        // Đóng tài liệu
        document.close();
    }

}
