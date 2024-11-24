package org.example.quan_ao_f4k.controller.users;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.users.EmployeeRequest;
import org.example.quan_ao_f4k.dto.response.users.EmployeeResponse;
import org.example.quan_ao_f4k.service.users.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/employee")
@AllArgsConstructor
public class EmployeeController {
	private final EmployeeService employeeService;

	@GetMapping({"","/"})
	public String getAllEmployees() {
		return "/admin/users/employee";
	}

	@GetMapping("/list")
	public ResponseEntity<Page<EmployeeResponse>> listCustomer(@RequestParam(defaultValue = "1") int page,
	                                                                    @RequestParam(defaultValue = "10") int size,
	                                                                    @RequestParam(required = false) String search) {
		return ResponseEntity.ok(employeeService.findEmployeesByStaffRole(page, size, search));
	}

	@PostMapping
	public ResponseEntity<?> createCustomer(@Valid @RequestBody EmployeeRequest employeeRequest, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors());
		}
		EmployeeResponse employeeResponse = employeeService.save(employeeRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(employeeResponse);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateCustomer(@PathVariable("id") Long id, @Valid @RequestBody EmployeeRequest employeeRequest, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors());
		}
		EmployeeResponse employeeResponse = employeeService.save(id,employeeRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(employeeResponse);
	}

}
