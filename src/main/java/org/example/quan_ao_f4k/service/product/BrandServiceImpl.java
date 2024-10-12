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
import org.example.quan_ao_f4k.dto.request.product.BrandRequest;
import org.example.quan_ao_f4k.dto.response.product.BrandResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.product.BrandMapper;
import org.example.quan_ao_f4k.model.product.Brand;
import org.example.quan_ao_f4k.repository.product.BrandRepository;
import org.example.quan_ao_f4k.util.SearchFields;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
		return defaultFindById(aLong,brandRepository, brandMapper,"");
	}

	@Override
	public BrandResponse save(BrandRequest request) {
		return defaultSave(request, brandRepository, brandMapper);
	}

	@Override
	public BrandResponse save(Long aLong, BrandRequest request) {
		return defaultSave(aLong,request,brandRepository,brandMapper,"");
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
	public void updateStatus(Long id, int status) {
		Brand brand = brandRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tồn tại đối tượng"));
		brand.setStatus(status);
		brandRepository.save(brand);
	}

	@Override
	public void exportExcel(HttpServletResponse response) throws Exception {
		List<Brand> brands = brandRepository.findAll();

		// Đặt loại nội dung và tiêu đề cho response
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=DanhSach.xlsx");

		// Tạo workbook và sheet
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Danh sách");

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

		// Bắt đầu thêm dữ liệu từ dòng thứ 3
		int dataRowIndex = 4;
		int stt = 1;

		for (Brand brand : brands) {
			XSSFRow dataRow = sheet.createRow(dataRowIndex++);

			// Ô STT
			cell = dataRow.createCell(0);
			cell.setCellValue(stt++);
			cell.setCellStyle(dataStyle);

			// Ô Tên thương hiệu
			cell = dataRow.createCell(1);
			cell.setCellValue(brand.getName());
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
		List<Brand> brands = brandRepository.findAll();

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

		// Tạo bảng với 2 cột (STT và Tên thương hiệu)
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		table.setSpacingBefore(10f);

		// Đặt chiều rộng các cột
		float[] columnWidths = {1f, 4f};
		table.setWidths(columnWidths);

		// Thêm tiêu đề cho các cột
		PdfPCell cell;

		cell = new PdfPCell(new Phrase("STT", fontHeader));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase("Tên", fontHeader));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);

		// Thêm dữ liệu cho các dòng
		int stt = 1;
		for (Brand brand : brands) {
			// Ô STT
			cell = new PdfPCell(new Phrase(String.valueOf(stt++), fontCell));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			// Ô Tên thương hiệu
			cell = new PdfPCell(new Phrase(brand.getName(), fontCell));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(cell);
		}

		// Thêm bảng vào tài liệu PDF
		document.add(table);

		// Đóng tài liệu
		document.close();
	}

	@Override
	public boolean existsByName(String name) {
		return brandRepository.existsByName(name);
	}

	@Override
	public boolean existsByNameAndIdNot(String name, Long id) {
		return brandRepository.existsByNameAndIdNot(name,id);
	}

	@Override
	public List<BrandResponse> findByStatusActive() {
		List<Brand> brands = brandRepository.findByStatus(1);
		List<BrandResponse> responses = new ArrayList<>();
		for (Brand brand : brands) {
			BrandResponse brandResponse = new BrandResponse(brand.getId(),brand.getName(),brand.getStatus());
			responses.add(brandResponse);
		}
		return responses;
	}

}
