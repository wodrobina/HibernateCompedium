package eu.wodrobina.hibernatecompedium;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import eu.wodrobina.hibernatecompedium.product.EagerProduct;
import eu.wodrobina.hibernatecompedium.product.EagerProductRepository;
import eu.wodrobina.hibernatecompedium.product.LazyProduct;
import eu.wodrobina.hibernatecompedium.product.LazyProductRepository;
import eu.wodrobina.hibernatecompedium.review.Review;
import eu.wodrobina.hibernatecompedium.review.ReviewRepository;

@SpringBootTest
class HibernateCompediumApplicationTests {

    final ExecutorService executor = Executors.newFixedThreadPool(1);

    @Autowired
    private LazyProductRepository lazyProductRepository;

    @Autowired
    private EagerProductRepository eagerProductRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    DataSource dataSource;

    @AfterEach
    void tearDown() {
        executor.shutdown();
    }

    @Test
    @DisplayName("Will create 3 products with reviews and retrieve only products from database")
    @Transactional
    void test01() throws Exception {
        //GIVEN
        createLazyProduct("first", createReviews(Set.of("first one", "first two", "first three")));
        createLazyProduct("first", createReviews(Set.of("second one", "second two", "second three")));
        createLazyProduct("third", createReviews(Set.of("third one", "third two", "third three")));

        //WHEN
        Iterable<LazyProduct> all = lazyProductRepository.findAll();
    }

    @Test
    @DisplayName("Will create 3 eager products with reviews and retrieve only products from database")
    @Transactional
    void test02() throws Exception {
        //GIVEN
        createEagerProduct("first", createReviews(Set.of("first one", "first two", "first three")));
        createEagerProduct("first", createReviews(Set.of("second one", "second two", "second three")));
        createEagerProduct("third", createReviews(Set.of("third one", "third two", "third three")));

        //WHEN
        Iterable<LazyProduct> all = lazyProductRepository.findAll();

    }

    @Test
    @DisplayName("Will create 3 lazy products with reviews and retrieve products and reviews from database")
    @Transactional
    void test03() throws Exception {
        //GIVEN
        createLazyProduct("first", createReviews(Set.of("first one", "first two", "first three")));
        createLazyProduct("first", createReviews(Set.of("second one", "second two", "second three")));
        createLazyProduct("third", createReviews(Set.of("third one", "third two", "third three")));

        //WHEN
        Iterable<LazyProduct> all = lazyProductRepository.findAll();

        //THEN
        all.forEach(p -> p.getReviews().forEach(Review::getText));
    }

    @Test
    @DisplayName("Will create 3 eager products with reviews and retrieve products and reviews from database")
    @Transactional
    void test04() throws Exception {
        //GIVEN
        createEagerProduct("first", createReviews(Set.of("first one", "first two", "first three")));
        createEagerProduct("first", createReviews(Set.of("second one", "second two", "second three")));
        createEagerProduct("third", createReviews(Set.of("third one", "third two", "third three")));

        //WHEN
        Iterable<EagerProduct> all = eagerProductRepository.findAll();

        //THEN
        all.forEach(p -> p.getReviews().forEach(Review::getText));
    }

    private LazyProduct createLazyProduct(String productName, Iterable<Review> savedReviews) throws InterruptedException, ExecutionException {
        return CompletableFuture.supplyAsync(() -> {
            LazyProduct item = new LazyProduct(productName);
            savedReviews.forEach(item::addReview);
            return lazyProductRepository.save(item);
        }, executor).get();
    }

    private EagerProduct createEagerProduct(String productName, Iterable<Review> savedReviews) throws InterruptedException, ExecutionException {
        return CompletableFuture.supplyAsync(() -> {
            EagerProduct item = new EagerProduct(productName);
            savedReviews.forEach(item::addReview);
            return eagerProductRepository.save(item);
        }, executor).get();
    }

    private Iterable<Review> createReviews(Set<String> reviewTexts) throws InterruptedException, ExecutionException {
        return CompletableFuture.supplyAsync(() -> reviewRepository.saveAll(reviewTexts.stream().map(Review::new).collect(Collectors.toList())), executor).get();
    }

}
