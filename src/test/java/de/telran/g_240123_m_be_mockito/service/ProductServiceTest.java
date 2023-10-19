package de.telran.g_240123_m_be_mockito.service;

import de.telran.g_240123_m_be_mockito.domain.entity.Product;
import de.telran.g_240123_m_be_mockito.repository.ProductRepository;
import de.telran.g_240123_m_be_mockito.service.interfaces.MerchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository repository;

    private MerchService merchService;

    @BeforeEach
    public void init() {
        merchService = Mockito.mock(MerchService.class);
        productService.setMerchService(merchService);
    }

    @Test
    public void checkIfSupplierInfoIsNull() {
        when(merchService.withoutSupplierInfo()).thenReturn(true);
        List<Product> products = productService.getAll();
        for (Product product : products) {
            assertNull(product.getSupplier());
        }
    }

    @Test
    public void checkIfSupplierInfoIsNotNull() {
        when(merchService.withoutSupplierInfo()).thenReturn(false);
        List<Product> products = productService.getAll();
        for (Product product : products) {
            assertNotNull(product.getSupplier());
        }
    }

    @Test
    public void checkCorrectMarkup() {
        when(merchService.getMarkup()).thenReturn(30.0);
        Product product = productService.getById(1);
        double expectedPrice = 130;
        double actualPrice = product.getPrice();
        assertEquals(expectedPrice, actualPrice);
    }

    @Test
    public void checkCorrectMarkupWithRepositoryMock() {
        ProductRepository repository1 = mock(ProductRepository.class);
        productService = new ProductService(repository1);
        productService.setMerchService(merchService);

        Product product = new Product();
        product.setPrice(500);

        when(merchService.getMarkup()).thenReturn(40.0);
        // Можем определять поведение заглушки при конкретных идентификаторах
//        when(repository1.findById(1)).thenReturn(Optional.of(product));
//        when(repository1.findById(2)).thenReturn(Optional.of(product));

        // Или можем сделать так, чтобы заглушка одинаково реагировала
        // на любой переданный идентификатор
        when(repository1.findById(any())).thenReturn(Optional.of(product));

        Product foundProduct = productService.getById(10);

        assertEquals(700, foundProduct.getPrice());
    }

    @Test
    public void checkIfCorrectArticleWasSet() {
        Product product = new Product();
        String testArticle = "TestArticle777";

        doAnswer(x -> {
            Product product1 = x.getArgument(0);
            product1.setArticle(testArticle);
            return product1;
        }).when(merchService).setArticle(product);

        product = productService.save(product);
        product = repository.findById(product.getId()).orElse(null);

        assertNotNull(product);
        assertEquals(testArticle, product.getArticle());
        repository.delete(product);
    }

    @Test
    public void checkFullDeletion() {
        when(merchService.fullDeletion()).thenReturn(true);

        Product product = new Product();
        product.setActive(true);

        product = repository.save(product);

        productService.delete(product.getId());

        product = repository.findById(product.getId()).orElse(null);

        assertNull(product);
    }

    @Test
    public void checkIfInactiveStatusWasSet() {
        when(merchService.fullDeletion()).thenReturn(false);

        Product product = new Product();
        product.setActive(true);

        product = repository.save(product);

        productService.delete(product.getId());

        product = repository.findById(product.getId()).orElse(null);

        assertNotNull(product);
        assertFalse(product.isActive());
        repository.delete(product);
    }

    @Test
    public void mockAndSpyDemo() {
        ProductService mockService = mock(ProductService.class);
        ProductService spyService = spy(productService);

        Product product1 = new Product();
        Product product2 = new Product();

        product1.setName("Test name");
        product2.setSupplier("Test supplier");

        List<Product> products = List.of(product1, product2);

        when(mockService.getAll()).thenReturn(products);
        when(spyService.getAll()).thenReturn(products);

        System.out.println();
        System.out.println("Behavior with Mockito.when():");
        System.out.println("Mock: " + mockService.getAll());
        System.out.println("Spy:  " + spyService.getAll());
        System.out.println();

        System.out.println("Behavior without Mockito.when():");
        System.out.println("Mock: " + mockService.getById(1));
        System.out.println("Spy:  " + spyService.getById(1));
        System.out.println();
    }
}