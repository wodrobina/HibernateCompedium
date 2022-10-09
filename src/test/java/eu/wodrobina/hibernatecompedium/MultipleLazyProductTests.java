package eu.wodrobina.hibernatecompedium;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import eu.wodrobina.hibernatecompedium.product.EagerProduct;
import eu.wodrobina.hibernatecompedium.product.LazyProduct;
import eu.wodrobina.hibernatecompedium.product.LazyProductRepository;
import eu.wodrobina.hibernatecompedium.review.Review;
import eu.wodrobina.hibernatecompedium.review.ReviewRepository;

@SpringBootTest
class MultipleLazyProductTests {

    final ExecutorService executor = Executors.newFixedThreadPool(1);

    @Autowired
    private LazyProductRepository lazyProductRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @AfterEach
    void tearDown() {
        executor.shutdown();
    }

    @Test
    @Transactional
    @DisplayName("Create products and then search all objects. Retrieve linked objects data")
    void test03() throws ExecutionException, InterruptedException {
        //GIVEN
        createEagerProduct(createReviews());
        createEagerProduct(createReviews());
        createEagerProduct(createReviews());

        //WHEN
        Iterable<LazyProduct> all = lazyProductRepository.findAll();

        //THEN
        all.forEach(eagerProduct->eagerProduct.getReviews()
            .forEach(Review::getText));
    }

    @Test
    @Transactional
    @DisplayName("Create products and then search all objects")
    void test04() throws ExecutionException, InterruptedException {
        //GIVEN
        createEagerProduct(createReviews());
        createEagerProduct(createReviews());
        createEagerProduct(createReviews());

        //WHEN//THEN
        lazyProductRepository.findAll();
    }

    private LazyProduct createEagerProduct(Iterable<Review> savedReviews) throws InterruptedException, ExecutionException {
        return CompletableFuture.supplyAsync(() -> {
                LazyProduct item = new LazyProduct("item");
                savedReviews.forEach(item::addReview);
                return lazyProductRepository.save(item);
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
