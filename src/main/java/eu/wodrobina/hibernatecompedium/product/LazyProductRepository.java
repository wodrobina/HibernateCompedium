package eu.wodrobina.hibernatecompedium.product;

import org.springframework.data.repository.CrudRepository;

public interface LazyProductRepository extends CrudRepository<LazyProduct, Integer> {
}
