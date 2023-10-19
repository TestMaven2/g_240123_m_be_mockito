package de.telran.g_240123_m_be_mockito.service.interfaces;

import de.telran.g_240123_m_be_mockito.domain.entity.Product;

public interface MerchService {

    boolean withoutSupplierInfo();

    double getMarkup();

    void setArticle(Product product);

    boolean fullDeletion();
}