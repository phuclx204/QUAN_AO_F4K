package org.example.quan_ao_f4k.repository.product;

import org.example.quan_ao_f4k.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product, Integer>,
		JpaSpecificationExecutor<Product> {
}
