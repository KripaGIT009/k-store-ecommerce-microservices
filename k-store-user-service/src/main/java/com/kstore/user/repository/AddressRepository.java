package com.kstore.user.repository;

import com.kstore.user.entity.Address;
import com.kstore.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    List<Address> findByUser(User user);
    
    List<Address> findByUserId(Long userId);
    
    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.type = :type")
    List<Address> findByUserIdAndType(@Param("userId") Long userId, @Param("type") Address.AddressType type);
    
    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.isDefault = true")
    Optional<Address> findDefaultByUserId(@Param("userId") Long userId);
    
    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.type = :type AND a.isDefault = true")
    Optional<Address> findDefaultByUserIdAndType(@Param("userId") Long userId, @Param("type") Address.AddressType type);
}
