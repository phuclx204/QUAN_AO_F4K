package org.example.quan_ao_f4k.service.product;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.example.quan_ao_f4k.dto.request.product.BrandRequest;
import org.example.quan_ao_f4k.dto.response.product.BrandResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.product.BrandMapper;
import org.example.quan_ao_f4k.model.product.Brand;
import org.example.quan_ao_f4k.repository.product.BrandRepository;
import org.example.quan_ao_f4k.util.SearchFields;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BrandServiceImpl implements BrandService {
    private BrandMapper brandMapper;
    private BrandRepository brandRepository;

    @Override
    public ListResponse<BrandResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
        return defaultFindAll(page, size, sort, filter, search, all, SearchFields.BRAND, brandRepository, brandMapper);

    }

    @Override
    public BrandResponse findById(Long aLong) {
        return findById(aLong);
    }

    @Override
    public BrandResponse save(BrandRequest request) {
        return defaultSave(request, brandRepository, brandMapper);
    }

    @Override
    public BrandResponse save(Long aLong, BrandRequest request) {
        return defaultSave(aLong, request, brandRepository, brandMapper, "");
    }

    @Override
    public void delete(Long aLong) {
        brandRepository.deleteById(aLong);

    }

    @Override
    public void delete(List<Long> longs) {
        brandRepository.deleteAllById(longs);

    }

    @Override
    public BrandResponse findByName(String name) {
        return findByName(name);
    }

    @Override
    public void exportExcel(HttpServletResponse response) throws Exception {
        List<Brand> brands = brandRepository.findAll();

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Danh sách thương hiệu");
        HSSFRow row = sheet.createRow(3);

        row.createCell(0).setCellValue("STT");
        row.createCell(1).setCellValue("Tên thương hiệu");

        int dataRowIndex = 4; // dòng bắt đầu dữ liệu
        int stt = 1;

        for (Brand brand : brands) {
            HSSFRow dataRow = sheet.createRow(dataRowIndex);
            dataRow.createCell(0).setCellValue(stt++);
            dataRow.createCell(1).setCellValue(brand.getName());
            dataRowIndex++;
        }
        ServletOutputStream ops = response.getOutputStream();
        workbook.write(ops);
        workbook.close();
        ops.close();
    }

    @Override
    public void exportPdf(HttpServletResponse response) throws Exception {
        List<Brand> brands = brandRepository.findAll();

        // Thiết lập loại nội dung cho response là PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=DanhSachThuongHieu.pdf");

        // Tạo tài liệu PDF
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        // Mở tài liệu để ghi dữ liệu
        document.open();

        // Thêm tiêu đề
        Font fontTitle = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Paragraph title = new Paragraph("Danh sách thương hiệu", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Thêm khoảng cách sau tiêu đề
        document.add(new Paragraph(" "));

        // Tạo bảng với 2 cột (STT và Tên thương hiệu)
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);

        // Đặt chiều rộng các cột
        float[] columnWidths = {1f, 4f};
        table.setWidths(columnWidths);

        // Thêm tiêu đề cho các cột
        PdfPCell cell;

        Font fontHeader = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        cell = new PdfPCell(new Phrase("STT", fontHeader));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Tên thương hiệu", fontHeader));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        // Thêm dữ liệu cho các dòng
        int stt = 1;
        for (Brand brand : brands) {
            table.addCell(String.valueOf(stt++));
            table.addCell(brand.getName());
        }

        // Thêm bảng vào tài liệu PDF
        document.add(table);

        // Đóng tài liệu
        document.close();
    }

}
