package eu.wodrobina.hibernatecompedium;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import eu.wodrobina.hibernatecompedium.product.Product;
import eu.wodrobina.hibernatecompedium.product.ProductRepository;
import eu.wodrobina.hibernatecompedium.review.Review;
import eu.wodrobina.hibernatecompedium.review.ReviewRepository;

@SpringBootTest
class HibernateCompediumApplicationTests {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    void test01() {
        Iterable<Review> savedReviews = reviewRepository.saveAll(List.of(
            new Review("first review"),
            new Review("second review"),
            new Review("third review")
        ));

        Product item = new Product("item");
        savedReviews.forEach(item::addReview);
        Product saved = productRepository.save(item);

        Optional<Product> byId = productRepository.findById(saved.getId());

        byId.get()
            .getReviews();
    }

}
