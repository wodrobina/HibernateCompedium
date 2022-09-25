package eu.wodrobina.hibernatecompedium;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import eu.wodrobina.hibernatecompedium.product.Product;
import eu.wodrobina.hibernatecompedium.product.ProductRepository;
import eu.wodrobina.hibernatecompedium.review.Review;
import eu.wodrobina.hibernatecompedium.review.ReviewRepository;

@SpringBootTest
class HibernateCompediumApplicationTests {

    final ExecutorService executor = Executors.newFixedThreadPool(1);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @AfterEach
    void tearDown() {
        executor.shutdown();
    }

    @Test
    @Transactional
    void test01() throws ExecutionException, InterruptedException {
        //GIVEN
        Iterable<Review> savedReviews = createReviews();
        Product product = createProduct(savedReviews);

        //WHEN
        Optional<Product> byId = productRepository.findById(product.getId());

        //THEN
        byId.get().getReviews()
            .forEach(Review::getText);
    }

    private Product createProduct(Iterable<Review> savedReviews) throws InterruptedException, ExecutionException {
        return CompletableFuture.supplyAsync(() -> {
                Product item = new Product("item");
                savedReviews.forEach(item::addReview);
                return productRepository.save(item);
            }, executor)
            .get();
    }

    private Iterable<Review> createReviews() throws InterruptedException, ExecutionException {
        return CompletableFuture.supplyAsync(() -> reviewRepository.saveAll(List.of(
                new Review("first review"),
                new Review("second review"),
                new Review("third review")
            )), executor)
            .get();
    }

}
