package org.example.quan_ao_f4k.controller.users;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.users.CustomerRequest;
import org.example.quan_ao_f4k.dto.response.users.CustomerResponse;
import org.example.quan_ao_f4k.service.users.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/customer")
@AllArgsConstructor
public class CustomerController {
	private final CustomerService customerService;

	@GetMapping({"/",""})
	public String getAllCustomers() {
		return "/admin/users/customer";
	}

	@GetMapping("/list")
	public ResponseEntity<Page<CustomerResponse>> listCustomer(@RequestParam(defaultValue = "1") int page,
	                                                           @RequestParam(defaultValue = "10") int size,
	                                                           @RequestParam(required = false) String search) {
		return ResponseEntity.ok(customerService.findCustomersByUserRole(page, size, search));
	}
	@GetMapping("/detail")
	public ResponseEntity<?> detailCustomer(@RequestParam Long id) {
		return ResponseEntity.ok(customerService.findById(id));
	}

	@PostMapping({"/",""})
	@ResponseBody
	public ResponseEntity<?> createCustomer(@Valid @RequestBody CustomerRequest customerRequest, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors());
		}
		CustomerResponse customerResponse = customerService.save(customerRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(customerResponse);
	}
	@PutMapping({"/",""})
	@ResponseBody
	public ResponseEntity<?> updateCustomer( @Valid @RequestBody CustomerRequest customerRequest, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors());
		}
		if (customerRequest.getId() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer ID is required for update.");
		}
		CustomerResponse customerResponse = customerService.save(customerRequest.getId(),customerRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(customerResponse);
	}
}
