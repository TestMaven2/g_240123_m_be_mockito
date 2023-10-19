package de.telran.g_240123_m_be_mockito.repository;

import de.telran.g_240123_m_be_mockito.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}