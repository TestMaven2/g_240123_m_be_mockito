package de.telran.g_240123_m_be_mockito.service;

import de.telran.g_240123_m_be_mockito.domain.entity.Product;
import de.telran.g_240123_m_be_mockito.repository.ProductRepository;
import de.telran.g_240123_m_be_mockito.service.interfaces.MerchService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private ProductRepository repository;

    private MerchService merchService;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public void setMerchService(MerchService merchService) {
        this.merchService = merchService;
    }

    // Запрос всех продуктов:
    // Мы должны запросить сторонний сервис, нужна ли информация о поставщике.
    // Если нет - удаляем информацию из объектов продуктов.

    public List<Product> getAll() {
        List<Product> products = repository.findAll();

        if (merchService.withoutSupplierInfo()) {
            products.forEach(x -> x.setSupplier(null));
        }

        return products;
    }

    // Запрос одного продукта:
    // Мы должны запросить у стороннего сервиса наценку.
    // Применяем наценку к запрошенному товару.

    public Product getById(int id) {
        Product product = repository.findById(id).orElse(null);

        if (product != null) {
            double markup = merchService.getMarkup();
            double newPrice = product.getPrice() * (100 + markup) / 100;
            product.setPrice(newPrice);
        }

        return product;
    }

    // Сохранение продукта:
    // Мы должны обратиться к стороннему сервису, чтобы он создал артикул.
    // Созданный артикул применяется к продукту, сохраняем продукт в БД.

    public Product save(Product product) {
        merchService.setArticle(product);
        repository.save(product);
        return product;
    }

    // Удаление продукта:
    // Мы должны обратиться к стороннему сервису, чтобы понять
    // удалять продукт из БД физически или просто выставлять статус в 0.
    // Поступаем соответствующим образом.

    public void delete(int id) {
        if (merchService.fullDeletion()) {
            repository.deleteById(id);
        } else {
            Product product = repository.findById(id).orElse(null);

            if (product != null) {
                product.setActive(false);
                repository.save(product);
            }
        }
    }
}